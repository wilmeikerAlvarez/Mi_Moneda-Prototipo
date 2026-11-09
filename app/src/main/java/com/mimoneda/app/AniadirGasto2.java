package com.mimoneda.app;

public class AniadirGasto2 extends BaseGastoActivity {

    @Override
    protected int obtenerLayout() {
        return R.layout.activity_aniadir_gasto2;
    }

    @Override
    protected String obtenerCategoriaInicial() {
        return "Compras";
    }
}
