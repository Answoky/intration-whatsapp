/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.co.novaip.models;

import java.io.Serializable;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisHash;

/**
 *
 * @author desarrollo7
 */
@RedisHash("MessageChat")
public class MensajeChat implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private String id;
    private String mapMessage;
    private String name;
    private String state;
    private String subState;
    private String jsonFinal;

    public MensajeChat() {
    }
    
    public MensajeChat(String id, String mapMessage, String name, String stage, String subState, String jsonFinal){
        this.id=id;
        this.name=name;
        this.mapMessage=mapMessage;
        this.state=stage;
        this.subState=subState;
        this.jsonFinal=jsonFinal;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getMapMessage() {
        return mapMessage;
    }

    public void setMapMessage(String mapMessage) {
        this.mapMessage = mapMessage;
    }
    
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSubState() {
        return subState;
    }

    public void setSubState(String subState) {
        this.subState = subState;
    }

    public String getJsonFinal() {
        return jsonFinal;
    }

    public void setJsonFinal(String jsonFinal) {
        this.jsonFinal = jsonFinal;
    }

    @Override
    public String toString() {
        return "MensajeChat{" + "id=" + id + ", mapMessage=" + mapMessage + ", name=" + name + ", state=" + state + ", subState=" + subState + ", jsonFinal=" + jsonFinal + '}';
    }

}
