package com.renanmateus.vendafridas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "Pedidos_Finais")
@AllArgsConstructor
@NoArgsConstructor

public class PedidoFinal {

	@Id
	private long pedidoFinalId;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "pedido_itens_finais", joinColumns = @JoinColumn(name = "pedidoFinalId", referencedColumnName = "pedidoFinalId"), inverseJoinColumns = @JoinColumn(name = "itemId", referencedColumnName = "itemId"))
	private List<Item> itens;


	private BigDecimal valorFinal = new BigDecimal("0.00");

	private long numeroPedidoDia;

	public PedidoFinal(Pedido pedido) {
		this.pedidoFinalId = pedido.getPedidoId();
		this.itens = pedido.getItens();
		this.valorFinal = pedido.getValorFinal();

	}

}
