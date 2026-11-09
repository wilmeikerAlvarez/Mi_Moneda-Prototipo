package com.mimoneda.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SesionManager {

    private static final String PREFERENCIAS = "sesion_mi_moneda";
    private static final String CLAVE_USUARIO = "id_usuario";
    private static final String CLAVE_CUENTA = "id_cuenta";
    private static final long SIN_SESION = -1L;

    private final SharedPreferences preferencias;

    public SesionManager(Context context) {
        preferencias = context.getApplicationContext()
                .getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
    }

    public void guardarSesion(long idUsuario, long idCuenta) {
        preferencias.edit()
                .putLong(CLAVE_USUARIO, idUsuario)
                .putLong(CLAVE_CUENTA, idCuenta)
                .apply();
    }

    public long obtenerIdUsuario() {
        return preferencias.getLong(CLAVE_USUARIO, SIN_SESION);
    }

    public long obtenerIdCuenta() {
        return preferencias.getLong(CLAVE_CUENTA, SIN_SESION);
    }

    public boolean haySesion() {
        return obtenerIdUsuario() != SIN_SESION && obtenerIdCuenta() != SIN_SESION;
    }

    public void cerrarSesion() {
        preferencias.edit().clear().apply();
    }
}
