package com.example.poyecto;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ArrayAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PagosFragment extends Fragment {

    private TextView tvResumenFlujo;
    private List<Pago> lista;
    private GenericAdapter<Pago> adapter;
    private Pago selectedItem = null;
    private SQLite dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_pagos, container, false);

        tvResumenFlujo = v.findViewById(R.id.tvResumenFlujo);
        Spinner spMetodoPago = v.findViewById(R.id.spMetodoPago);
        Button btnVerFactura = v.findViewById(R.id.btnVerFactura);
        Button btnActualizar = v.findViewById(R.id.btnActualizar);
        Button btnEliminar = v.findViewById(R.id.btnEliminar);
        Button btnRegresar = v.findViewById(R.id.btnRegresar);
        RecyclerView rv = v.findViewById(R.id.rvPagos);

        dbHelper = SQLite.getInstance(requireContext());
        lista = dbHelper.obtenerPagos();

        cargarResumen();

        // Configurar Spinner de Método de Pago
        String[] metodos = {"Tarjeta de Crédito", "Tarjeta de Débito", "Transferencia Bancaria", "Efectivo"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, metodos);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMetodoPago.setAdapter(spinnerAdapter);

        adapter = new GenericAdapter<>(lista,
                new GenericAdapter.ItemExtractor<Pago>() {
                    @Override
                    public String getTitle(Pago item) {
                        return "Factura: " + item.getClienteNombre();
                    }

                    @Override
                    public String getSubtitle(Pago item) {
                        return "Monto: $" + item.getMonto() + "  |  Fecha: " + item.getFecha();
                    }
                },
                (item, pos) -> {
                    selectedItem = item;
                    
                    // Actualizar el card de resumen con la información del pago seleccionado
                    String resumen =
                            "CLIENTE: " + item.getClienteNombre() + "\n" +
                            "LUGAR: " + item.getLugar() + "\n" +
                            "PAQUETE: Viaje Registrado\n" +
                            "FECHA PAGO: " + item.getFecha() + "\n" +
                            "TOTAL: $" + item.getMonto();
                    
                    tvResumenFlujo.setText(resumen);

                    Toast.makeText(requireContext(),
                            "Seleccionado: " + item.getClienteNombre(),
                            Toast.LENGTH_SHORT).show();
                });

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        // ================= FACTURA =================
        btnVerFactura.setOnClickListener(view -> {

            Cliente c = DataStore.getInstance().getClienteActual();
            Lugar l = DataStore.getInstance().getLugarActual();
            Itinerario i = DataStore.getInstance().getItinerarioActual();

            // VALIDACIÓN 1: flujo completo
            if (c == null || l == null || i == null) {
                Toast.makeText(requireContext(),
                        "Complete Cliente → Lugar → Itinerario",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // VALIDACIÓN 2: datos obligatorios del cliente
            if (TextUtils.isEmpty(c.getNombre()) || TextUtils.isEmpty(c.getCedula())) {
                Toast.makeText(requireContext(),
                        "Datos del cliente incompletos",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // VALIDACIÓN 3: precio válido
            if (TextUtils.isEmpty(i.getPrecio())) {
                Toast.makeText(requireContext(),
                        "Precio del itinerario inválido",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String fecha = android.text.format.DateFormat
                    .format("dd/MM/yyyy", new java.util.Date()).toString();

            String metodoSeleccionado = spMetodoPago.getSelectedItem().toString();

            Pago p = new Pago(
                    c.getNombre() + " " + c.getApellido(),
                    c.getCedula(),
                    l.getProvincia(),
                    l.getNombre(),
                    "1",
                    metodoSeleccionado,
                    i.getPrecio(),
                    fecha
            );

            if (dbHelper.insertarPago(p)) {
                lista.add(0, p);
                adapter.notifyItemInserted(0);
                rv.scrollToPosition(0);
                mostrarFactura(p);
            } else {
                Toast.makeText(requireContext(),
                        "Error al registrar pago",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // ================= ACTUALIZAR =================
        btnActualizar.setOnClickListener(view -> {

            if (selectedItem == null) {
                Toast.makeText(requireContext(),
                        "Selecciona un pago primero",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            EditText etMonto = new EditText(requireContext());
            etMonto.setHint("Nuevo monto");
            etMonto.setText(selectedItem.getMonto());

            new AlertDialog.Builder(requireContext())
                    .setTitle("Editar monto")
                    .setView(etMonto)
                    .setPositiveButton("Guardar", (d, w) -> {

                        String nuevoMonto = etMonto.getText().toString().trim();

                        // VALIDACIÓN MONTO
                        if (TextUtils.isEmpty(nuevoMonto)) {
                            Toast.makeText(requireContext(),
                                    "Ingrese un monto válido",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        selectedItem.setMonto(nuevoMonto);

                        if (dbHelper.actualizarPago(selectedItem)) {
                            adapter.notifyDataSetChanged();
                            Toast.makeText(requireContext(),
                                    "Pago actualizado",
                                    Toast.LENGTH_SHORT).show();
                        }

                        selectedItem = null;
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // ================= ELIMINAR =================
        btnEliminar.setOnClickListener(view -> {

            if (selectedItem == null) {
                Toast.makeText(requireContext(),
                        "Selecciona un pago primero",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar pago")
                    .setMessage("¿Eliminar factura de " + selectedItem.getClienteNombre() + "?")
                    .setPositiveButton("Eliminar", (d, w) -> {

                        if (dbHelper.eliminarPago(selectedItem.getId())) {
                            int pos = lista.indexOf(selectedItem);
                            lista.remove(pos);
                            adapter.notifyItemRemoved(pos);

                            Toast.makeText(requireContext(),
                                    "Pago eliminado",
                                    Toast.LENGTH_SHORT).show();
                        }

                        selectedItem = null;
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnRegresar.setOnClickListener(view ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new InicioFragment())
                        .commit()
        );

        return v;
    }

    private void cargarResumen() {
        Cliente c = DataStore.getInstance().getClienteActual();
        Lugar l = DataStore.getInstance().getLugarActual();
        Itinerario i = DataStore.getInstance().getItinerarioActual();
        Reserva r = DataStore.getInstance().getReservaActual();

        String resumen =
                "CLIENTE: " + (c != null ? c.getNombre() : "N/A") + "\n" +
                "LUGAR: " + (l != null ? l.getNombre() : "N/A") + "\n" +
                "PAQUETE: " + (i != null ? i.getNombre() : (r != null ? r.getItinerarioNombre() : "N/A")) + "\n" +
                "FECHA VIAJE: " + (r != null ? r.getFecha() : "N/A") + "\n" +
                "TOTAL: $" + (i != null ? i.getPrecio() : "0");

        tvResumenFlujo.setText(resumen);
    }

    private void mostrarFactura(Pago pago) {
        String detalle =
                "-------------------------------------------\n" +
                "   DESTINOS GLOBALES PRO\n" +
                "-------------------------------------------\n" +
                "FACTURA\n\n" +
                "CLIENTE: " + pago.getClienteNombre() + "\n" +
                "CÉDULA: " + pago.getCedula() + "\n" +
                "DESTINO: " + pago.getLugar() + "\n" +
                "PROVINCIA: " + pago.getProvincia() + "\n" +
                "FECHA PAGO: " + pago.getFecha() + "\n" +
                "MÉTODO: " + pago.getTarjeta() + "\n\n" +
                "TOTAL PAGADO: $" + pago.getMonto() + "\n" +
                "-------------------------------------------\n" +
                "¡Gracias por su preferencia!";

        new AlertDialog.Builder(requireContext())
                .setTitle("Pago Exitoso")
                .setMessage(detalle)
                .setPositiveButton("Cerrar", (dialog, which) -> {
                    DataStore.getInstance().resetFlujo();
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, new InicioFragment())
                            .commit();
                })
                .setCancelable(false)
                .show();
    }
}