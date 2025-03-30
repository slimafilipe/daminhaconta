package dev.filipe.daminhaconta.controller;

import dev.filipe.daminhaconta.model.Charge;
import dev.filipe.daminhaconta.repository.ChargeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final ChargeRepository chargeRepository;

    @PostMapping("/mercadopago")
    public ResponseEntity<String> receiveWebhook(@RequestBody Map<String, Object> payload) {
        log.info("Recebido webhook do Mercado Pago: {}", payload);

        try {
            // Extrair dados relevantes da notificação
            String type = (String) payload.get("type");

            if ("payment".equals(type)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                String idPayment = String.valueOf(data.get("id"));

                // Buscar cobrança pelo ID externo
                Optional<Charge> chargeOptional = chargeRepository.findAll().stream()
                        .filter(c-> idPayment.equals(c.getIdExternMecadoPago()))
                        .findFirst();

                if (chargeOptional.isPresent()) {
                    Charge charge = chargeOptional.get();

                    //Atualizar status da cobrança com base na notificação
                    String action = (String) payload.get("action");

                    if ("payment.updated".equals(action)) {
                        String status = (String) data.get("status");

                        if ("approved".equals(status)) {
                            charge.setStatus("PAGA");
                            chargeRepository.save(charge);
                            log.info("Cobrança {} marcada como PAGA via webhook", charge.getId());
                        }
                    }
                }
            }
            return ResponseEntity.ok("Webhook processado com sucesso");
        } catch (Exception e) {
            log.error("Erro ao processar webhook do Mercado Pago", e);
            return ResponseEntity.ok("Webhook recebido, mas ocorreu um erro no processamento");
        }
    }
}
