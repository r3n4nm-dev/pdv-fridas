package com.renanmateus.vendafridas.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.renanmateus.vendafridas.model.Faturamento;
import com.renanmateus.vendafridas.model.FaturamentoMensal;
import com.renanmateus.vendafridas.model.FaturamentoSemanal;
import com.renanmateus.vendafridas.repository.FaturamentoSemanalRepository;
import com.renanmateus.vendafridas.service.FaturamentoService;

@Controller
@RequestMapping(value = "/faturamento")
public class FaturamentoController {

	@Autowired
	private FaturamentoService faturamentoService;
	
	@Autowired
	private FaturamentoSemanalRepository fsr;

	@GetMapping
	public String faturamento(ModelMap modelMap) {
		
		
	
		// SEMANA
		LocalDate inicioSemana = LocalDate.now(ZoneId.of("America/Sao_Paulo")).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
		LocalDate fimSemana = inicioSemana.plusDays(6);
		FaturamentoSemanal faturamentoSemanaAtual = this.faturamentoService.buscarFaturamentoSemanal(inicioSemana, fimSemana);
		FaturamentoSemanal faturamentoSemanaPassada = this.faturamentoService.buscarFaturamentoSemanal(inicioSemana.minusWeeks(1), fimSemana.minusWeeks(1));
		
		
		// MÊS
		LocalDate inicioMes = YearMonth.now().atDay(1);
		LocalDate fimMes   = YearMonth.now().atEndOfMonth();
		LocalDate fimMesPassado   = YearMonth.now().minusMonths(1).atEndOfMonth();
		FaturamentoMensal faturamentoMesAtual = this.faturamentoService.buscarFaturamentoMensal(inicioMes, fimMes);
		FaturamentoMensal faturamentoMesPassado = this.faturamentoService.buscarFaturamentoMensal(inicioMes.minusMonths(1), fimMesPassado);
		

		// DIA
		Faturamento faturamentoOntem = this.faturamentoService.buscarFaturamentoDiario(LocalDate.now().minusDays(1));
		Faturamento faturamentoHoje = this.faturamentoService.buscarFaturamentoDiario(LocalDate.now());
		
		
		List<FaturamentoSemanal> fs = this.fsr.buscarCincoPrimeiros(PageRequest.of(0,5));	
		modelMap.addAttribute("fs", fs);

		modelMap.addAttribute("fa", faturamentoSemanaAtual);
		modelMap.addAttribute("fp", faturamentoSemanaPassada);
		
		modelMap.addAttribute("faturamentoHoje", faturamentoHoje);
		modelMap.addAttribute("faturamentoOntem", faturamentoOntem);
		
		modelMap.addAttribute("ma", faturamentoMesAtual);
		modelMap.addAttribute("mp", faturamentoMesPassado);


		return "faturamento";
	}

}
