package com.github.lvpasqualini.ms.pagamento.repositories;

import com.github.lvpasqualini.ms.pagamento.entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
