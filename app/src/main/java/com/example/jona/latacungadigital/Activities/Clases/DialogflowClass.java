package com.example.jona.latacungadigital.Activities.Clases;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.example.jona.latacungadigital.Activities.Adapters.MessagesAdapter;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class DialogflowClass {

    // Declaración de variables para interactuar con Dialogflow.
    private AIDataService aiDataService;
    private static final String ACCESS_CLIENT_TOKEN = "70cabdb5e2594ff0b81f86520564fbd6";

    // Declaracion de variables para saber que tipo de vista es.
    private static final int VIEW_TYPE_MESSAGE_USER = 1;
    private static final int VIEW_TYPE_MESSAGE_CHATBOT = 2;
    private static final int VIEW_TYPE_MESSAGE_CHATBOT_TYPING = 3;
    private static final int VIEW_TYPE_MESSAGE_ATTRACTIVE_CHATBOT = 4;
    private static final int VIEW_TYPE_MESSAGE_CARD_VIEW_MAP = 5;

    // Declaración de variables para inicializar este modelo.
    private View view;
    private List<TextMessageModel> listMessagesText;
    private List<MessageCardMapListItemView> listMessageCardMapView;
    private RecyclerView rvListMessages;
    private MessagesAdapter messagesAdapter;
    private EditText txtMessageUserSend;

    public DialogflowClass(View view, List<TextMessageModel> listMessagesText, RecyclerView rvListMessages, MessagesAdapter messagesAdapter, EditText txtMessageUserSend) {
        this.view = view;
        this.listMessagesText = listMessagesText;
        this.rvListMessages = rvListMessages;
        this.messagesAdapter = messagesAdapter;
        this.txtMessageUserSend = txtMessageUserSend;
    }

    // Metodo de configuración para conectarse con Dialogflow.
    public void ConfigurationDialogflow() {
        final AIConfiguration configurationAI = new AIConfiguration(ACCESS_CLIENT_TOKEN,
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        // Agregar la configuración para conectarse con Dialogflow
        aiDataService = new AIDataService(configurationAI);
    }

    // Método para enviar el mensaje a Dialogflow.
    private void SendMessageTextToDialogflow(final String message) {
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(message); // Enviamos la pregunta del usuario a Dialogflow.

        new AsyncTask<AIRequest, Void, AIResponse>() {

            // Método que se ejecuta antes de que comience el proceso doInBackground().
            @Override
            protected void onPreExecute() {
                MessageTypingToDialogflow();
            }

            /* Método que se ejecutara despues de onPreExecute(). Este método recibe los parámetros de entrada para ejecutar las instrucciones
               especificas que irán en segundo plano. */
            @Override
            protected AIResponse doInBackground(AIRequest... aiRequests) {
                final AIRequest request = aiRequests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                    e.printStackTrace();
                }
                return null;
            }

            // Método que se ejecutara cuando finalize el metodo doInBackground() y pasamos como parametro el resultado que retorna el mismo.
            @Override
            protected void onPostExecute(AIResponse response) {
                if (response != null) {
                    RemoveMessageTypingToDialogflow();
                    ResponseToDialogflow(response);
                }
            }
        }.execute(aiRequest);
    }

    // Método para posicionar el último elemento del Recycle View.
    private void setScrollbarChat() {
        rvListMessages.scrollToPosition(messagesAdapter.getItemCount() - 1);
    }

    // Método para crear un nuevo mensaje del usuario y del ChatBot.
    public void CreateMessage(String message) {
        if (!message.equals("")) { // Se valida el mensaje que sea diferente de nulo para que no envie un texto vació a Dialogflow ni al chat.
            TextMessageModel textMessageModel = new TextMessageModel(message); // Inicializamos la clase con el mensaje y quien envia el mensaje.
            textMessageModel.setViewTypeMessage(VIEW_TYPE_MESSAGE_USER);
            listMessagesText.add(textMessageModel);
        }

        SendMessageTextToDialogflow(message); // Para enviar un mensaje a Dialogflow y para recibir el mensaje.
        txtMessageUserSend.setText(""); // Para limpiar el texto al momento de que envia un mensaje el usuario.
        messagesAdapter.notifyDataSetChanged();
        setScrollbarChat();
    }

    // Método para enviar la respuesta
    private void ResponseToDialogflow(AIResponse response) {
        if (response != null) {
            Result result = response.getResult();

            String action = result.getAction(); // Variable para reconocer la acción según la pregunta del usuario.

            if(action.equals("weatherAction")) { // Accion cuando es del clima

                final WeatherClass weatherModel = new WeatherClass(view);
                weatherModel.CurrentWeather(new WeatherClass.WeatherCallback() {
                    @Override
                    public void getResponseWeather(String response) {
                        MessageSendToDialogflow(response);
                    }
                });
            } else if (action.equals("churchInformationAction")) { // Accion cuando es una consulta de un atractivo turistico
                AttractiveClass attractiveClass = new AttractiveClass();
                CardDialogflow(attractiveClass, result);

            } else if (action.equals("consultarAlojamientoEnElArea")) { // Accion cuando es una consulta sobre servicios de alojamiento cercanos
                sendServicesToMapMessage(result);
            } else {

                String speech = result.getFulfillment().getSpeech();
                MessageSendToDialogflow(speech);
            }
        }
    }

    private void RemoveMessageTypingToDialogflow() {
        listMessagesText.remove(listMessagesText.size() - 1);
        addMessagesAdapter(listMessagesText);
    }

    // Método para enviar la respuesta al usuario.
    private void MessageSendToDialogflow(String message) {
        TextMessageModel textMessageModel = new TextMessageModel(message);
        textMessageModel.setViewTypeMessage(VIEW_TYPE_MESSAGE_CHATBOT);
        listMessagesText.add(textMessageModel);

        addMessagesAdapter(listMessagesText);
    }

    // Método para que el usuario sepa que el chatbot esta escribiendo el mensaje.
    private void MessageTypingToDialogflow() {
        TextMessageModel textMessageModel = new TextMessageModel();
        textMessageModel.setViewTypeMessage(VIEW_TYPE_MESSAGE_CHATBOT_TYPING);
        listMessagesText.add(textMessageModel);

        addMessagesAdapter(listMessagesText);
    }

    // Método para enviar la respuesta de la informacion de los atractivos turisticos al usuario.
    private void CardDialogflow(AttractiveClass attractiveModel, Result result) {
        TextMessageModel textMessageModel = new TextMessageModel();
        attractiveModel.readJSONDialogflow(result);

        if (attractiveModel.getState()) { // Para saber si el JSON no esta vacio.
            // Asignamos los valores leidos del JSON que envia Dialogflow y los asignamos a las varibales del Modelo TextMessageModel.
            textMessageModel.setViewTypeMessage(VIEW_TYPE_MESSAGE_ATTRACTIVE_CHATBOT);
            textMessageModel.setNameAttractive(attractiveModel.getNameAttractive());
            textMessageModel.setCategoryAttactive(attractiveModel.getCategory());
            textMessageModel.setDescriptionAttractive(attractiveModel.getDescription());
            textMessageModel.setListImagesURL(attractiveModel.getListImages());
            listMessagesText.add(textMessageModel);
            addMessagesAdapter(listMessagesText);
        } else { // Si el JSON esta vacio enviamos la respuesta por defecto de Dialogflow.
            String speech = result.getFulfillment().getSpeech();
            MessageSendToDialogflow(speech);
        }
    }

    // Método para enviar la respuesta del fullfiltment de Dialogflow en mensaje del tipo Mapa
    private void sendServicesToMapMessage(Result result){
        Map<String, JsonElement> JSONDialogflowResult = result.getFulfillment().getData(); // Obtenemos el nodo Data del Json
        ArrayList<ServiceClass> listService =  new ArrayList<ServiceClass>(); // Lista de servicios
        TextMessageModel textMessageModel = new TextMessageModel();
        // Recorremos el resultado obtenido de dialogflow
        Set mapDialogFlowResult = JSONDialogflowResult.entrySet();
        Iterator iterator = mapDialogFlowResult.iterator();
        while(iterator.hasNext()){
            ServiceClass tempService = new ServiceClass();
            Map.Entry mapService = (Map.Entry) iterator.next(); // Obtenemos el servicio dentro del JSon del mapa
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            String key = mapService.getKey().toString(); // Obtenemos la clave del servicio
            JsonElement values = jsonParser.parse(gson.toJson(mapService.getValue())); // Obtenemos los valores del servicio
            tempService.readJSONDialogflow(key,values); // Asignamos los valores del Json al objeto servicio
            listService.add(tempService); // Añadimo el objeto servicio a la lista
        }
        if(!listService.isEmpty()){
            // Asignamos los valores leidos del JSON que envia Dialogflow y los asignamos a las varibales del Modelo TextMessageModel.
            textMessageModel.setViewTypeMessage(VIEW_TYPE_MESSAGE_CARD_VIEW_MAP);
            textMessageModel.setListService(listService);
            listMessagesText.add(textMessageModel);
            addMessagesAdapter(listMessagesText);
        }
    }

    // Método para adaptar la lista de mensajes a la clase MessagesAdapter.
    private void addMessagesAdapter(List<TextMessageModel> listMessages) {
        MessagesAdapter messagesAdapter = new MessagesAdapter(listMessages, this.messagesAdapter.getListMessageCardMapView(),view.getContext());
        rvListMessages.setAdapter(messagesAdapter);
        messagesAdapter.notifyDataSetChanged();
        setScrollbarChat();
    }
}
