package com.renanmateus.vendafridas.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="pedidoId")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name="itemId")
    private Item item;
    @CreatedDate
    private LocalDateTime dataCriacao;
    @LastModifiedDate
    private LocalDateTime dataAtualizacao;
    private boolean isPago;
    private boolean isCardPayment;
}
