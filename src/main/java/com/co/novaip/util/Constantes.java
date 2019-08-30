package com.co.novaip.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class Constantes {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String WELCOME_MESSAGE_FIRST = "Gracias por escribirnos al sistema de comunicaciones por whatsapp de la campana de ..., a continuación se harán las siguientes preguntas para realizar para realizar la inscripción: \n\n";
    public static final String WELCOME_MESSAGE_FIRST_OPTIONS = "Por favor, marque una opción para continuar:  \n\n";
    public static final String WELCOME_MESSAGE_SECOND = "Gracias por escribirnos al sistema de comunicaciones por WhatsApp de la campaña de ";
    public static final String ERROR_QUESTIONS = "Por favor, vuelva a ingresar una opcion valida.";
    public static final String ERROR_QUESTIONS_DB = "Por favor, revise la base de datos, preguntas y demas.";
    public static final String SELECT_OPTION = "Por favor escriba una respuesta correcta.";
    
    // State 
    public static final Integer CALL_TWO = -1;
    public static final Integer STATE_INITIAL_ZERO = 0;
    public static final Integer STATE_ONE = 1;
    public static final Integer STATE_TWO = 2;
    public static final Integer STATE_THIRD = 3;
    public static final Integer STATE_FOUR = 4;
    public static final Integer STATE_FIFTY = 5;
    public static final Integer STATE_SIX = 6;
    
    // SubState
    public static final Integer SUBSTATE_INITIAL_ZERO = 0;
    public static final Integer SUBSTATE_ONE = 1;
    public static final Integer SUBSTATE_TWO = 2;
    public static final Integer SUBSTATE_THIRD = 3;
    public static final Integer SUBSTATE_FOUR = 4;
    public static final Integer SUBSTATE_FIFTY = 5;
    public static final Integer SUBSTATE_SIX = 6;
    
    // URL
    public static final String URL_SEND_CHAT = "https://www.waboxapp.com/api/send/chat";
    
    
    


}
