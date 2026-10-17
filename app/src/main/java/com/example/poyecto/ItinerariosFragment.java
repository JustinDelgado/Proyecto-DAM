package com.example.poyecto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.List;

public class ItinerariosFragment extends Fragment {

    private Spinner spPaquetes;
    private SeekBar sbDias;
    private TextView tvDiasSeleccionados;
    private EditText etPrecio;
    private SwitchMaterial swSeguro;

    private List<Itinerario> lista;
    private GenericAdapter<Itinerario> adapter;
    private Itinerario selectedItem = null;
    private SQLite dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_itinerarios, container, false);

        spPaquetes = v.findViewById(R.id.spPaquetes);
        sbDias = v.findViewById(R.id.sbDias);
        tvDiasSeleccionados = v.findViewById(R.id.tvDiasSeleccionados);
        etPrecio = v.findViewById(R.id.etPrecio);
        swSeguro = v.findViewById(R.id.swSeguro);

        RecyclerView rv = v.findViewById(R.id.rvItinerarios);

        Button btnGuardar = v.findViewById(R.id.btnGuardar);
        Button btnSiguiente = v.findViewById(R.id.btnSiguiente);
        Button btnActualizar = v.findViewById(R.id.btnActualizar);
        Button btnEliminar = v.findViewById(R.id.btnEliminar);
        Button btnRegresar = v.findViewById(R.id.btnRegresar);

        dbHelper = SQLite.getInstance(requireContext());

        // 🔥 SIEMPRE cargar desde BD
        lista = dbHelper.obtenerItinerarios();

        // 🔥 SI NO HAY DATOS, INSERTAR BASE
        if (lista.isEmpty()) {

            Itinerario i1 = new Itinerario("Tour Galápagos", "5", "750");
            Itinerario i2 = new Itinerario("Quito Histórico", "3", "450");
            Itinerario i3 = new Itinerario("Guayaquil Nocturno", "2", "300");

            dbHelper.insertarItinerario(i1);
            dbHelper.insertarItinerario(i2);
            dbHelper.insertarItinerario(i3);

            lista = dbHelper.obtenerItinerarios();
        }

        // =========================
        // SPINNER
        // =========================
        ArrayAdapter<Itinerario> spAdapter =
                new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        lista);

        spPaquetes.setAdapter(spAdapter);

        spPaquetes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                selectedItem = lista.get(pos);

                try {
                    sbDias.setProgress(Integer.parseInt(selectedItem.getDias()));
                } catch (Exception e) {
                    sbDias.setProgress(1);
                }

                etPrecio.setText(selectedItem.getPrecio());
                calcularPrecioActual();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // =========================
        // SEEKBAR
        // =========================
        sbDias.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int dias = Math.max(1, progress);

                tvDiasSeleccionados.setText("Duración: " + dias + " días");

                calcularPrecioActual();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // =========================
        // SWITCH
        // =========================
        swSeguro.setOnCheckedChangeListener((btn, isChecked) -> {
            calcularPrecioActual();
        });

        // =========================
        // ADAPTER
        // =========================
        adapter = new GenericAdapter<>(lista,
                new GenericAdapter.ItemExtractor<Itinerario>() {
                    @Override
                    public String getTitle(Itinerario item) {
                        return item.getNombre() + " (" + item.getDias() + " días)";
                    }

                    @Override
                    public String getSubtitle(Itinerario item) {
                        try {
                            double p = Double.parseDouble(item.getPrecio());
                            return "Precio: $" + String.format(java.util.Locale.US, "%.2f", p);
                        } catch (Exception e) {
                            return "Precio: $" + item.getPrecio();
                        }
                    }
                },
                (item, pos) -> {

                    selectedItem = item;

                    spPaquetes.setSelection(pos);

                    try {
                        sbDias.setProgress(Integer.parseInt(item.getDias()));
                    } catch (Exception ignored) {}

                    etPrecio.setText(item.getPrecio());

                    DataStore.getInstance().setItinerarioActual(item);
                });

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        // =========================
        // ACCIONES DE BOTONES
        // =========================

        btnGuardar.setOnClickListener(v1 -> {
            String nombre = spPaquetes.getSelectedItem() != null ? spPaquetes.getSelectedItem().toString() : "Nuevo Paquete";
            String dias = String.valueOf(sbDias.getProgress());
            String precio = etPrecio.getText().toString();

            Itinerario nuevo = new Itinerario(nombre + " Personalizado", dias, precio);
            if (dbHelper.insertarItinerario(nuevo)) {
                Toast.makeText(getContext(), "Itinerario guardado", Toast.LENGTH_SHORT).show();
                refrescarLista();
            }
        });

        btnActualizar.setOnClickListener(v1 -> {
            if (selectedItem != null) {
                selectedItem.setDias(String.valueOf(sbDias.getProgress()));
                selectedItem.setPrecio(etPrecio.getText().toString());
                if (dbHelper.actualizarItinerario(selectedItem)) {
                    Toast.makeText(getContext(), "Itinerario actualizado", Toast.LENGTH_SHORT).show();
                    refrescarLista();
                }
            } else {
                Toast.makeText(getContext(), "Selecciona un itinerario de la lista", Toast.LENGTH_SHORT).show();
            }
        });

        btnEliminar.setOnClickListener(v1 -> {
            if (selectedItem != null) {
                if (dbHelper.eliminarItinerario(selectedItem.getId())) {
                    Toast.makeText(getContext(), "Itinerario eliminado", Toast.LENGTH_SHORT).show();
                    selectedItem = null;
                    refrescarLista();
                }
            }
        });

        btnSiguiente.setOnClickListener(v1 -> {
            if (selectedItem != null) {
                // Capturar los valores actuales de la UI antes de avanzar
                selectedItem.setDias(String.valueOf(sbDias.getProgress()));
                selectedItem.setPrecio(etPrecio.getText().toString());
                
                // Guardar en el DataStore para el flujo de la App
                DataStore.getInstance().setItinerarioActual(selectedItem);
                
                // Navegar a Reservas
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new ReservasFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Por favor, selecciona un paquete", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegresar.setOnClickListener(v1 -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new LugaresFragment())
                    .commit();
        });

        return v;
    }

    private void calcularPrecioActual() {
        int dias = Math.max(1, sbDias.getProgress());
        double subtotal = dias * 150.0;
        if (swSeguro.isChecked()) {
            subtotal += (dias * 50.0);
        }

        // Aplicar descuento si el cliente es Nacional
        Cliente c = DataStore.getInstance().getClienteActual();
        if (c != null && "Nacional".equalsIgnoreCase(c.getTipo())) {
            subtotal *= 0.90;
        }

        etPrecio.setText(String.format(java.util.Locale.US, "%.2f", subtotal));
    }

    private void refrescarLista() {
        lista.clear();
        lista.addAll(dbHelper.obtenerItinerarios());
        adapter.notifyDataSetChanged();
        
        // Actualizar Spinner
        ArrayAdapter<Itinerario> spAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, lista);
        spPaquetes.setAdapter(spAdapter);
    }
}