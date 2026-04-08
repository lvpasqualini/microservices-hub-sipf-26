package com.github.lvpasqualini.ms.pagamento.dto;

import com.github.lvpasqualini.ms.pagamento.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StatusDTO {
    private Status status;
}
