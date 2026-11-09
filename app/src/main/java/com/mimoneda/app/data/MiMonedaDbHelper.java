package com.mimoneda.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mimoneda.app.model.Movimiento;
import com.mimoneda.app.util.PasswordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MiMonedaDbHelper extends SQLiteOpenHelper {

    public static final String TIPO_INGRESO = "INGRESO";
    public static final String TIPO_GASTO = "GASTO";

    private static final String NOMBRE_BASE_DATOS = "mi_moneda.db";
    private static final int VERSION_BASE_DATOS = 1;

    private static final String TABLA_USUARIOS = "usuarios";
    private static final String TABLA_CUENTAS = "cuentas";
    private static final String TABLA_CATEGORIAS = "categorias";
    private static final String TABLA_MOVIMIENTOS = "movimientos";

    private static volatile MiMonedaDbHelper instancia;

    public static MiMonedaDbHelper obtenerInstancia(Context context) {
        if (instancia == null) {
            synchronized (MiMonedaDbHelper.class) {
                if (instancia == null) {
                    instancia = new MiMonedaDbHelper(context.getApplicationContext());
                }
            }
        }
        return instancia;
    }

    private MiMonedaDbHelper(Context context) {
        super(context, NOMBRE_BASE_DATOS, null, VERSION_BASE_DATOS);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLA_USUARIOS + " (" +
                        "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nombre TEXT NOT NULL, " +
                        "correo TEXT NOT NULL COLLATE NOCASE UNIQUE, " +
                        "password_hash TEXT NOT NULL, " +
                        "password_salt TEXT NOT NULL, " +
                        "fecha_creacion INTEGER NOT NULL" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLA_CUENTAS + " (" +
                        "id_cuenta INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_usuario INTEGER NOT NULL, " +
                        "nombre TEXT NOT NULL, " +
                        "moneda TEXT NOT NULL DEFAULT 'EUR', " +
                        "saldo_inicial_centimos INTEGER NOT NULL DEFAULT 0, " +
                        "activa INTEGER NOT NULL DEFAULT 1 CHECK (activa IN (0, 1)), " +
                        "FOREIGN KEY (id_usuario) REFERENCES " + TABLA_USUARIOS +
                        "(id_usuario) ON DELETE CASCADE, " +
                        "UNIQUE (id_usuario, nombre)" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLA_CATEGORIAS + " (" +
                        "id_categoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nombre TEXT NOT NULL, " +
                        "tipo TEXT NOT NULL CHECK (tipo IN ('INGRESO', 'GASTO')), " +
                        "orden INTEGER NOT NULL DEFAULT 0, " +
                        "UNIQUE (nombre, tipo)" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLA_MOVIMIENTOS + " (" +
                        "id_movimiento INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_cuenta INTEGER NOT NULL, " +
                        "id_categoria INTEGER NOT NULL, " +
                        "tipo TEXT NOT NULL CHECK (tipo IN ('INGRESO', 'GASTO')), " +
                        "importe_centimos INTEGER NOT NULL CHECK (importe_centimos > 0), " +
                        "concepto TEXT NOT NULL, " +
                        "fecha TEXT NOT NULL, " +
                        "fecha_creacion INTEGER NOT NULL, " +
                        "FOREIGN KEY (id_cuenta) REFERENCES " + TABLA_CUENTAS +
                        "(id_cuenta) ON DELETE CASCADE, " +
                        "FOREIGN KEY (id_categoria) REFERENCES " + TABLA_CATEGORIAS +
                        "(id_categoria) ON DELETE RESTRICT" +
                        ")"
        );

        db.execSQL(
                "CREATE INDEX idx_movimientos_cuenta_fecha ON " + TABLA_MOVIMIENTOS +
                        " (id_cuenta, fecha DESC)"
        );
        db.execSQL(
                "CREATE INDEX idx_movimientos_categoria ON " + TABLA_MOVIMIENTOS +
                        " (id_categoria)"
        );

        insertarCategoriasIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        // La versión inicial no necesita migraciones. Las futuras versiones deben
        // añadir aquí sus ALTER TABLE para no perder los datos del usuario.
    }

    private void insertarCategoriasIniciales(SQLiteDatabase db) {
        insertarCategoria(db, "Casa", TIPO_GASTO, 1);
        insertarCategoria(db, "Compras", TIPO_GASTO, 2);
        insertarCategoria(db, "Familia", TIPO_GASTO, 3);
        insertarCategoria(db, "Transporte", TIPO_GASTO, 4);
        insertarCategoria(db, "Ocio", TIPO_GASTO, 5);
        insertarCategoria(db, "Otros", TIPO_GASTO, 6);
        insertarCategoria(db, "Ingresos", TIPO_INGRESO, 1);
    }

    private void insertarCategoria(
            SQLiteDatabase db,
            String nombre,
            String tipo,
            int orden
    ) {
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("tipo", tipo);
        valores.put("orden", orden);
        db.insertOrThrow(TABLA_CATEGORIAS, null, valores);
    }

    public long registrarUsuario(String nombre, String correo, String password) {
        SQLiteDatabase db = getWritableDatabase();
        PasswordUtils.Credenciales credenciales =
                PasswordUtils.crearCredenciales(password);
        long idUsuario = -1L;

        db.beginTransaction();
        try {
            ContentValues usuario = new ContentValues();
            usuario.put("nombre", nombre.trim());
            usuario.put("correo", normalizarCorreo(correo));
            usuario.put("password_hash", credenciales.getHash());
            usuario.put("password_salt", credenciales.getSalt());
            usuario.put("fecha_creacion", System.currentTimeMillis());

            idUsuario = db.insertOrThrow(TABLA_USUARIOS, null, usuario);

            ContentValues cuenta = new ContentValues();
            cuenta.put("id_usuario", idUsuario);
            cuenta.put("nombre", "Cuenta principal");
            cuenta.put("moneda", "EUR");
            cuenta.put("saldo_inicial_centimos", 0);
            cuenta.put("activa", 1);
            db.insertOrThrow(TABLA_CUENTAS, null, cuenta);

            db.setTransactionSuccessful();
        } catch (SQLiteConstraintException excepcion) {
            idUsuario = -1L;
        } finally {
            db.endTransaction();
        }

        return idUsuario;
    }

    public long autenticarUsuario(String correo, String password) {
        String consulta =
                "SELECT id_usuario, password_hash, password_salt " +
                        "FROM " + TABLA_USUARIOS + " WHERE correo = ? LIMIT 1";

        try (Cursor cursor = getReadableDatabase().rawQuery(
                consulta,
                new String[]{normalizarCorreo(correo)}
        )) {
            if (!cursor.moveToFirst()) {
                return -1L;
            }

            long idUsuario = cursor.getLong(cursor.getColumnIndexOrThrow("id_usuario"));
            String hash = cursor.getString(cursor.getColumnIndexOrThrow("password_hash"));
            String salt = cursor.getString(cursor.getColumnIndexOrThrow("password_salt"));

            return PasswordUtils.verificar(password, hash, salt) ? idUsuario : -1L;
        }
    }

    public long obtenerCuentaPrincipal(long idUsuario) {
        String consulta =
                "SELECT id_cuenta FROM " + TABLA_CUENTAS +
                        " WHERE id_usuario = ? AND activa = 1 " +
                        "ORDER BY id_cuenta LIMIT 1";

        try (Cursor cursor = getReadableDatabase().rawQuery(
                consulta,
                new String[]{String.valueOf(idUsuario)}
        )) {
            return cursor.moveToFirst()
                    ? cursor.getLong(cursor.getColumnIndexOrThrow("id_cuenta"))
                    : -1L;
        }
    }

    public long insertarMovimiento(
            long idCuenta,
            String tipo,
            long importeCentimos,
            String concepto,
            String fechaIso,
            String categoria
    ) {
        if (importeCentimos <= 0) {
            return -1L;
        }

        SQLiteDatabase db = getWritableDatabase();
        long idCategoria = obtenerIdCategoria(db, categoria, tipo);
        if (idCategoria == -1L) {
            return -1L;
        }

        ContentValues movimiento = new ContentValues();
        movimiento.put("id_cuenta", idCuenta);
        movimiento.put("id_categoria", idCategoria);
        movimiento.put("tipo", tipo);
        movimiento.put("importe_centimos", importeCentimos);
        movimiento.put("concepto", concepto.trim());
        movimiento.put("fecha", fechaIso);
        movimiento.put("fecha_creacion", System.currentTimeMillis());

        return db.insert(TABLA_MOVIMIENTOS, null, movimiento);
    }

    public long obtenerSaldoCentimos(long idCuenta) {
        String consulta =
                "SELECT c.saldo_inicial_centimos + " +
                        "COALESCE(SUM(CASE WHEN m.tipo = ? " +
                        "THEN m.importe_centimos ELSE -m.importe_centimos END), 0) AS saldo " +
                        "FROM " + TABLA_CUENTAS + " c " +
                        "LEFT JOIN " + TABLA_MOVIMIENTOS + " m " +
                        "ON m.id_cuenta = c.id_cuenta " +
                        "WHERE c.id_cuenta = ? GROUP BY c.id_cuenta";

        try (Cursor cursor = getReadableDatabase().rawQuery(
                consulta,
                new String[]{TIPO_INGRESO, String.valueOf(idCuenta)}
        )) {
            return cursor.moveToFirst()
                    ? cursor.getLong(cursor.getColumnIndexOrThrow("saldo"))
                    : 0L;
        }
    }

    public List<Movimiento> obtenerUltimosMovimientos(long idCuenta, int limite) {
        List<Movimiento> movimientos = new ArrayList<>();
        String consulta =
                "SELECT m.id_movimiento, m.tipo, m.importe_centimos, " +
                        "m.concepto, m.fecha, c.nombre AS categoria " +
                        "FROM " + TABLA_MOVIMIENTOS + " m " +
                        "INNER JOIN " + TABLA_CATEGORIAS + " c " +
                        "ON c.id_categoria = m.id_categoria " +
                        "WHERE m.id_cuenta = ? " +
                        "ORDER BY m.fecha DESC, m.id_movimiento DESC LIMIT ?";

        try (Cursor cursor = getReadableDatabase().rawQuery(
                consulta,
                new String[]{String.valueOf(idCuenta), String.valueOf(limite)}
        )) {
            while (cursor.moveToNext()) {
                movimientos.add(new Movimiento(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id_movimiento")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("importe_centimos")),
                        cursor.getString(cursor.getColumnIndexOrThrow("concepto")),
                        cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                        cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                ));
            }
        }

        return movimientos;
    }

    private long obtenerIdCategoria(
            SQLiteDatabase db,
            String nombre,
            String tipo
    ) {
        String consulta =
                "SELECT id_categoria FROM " + TABLA_CATEGORIAS +
                        " WHERE nombre = ? AND tipo = ? LIMIT 1";

        try (Cursor cursor = db.rawQuery(consulta, new String[]{nombre, tipo})) {
            return cursor.moveToFirst()
                    ? cursor.getLong(cursor.getColumnIndexOrThrow("id_categoria"))
                    : -1L;
        }
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
