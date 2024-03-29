package com.renanmateus.vendafridas.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name = "Itens")
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long itemId;
	@NotBlank(message=" É Obrigatorio.")
	private String nome;
	@NotNull(message=" Não pode ficar em branco.")
	private BigDecimal preco;
	@NotNull(message=" Não pode ficar em branco.")
	private Tipo tipo;



}
