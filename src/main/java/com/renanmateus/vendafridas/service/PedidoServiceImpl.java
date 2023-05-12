package com.renanmateus.vendafridas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renanmateus.vendafridas.model.Estado;
import com.renanmateus.vendafridas.model.Item;
import com.renanmateus.vendafridas.model.Pedido;
import com.renanmateus.vendafridas.model.PedidoFinal;
import com.renanmateus.vendafridas.repository.ItemRepository;
import com.renanmateus.vendafridas.repository.PedidoFinalRepository;
import com.renanmateus.vendafridas.repository.PedidoRepository;
import com.renanmateus.vendafridas.utils.SplitItemIdFromString;

@Service
public class PedidoServiceImpl implements PedidoService {

	private BigDecimal valor;
	private BigDecimal valorFinal;
	//private final static double TAXA_CARTAO = 1-0.019; // Taxa de 1.9%
	private final static BigDecimal TAXA_CARTAO= new BigDecimal("0.981"); 
	private BigDecimal valorCartao;
	private BigDecimal valorDinheiro;
	
	private List<Item> it = new ArrayList<Item>();

	@Autowired
	private PedidoRepository pedidoRepository;
	

	@Autowired
	private PedidoFinalRepository pedidoFinalRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private FaturamentoService faturamentoService;

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
	public Pedido addItemPedido(Pedido pedido) {
		List<Item> itens = pedido.getItens();
		this.valor = new BigDecimal("0.00");
		itens.forEach(item -> {
			this.valor = this.valor.add(item.getPreco());
			pedido.setValor(this.valor);
			//pedido.setValorFinal(pedido.getValor());
		});
		
		//this.pedidoFinalRepository.save(new PedidoFinal(pedido)); // <== aqui
		return this.pedidoRepository.save(pedido);
	}

	@Override
	public Pedido removeItemPedido(Pedido pedido) {
		List<Item> itens = pedido.getItens();
		this.valor = new BigDecimal("0.00");
		if (itens.isEmpty()) {
			pedido.setValor(new BigDecimal("0.00"));
			pedido.setValorFinal(pedido.getValor());
			//this.pedidoFinalRepository.save(new PedidoFinal(pedido));
			return this.pedidoRepository.save(pedido);
		}
		itens.forEach(item -> {
			this.valor = this.valor.subtract(item.getPreco());
			//this.valor = item.getPreco().subtract(this.valor);
			pedido.setValor(this.valor.multiply(new BigDecimal("-1.00"))); // só mexa se realmente precisar. :D
			//pedido.setValor(this.valor);

		});
		this.pedidoFinalRepository.save(new PedidoFinal(pedido));
		return this.pedidoRepository.save(pedido);
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
		return this.pedidoRepository.findByEstadoOrderByPedidoIdDesc(Estado.Aberto);
	}
	
	@Override
	public List<Pedido> listarPedidosEncerrados(LocalDateTime hojeManha, LocalDateTime hojeNoite) {
		return this.pedidoRepository.buscarPedidosFechadosPorDia(hojeManha, hojeNoite);
	}

	@Override
	public void confimarPedido(Long pedidoId, Pedido p) {
		Pedido pedido = this.pedidoRepository.findById(pedidoId).get();

		Optional<PedidoFinal> pedidoFinal = this.pedidoFinalRepository.findById(pedidoId);
		if(pedidoFinal.isEmpty()) {
			PedidoFinal pf = new PedidoFinal();
			pf.setItens(new ArrayList<Item>());
			pf.setValorFinal(pedido.getValorFinal());
			pf.setPedidoFinalId(pedidoId);
			this.pedidoFinalRepository.save(pf);
		}
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
		pedido.setValorFinal(pedido.getValor().add(pedido.getValorFinal()));
		PedidoFinal pedidoFinal = this.pedidoFinalRepository.findById(pedidoId).get();
		List<Item> itens = new ArrayList<Item>(pedido.getItens());		
		itens.forEach(item -> pedidoFinal.getItens().add(item));
		this.pedidoFinalRepository.save(pedidoFinal);
		this.pedidoRepository.save(pedido);
		this.faturamentoService.salvar();
	}

