/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.co.novaip.models;

import com.co.novaip.redis.RedisRepositoryImpl;
import com.co.novaip.service.QuestionService;
import com.co.novaip.util.Constantes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author desarrollo7
 */
@Component
public class ConversationalPattern {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(ConversationalPattern.class);

    @Autowired
    RedisRepositoryImpl redisRepositoryImpl;
    @Autowired
    QuestionService questionService;
    @Autowired
    RestTemplate restTemplate;

    public ConversationalPattern() {
    }

    public String processResponse(JSONObject jsonMessage, MensajeChat stateRedis) {
        String response = "";
        Boolean oneWay = null;
        List<Question> listQuestions = new ArrayList<Question>();
        if (stateRedis.getState().equals(Constantes.STATE_INITIAL_ZERO.toString()) && stateRedis.getSubState().equals(Constantes.SUBSTATE_INITIAL_ZERO.toString())) {
            StringBuilder opciones = new StringBuilder();

            if (questionService.findByParentIsNull().isPresent()) {

                listQuestions = questionService.findByParentIsNull().get();

                for (Question question : listQuestions) {
                    opciones.append(question.getPregunta()).append("\n");
                }
                stateRedis.setJsonFinal("{}");
                oneWay = listQuestions.get(0).getOneWay();
                response = opciones.toString();
                stateRedis.setSubState(Constantes.SUBSTATE_ONE.toString());
            } else {
                return Constantes.ERROR_QUESTIONS_DB;
            }
        } else {
            //settea arriba el state que llega, aqui tengo que hacer algo despues de que llega la respuesta 
            stateRedis = saveRedis(jsonMessage, stateRedis); //get("id").toString(), responseState.get("key").toString()
            if (Objects.isNull(stateRedis)) {
                response = Constantes.ERROR_QUESTIONS;
            } else {
                response = responseBySubState(jsonMessage, stateRedis);
                validateToSave(jsonMessage, stateRedis);
            }
        }
        
        //Un camino
        if (Objects.nonNull(oneWay) && oneWay && stateRedis.getSubState().equals(Constantes.SUBSTATE_ONE.toString())) {
            stateRedis = saveNewStateRedis(jsonMessage, stateRedis, listQuestions.get(0).getId().toString());
            response = response.concat(responseBySubState(jsonMessage, stateRedis));
        }
        //Dos caminos
        if (Objects.nonNull(oneWay) && !(oneWay) && stateRedis.getSubState().equals(Constantes.SUBSTATE_ONE.toString())){
//            saveNewStateRedisTW(jsonMessage, stateRedis, Constantes.SUBSTATE_ONE.toString());
        }
        log.info("response " + response.toString());
        return response;
    }

    public String responseBySubState(JSONObject jsonMessage, MensajeChat newStateRedis) {
        StringBuilder response = new StringBuilder();
        try {
            Optional<List<Question>> listQuestion = questionService.findByParent(Long.parseLong(newStateRedis.getSubState()));
            if (listQuestion.isPresent()) {
                for (Question question : listQuestion.get()) {
                    response.append(addName(question.getPregunta().toString(), newStateRedis.getName())).append("\n"); //////////////////
                }
            }
        } catch (Exception e) {
            log.info("Error, Function (responseBySubState) : >>>>>>>> " + e.getMessage());
            return "";
        }
        return response.toString();
    }
    
    public String addName(String pregunta, String name) {
        if(Objects.isNull(name)){
            name = "";
        }
        return pregunta.replaceAll("-----", name);
    }
    
    public String stateDefault(Integer stateNumber) {
        String response = "Dato incorrecto, por favor vuelva a digitar su opcion.";
        if (!stateNumber.equals(Constantes.STATE_INITIAL_ZERO)) {
            response = "Ha ocurrido un erro en nuestro sistema, en breve puede continuar el proceso.";
        }
        return response;
    }

    // Funciones para guardar en Redis (One way)
    public MensajeChat saveNewStateRedis(JSONObject jsonMessage, MensajeChat stateRedis, String subState) {

        try {
            stateRedis.setState(Constantes.STATE_ONE.toString());
            stateRedis.setSubState(subState); // Cambiar en caso tal de que se cambie la tabla
            stateRedis.setMapMessage(jsonMessage.toString());
            redisRepositoryImpl.update(stateRedis);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error, Function (saveNewStateRedis) : >>>>> " + e.getMessage());
        }
        return stateRedis;
    }
    
    // Funciones para guardar en Redis (Two ways)
    public MensajeChat saveNewStateRedisTW(JSONObject jsonMessage, MensajeChat stateRedis, String subState) {

        try {
            stateRedis.setState(Constantes.STATE_ONE.toString());
            stateRedis.setSubState(subState); // Cambiar en caso tal de que se cambie la tabla
            stateRedis.setMapMessage(jsonMessage.toString());
            redisRepositoryImpl.update(stateRedis);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error, Function (saveNewStateRedisTW) : >>>>> " + e.getMessage());
        }
        return stateRedis;
    }
    
