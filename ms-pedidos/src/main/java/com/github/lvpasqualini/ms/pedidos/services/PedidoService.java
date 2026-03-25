package com.github.lvpasqualini.ms.pedidos.services;

import com.github.lvpasqualini.ms.pedidos.dto.PedidoDTO;
import com.github.lvpasqualini.ms.pedidos.entities.Pedido;
import com.github.lvpasqualini.ms.pedidos.repositories.ItemDoPedidoRepository;
import com.github.lvpasqualini.ms.pedidos.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.lvpasqualini.ms.pedidos.exceptions.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemDoPedidoRepository itemDoPedidoRepository;

    @Transactional(readOnly = true)
    public List<PedidoDTO> findAll() {
        return pedidoRepository.findAll().stream()
                .map(PedidoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoDTO findById(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado com ID: " + id)
        );
        return new PedidoDTO(pedido);
    }
}
