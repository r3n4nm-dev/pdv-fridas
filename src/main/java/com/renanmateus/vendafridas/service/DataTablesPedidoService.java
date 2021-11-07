package com.renanmateus.vendafridas.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.renanmateus.vendafridas.model.Item;
import com.renanmateus.vendafridas.repository.PedidoRepository;


public class DataTablesPedidoService {


	public Map<String, Object> execute(PedidoRepository pedidoRepository, Long id, HttpServletRequest request) {
		int draw = Integer.parseInt(request.getParameter("draw"));
		List<Item> pagina = queryBy(pedidoRepository, id);
		
		Map<String, Object> json = new LinkedHashMap<>();
		json.put("draw", draw);
	
		json.put("data", pagina);

		return json;
	}


	private List<Item> queryBy(PedidoRepository pedidoRepository, Long id) {
			  
			  return pedidoRepository.findById(id).get().getItens();
			  }


}