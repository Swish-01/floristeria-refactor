package com.floristeria.blomst.service;

import java.util.Map;

public interface EmailService {
    void sendNewAccountEmail(String name, String email, String token);

    void sendPasswordResetEmail(String name, String email, String token);

    void sendOrderPropostal(String email, Map<String, String> placeholders);
}
