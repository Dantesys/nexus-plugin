package org.dantesys.reliquiasNexus.util;

import java.util.UUID;

public record Troca(UUID offererUuid, String offeredRelicName) {
    private static String player2RelicName;

    public void setPlayer2Relic(String player2RelicName) {
        Troca.player2RelicName = player2RelicName;
    }

    public UUID uuid() {
        return offererUuid;
    }

    public String stack() {
        return offeredRelicName;
    }

    public String player2Relic() {
        return player2RelicName;
    }
}