package com.example.poyecto;

import java.util.UUID;

public class Reserva {
    private String id, clienteNombre, itinerarioNombre, fecha;

    public Reserva(String clienteNombre, String itinerarioNombre, String fecha) {
        this.id = UUID.randomUUID().toString();
        this.clienteNombre = clienteNombre;
        this.itinerarioNombre = itinerarioNombre;
        this.fecha = fecha;
    }

    public String getId() { return id; }
    // Necesario para restaurar el id al leer desde SQLite
    public void setId(String id) { this.id = id; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getItinerarioNombre() { return itinerarioNombre; }
    public void setItinerarioNombre(String itinerarioNombre) { this.itinerarioNombre = itinerarioNombre; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}