package com.floristeria.blomst.dtorequest.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateWebRequest {
    @NotEmpty(message = "La url no puede estar vacio o nulo")
    private String url;
    @NotEmpty(message = "El nombre no puede estar vacio o nulo")
    private String name;
    @NotEmpty(message = "El Logo no puede estar vacio o nulo")
    private String urlLogo;
    @NotEmpty(message = "La clave del cliente no puede estar vacio o nulo")
    private String customerKey;
    @NotEmpty(message = "La clave secreta no puede estar vacio o nulo")
    private String secretkey;
}
