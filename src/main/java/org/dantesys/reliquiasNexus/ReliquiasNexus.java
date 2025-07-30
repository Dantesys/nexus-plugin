package org.dantesys.reliquiasNexus;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.dantesys.reliquiasNexus.eventos.*;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.util.NexusKeys;

import java.util.List;
import java.util.UUID;

public final class ReliquiasNexus extends JavaPlugin {
    FileConfiguration config = getConfig();
    @Override
    public void onEnable() {
        ItemsRegistro.init();
        config.createSection("nexus");
        config.addDefault("nexus.guerreiro","");
        config.addDefault("nexus.ceifador","");
        config.options().copyDefaults(true);
        saveConfig();
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("nexus").executes(ctx -> {
            ctx.getSource().getSender().sendMessage("§rUsando Nexus");
            ctx.getSource().getSender().sendMessage("§r/nexus list");
            ctx.getSource().getSender().sendMessage("§r    -Mostrar quem está com qual reliquia");
            ctx.getSource().getSender().sendMessage("§r/nexus level");
            ctx.getSource().getSender().sendMessage("§r    -Mostrar seus niveis de reliquias");
            ctx.getSource().getSender().sendMessage("§r/nexus level <player>");
            ctx.getSource().getSender().sendMessage("§r    -Mostrar os niveis de reliquias de um jogador especifico");
            return Command.SINGLE_SUCCESS;
        });
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
            }else ctx.getSource().getSender().sendMessage("§cNão foi encontrado nenhum Nexus");
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal("level").executes(ctx -> {
            if(ctx.getSource().getExecutor() instanceof Player player){
                List<NamespacedKey> keys = NexusKeys.getKeyLevel();
                PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                player.sendMessage("§n§lSeus leveis de reliquias");
                for(NamespacedKey k:keys){
                    int l = dataPlayer.getOrDefault(k, PersistentDataType.INTEGER,0);
                    if(l>0){
                        player.sendMessage("§r§2"+k.namespace()+": "+l);
                    }else{
                        player.sendMessage("§r§2"+k.namespace()+": §r§cNunca usou");
                    }
                }
            }else ctx.getSource().getSender().sendMessage("§cApenas jogadores podem usar");
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal("level").then(Commands.argument("player",ArgumentTypes.player())).requires(sender -> sender.getSender().isOp()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
            final Player player = targetResolver.resolve(ctx.getSource()).getFirst();
            List<NamespacedKey> keys = NexusKeys.getKeyLevel();
            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
            player.sendMessage("§n§l"+player.getName()+" leveis de reliquias");
            for(NamespacedKey k:keys){
                int l = dataPlayer.getOrDefault(k, PersistentDataType.INTEGER,0);
                if(l>0){
                    player.sendMessage("§r§2"+k.namespace()+": "+l);
                }else{
                    player.sendMessage("§r§2"+k.namespace()+": §r§cNunca usou");
                }
            }
            return Command.SINGLE_SUCCESS;
        }));
        LiteralCommandNode<CommandSourceStack> buildCommand = root.build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(buildCommand));
        getServer().getPluginManager().registerEvents(new JoinQuit(), this);
        getServer().getPluginManager().registerEvents(new GuerreiroEvent(), this);
        getServer().getPluginManager().registerEvents(new LimitadorEvent(), this);
        getServer().getPluginManager().registerEvents(new PassivaEvent(), this);
        getServer().getPluginManager().registerEvents(new PerdeuReliquia(), this);
        getServer().getPluginManager().registerEvents(new CeifadorEvent(), this);
        getServer().getConsoleSender().sendMessage("§2[Nexus]: Plugin Ativado!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().clearRecipes();
        getServer().getConsoleSender().sendMessage("§4[Nexus]: Plugin Desativado!");
    }
}
