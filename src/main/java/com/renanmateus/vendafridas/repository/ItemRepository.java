package com.renanmateus.vendafridas.repository;

import java.util.List;

import com.renanmateus.vendafridas.model.Estado;
import com.renanmateus.vendafridas.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.renanmateus.vendafridas.model.Item;
import com.renanmateus.vendafridas.model.Tipo;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

	// Listagem para preencher pagina de itens
	List<Item> findByTipoAndDeleted(Tipo tipo, boolean deleted);

	List<Item> findByPedidoItensPedidoPedidoIdAndPedidoItensIsPago(Long pedidoId, boolean isPago);

	List<Item> findByPedidoItensPedidoPedidoId(Long pedidoId);
	}
