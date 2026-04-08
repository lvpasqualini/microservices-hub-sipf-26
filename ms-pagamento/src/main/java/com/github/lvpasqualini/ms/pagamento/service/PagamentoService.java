package com.github.lvpasqualini.ms.pagamento.service;

import com.github.lvpasqualini.ms.pagamento.dto.PagamentoDTO;
import com.github.lvpasqualini.ms.pagamento.entities.Pagamento;
import com.github.lvpasqualini.ms.pagamento.entities.Status;
import com.github.lvpasqualini.ms.pagamento.exceptions.ResourceNotFoundException;
import com.github.lvpasqualini.ms.pagamento.repositories.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagamentoService {
    @Autowired
    private PagamentoRepository repository;

    @Transactional(readOnly = true)
    public List<PagamentoDTO> findAll() {
        return repository.findAll().stream().map(PagamentoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagamentoDTO findById(Long id) {
        Pagamento pagamento = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado com o ID: " +id)
        );
        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO save(PagamentoDTO pagamentoDTO) {
        Pagamento pagamento = new Pagamento();
        mapperDtoToPagamento(pagamentoDTO,pagamento);
        pagamento.setStatus(Status.CRIADO);
        pagamento = repository.save(pagamento);
        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO update(Long id, PagamentoDTO pagamentoDTO) {
        try {
            Pagamento pagamento = repository.getReferenceById(id);
            mapperDtoToPagamento(pagamentoDTO,pagamento);
            pagamento.setStatus(pagamentoDTO.getStatus());
            pagamento = repository.save(pagamento);
            return new PagamentoDTO(pagamento);
        }catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado com o ID: " + id);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        if(!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado com o ID: "+ id);
        }
        repository.deleteById(id);
    }


    private void mapperDtoToPagamento(PagamentoDTO pagamentoDTO, Pagamento pagamento) {
        pagamento.setValor(pagamentoDTO.getValor());
        pagamento.setNome(pagamentoDTO.getNome());
        pagamento.setNumeroCartao(pagamentoDTO.getNumeroCartao());
        pagamento.setValidade(pagamentoDTO.getValidade());
        pagamento.setCodigoSeguranca(pagamentoDTO.getCodigoSeguranca());
        pagamento.setPedidoId(pagamentoDTO.getPedidoId());
    }
}