    public MensajeChat setName(String key, MensajeChat stateRedis, String mensajeReceiver) {
        if(key.equals("name") || key.equals("message")){
            stateRedis.setName(mensajeReceiver);
        }
        return stateRedis;
    }

    public MensajeChat saveRedis(JSONObject jsonMessage, MensajeChat stateRedis) {
        JSONObject jsonSimpatizante = null;
        JSONObject respuestaPosible = null;
        String posibleResponse = "";
        String respuestaPosibleString = "";
        String key = "";
        String mensajeReceiver = jsonMessage.get("message[body][text]").toString();
        StringBuilder response = new StringBuilder();

        response.append(responseBySubState(jsonMessage, stateRedis));
        
        Optional<List<Question>> listQuestion = questionService.findByParent(Long.parseLong(stateRedis.getSubState()));

        if (listQuestion.isPresent()) {
            key = listQuestion.get().get(0).getKey() != null ? listQuestion.get().get(0).getKey().toString() : "";
            respuestaPosibleString = listQuestion.get().get(0).getRespuestaPosible() != null ? listQuestion.get().get(0).getRespuestaPosible().toString() : "";
        }

        if (respuestaPosibleString != "") {
            respuestaPosible = new JSONObject(respuestaPosibleString);
        }

        if (key != "" || key != null) {
            jsonSimpatizante = new JSONObject(stateRedis.getJsonFinal().toString());
            if (respuestaPosible != null) {
                posibleResponse = processPossibleResponse(respuestaPosible, mensajeReceiver);
                if (posibleResponse != "") {
                    log.info("posibleResponse " + posibleResponse.toString());
                    jsonSimpatizante.put(key, posibleResponse);
                    stateRedis = setName(key, stateRedis, mensajeReceiver);
                } else {
                    return null;
                }
            } else {
                jsonSimpatizante.put(key, mensajeReceiver);
                stateRedis = setName(key, stateRedis, mensajeReceiver);
            }
            stateRedis.setJsonFinal(jsonSimpatizante.toString());
            try {
                stateRedis.setSubState(listQuestion.get().get(0).getId().toString());
                stateRedis.setMapMessage(jsonMessage.toString());
                redisRepositoryImpl.update(stateRedis);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("Error, Function (saveRedis) : >>>>> " + e.getMessage());
            }
        }
        return stateRedis;
    }

    public String processPossibleResponse(JSONObject respuestaPosible, String respuesta) {

        String response = "";
        try {

            if (respuestaPosible.has(respuesta)) {
                response = respuestaPosible.getString(respuesta);
            }

        } catch (Exception e) {
            System.out.println("Error Function: (processPossibleResponse) >>>>>>> ConversationalPattern " + e.getMessage());
        }

        return response;
    }

    public void validateToSave(JSONObject respuesta, MensajeChat stateRedis) {

        Optional<List<Question>> listQuestion = questionService.findByParent(Long.parseLong(stateRedis.getSubState()));
        JSONObject jsonSimpatizante = new JSONObject(stateRedis.getJsonFinal().toString());
        String key = "";

        if (listQuestion.isPresent()) {
            key = listQuestion.get().get(0).getKey() != null ? listQuestion.get().get(0).getKey().toString() : "";
            if (key.equals("save")) {
                jsonSimpatizante.put("number", respuesta.get("contact[uid]"));
                jsonSimpatizante.put("empresaSlug", "novaip");
                log.info("jsonSimpatizante >>>>> pasa " + jsonSimpatizante.toString());
                sendToSave(jsonSimpatizante, listQuestion.get().get(0).getEndPoint().toString(), respuesta.get("contact[uid]").toString()); //restTemplate.put("192.168.0.92:9196/chatbot/json/guber/almacena_registro.php", stateRedis.getSimpatizante());
            }
        }
    }

    public void sendToSave(JSONObject respuestaPosible, String endPoint, String keyRedis) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Query parameters
        HttpEntity entity = new HttpEntity<>(respuestaPosible.toMap(), headers);
        ResponseEntity<String> strResponse = null;
        try {
            strResponse = restTemplate.exchange(endPoint, HttpMethod.POST, entity, String.class);
            redisRepositoryImpl.delete(keyRedis);
        } catch (Exception e) {
            System.out.println("Error Funtion: (sendToSave) >>>>>>> ConversationalPattern " + e.getMessage());
        }
    }

    public void sendMessageProcess() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        // Query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://192.168.0.92:9196/chatbot/json/guber/consulta_documento.php")
                // Add query parameter
                .queryParam("id_chatbot", "23")
                .queryParam("identificacion", "159487263");
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> strResponse = null;
        try {
            strResponse = restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.GET, entity, String.class);
        } catch (Exception e) {
            System.out.println("Error Funtion: (sendMessage) >>>>>>> WhatsAppController " + e.getMessage());
        }
        log.info("strResponse sendMessageProcess " + strResponse.getBody().toString());
    }

}
