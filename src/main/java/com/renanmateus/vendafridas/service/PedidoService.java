package com.renanmateus.vendafridas.service;

import java.time.LocalDateTime;
import java.util.List;

import com.renanmateus.vendafridas.model.Pedido;

public interface PedidoService {

	 Pedido novoPedido();
	
	 Pedido buscarPedido(Long pedidoId);
		
	 List<Pedido> listarPedidos();

	Pedido addItemPedido(Pedido pedido);

	Pedido removeItemPedido(Pedido pedido);

	void pagarValorTotalEmDinheiro(Long pedidoId);

	void pagarValorTotalEmCartaoDebito(Long pedidoId);
	
	void pagar(Long pedidoId);

	List<Pedido> listarPedidosEncerrados(LocalDateTime hojeManha, LocalDateTime hojeNoite);

	void confimarPedido(Long pedidoId, Pedido p);
	
	void removerPedido(Long pedidoId);

	void renomearPedido(Long pedidoId, Pedido pedido);
	
	
}