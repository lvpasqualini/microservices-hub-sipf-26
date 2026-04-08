package com.github.lvpasqualini.ms.pagamento.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_pagamento")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private BigDecimal valor;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, length = 16)
    private String numeroCartao;
    @Column(nullable = false,length = 5)
    private String validade;
    @Column(nullable = false,length = 3)
    private String codigoSeguranca;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(nullable = false,length = 16)
    private Long pedidoId;
}
