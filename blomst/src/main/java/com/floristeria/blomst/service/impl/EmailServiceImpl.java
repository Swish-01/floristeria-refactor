package com.floristeria.blomst.service.impl;

import com.floristeria.blomst.exception.ApiException;
import com.floristeria.blomst.service.EmailService;
import com.floristeria.blomst.utils.EmailUtils;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final String NEW_USER_ACCOUNT_VERIFICATION = "Verificación de cuenta de nuevo usuario";
    private static final String PASSWORD_RESET_REQUEST = "Solicitud de restablecimiento de contraseña";
    private static final String NEW_ORDER_PROPOSAL_SUBJECT = "Grupo Blomst - Nuevo pedido ";

    private final JavaMailSender sender;
    @Value("${spring.mail.verify.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async("asyncExecutor")
    public void sendNewAccountEmail(String name, String email, String token) {
        sendEmail(
                email,
                NEW_USER_ACCOUNT_VERIFICATION,
                EmailUtils.getEmailNewAccountMessage(name, host, token));
    }

    @Override
    @Async("asyncExecutor")
    public void sendPasswordResetEmail(String name, String email, String token) {
        sendEmail(
                email,
                PASSWORD_RESET_REQUEST,
                EmailUtils.getResetPasswordMessage(name, host, token));
    }

    @Override
    @Async("asyncExecutor")
    public void sendOrderPropostal(String email, Map<String, String> placeholders) {
        String content = EmailUtils.getOrderProposalEmail(placeholders);
        sendEmail(email, NEW_ORDER_PROPOSAL_SUBJECT + placeholders.get("orderNumber"), content);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            sender.send(message);
            log.info("Correo enviado correctamente a: {}", to);
        } catch (Exception e) {
            log.error("Error al enviar el correo: {}", e.getMessage());
            throw new ApiException("No se pudo enviar el correo a " + to);
        }
    }

    // @Override
    // @Async("asyncExecutor")
    // public void sendNewAccountEmail(String name, String email, String token) {
    // try {
    // MimeMessage message = sender.createMimeMessage();
    // MimeMessageHelper helper = new MimeMessageHelper(message);
    // helper.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
    // helper.setTo(fromEmail);
    // helper.setTo(email);
    // helper.setText(getEmailNewAccountMessage(name, host, token), true);
    // sender.send(message);
    // } catch (Exception e) {
    // log.error(e.getMessage());
    // throw new ApiException("No se puede enviar el correo electrónico de
    // verificación de nueva cuenta");
    // }
    // }

    // @Override
    // @Async("asyncExecutor")
    // public void sendPasswordResetEmail(String name, String email, String token) {
    // try {
    // MimeMessage message = sender.createMimeMessage();
    // MimeMessageHelper helper = new MimeMessageHelper(message);
    // helper.setSubject(PASSWORD_RESET_REQUEST);
    // helper.setTo(fromEmail);
    // helper.setTo(email);
    // helper.setText(getResetPasswordMessage(name, host, token),true);
    // sender.send(message);
    // } catch (Exception e) {
    // log.error(e.getMessage());
    // throw new ApiException("No se puede enviar el correo electrónico para
    // restablecer la contraseña");
    // }
    // }

    // @Override
    // @Async("asyncExecutor")
    // public void sendOrderPropostal(String name, String email, String token) {
    // try {
    // MimeMessage message = sender.createMimeMessage();
    // MimeMessageHelper helper = new MimeMessageHelper(message);
    // helper.setSubject("Test");
    // helper.setTo(fromEmail);
    // helper.setTo(email);
    // sender.send(message);
    // } catch (Exception e) {
    // log.error(e.getMessage());
    // throw new ApiException("No se puede enviar el correo electrónico para la
    // propuesta de pedido");
    // }
    // }
}
