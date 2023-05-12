package com.renanmateus.vendafridas.controller;



import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.renanmateus.vendafridas.model.Estado;
import com.renanmateus.vendafridas.model.Faturamento;
import com.renanmateus.vendafridas.model.FaturamentoSemanal;
import com.renanmateus.vendafridas.model.Item;
import com.renanmateus.vendafridas.model.Pedido;
import com.renanmateus.vendafridas.model.PedidoFinal;
import com.renanmateus.vendafridas.model.Tipo;
import com.renanmateus.vendafridas.repository.ItemRepository;
import com.renanmateus.vendafridas.repository.PedidoFinalRepository;
import com.renanmateus.vendafridas.repository.PedidoRepository;
import com.renanmateus.vendafridas.service.DataTablesPedidoService;
import com.renanmateus.vendafridas.service.FaturamentoService;
import com.renanmateus.vendafridas.service.PedidoServiceImpl;

@Controller
@RequestMapping(value = {"/", "/pedidos"} )
public class PedidoController {

	
	@Autowired
	private FaturamentoService faturamentoService;
	
	@Autowired
	private PedidoServiceImpl pedidoService;
	
	@Autowired 
	private ItemRepository itemRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private PedidoFinalRepository pedidoFinalRepository;

	
	
	private boolean pago;
	
	public void setPago(boolean pago) {
		this.pago = pago;
	}
	public boolean isPago() {
		return pago;
	}	

	/////////////////////////////// DATATABLES ////////////////////////////////
	
	@GetMapping("/lista/{id}")
	public ResponseEntity<?> dataTables(HttpServletRequest request, @PathVariable Long id) {
		Map<String, Object> data = new DataTablesPedidoService().execute(this.pedidoRepository, id,  request);
		return ResponseEntity.ok(data);
	}
	
	@GetMapping("/pagament/{id}")
	public ResponseEntity<?> dataTablesPagamento(HttpServletRequest request, @PathVariable Long id) {
		Map<String, Object> data = new DataTablesPedidoService().execute(this.pedidoRepository, id,  request);
		return ResponseEntity.ok(data);
	}
	
