package org.dantesys.reliquiasNexus.eventos;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

public class ChatListener implements Listener {

    private final ReliquiasNexus plugin;

    public ChatListener(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String rank = plugin.getConfig().getString("players." + player.getUniqueId().toString() + ".rank", "membro");
        Component prefix;

        switch (rank.toLowerCase()) {
            case "dono":
                prefix = Component.text("[Dono] ").color(NamedTextColor.RED);
                break;
            case "staff":
                prefix = Component.text("[Staff] ").color(NamedTextColor.AQUA);
                break;
            case "ajudante":
                prefix = Component.text("[Ajudante] ").color(NamedTextColor.GREEN);
                break;
            case "membro":
            default:
                prefix = Component.text("[Membro] ").color(NamedTextColor.GRAY);
                break;
        }

        // Formata a mensagem com a tag e o nome do jogador
        Component finalMessage = prefix
                .append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                .append(Component.text(": ").color(NamedTextColor.GRAY))
                .append(event.message());

        event.renderer((source, name, message, audience) -> finalMessage);
    }
}