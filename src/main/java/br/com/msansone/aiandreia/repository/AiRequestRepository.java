package br.com.msansone.aiandreia.repository;

import br.com.msansone.aiandreia.entity.AiRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRequestRepository extends JpaRepository<AiRequest, Long> {
}
