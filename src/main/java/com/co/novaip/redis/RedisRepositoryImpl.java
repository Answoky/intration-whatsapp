/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.co.novaip.redis;

import com.co.novaip.models.MensajeChat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author desarrollo7
 */
@Service
public class RedisRepositoryImpl {

    private static final String KEY = "MessageChat";

    private ObjectMapper objectMapper = new ObjectMapper();
    private RedisTemplate<String, String> redisTemplate;
    private HashOperations hashOperations;

    @Autowired
    public RedisRepositoryImpl(RedisTemplate<String, String> redisTemplates) {
        redisTemplate = redisTemplates;
        hashOperations = redisTemplate.opsForHash();
    }

    public void save(MensajeChat chatMensaje) {
        try {
            hashOperations.put(KEY, chatMensaje.getId(), objectMapper.writeValueAsString(chatMensaje));
            redisTemplate.expire(KEY, 60, TimeUnit.MINUTES);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(RedisRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List findAll() {
        return hashOperations.values(KEY);
    }

    public MensajeChat findById(String id) {

        MensajeChat mensajeChat = new MensajeChat();
        try {
            if (hashOperations.get(KEY, id) != null) {
                mensajeChat = objectMapper.readValue(hashOperations.get(KEY, id).toString(), MensajeChat.class);
            }
        } catch (IOException ex) {
            Logger.getLogger(RedisRepositoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mensajeChat;
    }

    public void update(MensajeChat chatMensaje) {
        save(chatMensaje);
    }

    public void delete(String id) {
        hashOperations.delete(KEY, id);
    }
}
