package com.renanmateus.vendafridas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Faturamento")

public class Faturamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long faturamentoId;
	
	@Column(nullable = false)
	private LocalDate dataFaturamento;
	
	@Column
	private BigDecimal valorFaturamento = new BigDecimal("0.00");
	
	@Column
	private long quantidadePedido;
		
	@JoinColumn(name = "faturamentoSemanalId", referencedColumnName = "faturamentoSemanalId")
    @ManyToOne(optional = true) // aceita valor nulo para esta relação
    private FaturamentoSemanal faturamentoSemanalId;
	
	
	public Faturamento(BigDecimal valorFaturamento, LocalDate dataFaturamento, long quantidadePedido, FaturamentoSemanal faturamentoSemanalId) {
		this.valorFaturamento = valorFaturamento;
		this.dataFaturamento = dataFaturamento;
		this.quantidadePedido = quantidadePedido;
		this.faturamentoSemanalId = faturamentoSemanalId;
	}
	
	public Faturamento(LocalDate dataFaturamento,  long quantidadePedido, FaturamentoSemanal faturamentoSemanalId) {
		this.dataFaturamento = dataFaturamento;
		this.quantidadePedido = quantidadePedido;
		this.faturamentoSemanalId = faturamentoSemanalId;


		
	}
}
