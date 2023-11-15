package com.renanmateus.vendafridas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.renanmateus.vendafridas.model.PedidoItem;
import com.renanmateus.vendafridas.repository.PedidoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renanmateus.vendafridas.model.Estado;
import com.renanmateus.vendafridas.model.Item;
import com.renanmateus.vendafridas.model.Pedido;
import com.renanmateus.vendafridas.repository.ItemRepository;
import com.renanmateus.vendafridas.repository.PedidoRepository;
import com.renanmateus.vendafridas.utils.SplitItemIdFromString;

@Service
public class PedidoServiceImpl implements PedidoService {

	private BigDecimal valor;
	private BigDecimal valorPedido;
	//private final static double TAXA_CARTAO = 1-0.019; // Taxa de 1.9%
	private final static BigDecimal TAXA_CARTAO= new BigDecimal("0.981"); 
	private List<Item> it = new ArrayList<Item>();
	@Autowired
	private PedidoRepository pedidoRepository;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private FaturamentoService faturamentoService;
	@Autowired
	private PedidoItemRepository pedidoItemRepository;

	@Override
	public Pedido novoPedido() {
		//pedido.setHoraPedido(HoraPedidoUtils.converteDataHora());
		Pedido pedido = new Pedido();
		pedido.setHoraPedido(LocalDateTime.now());
		pedido.setEstado(Estado.Criado);
		// VERIFIQUE SE O PEDIDO JA EXISTE, E SE POSITIVO, NAO SALVE O NUMEROPEDIDODIA
		//contando quantidade de pedidos por dia!
		long countPedido =  this.pedidoRepository.countHoraPedido(LocalDate.now().atStartOfDay(), LocalDate.now().atTime(23, 59, 59));
		pedido.setNumeroPedidoDia(countPedido+1);
		return this.pedidoRepository.save(pedido);
	}
	@Override
	public BigDecimal getValorPedido(Long pedidoId) {
		this.valorPedido = new BigDecimal("0.00");
		List<Item> itens = this.itemRepository.findByPedidoItensPedidoPedidoIdAndPedidoItensIsPago(pedidoId, false);
		itens.forEach(its -> valorPedido = its.getPreco().add(valorPedido));  // somando o valor Total dos itens do pedido
		return valorPedido;
	}

	@Override
	public void addItemPedido(Long pedidoId, Long itemId) {
		Pedido p = new Pedido();
		p.setPedidoId(pedidoId);
		Item i = new Item();
		i.setItemId(itemId);
		List<Item> itens = this.itemRepository.findByPedidoItensPedidoPedidoId(pedidoId); 	// pegar todos os itens, pagos ou não para salvar o valor final do pedido
		itens.add(this.itemRepository.findById(itemId).get());

		this.valor = new BigDecimal("0.00");
		itens.forEach(item -> {
			this.valor = this.valor.add(item.getPreco());
		});

		PedidoItem piAtual = new PedidoItem();
		piAtual.setItem(i);
		piAtual.setPedido(p);
		piAtual.setDataCriacao(LocalDateTime.now());

		this.pedidoItemRepository.save(piAtual);
		this.pedidoRepository.atualizaValorPedido(pedidoId, this.valor);
	}

	@Override
	public void removeItemPedido(Long pedidoId, Long itemId) {
		this.valor = new BigDecimal("0.00");
		List<Item> itens = this.itemRepository.findByPedidoItensPedidoPedidoIdAndPedidoItensIsPago(pedidoId, false);
		// somando o valor Total dos itens do pedido
		itens.forEach(its -> this.valor = its.getPreco().add(this.valor));
		for(int element = 0; element < itens.size(); element++) {
			if(itens.get(element).getItemId() == itemId) {
				this.valor = this.valor.subtract(itens.get(element).getPreco());
				itens.remove(element);
				break;
			}
		}
		PedidoItem pedidoItem = this.pedidoItemRepository.findFirstByPedidoPedidoIdAndItemItemIdAndIsPago(pedidoId, itemId, false);
		this.pedidoItemRepository.deleteById(pedidoItem.getId());
		this.pedidoRepository.atualizaValorPedido(pedidoId, this.valor);
	}
	
	@Override
	public Pedido buscarPedido(Long pedidoId) {
		Pedido pedido = this.pedidoRepository.findById(pedidoId).get();
		if (pedido.getCliente() != null) {
			pedido.setEditName(true);
		}
		return pedido;
	}

