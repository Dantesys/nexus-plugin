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
import org.dantesys.reliquiasNexus.util.UpdaterCheck;

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
        new UpdaterCheck(this, "dantesys/nexus-plugin").checkForUpdates();

        // Comando /nexus principal
        LiteralArgumentBuilder<CommandSourceStack> nexusRoot = Commands.literal("nexus").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();

            Component mensagem = Component.text()
                    .append(Component.text("\n§6§l⭐ §e§lNEXUS COMMANDS §6§l⭐\n").decorate(TextDecoration.BOLD))
                    .append(Component.text("§7➤ §b/nexus livro §f- Receber livro das relíquias\n"))
                    .append(Component.text("§7➤ §b/nexus evoluir §f- Evoluir relíquia na mão\n"))
                    .append(Component.text("§7➤ §b/nexus expurgo §f- Ver status do expurgo\n"))
                    .append(Component.text("§7➤ §b/nexus trocar <jogador> §f- Oferecer troca\n"))
                    .append(Component.text("§7➤ §b/nexus list §f- Listar relíquias do servidor\n"))
                    .append(Component.text("§7➤ §b/nexus level §f- Seus níveis de relíquias\n"))
                    .append(Component.text("§7➤ §b/nexus aceitar §f- Aceitar oferta de troca\n"))
                    .append(Component.text("§7➤ §b/nexus cancelar §f- Recusar oferta de troca\n"))
                    .append(Component.text("§6§l⭐ §e§lNEXUS COMMANDS §6§l⭐\n").decorate(TextDecoration.BOLD))
                    .build();

            sender.sendMessage(mensagem);
            return Command.SINGLE_SUCCESS;
        });

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
                        NamespacedKey key = NexusKeys.getKey(nome);
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
                ctx.getSource().getSender().sendMessage(Component.text("🛡️ §a§lMODO EXPURGO DESATIVADO\n§2✔ §aSuas relíquias estão seguras!")
                        .color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            }
            return Command.SINGLE_SUCCESS;
        }));

        // Comando trocar
        nexusRoot.then(Commands.literal("trocar").then(Commands.argument("jogador", ArgumentTypes.player()).executes(ctx -> {
            final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("jogador", PlayerSelectorArgumentResolver.class);
            final Player p = targetResolver.resolve(ctx.getSource()).getFirst();
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
                                        .clickEvent(ClickEvent.runCommand("/nexus aceitar"))
                                        .hoverEvent(HoverEvent.showText(Component.text("§aClique para aceitar a troca"))))
                                .append(Component.text("❌ RECUSAR")
                                        .color(NamedTextColor.RED)
                                        .clickEvent(ClickEvent.runCommand("/nexus cancelar"))
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

        // Comando aceitar
        nexusRoot.then(Commands.literal("aceitar").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                Troca t = trocas.get(player.getUniqueId());

                if(t == null){
                    sender.sendMessage(Component.text("❌ §cVocê não tem ofertas de troca pendentes!")
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }

                Player p = Bukkit.getPlayer(t.uuid());
                if(p == null || !p.isOnline()){
                    trocas.remove(player.getUniqueId());
                    sender.sendMessage(Component.text("❌ §cO jogador não está mais online!")
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }

                // Lógica de aceitação da troca
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(stack == null || !stack.hasItemMeta()){
                    sender.sendMessage(Component.text("❌ §cVocê precisa segurar uma relíquia na mão para aceitar a troca!")
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }

                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();

                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    if(nome != null){
                        // Processar troca
                        if(processarTroca(player, p, nome, t.stack())){
                            trocas.remove(player.getUniqueId());
                            sender.sendMessage(Component.text("✅ §aTroca realizada com sucesso!")
                                    .color(NamedTextColor.GREEN));
                            p.sendMessage(Component.text("✅ §aTroca realizada com sucesso!")
                                    .color(NamedTextColor.GREEN));
                        } else {
                            sender.sendMessage(Component.text("❌ §cFalha ao processar a troca. Certifique-se de que o outro jogador ainda tem a relíquia!")
                                    .color(NamedTextColor.RED));
                        }
                    }
                } else {
                    sender.sendMessage(Component.text("❌ §cVocê precisa segurar uma relíquia Nexus na mão!")
                            .color(NamedTextColor.RED));
                }
            }else{
                sender.sendMessage(Component.text("❌ §cApenas jogadores podem usar este comando!")
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));

        // Comando cancelar
        nexusRoot.then(Commands.literal("cancelar").executes(ctx -> {
            final CommandSender sender = ctx.getSource().getSender();
            if(ctx.getSource().getExecutor() instanceof Player player){
                Troca t = trocas.remove(player.getUniqueId());

                if(t == null){
                    sender.sendMessage(Component.text("❌ §cVocê não tem ofertas de troca pendentes!")
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }

                Player p = Bukkit.getPlayer(t.uuid());
                if(p != null && p.isOnline()){
                    p.sendMessage(Component.text("❌ §c" + player.getName() + " recusou sua oferta de troca!")
                            .color(NamedTextColor.RED));
                }

                sender.sendMessage(Component.text("❌ §cVocê recusou a oferta de troca.")
                        .color(NamedTextColor.RED));
            }else{
                sender.sendMessage(Component.text("❌ §cApenas jogadores podem usar este comando!")
                        .color(NamedTextColor.RED));
            }
            return Command.SINGLE_SUCCESS;
        }));

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
                List<NamespacedKey> keys = NexusKeys.getKeyLevel();
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

        // Comando /nexu para operadores
        LiteralArgumentBuilder<CommandSourceStack> nexuRoot = Commands.literal("nexu").requires(sender -> sender.getSender().isOp()).executes(ctx -> {
            Component mensagem = Component.text()
                    .append(Component.text("\n§4§l⚡ §c§lNEXU - COMANDOS DE OPERADOR §4§l⚡\n").decorate(TextDecoration.BOLD))
                    .append(Component.text("§7➤ §b/nexu setlevel <level> §f- Setar level da relíquia\n"))
                    .append(Component.text("§7➤ §b/nexu expurgar <true/false> §f- Ativar/desativar expurgo\n"))
                    .append(Component.text("§7➤ §b/nexu receber <jogadores> §f- Dar relíquia aleatória\n"))
                    .append(Component.text("§7➤ §b/nexu dar <jogador> <reliquia> §f- Dar relíquia específica\n"))
                    .append(Component.text("§7➤ §b/nexu remover <jogador> <reliquia> §f- Remover relíquia\n"))
                    .append(Component.text("§7➤ §b/nexu missao <jogador/all> §f- Gerar missão especial\n"))
                    .append(Component.text("§7➤ §b/nexu finalizarmissao <jogador> §f- Finalizar missão de jogador\n"))
                    .append(Component.text("§7➤ §b/nexu limite <valor> §f- Alterar limite de relíquias\n"))
                    .append(Component.text("§4§l⚡ §c§lNEXU - COMANDOS DE OPERADOR §4§l⚡\n").decorate(TextDecoration.BOLD))
                    .build();

            ctx.getSource().getSender().sendMessage(mensagem);
            return Command.SINGLE_SUCCESS;
        });

        // Comando setlevel (operador)
        nexuRoot.then(Commands.literal("setlevel").then(Commands.argument("level", IntegerArgumentType.integer()).executes(ctx -> {
            if(ctx.getSource().getExecutor() instanceof Player player){
                int level = ctx.getArgument("level", int.class);
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(stack == null || !stack.hasItemMeta()){
                    player.sendMessage(Component.text("❌ §cSegure uma relíquia na mão!")
                            .color(NamedTextColor.RED));
                    return Command.SINGLE_SUCCESS;
                }

                ItemMeta meta = stack.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    if(nome!=null){
                        NamespacedKey key = NexusKeys.getKey(nome);
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
                    player.sendMessage(Component.text("🛡️ §a§lMODO EXPURGO DESATIVADO\n§2✔ §aSuas relíquias estão seguras!")
                            .color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                });
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

        // Comando remover (operador) - Remover relíquia de jogador
        nexuRoot.then(Commands.literal("remover").then(Commands.argument("jogador", ArgumentTypes.player()).then(Commands.argument("reliquia", StringArgumentType.string()).suggests( (ctx, builder) -> {
            Player player = ctx.getArgument("jogador", Player.class);
            if(player != null) {
                PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                List<NamespacedKey> keys = NexusKeys.getKeyLevel();
                for(NamespacedKey k : keys) {
                    if (dataPlayer.has(k, PersistentDataType.INTEGER)) {
                        builder.suggest(k.getKey());
                    }
                }
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

            // Verificar se o jogador tem a relíquia
            NamespacedKey key = NexusKeys.getKey(reliquia.toLowerCase());
            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
            int level = dataPlayer.getOrDefault(key, PersistentDataType.INTEGER, 0);

            if (level <= 0) {
                sender.sendMessage(Component.text("❌ §cO jogador §b" + player.getName() + " §cnão possui a relíquia §6" + reliquia)
                        .color(NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }

            // Remover do jogador
            dataPlayer.remove(key);

            // Remover do inventário
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    if (data.has(NEXUS.key, PersistentDataType.STRING)) {
                        String nomeRelic = data.get(NEXUS.key, PersistentDataType.STRING);
                        if (reliquia.equalsIgnoreCase(nomeRelic)) {
                            player.getInventory().remove(item);
                            break;
                        }
                    }
                }
            }

            // Remover do config
            config.set("nexus." + reliquia.toLowerCase(), null);
            saveConfig();

            player.sendMessage(Component.text("🗑️ §cA relíquia §6" + reliquia + " §cfoi removida de você!")
                    .color(NamedTextColor.RED));
            sender.sendMessage(Component.text("✅ §aRelíquia §6" + reliquia + " §aremovida de §b" + player.getName())
                    .color(NamedTextColor.GREEN));

            return Command.SINGLE_SUCCESS;
        }))));

        // Comando missao (operador) - Gerar missão especial
        nexuRoot.then(Commands.literal("missao").then(Commands.argument("alvo", StringArgumentType.string()).executes(ctx -> {
            final String alvo = ctx.getArgument("alvo", String.class);
            final CommandSender sender = ctx.getSource().getSender();

            SpecialEvent specialEvent = new SpecialEvent(this);

            if (alvo.equalsIgnoreCase("all")) {
                // Gerar missão para todos os jogadores online
                for (Player player : Bukkit.getOnlinePlayers()) {
                    specialEvent.gerarMissaoEspecial(player);
                }
                sender.sendMessage(Component.text("✅ §aMissão especial gerada para todos os jogadores online!")
                        .color(NamedTextColor.GREEN));
            } else {
                // Gerar missão para jogador específico
                Player player = Bukkit.getPlayer(alvo);
                if (player != null && player.isOnline()) {
                    specialEvent.gerarMissaoEspecial(player);
                    sender.sendMessage(Component.text("✅ §aMissão especial gerada para §b" + player.getName())
                            .color(NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("❌ §cJogador não encontrado ou offline!")
                            .color(NamedTextColor.RED));
                }
            }

            return Command.SINGLE_SUCCESS;
        })));

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

        // Registrar comandos
        LiteralCommandNode<CommandSourceStack> nexusCommand = nexusRoot.build();
        LiteralCommandNode<CommandSourceStack> nexuCommand = nexuRoot.build();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(nexusCommand);
            commands.registrar().register(nexuCommand);
        });

        // Registrar eventos
        getServer().getPluginManager().registerEvents(new JoinQuitEvent(this), this);
        getServer().getPluginManager().registerEvents(new LimitadorEvent(this), this);
        getServer().getPluginManager().registerEvents(new PassivaEvent(), this);
        getServer().getPluginManager().registerEvents(new PerdeuEvent(), this);
        getServer().getPluginManager().registerEvents(new EvoluirEvent(this), this);
        getServer().getPluginManager().registerEvents(new SpecialEvent(this), this);

        getServer().getConsoleSender().sendMessage("§2✅ §a[Nexus]: Plugin Ativado com Sucesso!");
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
                NamespacedKey key = NexusKeys.getKey(nomeRelic);
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

    public static void saiu(Player p){
        Troca t = trocas.remove(p.getUniqueId());
        if(t == null) return;
        Player player = Bukkit.getPlayer(t.uuid());
        if(player != null){
            player.sendMessage(Component.text("❌ §c" + p.getName() + " saiu do servidor. Troca cancelada.")
                    .color(NamedTextColor.RED));
        }
    }
}
