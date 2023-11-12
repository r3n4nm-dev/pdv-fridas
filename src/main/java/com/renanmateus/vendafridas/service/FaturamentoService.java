package com.renanmateus.vendafridas.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import com.renanmateus.vendafridas.model.Pedido;
import com.renanmateus.vendafridas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renanmateus.vendafridas.model.Faturamento;
import com.renanmateus.vendafridas.model.FaturamentoMensal;
import com.renanmateus.vendafridas.model.FaturamentoSemanal;

@Service

public class FaturamentoService {
	private final static BigDecimal TAXA_CARTAO= new BigDecimal("0.981");
	@Autowired
	private FaturamentoRepository faturamentoRepository;

	@Autowired
	private FaturamentoSemanalRepository faturamentoSemanalRepository;

	@Autowired
	private FaturamentoMensalRepository faturamentoMensalRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private PedidoItemRepository pedidoItemRepository;

	// Retornando um faturamento mensal para ser salvo em um faturamentoDiário;
	// Se o faturamento mensal existir, salve ele
	// se negativo, crie um e insira-o no faturamento diário.
	public FaturamentoSemanal retornaFaturamentoMensal() {
		Optional <FaturamentoSemanal> faturamentoSemanal= this.faturamentoSemanalRepository.buscarFaturamentoSemanalPorIntervaloDeData(LocalDate.now());
		if (faturamentoSemanal.isEmpty()) 	{
			LocalDate inicio = LocalDate.now(ZoneId.of("America/Sao_Paulo")).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
			LocalDate fim = inicio.plusDays(6);
			return this.faturamentoSemanalRepository.save(new FaturamentoSemanal(inicio, fim));
		}
		return faturamentoSemanal.get();
	}

	// usado para a remoção de pedidos afim de atulizar o faturamento, reduzindo o valor do pedido do faturamento
	public Faturamento atualizarTodosFaturamentos(Pedido pedido) {
		LocalDateTime hoje = pedido.getHoraPedido();
		LocalDateTime hojeManha = hoje.toLocalDate().atStartOfDay();
		LocalDateTime hojeNoite = hoje.toLocalDate().atTime(23, 59, 59);

		// refatorar essa bosta
		// Cenário: ao iniciar um pedido e não confirma-lo quando
		// removo pedidos já finalizados o faturamento conta esse pedidos que não
		// foram confirmados no valor final do faturamento

		// está somando itens de um pedido não confirmado no faturamento!
		// e só corrige quando um pagamento é efetuado, já que o faturamento é atualizado.
		BigDecimal valorFaturamentoDiario = this.pedidoRepository.sumPedidos(hojeManha, hojeNoite);
		long qntPedidos = pedidoRepository.countHoraPedido(hojeManha,hojeNoite);
		Optional<Faturamento> faturamento = this.faturamentoRepository.findByDataFaturamento(hoje.toLocalDate());
		faturamento.get().setValorFaturamento(valorFaturamentoDiario);
		faturamento.get().setQuantidadePedido(qntPedidos);
		return this.faturamentoRepository.save(faturamento.get());
	}
	
