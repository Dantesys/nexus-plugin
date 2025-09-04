package org.dantesys.reliquiasNexus;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
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
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;
import org.dantesys.reliquiasNexus.economia.Banco;
import org.dantesys.reliquiasNexus.eventos.*;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.SpeciaisPassivas.Morte;
import org.dantesys.reliquiasNexus.tab.PlayerListManager;
import org.dantesys.reliquiasNexus.team.Team;
import org.dantesys.reliquiasNexus.util.Economia;
import org.dantesys.reliquiasNexus.util.NexusKeys;
import org.dantesys.reliquiasNexus.util.Troca;
import org.dantesys.reliquiasNexus.util.UpdaterCheck;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.bukkit.Material;


import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public final class ReliquiasNexus extends JavaPlugin {
    private static final Map<UUID, Troca> trocas = new ConcurrentHashMap<>();
    private static FileConfiguration config;
    private static YamlConfiguration lang;
    private static final Map<UUID, PermissionAttachment> opAttachments = new HashMap<>();
    private static Banco nexusCentralBank;
    private PlayerListManager playerListManager;
    public final List<String> names = List.of("guerreiro","ceifador","vida","mares","barbaro",
            "fazendeiro","espiao","arqueiro","cacador","tempestade","mineiro","fenix","protetor",
            "hulk","sculk","pescador","flash","mago","ladrao","domador","cozinheiro","construtor",
            "abissal","cronosombra","assassino","frostis","necromante","alquimista","golem","dragao", "morte", "carrasco");

    public static void saiu(Player player) {
    }

    public static Banco getNexusCentralBank() {
        return nexusCentralBank;
    }

    @Override
    public void onEnable() {
        NexusKeys.init(this);
        ItemsRegistro.init();
        saveResource("lang/pt-br.yml",true);
        saveResource("lang/en-us.yml",false);
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

        // Inicializa o Nexus Central Bank
        double saldoInicial = config.getDouble("nexus_central_bank.saldo", 25000.0);
        nexusCentralBank = new Banco("Nexus Central Bank", null, saldoInicial);
        // Salva o saldo inicial no config
        config.set("nexus_central_bank.saldo", nexusCentralBank.getSaldo());

        // Inicializar o gerenciador da player list
        playerListManager = new PlayerListManager(this);

        new UpdaterCheck(this, "dantesys/nexus-plugin").checkForUpdates();

        // Comando /nexus principal
        LiteralArgumentBuilder<CommandSourceStack> nexusRoot = Commands.literal("nexus").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();

            Component mensagem = Component.text()
                    .append(Component.text("\n§6§l⭐ §e§lNEXUS COMMANDS §6§l⭐\n").decorate(TextDecoration.BOLD))
                    .append(Component.text("§7➤ §b/nexus livro §f- Receber livro das relíquias\n"))
                    .append(Component.text("§7➤ §b/nexus historia §f- Conheça a história do servidor\n"))
                    .append(Component.text("§7➤ §b/nexus evoluir §f- Evoluir relíquia na mão\n"))
                    .append(Component.text("§7➤ §b/nexus expurgo §f- Ver status do expurgo\n"))
                    .append(Component.text("§7➤ §b/nexus missao §f- Gerar uma missão aleatória\n"))
                    .append(Component.text("§7➤ §b/nexus Loja §f- Abrir menu da loja\n"))
                    .append(Component.text("§7➤ §b/nexus trocar <jogador> §f- Oferecer troca\n"))
                    .append(Component.text("§7➤ §b/nexus banco §f- Abrir menu do banco\n"))
                    .append(Component.text("§7➤ §b/nexus team §f- Comandos de equipe\n"))
                    .append(Component.text("§7➤ §b/nexus list §f- Listar relíquias do servidor\n"))
                    .append(Component.text("§7➤ §b/nexus level §f- Seus níveis de relíquias\n"))
                    .append(Component.text("§7➤ §b/nexus procurados §f- Ver lista de procurados\n"))
                    .append(Component.text("§6§l⭐ §e§lNEXUS COMMANDS §6§l⭐\n").decorate(TextDecoration.BOLD))
                    .build();

            sender.sendMessage(mensagem);
            return Command.SINGLE_SUCCESS;
        });

        // Novo comando /nexus historia
        nexusRoot.then(Commands.literal("historia").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (ctx.getSource().getExecutor() instanceof Player player) {
                player.getInventory().addItem(ItemsRegistro.nexusStoryBook.getItem(1));
                sender.sendMessage(Component.text("📖 §aVocê recebeu o livro Nexus Story!")
                        .color(NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("❌ §cApenas jogadores podem usar este comando!")
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));

        // Comando livro
        nexusRoot.then(Commands.literal("livro").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                player.getInventory().addItem(ItemsRegistro.livro.getItem(1));
                sender.sendMessage(Component.text("📖 §aVocê recebeu o Livro das Relíquias!")
                        .color(NamedTextColor.GREEN));
            }else{
                sender.sendMessage(Component.text("❌ §cApenas jogadores podem usar este comando!")
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));

        // Comando evoluir
        nexusRoot.then(Commands.literal("evoluir").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(stack == null || !stack.hasItemMeta()){
                    sender.sendMessage(Component.text("❌ §cVocê precisa segurar uma relíquia Nexus na mão!")
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
                        int level = dataPlayer.getOrDefault(key, PersistentDataType.INTEGER, 1);
                        evo.tentarEvoluir(player,stack,level,evo.getSlotOfItem(player,stack));
                    }else{
                        sender.sendMessage(Component.text("❌ §cRelíquia inválida!")
                                .color(NamedTextColor.RED));
                    }
                }else{
                    sender.sendMessage(Component.text("❌ §cVocê precisa segurar uma relíquia Nexus na mão!")
                            .color(NamedTextColor.RED));
                }
            }else{
                sender.sendMessage(Component.text("❌ §cApenas jogadores podem usar este comando!")
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));

        // Comando expurgo
        nexusRoot.then(Commands.literal("expurgo").executes(ctx -> {
            boolean expurgo = config.getBoolean("expurgo");
            if(expurgo){
                ctx.getSource().getSender().sendMessage(Component.text("⚡ §c§lMODO EXPURGO ATIVADO!\n§4⚠ §cTodas as relíquias dropam ao morrer!")
                        .color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
            }else{
                Bukkit.getOnlinePlayers().forEach(player -> {
                    playerListManager.updateAllPlayerLists();
                });
                ctx.getSource().getSender().sendMessage(Component.text("🛡️ §a§lMODO EXPURGO DESATIVADO\n§2✔ §aSuas relíquias estão seguras!")
                        .color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            }
            return Command.SINGLE_SUCCESS;
        }));

        // Comando trocar
        nexusRoot.then(Commands.literal("trocar").then(Commands.argument("jogador", ArgumentTypes.player()).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo")) {
                sender.sendMessage(Component.text("❌ O comando trocar está desativado durante o expurgo.").color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if(ctx.getSource().getExecutor() instanceof Player player){
                final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogador", PlayerSelectorArgumentResolver.class);
                final Player p = targetResolver.resolve(ctx.getSource()).getFirst();
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(stack == null || !stack.hasItemMeta()){
                    sender.sendMessage(Component.text("❌ §cVocê precisa segurar uma relíquia Nexus na mão!")
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
                        Component msgEnvio = Component.text()
                                .append(Component.text("✅ §aOferta de troca enviada para §6" + p.getName() + "§a!\n")
                                        .color(NamedTextColor.GREEN))
                                .append(Component.text("§7Relíquia: §b" + nome + "\n")
                                        .color(NamedTextColor.GRAY))
                                .append(Component.text("§7Aguarde a resposta...")
                                        .color(NamedTextColor.GRAY))
                                .build();
                        sender.sendMessage(msgEnvio);

                        // Mensagem clicável para o jogador que recebeu
                        Component msgRecebido = Component.text()
                                .append(Component.text("\n🎁 §6§lOFERTA DE TROCA RECEBIDA!\n")
                                        .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                                .append(Component.text("§7De: §b" + player.getName() + "\n")
                                        .color(NamedTextColor.GRAY))
                                .append(Component.text("§7Relíquia: §a" + nome + "\n\n")
                                        .color(NamedTextColor.GRAY))
                                .append(Component.text("✅ ACEITAR ")
                                        .color(NamedTextColor.GREEN)
                                        .clickEvent(ClickEvent.runCommand("/nexus aceitar " + player.getUniqueId().toString()))
                                        .hoverEvent(HoverEvent.showText(Component.text("§aClique para aceitar a troca"))))
                                .append(Component.text("❌ RECUSAR")
                                        .color(NamedTextColor.RED)
                                        .clickEvent(ClickEvent.runCommand("/nexus cancelar " + player.getUniqueId().toString()))
                                        .hoverEvent(HoverEvent.showText(Component.text("§cClique para recusar a troca"))))
                                .build();

                        p.sendMessage(msgRecebido);
                    }
                }else{
                    sender.sendMessage(Component.text("❌ §cVocê precisa segurar uma relíquia Nexus na mão!")
                            .color(NamedTextColor.RED));
                }
            }else{
                sender.sendMessage(Component.text("❌ §cApenas jogadores podem usar este comando!")
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        })));

        // Comando aceitar (oculto)
        // Note: A visibilidade deste comando é intencionalmente restrita para não aparecer nas sugestões.
        nexusRoot.then(Commands.literal("aceitar").then(Commands.argument("offerer", StringArgumentType.string()).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo")) {
                sender.sendMessage(Component.text("❌ O comando trocar está desativado durante o expurgo.").color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if (ctx.getSource().getExecutor() instanceof Player player) {
                String offererId = ctx.getArgument("offerer", String.class);
                UUID offererUuid = UUID.fromString(offererId);
                Player offerer = Bukkit.getPlayer(offererUuid);
                Troca t = trocas.get(player.getUniqueId());

                if (t == null || !t.offererUuid().equals(offererUuid)) {
                    sender.sendMessage(Component.text("❌ §cEsta oferta não é mais válida!")
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }

                if (offerer == null || !offerer.isOnline()) {
                    trocas.remove(player.getUniqueId());
                    sender.sendMessage(Component.text("❌ §cO jogador que ofereceu a troca saiu do servidor!")
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }

                ItemStack stack = player.getInventory().getItemInMainHand();
                if(stack == null || !stack.hasItemMeta() || !stack.getItemMeta().getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                    sender.sendMessage(Component.text("❌ §cVocê precisa segurar uma relíquia Nexus na mão para aceitar a troca!")
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }
                String relicToGive = stack.getItemMeta().getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                t.setPlayer2Relic(relicToGive);

                sender.sendMessage(Component.text("✅ §aVocê aceitou a troca! A contagem regressiva irá começar.")
                        .color(NamedTextColor.GREEN));
                offerer.sendMessage(Component.text("✅ §a" + player.getName() + " aceitou sua oferta! A troca será concluída em instantes.")
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

                            Component countdownMessage = Component.text("⚡ Troca em andamento: " + count + "...", color);
                            player.sendMessage(countdownMessage);
                            offerer.sendMessage(countdownMessage);
                            count--;
                        } else {
                            if (processarTroca(offerer, player, t.offeredRelicName(), t.player2Relic())) {
                                player.sendMessage(Component.text("✅ Troca finalizada com sucesso!").color(NamedTextColor.GREEN));
                                offerer.sendMessage(Component.text("✅ Troca finalizada com sucesso!").color(NamedTextColor.GREEN));
                                trocas.remove(player.getUniqueId());
                            } else {
                                player.sendMessage(Component.text("❌ Falha na troca. Relíquias não encontradas.").color(NamedTextColor.RED));
                                offerer.sendMessage(Component.text("❌ Falha na troca. Relíquias não encontradas.").color(NamedTextColor.RED));
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
            if (config.getBoolean("expurgo")) {
                sender.sendMessage(Component.text("❌ O comando trocar está desativado durante o expurgo.").color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if (ctx.getSource().getExecutor() instanceof Player player) {
                String offererId = ctx.getArgument("offerer", String.class);
                UUID offererUuid = UUID.fromString(offererId);
                Player offerer = Bukkit.getPlayer(offererUuid);
                Troca t = trocas.remove(player.getUniqueId());

                if (t != null && t.offererUuid().equals(offererUuid)) {
                    if (offerer != null && offerer.isOnline()) {
                        offerer.sendMessage(Component.text("❌ §c" + player.getName() + " recusou sua oferta de troca!")
                                .color(NamedTextColor.RED));
                    }
                    sender.sendMessage(Component.text("❌ §cVocê recusou a oferta de troca.")
                            .color(NamedTextColor.RED));
                } else {
                    sender.sendMessage(Component.text("❌ §cEsta oferta não é mais válida.")
                            .color(NamedTextColor.RED));
                }
            }
            return Command.SINGLE_SUCCESS;
        })));

        // Comando list
        nexusRoot.then(Commands.literal("list").executes(ctx -> {
            ConfigurationSection secao = config.getConfigurationSection("nexus");
            Component header = Component.text()
                    .append(Component.text("\n📜 §6§lRELÍQUIAS DO SERVIDOR §6§l📜\n")
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
                            String nomeJogador = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Desconhecido";
                            donoComponent = Component.text("§a" + nomeJogador)
                                    .color(NamedTextColor.GREEN);
                        }catch(IllegalArgumentException ignored){
                            donoComponent = Component.text("§cCorrompido")
                                    .color(NamedTextColor.RED);
                        }
                    }else{
                        donoComponent = Component.text("§cNinguém")
                                .color(NamedTextColor.RED);
                    }
                    Component linha = Component.text()
                            .append(Component.text("§7• §b" + nexus + " §7- ")
                                    .color(NamedTextColor.GRAY))
                            .append(donoComponent)
                            .build();
                    ctx.getSource().getSender().sendMessage(linha);
                }
            }else{
                ctx.getSource().getSender().sendMessage(Component.text("§cNenhuma relíquia registrada no servidor.")
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));

        // Comando level
        nexusRoot.then(Commands.literal("level").executes(ctx -> {
            if(ctx.getSource().getExecutor() instanceof Player player){
                List<NamespacedKey> keys = getKeyLevel();
                PersistentDataContainer dataPlayer = player.getPersistentDataContainer();

                Component header = Component.text()
                        .append(Component.text("\n⭐ §6§lSEUS NÍVEIS DE RELÍQUIAS §6§l⭐\n")
                                .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .build();
                player.sendMessage(header);
                boolean temReliquias = false;
                for(NamespacedKey k: keys){
                    int l = dataPlayer.getOrDefault(k, PersistentDataType.INTEGER,0);
                    if(l > 0){
                        temReliquias = true;
                        String nomeRelic = k.getKey().toUpperCase();
                        Component nivel = Component.text()
                                .append(Component.text("§7• §b" + nomeRelic + " §7- ")
                                        .color(NamedTextColor.GRAY))
                                .append(Component.text("§aNível " + l)
                                        .color(NamedTextColor.GREEN))
                                .build();
                        player.sendMessage(nivel);
                    }
                }
                if(!temReliquias){
                    player.sendMessage(Component.text("§cVocê não possui nenhuma relíquia ainda!")
                            .color(NamedTextColor.RED));
                }
            }else{
                ctx.getSource().getSender().sendMessage(Component.text("❌ §cApenas jogadores podem usar este comando!")
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));

        // Comando missao (player) - Gerar missão aleatória
        nexusRoot.then(Commands.literal("missao").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo")) {
                sender.sendMessage(Component.text("❌ O comando de missões está desativado durante o expurgo.").color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if (ctx.getSource().getExecutor() instanceof Player player) {
                new SpecialEvent(this).gerarMissaoAleatoria(player);
                return Command.SINGLE_SUCCESS;
            }
            sender.sendMessage(Component.text("❌ Apenas jogadores podem usar este comando!").color(NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }));

        // Comando /nexus missao morte
        nexusRoot.then(Commands.literal("missao").then(Commands.literal("morte").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (sender instanceof Player player) {
                Morte.startMissaoMorte(player);
            } else {
                sender.sendMessage(Component.text("❌ Apenas jogadores podem usar este comando!").color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        })));

        // Comando /nexus procurados
        nexusRoot.then(Commands.literal("procurados").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            Component header = Component.text("\n§4§l⚔️ JOGADORES PROCURADOS ⚔️\n").decorate(TextDecoration.BOLD);
            sender.sendMessage(header);

            ConfigurationSection wantedSection = config.getConfigurationSection("procurados");
            if (wantedSection == null || wantedSection.getKeys(false).isEmpty()) {
                sender.sendMessage(Component.text("§aNão há jogadores procurados no momento.").color(NamedTextColor.GREEN));
                return Command.SINGLE_SUCCESS;
            }

            for (String uuidStr : wantedSection.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(uuid);
                String nomeJogador = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Desconhecido";

                double recompensa = wantedSection.getDouble(uuidStr + ".recompensa", 0);
                long tempoRestante = (wantedSection.getLong(uuidStr + ".expiracao") - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);

                Component linha = Component.text("§e" + nomeJogador + " §7- Recompensa: §6" + recompensa + " moly §7- Tempo restante: §b" + tempoRestante + " dias").color(NamedTextColor.YELLOW);
                sender.sendMessage(linha);
            }
            return Command.SINGLE_SUCCESS;
        }));

        // Comando Loja (player) - Abrir menu da loja
        nexusRoot.then(Commands.literal("Loja").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo")) {
                sender.sendMessage(Component.text("❌ O comando de loja está desativado durante o expurgo.").color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if (ctx.getSource().getExecutor() instanceof Player player) {
                new LojaEvent(this).abrirMenuPrincipal(player);
                return Command.SINGLE_SUCCESS;
            }
            sender.sendMessage(Component.text("❌ Apenas jogadores podem usar este comando!").color(NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }));

        // Comando /nexus banco (player) - Abrir menu do banco
        nexusRoot.then(Commands.literal("banco").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo")) {
                sender.sendMessage(Component.text("❌ O comando de banco está desativado durante o expurgo.").color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if (ctx.getSource().getExecutor() instanceof Player player) {
                new BancoEvent(this).abrirMenuPrincipal(player);
                return Command.SINGLE_SUCCESS;
            }
            sender.sendMessage(Component.text("❌ Apenas jogadores podem usar este comando!").color(NamedTextColor.RED));
            return Command.SINGLE_SUCCESS;
        }));

        // Comando /nexus team
        nexusRoot.then(Commands.literal("team").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (config.getBoolean("expurgo")) {
                sender.sendMessage(Component.text("❌ Os comandos de time estão desativados durante o expurgo.").color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }
            if (ctx.getSource().getExecutor() instanceof Player player) {
                Team.abrirMenuTeam(player);
            } else {
                sender.sendMessage(Component.text("❌ Apenas jogadores podem usar este comando!").color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));

        // Comando /nexus vender
        nexusRoot.then(Commands.literal("vender")
                .executes(ctx -> {
                    if (ctx.getSource().getExecutor() instanceof Player player) {
                        venderMinerio(player, player.getInventory().getItemInMainHand().getAmount());
                        return Command.SINGLE_SUCCESS;
                    }
                    ctx.getSource().getSender().sendMessage(Component.text("❌ Apenas jogadores podem usar este comando!").color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("quantidade", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player) {
                                int quantidade = ctx.getArgument("quantidade", Integer.class);
                                venderMinerio(player, quantidade);
                                return Command.SINGLE_SUCCESS;
                            }
                            ctx.getSource().getSender().sendMessage(Component.text("❌ Apenas jogadores podem usar este comando!").color(NamedTextColor.RED));
                            return Command.SINGLE_SUCCESS;
                        }))
        );

        // Comando oculto para renascer
        nexusRoot.then(Commands.literal("pagar_renascer").executes(ctx -> {
            if (config.getBoolean("sistema_renascimento_ativado", true)) {
                if (ctx.getSource().getExecutor() instanceof Player player) {
                    MorteEvent.handlePaidRespawn(player);
                    return Command.SINGLE_SUCCESS;
                }
            }
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

        // Comando /nexu para operadores
        LiteralArgumentBuilder<CommandSourceStack> nexuRoot = Commands.literal("nexu").requires(sender -> sender.getSender().isOp()).executes(ctx -> {
            Component mensagem = Component.text()
                    .append(Component.text("\n§4§l⚡ §c§lNEXU - COMANDOS DE OPERADOR §4§l⚡\n").decorate(TextDecoration.BOLD))
                    .append(Component.text("§7➤ §b/nexu setlevel <level> §f- Setar level da relíquia\n"))
                    .append(Component.text("§7➤ §b/nexu expurgar <true/false> §f- Ativar/desativar expurgo\n"))
                    .append(Component.text("§7➤ §b/nexu receber <jogadores> §f- Dar relíquia aleatória\n"))
                    .append(Component.text("§7➤ §b/nexu dar <jogador> <reliquia> §f- Dar relíquia específica\n"))
                    .append(Component.text("§7➤ §b/nexu remo <jogador> <reliquia> §f- Remover relíquia de jogador\n"))
                    .append(Component.text("§7➤ §b/nexu missao <jogador/all> <tipo> <dificuldade> §f- Gerar missão especial\n"))
                    .append(Component.text("§7➤ §b/nexu finalizarmissao <jogador> §f- Finalizar missão de jogador\n"))
                    .append(Component.text("§7➤ §b/nexu dar moly <jogador> <quantia> §f- Dar moly a um jogador\n"))
                    .append(Component.text("§7➤ §b/nexu limite <valor> §f- Alterar limite de relíquias\n"))
                    .append(Component.text("§7➤ §b/nexu reliquia receber ao entrar <on/off> §f- Ativar/Desativar reliquia inicial\n"))
                    .append(Component.text("§7➤ §b/nexu sistema renascer <on/off> §f- Ativar/Desativar sistema de renascimento\n"))
                    .append(Component.text("§7➤ §b/nexu op <player> §f- Dar permissões de OP limitadas ao jogador\n"))
                    .append(Component.text("§7➤ §b/nexu alma dar <player> <quantidade> §f- Dar almas para um jogador\n"))
                    .append(Component.text("§7➤ §b/nexu procurado <player> <valor> §f- Marcar jogador como procurado\n"))
                    .append(Component.text("§7➤ §b/nexu playerlist all §f- Forçar atualização da player list\n"))
                    .append(Component.text("§7➤ §b/nexu addlist <player> <cargo> §f- Adicionar cargo na player list\n"))
                    .append(Component.text("§4§l⚡ §c§lNEXU - COMANDOS DE OPERADOR §4§l⚡\n").decorate(TextDecoration.BOLD))
                    .build();

            ctx.getSource().getSender().sendMessage(mensagem);
            return Command.SINGLE_SUCCESS;
        });

        // Novo comando /nexu playerlist all
        nexuRoot.then(Commands.literal("playerlist").then(Commands.literal("all").executes(ctx -> {
            playerListManager.updateAllPlayerLists();
            ctx.getSource().getSender().sendMessage(Component.text("✅ Player list atualizada para todos os jogadores!")
                    .color(NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        })));

        // Novo comando /nexu addlist
        nexuRoot.then(Commands.literal("addlist").then(Commands.argument("player", ArgumentTypes.player()).then(Commands.argument("cargo", StringArgumentType.string()).executes(ctx -> {
                    final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                    final Player player = targetResolver.resolve(ctx.getSource()).getFirst();
                    final String cargo = ctx.getArgument("cargo", String.class).toLowerCase();
                    final CommandSender sender = ctx.getSource().getSender();

                    playerListManager.setPlayerRank(player.getUniqueId(), cargo);
                    sender.sendMessage(Component.text("✅ Cargo de " + player.getName() + " definido para " + cargo + ".").color(NamedTextColor.GREEN));

                    return Command.SINGLE_SUCCESS;
                })))
        );

        // Comando dar moly (operador)
        nexuRoot.then(Commands.literal("dar").then(Commands.literal("moly").then(Commands.argument("jogador", ArgumentTypes.player()).then(Commands.argument("quantia", DoubleArgumentType.doubleArg()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogador", PlayerSelectorArgumentResolver.class);
            final Player player = targetResolver.resolve(ctx.getSource()).getFirst();
            final double quantia = ctx.getArgument("quantia", Double.class);
            final CommandSender sender = ctx.getSource().getSender();

            if (nexusCentralBank.getSaldo() >= quantia) {
                Economia.adicionarSaldo(player, quantia, "Comando de Operador");
                nexusCentralBank.setSaldo(nexusCentralBank.getSaldo() - quantia);
                sender.sendMessage(Component.text("✅ §a" + quantia + " moly(s) foram adicionados ao saldo de §b" + player.getName()).color(NamedTextColor.GREEN));
                player.sendMessage(Component.text("🎁 §aVocê recebeu " + quantia + " moly(s) de um operador!").color(NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("❌ O Banco Central não tem saldo suficiente para dar esta quantia.").color(NamedTextColor.RED));
            }

            return Command.SINGLE_SUCCESS;
        })))));

        // Novo comando para dar almas (operador)
        nexuRoot.then(Commands.literal("alma").then(Commands.literal("dar").then(Commands.argument("player", ArgumentTypes.player()).then(Commands.argument("quantidade", IntegerArgumentType.integer()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
            final Player player = targetResolver.resolve(ctx.getSource()).getFirst();
            final int quantidade = ctx.getArgument("quantidade", Integer.class);
            final CommandSender sender = ctx.getSource().getSender();

            PersistentDataContainer pdc = player.getPersistentDataContainer();
            int almasAtuais = pdc.getOrDefault(MISSAOMORTE.key, PersistentDataType.INTEGER, 0);
            int novasAlmas = almasAtuais + quantidade;
            pdc.set(MISSAOMORTE.key, PersistentDataType.INTEGER, novasAlmas);

            sender.sendMessage(Component.text("✅ §a" + quantidade + " almas foram adicionadas para " + player.getName() + ".").color(NamedTextColor.GREEN));
            player.sendMessage(Component.text("✨ Você recebeu " + quantidade + " almas de um operador!").color(NamedTextColor.YELLOW));
            return Command.SINGLE_SUCCESS;
        })))));


        // Novo comando para remover relíquia
        nexuRoot.then(Commands.literal("remo").then(Commands.argument("jogador", ArgumentTypes.player()).then(Commands.argument("reliquia", StringArgumentType.string()).suggests((ctx, builder) -> {
            for (String reliquia : names) {
                builder.suggest(reliquia);
            }
            return builder.buildFuture();
        }).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogador", PlayerSelectorArgumentResolver.class);
            final Player player = targetResolver.resolve(ctx.getSource()).getFirst();
            final String reliquiaNome = ctx.getArgument("reliquia", String.class).toLowerCase();
            final CommandSender sender = ctx.getSource().getSender();

            if (!names.contains(reliquiaNome)) {
                sender.sendMessage(Component.text("❌ §cRelíquia inválida!").color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }

            // Remove do inventário
            boolean removedFromInv = false;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.hasItemMeta()) {
                    PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
                    if (reliquiaNome.equals(data.get(NEXUS.key, PersistentDataType.STRING))) {
                        player.getInventory().remove(item);
                        removedFromInv = true;
                        break;
                    }
                }
            }

            // Remove do registro do servidor
            config.set("nexus." + reliquiaNome, null);
            saveConfig();

            if (removedFromInv) {
                sender.sendMessage(Component.text("✅ §aA relíquia '" + reliquiaNome + "' foi removida do jogador '" + player.getName() + "'.").color(NamedTextColor.GREEN));
                player.sendMessage(Component.text("❌ §cA relíquia '" + reliquiaNome + "' foi removida de você por um administrador.").color(NamedTextColor.RED));
            } else {
                sender.sendMessage(Component.text("❌ §cO jogador '" + player.getName() + "' não possui a relíquia '" + reliquiaNome + "'. Mas o registro foi limpo.").color(NamedTextColor.YELLOW));
            }
            return Command.SINGLE_SUCCESS;
        }))));

        // Comando setlevel (operador)
        nexuRoot.then(Commands.literal("setlevel").then(Commands.argument("level", IntegerArgumentType.integer()).executes(ctx -> {
            if(ctx.getSource().getExecutor() instanceof Player player){
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(stack == null || !stack.hasItemMeta()){
                    player.sendMessage(Component.text("❌ §cSegure uma relíquia na mão!")
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
                            player.sendMessage(Component.text("✅ §aLevel da relíquia definido para: " + level)
                                    .color(NamedTextColor.GREEN));
                        }
                    }
                } else {
                    player.sendMessage(Component.text("❌ §cSegure uma relíquia Nexus na mão!")
                            .color(NamedTextColor.RED));
                }
            }else{
                ctx.getSource().getSender().sendMessage(Component.text("❌ §cApenas jogadores podem usar este comando!")
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        })));

        // Comando expurgar (operador)
        nexuRoot.then(Commands.literal("expurgar").then(Commands.argument("exp", BoolArgumentType.bool()).executes(ctx -> {
            boolean exp = ctx.getArgument("exp", boolean.class);
            config.set("expurgo",exp);
            saveConfig();
            if(exp){
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendMessage(Component.text("⚡ §c§lMODO EXPURGO ATIVADO!\n§4⚠ §cTodas as relíquias dropam ao morrer!")
                            .color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
                });
            }else{
                Bukkit.getOnlinePlayers().forEach(player -> {
                    playerListManager.updateAllPlayerLists();
                });
                ctx.getSource().getSender().sendMessage(Component.text("🛡️ §a§lMODO EXPURGO DESATIVADO\n§2✔ §aSuas relíquias estão seguras!")
                        .color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            }
            ctx.getSource().getSender().sendMessage(Component.text("✅ §aExpurgo definido para: " + exp)
                    .color(NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        })));

        // Comando receber (operador) - Dar relíquia aleatória para jogadores
        nexuRoot.then(Commands.literal("receber").then(Commands.argument("jogadores", ArgumentTypes.players()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogadores", PlayerSelectorArgumentResolver.class);
            final Collection<Player> players = targetResolver.resolve(ctx.getSource());
            final CommandSender sender = ctx.getSource().getSender();

            if (players.isEmpty()) {
                sender.sendMessage(Component.text("❌ §cNenhum jogador encontrado!")
                        .color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }

            Random random = new Random();
            String reliquiaAleatoria = names.get(random.nextInt(names.size()));
            Nexus nexus = ItemsRegistro.getFromNome(reliquiaAleatoria);

            if (nexus == null) {
                sender.sendMessage(Component.text("❌ §cRelíquia não encontrada!")
                        .color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }

            for (Player player : players) {
                ItemStack item = nexus.getItem(1);
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING, player.getUniqueId().toString());
                item.setItemMeta(meta);

                player.getInventory().addItem(item);
                config.set("nexus." + reliquiaAleatoria, player.getUniqueId().toString());

                player.sendMessage(Component.text("🎁 §aVocê recebeu a relíquia: §6" + reliquiaAleatoria)
                        .color(NamedTextColor.GREEN));
            }

            saveConfig();
            sender.sendMessage(Component.text("✅ §aRelíquia §6" + reliquiaAleatoria + " §adada para " + players.size() + " jogador(es)!")
                    .color(NamedTextColor.GREEN));

            return Command.SINGLE_SUCCESS;
        })));

        // Comando dar (operador) - Dar relíquia específica para jogador
        nexuRoot.then(Commands.literal("dar").then(Commands.argument("jogador", ArgumentTypes.player()).then(Commands.argument("reliquia", StringArgumentType.string()).suggests( (ctx, builder) -> {
            for (String reliquia : names) {
                builder.suggest(reliquia);
            }
            return builder.buildFuture();
        }).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogador", PlayerSelectorArgumentResolver.class);
            final Player player = targetResolver.resolve(ctx.getSource()).getFirst();
            final String reliquia = ctx.getArgument("reliquia", String.class);
            final CommandSender sender = ctx.getSource().getSender();

            if (!names.contains(reliquia.toLowerCase())) {
                sender.sendMessage(Component.text("❌ §cRelíquia inválida! Use uma das seguintes: " + String.join(", ", names))
                        .color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }

            Nexus nexus = ItemsRegistro.getFromNome(reliquia.toLowerCase());

            if (nexus == null) {
                sender.sendMessage(Component.text("❌ §cRelíquia não encontrada!")
                        .color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }

            ItemStack item = nexus.getItem(1);
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING, player.getUniqueId().toString());
            item.setItemMeta(meta);

            player.getInventory().addItem(item);
            config.set("nexus." + reliquia.toLowerCase(), player.getUniqueId().toString());
            saveConfig();

            player.sendMessage(Component.text("🎁 §aVocê recebeu a relíquia: §6" + reliquia)
                    .color(NamedTextColor.GREEN));
            sender.sendMessage(Component.text("✅ §aRelíquia §6" + reliquia + " §adada para §b" + player.getName())
                    .color(NamedTextColor.GREEN));

            return Command.SINGLE_SUCCESS;
        }))));

        // Comando para controlar se relíquias são dadas ao entrar
        nexuRoot.then(Commands.literal("reliquia").then(Commands.literal("receber").then(Commands.literal("ao").then(Commands.literal("entrar").then(Commands.argument("on/off", BoolArgumentType.bool()).executes(ctx -> {
            boolean status = ctx.getArgument("on/off", boolean.class);
            config.set("dar_reliquia_ao_entrar", status);
            saveConfig();

            ctx.getSource().getSender().sendMessage(Component.text("✅ §aRelíquia aleatória ao entrar: " + (status ? "Ativado" : "Desativado")).color(NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        }))))));

        // Comando missao (operador) - Gerar missão especial
        nexuRoot.then(Commands.literal("missao").then(Commands.argument("alvo", StringArgumentType.string()).suggests((context, builder) -> {
            builder.suggest("all");
            for (Player player : Bukkit.getOnlinePlayers()) {
                builder.suggest(player.getName());
            }
            return builder.buildFuture();
        }).then(Commands.argument("tipoMissao", StringArgumentType.string()).suggests((context, builder) -> {
            SpecialEvent specialEvent = new SpecialEvent(this);
            for (String type : specialEvent.getMissionTypes()) {
                builder.suggest(type);
            }
            builder.suggest("ender");
            return builder.buildFuture();
        }).then(Commands.argument("dificuldade", StringArgumentType.string()).suggests((context, builder) -> {
            builder.suggest("facil");
            builder.suggest("medio");
            builder.suggest("dificil");
            builder.suggest("extreme");
            builder.suggest("ender");
            return builder.buildFuture();
        }).executes(ctx -> {
            final String alvo = ctx.getArgument("alvo", String.class);
            final String tipoMissao = ctx.getArgument("tipoMissao", String.class);
            final String dificuldade = ctx.getArgument("dificuldade", String.class);
            final CommandSender sender = ctx.getSource().getSender();

            SpecialEvent specialEvent = new SpecialEvent(this);

            if (alvo.equalsIgnoreCase("all") || alvo.equalsIgnoreCase("@a")) {
                Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                if (players.isEmpty()) {
                    sender.sendMessage(Component.text("❌ §cNenhum jogador encontrado!")
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }
                for (Player player : players) {
                    specialEvent.gerarMissao(player, tipoMissao, dificuldade, true);
                }
                sender.sendMessage(Component.text("✅ §aMissão especial gerada para todos os jogadores online!")
                        .color(NamedTextColor.GREEN));
            } else {
                Player player = Bukkit.getPlayer(alvo);
                if (player != null && player.isOnline()) {
                    specialEvent.gerarMissao(player, tipoMissao, dificuldade, true);
                    sender.sendMessage(Component.text("✅ §aMissão especial gerada para §b" + player.getName())
                            .color(NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("❌ §cJogador não encontrado ou offline!")
                            .color(NamedTextColor.RED));
                }
            }

            return Command.SINGLE_SUCCESS;
        })))));

        // Comando para finalizar missão do jogador
        nexuRoot.then(Commands.literal("finalizarmissao").then(Commands.argument("jogador", ArgumentTypes.player()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogador", PlayerSelectorArgumentResolver.class);
            final Player player = targetResolver.resolve(ctx.getSource()).getFirst();
            final CommandSender sender = ctx.getSource().getSender();

            SpecialEvent specialEvent = new SpecialEvent(this);
            specialEvent.finalizarMissao(player);

            sender.sendMessage(Component.text("✅ §aMissão de " + player.getName() + " foi finalizada!")
                    .color(NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        })));

        // Comando limite (operador)
        nexuRoot.then(Commands.literal("limite").then(Commands.argument("valor", IntegerArgumentType.integer()).executes(ctx -> {
            int valor = ctx.getArgument("valor", int.class);
            config.set("limite", valor);
            saveConfig();

            ctx.getSource().getSender().sendMessage(Component.text("✅ §aLimite de relíquias definido para: " + valor)
                    .color(NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        })));

        // Novo comando /nexu op
        nexuRoot.then(Commands.literal("op").then(Commands.argument("player", ArgumentTypes.player()).executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if (!sender.isOp()) {
                sender.sendMessage(Component.text("❌ Você não tem permissão para usar este comando!").color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }

            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
            final Player targetPlayer = targetResolver.resolve(ctx.getSource()).getFirst();

            // Adiciona a permissão persistente
            if (config.contains("op-players." + targetPlayer.getUniqueId())) {
                sender.sendMessage(Component.text("❌ " + targetPlayer.getName() + " já é um OP limitado.").color(NamedTextColor.RED));
            } else {
                config.set("op-players." + targetPlayer.getUniqueId(), true);
                saveConfig();

                // Aplica a permissão imediatamente se o jogador estiver online
                if (targetPlayer.isOnline()) {
                    PermissionAttachment attachment = targetPlayer.addAttachment(this);
                    attachment.setPermission("reliquiasnexus.command.nexus", true);
                    attachment.setPermission("reliquiasnexus.command.nexu", true);
                    opAttachments.put(targetPlayer.getUniqueId(), attachment);
                }

                sender.sendMessage(Component.text("✅ " + targetPlayer.getName() + " agora tem permissões de OP limitadas.").color(NamedTextColor.GREEN));
                targetPlayer.sendMessage(Component.text("⚡ Você recebeu permissões de OP limitadas. Use /nexus e /nexu para ver os comandos!").color(NamedTextColor.GOLD));
            }

            return Command.SINGLE_SUCCESS;
        })));

        // Comando para controlar o sistema de renascimento
        nexuRoot.then(Commands.literal("sistema").then(Commands.literal("renascer").then(Commands.argument("on/off", BoolArgumentType.bool()).executes(ctx -> {
            boolean status = ctx.getArgument("on/off", boolean.class);
            config.set("sistema_renascimento_ativado", status);
            saveConfig();

            ctx.getSource().getSender().sendMessage(Component.text("✅ §aSistema de Renascimento: " + (status ? "Ativado" : "Desativado")).color(NamedTextColor.GREEN));
            return Command.SINGLE_SUCCESS;
        }))));

        // Comando para abrir o ender chest
        LiteralCommandNode<CommandSourceStack> eccommandNode = Commands.literal("ec").executes(ctx -> {
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

        // Novo comando /nexu procurado [player] {valor}
        nexuRoot.then(Commands.literal("procurado")
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

        // Novo comando para iniciar missão da Morte
        nexuRoot.then(Commands.literal("missao_morte").executes(ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (sender instanceof Player player) {
                Morte.startMissaoMorte(player);
            } else {
                sender.sendMessage(Component.text("❌ Apenas jogadores podem usar este comando!").color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));

        // Registrar comandos
        LiteralCommandNode<CommandSourceStack> nexusCommand = nexusRoot.build();
        LiteralCommandNode<CommandSourceStack> nexuCommand = nexuRoot.build();

        ArgumentTypes Arguments = null;
        LiteralCommandNode<CommandSourceStack> nexusTeamRoot = Commands.literal("team")
                .executes(ctx -> {
                    if (ctx.getSource().getExecutor() instanceof Player player) {
                        Team.abrirMenuTeam(player);
                    } else {
                        ctx.getSource().getSender().sendMessage(Component.text("❌ §cApenas jogadores podem usar este comando!").color(NamedTextColor.RED));
                    }
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("criar").then(Commands.argument("nome", StringArgumentType.string()).executes(ctx -> {
                    if (ctx.getSource().getExecutor() instanceof Player player) {
                        String teamName = ctx.getArgument("nome", String.class);
                        Team.criarTeam(player, teamName);
                    }
                    return Command.SINGLE_SUCCESS;
                })))
                .then(Commands.literal("convidar").then(Commands.argument("player", ArgumentTypes.player()).executes(ctx -> {
                    if (ctx.getSource().getExecutor() instanceof Player player) {
                        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                        final Player targetPlayer = targetResolver.resolve(ctx.getSource()).getFirst();
                        Team.convidarPlayer(player, targetPlayer);
                    }
                    return Command.SINGLE_SUCCESS;
                })))
                .then(Commands.literal("aceitar").then(Commands.argument("team", StringArgumentType.string()).executes(ctx -> {
                            if (ctx.getSource().getExecutor() instanceof Player player) {
                                String teamName = ctx.getArgument("team", String.class);
                                Team.aceitarConvite(player, teamName);
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                )
                .then(Commands.literal("sair").executes(ctx -> {
                    if (ctx.getSource().getExecutor() instanceof Player player) {
                        Team.sairTeam(player);
                    }
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("depositar").then(Commands.argument("valor", DoubleArgumentType.doubleArg(0)).executes(ctx -> {
                    if (ctx.getSource().getExecutor() instanceof Player player) {
                        double valor = ctx.getArgument("valor", Double.class);
                        Team.depositar(player, valor);
                    }
                    return Command.SINGLE_SUCCESS;
                })))
                .build();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(nexusCommand);
            commands.registrar().register(nexuCommand);
            commands.registrar().register(nexusTeamRoot);
            commands.registrar().register(ecCommandNode);
        });

        // Registrar eventos
        getServer().getPluginManager().registerEvents(new JoinQuitEvent(this), this);
        getServer().getPluginManager().registerEvents(new LimitadorEvent(this), this);
        getServer().getPluginManager().registerEvents(new PassivaEvent(), this);
        getServer().getPluginManager().registerEvents(new PerdeuEvent(this), this);
        getServer().getPluginManager().registerEvents(new EvoluirEvent(this), this);
        getServer().getPluginManager().registerEvents(new SpecialEvent(this), this);
        getServer().getPluginManager().registerEvents(new LojaEvent(this), this);
        getServer().getPluginManager().registerEvents(new BancoEvent(this), this);
        getServer().getPluginManager().registerEvents(new MorteEvent(this), this);
        getServer().getPluginManager().registerEvents(playerListManager, this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);


        getServer().getConsoleSender().sendMessage("§2✅ §a[Nexus]: Plugin Ativado com Sucesso!");
    }

    private void venderMinerio(Player player, int quantidade) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(Component.text("❌ Você precisa segurar um minério para vendê-lo.").color(NamedTextColor.RED));
            return;
        }

        Material material = itemInHand.getType();

        if (itemInHand.getAmount() < quantidade) {
            player.sendMessage(Component.text("❌ Você não tem a quantidade necessária para vender.").color(NamedTextColor.RED));
            return;
        }

        String nomeItem = "";
        double valorUnitario = 0;
        switch (material) {
            case NETHERITE_INGOT:
                valorUnitario = 750;
                nomeItem = "Barra de Netherite";
                break;
            case DIAMOND:
                valorUnitario = 500;
                nomeItem = "Diamante";
                break;
            case EMERALD:
                valorUnitario = 300;
                nomeItem = "Esmeralda";
                break;
            case GOLD_INGOT:
                valorUnitario = 200;
                nomeItem = "Barra de Ouro";
                break;
            case IRON_INGOT:
                valorUnitario = 100;
                nomeItem = "Barra de Ferro";
                break;
            case COAL:
                valorUnitario = 50;
                nomeItem = "Carvão";
                break;
            case REDSTONE:
                valorUnitario = 75;
                nomeItem = "Redstone";
                break;
            default:
                player.sendMessage(Component.text("❌ Este item não pode ser vendido.").color(NamedTextColor.RED));
                return;
        }

        double valorTotal = valorUnitario * quantidade;

        itemInHand.setAmount(itemInHand.getAmount() - quantidade);
        Economia.adicionarSaldo(player, valorTotal, "Venda de " + nomeItem);

        // Adiciona o valor ao saldo do banco central
        nexusCentralBank.setSaldo(nexusCentralBank.getSaldo() + valorTotal);

        player.sendMessage(Component.text("✅ Você vendeu " + quantidade + " de " + nomeItem + " por " + valorTotal + " moly.").color(NamedTextColor.GREEN));
    }

    private boolean processarTroca(Player player1, Player player2, String relic1, String relic2) {
        try {
            // Verifica se a relíquia de P1 é realmente a que ele está segurando
            ItemStack stackP1 = player1.getInventory().getItemInMainHand();
            if (stackP1 == null || !stackP1.hasItemMeta() || !stackP1.getItemMeta().getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING) || !stackP1.getItemMeta().getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING).equals(relic1)) {
                player1.sendMessage(Component.text("❌ §cVocê não está segurando a relíquia que ofereceu!")
                        .color(NamedTextColor.RED));
                return false;
            }

            // Verifica se a relíquia de P2 é realmente a que ele está segurando
            ItemStack stackP2 = player2.getInventory().getItemInMainHand();
            if (stackP2 == null || !stackP2.hasItemMeta() || !stackP2.getItemMeta().getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING) || !stackP2.getItemMeta().getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING).equals(relic2)) {
                player1.sendMessage(Component.text("❌ §cO outro jogador não está segurando a relíquia que ofereceu!")
                        .color(NamedTextColor.RED));
                player2.sendMessage(Component.text("❌ §cVocê precisa segurar a relíquia que está trocando!")
                        .color(NamedTextColor.RED));
                return false;
            }

            // Lógica de troca - trocar os itens entre os jogadores
            // Remover do inventário
            removerReliquiaMao(player1);
            removerReliquiaMao(player2);

            // Trocar os donos das relíquias no config antes de dar os itens
            config.set("nexus." + relic1, player2.getUniqueId().toString());
            config.set("nexus." + relic2, player1.getUniqueId().toString());
            saveConfig();

            // Criar e dar novos itens com os donos atualizados
            ItemStack item1 = criarItemReliquia(player2, relic1);
            ItemStack item2 = criarItemReliquia(player1, relic2);

            player1.getInventory().addItem(item2);
            player2.getInventory().addItem(item1);

            return true;
        } catch (Exception e) {
            getLogger().warning("Erro ao processar troca: " + e.getMessage());
        }
        return false;
    }

    private ItemStack criarItemReliquia(Player player, String nomeRelic) {
        try {
            Nexus nexus = ItemsRegistro.getFromNome(nomeRelic);
            if(nexus != null) {
                PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                NamespacedKey key = getKey(nomeRelic);
                int level = dataPlayer.getOrDefault(key, PersistentDataType.INTEGER, 1);

                ItemStack item = nexus.getItem(level);
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(DONO.key, PersistentDataType.STRING, player.getUniqueId().toString());
                item.setItemMeta(meta);

                return item;
            }
        } catch (Exception e) {
            getLogger().warning("Erro ao criar item: " + e.getMessage());
        }
        return null;
    }

    private void removerReliquiaMao(Player player) {
        ItemStack itemMao = player.getInventory().getItemInMainHand();
        if(itemMao != null && itemMao.hasItemMeta()) {
            ItemMeta meta = itemMao.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key, PersistentDataType.STRING)) {
                player.getInventory().setItemInMainHand(null);
            }
        }
    }

    @Override
    public void onDisable() {
        // Salva o saldo do banco central no desligamento do servidor
        if (nexusCentralBank != null) {
            config.set("nexus_central_bank.saldo", nexusCentralBank.getSaldo());
        }
        saveConfig();
        getServer().getConsoleSender().sendMessage("§4❌ §c[Nexus]: Plugin Desativado!");
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
}