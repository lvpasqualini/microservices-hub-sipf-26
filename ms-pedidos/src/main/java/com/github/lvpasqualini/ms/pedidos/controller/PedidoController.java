package com.github.lvpasqualini.ms.pedidos.controller;

import com.github.lvpasqualini.ms.pedidos.dto.PedidoDTO;
import com.github.lvpasqualini.ms.pedidos.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> findAll() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> findById(@PathVariable Long id) {
        PedidoDTO pedidoDTO = pedidoService.findById(id);
        return ResponseEntity.ok(pedidoDTO);
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> createPedido(@Valid @RequestBody PedidoDTO pedidoDTO) {
        pedidoDTO = pedidoService.savePedido(pedidoDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(pedidoDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(pedidoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> updatePedido(@Valid @PathVariable Long id, @RequestBody PedidoDTO pedidoDTO) {
        PedidoDTO dto = pedidoService.updatePedido(id,pedidoDTO);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        pedidoService.deleteProduto(id);
        return ResponseEntity.noContent().build();
    }
}