	@Override
	public void renomearPedido(Long pedidoId, Pedido p) {
		Pedido pedido = this.pedidoRepository.findById(pedidoId).get();
		pedido.setCliente(p.getCliente());
		this.pedidoRepository.save(pedido);
	}
	
	@Override
	public List<Pedido> listarPedidos() {
		List<PedidoItem> pedidoItems = this.pedidoItemRepository.findByIsPagoAndPedidoEstado(true, Estado.Aberto);
		List<Pedido> pedidos =  this.pedidoRepository.findByEstadoOrderByPedidoIdDesc(Estado.Aberto);
		pedidoItems.forEach(pedidoItem -> {
			pedidos.forEach(pedido -> {
				if (pedidoItem.getPedido().getPedidoId() == pedido.getPedidoId()) {
					pedido.setValor(pedido.getValor().subtract(pedidoItem.getItem().getPreco()));
				}
			});
		});
		return pedidos;
	}
	
	@Override
	public List<Pedido> listarPedidosEncerrados(LocalDateTime hojeManha, LocalDateTime hojeNoite) {
		return this.pedidoRepository.buscarPedidosFechadosPorDia(hojeManha, hojeNoite);
	}

	@Override
	public void confimarPedido(Long pedidoId, Pedido p) {
		Pedido pedido = this.pedidoRepository.findById(pedidoId).get();
		if (pedido.getCliente() == null) {
			pedido.setCliente(p.getCliente());
		}
		pedido.setEstado(Estado.Aberto);
		this.pedidoRepository.save(pedido);
	}
	
	@Override
	public void pagar(Long pedidoId) {
		Pedido pedido = this.pedidoRepository.findById(pedidoId).get();
		pedido.setEstado(Estado.Fechado);
		pedido.setHoraPedido(LocalDateTime.now());
		this.pedidoRepository.save(pedido);
		this.faturamentoService.salvar();
	}

	@Override
	public void pagarValorTotalEmDinheiro(Long pedidoId) {
		Pedido pedido = this.pedidoRepository.findById(pedidoId).get();
		pedido.setEstado(Estado.Fechado);
		pedido.setHoraPedido(LocalDateTime.now());
		this.pedidoItemRepository.updatePedidoItemIsPagoByPedidoItemPedido(true, false, pedido);
		this.pedidoRepository.save(pedido);
		this.faturamentoService.salvar();
	}

	public void pagarItemPedidoEmDinheiro(Long pedidoId, String arrayItemId) {	
		List<Long> itensAPagar = SplitItemIdFromString.splitString(arrayItemId); // lista de itens selecionados no frontend
		itensAPagar.forEach(item -> {
			PedidoItem pedidoItem = this.pedidoItemRepository.findFirstByPedidoPedidoIdAndItemItemIdAndIsPago(pedidoId, item, false);
			this.pedidoItemRepository.updatePedidoItemIsPagoAndIsCardPaymentById(true, false, pedidoItem.getId());

		});
		this.faturamentoService.salvar();
	}

	@Override
	public void pagarValorTotalEmCartaoDebito(Long pedidoId) {
		Pedido pedido = this.pedidoRepository.findById(pedidoId).get();
		pedido.setEstado(Estado.Fechado);
		pedido.setHoraPedido(LocalDateTime.now());
		pedido.setValor(pedido.getValor().multiply(TAXA_CARTAO));
		this.pedidoRepository.save(pedido);
		this.pedidoItemRepository.updatePedidoItemIsPagoByPedidoItemPedido(true, true,pedido);
		this.faturamentoService.salvar();
	}

	public void pagarItemPedidoEmCartao(Long pedidoId, String arrayItemId) {
		List<Long> itensAPagar = SplitItemIdFromString.splitString(arrayItemId); // lista de itens selecionados no frontend
		itensAPagar.forEach(item -> {
			PedidoItem pedidoItem = this.pedidoItemRepository.findFirstByPedidoPedidoIdAndItemItemIdAndIsPago(pedidoId, item, false);
			this.pedidoItemRepository.updatePedidoItemIsPagoAndIsCardPaymentById(true,true,  pedidoItem.getId());
		});
		this.faturamentoService.salvar();
	}

	@Override
	public void removerPedido(Long pedidoId) {
		Pedido pedido = this.pedidoRepository.findById(pedidoId).get();
		this.pedidoItemRepository.deleteByPedidoPedidoId(pedidoId);
		this.pedidoRepository.deleteById(pedidoId);
		this.faturamentoService.atualizarTodosFaturamentos(pedido);
	}
}
