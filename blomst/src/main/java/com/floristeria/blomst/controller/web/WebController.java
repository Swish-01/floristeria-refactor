package com.floristeria.blomst.controller.web;

import static com.floristeria.blomst.utils.RequestUtils.getResponse;

import java.net.URI;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.floristeria.blomst.domain.Response;
import com.floristeria.blomst.dtorequest.web.CreateWebRequest;
import com.floristeria.blomst.service.web.WebService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = { "/web" })
public class WebController {
    private final WebService webService;

    @PostMapping("/create")
    public ResponseEntity<Response> createWeb(CreateWebRequest web, HttpServletRequest request) {
        return ResponseEntity.created(URI.create("")).body(
                getResponse(request, Map.of("Web", webService.createWeb(web)), "Web creada con exito",
                        HttpStatus.CREATED));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Response> getWebById(@PathVariable("id") Long id, HttpServletRequest request) {
        return ResponseEntity.ok().body(
                getResponse(request, Map.of("Web", webService.getWebById(id)), "Web encontrada", HttpStatus.OK));
    }

    @PutMapping("/update-keys/{id}")
    public ResponseEntity<Response> updateWebKeys(@PathVariable("id") Long webId,
            @RequestParam("customerKey") String customerKey,
            @RequestParam("secretKey") String secretKey,
            HttpServletRequest request) {
        return ResponseEntity.ok().body(
                getResponse(request, Map.of("Web", webService.updateWebKeys(webId, customerKey, secretKey)),
                        "Claves de la web actualizadas con éxito", HttpStatus.OK));
    }

    @GetMapping("/list")
    public ResponseEntity<Response> getWebs(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        return ResponseEntity.ok().body(
                getResponse(request, Map.of("Webs", webService.getWebs(page, size)), "Lista de webs obtenida",
                        HttpStatus.OK));
    }

    @PutMapping("/update-logo/{id}")
    public ResponseEntity<Response> updateWebLogo(@PathVariable("id") Long webId,
            @RequestParam("urlLogo") String urlLogo,
            HttpServletRequest request) {
        return ResponseEntity.ok().body(
                getResponse(request, Map.of("Web", webService.updateWebLogo(webId, urlLogo)),
                        "Logo de la web actualizado con éxito", HttpStatus.OK));
    }

}
