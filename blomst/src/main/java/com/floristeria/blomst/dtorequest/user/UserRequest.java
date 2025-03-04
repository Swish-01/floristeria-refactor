package com.floristeria.blomst.dtorequest.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {
    @NotEmpty(message = "El nombre no puede estar vacío o en blanco")
    private String firstName;
    @NotEmpty(message = "El apellido no puede estar vacío o en blanco")
    private String lastName;
    @NotEmpty(message = "El email no puede estar vacío o en blanco")
    @Email(message = "Dirección de email invalida")
    private String email;
    @NotEmpty(message = "La contraseña no puede estar vacía o en blanco")
    private String password;
    private String bio;
    private String phone;
}
