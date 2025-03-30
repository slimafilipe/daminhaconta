package dev.filipe.daminhaconta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymenteResponse {

    private String initPoint;
    private String status;
    private String id;
}
