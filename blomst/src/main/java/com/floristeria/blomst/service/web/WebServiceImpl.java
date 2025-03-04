package com.floristeria.blomst.service.web;

import static org.springframework.data.domain.PageRequest.of;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.floristeria.blomst.dto.web.Web;
import com.floristeria.blomst.dtorequest.web.CreateWebRequest;
import com.floristeria.blomst.entity.web.WebEntity;
import com.floristeria.blomst.exception.ApiException;
import com.floristeria.blomst.repository.web.WebRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class WebServiceImpl implements WebService {
    private final WebRepository webRepository;

    @Override
    public Web createWeb(CreateWebRequest web) {
        WebEntity webEntity = WebEntity.builder()
                .url(web.getUrl())
                .name(web.getName())
                .urlLogo(web.getUrlLogo())
                .customerKey(web.getCustomerKey())
                .secretkey(web.getSecretkey())
                .build();
        WebEntity savedWeb = webRepository.save(webEntity);
        return getWebById(savedWeb.getId());
    }

    @Override
    public Web updateWebKeys(Long webId, String customerKey, String secretkey) {
        int updatedRows = webRepository.updateWebKeys(webId, customerKey, secretkey);

        if (updatedRows == 0) {
            throw new ApiException("No existe una web con este id: " + webId);
        }

        return getWebById(webId);
    }

    @Override
    public Web getWebById(Long id) {
        return webRepository.findWebById(id)
                .orElseThrow(() -> new ApiException("No existe una web con este id {}" + id));
    }

    @Override
    public Page<Web> getWebs(int page, int size) {
        return webRepository.findWebs(of(page, size));
    }

    @Override
    public Web updateWebLogo(Long webId, String urlLogo) {
        int updatedRows = webRepository.updateWebLogo(webId, urlLogo);

        if (updatedRows == 0) {
            throw new ApiException("No existe una web con este id: " + webId);
        }

        return getWebById(webId);
    }
}
