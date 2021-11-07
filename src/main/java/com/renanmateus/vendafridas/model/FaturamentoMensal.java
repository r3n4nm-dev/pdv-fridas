package com.renanmateus.vendafridas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class FaturamentoMensal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private LocalDate dataInicial;
	
	@Column(nullable = false)
	private LocalDate dataFinal;
	
	@Column
	private BigDecimal valorFaturamento = new BigDecimal("0.00");
	
	@Column
	private long quantidadePedido;

	public FaturamentoMensal(LocalDate dataInicial, LocalDate dataFinal, BigDecimal valorFaturamento, long quantidadePedido) {
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
		this.valorFaturamento = valorFaturamento;
		this.quantidadePedido = quantidadePedido;
	}
	
	

		
	
}
