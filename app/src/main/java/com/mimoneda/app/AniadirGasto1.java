package com.mimoneda.app;

public class AniadirGasto1 extends BaseGastoActivity {

    @Override
    protected int obtenerLayout() {
        return R.layout.activity_aniadir_gasto1;
    }

    @Override
    protected String obtenerCategoriaInicial() {
        return "Casa";
    }
}
