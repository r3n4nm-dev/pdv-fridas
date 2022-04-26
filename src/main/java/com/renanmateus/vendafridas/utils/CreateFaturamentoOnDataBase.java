package com.renanmateus.vendafridas.utils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.renanmateus.vendafridas.model.FaturamentoSemanal;
import com.renanmateus.vendafridas.repository.FaturamentoRepository;
import com.renanmateus.vendafridas.repository.FaturamentoSemanalRepository;
@Component
public class CreateFaturamentoOnDataBase {
	@Autowired
	private FaturamentoSemanalRepository faturamentoSemanalRepository;
	
	@Autowired
	private FaturamentoRepository faturamentoRepository;

	
	@PostConstruct
	public void createInfoInDB() {

		
		
		// criando faturamento semanal da semana passada
		LocalDate inicio = LocalDate.now(ZoneId.of("America/Sao_Paulo"))
				.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
		//LocalDate fim = inicio.plusDays(5);
		
		LocalDate inicioDaSemanaAnterior = inicio.minusWeeks(1);
		LocalDate fimDaSemanaAnterior = inicioDaSemanaAnterior.plusDays(6);
		
		Optional<FaturamentoSemanal> faturamentoSemanaAnterior = this.faturamentoSemanalRepository.findByDataInicial(inicioDaSemanaAnterior);
		if (faturamentoSemanaAnterior.isEmpty()) {
			
			BigDecimal valorFaturamento = this.faturamentoRepository.sumFaturamentos(inicioDaSemanaAnterior, fimDaSemanaAnterior);
			Long qntPedidos = this.faturamentoRepository.sumQntPedidos(inicioDaSemanaAnterior, fimDaSemanaAnterior);

			this.faturamentoSemanalRepository
					.save(new FaturamentoSemanal(inicioDaSemanaAnterior, fimDaSemanaAnterior, valorFaturamento, qntPedidos));
		}	
		
		/*
		 * Optional<FaturamentoSemanal> faturamentoSemanaAtual =
		 * this.faturamentoSemanalRepository.findByDataInicial(inicio); if
		 * (faturamentoSemanaAtual.isEmpty()) { BigDecimal valorFaturamento =
		 * this.faturamentoRepository.sumFaturamentos(inicio, inicio); Long qntPedidos =
		 * this.faturamentoRepository.sumQntPedidos(inicio, fim);
		 * 
		 * this.faturamentoSemanalRepository .save(new FaturamentoSemanal(inicio, fim,
		 * valorFaturamento, qntPedidos)); }
		 */
		
	
	}
}
