package org.dantesys.reliquiasNexus.bosses;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;

public enum BossRarity {
    COMMUN(NamedTextColor.GRAY, "Comum", 50, 0.5, 0.5, Color.GRAY),
    INCOMMON(NamedTextColor.GREEN, "Incomum", 30, 0.75, 0.6, Color.LIME),
    RARE(NamedTextColor.AQUA, "Raro", 15, 1.0, 0.75, Color.AQUA),
    EPIC(NamedTextColor.LIGHT_PURPLE, "Épico", 4, 1.5, 0.9, Color.PURPLE),
    LEGENDARY(NamedTextColor.GOLD, "Lendário", 1, 1.5, 1.0, Color.YELLOW);

    public final NamedTextColor color;
    public final String displayName;
    public final int weight;
    public final double healthMultiplier;
    public final double damageMultiplier;
    public final Color armorColor;

    BossRarity(NamedTextColor color, String displayName, int weight, double healthMultiplier, double damageMultiplier, Color armorColor) {
        this.color = color;
        this.displayName = displayName;
        this.weight = weight;
        this.healthMultiplier = healthMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.armorColor = armorColor;
    }

    public static BossRarity fromString(String name) {
        for (BossRarity rarity : values()) {
            if (rarity.name().equalsIgnoreCase(name)) {
                return rarity;
            }
        }
        return null;
    }
}