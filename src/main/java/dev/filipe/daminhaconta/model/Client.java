package dev.filipe.daminhaconta.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private  String name;

    private  int numberPhone;

    private String email;

    private String cpfCnpj;

    private BigDecimal valuePayment;

    private String PaymentPreferenceMethod;

    private String idExtern;
}
