package org.dantesys.reliquiasNexus.util;

import org.bukkit.ChatColor;

public class TextFormatter {
    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String formatHeader() {
        return format("&6&lResourcePackNexus\n&7TPS: 29.9 | Pins: 1");
    }

    public static String formatFooter() {
        return format("&8----------------\n&ewww.meuserver.com");
    }

    public static String formatPlayerRank(String playerName, String rank) {
        String rankColor;
        switch (rank.toLowerCase()) {
            case "admin":
                rankColor = "&c";
                break;
            case "mod":
                rankColor = "&9";
                break;
            case "vip":
                rankColor = "&a";
                break;
            default:
                rankColor = "&7";
        }

        return format("&7[" + rankColor + rank + "&7] &f" + playerName);
    }
}