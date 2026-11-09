package com.mimoneda.app.model;

public class Movimiento {

    private final long id;
    private final String tipo;
    private final long importeCentimos;
    private final String concepto;
    private final String fecha;
    private final String categoria;

    public Movimiento(
            long id,
            String tipo,
            long importeCentimos,
            String concepto,
            String fecha,
            String categoria
    ) {
        this.id = id;
        this.tipo = tipo;
        this.importeCentimos = importeCentimos;
        this.concepto = concepto;
        this.fecha = fecha;
        this.categoria = categoria;
    }

    public long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public long getImporteCentimos() {
        return importeCentimos;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getFecha() {
        return fecha;
    }

    public String getCategoria() {
        return categoria;
    }
}
