package com.floristeria.blomst.controller.registerOrders;

import com.floristeria.blomst.domain.Response;
import com.floristeria.blomst.entity.web.CategoryEntity;
import com.floristeria.blomst.entity.web.ProductEntity;
import com.floristeria.blomst.entity.web.ProductVariationEntity;
import com.floristeria.blomst.entity.web.WebEntity;
import com.floristeria.blomst.repository.product.CategoryRepository;
import com.floristeria.blomst.repository.product.ProductRepository;
import com.floristeria.blomst.repository.product.ProductVariationRepository;
import com.floristeria.blomst.repository.web.WebRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static com.floristeria.blomst.utils.RequestUtils.getResponse;
import org.springframework.http.*;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = { "/african-safari-travel" })
@Transactional
public class TestController {
    private final WebRepository repository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariationRepository productVariationRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/update-categories-and-products/{webId}")
    public ResponseEntity<Response> updateCategoriesAndProducts(@PathVariable Long webId, HttpServletRequest request) {
        Optional<WebEntity> webOpt = repository.findById(webId);

        if (webOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(getResponse(request, Map.of(), "Web not found", HttpStatus.NOT_FOUND));
        }

        WebEntity web = webOpt.get();
        String wooCommerceUrl = web.getUrl();
        String customerKey = web.getCustomerKey();
        String secretKey = web.getSecretkey();

        String categoriesEndpoint = wooCommerceUrl + "/wp-json/wc/v3/products/categories";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(customerKey, secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List> categoriesResponse = restTemplate.exchange(
                categoriesEndpoint,
                HttpMethod.GET,
                entity,
                List.class);

        if (categoriesResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(categoriesResponse.getStatusCode())
                    .body(getResponse(request, Map.of(), "Failed to fetch categories",
                            HttpStatus.valueOf(categoriesResponse.getStatusCode().value())));
        }

        List<Map<String, Object>> categoriesData = categoriesResponse.getBody();
        if (categoriesData == null || categoriesData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(getResponse(request, Map.of(), "No categories found", HttpStatus.NO_CONTENT));
        }

        List<CategoryEntity> categories = new ArrayList<>();
        for (Map<String, Object> categoryData : categoriesData) {
            String categoryName = (String) categoryData.get("name");
            Long categoryId = Long.valueOf(categoryData.get("id").toString());

            CategoryEntity category = new CategoryEntity();
            category.setName(categoryName);
            category.setWeb(web);
            category.setWooCommerceId(categoryId);
            categories.add(category);
        }

        categoryRepository.saveAll(categories);

        // 📌 Primero guardamos los productos
        List<ProductEntity> products = new ArrayList<>();
        Map<Long, ProductEntity> productMap = new HashMap<>();

        for (CategoryEntity category : categories) {
            String productsEndpoint = wooCommerceUrl + "/wp-json/wc/v3/products?category="
                    + category.getWooCommerceId();

            ResponseEntity<List> productsResponse = restTemplate.exchange(
                    productsEndpoint,
                    HttpMethod.GET,
                    entity,
                    List.class);

            if (productsResponse.getStatusCode() == HttpStatus.OK && productsResponse.getBody() != null) {
                List<Map<String, Object>> productsData = productsResponse.getBody();

                for (Map<String, Object> productData : productsData) {
                    ProductEntity product = new ProductEntity();
                    Long productId = Long.valueOf(productData.get("id").toString());
                    product.setName((String) productData.get("name"));
                    product.setCategory(category);
                    product.setSku((String) productData.get("sku"));
                    product.setRegularPrice((String) productData.get("regular_price"));
                    product.setSalePrice((String) productData.get("sale_price"));

                    products.add(product);
                    productMap.put(productId, product);
                }
            }
        }

        // 📌 Guardamos los productos y obtenemos sus IDs
        productRepository.saveAll(products);

        // 📌 Recuperamos los productos de la BD con sus IDs asignados
        for (ProductEntity savedProduct : productRepository.findAll()) {
            productMap.put(savedProduct.getId(), savedProduct);
        }

        // 📌 Ahora guardamos las variaciones con productos ya persistidos
        List<ProductVariationEntity> productVariations = new ArrayList<>();

        for (Map.Entry<Long, ProductEntity> entry : productMap.entrySet()) {
            Long wooProductId = entry.getKey();
            ProductEntity product = entry.getValue();

            String variationsEndpoint = wooCommerceUrl + "/wp-json/wc/v3/products/" + wooProductId + "/variations";

            ResponseEntity<List> variationsResponse = restTemplate.exchange(
                    variationsEndpoint,
                    HttpMethod.GET,
                    entity,
                    List.class);

            if (variationsResponse.getStatusCode() == HttpStatus.OK && variationsResponse.getBody() != null) {
                List<Map<String, Object>> variationsData = variationsResponse.getBody();

                for (Map<String, Object> variationData : variationsData) {
                    ProductVariationEntity variation = new ProductVariationEntity();
                    variation.setSku((String) variationData.get("sku"));
                    variation.setRegularPrice((String) variationData.get("regular_price"));
                    variation.setSalePrice((String) variationData.get("sale_price"));
                    variation.setOnSale((Boolean) variationData.get("on_sale"));

                    // Ahora podemos asignarle su producto correctamente
                    variation.setProduct(product);
                    productVariations.add(variation);
                }
            }
        }
        // Guardar productos en la base de datos primero
        productRepository.saveAll(products);

        // Ahora que los productos tienen ID, podemos asignarlos a sus variaciones
        for (ProductVariationEntity variation : productVariations) {
            variation.setProduct(productRepository.findById(variation.getProduct().getId()).orElse(null));
        }

        // Guardar las variaciones después de que los productos estén persistidos
        productVariationRepository.saveAll(productVariations);

        return ResponseEntity.ok(getResponse(request,
                Map.of("categories", categories, "products", products, "variations", productVariations),
                "Categories, products, and variations updated", HttpStatus.OK));
    }

    @PostMapping("/create")
    public ResponseEntity<Response> registerWithNewsletter(HttpServletRequest request) {
        WebEntity bopel = WebEntity.builder().name("Bopel")
                .url("https://bopel.es/")
                .urlLogo("https://bopel.es/wp-content/uploads/2019/12/logo_gris.png")
                .customerKey("ck_54114ed455fabbbe7226f879c4db5af555d6806a")
                .secretkey("cs_a6bf1fd9b35a4b2b68babd66a77655570e6a9985").build();
        repository.save(bopel);

        return ResponseEntity.created(URI.create("")).body(
                getResponse(request, Map.of("Web", repository.findAll()), "Todo correcto", HttpStatus.CREATED));
    }

}
