package org.dantesys.reliquiasNexus;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;

import java.util.UUID;

public final class ReliquiasNexus extends JavaPlugin implements EventRegistrar {
    FileConfiguration config = getConfig();
    @Override
    public void onEnable() {
        getLogger().info("Registering Geyser event bus!");
        GeyserApi.api().eventBus().register(this, this);
        ItemsRegistro.init();
        config.createSection("nexus");
        config.options().copyDefaults(true);
        saveConfig();
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("nexus");
        root.then(Commands.literal("list").executes(ctx -> {
            ConfigurationSection secao = config.getConfigurationSection("nexus");
            if(secao!=null){
                for(String nexus: secao.getKeys(false)){
                    String uuidStr = config.getString("nexus."+nexus);
                    String dono = "§cSem dono";
                    if(uuidStr != null && !uuidStr.isBlank()){
                        try{
                            UUID uuid = UUID.fromString(uuidStr);
                            OfflinePlayer player = getServer().getOfflinePlayer(uuid);
                            dono = "§c"+(player.getName() != null? player.getName():"Desconhecido");
                        }catch(IllegalArgumentException ignored){
                            dono = "§cJogador Corrompido";
                        }
                    }
                    ctx.getSource().getSender().sendMessage(dono);
                }
            }else ctx.getSource().getSender().sendMessage("§cNão foi encontrado nenhuma reliquia Nexus");
            return Command.SINGLE_SUCCESS;
        }));
        LiteralCommandNode<CommandSourceStack> buildCommand = root.build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(buildCommand);
        });
        getServer().getConsoleSender().sendMessage("§2[Valent City]: Plugin Ativado!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().clearRecipes();
        getServer().getConsoleSender().sendMessage("§4[Valent City]: Plugin Desativado!");
    }
    @Subscribe
    public void onGeyserPostInitializeEvent(GeyserPostInitializeEvent event) {
        getLogger().info("Geyser started!");
    }
}
