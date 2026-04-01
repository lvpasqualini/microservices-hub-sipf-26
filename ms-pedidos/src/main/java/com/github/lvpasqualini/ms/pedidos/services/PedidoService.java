package com.github.lvpasqualini.ms.pedidos.services;

import com.github.lvpasqualini.ms.pedidos.controller.PedidoController;
import com.github.lvpasqualini.ms.pedidos.dto.ItemPedidoDTO;
import com.github.lvpasqualini.ms.pedidos.dto.PedidoDTO;
import com.github.lvpasqualini.ms.pedidos.entities.ItemDoPedido;
import com.github.lvpasqualini.ms.pedidos.entities.Pedido;
import com.github.lvpasqualini.ms.pedidos.entities.Status;
import com.github.lvpasqualini.ms.pedidos.repositories.ItemDoPedidoRepository;
import com.github.lvpasqualini.ms.pedidos.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.lvpasqualini.ms.pedidos.exceptions.*;

import java.time.LocalDate;
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

    @Transactional
    public PedidoDTO savePedido(PedidoDTO pedidoDTO) {
        Pedido pedido = new Pedido();
        pedido.setData(LocalDate.now());
        pedido.setStatus(Status.CRIADO);
        mapDtoToPedido(pedidoDTO,pedido);
        pedido.calcularValorTotalPedido();
        pedido = pedidoRepository.save(pedido);
        return new PedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO updatePedido(Long id, PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pedido com ID: " + id + " não encontrado")
        );
        pedido.getItens().clear();
        pedido.setData(LocalDate.now());
        pedido.setStatus(Status.CRIADO);
        mapDtoToPedido(pedidoDTO,pedido);
        pedido.calcularValorTotalPedido();
        pedido = pedidoRepository.save(pedido);
        return new PedidoDTO(pedido);
    }

    @Transactional
    public void deleteProduto(Long id) {
        if(!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido com ID: " + id + " não encontrado");
        }
        pedidoRepository.deleteById(id);
    }

    private void mapDtoToPedido(PedidoDTO pedidoDTO, Pedido pedido) {
        pedido.setNome(pedidoDTO.getNome());
        pedido.setCpf(pedidoDTO.getCpf());

        for (ItemPedidoDTO itemDTO : pedidoDTO.getItens()) {
            ItemDoPedido itemPedido = new ItemDoPedido();
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setDescricao(itemDTO.getDescricao());
            itemPedido.setPrecoUnitario(itemDTO.getPrecoUnitario());
            itemPedido.setPedido(pedido);
            pedido.getItens().add(itemPedido);
        }
    }
}
