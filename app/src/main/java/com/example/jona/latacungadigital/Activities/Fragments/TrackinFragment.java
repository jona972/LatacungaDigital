package com.example.jona.latacungadigital.Activities.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.jona.latacungadigital.Activities.Adapters.TrackingAdapter;
import com.example.jona.latacungadigital.Activities.modelos.TrackinModel;
import com.example.jona.latacungadigital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class TrackinFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters


    private OnFragmentInteractionListener mListener;
    private static ArrayList<TrackinModel> listaAmigos = new ArrayList<>();
    DatabaseReference mdatabase;
    FirebaseAuth userFirebase;
    RecyclerView recyclerView;
    private static final ArrayList<String> listaUsuarios = new ArrayList<>();

    public TrackinFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trackin, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_friends);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        ConsultaAmigos();
        ConsultaUsuarios();

        //Autocompletar
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, listaUsuarios);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                view.findViewById(R.id.tv_buscar);
        textView.setAdapter(adapter);

        //Flotante que redirige al mapa
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Se presionó el FAB", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                MapaFragment mf = new MapaFragment();
                //fr.setArguments(bn);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment, mf);
                transaction.addToBackStack(null);

                // Commit a la transacción
                transaction.commit();
            }
        });


        return view;


    }

    private void ConsultaUsuarios() {
        mdatabase = FirebaseDatabase.getInstance().getReference("cliente");
        mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    if (child.child("nombre").exists())
                        listaUsuarios.add(child.child("nombre").getValue().toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ConsultaAmigos() {
        userFirebase = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference("autorizados")
                    .child(userFirebase.getCurrentUser().getUid());
        //System.out.println("uid: "+userFirebase.getCurrentUser().getUid());
        mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    //System.out.println("uidn: "+child.child("nombre").getValue().toString());
                    String nombre = child.child("nombre").getValue().toString();
                    String email = child.child("email").getValue().toString();
                    String key = child.child("key").getValue().toString();
                    String pathimage = child.child("pathImagen").getValue().toString();
                    boolean autorizacion = false;
                    if (child.child("autorizacion").getValue().toString() == "true")
                        autorizacion = true;
                    TrackinModel trac = new TrackinModel(nombre, email, pathimage, key, autorizacion);
                    listaAmigos.add(trac);
                }
                recyclerView.setAdapter(new TrackingAdapter( listaAmigos));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}