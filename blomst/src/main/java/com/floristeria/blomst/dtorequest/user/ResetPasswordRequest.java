package com.floristeria.blomst.dtorequest.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordRequest {
    @NotEmpty(message = "Id del usuario no puede estar vacío o en blanco")
    private String userId;
    @NotEmpty(message = "La contraseña no puede estar vacía o en blanco")
    private String newPassword;
    @NotEmpty(message = "La contraseña de confirmación no puede estar vacía o en blanco")
    private String confirmedPassword;
}
