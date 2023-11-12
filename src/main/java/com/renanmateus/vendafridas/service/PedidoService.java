package com.renanmateus.vendafridas.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.renanmateus.vendafridas.model.Item;
import com.renanmateus.vendafridas.model.Pedido;

public interface PedidoService {

	 Pedido novoPedido();
	
	 Pedido buscarPedido(Long pedidoId);
		
	 List<Pedido> listarPedidos();

	void addItemPedido(Long pedidoId, Long itemId);
	void removeItemPedido(Long pedidoId, Long itemId);
	void pagarValorTotalEmDinheiro(Long pedidoId);

	void pagarValorTotalEmCartaoDebito(Long pedidoId);
	
	void pagar(Long pedidoId);

	List<Pedido> listarPedidosEncerrados(LocalDateTime hojeManha, LocalDateTime hojeNoite);

	void confimarPedido(Long pedidoId, Pedido p);
	
	void removerPedido(Long pedidoId);

	void renomearPedido(Long pedidoId, Pedido pedido);
	BigDecimal getValorPedido(Long pedidoId);

	}