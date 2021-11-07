package com.renanmateus.vendafridas.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.renanmateus.vendafridas.model.Faturamento;

@Repository
public interface FaturamentoRepository  extends JpaRepository<Faturamento, Long>{

	Optional<Faturamento> findByDataFaturamento(LocalDate data);
	
	@Query("SELECT COALESCE(sum(f.valorFaturamento),0) FROM Faturamento f WHERE f.dataFaturamento BETWEEN :data1 and :data2")
	BigDecimal sumFaturamentos(LocalDate data1, LocalDate data2);
	
	

	@Query("SELECT COALESCE(sum(f.quantidadePedido),0) FROM Faturamento f WHERE f.dataFaturamento BETWEEN :data1 and :data2")
	long sumQntPedidos(LocalDate data1, LocalDate data2);
	
	List<Faturamento> findByDataFaturamentoBetween(LocalDate data1, LocalDate data2);
}
