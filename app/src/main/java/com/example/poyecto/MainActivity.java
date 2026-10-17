package com.example.poyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DataStore.getInstance().syncFromDatabase(SQLite.getInstance(this));

        // 1. Configurar la Barra Superior (Toolbar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Empresa Turística");
        setSupportActionBar(toolbar);

        // 2. Inicializar el Menú Lateral y el Panel de Navegación
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 3. Configurar el botón (☰) sin textos para evitar errores
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 4. Cargar el Fragmento de Inicio por defecto al abrir la App
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new InicioFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_inicio);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            cerrarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int id = item.getItemId();

        // Lógica de navegación para cada módulo
        if (id == R.id.nav_inicio) {
            selectedFragment = new InicioFragment();
        } else if (id == R.id.nav_clientes) {
            selectedFragment = new ClientesFragment();
        } else if (id == R.id.nav_lugares) {
            selectedFragment = new LugaresFragment();
        } else if (id == R.id.nav_itinerarios) {
            selectedFragment = new ItinerariosFragment();
        } else if (id == R.id.nav_reservas) {
            selectedFragment = new ReservasFragment();
        } else if (id == R.id.nav_pagos) {
            selectedFragment = new PagosFragment();
        } else if (id == R.id.nav_acerca_de) {
            selectedFragment = new AcercaDeFragment();
        }

        // Reemplazar el contenido actual por el fragmento seleccionado
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, selectedFragment)
                    .commit();
        }

        // Cerrar el menú después de seleccionar una opción
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Si el menú está abierto al presionar atrás, cerrarlo en lugar de salir de la app
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void cerrarSesion() {
        // 1. Limpiar datos de sesión en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // 2. Limpiar datos en memoria (DataStore)
        DataStore.getInstance().logout();

        // 3. Mostrar mensaje
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        // 4. Navegar al Login (sin cerrar la app)
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // 5. Cerrar MainActivity para que no quede en el stack
        finish();
    }
}