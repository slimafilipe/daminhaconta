package dev.filipe.daminhaconta.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mercadoPagoClient", url = "https://api.mercadopago.com")
public interface MercadoPagoClient {

    @PostMapping("/v1/payments")
    PaymenteResponse createPayment(@RequestBody PaymentRequest request);
}
