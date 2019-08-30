package com.co.novaip.repository;

import com.co.novaip.models.Question;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    void deleteById(Long Id);

    Page<Question> findAllByEliminadoEquals(Boolean eliminado, Pageable pageable);

    Optional<Question> findOneByIdAndEliminadoIsFalse(Long id);
    
    @Query("SELECT q FROM Question q WHERE q.eliminado = false and q.parent is null ORDER BY q.id ASC")
    Optional<List<Question>> findByParentIsNull();
    
    @Query("SELECT q FROM Question q WHERE q.eliminado = false and q.parent = ?1 ORDER BY q.id ASC")
    Optional<List<Question>> findByParent(Long id);
    
}