	public Faturamento salvar() {
		LocalDateTime hojeManha = LocalDate.now().atStartOfDay();
		LocalDateTime hojeNoite = LocalDate.now().atTime(23, 59, 59);
		// salvando os itens já pagos, ao invés de salvar o pedido, que pode não ter sido totalmente pago.
		BigDecimal valorFaturamentoDiarioEmDinheiro = this.pedidoItemRepository.somarValorItensPagosEmDinheiroOuCartao(true, false,  hojeManha, hojeNoite);
		BigDecimal valorFaturamentoDiarioEmCartao = this.pedidoItemRepository.somarValorItensPagosEmDinheiroOuCartao(true, true,  hojeManha, hojeNoite);
		valorFaturamentoDiarioEmDinheiro = valorFaturamentoDiarioEmDinheiro != null ? valorFaturamentoDiarioEmDinheiro : new BigDecimal("0.00");
		valorFaturamentoDiarioEmCartao = valorFaturamentoDiarioEmCartao != null ? valorFaturamentoDiarioEmCartao.multiply(TAXA_CARTAO) : new BigDecimal("0.00");
		BigDecimal valorFaturamentoDiario = valorFaturamentoDiarioEmCartao.add(valorFaturamentoDiarioEmDinheiro);


		long qntPedidos = pedidoRepository.countHoraPedido(hojeManha,hojeNoite);
		
		Optional<Faturamento> faturamento = this.faturamentoRepository.findByDataFaturamento(LocalDate.now());
		if (faturamento.isEmpty()) {
			return this.faturamentoRepository.save(new Faturamento(valorFaturamentoDiario, LocalDate.now(), qntPedidos, retornaFaturamentoMensal()));
		}

		faturamento.get().setValorFaturamento(valorFaturamentoDiario);
		faturamento.get().setQuantidadePedido(qntPedidos);
		return this.faturamentoRepository.save(faturamento.get());
	}

	public Faturamento buscarFaturamentoDiario(LocalDate data) {
		Optional<Faturamento> faturamento = this.faturamentoRepository.findByDataFaturamento(data);
		Optional <FaturamentoSemanal> faturamentoSemanal= this.faturamentoSemanalRepository.buscarFaturamentoSemanalPorIntervaloDeData(LocalDate.now());

		if (faturamento.isEmpty()) {
			return this.faturamentoRepository.save(new Faturamento(data, 0L, faturamentoSemanal.get()));
		}
		return faturamento.get();
	}

	public FaturamentoSemanal buscarFaturamentoSemanal(LocalDate inicio, LocalDate fim) {
			// Recebendo faturamento semanal 
		BigDecimal valorFaturamento = this.faturamentoRepository.sumFaturamentos(inicio, fim);
			// Recebendo quantidade de pedidos semanal
		Optional<FaturamentoSemanal> fat = this.faturamentoSemanalRepository.findByDataInicial(inicio);
		Long qntPedidos = this.faturamentoRepository.sumQntPedidos(inicio, fim);
		List<Faturamento> listaFaturamentoDiario = this.faturamentoRepository.findByDataFaturamentoBetween(inicio, fim);
			// Este faturamento semanal já existe?
		
		if (fat.isEmpty()) {
			// Não existe
			// Criando novo faturamento
			return this.faturamentoSemanalRepository.save(new FaturamentoSemanal(inicio, fim, valorFaturamento, qntPedidos));
	}
		// já existe
		// salvando o valor do faturamento semanal no objeto faturamento
		fat.get().setFaturamentosDiarios(listaFaturamentoDiario);
		fat.get().setValorFaturamento(valorFaturamento);
		fat.get().setQuantidadePedido(qntPedidos);
		return this.faturamentoSemanalRepository.save(fat.get());
	}

	public FaturamentoMensal buscarFaturamentoMensal(LocalDate inicio, LocalDate fim) {
		// Recebendo faturamento semanal 
		BigDecimal valorFaturamento = this.faturamentoRepository.sumFaturamentos(inicio, fim);
			// Recebendo quantidade de pedidos semanal
		Optional<FaturamentoMensal> fat = this.faturamentoMensalRepository.findByDataInicial(inicio);
		Long qntPedidos = this.faturamentoRepository.sumQntPedidos(inicio, fim);
			// Este faturamento semana já existe?
		if (fat.isEmpty()) {
			// Não existe
			// Criando novo faturamento
			return this.faturamentoMensalRepository.save(new FaturamentoMensal(inicio, fim, valorFaturamento, qntPedidos));
		}
		// já existe
		// salvando o valor do faturamento mensal no objeto faturamento
		fat.get().setValorFaturamento(valorFaturamento);
		fat.get().setQuantidadePedido(qntPedidos);
		return this.faturamentoMensalRepository.save(fat.get());
	}
}
