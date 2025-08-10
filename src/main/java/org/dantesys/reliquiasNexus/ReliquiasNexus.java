package org.dantesys.reliquiasNexus;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.dantesys.reliquiasNexus.eventos.*;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.NexusKeys;
import org.dantesys.reliquiasNexus.util.Troca;

import java.util.*;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public final class ReliquiasNexus extends JavaPlugin {
    private static final Map<UUID, Troca> trocas = new HashMap<>();
    private static FileConfiguration config;
    final List<String> names = List.of("guerreiro","ceifador","vida","mares","barbaro",
            "fazendeiro","espiao","arqueiro","cacador","tempestade","mineiro","fenix","protetor",
            "hulk","sculk","pescador","flash","mago","ladrao","domador");
    @Override
    public void onEnable() {
        ItemsRegistro.init();
        saveDefaultConfig();
        config = getConfig();
        saveConfig();
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("nexus").executes(ctx -> {
            ctx.getSource().getSender().sendMessage("§rUsando Nexus");
            ctx.getSource().getSender().sendMessage("§r/nexus list");
            ctx.getSource().getSender().sendMessage("§r-Mostrar quem está com qual reliquia.");
            ctx.getSource().getSender().sendMessage("§r/nexus level");
            ctx.getSource().getSender().sendMessage("§r-Mostrar seus niveis de reliquias.");
            ctx.getSource().getSender().sendMessage("§r/nexus expurgo");
            ctx.getSource().getSender().sendMessage("§r-Mostrar seu o servidor está em expurgo.");
            ctx.getSource().getSender().sendMessage("§r/nexus trocar <jogador>");
            ctx.getSource().getSender().sendMessage("§r-Envia um pedido de troca para o jogador escolhido.");
            ctx.getSource().getSender().sendMessage("§r/nexus troca-aceitar");
            ctx.getSource().getSender().sendMessage("§r-Aceita um pedido de troca.");
            ctx.getSource().getSender().sendMessage("§r/nexus troca-cancelar");
            ctx.getSource().getSender().sendMessage("§r-Cancela um pedido de troca.");
            if(ctx.getSource().getSender().isOp()){
                int limite = config.getInt("limite");
                ctx.getSource().getSender().sendMessage("§r/nexus receber <jogador/jogadores>");
                ctx.getSource().getSender().sendMessage("§r-Envia reliquias não utilizadas aleatoriamente para jogadores que não tenha "+limite+" ou mais reliquias.");
                ctx.getSource().getSender().sendMessage("§r/nexus reliquia <jogador> <reliquia>");
                ctx.getSource().getSender().sendMessage("§r-Envia reliquias não utilizadas da sua escolhar para um jogador que não tenha mais de "+limite+" reliquias.");
                ctx.getSource().getSender().sendMessage("§r/nexus expurgar <true/false>");
                ctx.getSource().getSender().sendMessage("§r-Define se está em expurgo ou não");
            }
            return Command.SINGLE_SUCCESS;
        });
        root.then(Commands.literal("trocar").then(Commands.argument("jogador", ArgumentTypes.player()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogador", PlayerSelectorArgumentResolver.class);
            final Player p = targetResolver.resolve(ctx.getSource()).getFirst();
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                ItemStack stack = player.getInventory().getItemInMainHand();
                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    Troca t = new Troca(player.getUniqueId(),nome);
                    trocas.put(p.getUniqueId(),t);
                    p.sendMessage("§rVocê recebeu 1 pedido de troca!");
                    p.sendMessage("§rO jogador "+player.getName()+" está querendo trocar o nexus "+nome+"!");
                    p.sendMessage("§2Use o comando /nexus troca-aceitar para aceitar o pedido de troca!");
                    p.sendMessage("§cUse o comando /nexus troca-cancelar para cancelar o pedido de troca!");
                    sender.sendMessage("§2Pedido de troca enviado com sucesso, aguarde "+p.getName()+" aceitar ou cancelar a troca!");
                }else{
                    sender.sendMessage("§cVocê precisa estar segurando a reliquia para solicitar a troca!");
                }
            }else{
                sender.sendMessage("§cApenas jogadores podem fazer trocas!");
            }
            return Command.SINGLE_SUCCESS;
        })));
        root.then(Commands.literal("livro").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                player.getInventory().addItem(ItemsRegistro.livro.getItem(1));
                sender.sendMessage("§2Você recebeu o livro!");
            }else{
                sender.sendMessage("§cApenas jogadores podem receber o livro!");
            }
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal("evoluir").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                ItemStack stack = player.getInventory().getItemInMainHand();
                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    EvoluirEvent evo = new EvoluirEvent(this);
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    if(nome!=null){
                        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                        int level=switch (nome){
                            case "barbaro" -> dataPlayer.getOrDefault(BARBARO.key,PersistentDataType.INTEGER,1);
                            case "ceifador" -> dataPlayer.getOrDefault(CEIFADOR.key,PersistentDataType.INTEGER,1);
                            case "fazendeiro" -> dataPlayer.getOrDefault(FAZENDEIRO.key,PersistentDataType.INTEGER,1);
                            case "guerreiro" -> dataPlayer.getOrDefault(GUERREIRO.key,PersistentDataType.INTEGER,1);
                            case "mares" -> dataPlayer.getOrDefault(MARES.key,PersistentDataType.INTEGER,1);
                            case "vida" -> dataPlayer.getOrDefault(VIDA.key,PersistentDataType.INTEGER,1);
                            case "espiao" -> dataPlayer.getOrDefault(ESPIAO.key,PersistentDataType.INTEGER,1);
                            case "arqueiro" -> dataPlayer.getOrDefault(ARQUEIRO.key,PersistentDataType.INTEGER,1);
                            case "cacador" -> dataPlayer.getOrDefault(CACADOR.key,PersistentDataType.INTEGER,1);
                            case "tempestade" -> dataPlayer.getOrDefault(TEMPESTADE.key,PersistentDataType.INTEGER,1);
                            case "mineiro" -> dataPlayer.getOrDefault(MINEIRO.key,PersistentDataType.INTEGER,1);
                            case "fenix" -> dataPlayer.getOrDefault(FENIX.key,PersistentDataType.INTEGER,1);
                            case "protetor" -> dataPlayer.getOrDefault(PROTETOR.key,PersistentDataType.INTEGER,1);
                            case "hulk" -> dataPlayer.getOrDefault(HULK.key,PersistentDataType.INTEGER,1);
                            case "sculk" -> dataPlayer.getOrDefault(SCULK.key,PersistentDataType.INTEGER,1);
                            case "pescador" -> dataPlayer.getOrDefault(PESCADOR.key,PersistentDataType.INTEGER,1);
                            case "flash" -> dataPlayer.getOrDefault(FLASH.key,PersistentDataType.INTEGER,1);
                            case "mago" -> dataPlayer.getOrDefault(MAGO.key,PersistentDataType.INTEGER,1);
                            case "ladrao" -> dataPlayer.getOrDefault(LADRAO.key,PersistentDataType.INTEGER,1);
                            case "domador" -> dataPlayer.getOrDefault(DOMADOR.key,PersistentDataType.INTEGER,1);
                            default -> 1;
                        };
                        evo.tentarEvoluir(player,stack,level,evo.getSlotOfItem(player,stack));
                    }else{
                        sender.sendMessage("§cVocê precisa estar segurando a reliquia para tentar evoluir!");
                    }
                }else{
                    sender.sendMessage("§cVocê precisa estar segurando a reliquia para tentar evoluir!");
                }
            }else{
                sender.sendMessage("§cApenas jogadores podem fazer evolução!");
            }
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal("troca-aceitar").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                ItemStack stack = player.getInventory().getItemInMainHand();
                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    Troca t = trocas.remove(player.getUniqueId());
                    Player p = Bukkit.getPlayer(t.uuid());
                    if(p!=null && nome!=null){
                        PlayerInventory inv = p.getInventory();
                        for(ItemStack s:inv.getContents()){
                            if(s!=null){
                                ItemMeta m = s.getItemMeta();
                                PersistentDataContainer d = m.getPersistentDataContainer();
                                if(d.has(NEXUS.key,PersistentDataType.STRING)){
                                    String n = d.get(NEXUS.key,PersistentDataType.STRING);
                                    if(n!=null && n.equals(t.stack())){
                                        Nexus nex = ItemsRegistro.getFromNome(n);
                                        if(nex!=null){
                                            PersistentDataContainer container = player.getPersistentDataContainer();
                                            NamespacedKey key = NexusKeys.getKey(nex.getNome());
                                            int level=1;
                                            if(key!=null && container.has(key,PersistentDataType.INTEGER)){
                                                level=container.getOrDefault(key,PersistentDataType.INTEGER,1);
                                            }else if(key!=null){
                                                container.set(key,PersistentDataType.INTEGER,1);
                                            }
                                            ItemStack aux = nex.getItem(level);
                                            player.getInventory().setItemInMainHand(aux);
                                            config.set("nexus."+n,player.getUniqueId().toString());
                                            nex = ItemsRegistro.getFromNome(nome);
                                            if(nex!=null){
                                                PersistentDataContainer pc = p.getPersistentDataContainer();
                                                key = NexusKeys.getKey(nex.getNome());
                                                level=1;
                                                if(key!=null && pc.has(key,PersistentDataType.INTEGER)){
                                                    level=pc.getOrDefault(key,PersistentDataType.INTEGER,1);
                                                }else if(key!=null){
                                                    pc.set(key,PersistentDataType.INTEGER,1);
                                                }
                                                aux = nex.getItem(level);
                                                config.set("nexus."+nome,p.getUniqueId().toString());
                                                inv.remove(s);
                                                inv.addItem(aux);
                                                p.sendMessage("§2Troca feita com sucesso!");
                                                sender.sendMessage("§2Troca feita com sucesso!");
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        sender.sendMessage("§cO jogador não está online cancelando a troca.");
                    }
                }else{
                    sender.sendMessage("§cVocê precisa estar segurando a reliquia para aceitar a troca!");
                }
            }else{
                sender.sendMessage("§cApenas jogadores podem fazer aceitar trocas!");
            }
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal("troca-cancelar").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                Troca t = trocas.remove(player.getUniqueId());
                Player p = Bukkit.getPlayer(t.uuid());
                if(p!=null){
                    p.sendMessage("§cSeu pedido de troca para "+player.getName()+" foi cancelaro!");
                }
                sender.sendMessage("§cVocê cancelou o pedido de troca!");
            }else{
                sender.sendMessage("§cApenas jogadores podem fazer cancelar trocas!");
            }
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal("expurgo").executes(ctx -> {
            boolean expurgo = config.getBoolean("expurgo");
            String msg = "§r§2O servidor está seguto!";
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
            saveConfig();
            if(exp){
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendMessage("§r§c+=============|AVISO|=============+");
                    player.sendMessage("§r§c|O Servidor agora está em expurgo.|");
                    player.sendMessage("§r§c+=============|AVISO|=============+");
                });
            }else{
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendMessage("§r§2+==============|AVISO|==============+");
                    player.sendMessage("§r§2|O Servidor agora esta seguro agora.|");
                    player.sendMessage("§r§2+==============|AVISO|==============+");
                });
            }
            ctx.getSource().getSender().sendMessage("§2O expurgo foi definido como:"+exp);
            return Command.SINGLE_SUCCESS;
        })));
        root.then(Commands.literal("receber").then(Commands.argument("jogadores", ArgumentTypes.players()).requires(sender -> sender.getSender().isOp()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogadores", PlayerSelectorArgumentResolver.class);
            final List<Player> targets = targetResolver.resolve(ctx.getSource());
            final CommandSender sender = ctx.getSource().getSender();
            int limite = config.getInt("limite");
            for (final Player p : targets) {
                PersistentDataContainer dataPlayer = p.getPersistentDataContainer();
                int qtd = dataPlayer.getOrDefault(QTD.key, PersistentDataType.INTEGER,0);
                if(qtd>=limite){
                    ctx.getSource().getSender().sendMessage("§cO jogador "+p.getName()+" não pode receber a reliquia!");
                }else{
                    qtd++;
                    List<Nexus> reliquias = ItemsRegistro.getValidReliquia(config);
                    Random rng = new Random();
                    int escolhido = rng.nextInt(reliquias.size());
                    Nexus n = reliquias.get(escolhido);
                    String nome = n.getNome();
                    config.set("nexus."+nome,p.getUniqueId().toString());
                    saveConfig();
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
                    p.sendMessage(Component.text("§2Você recebeu a reliquia "+nome));
                    sender.sendMessage(Component.text("§2O jogador "+p.getName()+" recebeu a reliquia "+nome));
                }
            }
            return Command.SINGLE_SUCCESS;
        })));
        root.then(Commands.literal("reliquia").then(Commands.argument("jogador", ArgumentTypes.player()).then(Commands.argument("reliquia", StringArgumentType.word()).suggests((ctx, builder) -> {
            names.stream().filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase())).forEach(builder::suggest);
            return builder.buildFuture();
        }).requires(sender -> sender.getSender().isOp()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogador", PlayerSelectorArgumentResolver.class);
            final Player p = targetResolver.resolve(ctx.getSource()).getFirst();
            final CommandSender sender = ctx.getSource().getSender();
            final String reliquia = ctx.getArgument("reliquia",String.class).toLowerCase();
            Nexus n = ItemsRegistro.getFromNome(reliquia);
            if(n!=null){
                int limite = config.getInt("limite");
                PersistentDataContainer dataPlayer = p.getPersistentDataContainer();
                int qtd = dataPlayer.getOrDefault(QTD.key, PersistentDataType.INTEGER,0);
                if(qtd>=limite){
                    ctx.getSource().getSender().sendMessage("§cO jogador "+p.getName()+" não pode receber a reliquia!");
                }else{
                    qtd++;
                    String nome = n.getNome();
                    String uuidStr = config.getString("nexus"+nome);
                    if(uuidStr==null || uuidStr.isBlank()){
                        config.set("nexus."+nome,p.getUniqueId().toString());
                        saveConfig();
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
                        p.sendMessage(Component.text("§2Você recebeu a reliquia "+nome));
                        sender.sendMessage(Component.text("§2O jogador "+p.getName()+" recebeu a reliquia "+nome));
                    }else{
                        sender.sendMessage(Component.text("§cA reliquia "+reliquia+" já possui dono!"));
                    }
                }
            }else{
                sender.sendMessage("§cA reliquia "+reliquia+" não existe!");
            }
            return Command.SINGLE_SUCCESS;
        }))));
        LiteralCommandNode<CommandSourceStack> buildCommand = root.build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(buildCommand));
        getServer().getPluginManager().registerEvents(new JoinQuitEvent(this), this);
        getServer().getPluginManager().registerEvents(new LimitadorEvent(this), this);
        getServer().getPluginManager().registerEvents(new PassivaEvent(), this);
        getServer().getPluginManager().registerEvents(new PerdeuEvent(), this);
        getServer().getPluginManager().registerEvents(new EvoluirEvent(this), this);
        getServer().getPluginManager().registerEvents(new SpecialEvent(this), this);
        getServer().getConsoleSender().sendMessage("§2[Nexus]: Plugin Ativado!");
    }

    @Override
    public void onDisable() {
        saveConfig();
        getServer().getConsoleSender().sendMessage("§4[Nexus]: Plugin Desativado!");
    }
    public static FileConfiguration getNexusConfig(){
        return config;
    }
    public static void setConfigSave(String path,Object value){
        config.set(path,value);
    }
    public static void saiu(Player p){
        Troca t = trocas.remove(p.getUniqueId());
        if(t==null)return;
        Player player = Bukkit.getPlayer(t.uuid());
        if(player!=null){
            player.sendMessage("§cO jogador"+p.getName()+" saiu sem aceitar ou cancelar a troca!");
        }
    }
}
