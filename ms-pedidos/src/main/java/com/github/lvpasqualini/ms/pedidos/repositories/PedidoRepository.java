package com.github.lvpasqualini.ms.pedidos.repositories;

import com.github.lvpasqualini.ms.pedidos.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido,Long> {
}
