package br.com.msansone.aiandreia.repository;

import br.com.msansone.aiandreia.entity.AiRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiRequestRepository extends JpaRepository<AiRequest, Long> {

    List<AiRequest> findByUserIdOrderByCreatedAtAsc(Long userId);

    void deleteByUserId(Long userId);
}
