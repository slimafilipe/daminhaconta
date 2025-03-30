package dev.filipe.daminhaconta.dto;

import dev.filipe.daminhaconta.model.Client;
import dev.filipe.daminhaconta.model.Charge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestBody {
    private Client client;
    private Charge charge;
}
