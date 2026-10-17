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

public class ClientesFragment extends Fragment {

    private EditText etNombre, etApellido, etCedula, etEdad, etCorreo, etTelefono;
    private Spinner spTipoTurista;
    private List<Cliente> lista;
    private GenericAdapter<Cliente> adapter;
    private Cliente selectedItem = null;
    private SQLite dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_clientes, container, false);

        etNombre    = v.findViewById(R.id.etNombre);
        etApellido  = v.findViewById(R.id.etApellido);
        etCedula    = v.findViewById(R.id.etCedula);
        etEdad      = v.findViewById(R.id.etEdad);
        etCorreo    = v.findViewById(R.id.etCorreo);
        etTelefono  = v.findViewById(R.id.etTelefono);
        spTipoTurista = v.findViewById(R.id.spTipoTurista);

        String[] tipos = {"Nacional", "Extranjero"};
        spTipoTurista.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, tipos));

        RecyclerView rv = v.findViewById(R.id.rvClientes);
        Button btnGuardar = v.findViewById(R.id.btnGuardar);
        Button btnSiguiente = v.findViewById(R.id.btnSiguiente);
        Button btnActualizar = v.findViewById(R.id.btnActualizar);
        Button btnEliminar = v.findViewById(R.id.btnEliminar);
        Button btnRegresar = v.findViewById(R.id.btnRegresar);

        dbHelper = SQLite.getInstance(requireContext());
        lista = dbHelper.obtenerClientes();

        adapter = new GenericAdapter<Cliente>(
                lista,
                new GenericAdapter.ItemExtractor<Cliente>() {
                    @Override
                    public String getTitle(Cliente item) {
                        return item.getNombre() + " " + item.getApellido();
                    }

                    @Override
                    public String getSubtitle(Cliente item) {
                        return "C.I: " + item.getCedula();
                    }
                },
                (item, pos) -> {
                    selectedItem = item;

                    etNombre.setText(item.getNombre());
                    etApellido.setText(item.getApellido());
                    etCedula.setText(item.getCedula());
                    etEdad.setText(item.getEdad());
                    etCorreo.setText(item.getCorreo());
                    etTelefono.setText(item.getTelefono());

                    spTipoTurista.setSelection(
                            item.getTipo().equals("Nacional") ? 0 : 1
                    );

                    DataStore.getInstance().setClienteActual(item);
                }
        );

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        // =========================
        // GUARDAR CON VALIDACIONES
        // =========================
        btnGuardar.setOnClickListener(view -> {

            if (!validarFormulario()) return;

            Cliente c = new Cliente(
                    etNombre.getText().toString().trim(),
                    etApellido.getText().toString().trim(),
                    etCedula.getText().toString().trim(),
                    etEdad.getText().toString().trim(),
                    etCorreo.getText().toString().trim(),
                    etTelefono.getText().toString().trim(),
                    spTipoTurista.getSelectedItem().toString()
            );

            if (dbHelper.insertarCliente(c)) {
                lista.add(c);
                adapter.notifyItemInserted(lista.size() - 1);

                DataStore.getInstance().setClienteActual(c);

                Toast.makeText(requireContext(),
                        "Cliente guardado correctamente",
                        Toast.LENGTH_SHORT).show();

                limpiar();
            } else {
                Toast.makeText(requireContext(),
                        "Error al guardar cliente",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // ACTUALIZAR
        btnActualizar.setOnClickListener(view -> {

            if (selectedItem == null) {
                Toast.makeText(requireContext(),
                        "Selecciona un cliente",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validarFormulario()) return;

            selectedItem.setNombre(etNombre.getText().toString().trim());
            selectedItem.setApellido(etApellido.getText().toString().trim());
            selectedItem.setCedula(etCedula.getText().toString().trim());
            selectedItem.setEdad(etEdad.getText().toString().trim());
            selectedItem.setCorreo(etCorreo.getText().toString().trim());
            selectedItem.setTelefono(etTelefono.getText().toString().trim());
            selectedItem.setTipo(spTipoTurista.getSelectedItem().toString());

            if (dbHelper.actualizarCliente(selectedItem)) {
                adapter.notifyDataSetChanged();

                Toast.makeText(requireContext(),
                        "Cliente actualizado",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(),
                        "Error al actualizar",
                        Toast.LENGTH_SHORT).show();
            }

            limpiar();
        });

        // ELIMINAR
        btnEliminar.setOnClickListener(view -> {

            if (selectedItem == null) {
                Toast.makeText(requireContext(),
                        "Selecciona un cliente",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.eliminarCliente(selectedItem.getId())) {

                int pos = lista.indexOf(selectedItem);
                lista.remove(pos);
                adapter.notifyItemRemoved(pos);

                Toast.makeText(requireContext(),
                        "Cliente eliminado",
                        Toast.LENGTH_SHORT).show();
            }

            limpiar();
        });

        // SIGUIENTE
        btnSiguiente.setOnClickListener(view -> {
            if (DataStore.getInstance().getClienteActual() == null) {
                Toast.makeText(requireContext(),
                        "Guarda o selecciona un cliente",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new LugaresFragment())
                    .commit();
        });

        // REGRESAR
        btnRegresar.setOnClickListener(view ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new InicioFragment())
                        .commit()
        );

        return v;
    }

    // =========================
    // VALIDACIONES
    // =========================
    private boolean validarFormulario() {

        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String cedula = etCedula.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        // NOMBRE
        if (nombre.isEmpty()) {
            etNombre.setError("Ingresa el nombre");
            return false;
        }

        // APELLIDO
        if (apellido.isEmpty()) {
            etApellido.setError("Ingresa el apellido");
            return false;
        }

        // CÉDULA ECUATORIANA
        if (!validarCedula(cedula)) {
            etCedula.setError("Cédula inválida");
            return false;
        }

        // EDAD
        if (edad.isEmpty() || Integer.parseInt(edad) < 1 || Integer.parseInt(edad) > 120) {
            etEdad.setError("Edad inválida");
            return false;
        }

        // CORREO (básico)
        if (!correo.contains("@") || !correo.contains(".")) {
            etCorreo.setError("Correo inválido");
            return false;
        }

        // TELÉFONO
        if (telefono.length() < 7) {
            etTelefono.setError("Teléfono inválido");
            return false;
        }

        return true;
    }

    // =========================
    // VALIDACIÓN CÉDULA ECUADOR
    // =========================
    private boolean validarCedula(String cedula) {

        if (cedula == null || cedula.length() != 10) return false;

        try {
            int provincia = Integer.parseInt(cedula.substring(0, 2));
            if (provincia < 1 || provincia > 24) return false;

            int[] coef = {2,1,2,1,2,1,2,1,2};
            int suma = 0;

            for (int i = 0; i < 9; i++) {
                int val = Character.getNumericValue(cedula.charAt(i)) * coef[i];
                if (val > 9) val -= 9;
                suma += val;
            }

            int digito = 10 - (suma % 10);
            if (digito == 10) digito = 0;

            return digito == Character.getNumericValue(cedula.charAt(9));

        } catch (Exception e) {
            return false;
        }
    }

    private void limpiar() {
        etNombre.setText("");
        etApellido.setText("");
        etCedula.setText("");
        etEdad.setText("");
        etCorreo.setText("");
        etTelefono.setText("");
        spTipoTurista.setSelection(0);
        selectedItem = null;
    }
}