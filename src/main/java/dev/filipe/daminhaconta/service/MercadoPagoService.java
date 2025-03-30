package dev.filipe.daminhaconta.service;

import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.exceptions.MPApiException;
import dev.filipe.daminhaconta.model.Client;
import dev.filipe.daminhaconta.model.Charge;

import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoService {

    public String createPaymentPix(Client client, Charge charge) {
        try {
            // Cliente para criar pagamentos
            PaymentClient paymentClient = new PaymentClient();

            //Criando o pagamento via pix
            PaymentCreateRequest request = PaymentCreateRequest.builder()
                            .transactionAmount(BigDecimal.valueOf(charge.getValue().floatValue()))
                            .description("Cobrança Mensal - " + client.getName())
                            .paymentMethodId("pix")
                            .payer(
                                    PaymentPayerRequest.builder()
                                            .email(client.getEmail())
                                            .firstName(client.getName().split(" ")[0])
                                            .lastName(client.getName().contains(" ") ?
                                                    client.getName().substring(client.getName().indexOf(" ") + 1): "")
                                            .identification(
                                                    IdentificationRequest.builder()
                                                            .type("CPF")
                                                            .number(client.getCpfCnpj())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();
            Payment payment = paymentClient.create(request);

            // Salvando o ID do pagamento para referência futura
            charge.setIdExternMecadoPago(payment.getId().toString());

            //Retorna o QR code do Pix ou código para copiar e colar
            return payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
        } catch (MPException | MPApiException e) {
            throw new RuntimeException("Erro ao criar cobrança PIX: " + e.getMessage(), e);
        }
    }

    public String createLinkCharge(Client client, Charge charge) {
        try {
            PreferenceClient preferenceClient = new PreferenceClient();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(
                    PreferenceItemRequest.builder()
                            .title("Cobrança mensal - " + client.getName())
                            .quantity(1)
                            .unitPrice(charge.getValue())
                            .build()
            );

            PreferenceRequest request = PreferenceRequest.builder()
                    .items(items)
                    .payer(
                            com.mercadopago.client.preference.PreferencePayerRequest.builder()
                                    .email(client.getEmail())
                                    .name(client.getName())
                                    .build()
                    )
                    .externalReference(charge.getId().toString())
                    .build();

            Preference preference = preferenceClient.create(request);

            charge.setIdExternMecadoPago(preference.getId());

            return preference.getInitPoint();
        } catch (MPException | MPApiException e) {
            throw new RuntimeException("Erro ao criar link de pagamento: " + e.getMessage(), e);
        }
    }

    public String checkStatusPayment(String idCharge) {
        try {
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.valueOf(idCharge));
            return payment.getStatus();
        } catch (MPException | MPApiException e) {
            throw new RuntimeException("Erro ao verificar status do pagamento: " + e.getMessage(),e );
        }
    }
}
