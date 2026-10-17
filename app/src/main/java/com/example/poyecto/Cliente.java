package com.example.poyecto;

public class Cliente {
    private String id, nombre, apellido, cedula, edad, correo, telefono, tipo;

    public Cliente(String nombre, String apellido, String cedula, String edad, String correo, String telefono, String tipo) {
        this.id = java.util.UUID.randomUUID().toString();
        this.nombre = nombre; this.apellido = apellido; this.cedula = cedula;
        this.edad = edad; this.correo = correo; this.telefono = telefono; this.tipo = tipo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }   // necesario para restaurar desde SQLite
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getEdad() { return edad; }
    public void setEdad(String edad) { this.edad = edad; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getNombreCompleto() { return nombre + " " + apellido; }
}