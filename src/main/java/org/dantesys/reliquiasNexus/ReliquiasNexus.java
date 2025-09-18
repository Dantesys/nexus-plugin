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
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dantesys.reliquiasNexus.eventos.*;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.loja.LojaManager;
import org.dantesys.reliquiasNexus.missoes.Missao;
import org.dantesys.reliquiasNexus.missoes.MissoesManager;
import org.dantesys.reliquiasNexus.tab.PlayerListManager;
import org.dantesys.reliquiasNexus.util.*;
import org.dantesys.reliquiasNexus.bosses.BossManager;
import org.dantesys.reliquiasNexus.bosses.BossRarity;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


import static org.dantesys.reliquiasNexus.util.NexusKeys.*;
/*
* TODO
*  Modificar Sistema de loja e criar sistema de loja comunitaria - Testando
*  Criar Comando TPA - Testanddo
*  Criar Comando para definir custo de tpa - Testando
*  Criar Comando para setar o nome do dinheiro - Testando
*  Ajustar arquivo de tradução - Fazendo e Testando
*  Refazer sistema de procurado - PENDENTE
*  Refazer sistema de bosses - PENDENTE
*  Refazer sistema de economia - Testando
*/
public final class ReliquiasNexus extends JavaPlugin {
    private static final Map<UUID, Troca> trocas = new ConcurrentHashMap<>();
    private static final Map<UUID, List<Missao>> missoesOfertas = new ConcurrentHashMap<>();
    private final Map<UUID,TpaRequest> tpaRequests = new HashMap<>();
    private static FileConfiguration config;
    private static YamlConfiguration lang;
    private static YamlConfiguration missaoAtivaBK;
    private static YamlConfiguration lojaSV;
    private MissoesManager missoesManager;
    private LojaManager lojaManager;
    private BossManager bossManager;
    public final List<String> names = List.of("guerreiro","ceifador","vida","mares","barbaro",
            "fazendeiro","espiao","arqueiro","cacador","tempestade","mineiro","fenix","protetor",
            "hulk","sculk","pescador","flash","mago","ladrao","domador","cozinheiro","construtor",
            "abissal","cronosombra","assassino","frostis","necromante","alquimista","golem","dragao");

