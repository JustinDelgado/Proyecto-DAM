package com.example.poyecto;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etCedula, etNombres, etApellidos, etEdad, etFechaNacimiento;
    private Spinner spNacionalidad, spGenero;
    private RadioGroup rgEstadoCivil;
    private RatingBar rbNivelIngles;
    private final String NOMBRE_ARCHIVO = "datos_registro.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etCedula = findViewById(R.id.etCedula);
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etEdad = findViewById(R.id.etEdad);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        spNacionalidad = findViewById(R.id.spNacionalidad);
        spGenero = findViewById(R.id.spGenero);
        rgEstadoCivil = findViewById(R.id.rgEstadoCivil);
        rbNivelIngles = findViewById(R.id.rbNivelIngles);

        MaterialButton btnRegistrar = findViewById(R.id.btnRegistrar);
        MaterialButton btnRecuperar = findViewById(R.id.btnRecuperar);
        MaterialButton btnBorrar = findViewById(R.id.btnBorrar);
        MaterialButton btnCancelar = findViewById(R.id.btnCancelar);

        etFechaNacimiento.setOnClickListener(v -> showDatePickerDialog());

        btnRegistrar.setOnClickListener(v -> {
            if (validarCampos()) {
                registrarInformacion();
            }
        });

        btnRecuperar.setOnClickListener(v -> recuperarInformacion());
        btnBorrar.setOnClickListener(v -> borrarFormulario());
        btnCancelar.setOnClickListener(v -> finish());
    }

    // =========================
    // VALIDACIONES
    // =========================
    private boolean validarCampos() {

        String cedula = etCedula.getText().toString().trim();
        String nombres = etNombres.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String fecha = etFechaNacimiento.getText().toString().trim();

        // CÉDULA
        if (cedula.isEmpty() || cedula.length() != 10) {
            etCedula.setError("Cédula inválida (10 dígitos)");
            return false;
        }

        // NOMBRES
        if (nombres.isEmpty()) {
            etNombres.setError("Ingrese nombres");
            return false;
        }

        // APELLIDOS
        if (apellidos.isEmpty()) {
            etApellidos.setError("Ingrese apellidos");
            return false;
        }

        // EDAD
        if (edadStr.isEmpty()) {
            etEdad.setError("Ingrese edad");
            return false;
        }

        int edad = Integer.parseInt(edadStr);
        if (edad < 1 || edad > 120) {
            etEdad.setError("Edad inválida");
            return false;
        }

        // FECHA NACIMIENTO
        if (fecha.isEmpty()) {
            etFechaNacimiento.setError("Seleccione fecha");
            return false;
        }

        // RADIO BUTTON (estado civil)
        if (rgEstadoCivil.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Seleccione estado civil", Toast.LENGTH_SHORT).show();
            return false;
        }

        // NIVEL INGLÉS
        if (rbNivelIngles.getRating() == 0) {
            Toast.makeText(this, "Califique nivel de inglés", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // =========================
    // TU MÉTODO ORIGINAL
    // =========================
    private void registrarInformacion() {

        String cedula = etCedula.getText().toString();
        String nombres = etNombres.getText().toString();
        String apellidos = etApellidos.getText().toString();
        String edad = etEdad.getText().toString();
        String nacionalidad = spNacionalidad.getSelectedItem().toString();
        String genero = spGenero.getSelectedItem().toString();
        String fechaNac = etFechaNacimiento.getText().toString();
        float nivelIngles = rbNivelIngles.getRating();

        int selectedId = rgEstadoCivil.getCheckedRadioButtonId();
        String estadoCivil = (selectedId != -1)
                ? ((RadioButton) findViewById(selectedId)).getText().toString()
                : "N/A";

        String datosParaGuardar = cedula + ";"
                + nombres + ";"
                + apellidos + ";"
                + edad + ";"
                + nacionalidad + ";"
                + genero + ";"
                + estadoCivil + ";"
                + fechaNac + ";"
                + nivelIngles + ";";

        try (java.io.FileOutputStream fos = openFileOutput(NOMBRE_ARCHIVO, MODE_PRIVATE)) {
            fos.write(datosParaGuardar.getBytes());
            Toast.makeText(this, "Datos almacenados correctamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    // =========================
    // DATE PICKER
    // =========================
    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String fecha = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    etFechaNacimiento.setText(fecha);
                }, year, month, day);
        datePickerDialog.show();
    }

    // =========================
    // RESTO SIN CAMBIOS
    // =========================
    private void recuperarInformacion() { /* igual */ }

    private void borrarFormulario() {
        etCedula.setText("");
        etNombres.setText("");
        etApellidos.setText("");
        etEdad.setText("");
        etFechaNacimiento.setText("");
        spNacionalidad.setSelection(0);
        spGenero.setSelection(0);
        rgEstadoCivil.clearCheck();
        rbNivelIngles.setRating(0);
        Toast.makeText(this, "Formulario limpiado", Toast.LENGTH_SHORT).show();
    }
}