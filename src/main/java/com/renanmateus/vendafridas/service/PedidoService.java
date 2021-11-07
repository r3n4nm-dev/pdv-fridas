package com.renanmateus.vendafridas.service;

import java.util.List;

import com.renanmateus.vendafridas.model.Pedido;

public interface PedidoService {

	 Pedido novoPedido();
	
	 Pedido buscarPedido(Long pedidoId);
		
	 List<Pedido> listarPedidos();

	Pedido addItemPedido(Pedido pedido);

	Pedido removeItemPedido(Pedido pedido);

	List<Pedido> listarPedidosEncerrados();

	void confimarPedido(Long pedidoId);


	void pagarValorTotalEmDinheiro(Long pedidoId);

	void pagarValorTotalEmCartaoDebito(Long pedidoId);
	
	void pagar(Long pedidoId);
	
	
}