package com.example.jona.latacungadigital.Activities.Clases;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jona.latacungadigital.Activities.MainActivity;
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
    // Variables para editar el mapa
    ArrayList<ServiceClass> listService;
    GoogleMap gMap;

    // Varaibles de acuerdo a los componentes que comprenden el layout: message_cv_map.xml
    protected TextView txtTitle;
    protected MapView mapView;
    private TextMessageModel message;

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
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        mapView = (MapView) view.findViewById(R.id.mapView);
    }

    private void setValues() {
        if(message != null){
            listService = message.getListService();
            txtTitle.setText(message.getTitulo());
            mapView.getMapAsync(this);
        }
    }

    public void setMessage(TextMessageModel message) {
        this.message = message;
        setValues();
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
    private void createMarker(ServiceClass service){
        MarkerOptions markerOptions =  new MarkerOptions();
        markerOptions.position(new LatLng(service.getLatitude(),service.getLongitude()));
        if(service.getIcon() != 0){ // Validar si existe un icono predefinido del servicio
            markerOptions.icon(BitmapDescriptorFactory.fromResource(service.getIcon()));
        }
        gMap.addMarker(markerOptions);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        // Definir la posicion de la camara en el map
        LatLngBounds centroHistorico = new LatLngBounds(
                new LatLng(-0.9364, -78.6163), new LatLng(-0.9301, -78.6129));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroHistorico.getCenter(), 15));

        // Agregar un marcador en cada posicion del servicio
        for (int cont=0; cont < listService.size(); cont++ ){
            createMarker(listService.get(cont));
        }

        // Definir la funcion de click en el mapa
        gMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        getContext().startActivity(intent);
    }
}