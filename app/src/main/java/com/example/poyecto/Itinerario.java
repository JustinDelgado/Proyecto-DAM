package com.example.poyecto;

import java.util.UUID;

public class Itinerario {
    private String id, nombre, dias, precio;

    public Itinerario(String nombre, String dias, String precio) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre; this.dias = dias; this.precio = precio;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDias() { return dias; }
    public void setDias(String dias) { this.dias = dias; }
    public String getPrecio() { return precio; }
    public void setPrecio(String precio) { this.precio = precio; }

    @Override
    public String toString() { return nombre; }
}