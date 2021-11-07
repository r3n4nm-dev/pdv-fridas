package com.renanmateus.vendafridas.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.renanmateus.vendafridas.model.FaturamentoMensal;

@Repository
public interface FaturamentoMensalRepository  extends JpaRepository<FaturamentoMensal, Long>{

	
	Optional<FaturamentoMensal> findByDataInicial(LocalDate date);
}
