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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


import lombok.Data;

@Data
@Entity
@Table(name = "Pedidos")
public class Pedido {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long pedidoId;
	private LocalDateTime horaPedido;
	
	@Lob
	private String cliente;
	
	@ManyToMany (fetch=FetchType.EAGER)
	@JoinTable(name = "pedido_itens", joinColumns = @JoinColumn(name = "pedidoId", referencedColumnName = "pedidoId"), inverseJoinColumns = @JoinColumn(name = "itemId", referencedColumnName = "itemId"))
	private List<Item> itens;
	
	private Estado estado;
	// valor da soma dos pedidos
	private BigDecimal valor = new BigDecimal("0.00");
	private BigDecimal valorFinal = new BigDecimal("0.00");
	
	private long numeroPedidoDia;






	
}
