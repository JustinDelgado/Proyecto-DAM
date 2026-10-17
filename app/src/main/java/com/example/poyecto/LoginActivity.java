package com.example.poyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUser, etPass;
    private android.widget.CheckBox cbMantenerSesion;
    private MaterialButton btnLogin, btnCrearCuenta;

    private static final String VALID_USER = "admin";
    private static final String VALID_PASS = "12345";

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LOGGED_USER = "loggedUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 🔥 AUTO LOGIN
        if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        cbMantenerSesion = findViewById(R.id.cbMantenerSesion);
        btnLogin = findViewById(R.id.btnLogin);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        btnLogin.setOnClickListener(v -> validarLogin());

        btnCrearCuenta.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void validarLogin() {

        String usuario = etUser.getText().toString().trim();
        String clave = etPass.getText().toString().trim();

        if (usuario.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this,
                    "Complete todos los campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuario.equals(VALID_USER) && clave.equals(VALID_PASS)) {

            if (cbMantenerSesion.isChecked()) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit()
                        .putBoolean(KEY_IS_LOGGED_IN, true)
                        .putString(KEY_LOGGED_USER, usuario)
                        .apply();
            }

            DataStore.getInstance().setLoggedUser(usuario);

            Toast.makeText(this,
                    "Bienvenido " + usuario,
                    Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else {
            Toast.makeText(this,
                    "Usuario o contraseña incorrectos",
                    Toast.LENGTH_SHORT).show();
        }
    }
}