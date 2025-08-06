package org.dantesys.reliquiasNexus;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.dantesys.reliquiasNexus.eventos.*;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.NexusKeys;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.dantesys.reliquiasNexus.util.NexusKeys.DONO;
import static org.dantesys.reliquiasNexus.util.NexusKeys.QTD;

public final class ReliquiasNexus extends JavaPlugin {
    FileConfiguration config = getConfig();
    @Override
    public void onEnable() {
        ItemsRegistro.init();
        config.addDefault("expurgo",false);
        config.createSection("nexus");
        config.addDefault("nexus.guerreiro","");
        config.addDefault("nexus.ceifador","");
        config.addDefault("nexus.vida","");
        config.addDefault("nexus.mares","");
        config.addDefault("nexus.barbaro","");
        config.addDefault("nexus.fazendeiro","");
        config.addDefault("nexus.espiao","");
        config.addDefault("nexus.arqueiro","");
        config.addDefault("nexus.cacador","");
        config.addDefault("nexus.tempestade","");
        config.addDefault("nexus.mineiro","");
        config.addDefault("nexus.fenix","");
        config.addDefault("nexus.protetor","");
        config.addDefault("nexus.hulk","");
        config.addDefault("nexus.sculk","");
        config.addDefault("nexus.pescador","");
        config.addDefault("nexus.flash","");
        config.addDefault("nexus.mago","");
        config.addDefault("nexus.ladrao","");
        config.addDefault("nexus.domador","");
        config.options().copyDefaults(true);
        saveConfig();
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("nexus").executes(ctx -> {
            ctx.getSource().getSender().sendMessage("§rUsando Nexus");
            ctx.getSource().getSender().sendMessage("§r/nexus list");
            ctx.getSource().getSender().sendMessage("§r-Mostrar quem está com qual reliquia.");
            ctx.getSource().getSender().sendMessage("§r/nexus level");
            ctx.getSource().getSender().sendMessage("§r-Mostrar seus niveis de reliquias.");
            ctx.getSource().getSender().sendMessage("§r/nexus expurgo");
            ctx.getSource().getSender().sendMessage("§r-Mostrar seu o servidor está em expurgo.");
            if(ctx.getSource().getSender().isOp()){
                ctx.getSource().getSender().sendMessage("§r/nexus receber <jogador/jogadores>");
                ctx.getSource().getSender().sendMessage("§r-Envia reliquias não utilizadas aleatoriamente para jogadores que não tenha 3 ou mais reliquias.");
                ctx.getSource().getSender().sendMessage("§r/nexus expurgar <true/false>");
                ctx.getSource().getSender().sendMessage("§r-Define se está em expurgo ou não");
            }
            return Command.SINGLE_SUCCESS;
        });
        root.then(Commands.literal("expurgo").executes(ctx -> {
            boolean expurgo = config.getBoolean("expurgo");
            String msg = "§r§2O servidor não está em expurgo!";
            if(expurgo){
                msg = "§r§cO servidor está em expurgo!";
                if(ctx.getSource().getExecutor() instanceof Player){
                    msg = "§r§cO servidor está em expurgo, caçe ou seja caçado!";
                }
            }
            ctx.getSource().getSender().sendMessage(msg);
            return Command.SINGLE_SUCCESS;
        }));
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
                            dono = "§c"+(player.getName() != null? "§r§2"+player.getName():"§r§cDesconhecido");
                        }catch(IllegalArgumentException ignored){
                            dono = "§cJogador Corrompido";
                        }
                    }
                    ctx.getSource().getSender().sendMessage(nexus+": "+dono);
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
                        player.sendMessage("§r§2"+k.getKey()+": "+l);
                    }else{
                        player.sendMessage("§r§2"+k.getKey()+": §r§cNunca usou");
                    }
                }
            }else ctx.getSource().getSender().sendMessage("§cApenas jogadores podem usar");
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal("expurgar").then(Commands.argument("exp", BoolArgumentType.bool()).requires(sender -> sender.getSender().isOp()).executes(ctx -> {
            boolean exp = ctx.getArgument("exp", boolean.class);
            config.set("expurgo",exp);
            if(exp){
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendMessage("§r§c╔═══════════╡⚠ AVISO ⚠╞═══════════╗");
                    player.sendMessage("§r§c║O Servidor agora está em expurgo.║");
                    player.sendMessage("§r§c╚═══════════╡⚠ AVISO ⚠╞═══════════╝");
                });
            }else{
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendMessage("§r§2╔════════════╡⚠ AVISO ⚠╞════════════╗");
                    player.sendMessage("§r§2║O Servidor agora esta seguro agora.║");
                    player.sendMessage("§r§2╚════════════╡⚠ AVISO ⚠╞════════════╝");
                });
            }
            ctx.getSource().getSender().sendMessage("§2O expurgo foi definido como:"+exp);
            return Command.SINGLE_SUCCESS;
        })));
        root.then(Commands.literal("receber").then(Commands.argument("jogadores", ArgumentTypes.players()).requires(sender -> sender.getSender().isOp()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogadores", PlayerSelectorArgumentResolver.class);
            final List<Player> targets = targetResolver.resolve(ctx.getSource());
            final CommandSender sender = ctx.getSource().getSender();
            for (final Player p : targets) {
                PersistentDataContainer dataPlayer = p.getPersistentDataContainer();
                int qtd = dataPlayer.getOrDefault(QTD.key, PersistentDataType.INTEGER,0);
                if(qtd>=3){
                    ctx.getSource().getSender().sendMessage("§cO jogador "+p.getName()+" não pode receber a reliquia!");
                }else{
                    qtd++;
                    List<Nexus> reliquias = ItemsRegistro.getValidReliquia(config);
                    Random rng = new Random();
                    int escolhido = rng.nextInt(reliquias.size());
                    Nexus n = reliquias.get(escolhido);
                    String nome = n.getNome();
                    config.set("nexus."+nome,p.getUniqueId());
                    dataPlayer.set(QTD.key,PersistentDataType.INTEGER,qtd);
                    int level =1;
                    NamespacedKey key = NexusKeys.getKey(nome);
                    if(key!=null && dataPlayer.has(key,PersistentDataType.INTEGER)){
                        level=dataPlayer.getOrDefault(key,PersistentDataType.INTEGER,1);
                    }else if(key!=null){
                        dataPlayer.set(key,PersistentDataType.INTEGER,1);
                    }
                    ItemStack stack = n.getItem(level);
                    ItemMeta meta = stack.getItemMeta();
                    meta.getPersistentDataContainer().set(DONO.key,PersistentDataType.STRING,p.getUniqueId().toString());
                    stack.setItemMeta(meta);
                    p.getInventory().addItem(stack);
                    p.sendMessage(Component.text("§2 Você recebeu a reliquia "+nome));
                    sender.sendMessage(Component.text("§2 O jogador "+p.getName()+" recebeu a reliquia "+nome));
                }
            }
            return Command.SINGLE_SUCCESS;
        })));
        LiteralCommandNode<CommandSourceStack> buildCommand = root.build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(buildCommand));
        getServer().getPluginManager().registerEvents(new JoinQuitEvent(), this);
        getServer().getPluginManager().registerEvents(new LimitadorEvent(), this);
        getServer().getPluginManager().registerEvents(new PassivaEvent(), this);
        getServer().getPluginManager().registerEvents(new PerdeuEvent(), this);
        getServer().getPluginManager().registerEvents(new EvoluirEvent(), this);
        getServer().getPluginManager().registerEvents(new SpecialEvent(), this);
        getServer().getConsoleSender().sendMessage("§2[Nexus]: Plugin Ativado!");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§4[Nexus]: Plugin Desativado!");
    }
}
