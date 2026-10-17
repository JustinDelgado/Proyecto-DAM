package com.example.poyecto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LugaresFragment extends Fragment {

    private EditText etNombreLugar, etProvincia, etCosto;
    private Spinner spCategoria;
    private RatingBar rbPopularidad;
    private CheckBox cbWifi, cbGuia, cbTransporte;

    private List<Lugar> lista;
    private GenericAdapter<Lugar> adapter;
    private Lugar selectedItem = null;
    private SQLite dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_lugares, container, false);

        etNombreLugar = v.findViewById(R.id.etNombreLugar);
        spCategoria = v.findViewById(R.id.spCategoria);
        etProvincia = v.findViewById(R.id.etCiudad);
        etCosto = v.findViewById(R.id.etCosto);
        rbPopularidad = v.findViewById(R.id.rbPopularidad);
        cbWifi = v.findViewById(R.id.cbWifi);
        cbGuia = v.findViewById(R.id.cbGuia);
        cbTransporte = v.findViewById(R.id.cbTransporte);

        RecyclerView rv = v.findViewById(R.id.rvLugares);

        Button btnGuardar = v.findViewById(R.id.btnGuardar);
        Button btnSiguiente = v.findViewById(R.id.btnSiguiente);
        Button btnActualizar = v.findViewById(R.id.btnActualizar);
        Button btnEliminar = v.findViewById(R.id.btnEliminar);
        Button btnRegresar = v.findViewById(R.id.btnRegresar);

        String[] cats = {"Playa", "Montaña", "Selva", "Ciudad Histórica"};
        spCategoria.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, cats));

        dbHelper = SQLite.getInstance(requireContext());
        lista = dbHelper.obtenerLugares();

        adapter = new GenericAdapter<>(lista,
                new GenericAdapter.ItemExtractor<Lugar>() {
                    @Override
                    public String getTitle(Lugar item) {
                        return item.getNombre();
                    }

                    @Override
                    public String getSubtitle(Lugar item) {
                        return item.getProvincia() + " | $" + item.getCosto();
                    }
                },
                (item, pos) -> {
                    selectedItem = item;

                    etNombreLugar.setText(item.getNombre());
                    etProvincia.setText(item.getProvincia());
                    etCosto.setText(item.getCosto());

                    DataStore.getInstance().setLugarActual(item);
                });

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        // ======================
        // GUARDAR
        // ======================
        btnGuardar.setOnClickListener(view -> {

            if (etNombreLugar.getText().toString().trim().isEmpty()) {
                etNombreLugar.setError("Campo requerido");
                return;
            }

            String servicios = buildServiciosTag();

            Lugar l = new Lugar(
                    etNombreLugar.getText().toString().trim(),
                    etProvincia.getText().toString().trim(),
                    etCosto.getText().toString().trim()
            );

            if (dbHelper.insertarLugar(l)) {
                lista.add(l);
                adapter.notifyItemInserted(lista.size() - 1);

                DataStore.getInstance().setLugarActual(l);

                Itinerario nuevoIti = new Itinerario(l.getNombre(), "1", l.getCosto());
                dbHelper.insertarItinerario(nuevoIti);

                Toast.makeText(requireContext(),
                        "Lugar y Paquete base guardados",
                        Toast.LENGTH_SHORT).show();
            }

            limpiar();
        });


        // METODO ACTUALIZAR

        btnActualizar.setOnClickListener(view -> {

            if (selectedItem == null) {
                Toast.makeText(requireContext(),
                        "Selecciona un lugar",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            selectedItem.setNombre(etNombreLugar.getText().toString().trim());
            selectedItem.setProvincia(etProvincia.getText().toString().trim());
            selectedItem.setCosto(etCosto.getText().toString().trim());

            dbHelper.actualizarLugar(selectedItem);
            adapter.notifyDataSetChanged();

            Toast.makeText(requireContext(),
                    "Lugar actualizado",
                    Toast.LENGTH_SHORT).show();

            limpiar();
        });


        // METODO ELIMINAR

        btnEliminar.setOnClickListener(view -> {

            if (selectedItem == null) {
                Toast.makeText(requireContext(),
                        "Selecciona un lugar",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.eliminarLugar(selectedItem.getId())) {

                int pos = lista.indexOf(selectedItem);

                if (pos != -1) {
                    lista.remove(pos);
                    adapter.notifyItemRemoved(pos);
                }

                Toast.makeText(requireContext(),
                        "Lugar eliminado",
                        Toast.LENGTH_SHORT).show();
            }

            limpiar();
        });

        // METODO SIGUIENTE
        btnSiguiente.setOnClickListener(view -> {

            if (DataStore.getInstance().getLugarActual() == null) {
                Toast.makeText(requireContext(),
                        "Guarda o selecciona un lugar",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new ItinerariosFragment())
                    .commit();
        });

        btnRegresar.setOnClickListener(view ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new ClientesFragment())
                        .commit()
        );

        return v;
    }

    private String buildServiciosTag() {
        StringBuilder sb = new StringBuilder();

        if (cbWifi.isChecked()) sb.append("WiFi, ");
        if (cbGuia.isChecked()) sb.append("Guía, ");
        if (cbTransporte.isChecked()) sb.append("Transp., ");

        if (sb.length() > 2) sb.setLength(sb.length() - 2);

        return sb.toString();
    }

    private void limpiar() {
        etNombreLugar.setText("");
        etProvincia.setText("");
        etCosto.setText("");

        rbPopularidad.setRating(0);

        cbWifi.setChecked(false);
        cbGuia.setChecked(false);
        cbTransporte.setChecked(false);

        spCategoria.setSelection(0);

        selectedItem = null;
    }
}