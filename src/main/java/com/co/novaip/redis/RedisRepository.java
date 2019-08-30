/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.co.novaip.redis;

import com.co.novaip.models.MensajeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author desarrollo7
 */
@Repository
public interface RedisRepository extends JpaRepository<MensajeChat, String>{
    
}