	public void pagarItemPedidoEmDinheiro(Long pedidoId, String arrayItemId) {	
	this.valorDinheiro= new BigDecimal("0.00");
		List<Long> listItens = SplitItemIdFromString.splitString(arrayItemId);
	Pedido pedido = this.pedidoRepository.findById(pedidoId).get();

	List<Item> itensDoPedido = pedido.getItens();
		listItens.forEach(item -> {	
			Item i = this.itemRepository.findById(item).get();
			this.valorDinheiro = i.getPreco().add(this.valorDinheiro) ;
			this.it.add(i);
			itensDoPedido.remove(i);
		});	
		
		this.valor = new BigDecimal("0.00");
		itensDoPedido.forEach(item -> {
			this.valor =  item.getPreco().add(this.valor);
			pedido.setValor(this.valor);
		});
	
		pedido.setValorFinal(this.valorDinheiro.add(pedido.getValorFinal()));
		
		PedidoFinal pedidoFinal = this.pedidoFinalRepository.findById(pedidoId).get();
		this.it.forEach(ia -> pedidoFinal.getItens().add(ia));
		this.it.clear();
		pedidoFinal.setPedidoFinalId(pedido.getPedidoId());
		this.pedidoFinalRepository.save(pedidoFinal);
		this.pedidoRepository.save(pedido);
		this.faturamentoService.salvar();
	}

	@Override
	public void pagarValorTotalEmCartaoDebito(Long pedidoId) {
		Pedido pedido = this.pedidoRepository.findById(pedidoId).get();
		pedido.setEstado(Estado.Fechado);
		pedido.setHoraPedido(LocalDateTime.now());
		this.valorFinal = pedido.getValor().multiply(TAXA_CARTAO);
		pedido.setValorFinal(this.valorFinal.add(pedido.getValorFinal()));
		PedidoFinal pedidoFinal = this.pedidoFinalRepository.findById(pedidoId).get();
		List<Item> itens = new ArrayList<Item>(pedido.getItens());		
		itens.forEach(item -> pedidoFinal.getItens().add(item));
		this.pedidoFinalRepository.save(pedidoFinal);

		this.pedidoRepository.save(pedido);
		this.faturamentoService.salvar();
	}

	public void pagarItemPedidoEmCartao(Long pedidoId, String arrayItemId) {	
		this.valorCartao= new BigDecimal("0.00");
		List<Long> listItens = SplitItemIdFromString.splitString(arrayItemId);
		Pedido pedido = this.pedidoRepository.findById(pedidoId).get();
		List<Item> itensDoPedido = pedido.getItens();
		listItens.forEach(item -> {	
			Item i = this.itemRepository.findById(item).get();
			this.valorCartao = (i.getPreco().multiply(TAXA_CARTAO)).add(this.valorCartao);
			this.it.add(i);
			itensDoPedido.remove(i);		
	});	
		this.valor = new BigDecimal("0.00");
		itensDoPedido.forEach(it -> {
			this.valor =  it.getPreco().add(this.valor);
			pedido.setValor(this.valor);
	});	
	
	pedido.setValorFinal(this.valorCartao.add(pedido.getValorFinal()));
	PedidoFinal pedidoFinal = this.pedidoFinalRepository.findById(pedidoId).get();
	this.it.forEach(ia -> pedidoFinal.getItens().add(ia));
	this.it.clear();
	pedidoFinal.setPedidoFinalId(pedido.getPedidoId());
	this.pedidoRepository.save(pedido);
	this.faturamentoService.salvar();
	}

	@Override
	public void removerPedido(Long pedidoId) {
		this.faturamentoService.atualizarTodosFaturamentos(pedidoId);
	}
}
