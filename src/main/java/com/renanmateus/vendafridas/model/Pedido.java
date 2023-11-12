package com.renanmateus.vendafridas.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "Pedidos")
@NoArgsConstructor
@ToString(exclude = {"pedidoItens"}) // evicted Method threw 'java.lang.StackOverflowError' exception. Cannot evaluate com.renanmateus.vendafridas.model.Pedido.toString()
public class Pedido {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long pedidoId;
	private LocalDateTime horaPedido;
	private boolean editName; 
	
	@Lob
	private String cliente;

	@JsonIgnore
	@OneToMany(mappedBy = "pedido")
	private List<PedidoItem> pedidoItens;
	
	private Estado estado;
	// valor da soma dos pedidos
	private BigDecimal valor = new BigDecimal("0.00");

	private long numeroPedidoDia;

	public Pedido(Long pedidoId) {
		pedidoId = this.pedidoId;
	}




	
}
