package com.example.poyecto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLite extends SQLiteOpenHelper {

    private static final String DB_NAME    = "turismo.db";
    private static final int    DB_VERSION = 2;

    // ══════════════════════════════════════════════════════════════════════════
    // TABLA: clientes
    // ══════════════════════════════════════════════════════════════════════════
    public static final String TABLE_CLIENTES  = "clientes";
    public static final String CLI_ID          = "id";
    public static final String CLI_NOMBRE      = "nombre";
    public static final String CLI_APELLIDO    = "apellido";
    public static final String CLI_CEDULA      = "cedula";
    public static final String CLI_EDAD        = "edad";
    public static final String CLI_CORREO      = "correo";
    public static final String CLI_TELEFONO    = "telefono";
    public static final String CLI_TIPO        = "tipo";

    private static final String CREATE_CLIENTES =
            "CREATE TABLE " + TABLE_CLIENTES + " (" +
                    CLI_ID       + " TEXT PRIMARY KEY, " +
                    CLI_NOMBRE   + " TEXT NOT NULL, " +
                    CLI_APELLIDO + " TEXT, " +
                    CLI_CEDULA   + " TEXT, " +
                    CLI_EDAD     + " TEXT, " +
                    CLI_CORREO   + " TEXT, " +
                    CLI_TELEFONO + " TEXT, " +
                    CLI_TIPO     + " TEXT" +
                    ");";

    // ══════════════════════════════════════════════════════════════════════════
    // TABLA: lugares
    // ══════════════════════════════════════════════════════════════════════════
    public static final String TABLE_LUGARES   = "lugares";
    public static final String LUG_ID         = "id";
    public static final String LUG_NOMBRE     = "nombre";
    public static final String LUG_PROVINCIA  = "provincia";
    public static final String LUG_COSTO      = "costo";

    private static final String CREATE_LUGARES =
            "CREATE TABLE " + TABLE_LUGARES + " (" +
                    LUG_ID        + " TEXT PRIMARY KEY, " +
                    LUG_NOMBRE    + " TEXT NOT NULL, " +
                    LUG_PROVINCIA + " TEXT, " +
                    LUG_COSTO     + " TEXT" +
                    ");";

    // ══════════════════════════════════════════════════════════════════════════
    // TABLA: itinerarios
    // ══════════════════════════════════════════════════════════════════════════
    public static final String TABLE_ITINERARIOS = "itinerarios";
    public static final String ITI_ID            = "id";
    public static final String ITI_NOMBRE        = "nombre";
    public static final String ITI_DIAS          = "dias";
    public static final String ITI_PRECIO        = "precio";

    private static final String CREATE_ITINERARIOS =
            "CREATE TABLE " + TABLE_ITINERARIOS + " (" +
                    ITI_ID     + " TEXT PRIMARY KEY, " +
                    ITI_NOMBRE + " TEXT NOT NULL, " +
                    ITI_DIAS   + " TEXT, " +
                    ITI_PRECIO + " TEXT" +
                    ");";

    // ══════════════════════════════════════════════════════════════════════════
    // TABLA: reservas
    // ══════════════════════════════════════════════════════════════════════════
    public static final String TABLE_RESERVAS        = "reservas";
    public static final String RES_ID                = "id";
    public static final String RES_CLIENTE_NOMBRE    = "cliente_nombre";
    public static final String RES_ITINERARIO_NOMBRE = "itinerario_nombre";
    public static final String RES_FECHA             = "fecha";

    private static final String CREATE_RESERVAS =
            "CREATE TABLE " + TABLE_RESERVAS + " (" +
                    RES_ID                + " TEXT PRIMARY KEY, " +
                    RES_CLIENTE_NOMBRE    + " TEXT NOT NULL, " +
                    RES_ITINERARIO_NOMBRE + " TEXT NOT NULL, " +
                    RES_FECHA             + " TEXT NOT NULL" +
                    ");";

    // ══════════════════════════════════════════════════════════════════════════
    // TABLA: pagos
    // ══════════════════════════════════════════════════════════════════════════
    public static final String TABLE_PAGOS   = "pagos";
    public static final String PAG_ID        = "id";
    public static final String PAG_CLIENTE   = "cliente_nombre";
    public static final String PAG_CEDULA    = "cedula";
    public static final String PAG_PROVINCIA = "provincia";
    public static final String PAG_LUGAR     = "lugar";
    public static final String PAG_PERSONAS  = "personas";
    public static final String PAG_TARJETA   = "tarjeta";
    public static final String PAG_MONTO     = "monto";
    public static final String PAG_FECHA     = "fecha";

    private static final String CREATE_PAGOS =
            "CREATE TABLE " + TABLE_PAGOS + " (" +
                    PAG_ID        + " TEXT PRIMARY KEY, " +
                    PAG_CLIENTE   + " TEXT, " +
                    PAG_CEDULA    + " TEXT, " +
                    PAG_PROVINCIA + " TEXT, " +
                    PAG_LUGAR     + " TEXT, " +
                    PAG_PERSONAS  + " TEXT, " +
                    PAG_TARJETA   + " TEXT, " +
                    PAG_MONTO     + " TEXT, " +
                    PAG_FECHA     + " TEXT" +
                    ");";

    // ══════════════════════════════════════════════════════════════════════════
    // Singleton
    // ══════════════════════════════════════════════════════════════════════════
    private static SQLite instance;

    public static synchronized SQLite getInstance(Context context) {
        if (instance == null) {
            instance = new SQLite(context.getApplicationContext());
        }
        return instance;
    }

    private SQLite(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLIENTES);
        db.execSQL(CREATE_LUGARES);
        db.execSQL(CREATE_ITINERARIOS);
        db.execSQL(CREATE_RESERVAS);
        db.execSQL(CREATE_PAGOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LUGARES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINERARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGOS);
        onCreate(db);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CRUD — CLIENTES
    // ══════════════════════════════════════════════════════════════════════════

    public boolean insertarCliente(Cliente c) {
        SQLiteDatabase db = getWritableDatabase();
        long r = db.insert(TABLE_CLIENTES, null, clienteToCV(c));
        db.close(); return r != -1;
    }

    public List<Cliente> obtenerClientes() {
        List<Cliente> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_CLIENTES, null, null, null, null, null, CLI_NOMBRE + " ASC");
        if (cur.moveToFirst()) do { lista.add(cursorToCliente(cur)); } while (cur.moveToNext());
        cur.close(); db.close(); return lista;
    }

    public boolean actualizarCliente(Cliente c) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.update(TABLE_CLIENTES, clienteToCV(c), CLI_ID + "=?", new String[]{c.getId()});
        db.close(); return rows > 0;
    }

    public boolean eliminarCliente(String id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_CLIENTES, CLI_ID + "=?", new String[]{id});
        db.close(); return rows > 0;
    }

    private ContentValues clienteToCV(Cliente c) {
        ContentValues cv = new ContentValues();
        cv.put(CLI_ID, c.getId()); cv.put(CLI_NOMBRE, c.getNombre());
        cv.put(CLI_APELLIDO, c.getApellido()); cv.put(CLI_CEDULA, c.getCedula());
        cv.put(CLI_EDAD, c.getEdad()); cv.put(CLI_CORREO, c.getCorreo());
        cv.put(CLI_TELEFONO, c.getTelefono()); cv.put(CLI_TIPO, c.getTipo());
        return cv;
    }

    private Cliente cursorToCliente(Cursor cur) {
        Cliente c = new Cliente(
                cur.getString(cur.getColumnIndexOrThrow(CLI_NOMBRE)),
                cur.getString(cur.getColumnIndexOrThrow(CLI_APELLIDO)),
                cur.getString(cur.getColumnIndexOrThrow(CLI_CEDULA)),
                cur.getString(cur.getColumnIndexOrThrow(CLI_EDAD)),
                cur.getString(cur.getColumnIndexOrThrow(CLI_CORREO)),
                cur.getString(cur.getColumnIndexOrThrow(CLI_TELEFONO)),
                cur.getString(cur.getColumnIndexOrThrow(CLI_TIPO))
        );
        c.setId(cur.getString(cur.getColumnIndexOrThrow(CLI_ID)));
        return c;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CRUD — LUGARES
    // ══════════════════════════════════════════════════════════════════════════

    public boolean insertarLugar(Lugar l) {
        SQLiteDatabase db = getWritableDatabase();
        long r = db.insert(TABLE_LUGARES, null, lugarToCV(l));
        db.close(); return r != -1;
    }

    public List<Lugar> obtenerLugares() {
        List<Lugar> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_LUGARES, null, null, null, null, null, LUG_NOMBRE + " ASC");
        if (cur.moveToFirst()) do { lista.add(cursorToLugar(cur)); } while (cur.moveToNext());
        cur.close(); db.close(); return lista;
    }

    public boolean actualizarLugar(Lugar l) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.update(TABLE_LUGARES, lugarToCV(l), LUG_ID + "=?", new String[]{l.getId()});
        db.close(); return rows > 0;
    }

    public boolean eliminarLugar(String id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_LUGARES, LUG_ID + "=?", new String[]{id});
        db.close(); return rows > 0;
    }

    private ContentValues lugarToCV(Lugar l) {
        ContentValues cv = new ContentValues();
        cv.put(LUG_ID, l.getId()); cv.put(LUG_NOMBRE, l.getNombre());
        cv.put(LUG_PROVINCIA, l.getProvincia()); cv.put(LUG_COSTO, l.getCosto());
        return cv;
    }

    private Lugar cursorToLugar(Cursor cur) {
        Lugar l = new Lugar(
                cur.getString(cur.getColumnIndexOrThrow(LUG_NOMBRE)),
                cur.getString(cur.getColumnIndexOrThrow(LUG_PROVINCIA)),
                cur.getString(cur.getColumnIndexOrThrow(LUG_COSTO))
        );
        l.setId(cur.getString(cur.getColumnIndexOrThrow(LUG_ID)));
        return l;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CRUD — ITINERARIOS
    // ══════════════════════════════════════════════════════════════════════════

    public boolean insertarItinerario(Itinerario i) {
        SQLiteDatabase db = getWritableDatabase();
        long r = db.insert(TABLE_ITINERARIOS, null, itinerarioToCV(i));
        db.close(); return r != -1;
    }

    public List<Itinerario> obtenerItinerarios() {
        List<Itinerario> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_ITINERARIOS, null, null, null, null, null, ITI_NOMBRE + " ASC");
        if (cur.moveToFirst()) do { lista.add(cursorToItinerario(cur)); } while (cur.moveToNext());
        cur.close(); db.close(); return lista;
    }

    public boolean actualizarItinerario(Itinerario i) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.update(TABLE_ITINERARIOS, itinerarioToCV(i), ITI_ID + "=?", new String[]{i.getId()});
        db.close(); return rows > 0;
    }

    public boolean eliminarItinerario(String id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_ITINERARIOS, ITI_ID + "=?", new String[]{id});
        db.close(); return rows > 0;
    }

    private ContentValues itinerarioToCV(Itinerario i) {
        ContentValues cv = new ContentValues();
        cv.put(ITI_ID, i.getId()); cv.put(ITI_NOMBRE, i.getNombre());
        cv.put(ITI_DIAS, i.getDias()); cv.put(ITI_PRECIO, i.getPrecio());
        return cv;
    }

    private Itinerario cursorToItinerario(Cursor cur) {
        Itinerario i = new Itinerario(
                cur.getString(cur.getColumnIndexOrThrow(ITI_NOMBRE)),
                cur.getString(cur.getColumnIndexOrThrow(ITI_DIAS)),
                cur.getString(cur.getColumnIndexOrThrow(ITI_PRECIO))
        );
        i.setId(cur.getString(cur.getColumnIndexOrThrow(ITI_ID)));
        return i;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CRUD — RESERVAS
    // ══════════════════════════════════════════════════════════════════════════

    public boolean insertarReserva(Reserva res) {
        SQLiteDatabase db = getWritableDatabase();
        long r = db.insert(TABLE_RESERVAS, null, reservaToCV(res));
        db.close(); return r != -1;
    }

    public List<Reserva> obtenerReservas() {
        List<Reserva> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_RESERVAS, null, null, null, null, null, RES_FECHA + " ASC");
        if (cur.moveToFirst()) do { lista.add(cursorToReserva(cur)); } while (cur.moveToNext());
        cur.close(); db.close(); return lista;
    }

    public boolean actualizarReserva(Reserva res) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.update(TABLE_RESERVAS, reservaToCV(res), RES_ID + "=?", new String[]{res.getId()});
        db.close(); return rows > 0;
    }

    public boolean eliminarReserva(String id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_RESERVAS, RES_ID + "=?", new String[]{id});
        db.close(); return rows > 0;
    }

    private ContentValues reservaToCV(Reserva r) {
        ContentValues cv = new ContentValues();
        cv.put(RES_ID, r.getId()); cv.put(RES_CLIENTE_NOMBRE, r.getClienteNombre());
        cv.put(RES_ITINERARIO_NOMBRE, r.getItinerarioNombre()); cv.put(RES_FECHA, r.getFecha());
        return cv;
    }

    private Reserva cursorToReserva(Cursor cur) {
        Reserva r = new Reserva(
                cur.getString(cur.getColumnIndexOrThrow(RES_CLIENTE_NOMBRE)),
                cur.getString(cur.getColumnIndexOrThrow(RES_ITINERARIO_NOMBRE)),
                cur.getString(cur.getColumnIndexOrThrow(RES_FECHA))
        );
        r.setId(cur.getString(cur.getColumnIndexOrThrow(RES_ID)));
        return r;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CRUD — PAGOS
    // ══════════════════════════════════════════════════════════════════════════

    public boolean insertarPago(Pago p) {
        SQLiteDatabase db = getWritableDatabase();
        long r = db.insert(TABLE_PAGOS, null, pagoToCV(p));
        db.close(); return r != -1;
    }

    public List<Pago> obtenerPagos() {
        List<Pago> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_PAGOS, null, null, null, null, null, PAG_FECHA + " DESC");
        if (cur.moveToFirst()) do { lista.add(cursorToPago(cur)); } while (cur.moveToNext());
        cur.close(); db.close(); return lista;
    }

    public boolean actualizarPago(Pago p) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.update(TABLE_PAGOS, pagoToCV(p), PAG_ID + "=?", new String[]{p.getId()});
        db.close(); return rows > 0;
    }

    public boolean eliminarPago(String id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_PAGOS, PAG_ID + "=?", new String[]{id});
        db.close(); return rows > 0;
    }

    private ContentValues pagoToCV(Pago p) {
        ContentValues cv = new ContentValues();
        cv.put(PAG_ID, p.getId()); cv.put(PAG_CLIENTE, p.getClienteNombre());
        cv.put(PAG_CEDULA, p.getCedula()); cv.put(PAG_PROVINCIA, p.getProvincia());
        cv.put(PAG_LUGAR, p.getLugar()); cv.put(PAG_PERSONAS, p.getPersonas());
        cv.put(PAG_TARJETA, p.getTarjeta()); cv.put(PAG_MONTO, p.getMonto());
        cv.put(PAG_FECHA, p.getFecha());
        return cv;
    }

    private Pago cursorToPago(Cursor cur) {
        Pago p = new Pago(
                cur.getString(cur.getColumnIndexOrThrow(PAG_CLIENTE)),
                cur.getString(cur.getColumnIndexOrThrow(PAG_CEDULA)),
                cur.getString(cur.getColumnIndexOrThrow(PAG_PROVINCIA)),
                cur.getString(cur.getColumnIndexOrThrow(PAG_LUGAR)),
                cur.getString(cur.getColumnIndexOrThrow(PAG_PERSONAS)),
                cur.getString(cur.getColumnIndexOrThrow(PAG_TARJETA)),
                cur.getString(cur.getColumnIndexOrThrow(PAG_MONTO)),
                cur.getString(cur.getColumnIndexOrThrow(PAG_FECHA))
        );
        p.setId(cur.getString(cur.getColumnIndexOrThrow(PAG_ID)));
        return p;
    }
}