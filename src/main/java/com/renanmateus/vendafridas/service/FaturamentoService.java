package com.renanmateus.vendafridas.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renanmateus.vendafridas.model.Faturamento;
import com.renanmateus.vendafridas.model.FaturamentoMensal;
import com.renanmateus.vendafridas.model.FaturamentoSemanal;
import com.renanmateus.vendafridas.repository.FaturamentoMensalRepository;
import com.renanmateus.vendafridas.repository.FaturamentoRepository;
import com.renanmateus.vendafridas.repository.FaturamentoSemanalRepository;
import com.renanmateus.vendafridas.repository.PedidoRepository;

@Service

public class FaturamentoService {

	@Autowired
	private FaturamentoRepository faturamentoRepository;

	@Autowired
	private FaturamentoSemanalRepository faturamentoSemanalRepository;

	@Autowired
	private FaturamentoMensalRepository faturamentoMensalRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;

	
	
	// Retornando um faturamento mensal para ser salvo em um faturamentoDiário;
	// Se o faturamento mensal existir, salve ele
	// se negativo, crie um e insira-o no faturamento diário.
	public FaturamentoSemanal retornaFaturamentoMensal() {
		
			Optional <FaturamentoSemanal> faturamentoSemanal= this.faturamentoSemanalRepository.buscarFaturamentoSemanalPorIntervaloDeData(LocalDate.now());
		if (faturamentoSemanal.isEmpty()) 	{
			LocalDate inicio = LocalDate.now(ZoneId.of("America/Sao_Paulo"))
					.with(TemporalAdjusters.previousOrSame(DayOfWeek.TUESDAY));
			LocalDate fim = inicio.plusDays(5);
	
			return this.faturamentoSemanalRepository.save(new FaturamentoSemanal(inicio, fim));
			
		}
return faturamentoSemanal.get();
	}
	
	
	public Faturamento salvar() {
		LocalDateTime hojeManha = LocalDate.now().atStartOfDay();
		LocalDateTime hojeNoite = LocalDate.now().atTime(23, 59, 59);
		
		BigDecimal valorFaturamentoDiario = this.pedidoRepository.sumPedidos(hojeManha, hojeNoite);
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
			return this.faturamentoSemanalRepository
					.save(new FaturamentoSemanal(inicio, fim, valorFaturamento, qntPedidos));
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
		return this.faturamentoMensalRepository
				.save(new FaturamentoMensal(inicio, fim, valorFaturamento, qntPedidos));
	}	
	// já existe
	// salvando o valor do faturamento mensal no objeto faturamento
	fat.get().setValorFaturamento(valorFaturamento);
	fat.get().setQuantidadePedido(qntPedidos);
	return this.faturamentoMensalRepository.save(fat.get());
}

}
