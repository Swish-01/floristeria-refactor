package com.floristeria.blomst.controller;

import com.floristeria.blomst.domain.Response;
import com.floristeria.blomst.dto.user.User;
import com.floristeria.blomst.dtorequest.email.EmailRequest;
import com.floristeria.blomst.dtorequest.user.*;
import com.floristeria.blomst.handler.ApiLogoutOutHandler;
import com.floristeria.blomst.service.JwtService;
import com.floristeria.blomst.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static com.floristeria.blomst.constant.Constants.PHOTO_DIRECTORY;
import static com.floristeria.blomst.enumeration.user.TokenType.ACCESS;
import static com.floristeria.blomst.enumeration.user.TokenType.REFRESH;
import static com.floristeria.blomst.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = {"/user"})
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final ApiLogoutOutHandler logoutOutHandler;
    @PostMapping("/register")
    public ResponseEntity<Response> saveUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {
        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
        return ResponseEntity.created(getUri()).body(
                getResponse(request, emptyMap(), "Cuenta creada. Verifica tu email para activar la cuenta", CREATED)
        );
    }

    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifyAccount(@RequestParam("key") String key, HttpServletRequest request) {
        userService.verifyAccountKey(key);
        return ResponseEntity.ok().body(
                getResponse(request, emptyMap(), "Cuenta Verificada", OK)
        );
    }

    @GetMapping("/profile")
    //@PreAuthorize("hasAnyAuthority('user:read') or hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Response> profile(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        var user = userService.getUserByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok().body(
                getResponse(request, Map.of("user", user), "Perfil encontrado", OK)
        );
    }

    @PatchMapping("/update")
    public ResponseEntity<Response> update(@AuthenticationPrincipal User userPrincipal, @RequestBody UserRequest userRequest, HttpServletRequest request) {
        var user = userService.updateUser(userPrincipal.getUserId(),
                userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userRequest.getBio());
        return ResponseEntity.ok().body(
                getResponse(request, Map.of("user", user), "Usuario actualizado con exito", OK)
        );
    }

    @PatchMapping("/update-role")
    public ResponseEntity<Response> updateRole(@AuthenticationPrincipal User userPrincipal, @RequestBody RoleRequest roleRequest, HttpServletRequest request) {
        userService.updateRole(userPrincipal.getUserId(), roleRequest.getRole());
        return ResponseEntity.ok().body(
                getResponse(request, emptyMap(), "Rol actualizado con exito", OK)
        );
    }

    @PatchMapping("/toggle-account-expired")
    public ResponseEntity<Response> toggleAccountExpired(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountExpired(user.getUserId());
        return ResponseEntity.ok().body(
                getResponse(request, emptyMap(), "Cuenta actualizado con exito", OK)
        );
    }

    @PatchMapping("/toggle-account-locked")
    public ResponseEntity<Response> toggleAccountLocked(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountLocked(user.getUserId());
        return ResponseEntity.ok().body(
                getResponse(request, emptyMap(), "Cuenta actualizado con exito", OK)
        );
    }

    @PatchMapping("/toggle-account-enabled")
    public ResponseEntity<Response> toggleAccountEnabled(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountEnabled(user.getUserId());
        return ResponseEntity.ok().body(
                getResponse(request, emptyMap(), "Cuenta actualizado con exito", OK)
        );
    }

    // START -- Double verifications methods
    @PatchMapping("/mfa/setup")
    public ResponseEntity<Response> setUpMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        var user = userService.setUpMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "MFA puesto exitosamente", OK));
    }

    @PatchMapping("/mfa/cancel")
    public ResponseEntity<Response> cancelMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        var user = userService.cancelMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "MFA cancelado exitosamente", OK));
    }

    @PostMapping("/verify/qrcode")
    public ResponseEntity<Response> verifyQrcode(@RequestBody QrCodeRequest qrCodeRequest, HttpServletRequest request, HttpServletResponse response) {
        var user = userService.verifyQrCode(qrCodeRequest.getUserId(), qrCodeRequest.getQrCode());
        jwtService.addCookie(response, user, ACCESS);
        jwtService.addCookie(response, user, REFRESH);
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "QR code verified", OK));
    }

    // END -- Double verifications methods

    // START -- Reset password when user is logged in

    @PatchMapping("/update-password")
    public ResponseEntity<Response> updatePassword(@AuthenticationPrincipal User user, @RequestBody UpdatePasswordRequest passwordRequest, HttpServletRequest request) {
        userService.updatePassword(user.getUserId(), passwordRequest.getPassword(),
                passwordRequest.getNewPassword(), passwordRequest.getConfirmedNewPassword());
        return ResponseEntity.ok().body(getResponse(
                request, emptyMap(), "Contraseña actualizada con éxito", OK)
        );
    }

    // END -- Reset password when user is not logged

    // START -- Reset password when user is not logged
    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid EmailRequest emailRequest, HttpServletRequest request) {
        userService.resetPassword(emailRequest.getEmail());
        return ResponseEntity.ok().body(getResponse(
                request, emptyMap(), "Te enviamos un email para re-establecer la contraseña", OK)
        );
    }

    @GetMapping("/verify/password")
    public ResponseEntity<Response> verifyPassword(@RequestParam("key") String key, HttpServletRequest request) {
        var user = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(getResponse(
                request, Map.of("user", user), "Introduce la nueva contraseña", OK)
        );
    }

    @PostMapping("/reset-password/reset")
    public ResponseEntity<Response> doResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest, HttpServletRequest request) {
        userService.updatePassword(resetPasswordRequest.getUserId(), resetPasswordRequest.getNewPassword(), resetPasswordRequest.getConfirmedPassword());
        return ResponseEntity.ok().body(getResponse(
                request, emptyMap(), "La contraseña ha sido modificada correctamente", OK)
        );
    }
    // END -- Reset password when user is not logged


    @PatchMapping("/photo")
    public ResponseEntity<Response> updatePhoto(@AuthenticationPrincipal User userPrincipal, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        var imgUrl = userService.updatePhoto(userPrincipal.getUserId(), file);
        return ResponseEntity.ok().body(
                getResponse(request, Map.of("imgUrl", imgUrl), "Photo actualizado con éxito", OK)
        );
    }

    @GetMapping(value = "/image/{filename}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutOutHandler.logout(request,response,authentication);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Log out éxito", OK));
    }

    private URI getUri() {
        return URI.create("");
    }
}
