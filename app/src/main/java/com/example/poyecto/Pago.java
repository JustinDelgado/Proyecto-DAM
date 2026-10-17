package com.example.poyecto;

import java.util.UUID;

public class Pago {
    private String id, clienteNombre, cedula, provincia, lugar, personas, tarjeta, monto, fecha;

    public Pago(String cliente, String cedula, String provincia, String lugar,
                String personas, String tarjeta, String monto, String fecha) {
        this.id = UUID.randomUUID().toString();
        this.clienteNombre = cliente; this.cedula = cedula; this.provincia = provincia;
        this.lugar = lugar; this.personas = personas; this.tarjeta = tarjeta;
        this.monto = monto; this.fecha = fecha;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String v) { clienteNombre = v; }
    public String getCedula() { return cedula; }
    public void setCedula(String v) { cedula = v; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String v) { provincia = v; }
    public String getLugar() { return lugar; }
    public void setLugar(String v) { lugar = v; }
    public String getPersonas() { return personas; }
    public void setPersonas(String v) { personas = v; }
    public String getTarjeta() { return tarjeta; }
    public void setTarjeta(String v) { tarjeta = v; }
    public String getMonto() { return monto; }
    public void setMonto(String v) { monto = v; }
    public String getFecha() { return fecha; }
    public void setFecha(String v) { fecha = v; }
}