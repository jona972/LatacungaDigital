package com.example.jona.latacungadigital.Activities.Views;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.Adapters.MessagesAdapter;
import com.example.jona.latacungadigital.Activities.Adapters.MyOnInfoWindowsClickListener;
import com.example.jona.latacungadigital.Activities.ChatBotActivity;
import com.example.jona.latacungadigital.Activities.Clases.AttractiveClass;
import com.example.jona.latacungadigital.Activities.Clases.ServiceClass;
import com.example.jona.latacungadigital.Activities.Fragments.MapaFragment;
import com.example.jona.latacungadigital.Activities.Permisos.EstadoGPS;
import com.example.jona.latacungadigital.Activities.modelos.TextMessageModel;
import com.example.jona.latacungadigital.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MessageCardMapListItemView extends LinearLayout implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    // Variables de la clase
    private MessagesAdapter messagesAdapter;
    private TextMessageModel message;
    Context context;

    // Variables para editar el mapa
    ArrayList<ServiceClass> listService;
    ArrayList<AttractiveClass> listAttractive;
    AttractiveClass attractive;
    ServiceClass service;
    GoogleMap gMap;
    LatLng currentUserLatLng;
    LatLng destinationLatLng;

    // Varaibles de acuerdo a los componentes que comprenden el layout: message_cv_map.xml
    protected TextView txtTitle;
    protected MapView mapView;

    // Constructores de la clase
    public MessageCardMapListItemView(@NonNull Context context) {
        super(context, null);
        setView(context);
    }

    public MessageCardMapListItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setView(context);
    }

    // Instanciar y dar valores a los componetes de la vista
    private void setView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.message_cv_map, this);
        txtTitle = view.findViewById(R.id.txtTitle);
        mapView = view.findViewById(R.id.mapView);
        this.context = context;
    }

    private void setValues() {
        if(message != null){
            switch (message.getAction()){
                case "consultarAtractivoEnElArea":
                    listAttractive = message.getListAttractive();
                    break;
                case "consultarAgenciasDeViajeEnElArea":
                case "consultarAlojamientoEnElArea":
                case "consultarComidaYBebidaEnElArea":
                case "consultarRecreacionDiversionEsparcimientoEnElArea":
                    listService = message.getListService();
                    break;
                case "church_information_intent.church_information_intent-yes":
                    attractive = message.getAttractive();
                    destinationLatLng = new LatLng(attractive.getLatitude(), attractive.getLongitude());
                    break;
                case "hotel_information_intent.hotel_information_intent-yes":
                    service = message.getService();
                    destinationLatLng = new LatLng(service.getLatitude(), service.getLongitude());
                    break;
            }
            txtTitle.setText(message.getTitulo());
            mapView.getMapAsync(this);
        }
    }

    public void setMessage(TextMessageModel message) {
        this.message = message;
        setValues();
    }

    public void setMessagesAdapter(MessagesAdapter messagesAdapter) {
        this.messagesAdapter = messagesAdapter;
    }

    //Metodos de la variable mapView
    public void mapViewOnCreate(Bundle savedInstanceState) {
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    public void mapViewOnResume() {
        if (mapView != null) {
            mapView.onResume();
        }
    }

    public void mapViewOnStart() {
        if (mapView != null) {
            mapView.onStart();
        }
    }

    public void mapViewOnStop() {
        if (mapView != null) {
            mapView.onStop();
        }
    }

    public void mapViewOnPause() {
        if (mapView != null) {
            mapView.onPause();
        }
    }

    public void mapViewOnDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    public void mapViewOnLowMemory() {
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    public void mapViewOnSaveInstanceState(Bundle outState){
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    // Crear un marcador en el mapa de acuerdo a un servicio
    private void createMarkerForService(ServiceClass service){
        MarkerOptions markerOptions =  new MarkerOptions();
        markerOptions.position(new LatLng(service.getLatitude(),service.getLongitude()));
        if(service.getIcon() != 0){ // Validar si existe un icono predefinido del servicio
            markerOptions.icon(BitmapDescriptorFactory.fromResource(service.getIcon()));
        }
        gMap.addMarker(markerOptions);
    }

    // Crear un marcador en el mapa de acuerdo a un atractivo
    private void createMarkerForAttractive(AttractiveClass attractive) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(attractive.getLatitude(), attractive.getLongitude()));
        if (attractive.getIcon() != 0) { // Validar si existe un icono predefinido del atractivo
            markerOptions.icon(BitmapDescriptorFactory.fromResource(attractive.getIcon()));
        }
        gMap.addMarker(markerOptions);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.clear();

        // Validar si la aplicacion tiene el permiso de Localizacion
        if ( ContextCompat.checkSelfPermission(this.messagesAdapter.getChatTextFragment().getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this.messagesAdapter.getChatTextFragment().getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        // Definir la posicion de la camara en el map
        LatLngBounds centroHistorico = new LatLngBounds(
                new LatLng(-0.9364, -78.6163), new LatLng(-0.9301, -78.6129));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroHistorico.getCenter(), 15));

        // Encontrar la posicion del usuario
        EstadoGPS estadoGPS = new EstadoGPS(context, gMap); // Variable para obtener la locacion donde se encuentra el usuario.
        estadoGPS.getCurrentLocation(); // Leer las coordenadas actuales de donde se encunetra el usuario y almacenarlo en un metodo Setter.
        if (estadoGPS.getCurrentLatLng() != null) { // Si esta permitido la ubicacion del usuario se pone las coordenadas de donde se encuentra.
            currentUserLatLng = estadoGPS.getCurrentLatLng();
        } else { // Si no esta permitido la ubicacion actual del usario se pone un punto preestablecido.
            currentUserLatLng = estadoGPS.puntoOrigen;
        }

        // Crear un marcador para la posicion del usuario
        MarkerOptions markerUser = new MarkerOptions()
                .position(currentUserLatLng) // Coordenadas actuales de donde se encunetra el usuario.
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_user_dark_blue));
        gMap.addMarker(markerUser);

        switch (message.getAction()){
            case "consultarAtractivoEnElArea":
                // Agregar un marcador en cada posicion del atractivo
                for (int cont=0; cont < listAttractive.size(); cont++ ){
                    createMarkerForAttractive(listAttractive.get(cont));
                }
                break;
            case "consultarAgenciasDeViajeEnElArea":
            case "consultarAlojamientoEnElArea":
            case "consultarComidaYBebidaEnElArea":
            case "consultarRecreacionDiversionEsparcimientoEnElArea":
                // Agregar un marcador en cada posicion del servicio
                for (int cont=0; cont < listService.size(); cont++ ){
                    createMarkerForService(listService.get(cont));
                }
                break;
            case "church_information_intent.church_information_intent-yes":
            case "hotel_information_intent.hotel_information_intent-yes":
                if(attractive != null){
                    // Crear marcador para la posicion del atractivo de destino
                    createMarkerForAttractive(attractive);
                }else {
                    // Crear marcador para la posicion del servicio de destino
                    createMarkerForService(service);
                }
                // Dibujar la ruta de como llegar al destino.
                MyOnInfoWindowsClickListener myOnInfoWindowsClickListener = new MyOnInfoWindowsClickListener(context, gMap);
                myOnInfoWindowsClickListener.distanciaGoogle(currentUserLatLng, destinationLatLng);

                // Posicionar la camara segun la ruta de donde se encuentre el usuario con el punto de destino.
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(currentUserLatLng);
                builder.include(destinationLatLng);
                gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 15));
                break;
        }
        // Definir la funcion de click en el mapa
        gMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) { // Al dar click llamamos al fragmento del mapa mostrnado solo lo consultado
        if(this.messagesAdapter != null){
            ChatBotActivity chatBotActivity = (ChatBotActivity) this.messagesAdapter.getChatTextFragment().getActivity();
            MapaFragment mapaFragment = new MapaFragment();
            mapaFragment.setSerchFromChatBot(true);
            mapaFragment.setChatBotAction(message.getAction());
            switch (message.getAction()){
                case "consultarAtractivoEnElArea":
                    mapaFragment.setListAttractive(listAttractive);
                    break;
                case "consultarAgenciasDeViajeEnElArea":
                case "consultarAlojamientoEnElArea":
                case "consultarComidaYBebidaEnElArea":
                case "consultarRecreacionDiversionEsparcimientoEnElArea":
                    mapaFragment.setListService(listService);
                    break;
                case "church_information_intent.church_information_intent-yes":
                case "hotel_information_intent.hotel_information_intent-yes":
                    mapaFragment.setPointDestination(destinationLatLng);
                    mapaFragment.setAttractive(this.attractive);
                    if(attractive != null){
                        mapaFragment.setAttractive(this.attractive);
                    }else {
                        mapaFragment.setService(this.service);
                    }
                    break;
            }
            chatBotActivity.changeFragmente(mapaFragment);
        } else {
            Log.d("messageAdapterNUll", "MessageAdapter es Null");
        }
    }
}