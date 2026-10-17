package com.example.poyecto;

import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private static DataStore instance;

    private List<Cliente> clientes = new ArrayList<>();
    private List<Lugar> lugares = new ArrayList<>();
    private List<Itinerario> itinerarios = new ArrayList<>();
    private List<Reserva> reservas = new ArrayList<>();
    private List<Pago> pagos = new ArrayList<>();

    private String loggedUser;
    private boolean isLoggedIn;

    private Cliente clienteActual;
    private Lugar lugarActual;
    private Itinerario itinerarioActual;
    private Reserva reservaActual;

    // 🔒 evita duplicación de datos
    private boolean initialized = false;

    private DataStore() {
        initSampleData();
    }

    // =========================
    // DATOS DE PRUEBA
    // =========================
    private void initSampleData() {
        if (initialized) return;

        itinerarios.add(new Itinerario("Aventura Galápagos", "5", "1200"));
        itinerarios.add(new Itinerario("Relajación Quito", "3", "450"));
        itinerarios.add(new Itinerario("Selva Amazónica", "7", "1800"));

        initialized = true;
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // =========================
    // CLIENTES
    // =========================
    public List<Cliente> getClientes() { return clientes; }
    public void setClientes(List<Cliente> clientes) { this.clientes = clientes; }
    public void addCliente(Cliente c) { this.clientes.add(c); }

    // =========================
    // LUGARES
    // =========================
    public List<Lugar> getLugares() { return lugares; }
    public void setLugares(List<Lugar> lugares) { this.lugares = lugares; }
    public void addLugar(Lugar l) { this.lugares.add(l); }

    // =========================
    // ITINERARIOS
    // =========================
    public List<Itinerario> getItinerarios() { return itinerarios; }
    public void setItinerarios(List<Itinerario> itinerarios) { this.itinerarios = itinerarios; }
    public void addItinerario(Itinerario i) { this.itinerarios.add(i); }

    // =========================
    // RESERVAS
    // =========================
    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
    public void addReserva(Reserva r) { this.reservas.add(r); }

    // =========================
    // PAGOS
    // =========================
    public List<Pago> getPagos() { return pagos; }
    public void setPagos(List<Pago> pagos) { this.pagos = pagos; }
    public void addPago(Pago p) { this.pagos.add(p); }

    // =========================
    // LOGIN
    // =========================
    public String getLoggedUser() { return loggedUser; }

    public void setLoggedUser(String user) {
        this.loggedUser = user;
        this.isLoggedIn = true;
    }

    public boolean isLoggedIn() { return isLoggedIn; }

    public void logout() {
        this.loggedUser = null;
        this.isLoggedIn = false;
        resetFlujo();
    }

    // =========================
    // FLUJO DE APP
    // =========================
    public Cliente getClienteActual() { return clienteActual; }

    public void setClienteActual(Cliente c) {
        this.clienteActual = c;
        this.lugarActual = null;
        this.itinerarioActual = null;
        this.reservaActual = null;
    }

    public Lugar getLugarActual() { return lugarActual; }

    public void setLugarActual(Lugar l) {
        this.lugarActual = l;
        this.itinerarioActual = null;
        this.reservaActual = null;
    }

    public Itinerario getItinerarioActual() { return itinerarioActual; }

    public void setItinerarioActual(Itinerario i) {
        this.itinerarioActual = i;
        this.reservaActual = null;
    }

    public Reserva getReservaActual() { return reservaActual; }
    public void setReservaActual(Reserva r) { this.reservaActual = r; }

    // =========================
    // RESET GENERAL
    // =========================
    public void resetFlujo() {
        clienteActual = null;
        lugarActual = null;
        itinerarioActual = null;
        reservaActual = null;
    }

    // =========================
    // SINCRONIZACIÓN BD
    // =========================
    public void syncFromDatabase(SQLite dbHelper) {
        this.clientes = dbHelper.obtenerClientes();
        this.lugares = dbHelper.obtenerLugares();
        this.itinerarios = dbHelper.obtenerItinerarios();
        this.reservas = dbHelper.obtenerReservas();
        this.pagos = dbHelper.obtenerPagos();
    }
}