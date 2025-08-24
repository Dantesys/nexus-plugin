package org.dantesys.reliquiasNexus;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.dantesys.reliquiasNexus.economy.Banco;
import org.dantesys.reliquiasNexus.economy.Emprestimo;
import org.dantesys.reliquiasNexus.eventos.*;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.NexusKeys;
import org.dantesys.reliquiasNexus.util.Troca;
import org.dantesys.reliquiasNexus.util.UpdaterCheck;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public final class ReliquiasNexus extends JavaPlugin implements Listener {
    private static final Map<UUID, Troca> trocas = new HashMap<>();
    private static final Map<UUID, Emprestimo> emprestimos = new HashMap<>();
    private static final Map<String, Banco> bancos = new HashMap<>();
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
        new UpdaterCheck(this, "dantesys/nexus-plugin").checkForUpdates();
        List<String> cmd = lang.getStringList("comandos.comando");
        getServer().getConsoleSender().sendMessage(""+this.getDataFolder());
        getServer().getConsoleSender().sendMessage(""+cmd);

        // Carregar dados econômicos
        carregarDadosEconomicos();

        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("nexus").executes(ctx -> {
            List<String> msgs = lang.getStringList("comandos.nexus");
            if (msgs.isEmpty()) {
                ctx.getSource().getSender().sendMessage("§a✨ === Comandos Nexus ===");
                ctx.getSource().getSender().sendMessage("§b📖 /nexus livro - Obter livro de relíquias");
                ctx.getSource().getSender().sendMessage("§b⚡ /nexus evoluir - Evoluir relíquia");
                ctx.getSource().getSender().sendMessage("§b🎯 /nexus missoes - Abrir menu de missões");
                ctx.getSource().getSender().sendMessage("§b🏪 /nexus loja - Abrir loja de relíquias");
                ctx.getSource().getSender().sendMessage("§b📋 /nexus list - Listar relíquias");
                ctx.getSource().getSender().sendMessage("§b📊 /nexus level - Ver níveis");
                ctx.getSource().getSender().sendMessage("§b🗑️ /nexus remover <reliquia> <jogador> - Remover relíquia (OP)");
                ctx.getSource().getSender().sendMessage("§b💰 /nexus carteira - Ver sua carteira");
                ctx.getSource().getSender().sendMessage("§b💳 /nexus emprestimo - Sistema de empréstimos");
                ctx.getSource().getSender().sendMessage("§b🏦 /nexus banco - Sistema bancário");
                ctx.getSource().getSender().sendMessage("§b🔄 /nexus trocar <jogador> - Trocar relíquia");
                ctx.getSource().getSender().sendMessage("§b✅ /nexus aceitar - Aceitar troca");
                ctx.getSource().getSender().sendMessage("§b❌ /nexus cancelar - Cancelar troca");
                ctx.getSource().getSender().sendMessage("§b🌐 /nexus servidor-economia - Info do Banco Central");
            } else {
                msgs.forEach(m -> ctx.getSource().getSender().sendMessage("§r"+m));
            }
            return Command.SINGLE_SUCCESS;
        });

        // Comando para remover relíquia
        root.then(Commands.literal("remover")
                .then(Commands.argument("reliquia", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            names.stream().filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase())).forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("jogador", ArgumentTypes.player())
                                .requires(sender -> sender.getSender().isOp())
                                .executes(ctx -> {
                                    final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogador", PlayerSelectorArgumentResolver.class);
                                    final Player p = targetResolver.resolve(ctx.getSource()).getFirst();
                                    final CommandSender sender = ctx.getSource().getSender();
                                    final String reliquia = ctx.getArgument("reliquia", String.class).toLowerCase();

                                    Nexus n = ItemsRegistro.getFromNome(reliquia);
                                    if (n != null) {
                                        boolean reliquiaRemovida = false;
                                        PlayerInventory inv = p.getInventory();
                                        for (ItemStack item : inv.getContents()) {
                                            if (item != null && item.hasItemMeta()) {
                                                ItemMeta meta = item.getItemMeta();
                                                PersistentDataContainer data = meta.getPersistentDataContainer();
                                                if (data.has(NEXUS.key, PersistentDataType.STRING)) {
                                                    String nomeReliquia = data.get(NEXUS.key, PersistentDataType.STRING);
                                                    if (reliquia.equalsIgnoreCase(nomeReliquia)) {
                                                        inv.remove(item);
                                                        reliquiaRemovida = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        if (reliquiaRemovida) {
                                            config.set("nexus." + reliquia, null);
                                            saveConfig();
                                            PersistentDataContainer dataPlayer = p.getPersistentDataContainer();
                                            int qtd = dataPlayer.getOrDefault(QTD.key, PersistentDataType.INTEGER, 0);
                                            if (qtd > 0) {
                                                dataPlayer.set(QTD.key, PersistentDataType.INTEGER, qtd - 1);
                                            }
                                            sender.sendMessage("§a✅ Relíquia removida com sucesso!");
                                        } else {
                                            sender.sendMessage("§c❌ Jogador " + p.getName() + " não possui a relíquia " + reliquia + "!");
                                        }
                                    } else {
                                        sender.sendMessage("§c❌ Relíquia " + reliquia + " não existe!");
                                    }
                                    return Command.SINGLE_SUCCESS;
                                }))
                )
        );

        // Comando para abrir o menu de missões
        root.then(Commands.literal("missoes")
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("§c❌ Este comando só pode ser usado por um jogador.");
                        return Command.SINGLE_SUCCESS;
                    }
                    sender.sendMessage(Component.text("§a🎯 Menu de missões aberto, escolha a sua missão diária!"));
                    Inventory missoesInv = Bukkit.createInventory(null, 27, Component.text("§5Missões"));
                    List<String> missoes = lang.getStringList("missoes.lista");
                    if (missoes.isEmpty()) {
                        player.sendMessage("§c❌ Não há missões disponíveis no momento.");
                        return Command.SINGLE_SUCCESS;
                    }
                    for (int i = 0; i < missoes.size() && i < 27; i++) {
                        ItemStack papel = new ItemStack(Material.PAPER);
                        ItemMeta meta = papel.getItemMeta();
                        meta.displayName(Component.text("§b📜 Trabalho"));
                        meta.lore(List.of(Component.text("§f" + missoes.get(i))));
                        papel.setItemMeta(meta);
                        missoesInv.setItem(i, papel);
                    }
                    player.openInventory(missoesInv);
                    return Command.SINGLE_SUCCESS;
                })
        );

        // Comando para dar uma relíquia específica (OP)
        root.then(Commands.literal("dar")
                .then(Commands.argument("jogador", ArgumentTypes.player())
                        .then(Commands.argument("reliquia", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    names.stream().filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase())).forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .requires(sender -> sender.getSender().isOp())
                                .executes(ctx -> {
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
                                                ctx.getSource().getSender().sendMessage("§c❌ "+m);
                                            }
                                        }else{
                                            qtd++;
                                            String nome = n.getNome();
                                            String uuidStr = config.getString("nexus."+nome);
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
                                                p.sendMessage(Component.text("§a✅ "+lang.getString("comandos.receber.sucesso")+" "+nome));
                                                String m = lang.getString("comandos.receber.slog");
                                                if(m!=null){
                                                    m=m.replace("<player>",p.getName());
                                                    sender.sendMessage("§a✅ "+m+" "+nome);
                                                }
                                            }else{
                                                String m = lang.getString("comandos.receber.erro");
                                                if(m!=null){
                                                    m=m.replace("<relic>",reliquia);
                                                    sender.sendMessage("§c❌ "+m);
                                                }
                                            }
                                        }
                                    }
                                    return Command.SINGLE_SUCCESS;
                                }))
                )
        );

        // Comando para receber relíquia aleatória (OP)
        root.then(Commands.literal("receber")
                .then(Commands.argument("jogadores", ArgumentTypes.players())
                        .requires(sender -> sender.getSender().isOp())
                        .executes(ctx -> {
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
                                        ctx.getSource().getSender().sendMessage("§c❌ "+m);
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
                                    p.sendMessage(Component.text("§a✅ "+lang.getString("comandos.receber.sucesso")+" "+nome));
                                    String m = lang.getString("comandos.receber.slog");
                                    if(m!=null){
                                        m=m.replace("<player>",p.getName());
                                        sender.sendMessage("§a✅ "+m+" "+nome);
                                    }
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
        );

        // Comando de troca
        root.then(Commands.literal("trocar")
                .then(Commands.argument("jogador", ArgumentTypes.player())
                        .executes(ctx -> {
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
                                            ctx.getSource().getSender().sendMessage("§a🔄 "+m);
                                        });
                                        String m = lang.getString("comandos.troca.recebido");
                                        if(m!=null){
                                            m=m.replace("<player>",p.getName());
                                            sender.sendMessage("§a🔄 "+m);
                                        }
                                    }
                                }else{
                                    sender.sendMessage("§c❌ "+lang.getString("comandos.troca.erro1"));
                                }
                            }else{
                                sender.sendMessage("§c❌ "+lang.getString("comandos.troca.erro2"));
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
        );

        // Comando para obter o livro
        root.then(Commands.literal("livro")
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if(ctx.getSource().getExecutor() instanceof Player player){
                        player.getInventory().addItem(ItemsRegistro.livro.getItem(1));
                        sender.sendMessage("§a📖 "+lang.getString("comandos.livro.sucesso"));
                    }else{
                        sender.sendMessage("§c❌ "+lang.getString("comandos.livro.erro"));
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );

        // Comando para evoluir a relíquia
        root.then(Commands.literal("evoluir")
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if(ctx.getSource().getExecutor() instanceof Player player){
                        ItemStack stack = player.getInventory().getItemInMainHand();
                        ItemMeta meta = stack.getItemMeta();
                        if (meta == null) {
                            sender.sendMessage("§c❌ "+lang.getString("comandos.evoluir.erro1"));
                            return Command.SINGLE_SUCCESS;
                        }
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
                                sender.sendMessage("§c❌ "+lang.getString("comandos.evoluir.erro1"));
                            }
                        }else{
                            sender.sendMessage("§c❌ "+lang.getString("comandos.evoluir.erro1"));
                        }
                    }else{
                        sender.sendMessage("§c❌ "+lang.getString("comandos.evoluir.erro2"));
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );

        // Comando para aceitar troca
        root.then(Commands.literal("aceitar")
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if(ctx.getSource().getExecutor() instanceof Player player){
                        ItemStack stack = player.getInventory().getItemInMainHand();
                        ItemMeta meta = stack.getItemMeta();
                        if(meta!=null){
                            PersistentDataContainer data = meta.getPersistentDataContainer();
                            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                                String nome = data.get(NEXUS.key, PersistentDataType.STRING);
                                Troca t = trocas.remove(player.getUniqueId());
                                Player p = Bukkit.getPlayer(t.uuid());
                                if(p!=null && nome!=null){
                                    PlayerInventory inv = p.getInventory();
                                    for(ItemStack s:inv.getContents()){
                                        if(s!=null){
                                            ItemMeta m = s.getItemMeta();
                                            PersistentDataContainer d = m.getPersistentDataContainer();
                                            if(d.has(NEXUS.key,PersistentDataType.STRING)){
                                                String n = d.get(NEXUS.key, PersistentDataType.STRING);
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
                                                            p.sendMessage("§a✅ "+msg);
                                                            sender.sendMessage("§a✅ "+msg);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    sender.sendMessage("§c❌ "+lang.getString("comandos.troca.aceita.erro1"));
                                }
                            }else{
                                sender.sendMessage("§c❌ "+lang.getString("comandos.troca.aceita.erro2"));
                            }
                        }else{
                            sender.sendMessage("§c❌ "+lang.getString("comandos.troca.aceita.erro2"));
                        }
                    }else{
                        sender.sendMessage("§c❌ "+lang.getString("comandos.troca.aceita.erro3"));
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );

        // Comando para cancelar troca
        root.then(Commands.literal("cancelar")
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if(ctx.getSource().getExecutor() instanceof Player player){
                        Troca t = trocas.remove(player.getUniqueId());
                        Player p = Bukkit.getPlayer(t.uuid());
                        if(p!=null){
                            String m = lang.getString("comandos.troca.cancela.envio");
                            if(m!=null){
                                m=m.replace("<player>",player.getName());
                                p.sendMessage("§c❌ "+m);
                            }
                        }
                        sender.sendMessage("§c❌ "+lang.getString("comandos.troca.cancela.recebido"));
                    }else{
                        sender.sendMessage("§c❌ "+lang.getString("comandos.troca.cancela.erro"));
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );

        // Comando para checar status do expurgo
        root.then(Commands.literal("expurgar")
                .executes(ctx -> {
                    boolean expurgo = config.getBoolean("expurgo");
                    String msg = "§a🛡️ "+lang.getString("comandos.expurgo.seguro");
                    if(expurgo){
                        msg = "§c⚔️ "+lang.getString("comandos.expurgo.perigo");
                    }
                    ctx.getSource().getSender().sendMessage(msg);
                    return Command.SINGLE_SUCCESS;
                })
        );

        // Comando para listar relíquias
        root.then(Commands.literal("list")
                .executes(ctx -> {
                    ConfigurationSection secao = config.getConfigurationSection("nexus");
                    if(secao!=null){
                        for(String nexus: secao.getKeys(false)){
                            String uuidStr = config.getString("nexus."+nexus);
                            String dono = "§c❌ "+lang.getString("comandos.list.sem");
                            if(uuidStr != null && !uuidStr.isBlank()){
                                try{
                                    UUID uuid = UUID.fromString(uuidStr);
                                    OfflinePlayer player = getServer().getOfflinePlayer(uuid);
                                    dono = (player.getName() != null? "§a✅ "+player.getName():"§c❌ "+lang.getString("comandos.list.desco"));
                                }catch(IllegalArgumentException ignored){
                                    dono = "§c❌ "+lang.getString("comandos.list.comro");
                                }
                            }
                            ctx.getSource().getSender().sendMessage("§b📋 "+nexus+": "+dono);
                        }
                    }else ctx.getSource().getSender().sendMessage("§c❌ "+lang.getString("comandos.list.erro"));
                    return Command.SINGLE_SUCCESS;
                })
        );

        // Comando para mostrar nível das relíquias
        root.then(Commands.literal("level")
                .executes(ctx -> {
                    if(ctx.getSource().getExecutor() instanceof Player player){
                        List<NamespacedKey> keys = NexusKeys.getKeyLevel();
                        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                        player.sendMessage("§6📊 "+lang.getString("comandos.level.msg"));
                        for(NamespacedKey k:keys){
                            int l = dataPlayer.getOrDefault(k, PersistentDataType.INTEGER,0);
                            if(l>0){
                                player.sendMessage("§a✅ "+k.getKey()+": "+l);
                            }else{
                                player.sendMessage("§c❌ "+k.getKey()+": "+lang.getString("comandos.level.sem"));
                            }
                        }
                    }else ctx.getSource().getSender().sendMessage("§c❌ "+lang.getString("comandos.level.erro"));
                    return Command.SINGLE_SUCCESS;
                })
        );

        // Comando para definir nível da relíquia (OP)
        root.then(Commands.literal("setlevel")
                .then(Commands.argument("level", IntegerArgumentType.integer())
                        .requires(sender -> sender.getSender().isOp())
                        .executes(ctx -> {
                            final CommandSender sender = ctx.getSource().getSender();
                            if(ctx.getSource().getExecutor() instanceof Player player){
                                int level = ctx.getArgument("level", int.class);
                                ItemStack stack = player.getInventory().getItemInMainHand();
                                ItemMeta meta = stack.getItemMeta();
                                if (meta == null) {
                                    sender.sendMessage("§c❌ Você precisa estar segurando uma relíquia.");
                                    return Command.SINGLE_SUCCESS;
                                }
                                PersistentDataContainer data = meta.getPersistentDataContainer();
                                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                                    String nome = data.get(NEXUS.key, PersistentDataType.STRING);
                                    if(nome!=null){
                                        NamespacedKey key = NexusKeys.getKey(nome);
                                        if(key!=null){
                                            player.getPersistentDataContainer().set(key,PersistentDataType.INTEGER,level);
                                            sender.sendMessage("§a✅ Nível da relíquia definido para " + level + "!");
                                        }
                                    }
                                } else {
                                    sender.sendMessage("§c❌ Você precisa estar segurando uma relíquia válida.");
                                }
                            }else {
                                sender.sendMessage("§c❌ "+lang.getString("comandos.level.erro"));
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
        );

        // Comando para ligar/desligar expurgo (OP)
        root.then(Commands.literal("exp")
                .then(Commands.argument("exp", BoolArgumentType.bool())
                        .requires(sender -> sender.getSender().isOp())
                        .executes(ctx -> {
                            boolean exp = ctx.getArgument("exp", boolean.class);
                            config.set("expurgo",exp);
                            saveConfig();
                            if(exp){
                                Bukkit.getOnlinePlayers().forEach(player -> {
                                    player.sendMessage("§c⚔️ "+lang.getString("comandos.expurgar.msg.aviso"));
                                    player.sendMessage("§c⚔️ "+lang.getString("comandos.expurgar.msg.perigo"));
                                    player.sendMessage("§c⚔️ "+lang.getString("comandos.expurgar.msg.aviso"));
                                });
                            }else{
                                Bukkit.getOnlinePlayers().forEach(player -> {
                                    player.sendMessage("§a🛡️ "+lang.getString("comandos.expurgar.msg.aviso"));
                                    player.sendMessage("§a🛡️ "+lang.getString("comandos.expurgar.msg.seguro"));
                                    player.sendMessage("§a🛡️ "+lang.getString("comandos.expurgar.msg.aviso"));
                                });
                            }
                            ctx.getSource().getSender().sendMessage("§a✅ "+lang.getString("comandos.expurgar.log")+" "+exp);
                            return Command.SINGLE_SUCCESS;
                        }))
        );

        // Comando para definir limite de relíquias (OP)
        root.then(Commands.literal("limite")
                .then(Commands.argument("valor", IntegerArgumentType.integer())
                        .requires(sender -> sender.getSender().isOp())
                        .executes(ctx -> {
                            final Integer valor = ctx.getArgument("valor", Integer.class);
                            final CommandSender sender = ctx.getSource().getSender();
                            if(valor<1){
                                sender.sendMessage("§c❌ "+lang.getString("comandos.limite.erro"));
                                return Command.SINGLE_SUCCESS;
                            }
                            config.set("limite",valor);
                            saveConfig();
                            sender.sendMessage("§a✅ "+lang.getString("comandos.limite.sucesso")+" "+valor);
                            return Command.SINGLE_SUCCESS;
                        }))
        );

        // Comando para abrir a loja
        root.then(Commands.literal("loja")
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("§c❌ Este comando só pode ser usado por um jogador.");
                        return Command.SINGLE_SUCCESS;
                    }

                    Inventory lojaInv = Bukkit.createInventory(null, 27, Component.text("§6🏪 Loja de Relíquias"));

                    // Item informativo da loja
                    ItemStack info = new ItemStack(Material.PAPER);
                    ItemMeta infoMeta = info.getItemMeta();
                    infoMeta.displayName(Component.text("§e🏪 Sistema de Loja"));
                    infoMeta.lore(Arrays.asList(
                            Component.text("§c⚠️ Sistema em desenvolvimento"),
                            Component.text("§7Em breve você poderá comprar"),
                            Component.text("§7relíquias especiais aqui!"),
                            Component.text(""),
                            Component.text("§a🎁 Volte em atualizações futuras!")
                    ));
                    info.setItemMeta(infoMeta);
                    lojaInv.setItem(13, info);

                    player.openInventory(lojaInv);
                    player.sendMessage("§a🏪 Menu da loja aberto!! Pressione ESC para fechá-lo");
                    return Command.SINGLE_SUCCESS;
                })
        );

        // Comando para ver a carteira
        root.then(Commands.literal("carteira")
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("§c❌ Este comando só pode ser usado por um jogador.");
                        return Command.SINGLE_SUCCESS;
                    }

                    double moly = getMoly(player.getUniqueId());
                    double molyBanco = getMolyBanco(player.getUniqueId());

                    player.sendMessage("§6💰 === SUA CARTEIRA ===");
                    player.sendMessage("§e💵 Moly na carteira: §a" + moly);
                    player.sendMessage("§e🏦 Moly no banco: §a" + molyBanco);
                    player.sendMessage("§e💎 Total: §a" + (moly + molyBanco));

                    // Verificar se tem empréstimo
                    if (emprestimos.containsKey(player.getUniqueId())) {
                        Emprestimo emp = emprestimos.get(player.getUniqueId());
                        double valorDevido = emp.getValorDevido();
                        player.sendMessage("§c⚠️ EMPRÉSTIMO PENDENTE!");
                        player.sendMessage("§c🏦 Banco: " + emp.getBanco());
                        player.sendMessage("§c💸 Valor devido: " + valorDevido + " Moly");
                        player.sendMessage("§c💳 Use /nexus emprestimo pagar para quitar sua dívida");
                    }

                    player.sendMessage("§a🤔 Gostaria de fazer um empréstimo com o banco?");
                    return Command.SINGLE_SUCCESS;
                })
        );

        // Comando para o sistema de empréstimos
        root.then(Commands.literal("emprestimo")
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("§c❌ Este comando só pode ser usado por um jogador.");
                        return Command.SINGLE_SUCCESS;
                    }

                    player.sendMessage("§6💳 === SISTEMA DE EMPRÉSTIMOS ===");
                    player.sendMessage("§a📝 /nexus emprestimo pedir - Pedir um empréstimo");
                    player.sendMessage("§a💵 /nexus emprestimo pagar - Pagar empréstimo");
                    player.sendMessage("§a📊 /nexus emprestimo info - Informações do seu empréstimo");
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("pedir")
                        .executes(ctx -> {
                            final Player player = (Player) ctx.getSource().getExecutor();
                            if(player==null)return Command.SINGLE_SUCCESS;
                            if (emprestimos.containsKey(player.getUniqueId())) {
                                player.sendMessage("§c❌ Você já tem um empréstimo pendente!");
                                return Command.SINGLE_SUCCESS;
                            }
                            player.sendMessage("§6🏦 Qual banco você quer pedir empréstimo?");
                            player.sendMessage("§a📋 Bancos disponíveis:");

                            for (String bancoNome : bancos.keySet()) {
                                Banco banco = bancos.get(bancoNome);
                                if (banco.isAprovado()) {
                                    player.sendMessage("§e- " + bancoNome + " (Max: " + banco.getMaxEmprestimo() + " Moly)");
                                }
                            }
                            player.sendMessage("§e- 🏛️ Banco Central Nexus (Max: 2000 Moly)");

                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("banco", StringArgumentType.string())
                                .then(Commands.argument("valor", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> {
                                            final Player player = (Player) ctx.getSource().getExecutor();
                                            String bancoNome = ctx.getArgument("banco", String.class);
                                            double valor = ctx.getArgument("valor", Double.class);

                                            if (bancoNome.equalsIgnoreCase("Banco Central Nexus")) {
                                                if (valor > 2000) {
                                                    player.sendMessage("§c❌ O Banco Central só empresta até 2000 Moly!");
                                                    return Command.SINGLE_SUCCESS;
                                                }
                                                // Lógica para empréstimo do banco central
                                                Emprestimo emprestimo = new Emprestimo(player.getUniqueId(), "Banco Central Nexus", valor, 0.5);
                                                emprestimos.put(player.getUniqueId(), emprestimo);
                                                addMoly(player.getUniqueId(), valor);

                                                player.sendMessage("§a✅ Empréstimo concedido! Você recebeu " + valor + " Moly");
                                                player.sendMessage("§c⚠️ ATENÇÃO: Você tem 3 dias para pagar!");
                                                player.sendMessage("§c📅 Se passar os 3 dias você será taxado em 5% por dia!");
                                                player.sendMessage("§c❌ Se não pagar em 10 dias você não terá acesso ao banco nem trocas!!");
                                                player.sendMessage("§c💸 Valor total a pagar: " + emprestimo.getValorTotal() + " Moly");

                                            } else if (bancos.containsKey(bancoNome)) {
                                                Banco banco = bancos.get(bancoNome);
                                                if (!banco.isAprovado()) {
                                                    player.sendMessage("§c❌ Este banco não está aprovado!");
                                                    return Command.SINGLE_SUCCESS;
                                                }
                                                if (valor > banco.getMaxEmprestimo()) {
                                                    player.sendMessage("§c❌ Este banco só empresta até " + banco.getMaxEmprestimo() + " Moly!");
                                                    return Command.SINGLE_SUCCESS;
                                                }
                                                if (valor > banco.getSaldo()) {
                                                    player.sendMessage("§c❌ O banco não tem saldo suficiente!");
                                                    return Command.SINGLE_SUCCESS;
                                                }

                                                // Verificar chance de sucesso
                                                Random random = new Random();
                                                if (random.nextDouble() > banco.getTaxaSucesso()) {
                                                    player.sendMessage("§c❌ Seu pedido de empréstimo foi negado!");
                                                    return Command.SINGLE_SUCCESS;
                                                }

                                                Emprestimo emprestimo = new Emprestimo(player.getUniqueId(), bancoNome, valor, banco.getTaxaJuros());
                                                emprestimos.put(player.getUniqueId(), emprestimo);
                                                banco.setSaldo(banco.getSaldo() - valor);
                                                addMoly(player.getUniqueId(), valor);

                                                player.sendMessage("§a✅ Empréstimo concedido! Você recebeu " + valor + " Moly");
                                                player.sendMessage("§c⚠️ ATENÇÃO: Você tem 3 dias para pagar!");
                                                player.sendMessage("§c💸 Valor total a pagar: " + emprestimo.getValorTotal() + " Moly");

                                            } else {
                                                player.sendMessage("§c❌ Banco não encontrado!");
                                            }
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                )
                .then(Commands.literal("pagar")
                        .executes(ctx -> {
                            final Player player = (Player) ctx.getSource().getExecutor();
                            if (!emprestimos.containsKey(player.getUniqueId())) {
                                player.sendMessage("§c❌ Você não tem empréstimos pendentes!");
                                return Command.SINGLE_SUCCESS;
                            }

                            Emprestimo emprestimo = emprestimos.get(player.getUniqueId());
                            double valorDevido = emprestimo.getValorDevido();
                            double moly = getMoly(player.getUniqueId());

                            if (moly >= valorDevido) {
                                removeMoly(player.getUniqueId(), valorDevido);
                                emprestimos.remove(player.getUniqueId());

                                // Devolver o valor ao banco
                                if (!emprestimo.getBanco().equals("Banco Central Nexus")) {
                                    Banco banco = bancos.get(emprestimo.getBanco());
                                    if (banco != null) {
                                        banco.setSaldo(banco.getSaldo() + valorDevido);
                                    }
                                }

                                player.sendMessage("§a✅ Empréstimo pago com sucesso!");
                            } else {
                                player.sendMessage("§c❌ Você não tem Moly suficiente! Necessário: " + valorDevido);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );

        // Comando para o servidor-economia
        root.then(Commands.literal("servidor-economia")
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    double totalMoly = getMolyTotalNoSistema();

                    sender.sendMessage("§6🏛️ === BANCO CENTRAL NEXUS ===");
                    sender.sendMessage("§e💰 Total de Moly no sistema: §a" + totalMoly);
                    sender.sendMessage("§a💸 Temos valor para emprestar para você!");
                    sender.sendMessage("§e📈 Limite máximo por empréstimo: §a2000 Moly");
                    return Command.SINGLE_SUCCESS;
                })
        );

        // Comando para o sistema bancário
        root.then(Commands.literal("banco")
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    sender.sendMessage("§6🏦 === SISTEMA BANCÁRIO ===");
                    sender.sendMessage("§a📊 /nexus banco info - Informações dos bancos");
                    sender.sendMessage("§a📝 /nexus banco criar-info - Info para criar banco");
                    sender.sendMessage("§a➕ /nexus banco criar - Criar um banco");
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("info")
                        .executes(ctx -> {
                            final CommandSender sender = ctx.getSource().getSender();
                            sender.sendMessage("§6🏦 === BANCOS EXISTENTES ===");

                            if (bancos.isEmpty()) {
                                sender.sendMessage("§c❌ Nenhum banco cadastrado no momento.");
                                return Command.SINGLE_SUCCESS;
                            }

                            for (String nomeBanco : bancos.keySet()) {
                                Banco banco = bancos.get(nomeBanco);
                                if (banco.isAprovado()) {
                                    sender.sendMessage("§e" + nomeBanco + " - Saldo: §a" + banco.getSaldo() + " Moly");
                                    sender.sendMessage("§e  📈 Max Empréstimo: §a" + banco.getMaxEmprestimo() + " Moly");
                                    sender.sendMessage("§e  📊 Taxa de Sucesso: §a" + (banco.getTaxaSucesso() * 100) + "%");
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("criar-info")
                        .executes(ctx -> {
                            final CommandSender sender = ctx.getSource().getSender();
                            sender.sendMessage("§6📝 === INFORMAÇÕES PARA CRIAR BANCO ===");
                            sender.sendMessage("§a💰 Para criar um banco você precisa:");
                            sender.sendMessage("§e- Pagar 10k de Moly à vista 💵 OU");
                            sender.sendMessage("§e- Pagar em 10x de 1k Moly (1k a cada 5 dias) 📅");
                            sender.sendMessage("§e📊 Após pagar os 10k, precisará pagar 250 Moly de imposto a cada 5 dias");
                            sender.sendMessage("§e💸 Você pode fazer empréstimos para jogadores");
                            sender.sendMessage("§e🏪 Terá licença de loja especial");
                            sender.sendMessage("§a➕ Use /nexus banco criar para iniciar");
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .then(Commands.literal("criar")
                        .executes(ctx -> {
                            final Player player = (Player) ctx.getSource().getExecutor();

                            // Verificar se já tem banco
                            for (Banco banco : bancos.values()) {
                                if (banco.getDono().equals(player.getUniqueId())) {
                                    player.sendMessage("§c❌ Você já tem um banco!");
                                    return Command.SINGLE_SUCCESS;
                                }
                            }

                            player.sendMessage("§6➕ === CRIAR BANCO ===");
                            player.sendMessage("§a💰 Escolha a forma de pagamento:");
                            player.sendMessage("§e1 - Pagar 10k de Moly à vista 💵");
                            player.sendMessage("§e2 - Pagar em 10x de 1k Moly 📅");
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("opcao", IntegerArgumentType.integer(1, 2))
                                .executes(ctx -> {
                                    final Player player = (Player) ctx.getSource().getExecutor();
                                    int opcao = ctx.getArgument("opcao", Integer.class);
                                    double moly = getMoly(player.getUniqueId());

                                    if (opcao == 1) {
                                        if (moly < 10000) {
                                            player.sendMessage("§c❌ Você não tem 10k Moly!");
                                            return Command.SINGLE_SUCCESS;
                                        }
                                        removeMoly(player.getUniqueId(), 10000);
                                        player.sendMessage("§a✅ Pagamento realizado! Seu pedido foi enviado para análise.");

                                    } else if (opcao == 2) {
                                        if (moly < 1000) {
                                            player.sendMessage("§c❌ Você não tem 1k Moly para a primeira parcela!");
                                            return Command.SINGLE_SUCCESS;
                                        }
                                        removeMoly(player.getUniqueId(), 1000);
                                        // Registrar pagamento parcelado
                                        config.set("banco.pagamento." + player.getUniqueId() + ".parcelas", 1);
                                        config.set("banco.pagamento." + player.getUniqueId() + ".total", 1000);
                                        saveConfig();

                                        player.sendMessage("§a✅ Primeira parcela paga! Restam 9 parcelas de 1k Moly.");
                                    }

                                    player.sendMessage("§a📨 Seu pedido foi enviado aos administradores!");
                                    player.sendMessage("§a👨‍💼 Eles verificarão sua autenticidade e aprovarão seu banco.");
                                    player.sendMessage("§a📱 Você será notificado via Discord sobre o status.");

                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );

        // Finaliza o registro dos comandos e eventos
        LiteralCommandNode<CommandSourceStack> buildCommand = root.build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(buildCommand));
        getServer().getPluginManager().registerEvents(new JoinQuitEvent(this), this);
        getServer().getPluginManager().registerEvents(new LimitadorEvent(this), this);
        getServer().getPluginManager().registerEvents(new PassivaEvent(), this);
        getServer().getPluginManager().registerEvents(new PerdeuEvent(), this);
        getServer().getPluginManager().registerEvents(new EvoluirEvent(this), this);
        getServer().getPluginManager().registerEvents(new SpecialEvent(this), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getConsoleSender().sendMessage("§2✅ [Nexus]: Plugin Ativado!");
    }

    @Override
    public void onDisable() {
        saveConfig();
        salvarDadosEconomicos();
        getServer().getConsoleSender().sendMessage("§4❌ [Nexus]: Plugin Desativado!");
    }

    // Métodos para manipulação da moeda Moly
    private double getMoly(UUID playerId) {
        return config.getDouble("moly." + playerId, 0);
    }

    private double getMolyBanco(UUID playerId) {
        return config.getDouble("moly_banco." + playerId, 0);
    }

    private void addMoly(UUID playerId, double amount) {
        double current = getMoly(playerId);
        config.set("moly." + playerId, current + amount);
        saveConfig();
    }

    private void removeMoly(UUID playerId, double amount) {
        double current = getMoly(playerId);
        config.set("moly." + playerId, Math.max(0, current - amount));
        saveConfig();
    }

    private double getMolyTotalNoSistema() {
        double total = 0;
        ConfigurationSection section = config.getConfigurationSection("moly");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                total += section.getDouble(key);
            }
        }
        return total;
    }

    private void carregarDadosEconomicos() {
        // Carregar empréstimos
        ConfigurationSection emprestimosSection = config.getConfigurationSection("emprestimos");
        if (emprestimosSection != null) {
            for (String key : emprestimosSection.getKeys(false)) {
                UUID playerId = UUID.fromString(key);
                String banco = config.getString("emprestimos." + key + ".banco");
                double valor = config.getDouble("emprestimos." + key + ".valor");
                double juros = config.getDouble("emprestimos." + key + ".juros");
                long data = config.getLong("emprestimos." + key + ".data");

                Emprestimo emprestimo = new Emprestimo(playerId, banco, valor, juros);
                emprestimos.put(playerId, emprestimo);
            }
        }

        // Carregar bancos
        ConfigurationSection bancosSection = config.getConfigurationSection("bancos");
        if (bancosSection != null) {
            for (String nome : bancosSection.getKeys(false)) {
                UUID dono = UUID.fromString(config.getString("bancos." + nome + ".dono"));
                double saldo = config.getDouble("bancos." + nome + ".saldo");
                double maxEmprestimo = config.getDouble("bancos." + nome + ".maxEmprestimo");
                double taxaJuros = config.getDouble("bancos." + nome + ".taxaJuros");
                double taxaSucesso = config.getDouble("bancos." + nome + ".taxaSucesso");
                String descricao = config.getString("bancos." + nome + ".descricao");
                boolean aprovado = config.getBoolean("bancos." + nome + ".aprovado");

                Banco banco = new Banco(nome, dono, saldo);
                banco.setMaxEmprestimo(maxEmprestimo);
                banco.setTaxaJuros(taxaJuros);
                banco.setTaxaSucesso(taxaSucesso);
                banco.setDescricao(descricao);
                banco.setAprovado(aprovado);

                bancos.put(nome, banco);
            }
        }
    }

    private void salvarDadosEconomicos() {
        // Salvar empréstimos
        for (Map.Entry<UUID, Emprestimo> entry : emprestimos.entrySet()) {
            UUID playerId = entry.getKey();
            Emprestimo emprestimo = entry.getValue();

            config.set("emprestimos." + playerId + ".banco", emprestimo.getBanco());
            config.set("emprestimos." + playerId + ".valor", emprestimo.getValor());
            config.set("emprestimos." + playerId + ".juros", 0.5); // Juros padrão
            config.set("emprestimos." + playerId + ".data", emprestimo.getDataContracao());
        }

        // Salvar bancos
        for (Map.Entry<String, Banco> entry : bancos.entrySet()) {
            String nome = entry.getKey();
            Banco banco = entry.getValue();

            config.set("bancos." + nome + ".dono", banco.getDono().toString());
            config.set("bancos." + nome + ".saldo", banco.getSaldo());
            config.set("bancos." + nome + ".maxEmprestimo", banco.getMaxEmprestimo());
            config.set("bancos." + nome + ".taxaJuros", banco.getTaxaJuros());
            config.set("bancos." + nome + ".taxaSucesso", banco.getTaxaSucesso());
            config.set("bancos." + nome + ".descricao", banco.getDescricao());
            config.set("bancos." + nome + ".aprovado", banco.isAprovado());
        }

        saveConfig();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        if (title.equals("§5Missões") || title.equals("§6🏪 Loja de Relíquias")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String nomePersonalizado = config.getString("nomes." + player.getUniqueId());
        if (nomePersonalizado != null && !nomePersonalizado.isEmpty()) {
            player.setDisplayName(nomePersonalizado);
            player.setPlayerListName(nomePersonalizado);
            player.setCustomName(nomePersonalizado);
        }

        // Verificar empréstimos vencidos
        if (emprestimos.containsKey(player.getUniqueId())) {
            Emprestimo emprestimo = emprestimos.get(player.getUniqueId());
            long diasPassados = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - emprestimo.getDataContracao());

            if (diasPassados > 3) {
                player.sendMessage("§c⚠️ SEU EMPRÉSTIMO ESTÁ ATRASADO!");
                player.sendMessage("§c📅 Dias de atraso: " + (diasPassados - 3));
                player.sendMessage("§c💸 Valor atual devido: " + emprestimo.getValorDevido() + " Moly");
            }

            if (diasPassados > 10) {
                player.sendMessage("§4❌ ACESSO AO BANCO BLOQUEADO!");
                player.sendMessage("§4🚫 Você não pagou seu empréstimo em 10 dias!");
            }
        }
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
                player.sendMessage("§c❌ "+m);
            }
        }
    }
}
