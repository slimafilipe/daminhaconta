package dev.filipe.daminhaconta.service;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {

    @Value("${mercadopago.access.token:TEST-6435524607675452-032800-3927736759b7d1fff353135112285f04-181739061}")
    private String mercadoPagoAccessToken;

    @PostConstruct
    public void initialize() {
        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
    }

}