package com.renanmateus.vendafridas.repository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.renanmateus.vendafridas.model.Estado;
import com.renanmateus.vendafridas.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	
	//Página de pedido abertos e fechados
	List<Pedido> findByEstadoOrderByPedidoIdDesc(Estado estado);
	
	@Query("SELECT p FROM Pedido p WHERE p.estado = '1' and p.horaPedido BETWEEN :data1 and :data2 order by p.pedidoId DESC")
	List<Pedido> buscarPedidosFechadosPorDia(LocalDateTime data1, LocalDateTime data2);
	
	
	@Query("SELECT sum(p.valorFinal) FROM Pedido p WHERE p.horaPedido BETWEEN :data1 and :data2")
	BigDecimal sumPedidos(LocalDateTime data1, LocalDateTime data2);
	
	@Query("SELECT count(p.pedidoId) FROM Pedido p WHERE p.estado = '0' or p.estado = '1' and p.horaPedido BETWEEN :data1 and :data2")
	long countHoraPedido(LocalDateTime data1, LocalDateTime data2);



}
