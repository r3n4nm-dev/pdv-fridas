package com.renanmateus.vendafridas.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;

@Data
@Entity
@Table(name = "Itens")
@SQLDelete(sql = "UPDATE itens SET deleted=true WHERE item_id=?")
@NoArgsConstructor
@ToString(exclude = {"pedidoItens"}) // evicted Method threw 'java.lang.StackOverflowError' exception. Cannot evaluate com.renanmateus.vendafridas.model.Pedido.toString()
// @Where(clause = "deleted = false")
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long itemId;
	private String nome;
	private BigDecimal preco;
	private Tipo tipo;
	private boolean deleted = Boolean.FALSE;
	@JsonIgnore // retirando recursividade da listagem de itens, que trazia item -> itemPedido -> item....
	@OneToMany(mappedBy = "item")
	private List<PedidoItem> pedidoItens;

 public Item(Long itemId) {
	 itemId = this.itemId;
 }

}
