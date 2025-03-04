package com.floristeria.blomst.service.web;



import org.springframework.data.domain.Page;

import com.floristeria.blomst.dto.web.Web;
import com.floristeria.blomst.dtorequest.web.CreateWebRequest;

public interface WebService {
    Web createWeb(CreateWebRequest web);
    Web getWebById(Long id);
    Page<Web> getWebs(int page,int size);
    Web updateWebKeys(Long webId, String customerKey, String secretkey);
    Web updateWebLogo(Long webId, String urlLogo);
}
