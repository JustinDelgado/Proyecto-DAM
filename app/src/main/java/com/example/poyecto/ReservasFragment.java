package com.example.poyecto;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.List;

public class ReservasFragment extends Fragment {

    private EditText etCliente;
    private Spinner spReservaPaquete;
    private Button btnSeleccionarFecha;
    private String fechaSeleccionada = "";

    private List<Reserva> lista;
    private GenericAdapter<Reserva> adapter;

    // Reserva actualmente seleccionada (para actualizar / eliminar)
    private Reserva reservaSeleccionada = null;

    // Helper de base de datos
    private SQLite dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reservas, container, false);

        // ── Referencias a vistas ──────────────────────────────────────────────
        etCliente          = v.findViewById(R.id.etCliente);
        spReservaPaquete   = v.findViewById(R.id.spReservaPaquete);
        btnSeleccionarFecha = v.findViewById(R.id.btnSeleccionarFecha);
        RecyclerView rv    = v.findViewById(R.id.rvReservas);
        Button btnGuardar  = v.findViewById(R.id.btnGuardar);
        Button btnSiguiente  = v.findViewById(R.id.btnSiguiente);
        Button btnActualizar = v.findViewById(R.id.btnActualizar);
        Button btnEliminar   = v.findViewById(R.id.btnEliminar);
        Button btnRegresar   = v.findViewById(R.id.btnRegresar);

        //Base de datos
        dbHelper = SQLite.getInstance(requireContext());

        //Cargar Itinerarios desde BD para el Spinner
        List<Itinerario> itinerariosDisponibles = dbHelper.obtenerItinerarios();
        String[] nombresPaquetes;
        
        if (itinerariosDisponibles.isEmpty()) {
            nombresPaquetes = new String[]{"Tour Galápagos", "Quito Histórico", "Guayaquil Nocturno"};
        } else {
            nombresPaquetes = new String[itinerariosDisponibles.size()];
            for (int i = 0; i < itinerariosDisponibles.size(); i++) {
                nombresPaquetes[i] = itinerariosDisponibles.get(i).getNombre();
            }
        }

        spReservaPaquete.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                nombresPaquetes));

        // AUTO-COMPLETADO DESDE FLUJO
        Cliente clienteActual = DataStore.getInstance().getClienteActual();
        if (clienteActual != null) {
            etCliente.setText(clienteActual.getNombre() + " " + clienteActual.getApellido());
        }

        Itinerario itiActual = DataStore.getInstance().getItinerarioActual();
        if (itiActual != null) {
            for (int i = 0; i < nombresPaquetes.length; i++) {
                if (nombresPaquetes[i].contains(itiActual.getNombre())) {
                    spReservaPaquete.setSelection(i);
                    break;
                }
            }
        }

        //Selector de fecha
        btnSeleccionarFecha.setOnClickListener(view -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (datePicker, year, month, day) -> {
                fechaSeleccionada = day + "/" + (month + 1) + "/" + year;
                btnSeleccionarFecha.setText("FECHA: " + fechaSeleccionada);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        //Lista desde SQLite
        lista = dbHelper.obtenerReservas();

        adapter = new GenericAdapter<>(lista,
                new GenericAdapter.ItemExtractor<Reserva>() {
                    @Override
                    public String getTitle(Reserva item) {
                        return item.getClienteNombre() + " - " + item.getItinerarioNombre();
                    }
                    @Override
                    public String getSubtitle(Reserva item) {
                        return "Viaje: " + item.getFecha();
                    }
                },
                new GenericAdapter.OnItemClickListener<Reserva>() {
                    @Override
                    public void onItemClick(Reserva item, int position) {
                        // Cargar datos en el formulario para editar / eliminar
                        reservaSeleccionada = item;
                        etCliente.setText(item.getClienteNombre());
                        fechaSeleccionada = item.getFecha();
                        btnSeleccionarFecha.setText("FECHA: " + fechaSeleccionada);

                        // Seleccionar el paquete correcto en el Spinner
                        for (int i = 0; i < nombresPaquetes.length; i++) {
                            if (nombresPaquetes[i].equals(item.getItinerarioNombre())) {
                                spReservaPaquete.setSelection(i);
                                break;
                            }
                        }
                    }
                });

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        //GUARDAR (INSERT)
        btnGuardar.setOnClickListener(view -> {
            if (!validarFormulario()) return;

            Reserva nueva = new Reserva(
                    etCliente.getText().toString().trim(),
                    spReservaPaquete.getSelectedItem().toString(),
                    fechaSeleccionada);

            if (dbHelper.insertarReserva(nueva)) {
                lista.add(nueva);
                adapter.notifyItemInserted(lista.size() - 1);
                Toast.makeText(requireContext(), "Reserva guardada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
            }
            limpiar();
        });

        //SIGUIENTE
        btnSiguiente.setOnClickListener(view -> {
            if (reservaSeleccionada != null) {
                // Guardar la reserva actual en el DataStore
                DataStore.getInstance().setReservaActual(reservaSeleccionada);

                // Navegar a Pagos
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new PagosFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(requireContext(), "Selecciona una reserva de la lista para proceder al pago", Toast.LENGTH_SHORT).show();
            }
        });

        //ACTUALIZAR (UPDATE)
        btnActualizar.setOnClickListener(view -> {
            if (reservaSeleccionada == null) {
                Toast.makeText(requireContext(), "Selecciona una reserva de la lista", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!validarFormulario()) return;

            reservaSeleccionada.setClienteNombre(etCliente.getText().toString().trim());
            reservaSeleccionada.setItinerarioNombre(spReservaPaquete.getSelectedItem().toString());
            reservaSeleccionada.setFecha(fechaSeleccionada);

            if (dbHelper.actualizarReserva(reservaSeleccionada)) {
                adapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Reserva actualizada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
            limpiar();
        });

        //ELIMINAR (DELETE)
        btnEliminar.setOnClickListener(view -> {
            if (reservaSeleccionada == null) {
                Toast.makeText(requireContext(), "Selecciona una reserva de la lista", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.eliminarReserva(reservaSeleccionada.getId())) {
                int pos = lista.indexOf(reservaSeleccionada);
                lista.remove(pos);
                adapter.notifyItemRemoved(pos);
                Toast.makeText(requireContext(), "Reserva eliminada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
            limpiar();
        });

        //REGRESAR
        btnRegresar.setOnClickListener(view ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new ItinerariosFragment())
                        .commit());

        return v;
    }

    //Helpers privados

    private boolean validarFormulario() {
        if (etCliente.getText().toString().trim().isEmpty()) {
            etCliente.setError("Ingresa el nombre del titular");
            return false;
        }
        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona una fecha de viaje", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void limpiar() {
        etCliente.setText("");
        fechaSeleccionada = "";
        btnSeleccionarFecha.setText("SELECCIONAR FECHA DE VIAJE");
        spReservaPaquete.setSelection(0);
        reservaSeleccionada = null;
    }
}