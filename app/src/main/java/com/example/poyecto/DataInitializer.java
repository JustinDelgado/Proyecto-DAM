package com.example.poyecto;

import java.util.List;

public class DataInitializer {
    public static void initialize() {
        List<Cliente> clientes = DataStore.getInstance().getClientes();
        if (clientes.isEmpty()) {
            clientes.add(new Cliente("Juan", "Pérez", "0102030405", "25", "juan.perez@gmail.com", "0998765432", "Nacional"));
            clientes.add(new Cliente("María", "Gómez", "0912345678", "30", "maria.gomez@gmail.com", "0987654321", "Extranjero"));
            clientes.add(new Cliente("Carlos", "Mendoza", "0922334455", "40", "carlos.mendoza@gmail.com", "0971122334", "Nacional"));
            clientes.add(new Cliente("Jesus", "Merchan", "0950963967", "25", "jesus_mjr@outlook.com", "0959417898", "Extranjero"));
        }
    }
}