	@GetMapping
	public String pedidos(ModelMap modelMap) {
		
	  LocalDate inicioSemana = LocalDate.now(ZoneId.of("America/Sao_Paulo")).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)); 
	  LocalDate fimSemana = inicioSemana.plusDays(6);
	  FaturamentoSemanal faturamentoSemanaAtual = this.faturamentoService.buscarFaturamentoSemanal(inicioSemana, fimSemana);
	  Faturamento faturamentoOntem = this.faturamentoService.buscarFaturamentoDiario(LocalDate.now().minusDays(1));
	  Faturamento faturamentoHoje = this.faturamentoService.buscarFaturamentoDiario(LocalDate.now());
	  if (faturamentoHoje.getValorFaturamento() == null) {
		  faturamentoHoje.setValorFaturamento(new BigDecimal("0.00"));
	  }
		modelMap.addAttribute("fa", faturamentoSemanaAtual);
		modelMap.addAttribute("faturamentoHoje", faturamentoHoje);
		modelMap.addAttribute("faturamentoOntem", faturamentoOntem);
		modelMap.addAttribute("pedidos", this.pedidoService.listarPedidos());

		if(isPago()) {
			modelMap.addAttribute("pago", true);
			setPago(false);
		}
		return "pedidos";
	}
	
	@GetMapping("/pedidos-encerrados")
	public String pedidosEncerrados(ModelMap modelMap) {
		LocalDateTime hojeManha = LocalDate.now().atStartOfDay();
		LocalDateTime hojeNoite = LocalDate.now().atTime(23, 59, 59);
		List<Pedido> pedidosEncerradosHoje = this.pedidoService.listarPedidosEncerrados(hojeManha, hojeNoite);
		List<Pedido> pedidosEncerradosOntem = this.pedidoService.listarPedidosEncerrados(hojeManha.minusDays(1), hojeNoite.minusDays(1));

		modelMap.addAttribute("pedidosEncerradosHoje",pedidosEncerradosHoje);
		modelMap.addAttribute("pedidosEncerradosOntem",pedidosEncerradosOntem);

		return "pedidos-encerrados";
		
	}
	///////////////////////// PEDIDO FECHADO ID \\\\\\\\\\\\\\\\\\\\\\\\\\

	@GetMapping("/encerrado/{pedidoId}")
	
	public String buscaPedidoFechado(@PathVariable Long pedidoId, ModelMap modelMap) {
		Optional<PedidoFinal> pedidoFinal = this.pedidoFinalRepository.findById(pedidoId);
		Optional<Pedido> pedido = this.pedidoRepository.findById(pedidoId);
		modelMap.addAttribute("pedido",pedido.get());
		modelMap.addAttribute("pedidoFinal",pedidoFinal.get());
		return "encerrado";
	}
		
	///////////////////////// PEDIDO ID \\\\\\\\\\\\\\\\\\\\\\\\\\
	@GetMapping("/{pedidoId}")
	public String buscarPedido(@PathVariable Long pedidoId, ModelMap modelMap, ModelAndView model) {
		Pedido pedido = this.pedidoService.buscarPedido(pedidoId);
		if(pedido.getEstado().equals(Estado.Fechado)) {
			return "redirect:/pedidos/encerrado/"+pedidoId;
		}
		model.addObject("pedido",pedido);
		//recupera
		modelMap.addAttribute("editName", pedido.isEditName());
		modelMap.addAttribute("pedido",pedido);
		modelMap.addAttribute("pao",this.itemRepository.findByTipoAndDeleted(Tipo.Pão, false));
		modelMap.addAttribute("tapiocas",this.itemRepository.findByTipoAndDeleted(Tipo.Tapioca, false));
		modelMap.addAttribute("tapiocas_doces",this.itemRepository.findByTipoAndDeleted(Tipo.Tapioca_Doce, false));
		modelMap.addAttribute("bebidas",this.itemRepository.findByTipoAndDeleted(Tipo.Bebida, false));
		modelMap.addAttribute("bebidas_quentes",this.itemRepository.findByTipoAndDeleted(Tipo.Bebida_Quente, false));
		modelMap.addAttribute("cuscuz",this.itemRepository.findByTipoAndDeleted(Tipo.Cuscuz, false));
		modelMap.addAttribute("ovos",this.itemRepository.findByTipoAndDeleted(Tipo.Ovos, false));
		modelMap.addAttribute("caseiro",this.itemRepository.findByTipoAndDeleted(Tipo.Pão_Caseiro, false));
		modelMap.addAttribute("crepioca",this.itemRepository.findByTipoAndDeleted(Tipo.Crepioca, false));
		modelMap.addAttribute("adicional",this.itemRepository.findByTipoAndDeleted(Tipo.Adicional, false));
		return "detalhes-pedido";
	}
	
	@GetMapping("/detalhes/{pedidoId}")
	public String detalhesPedido(@PathVariable Long pedidoId, ModelMap modelMap, ModelAndView model) {
		Pedido pedido = this.pedidoService.buscarPedido(pedidoId);
		model.addObject("pedido",pedido);
		modelMap.addAttribute("pedido",pedido);
		return "pedido";
	}
	
	///////////////////////// CRIAR PEDIDO \\\\\\\\\\\\\\\\\\\\\\\\\\
		@GetMapping("/novo-pedido")
		public String novoPedido() {
			Pedido pedido = this.pedidoService.novoPedido();
			//return "pedidos";
			return "redirect:/pedidos/"+pedido.getPedidoId();
		}

	///////////////////////// ADICIONAR ITEM PEDIDO \\\\\\\\\\\\\\\\\\\\\\\\\\
		@PostMapping("/add/{pedidoId}/{itemId}")
		public String addItem(@PathVariable Long pedidoId, @PathVariable Long itemId) {
			Pedido pedido = this.pedidoRepository.findById(pedidoId).get();
			List<Item> itens = pedido.getItens();
			itens.add(this.itemRepository.findById(itemId).get());
			this.pedidoService.addItemPedido(pedido);
			return "redirect:/pedidos/"+pedidoId;
		}
		
	///////////////////////// REMOVER ITEM PEDIDO \\\\\\\\\\\\\\\\\\\\\\\\\\
		@PostMapping("/remove/{pedidoId}/{itemId}")
		public String removeItem(@PathVariable Long pedidoId, @PathVariable Long itemId) {
			Pedido pedido = this.pedidoRepository.findById(pedidoId).get();
			List<Item> itens = pedido.getItens();
			itens.remove(this.itemRepository.findById(itemId).get());
			this.pedidoService.removeItemPedido(pedido);
			return "redirect:/pedidos/"+pedidoId;
		}

	///////////////////////// CONFIRMAR PEDIDO \\\\\\\\\\\\\\\\\\\\\\\\\\
	// Diferentes comportamentos para o mesmo método
	// Se o botão clicado for confirm
	@PostMapping(value = "/confirmar-pedido/{pedidoId}", params={"confirm"}	)
	public String confimar(@PathVariable Long pedidoId, RedirectAttributes attr, Pedido pedido) {
		pedidoService.confimarPedido(pedidoId, pedido);
		attr.addFlashAttribute("success", true);
		return "redirect:/pedidos";
	}
	// Se o botão clicado for rename
	@PostMapping(value = "/confirmar-pedido/{pedidoId}", params={"rename"}	)
	public String renomear(@PathVariable Long pedidoId, RedirectAttributes attr, Pedido pedido) {
		pedidoService.renomearPedido(pedidoId, pedido);
		return "redirect:/pedidos/"+pedidoId;	
		}
	
	///////////////////////// PAGAMENTOS \\\\\\\\\\\\\\\\\\\\\\\\\\

	@GetMapping("/pagamento/{pedidoId}")
	public String dividirPagamento(@PathVariable Long pedidoId, ModelMap modelMap) {
		modelMap.addAttribute("pedido",this.pedidoService.buscarPedido(pedidoId));
		return "pagamento";
		}
	
	@GetMapping("/pagar-valor-total-em-dinheiro/{pedidoId}")
	public String pagarValorTotalEmDinheiro(@PathVariable Long pedidoId) {
		this.pedidoService.pagarValorTotalEmDinheiro(pedidoId);
		setPago(true);
		return "redirect:pedidos";
	}
	
	@GetMapping("/pagar-valor-total-em-cartao/{pedidoId}")
	public String pagarValorTotalEmCartao(@PathVariable Long pedidoId) {
		this.pedidoService.pagarValorTotalEmCartaoDebito(pedidoId);
		setPago(true);
		return "redirect:pedidos";
	}

	@PostMapping("/pagar-item-dinheiro/{pedidoId}/{arrayItemId}")
	public String pagarItemPedidoDinheiro(@PathVariable Long pedidoId, @PathVariable String arrayItemId) {	
		this.pedidoService.pagarItemPedidoEmDinheiro(pedidoId, arrayItemId);
		return "redirect:/pagamento/"+pedidoId;
	}
	
	@PostMapping("/pagar-item-cartao/{pedidoId}/{arrayItemId}")
	public String pagarItemPedidoCartao(@PathVariable Long pedidoId, @PathVariable String arrayItemId) {	
		this.pedidoService.pagarItemPedidoEmCartao(pedidoId, arrayItemId);
		return "redirect:/pagamento/"+pedidoId;
	}
	
	@GetMapping("/pagar/{pedidoId}")
	public String pagar(@PathVariable Long pedidoId) {
		this.pedidoService.pagar(pedidoId);
		setPago(true);
		return "redirect:pedidos";
	}
	
	@GetMapping("/removerPedido/{pedidoId}")
	public String removerPedido(@PathVariable Long pedidoId) {
		this.pedidoService.removerPedido(pedidoId);
		return "redirect:/pedidos-encerrados";
	}

}