    @Override
    public void onEnable() {
        NexusKeys.init(this);
        ItemsRegistro.init();
        saveResource("lang/pt-br.yml",true);
        saveResource("lang/en-us.yml",true);
        saveResource("missaoAtiva.yml",false);
        saveResource("loja.yml",false);
        saveDefaultConfig();
        config = getConfig();
        String tipo = config.getString("lang");
        if(tipo==null){
            tipo="en-us";
            config.set("lang","en-us");
        }
        File file = new File(this.getDataFolder(), "/lang/"+tipo+".yml");
        lang = YamlConfiguration.loadConfiguration(file);
        lojaManager = new LojaManager(this,lang);
        missoesManager=new MissoesManager(this);
        File ms = new File(this.getDataFolder(), "missaoAtiva.yml");
        missaoAtivaBK = YamlConfiguration.loadConfiguration(ms);
        List<String> uuids = missaoAtivaBK.getStringList("players");
        for (String uuid : uuids) {
            missoesManager.aceitarMissao(UUID.fromString(uuid),missaoAtivaBK);
        }
        missaoAtivaBK.set("players",List.of());
        saveConfig();
        try {
            lang.save(file);
            missaoAtivaBK.save(ms);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File lojafile = new File(this.getDataFolder(), "loja.yml");
        lojaSV = YamlConfiguration.loadConfiguration(lojafile);
        if(config.get("servidor") == null){
            lojaManager.gerarDefault(lojaSV);
        }else{
            lojaManager.load(lojaSV);
        }
        // Inicializar o gerenciador da player list e do boss
        PlayerListManager playerListManager = new PlayerListManager(this);
        bossManager = new BossManager(this);
        new UpdaterCheck(this, "dantesys/nexus-plugin").checkForUpdates();
        // Comando /nexus
        LiteralArgumentBuilder<CommandSourceStack> nexusRoot = Commands.literal("nexus").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            List<String> nexus = lang.getStringList("nexus");
            nexus.forEach(texto ->{
                String revisado;
                if(!texto.equals(nexus.getFirst()))revisado = "§7➤ §b"+texto.replace("<div>","§f-")+"\n";
                else revisado = "\n§6§l⭐ §e§l"+texto+" §6§l⭐\n";
                sender.sendMessage(Component.text(revisado));
            });
            sender.sendMessage(Component.text("§6§l⭐ §e§l"+nexus.getFirst()+" §6§l⭐\n").decorate(TextDecoration.BOLD));
            return Command.SINGLE_SUCCESS;
        });
        // Comando /nexus livro
        nexusRoot.then(Commands.literal(lang.getString("livro.comando","livro")).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                player.getInventory().addItem(ItemsRegistro.livro.getItem(1));
                sender.sendMessage(Component.text("📖 "+lang.getString("livro.sucesso","Você recebeu o Livro das Relíquias!")).color(NamedTextColor.GREEN));
            }else{
                sender.sendMessage(Component.text("❌ "+lang.getString("livro.falha","Apenas jogadores podem usar este comando!")).color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));
        // Comando /nexus historia
        nexusRoot.then(Commands.literal(lang.getString("historia.comando","historia")).executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (ctx.getSource().getExecutor() instanceof Player player) {
                player.getInventory().addItem(ItemsRegistro.nexusStoryBook.getItem(1));
                sender.sendMessage(Component.text("📖 "+lang.getString("historia.sucesso","Você recebeu o livro Nexus Story!")).color(NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("❌ "+lang.getString("historia.sucesso","Apenas jogadores podem usar este comando!")).color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));
        // Comando /nexus evoluir
        nexusRoot.then(Commands.literal(lang.getString("evoluir.comando","evoluir")).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(!stack.hasItemMeta()){
                    sender.sendMessage(Component.text("❌ "+lang.getString("evoluir.falhaMao","Você precisa segurar uma relíquia Nexus na mão!"))
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }
                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    EvoluirEvent evo = new EvoluirEvent(this);
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    if(nome!=null){
                        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                        NamespacedKey key = getKey(nome);
                        if(key!=null){
                            int level = dataPlayer.getOrDefault(key, PersistentDataType.INTEGER, 1);
                            evo.tentarEvoluir(player,stack,level,evo.getSlotOfItem(player,stack));
                        }
                    }else{
                        sender.sendMessage(Component.text("❌ "+lang.getString("evoluir.falhaInvalida","Relíquia inválida!"))
                                .color(NamedTextColor.RED));
                    }
                }else{
                    sender.sendMessage(Component.text("❌ "+lang.getString("evoluir.falhaMao","Você precisa segurar uma relíquia Nexus na mão!"))
                            .color(NamedTextColor.RED));
                }
            }else{
                sender.sendMessage(Component.text("❌ "+lang.getString("evoluir.falhaPlayer","Apenas jogadores podem usar este comando!"))
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));
        // Comando /nexus expurgo
        nexusRoot.then(Commands.literal(lang.getString("expurgo.comando","expurgo")).executes(ctx -> {
            boolean expurgo = config.getBoolean("expurgo");
            if(expurgo){
                ctx.getSource().getSender().sendMessage(Component.text("⚡ "+lang.getString("expurgo.ativado","MODO EXPURGO ATIVADO!<break>⚠ Todas as relíquias podem ser roubadas!").replace("<break>","\n"))
                        .color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
            }else{
                ctx.getSource().getSender().sendMessage(Component.text("🛡️ "+lang.getString("expurgo.desativado","MODO EXPURGO DESATIVADO<break>✔ Suas relíquias estão seguras!").replace("<break>","\n"))
                        .color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            }
            return Command.SINGLE_SUCCESS;
        }));
        // Comando /nexus trocar
        nexusRoot.then(Commands.literal(lang.getString("troca.comando","troca")).then(Commands.argument("jogador", ArgumentTypes.player()).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo") || !config.getBoolean("recursos.troca")) {
                sender.sendMessage(Component.text("❌ "+lang.getString("troca.desativado","O comando trocar está desativado")).color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if(ctx.getSource().getExecutor() instanceof Player player){
                final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogador", PlayerSelectorArgumentResolver.class);
                final Player p = targetResolver.resolve(ctx.getSource()).getFirst();
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(!stack.hasItemMeta()){
                    sender.sendMessage(Component.text("❌ "+lang.getString("troca.falhaMao","Você precisa segurar uma relíquia Nexus na mão!"))
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }
                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    if(nome!=null){
                        // Salva a oferta de troca com o nome da relíquia do jogador que oferece
                        Troca t = new Troca(player.getUniqueId(), nome);
                        trocas.put(p.getUniqueId(), t);
                        // Mensagem para o jogador que ofereceu
                        List<String> oferta = lang.getStringList("troca.oferta");
                        Component msgEnvio = Component.text()
                                .append(Component.text("✅ "+oferta.getFirst().replace("<player>",player.getName())+"\n")
                                        .color(NamedTextColor.GREEN))
                                .append(Component.text(oferta.get(1).replace("<nexus>",nome) + "\n")
                                        .color(NamedTextColor.GRAY))
                                .append(Component.text(oferta.get(2))
                                        .color(NamedTextColor.GRAY))
                                .build();
                        sender.sendMessage(msgEnvio);
                        // Mensagem clicável para o jogador que recebeu
                        List<String> recebido = lang.getStringList("troca.recebido");
                        Component msgRecebido = Component.text()
                                .append(Component.text("\n🎁 "+recebido.getFirst()+"\n")
                                        .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                                .append(Component.text(recebido.get(1).replace("<player>",player.getName()) + "\n")
                                        .color(NamedTextColor.GRAY))
                                .append(Component.text(recebido.get(2).replace("<nexus>",nome) + "\n\n")
                                        .color(NamedTextColor.GRAY))
                                .append(Component.text("✅ "+lang.getString("troca.aceita","ACEITAR"))
                                        .color(NamedTextColor.GREEN)
                                        .clickEvent(ClickEvent.runCommand("/nexus aceitar " + player.getUniqueId().toString()))
                                        .hoverEvent(HoverEvent.showText(Component.text(lang.getString("troca.aceitaMsg","Clique para aceitar a troca")))))
                                .append(Component.text("❌ "+lang.getString("troca.recusa","RECUSAR"))
                                        .color(NamedTextColor.RED)
                                        .clickEvent(ClickEvent.runCommand("/nexus cancelar " + player.getUniqueId().toString()))
                                        .hoverEvent(HoverEvent.showText(Component.text(lang.getString("troca.recusaMsg","Clique para recusar a troca")))))
                                .build();

                        p.sendMessage(msgRecebido);
                    }
                }else{
                    sender.sendMessage(Component.text("❌ "+lang.getString("troca.falhaMao","Você precisa segurar uma relíquia Nexus na mão!"))
                            .color(NamedTextColor.RED));
                }
            }else{
                sender.sendMessage(Component.text("❌ "+lang.getString("troca.falhaPlayer","Apenas jogadores podem usar este comando!"))
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        })));
        // Comando aceitar (oculto)
        // Note: A visibilidade deste comando é intencionalmente restrita para não aparecer nas sugestões.
        nexusRoot.then(Commands.literal("aceitar").then(Commands.argument("offerer", StringArgumentType.string()).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo") || !config.getBoolean("recursos.troca")) {
                sender.sendMessage(Component.text("❌ "+lang.getString("troca.desativado","O comando trocar está desativada")).color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if (ctx.getSource().getExecutor() instanceof Player player) {
                String offererId = ctx.getArgument("offerer", String.class);
                UUID offererUuid = UUID.fromString(offererId);
                Player offerer = Bukkit.getPlayer(offererUuid);
                Troca t = trocas.get(player.getUniqueId());
                if (t == null || !t.offererUuid().equals(offererUuid)) {
                    sender.sendMessage(Component.text("❌ "+lang.getString("troca.invalida","Esta oferta não é mais válida!"))
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }
                if (offerer == null || !offerer.isOnline()) {
                    trocas.remove(player.getUniqueId());
                    sender.sendMessage(Component.text("❌ "+lang.getString("troca.falhaSaiu","O jogador que ofereceu a troca saiu do servidor!"))
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(!stack.hasItemMeta() || !stack.getItemMeta().getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                    sender.sendMessage(Component.text("❌ "+lang.getString("troca.falhaMao","Você precisa segurar uma relíquia Nexus na mão!"))
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }
                String relicToGive = stack.getItemMeta().getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                t.setPlayer2Relic(relicToGive);
                sender.sendMessage(Component.text("✅ "+lang.getString("troca.aceitou","Você aceitou a troca! A contagem regressiva irá começar."))
                        .color(NamedTextColor.GREEN));
                offerer.sendMessage(Component.text("✅ "+lang.getString("troca.aceitouSend","<player> aceitou sua oferta! A troca será concluída em instantes.").replace("<player>",player.getName()))
                        .color(NamedTextColor.GREEN));

                new BukkitRunnable() {
                    private int count = 5;
                    @Override
                    public void run() {
                        if (count > 0) {
                            NamedTextColor color = NamedTextColor.GREEN;
                            if (count <= 3) color = NamedTextColor.YELLOW;
                            if (count <= 2) color = NamedTextColor.GOLD;
                            if (count <= 1) color = NamedTextColor.RED;

                            Component countdownMessage = Component.text("⚡ "+lang.getString("troca.contador","Troca em andamento: ") + count + "...", color);
                            player.sendMessage(countdownMessage);
                            offerer.sendMessage(countdownMessage);
                            count--;
                        } else {
                            if (processarTroca(offerer, player, t.offeredRelicName(), t.player2Relic())) {
                                player.sendMessage(Component.text("✅ "+lang.getString("troca.sucesso","Troca finalizada com sucesso!")).color(NamedTextColor.GREEN));
                                offerer.sendMessage(Component.text("✅ "+lang.getString("troca.sucesso","Troca finalizada com sucesso!")).color(NamedTextColor.GREEN));
                                trocas.remove(player.getUniqueId());
                            } else {
                                player.sendMessage(Component.text("❌ "+lang.getString("troca.falha","Falha na troca. Relíquias não encontradas.")).color(NamedTextColor.RED));
                                offerer.sendMessage(Component.text("❌ "+lang.getString("troca.falha","Falha na troca. Relíquias não encontradas.")).color(NamedTextColor.RED));
                                trocas.remove(player.getUniqueId());
                            }
                            this.cancel();
                        }
                    }
                }.runTaskTimer(this, 0L, 20L);
            }
            return Command.SINGLE_SUCCESS;
        })));
        // Comando cancelar (oculto)
        // Note: A visibilidade deste comando é intencionalmente restrita para não aparecer nas sugestões.
        nexusRoot.then(Commands.literal("cancelar").then(Commands.argument("offerer", StringArgumentType.string()).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo") || !config.getBoolean("recursos.troca")) {
                sender.sendMessage(Component.text("❌ "+lang.getString("troca.desativado","O comando trocar está desativada")).color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if (ctx.getSource().getExecutor() instanceof Player player) {
                String offererId = ctx.getArgument("offerer", String.class);
                UUID offererUuid = UUID.fromString(offererId);
                Player offerer = Bukkit.getPlayer(offererUuid);
                Troca t = trocas.remove(player.getUniqueId());

                if (t != null && t.offererUuid().equals(offererUuid)) {
                    if (offerer != null && offerer.isOnline()) {
                        offerer.sendMessage(Component.text("❌ "+lang.getString("troca.recusouSend","<player> recusou sua oferta de troca!").replace("<player>",player.getName()))
                                .color(NamedTextColor.RED));
                    }
                    sender.sendMessage(Component.text("❌ "+lang.getString("troca.recusou","Você recusou a oferta de troca."))
                            .color(NamedTextColor.RED));
                } else {
                    sender.sendMessage(Component.text("❌ "+lang.getString("troca.invalida","Esta oferta não é mais válida!"))
                            .color(NamedTextColor.RED));
                }
            }
            return Command.SINGLE_SUCCESS;
        })));
        // Comando /nexus list
        nexusRoot.then(Commands.literal("list").executes(ctx -> {
            ConfigurationSection secao = config.getConfigurationSection("nexus");
            Component header = Component.text()
                    .append(Component.text("\n📜 "+lang.getString("list.titulo","RELÍQUIAS DO SERVIDOR")+" 📜\n")
                            .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .build();

            ctx.getSource().getSender().sendMessage(header);
            if(secao != null){
                for(String nexus: secao.getKeys(false)){
                    String uuidStr = config.getString("nexus."+nexus);
                    Component donoComponent;
                    if(uuidStr != null && !uuidStr.isBlank()){
                        try{
                            UUID uuid = UUID.fromString(uuidStr);
                            OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(uuid);
                            String nomeJogador = offlinePlayer.getName() != null ? offlinePlayer.getName() : lang.getString("list.desconhecido","Desconhecido");
                            donoComponent = Component.text(nomeJogador)
                                    .color(NamedTextColor.GREEN);
                        }catch(IllegalArgumentException ignored){
                            donoComponent = Component.text(lang.getString("list.desconhecido","Desconhecido"))
                                    .color(NamedTextColor.RED);
                        }
                    }else{
                        donoComponent = Component.text(lang.getString("list.semdono","Sem dono"))
                                .color(NamedTextColor.RED);
                    }
                    Component linha = Component.text()
                            .append(Component.text("§7• §b" + nexus + " §7- ")
                                    .color(NamedTextColor.GRAY))
                            .append(donoComponent)
                            .build();
                    ctx.getSource().getSender().sendMessage(linha);
                }
            }
            return Command.SINGLE_SUCCESS;
        }));
        // Comando /nexus level
        nexusRoot.then(Commands.literal("level").executes(ctx -> {
            if(ctx.getSource().getExecutor() instanceof Player player){
                List<NamespacedKey> keys = getKeyLevel();
                PersistentDataContainer dataPlayer = player.getPersistentDataContainer();

                Component header = Component.text()
                        .append(Component.text("\n⭐ "+lang.getString("level.titulo","SEUS NÍVEIS DE RELÍQUIAS")+" ⭐\n")
                                .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .build();
                player.sendMessage(header);
                for(NamespacedKey k: keys){
                    int l = dataPlayer.getOrDefault(k, PersistentDataType.INTEGER,0);
                    if(l > 0){
                        String nomeRelic = k.getKey().toUpperCase();
                        Component nivel = Component.text()
                                .append(Component.text("• " + nomeRelic + " - ")
                                        .color(NamedTextColor.GRAY))
                                .append(Component.text(lang.getString("level.nivel","Nível: ")+ l)
                                        .color(NamedTextColor.GREEN))
                                .build();
                        player.sendMessage(nivel);
                    }
                }
            }else{
                ctx.getSource().getSender().sendMessage(Component.text("❌ "+lang.getString("level.falha","Apenas jogadores podem usar este comando!"))
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));
        // Comando /nexus missao e /nexus missao cancelar
        nexusRoot.then(Commands.literal(lang.getString("missao.comando","missao")).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo") || !config.getBoolean("recursos.missao")) {
                sender.sendMessage(Component.text("❌ "+lang.getString("missao.desativado","O comando trocar está desativado")).color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if (ctx.getSource().getExecutor() instanceof Player player) {
                int tempo = player.getPersistentDataContainer().getOrDefault(MISSAOCOOLDOWN.key,PersistentDataType.INTEGER,0);
                int emMissao = player.getPersistentDataContainer().getOrDefault(MISSAOTEMPO.key,PersistentDataType.INTEGER,0);
                if(tempo<=0 && emMissao<=0){
                    List<Missao> plMissoes = missoesOfertas.containsKey(player.getUniqueId())?missoesOfertas.remove(player.getUniqueId()):missoesManager.gerarMissoes(player);
                    missoesOfertas.put(player.getUniqueId(),plMissoes);
                    Component msg = Component.text("\n "+lang.getString("missao.disponivel","MISSÕES DISPONIVEIS")+"\n")
                            .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
                    player.sendMessage(msg);
                    for(int i=0;i<plMissoes.size();i++){
                        Missao m = plMissoes.get(i);
                        String dif = switch(m.getDificuldade()){
                            case 2 -> "★★☆☆☆";
                            case 3 -> "★★★☆☆";
                            case 4 -> "★★★★☆";
                            case 5 -> "★★★★★";
                            default -> "★☆☆☆☆";
                        };
                        Component missao = Component.text("\n"+lang.getString("missao.tipo","Tipo: <nome> - ").replace("<nome>",m.getTipo()))
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(dif).color(NamedTextColor.YELLOW))
                                .append(Component.text("\n["+lang.getString("missao.aceitar","ACEITAR")+"]")
                                .color(NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.runCommand("/nexus missaoaceitar " + (i+1))));
                        player.sendMessage(missao);
                    }
                }else{
                    sender.sendMessage(Component.text("❌ "+lang.getString("missao.falhaTempo","Aguarde mais <time> segundos!").replace("<time>",tempo<=0?emMissao+"":tempo+"")).color(NamedTextColor.RED));
                }
                return Command.SINGLE_SUCCESS;
            }
            sender.sendMessage(Component.text("❌ "+lang.getString("missao.falhaPlayer","Apenas jogadores podem usar este comando!")).color(NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        })
                .then(Commands.literal(lang.getString("missao.cancelar","cancelar")).executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if (ctx.getSource().getExecutor() instanceof Player player) {
                        missoesManager.cancelarMissao(player);
                        player.getPersistentDataContainer().set(MISSAOCOOLDOWN.key,PersistentDataType.INTEGER,config.getInt("recursos.missaoCooldown",300));
                        player.getPersistentDataContainer().remove(MISSAOTEMPO.key);
                        sender.sendMessage(Component.text("❌ "+lang.getString("missao.cancelada","Missão cancelada!")).color(NamedTextColor.RED));
                        return Command.SINGLE_SUCCESS;
                    }
                    sender.sendMessage(Component.text("❌ "+lang.getString("missao.falhaPlayer","Apenas jogadores podem usar este comando!")).color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                })));
        // Comando missaoaceitar (oculto)
        // Note: A visibilidade deste comando é intencionalmente restrita para não aparecer nas sugestões.
        nexusRoot.then(Commands.literal("missaoaceitar").then(Commands.argument("missao",IntegerArgumentType.integer(1,5)).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo") || !config.getBoolean("recursos.missao")) {
                sender.sendMessage(Component.text("❌ "+lang.getString("missao.desativado","O comando de missões está desativado")).color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if (ctx.getSource().getExecutor() instanceof Player player) {
                List<Missao> missoes = missoesOfertas.remove(player.getUniqueId());
                int missao = ctx.getArgument("missao", int.class);
                Missao m = missoes.get(missao-1);
                if(m!=null){
                    missoesManager.aceitarMissao(player,m);
                }else{
                    sender.sendMessage(Component.text("❌ "+lang.getString("missao.falhaNao","Você etá em missão ou a missão não existe!")).color(NamedTextColor.RED));
                }
            }else{
                sender.sendMessage(Component.text("❌ "+lang.getString("missao.falhaPlayer","Apenas jogadores podem usar este comando!")).color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        })));
        // Comando /nexus loja e nexus loja vender <valor>
        nexusRoot.then(Commands.literal(lang.getString("loja.comando","loja")).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo") || !config.getBoolean("recursos.loja")) {
                sender.sendMessage(Component.text("❌ "+lang.getString("loja.desativado","O comando de loja está desativado.")).color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if (ctx.getSource().getExecutor() instanceof Player player) {
                lojaManager.abrirMenuPrincipal(player);
                return Command.SINGLE_SUCCESS;
            }
            sender.sendMessage(Component.text("❌ "+lang.getString("loja.falhaPlayer","Apenas jogadores podem usar este comando!")).color(NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        })
                .then(Commands.literal(lang.getString("loja.comandoVender","vender")).then(Commands.argument("money",DoubleArgumentType.doubleArg()).executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    if (config.getBoolean("expurgo") || !config.getBoolean("recursos.loja")) {
                        sender.sendMessage(Component.text("❌ "+lang.getString("loja.desativado","O comando de loja está desativado.")).color(NamedTextColor.RED));
                        return Command.SINGLE_SUCCESS;
                    }
                    if (ctx.getSource().getExecutor() instanceof Player player) {
                        double preco = DoubleArgumentType.getDouble(ctx,"money");
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (!item.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                            player.getInventory().remove(item);
                            List<Map<String, Object>> itensSalvosPlayers = (List<Map<String, Object>>) lojaSV.getList("players",new ArrayList<>());
                            Map<String, Object> mp = new HashMap<>();
                            mp.put("item",item);
                            mp.put("preco",preco);
                            mp.put("player",player.getUniqueId().toString());
                            itensSalvosPlayers.add(mp);
                            lojaSV.set("players",itensSalvosPlayers);
                            lojaManager.save(lojaSV);
                            sender.sendMessage(Component.text(lang.getString("loja.vendaSuccess", "Item colocado na loja com sucesso!")).color(NamedTextColor.GREEN));
                        } else {
                            sender.sendMessage(Component.text("❌ " + lang.getString("loja.falhaReliquia", "Itens Nexus não podem ser vendidos!")).color(NamedTextColor.RED));
                        }
                        return Command.SINGLE_SUCCESS;
                    }
                    sender.sendMessage(Component.text("❌ "+lang.getString("loja.falhaPlayer","Apenas jogadores podem usar este comando!")).color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }))));
        // Comando /nexus saldo
        nexusRoot.then(Commands.literal(lang.getString("saldo.comando","saldo")).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (ctx.getSource().getExecutor() instanceof Player player) {
                double saldo = player.getPersistentDataContainer().getOrDefault(SALDO.key,PersistentDataType.DOUBLE,0d);
                String formatado = String.format("%.2f", saldo);
                player.sendMessage(Component.text(lang.getString("saldo.info","Seu saldo é de $")+formatado+" "+config.getString("recursos.moneyName","Moly")).color(NamedTextColor.GREEN));
                return Command.SINGLE_SUCCESS;
            }
            sender.sendMessage(Component.text("❌ "+lang.getString("saldo.falhaPlayer","Apenas jogadores podem usar este comando!")).color(NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }));
        // Comando /nexus chatcor
        nexusRoot.then(Commands.literal("chatcor").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (ctx.getSource().getExecutor() instanceof Player player) {
                boolean cor = player.getPersistentDataContainer().getOrDefault(COR.key,PersistentDataType.BOOLEAN,false);
                player.getPersistentDataContainer().set(COR.key,PersistentDataType.BOOLEAN,!cor);
                player.sendMessage(Component.text("✅ "+lang.getString("chatcor."+(!cor?"habilitado":"desabilitado"),"Cor do rank/cargo "+(cor?"habilitada":"desabilitada"))).color(NamedTextColor.GREEN));
                return Command.SINGLE_SUCCESS;
            }
            sender.sendMessage(Component.text("❌ "+lang.getString("chatcor.falhaPlayer","Apenas jogadores podem usar este comando!")).color(NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }));
        // Comando para abrir o ender chest
        LiteralCommandNode<CommandSourceStack> ecCommandNode = Commands.literal("ec").executes(ctx -> {
            if (ctx.getSource().getExecutor() instanceof Player player) {
                PersistentDataContainer playerData = player.getPersistentDataContainer();
                if (playerData.has(ENDER_CHEST_OWNED.key, PersistentDataType.BOOLEAN) || player.isOp()) {
                    player.openInventory(player.getEnderChest());
                } else {
                    player.sendMessage(Component.text("❌ Você não tem a permissão para usar este comando! Compre o Baú do Fim na loja.").color(NamedTextColor.RED));
                }
                return Command.SINGLE_SUCCESS;
            }
            return Command.SINGLE_SUCCESS;
        }).build();
        // Comando /tpa
        LiteralArgumentBuilder<CommandSourceStack> tpaNode = Commands.literal("tpa").then(Commands.argument("player",ArgumentTypes.player()).executes(ctx->{
            final CommandSender sender = ctx.getSource().getSender();
            if(!config.getBoolean("expurgo",false) && config.getBoolean("recursos.tpa",true)){
                final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                final Player alvo = targetResolver.resolve(ctx.getSource()).getFirst();
                if(sender instanceof Player player){
                    int cd = player.getPersistentDataContainer().getOrDefault(TPACOOLDOWN.key,PersistentDataType.INTEGER,0);
                    if(cd<=0){
                        player.getPersistentDataContainer().set(TPACOOLDOWN.key,PersistentDataType.INTEGER,30);
                        TpaRequest request = new TpaRequest(player,alvo);
                        tpaRequests.put(alvo.getUniqueId(),request);
                        player.sendMessage(Component.text(lang.getString("tpa.send","Pedido enviado!"))
                                .color(NamedTextColor.GREEN));
                        alvo.sendMessage(Component.text(player.getName()+" "+lang.getString("tpa.sendPlayer","quer se teleportar até você!"))
                                .color(NamedTextColor.YELLOW));
                        alvo.sendMessage(Component.text("\n["+lang.getString("tpa.aceitar","ACEITAR")+"]")
                                .color(NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.runCommand("/tpa aceitar"))
                                .append(Component.text("\n["+lang.getString("tpa.cancelar","CANCELAR")+"]")
                                        .color(NamedTextColor.RED)
                                        .clickEvent(ClickEvent.runCommand("/tpa cancelar"))));
                    }else{
                        sender.sendMessage(Component.text("❌ Cooldown: "+cd)
                                .color(NamedTextColor.RED));
                    }
                }else{
                    sender.sendMessage(Component.text("❌ "+lang.getString("tpa.falhaPlayer","Apenas jogadores podem se teleportar!"))
                            .color(NamedTextColor.RED));
                }
            }else{
                sender.sendMessage(Component.text("❌ "+lang.getString("tpa.desativado","Comando tpa desativado!"))
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));
        // Comando /tpa aceitar
        tpaNode.then(Commands.literal("aceitar").executes(ctx->{
                    final CommandSender sender = ctx.getSource().getSender();
                    if(sender instanceof Player player){
                        TpaRequest request = tpaRequests.remove(player.getUniqueId());
                        if(request!=null){
                            Player rqPlayer = request.getRequest();
                            if(rqPlayer.isOnline()){
                                double saldo = rqPlayer.getPersistentDataContainer().getOrDefault(SALDO.key,PersistentDataType.DOUBLE,0.0);
                                double custo = config.getDouble("recursos.tpacost",100.0);
                                if(saldo>=custo){
                                    rqPlayer.getPersistentDataContainer().set(SALDO.key,PersistentDataType.DOUBLE,saldo-custo);
                                    rqPlayer.teleport(player.getLocation());
                                    String preco = String.format("%.2f", custo);
                                    rqPlayer.sendMessage(Component.text(lang.getString("tpa.pagou","Você pagou <valor> por usar o tpa!").replace("<>valor",preco))
                                            .color(NamedTextColor.GREEN));
                                }else{
                                    rqPlayer.sendMessage(Component.text("❌ "+lang.getString("tpa.falhaSaldo","Você não tem saldo suficiente para usar tpa!"))
                                            .color(NamedTextColor.RED));
                                }
                            }
                        }
                    }
                    else{
                        sender.sendMessage(Component.text("❌ "+lang.getString("tpa.falhaPlayer","Apenas jogadores podem se teleportar!"))
                                .color(NamedTextColor.RED));
                    }
                    return Command.SINGLE_SUCCESS;
                }));
        // Comando /tpa cancelar
        tpaNode.then(Commands.literal("cancelar").executes(ctx->{
                    final CommandSender sender = ctx.getSource().getSender();
                    if(sender instanceof Player player){
                        TpaRequest request = tpaRequests.remove(player.getUniqueId());
                        if(request!=null){
                            Player rqPlayer = request.getRequest();
                            if(rqPlayer.isOnline()){
                                rqPlayer.sendMessage(Component.text("❌ "+lang.getString("tpa.cancelado","Seu pedido foi cancelado!"))
                                        .color(NamedTextColor.RED));
                            }
                        }
                    }
                    else{
                        sender.sendMessage(Component.text("❌ "+lang.getString("tpa.falhaPlayer","Apenas jogadores podem se teleportar!"))
                                .color(NamedTextColor.RED));
                    }
                    return Command.SINGLE_SUCCESS;
                }));
        // Comando /nexusAdmin para operadores
        LiteralArgumentBuilder<CommandSourceStack> nexusAdminRoot = Commands.literal("nexusAdmin").requires(sender -> sender.getSender().isOp() || sender.getSender().hasPermission("reliquiasnexus.opzim")).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            List<String> nexus = lang.getStringList("nexusAdmin");
            nexus.forEach(texto ->{
                String revisado;
                if(!texto.equals(nexus.getFirst()))revisado = "§7➤ §b"+texto.replace("<div>","§f-")+"\n";
                else revisado = "\n§6§l⭐ §e§l"+texto+" §6§l⭐\n";
                sender.sendMessage(Component.text(revisado));
            });
            sender.sendMessage(Component.text("§6§l⭐ §e§l"+nexus.getFirst()+" §6§l⭐\n").decorate(TextDecoration.BOLD));
            return Command.SINGLE_SUCCESS;
        });
        // Comando /nexusAdmin rank e derivados
        nexusAdminRoot.then(Commands.literal("rank").executes( ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            List<String> ranks = config.getStringList("ranks");
            sender.sendMessage(Component.text("-="+lang.getString("rank.titulo","Lista de ranks/cargos do servidor")+"=-"));
            ranks.forEach(rank -> {
                String cor = config.getString("cargo."+rank, "#ffffff");
                sender.sendMessage(Component.text("➤ "+rank).color(TextColor.fromHexString(cor)));
            });
            return Command.SINGLE_SUCCESS;
        })
                .then(Commands.literal("new").then(Commands.argument("nome",StringArgumentType.string()).then(Commands.argument("cor",ArgumentTypes.hexColor()).executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    String rank = StringArgumentType.getString(ctx,"nome");
                    List<String> ranks = config.getStringList("ranks");
                    if(!ranks.contains(rank)){
                        ranks.add(rank);
                        config.set("ranks",ranks);
                        sender.sendMessage(Component.text(lang.getString("rank.novo","Novo rank criado com sucesso!")).color(NamedTextColor.GREEN));
                    }else{
                        sender.sendMessage(Component.text(lang.getString("rank.cor","Cor atualizada com sucesso!")).color(NamedTextColor.GREEN));
                    }
                    TextColor cor = ctx.getArgument("cor", TextColor.class);
                    config.set("cargos."+rank,cor.asHexString().toLowerCase());
                    saveConfig();
                    return Command.SINGLE_SUCCESS;
        })))));
        nexusAdminRoot.then(Commands.literal("rank").then(Commands.literal("set").then(Commands.argument("player",ArgumentTypes.player()).then(Commands.argument("rank",StringArgumentType.string()).suggests( (ctx, builder) -> {
            List<String> ranks = config.getStringList("ranks");
            if(ranks.isEmpty())return builder.buildFuture();
            for (String rank : ranks) {
                builder.suggest(rank);
            }
            return builder.buildFuture();
        })
                .executes(ctx -> {
                    final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                    final Player player = targetResolver.resolve(ctx.getSource()).getFirst();
                    final String rank = ctx.getArgument("rank", String.class);
                    final CommandSender sender = ctx.getSource().getSender();
                    List<String> ranks = config.getStringList("ranks");
                    if(ranks.contains(rank)){
                        config.set("players."+player.getUniqueId(),rank);
                        player.sendMessage(Component.text("✅ "+lang.getString("rank.mudaPlayer","Seu rank foi alterado para ")+rank).color(NamedTextColor.GREEN));
                        sender.sendMessage(Component.text("✅ "+lang.getString("rank.muda","Rank alterado com sucesso")).color(NamedTextColor.GREEN));
                        playerListManager.setPlayerRank(player.getUniqueId(),rank);
                    }else{
                        sender.sendMessage(Component.text("❌ "+lang.getString("rank.falhaInvalido","Rank invalido!")).color(NamedTextColor.RED));
                    }
                    return Command.SINGLE_SUCCESS;
                })))));
        // Comando /nexusAdmin setlevel
        nexusAdminRoot.then(Commands.literal("setlevel").then(Commands.argument("level", IntegerArgumentType.integer(1,config.getInt("levelMax",20))).executes(ctx -> {
            if(ctx.getSource().getExecutor() instanceof Player player){
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(!stack.hasItemMeta()){
                    player.sendMessage(Component.text("❌ "+lang.getString("setlevel.falhaMao","Segure uma relíquia Nexus na mão!"))
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }
                int level = ctx.getArgument("level", int.class);
                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    if(nome!=null){
                        NamespacedKey key = getKey(nome);
                        if(key!=null){
                            player.getPersistentDataContainer().set(key,PersistentDataType.INTEGER,level);
                            player.sendMessage(Component.text("✅ "+lang.getString("setlevel.sucesso","Level do Nexus definido para:")+ " " + level)
                                    .color(NamedTextColor.GREEN));
                        }
                    }
                } else {
                    player.sendMessage(Component.text("❌ "+lang.getString("setlevel.falhaMao","Segure uma relíquia Nexus na mão!"))
                            .color(NamedTextColor.RED));
                }
            }else{
                ctx.getSource().getSender().sendMessage(Component.text("❌ "+lang.getString("setlevel.falhaPlayer","Apenas jogadores podem usar este comando!"))
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        })));
        // Comando /nexusAdmin expurgar
        nexusAdminRoot.then(Commands.literal(lang.getString("expurgar.comando","expurgar")).then(Commands.argument("exp", BoolArgumentType.bool()).executes(ctx -> {
            boolean exp = ctx.getArgument("exp", boolean.class);
            config.set("expurgo",exp);
            saveConfig();
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(exp){
                    player.sendMessage(Component.text("⚡ "+lang.getString("expurgar.ativado","MODO EXPURGO ATIVADO!<break>⚠ Todas as relíquias podem ser roubadas!").replace("<break>","\n"))
                            .color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
                }else{
                    player.sendMessage(Component.text("🛡️ "+lang.getString("expurgar.desativado","MODO EXPURGO DESATIVADO<break>✔ Suas relíquias estão seguras!").replace("<break>","\n"))
                            .color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                }
            });
            ctx.getSource().getSender().sendMessage(Component.text("✅ "+lang.getString("expurgar.comando","expurgar")+": " + exp)
                    .color(NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        })));
        // Comando /nexusAdmin reliquia e derivados
        nexusAdminRoot.then(Commands.literal(lang.getString("reliquia.comando","reliquia"))
                .then(Commands.literal("send").then(Commands.argument("player",ArgumentTypes.player()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
            final Player player = targetResolver.resolve(ctx.getSource()).getFirst();
            PersistentDataContainer container = player.getPersistentDataContainer();
            int qtd = container.getOrDefault(QTD.key,PersistentDataType.INTEGER,0);
            CommandSender sender = ctx.getSource().getSender();
            if(qtd<config.getInt("limite",4)){
                List<Nexus> reliquias = ItemsRegistro.getValidReliquia(config);
                Random rng = new Random();
                int escolhido = rng.nextInt(reliquias.size());
                Nexus n = reliquias.get(escolhido);
                String nome = n.getNome();
                ReliquiasNexus.setConfigSave("nexus."+nome,player.getUniqueId().toString());
                this.saveConfig();
                container.set(QTD.key,PersistentDataType.INTEGER,qtd+1);
                int level = 1;
                NamespacedKey key = NexusKeys.getKey(nome);
                if(key!=null && container.has(key,PersistentDataType.INTEGER)){
                    level=container.getOrDefault(key,PersistentDataType.INTEGER,1);
                }else if(key!=null){
                    container.set(key,PersistentDataType.INTEGER,1);
                }
                ItemStack stack = n.getItem(level);
                ItemMeta meta = stack.getItemMeta();
                meta.getPersistentDataContainer().set(DONO.key,PersistentDataType.STRING,player.getUniqueId().toString());
                stack.setItemMeta(meta);
                player.getInventory().addItem(stack);
                String r=ReliquiasNexus.getLang().getString("reliquia.send","Você recebeu o nexus do <relic>");
                player.sendMessage(Component.text(r).color(NamedTextColor.GREEN));
                sender.sendMessage(Component.text(lang.getString("reliquia.sendSuccess","O jogador recebeu o nexus!")).color(NamedTextColor.GREEN));
            }else{
                sender.sendMessage(Component.text(lang.getString("reliquia.limite","O jogador já está no limite de nexus!")).color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        })
                .then(Commands.argument("nexus",StringArgumentType.string()).suggests( (ctx, builder) -> {
                   for (String nome : names) {
                       String r = config.getString("nexus."+nome,"");
                       if(!r.isBlank()){
                           try{
                               UUID uuid = UUID.fromString(r);
                               OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(uuid);
                               String nomeJogador = offlinePlayer.getName() != null ? offlinePlayer.getName() : "";
                               if(nomeJogador.isBlank()){
                                   builder.suggest(nome);
                               }
                           }catch(IllegalArgumentException ignored){
                               builder.suggest(nome);
                           }
                       }else{
                           builder.suggest(nome);
                       }
                    }
                    return builder.buildFuture();
                })
                        .executes(ctx -> {
                            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                            final Player player = targetResolver.resolve(ctx.getSource()).getFirst();
                            final String nome = ctx.getArgument("nexus", String.class);
                            final CommandSender sender = ctx.getSource().getSender();
                            String r = config.getString("nexus."+nome,"");
                            if(!r.isBlank()){
                                try{
                                    UUID uuid = UUID.fromString(r);
                                    OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(uuid);
                                    String nomeJogador = offlinePlayer.getName() != null ? offlinePlayer.getName() : "";
                                    if(!nomeJogador.isBlank()){
                                        sender.sendMessage(Component.text("❌ "+lang.getString("reliquia.falhaDono","Esse Nexus já possui dono"))
                                                .color(NamedTextColor.RED));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                }catch(IllegalArgumentException ignored){}
                            }
                            Nexus nexus = ItemsRegistro.getFromNome(nome.toLowerCase());
                            PersistentDataContainer container = player.getPersistentDataContainer();
                            int qtd = container.getOrDefault(QTD.key,PersistentDataType.INTEGER,0);
                            if(nexus!=null && qtd<config.getInt("limite",4)){
                                ReliquiasNexus.setConfigSave("nexus."+nome,player.getUniqueId().toString());
                                this.saveConfig();
                                container.set(QTD.key,PersistentDataType.INTEGER,qtd+1);
                                int level = 1;
                                NamespacedKey key = NexusKeys.getKey(nome);
                                if(key!=null && container.has(key,PersistentDataType.INTEGER)){
                                    level=container.getOrDefault(key,PersistentDataType.INTEGER,1);
                                }else if(key!=null){
                                    container.set(key,PersistentDataType.INTEGER,1);
                                }
                                ItemStack stack = nexus.getItem(level);
                                ItemMeta meta = stack.getItemMeta();
                                meta.getPersistentDataContainer().set(DONO.key,PersistentDataType.STRING,player.getUniqueId().toString());
                                stack.setItemMeta(meta);
                                player.getInventory().addItem(stack);
                                String msg=ReliquiasNexus.getLang().getString("reliquia.send","Você recebeu o nexus do <relic>");
                                player.sendMessage(Component.text(msg).color(NamedTextColor.GREEN));
                                sender.sendMessage(Component.text(lang.getString("reliquia.sendSuccess","O jogador recebeu o nexus!")).color(NamedTextColor.GREEN));
                            }else{
                                sender.sendMessage(Component.text(lang.getString("reliquia.limite","O jogador já está no limite de nexus!")).color(NamedTextColor.RED));
                            }
                            return Command.SINGLE_SUCCESS;
                        })))));
        nexusAdminRoot.then(Commands.literal(lang.getString("reliquia.comando","reliquia")).then(Commands.literal("remove").then(Commands.argument("player",ArgumentTypes.player()).then(Commands.argument("nexus",StringArgumentType.string()).suggests((ctx,builder)->{
                    for (String reliquia : names) {
                        builder.suggest(reliquia);
                    }
                    return builder.buildFuture();
                })
                .executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                    final Player player = targetResolver.resolve(ctx.getSource()).getFirst();
                    final String reliquiaNome = ctx.getArgument("nexus", String.class).toLowerCase();
                    String donoStr = config.getString("nexus."+reliquiaNome,"");
                    if(donoStr.equals(player.getUniqueId().toString())){
                        for (ItemStack itemStack : player.getInventory()) {
                            if(itemStack!=null){
                                ItemMeta meta = itemStack.getItemMeta();
                                if(meta!=null){
                                    String itemNome = meta.getPersistentDataContainer().getOrDefault(NEXUS.key,PersistentDataType.STRING,"");
                                    if(itemNome.equals(reliquiaNome)){
                                        player.getInventory().remove(itemStack);
                                        break;
                                    }
                                }
                            }
                        }
                        config.set("nexus." + reliquiaNome, "");
                        saveConfig();
                        sender.sendMessage(Component.text(lang.getString("reliquia.remove","Nexus removido com sucesso!")).color(NamedTextColor.GREEN));
                    }else{
                        sender.sendMessage(Component.text(lang.getString("reliquia.falhaNaoDono","O jogador não é o dono do Nexus!")).color(NamedTextColor.GREEN));
                    }
                    return Command.SINGLE_SUCCESS;
                })))));
        // Comando /nexusAdmin limite
        nexusAdminRoot.then(Commands.literal("limite").then(Commands.argument("valor", IntegerArgumentType.integer()).executes(ctx -> {
            int valor = ctx.getArgument("valor", int.class);
            config.set("limite", valor);
            saveConfig();
            ctx.getSource().getSender().sendMessage(Component.text("✅ "+lang.getString("limite","Limite definido para:")+" "+ valor)
                    .color(NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        })));
        // Comando /nexusAdmin recursos
        nexusAdminRoot.then(Commands.literal(lang.getString("recursos.comando","recursos")).then(Commands.argument("recurso",StringArgumentType.string()).suggests((ctx,builder) -> {
            List<String> recursos = lang.getStringList("recursos.list");
            for (String nome : recursos) {
                builder.suggest(nome);
            }
            return builder.buildFuture();
        })
                .then(Commands.argument("ativado",BoolArgumentType.bool())).executes(ctx -> {
                    String recurso=ctx.getArgument("recurso", String.class).toLowerCase();;
                    boolean ativado = ctx.getArgument("ativado", boolean.class);
                    config.set("recursos."+recurso,ativado);
                    saveConfig();
                    ctx.getSource().getSender().sendMessage(Component.text("✅ " + recurso + ": "+ativado).color(NamedTextColor.GREEN));
                    return Command.SINGLE_SUCCESS;
                })));
        // Comando /nexusAdmin op
        nexusAdminRoot.then(Commands.literal("op").then(Commands.argument("player", ArgumentTypes.player()).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
            final Player targetPlayer = targetResolver.resolve(ctx.getSource()).getFirst();
            boolean isOP = config.getBoolean("op-players." + targetPlayer.getUniqueId(),false);
            // Adiciona a permissão persistente
            if (isOP) {
                sender.sendMessage(Component.text("❌ "+targetPlayer.getName()+" "+lang.getString("op.isOP","já é um OP")).color(NamedTextColor.RED));
            } else {
                config.set("op-players." + targetPlayer.getUniqueId(), true);
                saveConfig();
                // Aplica a permissão imediatamente se o jogador estiver online
                if (targetPlayer.isOnline()) {
                    targetPlayer.addAttachment(this).setPermission("reliquiasnexus.opzim", true);
                }
                sender.sendMessage(Component.text("✅ " + targetPlayer.getName() + " "+lang.getString("op.setPlayerOP","agora tem permissões de OP")).color(NamedTextColor.GREEN));
                targetPlayer.sendMessage(Component.text("⚡ "+lang.getString("op.setOP","agora você tem acesso ao /nexusAdmin")).color(NamedTextColor.GOLD));
            }
            return Command.SINGLE_SUCCESS;
        })));
        // Comando /nexusAdmin deop
        nexusAdminRoot.then(Commands.literal("deop").then(Commands.argument("player", ArgumentTypes.player()).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
            final Player targetPlayer = targetResolver.resolve(ctx.getSource()).getFirst();
            boolean isOP = config.getBoolean("op-players." + targetPlayer.getUniqueId(),false);
            if (!isOP) {
                sender.sendMessage(Component.text("❌ "+targetPlayer.getName()+" "+lang.getString("op.isNOP","não é um OP")).color(NamedTextColor.RED));
            } else {
                config.set("op-player."+ targetPlayer.getUniqueId(),false);
                saveConfig();
                if (targetPlayer.isOnline()) {
                    targetPlayer.addAttachment(this).setPermission("reliquiasnexus.opzim", false);
                }
                sender.sendMessage(Component.text("✅ " + targetPlayer.getName() + " "+lang.getString("op.setPlayerNOP","agora não tem mais permissões de OP")).color(NamedTextColor.GREEN));
                targetPlayer.sendMessage(Component.text("⚡ "+lang.getString("op.setNOP","agora você não tem mais acesso ao /nexusAdmin")).color(NamedTextColor.GOLD));
            }
            return Command.SINGLE_SUCCESS;
        })));
        // Comando /nexusAdmin tpacost
        nexusAdminRoot.then(Commands.literal("tpacost").then(Commands.argument("cost",DoubleArgumentType.doubleArg(0.0)).executes(ctx -> {
            double cost = ctx.getArgument("cost", double.class);
            config.set("recursos.tpacost",cost);
            saveConfig();
            String preco = String.format("%.2f", cost);
            ctx.getSource().getSender().sendMessage(Component.text(lang.getString("tpacost","Custo do tpa definido para")+" $"+preco+" "+config.getString("recursos.moneyName","moly")).color(NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        })));
        // Comando /nexusAdmin saldo e derivados
        nexusAdminRoot.then(Commands.literal(lang.getString("saldoadm.comando","saldo")).then(Commands.argument("player", ArgumentTypes.player()).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
            final Player targetPlayer = targetResolver.resolve(ctx.getSource()).getFirst();
            double saldo = targetPlayer.getPersistentDataContainer().getOrDefault(SALDO.key,PersistentDataType.DOUBLE,0.0);
            String saldoStr = String.format("%.2f", saldo);
            sender.sendMessage(Component.text(lang.getString("saldoadm.player","O jogador tem")+" $"+saldoStr+" "+config.getString("recursos,moneyName","moly")).color(NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        })
                .then(Commands.literal("add").then(Commands.argument("valor",DoubleArgumentType.doubleArg()).executes(ctx -> {
                    final CommandSender sender = ctx.getSource().getSender();
                    final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                    final Player targetPlayer = targetResolver.resolve(ctx.getSource()).getFirst();
                    double saldo = targetPlayer.getPersistentDataContainer().getOrDefault(SALDO.key,PersistentDataType.DOUBLE,0.0);
                    double valor = ctx.getArgument("valor", double.class);
                    targetPlayer.getPersistentDataContainer().set(SALDO.key,PersistentDataType.DOUBLE,saldo+valor);
                    String preco = String.format("%.2f", valor);
                    String msg = valor>0?lang.getString("saldoadm.lucro","Foi adicionado <valor> ao saldo do jogador"):lang.getString("saldoadm.desconto","Foi descontado <valor> do saldo do jogador");
                    msg = msg.replace("<valor>",preco);
                    sender.sendMessage(Component.text(msg).color(NamedTextColor.GREEN));
                    String msgPlayer = valor>0?lang.getString("saldoadm.lucroPlayer","Você ganhou"):lang.getString("saldoadm.descontoPlayer","Você perdeu");
                    targetPlayer.sendMessage(Component.text(msgPlayer+" "+preco).color(valor>0?NamedTextColor.GREEN:NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                })))));
        // Comando /nexusAdmin moneyNome
        nexusAdminRoot.then(Commands.literal("moneyNome").then(Commands.argument("name",StringArgumentType.string()).executes(ctx -> {
            String nome = ctx.getArgument("name", String.class);
            config.set("recursos.moneyName",nome);
            saveConfig();
            ctx.getSource().getSender().sendMessage(Component.text(lang.getString("moneyName","Nome alterado para")+": "+config.getString("recursos.moneyName","moly")).color(NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        })));
        // Novo comando /nexu boss
        nexusAdminRoot.then(Commands.literal("boss").then(Commands.argument("rarity", StringArgumentType.string()).suggests((ctx, builder) -> {
            for (BossRarity rarity : BossRarity.values()) {
                builder.suggest(rarity.name().toLowerCase());
            }
            return builder.buildFuture();
        }).executes(ctx -> {
            String rarityName = ctx.getArgument("rarity", String.class);
            BossRarity rarity = BossRarity.fromString(rarityName);
            if (rarity != null) {
                bossManager.spawnBoss(rarity, false); // Alterado aqui
                ctx.getSource().getSender().sendMessage(Component.text("✅ Boss de raridade " + rarity.displayName + " invocado!").color(NamedTextColor.GREEN));
            } else {
                ctx.getSource().getSender().sendMessage(Component.text("❌ Raridade inválida!").color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        })));

        // Novo comando /nexu procurado [player] {valor}
        nexusAdminRoot.then(Commands.literal("procurado")
                .then(Commands.argument("player", ArgumentTypes.player())
                        .then(Commands.argument("valor", DoubleArgumentType.doubleArg(0.1))
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();
                                    if (sender instanceof Player player) {
                                        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                        Player alvo = targetResolver.resolve(ctx.getSource()).getFirst();
                                        double valor = ctx.getArgument("valor", Double.class);

                                        if (alvo.equals(player)) {
                                            player.sendMessage(Component.text("§cVocê não pode se marcar como procurado!").color(NamedTextColor.RED));
                                            return Command.SINGLE_SUCCESS;
                                        }

                                        ConfigurationSection wantedSection = config.getConfigurationSection("procurados");
                                        if (wantedSection == null) {
                                            wantedSection = config.createSection("procurados");
                                        }

                                        wantedSection.set(alvo.getUniqueId().toString() + ".recompensa", valor);
                                        wantedSection.set(alvo.getUniqueId().toString() + ".expiracao", System.currentTimeMillis() + (3L * 24 * 60 * 60 * 1000));
                                        saveConfig();

                                        Bukkit.broadcast(Component.text("§4O jogador §c" + alvo.getName() + " §4foi adicionado à lista de procurados! Recompensa: §6" + valor + " moly.").color(NamedTextColor.RED));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );

        // Registrar comandos
        LiteralCommandNode<CommandSourceStack> nexusCommand = nexusRoot.build();
        LiteralCommandNode<CommandSourceStack> nexusAdminCommand = nexusAdminRoot.build();
        LiteralCommandNode<CommandSourceStack> tpaCommand = tpaNode.build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(nexusCommand);
            commands.registrar().register(nexusAdminCommand);
            commands.registrar().register(ecCommandNode);
            commands.registrar().register(tpaCommand);
        });

        // Registrar eventos
        getServer().getPluginManager().registerEvents(new JoinQuitEvent(this), this);
        getServer().getPluginManager().registerEvents(new LimitadorEvent(this), this);
        getServer().getPluginManager().registerEvents(new PassivaEvent(), this);
        getServer().getPluginManager().registerEvents(new PerdeuEvent(this), this);
        getServer().getPluginManager().registerEvents(new EvoluirEvent(this), this);
        getServer().getPluginManager().registerEvents(new SpecialEvent(this), this);
        getServer().getPluginManager().registerEvents(lojaManager, this);
        getServer().getPluginManager().registerEvents(new BancoEvent(this), this);
        getServer().getPluginManager().registerEvents(playerListManager, this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(missoesManager, this);

        getServer().getConsoleSender().sendMessage("§2✅ §a[Nexus]: Plugin Ativado com Sucesso!");
        lojaManager.load(lojaSV);
        lojaManager.gerarItensAtuais();
    }

    private boolean processarTroca(Player player1, Player player2, String relic1, String relic2) {
        boolean achou1=false;
        ItemStack p1Item = null;
        for (ItemStack item : player1.getInventory().getContents()) {
            if (item != null && item.hasItemMeta()) {
                PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
                if (relic1.equals(data.get(NEXUS.key, PersistentDataType.STRING))) {
                    p1Item=item;
                    player1.getInventory().remove(item);
                    achou1 = true;
                    break;
                }
            }
        }
        boolean achou2=false;
        ItemStack p2Item = null;
        for (ItemStack item : player2.getInventory().getContents()) {
            if (item != null && item.hasItemMeta()) {
                PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
                if (relic2.equals(data.get(NEXUS.key, PersistentDataType.STRING))) {
                    p2Item=item;
                    player2.getInventory().remove(item);
                    achou2 = true;
                    break;
                }
            }
        }
        if(achou1 && achou2){
            config.set("nexus." + relic1, player2.getUniqueId().toString());
            config.set("nexus." + relic2, player1.getUniqueId().toString());
            player1.getInventory().addItem(p2Item);
            player2.getInventory().addItem(p1Item);
            saveConfig();
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        missoesManager.save(missaoAtivaBK);
        lojaManager.save(lojaSV);
        saveConfig();
        getServer().getConsoleSender().sendMessage("§4❌ §c[Nexus]: Plugin Desativado!");
    }
    public static FileConfiguration getNexusConfig(){
        return config;
    }
    public static FileConfiguration getLang(){
        return lang;
    }
    public static FileConfiguration getLoja(){ return lojaSV;}
    public static void setConfigSave(String path,Object value){
        config.set(path,value);
    }
    public void reiniciarMissao(Player player){
        missoesManager.reiniciarMissao(player);
    }
    public void pausarMissao(Player player){
        missoesManager.pausarMissao(player);
    }
}