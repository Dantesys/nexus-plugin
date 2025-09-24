package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
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
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.Economia;
import org.dantesys.reliquiasNexus.util.NexusKeys;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class JoinQuitEvent implements Listener {
    private final ReliquiasNexus plugin;
    public JoinQuitEvent(ReliquiasNexus plugin){
        this.plugin=plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws Exception {
        Player player = event.getPlayer();
        PersistentDataContainer container = player.getPersistentDataContainer();
        int qtd = container.getOrDefault(QTD.key, PersistentDataType.INTEGER,0);
        boolean novato = container.getOrDefault(new NamespacedKey("nexus_novato","novato"),PersistentDataType.BOOLEAN,true);
        container.set(SPECIAL.key,PersistentDataType.INTEGER,qtd);
        container.set(SPECIAL.key, PersistentDataType.INTEGER,0);
        boolean join = plugin.getConfig().getBoolean("recursos.nexus_onjoin");
        if(novato){
            container.set(new NamespacedKey("nexus_novato","novato"),PersistentDataType.BOOLEAN,false);
            if(join){
                List<Nexus> reliquias = ItemsRegistro.getValidReliquia(ReliquiasNexus.getNexusConfig());
                Random rng = new Random();
                int escolhido = rng.nextInt(reliquias.size());
                Nexus n = reliquias.get(escolhido);
                String nome = n.getNome();
                ReliquiasNexus.setConfigSave("nexus."+nome,player.getUniqueId().toString());
                plugin.saveConfig();
                container.set(QTD.key,PersistentDataType.INTEGER,1);
                int level =1;
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
                String r=ReliquiasNexus.getLang().getString("joinquit.relic");
                if(r==null){
                    r="Você recebeu a reliquia do <relic>";
                }
                r=r.replace("<relic>",nome);
                player.sendMessage(Component.text("§2"+r));
            }
            // Adiciona o livro de história ao inventário do jogador
            player.getInventory().addItem(ItemsRegistro.nexusStoryBook.getItem(1));

            String msg=ReliquiasNexus.getLang().getString("joinquit.joinnew");
            if(msg==null){
                msg="Bem-vindo ao jogo, Jogador <player>";
            }
            msg=msg.replace("<player>",player.getName());
            event.joinMessage(Component.text("§2"+msg));
        }else{
            String msg=ReliquiasNexus.getLang().getString("joinquit.join");
            if(msg==null){
                msg="Bem-vindo devolta, Jogador <player>";
            }
            msg=msg.replace("<player>",player.getName());
            event.joinMessage(Component.text("§2"+msg));
        }
        String rank = plugin.getConfig().getString("players." + player.getUniqueId().toString() + ".rank", "membro");
        String r = rank.substring(0, 1).toUpperCase();
        String corrigido = r + rank.substring(1);
        // Formata a mensagem com a tag e o nome do jogador
        if(plugin.getConfig().getBoolean("op-players." + player.getUniqueId())){
            player.addAttachment(plugin).setPermission("reliquiasnexus.opzim", true);
        }
        Color cor = plugin.getConfig().getColor("cargo."+rank, Color.WHITE);
        Component finalNome = Component.text("["+corrigido+"]").color(TextColor.color(cor.asRGB())).append(Component.text(player.getName()).color(NamedTextColor.WHITE));
        player.setDisplayName("["+corrigido+"]"+player.getName());
        player.displayName(finalNome);
        player.setCustomNameVisible(true);
        player.setResourcePack("https://github.com/Dantesys/nexus-plugin/raw/refs/heads/master/ResourcePackNexus/ResourcePackNexus.zip");
        plugin.reiniciarMissao(player);
        File file = new File(ReliquiasNexus.getPlugin(ReliquiasNexus.class).getDataFolder(), "vendas.yml");
        YamlConfiguration saldoOff = YamlConfiguration.loadConfiguration(file);
        double saldo = saldoOff.getDouble(player.getUniqueId().toString(),0.0);
        double atual = container.getOrDefault(SALDO.key,PersistentDataType.DOUBLE,0.0);
        container.set(SALDO.key,PersistentDataType.DOUBLE,saldo+atual);
        saldoOff.set(player.getUniqueId().toString(),0.0);
        try {
            saldoOff.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(player.getPersistentDataContainer().has(PROCURADO.key)){
            double recompensa = player.getPersistentDataContainer().getOrDefault(PROCURADO.key,PersistentDataType.DOUBLE,0.0);
            String precoStr = String.format("$ %.2f ", recompensa);
            Bukkit.broadcast(Component.text(ReliquiasNexus.getLang().getString("procurados.join","Cuidado!<break> jogador procurado acabou de entrar<break> recompensa <value> <moneyName>").replace("<value>",precoStr).replace("<break>","\n").replace("<moneyName>",plugin.getConfig().getString("recursos.moneyName","moly"))).color(NamedTextColor.YELLOW));
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.pausarMissao(player);
        String msg=ReliquiasNexus.getLang().getString("joinquit.quit");
        if(msg==null){
            msg="O Jogador <player> saiu do jogo!";
        }
        msg=msg.replace("<player>",player.getName());
        event.quitMessage(Component.text("§4"+msg));
    }
}