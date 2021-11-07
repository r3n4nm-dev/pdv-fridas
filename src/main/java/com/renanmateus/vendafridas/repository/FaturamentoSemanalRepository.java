package com.renanmateus.vendafridas.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.renanmateus.vendafridas.model.FaturamentoSemanal;

@Repository
public interface FaturamentoSemanalRepository  extends JpaRepository<FaturamentoSemanal, Long>{

	
	Optional<FaturamentoSemanal> findByDataInicial(LocalDate date);
	@Query(value = "SELECT f FROM FaturamentoSemanal f ORDER BY f.dataInicial DESC")
	List<FaturamentoSemanal> buscarCincoPrimeiros(PageRequest pageRequest);
	//List<FaturamentoSemanal> findTop5OrderByDataInicial();
	
	@Query(value = "SELECT f FROM FaturamentoSemanal f WHERE :data BETWEEN f.dataInicial AND f.dataFinal")
	Optional <FaturamentoSemanal> buscarFaturamentoSemanalPorIntervaloDeData(LocalDate data);

}
