package com.example.poyecto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InicioFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.volver_inicio, container, false);

        Button btnClientes = view.findViewById(R.id.btn_clientes);
        Button btnLugares = view.findViewById(R.id.btn_lugares);
        Button btnItinerarios = view.findViewById(R.id.btn_itinerarios);
        Button btnReservas = view.findViewById(R.id.btn_reservas);
        Button btnPagos = view.findViewById(R.id.btn_pagos);

        btnClientes.setOnClickListener(v ->
                navigateTo(new ClientesFragment()));

        btnLugares.setOnClickListener(v ->
                navigateTo(new LugaresFragment()));

        btnItinerarios.setOnClickListener(v ->
                navigateTo(new ItinerariosFragment()));

        btnReservas.setOnClickListener(v ->
                navigateTo(new ReservasFragment()));

        btnPagos.setOnClickListener(v ->
                navigateTo(new PagosFragment()));

        return view;
    }

    private void navigateTo(Fragment fragment) {
        if (getActivity() == null) return;

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}