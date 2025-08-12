package org.dantesys.reliquiasNexus;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import org.bukkit.configuration.file.YamlConfiguration;
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

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public final class ReliquiasNexus extends JavaPlugin {
    private static final Map<UUID, Troca> trocas = new HashMap<>();
    private static FileConfiguration config;
    private static YamlConfiguration lang;
    final List<String> names = List.of("guerreiro","ceifador","vida","mares","barbaro",
            "fazendeiro","espiao","arqueiro","cacador","tempestade","mineiro","fenix","protetor",
            "hulk","sculk","pescador","flash","mago","ladrao","domador");
    @Override
    public void onEnable() {
        ItemsRegistro.init();
        saveResource("lang/pt-br.yml",true);
        saveResource("lang/en-us.yml",true);
        saveDefaultConfig();
        config = getConfig();
        String tipo = config.getString("lang");
        if(tipo==null){
            tipo="en-us";
            config.set("lang","en-us");
        }
        File file = new File(this.getDataFolder(), "/lang/"+tipo+".yml");
        lang = YamlConfiguration.loadConfiguration(file);
        saveConfig();
        try {
            lang.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> cmd = lang.getStringList("comandos.comando");
        getServer().getConsoleSender().sendMessage(""+this.getDataFolder());
        getServer().getConsoleSender().sendMessage(""+cmd);
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("nexus").executes(ctx -> {
            List<String> msgs = lang.getStringList("comandos.nexus");
            msgs.forEach(m -> ctx.getSource().getSender().sendMessage("§r"+m));
            return Command.SINGLE_SUCCESS;
        });
        root.then(Commands.literal(cmd.get(3)).then(Commands.argument("jogador", ArgumentTypes.player()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogador", PlayerSelectorArgumentResolver.class);
            final Player p = targetResolver.resolve(ctx.getSource()).getFirst();
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                ItemStack stack = player.getInventory().getItemInMainHand();
                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    if(nome!=null){
                        Troca t = new Troca(player.getUniqueId(),nome);
                        trocas.put(p.getUniqueId(),t);
                        List<String> msgs = lang.getStringList("comandos.troca.envio");
                        msgs.forEach(m -> {
                            m=m.replace("<player>",player.getName());
                            m=m.replace("<relic>",nome);
                            ctx.getSource().getSender().sendMessage("§r"+m);
                        });
                        String m = lang.getString("comandos.troca.recebido");
                        if(m!=null){
                            m=m.replace("<player>",p.getName());
                            sender.sendMessage("§2"+m);
                        }
                    }
                }else{
                    sender.sendMessage("§c"+lang.getString("comandos.troca.erro1"));
                }
            }else{
                sender.sendMessage("§c"+lang.getString("comandos.troca.erro2"));
            }
            return Command.SINGLE_SUCCESS;
        })));
        root.then(Commands.literal(cmd.get(0)).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                player.getInventory().addItem(ItemsRegistro.livro.getItem(1));
                sender.sendMessage("§2"+lang.getStringList("comandos.livro.sucesso"));
            }else{
                sender.sendMessage("§c"+lang.getStringList("comandos.livro.erro"));
            }
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal(cmd.get(1)).executes(ctx -> {
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
                        sender.sendMessage("§c"+lang.getStringList("comandos.evoluir.erro1"));
                    }
                }else{
                    sender.sendMessage("§c"+lang.getStringList("comandos.evoluir.erro1"));
                }
            }else{
                sender.sendMessage("§c"+lang.getStringList("comandos.evoluir.erro2"));
            }
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal(cmd.get(4)).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                ItemStack stack = player.getInventory().getItemInMainHand();
                ItemMeta meta = stack.getItemMeta();
                if(meta!=null){
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
                                                    String msg = lang.getString("comandos.troca.aceita.sucesso");
                                                    p.sendMessage("§2"+msg);
                                                    sender.sendMessage("§2"+msg);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }else{
                            sender.sendMessage("§c"+lang.getString("comandos.troca.aceita.erro1"));
                        }
                    }else{
                        sender.sendMessage("§c"+lang.getString("comandos.troca.aceita.erro2"));
                    }
                }else{
                    sender.sendMessage("§c"+lang.getString("comandos.troca.aceita.erro2"));
                }
            }else{
                sender.sendMessage("§c"+lang.getString("comandos.troca.aceita.erro3"));
            }
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal(cmd.get(5)).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                Troca t = trocas.remove(player.getUniqueId());
                Player p = Bukkit.getPlayer(t.uuid());
                if(p!=null){
                    String m = lang.getString("comandos.troca.cancela.envio");
                    if(m!=null){
                        m=m.replace("<player>",player.getName());
                        p.sendMessage("§c"+m);
                    }
                }
                sender.sendMessage("§c"+lang.getString("comandos.troca.cancela.recebido"));
            }else{
                sender.sendMessage("§c"+lang.getString("comandos.troca.cancela.erro"));
            }
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal(cmd.get(2)).executes(ctx -> {
            boolean expurgo = config.getBoolean("expurgo");
            String msg = "§r§2"+lang.getString("comandos.expurgo.seguro");
            if(expurgo){
                msg = "§r§c"+lang.getString("comandos.expurgo.perigo");
            }
            ctx.getSource().getSender().sendMessage(msg);
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal("list").executes(ctx -> {
            ConfigurationSection secao = config.getConfigurationSection("nexus");
            if(secao!=null){
                for(String nexus: secao.getKeys(false)){
                    String uuidStr = config.getString("nexus."+nexus);
                    String dono = "§c"+lang.getString("comandos.list.sem");
                    if(uuidStr != null && !uuidStr.isBlank()){
                        try{
                            UUID uuid = UUID.fromString(uuidStr);
                            OfflinePlayer player = getServer().getOfflinePlayer(uuid);
                            dono = "§c"+(player.getName() != null? "§r§2"+player.getName():"§r§c"+lang.getString("comandos.list.desco"));
                        }catch(IllegalArgumentException ignored){
                            dono = "§c"+lang.getString("comandos.list.comro");
                        }
                    }
                    ctx.getSource().getSender().sendMessage(nexus+": "+dono);
                }
            }else ctx.getSource().getSender().sendMessage("§c"+lang.getString("comandos.list.erro"));
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal("level").executes(ctx -> {
            if(ctx.getSource().getExecutor() instanceof Player player){
                List<NamespacedKey> keys = NexusKeys.getKeyLevel();
                PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                player.sendMessage("§n§l"+lang.getString("comandos.level.msg"));
                for(NamespacedKey k:keys){
                    int l = dataPlayer.getOrDefault(k, PersistentDataType.INTEGER,0);
                    if(l>0){
                        player.sendMessage("§r§2"+k.getKey()+": "+l);
                    }else{
                        player.sendMessage("§r§2"+k.getKey()+": §r§c"+lang.getString("comandos.level.sem"));
                    }
                }
            }else ctx.getSource().getSender().sendMessage("§c"+lang.getString("comandos.level.erro"));
            return Command.SINGLE_SUCCESS;
        }));
        root.then(Commands.literal(cmd.get(8)).then(Commands.argument("exp", BoolArgumentType.bool()).requires(sender -> sender.getSender().isOp()).executes(ctx -> {
            boolean exp = ctx.getArgument("exp", boolean.class);
            config.set("expurgo",exp);
            saveConfig();
            if(exp){
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendMessage("§r§c"+lang.getString("comandos.expurgar.msg.aviso"));
                    player.sendMessage("§r§c"+lang.getString("comandos.expurgar.msg.perigo"));
                    player.sendMessage("§r§c"+lang.getString("comandos.expurgar.msg.aviso"));
                });
            }else{
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendMessage("§r§2"+lang.getString("comandos.expurgar.msg.aviso"));
                    player.sendMessage("§r§2"+lang.getString("comandos.expurgar.msg.seguro"));
                    player.sendMessage("§r§2"+lang.getString("comandos.expurgar.msg.aviso"));
                });
            }
            ctx.getSource().getSender().sendMessage("§2"+lang.getString("comandos.expurgar.log")+" "+exp);
            return Command.SINGLE_SUCCESS;
        })));
        root.then(Commands.literal(cmd.get(6)).then(Commands.argument("jogadores", ArgumentTypes.players()).requires(sender -> sender.getSender().isOp()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogadores", PlayerSelectorArgumentResolver.class);
            final List<Player> targets = targetResolver.resolve(ctx.getSource());
            final CommandSender sender = ctx.getSource().getSender();
            int limite = config.getInt("limite");
            for (final Player p : targets) {
                PersistentDataContainer dataPlayer = p.getPersistentDataContainer();
                int qtd = dataPlayer.getOrDefault(QTD.key, PersistentDataType.INTEGER,0);
                if(qtd>=limite){
                    String m = lang.getString("comandos.receber.limite");
                    if(m!=null){
                        m=m.replace("<player>",p.getName());
                        ctx.getSource().getSender().sendMessage("§2"+m);
                    }
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
                    p.sendMessage(Component.text("§2"+lang.getString("comandos.receber.sucesso")+" "+nome));
                    String m = lang.getString("comandos.receber.slog");
                    if(m!=null){
                        m=m.replace("<player>",p.getName());
                        sender.sendMessage("§2"+m+" "+nome);
                    }
                }
            }
            return Command.SINGLE_SUCCESS;
        })));
        root.then(Commands.literal(cmd.get(7)).then(Commands.argument("jogador", ArgumentTypes.player()).then(Commands.argument("reliquia", StringArgumentType.word()).suggests((ctx, builder) -> {
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
                    String m = lang.getString("comandos.receber.limite");
                    if(m!=null){
                        m=m.replace("<player>",p.getName());
                        ctx.getSource().getSender().sendMessage("§2"+m);
                    }
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
                        p.sendMessage(Component.text("§2"+lang.getString("comandos.receber.sucesso")+" "+nome));
                        String m = lang.getString("comandos.receber.slog");
                        if(m!=null){
                            m=m.replace("<player>",p.getName());
                            sender.sendMessage("§2"+m+" "+nome);
                        }
                    }else{
                        String m = lang.getString("comandos.receber.erro");
                        if(m!=null){
                            m=m.replace("<relic>",reliquia);
                            sender.sendMessage("§c"+m);
                        }
                    }
                }
            }
            return Command.SINGLE_SUCCESS;
        }))));
        root.then(Commands.literal(cmd.get(9)).then(Commands.argument("valor", IntegerArgumentType.integer()).requires(sender -> sender.getSender().isOp()).executes(ctx -> {
            final Integer valor = ctx.getArgument("valor", Integer.class);
            final CommandSender sender = ctx.getSource().getSender();
            if(valor<1){
                sender.sendMessage(Component.text("§c"+lang.getString("comandos.limite.erro")));
                return Command.SINGLE_SUCCESS;
            }
            config.set("limite",valor);
            saveConfig();
            sender.sendMessage(Component.text("§2"+lang.getString("comandos.limite.sucesso")+" "+valor));
            return Command.SINGLE_SUCCESS;
        })));
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
    public static FileConfiguration getLang(){
        return lang;
    }
    public static void setConfigSave(String path,Object value){
        config.set(path,value);
    }
    public static void saiu(Player p){
        Troca t = trocas.remove(p.getUniqueId());
        if(t==null)return;
        Player player = Bukkit.getPlayer(t.uuid());
        if(player!=null){
            String m = lang.getString("comandos.troca.erro3");
            if(m!=null){
                m=m.replace("<player>",p.getName());
                player.sendMessage("§2"+m);
            }
        }
    }
}
