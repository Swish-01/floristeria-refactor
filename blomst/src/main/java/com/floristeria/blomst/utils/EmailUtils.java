package com.floristeria.blomst.utils;

import com.floristeria.blomst.exception.ApiException;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class EmailUtils {
    private static String loadEmailTemplate(String templatePath) {
        try {
            InputStream inputStream = new ClassPathResource(templatePath).getInputStream();
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ApiException("Error al cargar la plantilla del email");
        }
    }

    public static String getEmailNewAccountMessage(String name, String host, String key) {
        String template = loadEmailTemplate("templates-html/new-account.html");
        String verificationUrl = getVerificationUrl(host, key);
        return template.replace("{{name}}", name).replace("{{verificationUrl}}", verificationUrl);
    }

    public static String getResetPasswordMessage(String name, String host, String key) {
        String template = loadEmailTemplate("templates-html/reset-password.html");
        String resetPasswordUrl = getResetPasswordUrl(host, key);
        return template.replace("{{name}}", name).replace("{{resetPasswordUrl}}", resetPasswordUrl);
    }

    public static String getVerificationUrl(String host, String key) {
        return host + "/user/verify/account?key=" + key;
    }

    public static String getResetPasswordUrl(String host, String key) {
        return host + "/user/verify/password?key=" + key;
    }

    /*Plantilla de enviar solicitud de propuesta de pedido  */
    public static String getOrderProposalEmail(Map<String, String> placeholders) {
        String template = loadEmailTemplate("templates-html/order-propostal.html");
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }
}
