package com.co.novaip.service;

import com.co.novaip.models.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import com.co.novaip.repository.QuestionRepository;
import java.util.List;

/**
 * Service Implementation for managing Question.
 */
@Service
@Transactional
public class QuestionService {

    private final Logger log = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionRepository questionsRepository;

    /**
     * Save a Question.
     *
     * @param Question the entity to save
     * @return the persisted entity
     */
    public Question save(Question Question) {
        log.info("Request to save Question : {}", Question);
            Question result = questionsRepository.save(Question);
            return result;
    }

    /**
     * Get all Vehicles.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Question> findAll(Pageable pageable) {
        log.debug("Request to get all Vehicles");
        return questionsRepository.findAllByEliminadoEquals(false, pageable);
    }

    /**
     * Get one Question by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<Question> findOne(Long id) {
        log.debug("Request to get Question : {}", id);
        return questionsRepository.findOneByIdAndEliminadoIsFalse(id);
    }
    
    /**
     * Get one Question by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<List<Question>> findByParentIsNull() {
        log.debug("Request to get Question By Parent Null : {}");
        return questionsRepository.findByParentIsNull();
    }
    
    /**
     * Get one Question by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<List<Question>> findByParent(Long id) {
        log.debug("Request to get Question By Parend ID : {}", id);
        return questionsRepository.findByParent(id);
    }

    /**
     * Delete the Question by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Question : {}", id);

        Optional<Question> Question = this.findOne(id);
        if (Question.isPresent()) {
            Question.get().setEliminado(Boolean.TRUE);
            questionsRepository.save(Question.get());
        }
    }

}
