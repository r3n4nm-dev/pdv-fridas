package com.renanmateus.vendafridas.repository;

import com.renanmateus.vendafridas.model.Estado;
import com.renanmateus.vendafridas.model.Pedido;
import com.renanmateus.vendafridas.model.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
    PedidoItem findFirstByPedidoPedidoIdAndItemItemIdAndIsPago(Long pedidoId, Long itemId, boolean isPago);

    // Pagar Tudo
    @Transactional
    @Modifying
    @Query("UPDATE PedidoItem pi SET pi.isPago = :isPago, pi.isCardPayment = :isCardPayment WHERE pi.pedido = :pedido")
    void updatePedidoItemIsPagoByPedidoItemPedido(boolean isPago, boolean isCardPayment, Pedido pedido);

     // Pagar item
    @Transactional
    @Modifying
    @Query("UPDATE PedidoItem pi SET pi.isPago = :isPago, pi.isCardPayment = :isCardPayment WHERE pi.id = :id")
    void updatePedidoItemIsPagoAndIsCardPaymentById(boolean isPago, boolean isCardPayment, Long id);

    List<PedidoItem> findByIsPagoAndPedidoEstado(boolean isPago, Estado estado);
    @Transactional
    @Modifying
    @Query("Delete PedidoItem pi WHERE pi.pedido.pedidoId = :pedidoId")
    void deleteByPedidoPedidoId(Long pedidoId);

    @Query("SELECT sum(pi.item.preco) FROM PedidoItem pi WHERE pi.isPago = :isPago AND pi.isCardPayment = :isCardPayment AND pi.dataCriacao BETWEEN :data1 and :data2")
    BigDecimal somarValorItensPagosEmDinheiroOuCartao(boolean isPago, boolean isCardPayment,  LocalDateTime data1, LocalDateTime data2);
}