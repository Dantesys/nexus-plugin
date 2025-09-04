package org.dantesys.reliquiasNexus.bosses;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;

public enum BossRarity {
    COMMUN(NamedTextColor.GRAY, "Comum", 50, 1.0, 1.0, Color.GRAY, 1.0),
    INCOMMON(NamedTextColor.GREEN, "Incomum", 30, 1.5, 1.2, Color.LIME, 1.2),
    RARE(NamedTextColor.AQUA, "Raro", 15, 2.0, 1.5, Color.AQUA, 1.4),
    EPIC(NamedTextColor.LIGHT_PURPLE, "Épico", 4, 3.0, 1.8, Color.PURPLE, 1.6),
    LEGENDARY(NamedTextColor.GOLD, "Lendário", 1, 5.0, 2.0, Color.YELLOW, 1.8);

    public final NamedTextColor color;
    public final String displayName;
    public final int weight;
    public final double healthMultiplier;
    public final double damageMultiplier;
    public final Color armorColor;
    public final double speedMultiplier;

    BossRarity(NamedTextColor color, String displayName, int weight, double healthMultiplier, double damageMultiplier, Color armorColor, double speedMultiplier) {
        this.color = color;
        this.displayName = displayName;
        this.weight = weight;
        this.healthMultiplier = healthMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.armorColor = armorColor;
        this.speedMultiplier = speedMultiplier;
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