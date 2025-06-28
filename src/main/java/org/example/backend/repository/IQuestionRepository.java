package org.example.backend.repository;

import org.example.backend.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IQuestionRepository extends JpaRepository<Question,Integer> {
    Page<Question> findAllByIsActiveTrue(Pageable pageable);
    boolean existsByContent(String content);
}
