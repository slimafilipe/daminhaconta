package dev.filipe.daminhaconta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private BigDecimal transaction_amount;
    private String description;
    private Payer payer;
}
