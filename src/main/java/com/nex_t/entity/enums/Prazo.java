package com.nex_t.entity.enums;

public enum Prazo {
    HOJE(3),
    AMANHA(2),
    SEMANA_MAIS(1);

    private final int pontos;

    Prazo(int pontos) {
        this.pontos = pontos;
    }

    public int getPontos() {
        return pontos;
    }
}
