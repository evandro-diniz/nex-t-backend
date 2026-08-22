package com.nex_t.entity.enums;

public enum Impacto {
    ALTO(3),
    MEDIO(2),
    BAIXO(1);

    private final int pontos;

    Impacto(int pontos) {
        this.pontos = pontos;
    }

    public int getPontos() {
        return pontos;
    }
}
