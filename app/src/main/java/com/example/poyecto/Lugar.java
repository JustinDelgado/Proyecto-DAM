package com.example.poyecto;

import java.util.UUID;

public class Lugar {
    private String id, nombre, provincia, costo;

    public Lugar(String nombre, String provincia, String costo) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre; this.provincia = provincia; this.costo = costo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String n) { nombre = n; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String p) { provincia = p; }
    public String getCosto() { return costo; }
    public void setCosto(String c) { costo = c; }

    @Override
    public String toString() { return nombre; }
}