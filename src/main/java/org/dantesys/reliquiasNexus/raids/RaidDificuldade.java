package org.dantesys.reliquiasNexus.raids;

import java.util.Random;

public enum RaidDificuldade {
    FACIL,
    MEDIO,
    DIFICIL,
    EXPERT,
    INSANO;

    private static final Random RANDOM = new Random();

    public static RaidDificuldade getRandom() {
        RaidDificuldade[] values = values();
        return values[RANDOM.nextInt(values.length)];
    }
}
