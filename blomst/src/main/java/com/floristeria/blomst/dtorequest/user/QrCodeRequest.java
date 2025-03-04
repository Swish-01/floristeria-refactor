package com.floristeria.blomst.dtorequest.user;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class QrCodeRequest {
    @NotEmpty(message = "User ID no puede estar vacio o nulo")
    private String userId;
    @NotEmpty(message = "QR code no puede estar vacio o nulo")
    private String qrCode;
}