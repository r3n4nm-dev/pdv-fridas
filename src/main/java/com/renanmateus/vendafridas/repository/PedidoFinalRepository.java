package com.renanmateus.vendafridas.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.renanmateus.vendafridas.model.PedidoFinal;

@Repository
public interface PedidoFinalRepository extends JpaRepository<PedidoFinal, Long> {
	

}
