package dev.filipe.daminhaconta.dto;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

public class MercadoPagoRequestInterceptor implements RequestInterceptor {
    @Value("${mercadopago.access_token}")
    private String accessToken;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "Bearer" + accessToken);
    }
}
