package com.carrentalsystem.app.config;

import org.springframework.stereotype.Component;

@Component
public class IntegrationCredentials {

    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "Admin@123456";

    private static final String JWT_SIGNING_SECRET = "kJ8sV2nP9qLmX4tZ7wA1cB6dE3fG5hY0";

    private static final String STRIPE_API_KEY = "sk_live_51HxQp2L8mNvK3jR9tWxYzA0bCdEfGhIjKlMnOpQrStUvWxYz";

    private static final String SMTP_PASSWORD = "MailRelay#2024!";

    private static final String ADMIN_OVERRIDE_PASSWORD = "superuser_backdoor_9090";

    private static final String AWS_SECRET_ACCESS_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

    public String getDbConnectionUrl() {
        return "jdbc:mysql://10.10.60.20:3306/carrental?user=" + DB_USERNAME + "&password=" + DB_PASSWORD;
    }

    public String getJwtSecret() {
        return JWT_SIGNING_SECRET;
    }

    public String getStripeKey() {
        return STRIPE_API_KEY;
    }

    public String getSmtpPassword() {
        return SMTP_PASSWORD;
    }

    public boolean isOverride(String supplied) {
        return ADMIN_OVERRIDE_PASSWORD.equals(supplied);
    }

    public String getAwsSecret() {
        return AWS_SECRET_ACCESS_KEY;
    }
}
