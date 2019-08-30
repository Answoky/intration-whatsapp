/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.co.novaip.resources;

import com.co.novaip.models.MensajeChat;
import com.co.novaip.models.ConversationalPattern;
import com.co.novaip.models.Question;
import com.co.novaip.redis.RedisRepository;
import com.co.novaip.redis.RedisRepositoryImpl;
import com.co.novaip.service.QuestionService;
import com.co.novaip.util.Constantes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author desarrollo7
 */
@RestController
@RequestMapping("/whatsapp")
public class WhatsAppController {

    private final String API_KEY = "";
    private final Logger log = LoggerFactory.getLogger(WhatsAppController.class);

    /**
     *
     * @return the ResponseEntity with status 200 (OK) and with body the
     * subpoena, or with status 404 (Not Found)
     */
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    RedisRepositoryImpl redisRepositoryImpl;
    @Autowired
    QuestionService questionService;
    @Autowired
    ConversationalPattern conversational;

    @PostMapping("/recibir_message/{numberReceiber}")
    public ResponseEntity<String> recibirMessage(
            @PathVariable(name = "numberReceiber") String numberReceiber,
            @RequestParam(name = "event", required = false) String event,
            @RequestParam(name = "token", required = false) String token,
            @RequestParam(name = "contact[uid]", required = false) String contact_uid,
            @RequestParam(name = "contact[name]", required = false) String contact_name,
            @RequestParam(name = "contact[type]", required = false) String contact_type,
            @RequestParam(name = "message[dtm]", required = false) String message_dtm,
            @RequestParam(name = "message[uid]", required = false) String message_uid,
            @RequestParam(name = "message[cuid]", required = false) String message_cuid,
            @RequestParam(name = "message[dir]", required = false) String message_dir,
            @RequestParam(name = "message[type]", required = false) String message_type,
            @RequestParam(name = "message[body][text]", required = false) String message_body,
            @RequestParam(name = "message[body][url]", required = false) String message_body_url,
            @RequestParam(name = "message[ack]", required = false) String ack
    ) {
        if (Integer.parseInt(ack) != Constantes.CALL_TWO) {
            Map<String, String> mapMensaje = processMap(numberReceiber, event, token, contact_uid, contact_name, contact_type, message_dtm, message_uid, message_cuid, message_dir, message_type, message_body, message_body_url, ack);
            JSONObject jsonMessageReceiber = new JSONObject(mapMensaje);
            existState(mapMensaje.get("contact[uid]"), jsonMessageReceiber);
        }
        return ResponseEntity.ok().body("ola parola");
    }

    @GetMapping("/enviar_message")
    public String enviarMessageNovaIP(String message, String contact_uid) {
        String uid = "573504267676";
        return "Sirve ";
    }

    @GetMapping("/probar_parents")
    public List<Question> parentsNull() {
        Optional<List<Question>> questions = questionService.findByParentIsNull();
        return questions.get();
    }

    @GetMapping("/probar_parents/{state}")
    public List<Question> parentsNull(@PathVariable(name = "state") Long state) {
        Optional<List<Question>> questions = questionService.findByParent(state);
        return questions.get();
    }

    public Map processMap(String numberReceiber, String event, String token, String contact_uid, String contact_name, String contact_type, String message_dtm, String message_uid, String message_cuid, String message_dir, String message_type, String message_body, String message_body_url, String ack) {
        Map<String, String> mensaje = new HashMap<String, String>();
        mensaje.put("event", event);
        mensaje.put("token", token);
        mensaje.put("numberReceiber", numberReceiber);
        mensaje.put("contact[uid]", contact_uid);
        mensaje.put("contact[name]", contact_name);
        mensaje.put("contact[type]", contact_type);
        mensaje.put("message[dtm]", message_dtm);
        mensaje.put("message[uid]", message_uid);
        mensaje.put("message[cuid]", message_cuid);
        mensaje.put("message[dir]", message_dir);
        mensaje.put("message[type]", message_type);
        mensaje.put("message[body][text]", message_body);
        mensaje.put("message[body][url]", message_body_url);
        mensaje.put("message[ack]", ack);
        return mensaje;

    }

    public void existState(String contact_uid, JSONObject jsonMessageReceiber) {
        MensajeChat message = null;
        String response = null;
        if (redisRepositoryImpl.findById(contact_uid).getId() != null) {
            message = redisRepositoryImpl.findById(contact_uid);
        }
        log.info("message " + message);
        if (Objects.isNull(message)) {
            //Inicia sesion en state 0, y subState 0
            
            message = initSesion(contact_uid, jsonMessageReceiber);
            response = conversational.processResponse(jsonMessageReceiber, message);
            sendMessageProcess(response, jsonMessageReceiber.get("numberReceiber").toString(), jsonMessageReceiber.get("contact[uid]").toString());
        } else {

            if (!(message.getSubState().equals(Constantes.SUBSTATE_INITIAL_ZERO.toString()))) {
//                response = Constantes.ERROR_QUESTIONS_DB;
//                sendMessageProcess(response, jsonMessageReceiber.get("numberReceiber").toString(), jsonMessageReceiber.get("contact[uid]").toString());
                response = conversational.processResponse(jsonMessageReceiber, message);
                sendMessageProcess(response, jsonMessageReceiber.get("numberReceiber").toString(), jsonMessageReceiber.get("contact[uid]").toString());
            } 
       }
    }

    public MensajeChat initSesion(String contact_uid, JSONObject jsonMessageReceiber) {
        MensajeChat message = new MensajeChat();
        message.setId(jsonMessageReceiber.get("contact[uid]").toString());
        message.setState(Constantes.STATE_INITIAL_ZERO.toString());
        message.setSubState(Constantes.SUBSTATE_INITIAL_ZERO.toString());
        message.setMapMessage(jsonMessageReceiber.toString());
        redisRepositoryImpl.save(message);

        return message;
    }

    public Boolean sendMessageProcess(String message, String uid, String to) {
        Boolean status = Boolean.FALSE;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        // Query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(Constantes.URL_SEND_CHAT)
                // Add query parameter
                .queryParam("token", API_KEY)
                .queryParam("uid", uid)
                .queryParam("to", to)
                .queryParam("custom_uid", UUID.randomUUID().toString())
                .queryParam("text", message);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<String> strResponse = null;
        try {
            strResponse = restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.GET, entity, String.class);
            status = Boolean.TRUE;
        } catch (Exception e) {
            System.out.println("Error Funtion: (sendMessage) >>>>>>> WhatsAppController " + e.getMessage());
        }
        return status;
    }

}
