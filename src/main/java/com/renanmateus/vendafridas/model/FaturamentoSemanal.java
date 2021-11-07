package com.renanmateus.vendafridas.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class FaturamentoSemanal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long faturamentoSemanalId;
	
	@Column(nullable = false)
	private LocalDate dataInicial;
	
	@Column(nullable = false)
	private LocalDate dataFinal;
	
	@Column
	private BigDecimal valorFaturamento = new BigDecimal("0.00");
	
	@Column
	private long quantidadePedido;
	
	@Column
	@OneToMany(mappedBy = "faturamentoSemanalId")
	private List<Faturamento> faturamentosDiarios;

	public FaturamentoSemanal(LocalDate dataInicial, LocalDate dataFinal, BigDecimal valorFaturamento, long quantidadePedido) {
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
		this.valorFaturamento = valorFaturamento;
		this.quantidadePedido = quantidadePedido;
	}

	public FaturamentoSemanal(LocalDate dataInicial, LocalDate dataFinal) {
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;	
		}

}
