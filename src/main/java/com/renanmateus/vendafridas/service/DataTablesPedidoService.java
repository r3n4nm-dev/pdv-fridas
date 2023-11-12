package com.renanmateus.vendafridas.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.renanmateus.vendafridas.model.Item;
import com.renanmateus.vendafridas.model.Pedido;
import com.renanmateus.vendafridas.repository.ItemRepository;
import com.renanmateus.vendafridas.repository.PedidoRepository;


public class DataTablesPedidoService {


	public Map<String, Object> execute(ItemRepository itemRepository, Long pedidoId, HttpServletRequest request) {
		int draw = Integer.parseInt(request.getParameter("draw"));
		List<Item> pagina = queryBy(itemRepository, pedidoId);
		
		Map<String, Object> json = new LinkedHashMap<>();
		json.put("draw", draw);
	
		json.put("data", pagina);

		return json;
	}


	private List<Item> queryBy(ItemRepository itemRepository, Long pedidoId) {
		return itemRepository.findByPedidoItensPedidoPedidoIdAndPedidoItensIsPago(pedidoId, false);
	}
}