package com.github.lvpasqualini.ms.pedidos.repositories;

import com.github.lvpasqualini.ms.pedidos.entities.ItemDoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDoPedidoRepository extends JpaRepository<ItemDoPedido,Long> {
}
