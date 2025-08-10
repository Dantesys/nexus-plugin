package org.dantesys.reliquiasNexus.eventos;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import io.papermc.paper.event.entity.FishHookStateChangeEvent;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class EvoluirEvent implements Listener {
    private final ReliquiasNexus plugin;

    public EvoluirEvent(ReliquiasNexus plugin) {
        this.plugin = plugin;
    }
    public void tentarEvoluir(Player player, ItemStack nexusItem, int levelAtual,int slot) {
        ItemMeta meta = nexusItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if(data.has(NEXUS.key,PersistentDataType.STRING)){
            String msg = "";
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                String condicao = podeEvoluir(player,nome,levelAtual);
                int level = player.getLevel();
                if(condicao==null){
                    if(level>=levelAtual*10){
                        player.setLevel(player.getLevel()-(10*levelAtual));
                        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                        Nexus n = ItemsRegistro.getFromNome(nome);
                        if(n!=null){
                            nexusItem=n.getItem(levelAtual+1);
                            if(meta.hasEnchants()){
                                meta.getEnchants().forEach((nexusItem::addEnchantment));
                            }
                            switch (nome){
                                case "barbaro" -> {
                                    dataPlayer.set(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(BARBARO.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "ceifador" -> {
                                    dataPlayer.set(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, 0d);
                                    dataPlayer.set(CEIFADOR.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "fazendeiro" -> {
                                    dataPlayer.set(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(FAZENDEIRO.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "guerreiro" -> {
                                    dataPlayer.set(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(GUERREIRO.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "mares" -> {
                                    dataPlayer.set(MISSAOMARES.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(MARES.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "vida" -> {
                                    dataPlayer.set(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
                                    dataPlayer.set(VIDA.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "espiao" -> {
                                    dataPlayer.set(MISSAOESPIAO.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(ESPIAO.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "arqueiro" -> {
                                    dataPlayer.set(MISSAOARQUEIRO.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(ARQUEIRO.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "cacador" -> {
                                    dataPlayer.set(MISSAOCACADOR.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(CACADOR.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "tempestade" -> {
                                    dataPlayer.set(MISSAOTEMPESTADE.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(TEMPESTADE.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "mineiro" -> {
                                    dataPlayer.set(MISSAOMINEIRO.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(MINEIRO.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "fenix" -> {
                                    dataPlayer.set(MISSAOFENIX.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(FENIX.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "protetor" -> {
                                    dataPlayer.set(MISSAOPROTETOR.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(PROTETOR.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "hulk" -> {
                                    dataPlayer.set(MISSAOHULK.key, PersistentDataType.DOUBLE, 0d);
                                    dataPlayer.set(HULK.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "sculk" -> {
                                    dataPlayer.set(MISSAOSCULK.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(SCULK.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "pescador" -> {
                                    dataPlayer.set(MISSAOPESCADOR.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(PESCADOR.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "flash" -> {
                                    dataPlayer.set(MISSAOFLASH.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(FLASH.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "mago" -> {
                                    dataPlayer.set(MISSAOMAGO.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(MAGO.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "ladrao" -> {
                                    dataPlayer.set(MISSAOLADRAO.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(LADRAO.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                                case "domador" -> {
                                    dataPlayer.set(MISSAODOMADOR.key, PersistentDataType.INTEGER, 0);
                                    dataPlayer.set(DOMADOR.key,PersistentDataType.INTEGER,levelAtual+1);
                                }
                            }
                            player.getInventory().setItem(slot,nexusItem);
                            msg = "§aSeu Nexus do "+nome+" evoluiu para o nível " + (levelAtual + 1) + "!";
                        }
                    }else{
                        msg = "§cVocê precisa de mais "+(levelAtual*10-level)+" leveis XP para evoluir sua reliquia do "+nome+"!";
                    }
                }else{
                    if(level>=levelAtual*10){
                        msg = "§cVocê precisa "+condicao+" para evoluir sua reliquia do "+nome+"!";
                    }else{
                        msg = "§cVocê precisa de mais "+(levelAtual*10-level)+" leveis XP e "+condicao+" para evoluir sua reliquia do "+nome+"!";
                    }
                }
            }
            player.sendMessage(msg);
        }

    }
    private String podeEvoluir(Player player, String nome,int level){
        String condicao=null;
        switch (nome){
            case "barbaro" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao="derrotar mais "+(level-kills)+" monstros ou bosses";
                }
            }
            case "ceifador" -> {
                double recuperacaoN=10*level;
                double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, 0d);
                if(recuperacao<recuperacaoN){
                    int qtd = (int) ((recuperacaoN)-recuperacao);
                    condicao="roube mais "+qtd+" pontos de vida";
                }
            }
            case "fazendeiro" -> {
                int colheitasN = 5 * level;
                int colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, 0);
                if(colheitas<colheitasN){
                    int qtd = colheitasN-colheitas;
                    condicao="colha mais "+qtd+" plantações";
                }
            }
            case "guerreiro" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, 0);
                if(kills < killsN){
                    condicao="derrotar mais "+(killsN-kills)+" monstros ou bosses";
                }
            }
            case "mares" -> {
                int killsN = 10 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOMARES.key, PersistentDataType.INTEGER, 0);
                if(kills < killsN){
                    condicao="derrotar mais "+(killsN-kills)+" seres aquaticos, monstros ou bosses";
                }
            }
            case "vida" -> {
                double recuperacaoN=10*level;
                double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
                if(recuperacao<recuperacaoN){
                    int qtd = (int) ((recuperacaoN)-recuperacao);
                    condicao="recupere mais "+qtd+" pontos de vida";
                }
            }
            case "espiao" -> {
                int hab = player.getPersistentDataContainer().getOrDefault(MISSAOESPIAO.key, PersistentDataType.INTEGER, 0);
                if(hab < level){
                    condicao="use a habilidade mais "+(level-hab)+" vezes";
                }
            }
            case "arqueiro" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOARQUEIRO.key, PersistentDataType.INTEGER, 0);
                if(kills < killsN){
                    condicao="atingir mais "+(killsN-kills)+" monstros ou bosses";
                }
            }
            case "cacador" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOCACADOR.key, PersistentDataType.INTEGER, 0);
                if(kills < killsN){
                    condicao="atingir mais "+(killsN-kills)+" monstros ou bosses";
                }
            }
            case "tempestade" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOTEMPESTADE.key, PersistentDataType.INTEGER, 0);
                if(kills < killsN){
                    condicao="derrotar mais "+(killsN-kills)+" monstros ou bosses";
                }
            }
            case "mineiro" -> {
                int colheitasN = 5 * level;
                int colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOMINEIRO.key, PersistentDataType.INTEGER, 0);
                if(colheitas<colheitasN){
                    int qtd = colheitasN-colheitas;
                    condicao="quebre mais "+qtd+" minerios";
                }
            }
            case "fenix" -> {
                int colheitasN = 5 * level;
                int colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOFENIX.key, PersistentDataType.INTEGER, 0);
                if(colheitas<colheitasN){
                    int qtd = colheitasN-colheitas;
                    condicao="use mais "+qtd+" foguetes";
                }
            }
            case "protetor" -> {
                int colheitasN = 5 * level;
                int colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOPROTETOR.key, PersistentDataType.INTEGER, 0);
                if(colheitas<colheitasN){
                    int qtd = colheitasN-colheitas;
                    condicao="se proteja com o escudo mais "+qtd+" vezes";
                }
            }
            case "hulk" -> {
                double colheitasN = 20 * level;
                double colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOHULK.key, PersistentDataType.DOUBLE, 0d);
                if(colheitas>=colheitasN){
                    condicao="";
                }else{
                    int qtd = (int) (colheitasN-colheitas);
                    condicao="receba mais "+qtd+" de dano por monstros ou bosses";
                }
            }
            case "sculk" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOSCULK.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao="seja atacado mais "+(level-kills)+" vezes por um Warden";
                }
            }
            case "pescador" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOPESCADOR.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao="pesque mais "+(level-kills)+" vezes";
                }
            }
            case "flash" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOFLASH.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao="use a habilidade mais "+(level-kills)+" vezes";
                }
            }
            case "mago" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOMAGO.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao="beba mais "+(level-kills)+" poções";
                }
            }
            case "ladrao" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOLADRAO.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao="roube mais "+(level-kills)+" itens!";
                }
            }
            case "domador" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAODOMADOR.key, PersistentDataType.INTEGER, 0);
                if(kills < level){
                    condicao="dome mais "+(level-kills)+" animais/pets!";
                }
            }
        }
        return condicao;
    }
    @EventHandler
    public void pescar(FishHookStateChangeEvent event){
        if(event.getNewHookState().equals(FishHook.HookState.HOOKED_ENTITY)){
            FishHook vara = event.getEntity();
            UUID uuid = vara.getOwnerUniqueId();
            Entity e = vara.getHookedEntity();
            if(uuid!=null && e instanceof WaterMob){
                Player player = Bukkit.getPlayer(uuid);
                if(player!=null){
                    ItemStack stack = player.getInventory().getItemInMainHand();
                    if(stack.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
                        String nome = stack.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
                        if(nome!=null && nome.equals("pescador")){
                            Nexus n = ItemsRegistro.getFromNome(nome);
                            if(n!=null){
                                PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                                int peixes = dataPlayer.getOrDefault(MISSAOPESCADOR.key, PersistentDataType.INTEGER, 0);
                                int level = dataPlayer.getOrDefault(PESCADOR.key,PersistentDataType.INTEGER,1);
                                n.setLevel(level);
                                peixes++;
                                dataPlayer.set(MISSAOPESCADOR.key,PersistentDataType.INTEGER,peixes);
                                tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void esconder(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int cd = dataPlayer.getOrDefault(SPECIAL.key, PersistentDataType.INTEGER, 0);
        if (player.isSneaking() && cd <= 0) {
            ItemStack stack = player.getInventory().getHelmet();
            if(stack!=null){
                if (stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                    String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && !nome.isBlank()) {
                        Nexus item = ItemsRegistro.getFromNome(nome);
                        if(item!=null){
                            if(item.getNome().equals("espiao")){
                                int l=dataPlayer.getOrDefault(ESPIAO.key,PersistentDataType.INTEGER,1);
                                int usos=dataPlayer.getOrDefault(MISSAOESPIAO.key,PersistentDataType.INTEGER,0);
                                Location loc = player.getLocation();
                                usos++;
                                Component comp = player.playerListName();
                                Temporizador timer = new Temporizador(plugin,
                                        9+l,
                                        () -> {
                                            player.sendActionBar(Component.text("Habilidade do Nexus do Espião Ativado!"));
                                            player.setGameMode(GameMode.SPECTATOR);
                                            player.playerListName(Component.text(""));
                                        },
                                        () -> {
                                            player.setGameMode(GameMode.SURVIVAL);
                                            player.teleport(loc);
                                            player.playerListName(comp);
                                        },
                                        (t) -> player.sendActionBar(Component.text("Modo Fantasma acaba em "+t.getSegundosRestantes()+" segundos!"))
                                );
                                timer.scheduleTimer(20L);
                                dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,120+l+9);
                                dataPlayer.set(MISSAOESPIAO.key,PersistentDataType.INTEGER,usos);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }
            }
            stack = player.getInventory().getBoots();
            if(stack!=null){
                if (stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                    String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && !nome.isBlank()) {
                        Nexus item = ItemsRegistro.getFromNome(nome);
                        if(item!=null){
                            if(item.getNome().equals("flash")){
                                int l=dataPlayer.getOrDefault(FLASH.key,PersistentDataType.INTEGER,1);
                                int usos=dataPlayer.getOrDefault(MISSAOFLASH.key,PersistentDataType.INTEGER,0);
                                usos++;
                                Block bloco = player.getTargetBlockExact(10+l);
                                if(bloco==null)return;
                                Location loc = bloco.getLocation();
                                World w = player.getWorld();
                                Location loc1 = loc.add(0d,1d,0d);
                                Location loc2 = loc1.add(0d,1d,0d);
                                while(!w.getBlockAt(loc1).getType().equals(Material.AIR) && !w.getBlockAt(loc2).getType().equals(Material.AIR)){
                                    loc1 = loc1.add(0d,1d,0d);
                                    loc2 = loc2.add(0d,1d,0d);
                                }
                                player.teleport(loc);
                                dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,120);
                                dataPlayer.set(MISSAOFLASH.key,PersistentDataType.INTEGER,usos);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void recuperaVida(EntityRegainHealthEvent event){
        Entity e = event.getEntity();
        if(e instanceof Player player){
            ItemStack stack = player.getInventory().getItemInMainHand();
            if(stack.getType() != Material.TOTEM_OF_UNDYING){
                stack = player.getInventory().getItemInOffHand();
            }
            if(stack.getType() != Material.TOTEM_OF_UNDYING)return;
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome!=null && !nome.isBlank() && nome.equals("vida")){
                    Nexus n = ItemsRegistro.getFromNome(nome);
                    if(n!=null){
                        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                        double recuperacao = dataPlayer.getOrDefault(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
                        int level = dataPlayer.getOrDefault(VIDA.key,PersistentDataType.INTEGER,1);
                        n.setLevel(level);
                        recuperacao+=event.getAmount();
                        dataPlayer.set(MISSAOVIDA.key,PersistentDataType.DOUBLE,recuperacao);
                        tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                    }
                }
            }else{
                stack = player.getInventory().getItemInOffHand();
                meta = stack.getItemMeta();
                data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)) {
                    String nome = data.get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && !nome.isBlank() && nome.equals("vida")) {
                        Nexus n = ItemsRegistro.getFromNome(nome);
                        if (n != null) {
                            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                            double recuperacao = dataPlayer.getOrDefault(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
                            int level = dataPlayer.getOrDefault(VIDA.key, PersistentDataType.INTEGER, 1);
                            n.setLevel(level);
                            recuperacao += event.getAmount();
                            dataPlayer.set(MISSAOVIDA.key, PersistentDataType.DOUBLE, recuperacao);
                            tentarEvoluir(player, n.getItem(level), level, getSlotOfItem(player,stack));
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player killer)) return;
        ItemStack stack = killer.getInventory().getItemInMainHand();
        if(stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
            String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
            if(nome==null) return;
            PersistentDataContainer data = killer.getPersistentDataContainer();
            if(nome.equals("barbaro")){
                int kills = data.getOrDefault(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
                int level = data.getOrDefault(BARBARO.key, PersistentDataType.INTEGER, 1);
                if(event.getEntity() instanceof Monster || event.getEntity() instanceof Boss){
                    data.set(MISSAOBARBARO.key, PersistentDataType.INTEGER, kills + 1);
                    tentarEvoluir(killer,stack,level, getSlotOfItem(killer,stack));
                }
            }
            if(nome.equals("guerreiro")){
                int kills = data.getOrDefault(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, 0);
                int level = data.getOrDefault(GUERREIRO.key, PersistentDataType.INTEGER, 1);
                if(event.getEntity() instanceof Monster || event.getEntity() instanceof Boss){
                    data.set(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, kills + 1);
                    tentarEvoluir(killer,stack,level,getSlotOfItem(killer,stack));
                }
            }
            if(nome.equals("mares")){
                int kills = data.getOrDefault(MISSAOMARES.key, PersistentDataType.INTEGER, 0);
                int level = data.getOrDefault(MARES.key, PersistentDataType.INTEGER, 1);
                if(event.getEntity() instanceof WaterMob || event.getEntity() instanceof Boss || event.getEntity() instanceof Monster){
                    data.set(MISSAOMARES.key, PersistentDataType.INTEGER, kills + 1);
                    tentarEvoluir(killer,stack,level,getSlotOfItem(killer,stack));
                }
            }
            if(nome.equals("tempestade")){
                int kills = data.getOrDefault(MISSAOTEMPESTADE.key, PersistentDataType.INTEGER, 0);
                int level = data.getOrDefault(TEMPESTADE.key, PersistentDataType.INTEGER, 1);
                if(event.getEntity() instanceof Monster || event.getEntity() instanceof Boss){
                    data.set(MISSAOTEMPESTADE.key, PersistentDataType.INTEGER, kills + 1);
                    tentarEvoluir(killer,stack,level,getSlotOfItem(killer,stack));
                }
            }
        }
    }
    @EventHandler
    public void roubaVida(EntityDamageByEntityEvent event){
        Entity atacante = event.getDamager();
        Entity atacado = event.getEntity();
        if(atacante instanceof Player player){
            ItemStack stack = player.getInventory().getItemInMainHand();
            PersistentDataContainerView data = stack.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome==null || nome.isBlank())return;
                if(nome.equals("ceifador")){
                    Nexus n = ItemsRegistro.getFromNome(nome);
                    if(n!=null){
                        double dano = event.getDamage();
                        double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, 0d);
                        int level = player.getPersistentDataContainer().getOrDefault(CEIFADOR.key,PersistentDataType.INTEGER,1);
                        double cura = dano/2;
                        player.heal(cura);
                        recuperacao+=cura;
                        player.getPersistentDataContainer().set(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, recuperacao);
                        tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                    }
                }
                if(nome.equals("ladrao")){
                    if(atacado instanceof LivingEntity furto){
                        ItemStack roubar=null;
                        Random rd = new Random();
                        if(furto instanceof Player roubado){
                            PlayerInventory pinv = roubado.getInventory();
                            ItemStack p = pinv.getItemInOffHand();
                            if(p.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
                                String pnome = p.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
                                if(pnome!=null && pnome.equals("protetor")){
                                    player.sendMessage("Você não pode roubar de quem tem a reliquia do protetor!");
                                }else{
                                    int escolhido = rd.nextInt(0,pinv.getContents().length);
                                    roubar = pinv.getItem(escolhido);
                                    if(roubar!=null && !roubar.isEmpty()){
                                        boolean expurgo = ReliquiasNexus.getNexusConfig().getBoolean("expurgo");
                                        if(roubar.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
                                            if(expurgo){
                                                PersistentDataContainer container = player.getPersistentDataContainer();
                                                String rnome = roubar.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
                                                roubar.getItemMeta().getPersistentDataContainer().set(DONO.key,PersistentDataType.STRING,player.getUniqueId().toString());
                                                ReliquiasNexus.setConfigSave("nexus."+rnome,player.getUniqueId().toString());
                                                plugin.saveConfig();
                                                int qtd = container.getOrDefault(QTD.key, PersistentDataType.INTEGER,1);
                                                qtd++;
                                                container.set(QTD.key, PersistentDataType.INTEGER,qtd);
                                                pinv.setItem(escolhido,new ItemStack(Material.AIR));
                                                player.sendMessage("Você roubou uma reliquia!");
                                                LimitadorEvent.checkLimit(player);
                                            }else{
                                                stack=null;
                                                player.sendMessage("Você não pode roubar uma reliquia fora do expurgo!");
                                            }
                                        }else{
                                            player.sendMessage("Você roubou uma item!");
                                        }
                                    }else{
                                        player.sendMessage("Você não conseguiu roubar nada!");
                                    }
                                }
                            }else{
                                int escolhido = rd.nextInt(0,pinv.getContents().length);
                                roubar = pinv.getItem(escolhido);
                                if(roubar!=null && !roubar.isEmpty()){
                                    boolean expurgo = ReliquiasNexus.getNexusConfig().getBoolean("expurgo");
                                    if(roubar.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
                                        if(expurgo){
                                            PersistentDataContainer container = player.getPersistentDataContainer();
                                            String rnome = roubar.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
                                            roubar.getItemMeta().getPersistentDataContainer().set(DONO.key,PersistentDataType.STRING,player.getUniqueId().toString());
                                            ReliquiasNexus.setConfigSave("nexus."+rnome,player.getUniqueId().toString());
                                            plugin.saveConfig();
                                            int qtd = container.getOrDefault(QTD.key, PersistentDataType.INTEGER,1);
                                            qtd++;
                                            container.set(QTD.key, PersistentDataType.INTEGER,qtd);
                                            pinv.setItem(escolhido,new ItemStack(Material.AIR));
                                            player.sendMessage("Você roubou uma reliquia!");
                                            LimitadorEvent.checkLimit(player);
                                        }else{
                                            stack=null;
                                            player.sendMessage("Você não pode roubar uma reliquia fora do expurgo!");
                                        }
                                    }else{
                                        player.sendMessage("Você roubou uma item!");
                                    }
                                }else{
                                    player.sendMessage("Você não conseguiu roubar nada!");
                                }
                            }
                        }else{
                            EntityEquipment equipa = furto.getEquipment();
                            if (equipa != null) {
                                int escolhido = rd.nextInt(0,6);
                                EquipmentSlot slot = switch (escolhido){
                                    case 1 -> EquipmentSlot.OFF_HAND;
                                    case 2 -> EquipmentSlot.FEET;
                                    case 3 -> EquipmentSlot.LEGS;
                                    case 4 -> EquipmentSlot.CHEST;
                                    case 5 -> EquipmentSlot.HEAD;
                                    default -> EquipmentSlot.HAND;
                                };
                                roubar = equipa.getItem(slot);
                                if(!roubar.isEmpty()){
                                    player.sendMessage("Você roubou uma item!");
                                    equipa.setItem(slot,new ItemStack(Material.AIR));
                                }else{
                                    player.sendMessage("Você não conseguiu roubar nada!");
                                }
                            }
                        }
                        Nexus n = ItemsRegistro.getFromNome(nome);
                        if(n!=null && roubar!=null && !roubar.isEmpty() && stack!=null){
                            BundleMeta meta = (BundleMeta) stack.getItemMeta();
                            meta.addItem(roubar);
                            stack.setItemMeta(meta);
                            int qtd = player.getPersistentDataContainer().getOrDefault(MISSAOLADRAO.key, PersistentDataType.INTEGER, 0);
                            int level = player.getPersistentDataContainer().getOrDefault(LADRAO.key,PersistentDataType.INTEGER,1);
                            qtd++;
                            player.getPersistentDataContainer().set(MISSAOLADRAO.key, PersistentDataType.INTEGER, qtd);
                            tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                        }
                    }
                }
            }
        }
        if(atacado instanceof Player player){
            ItemStack stack = player.getInventory().getLeggings();
            PersistentDataContainerView data;
            if(stack!=null){
                data = stack.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    if(nome!=null && !nome.isBlank() && nome.equals("hulk")){
                        Nexus n = ItemsRegistro.getFromNome(nome);
                        if(n!=null && (atacante instanceof Boss || atacante instanceof Monster)){
                            double protecao = player.getPersistentDataContainer().getOrDefault(MISSAOHULK.key, PersistentDataType.DOUBLE, 0d);
                            int level = player.getPersistentDataContainer().getOrDefault(HULK.key,PersistentDataType.INTEGER,1);
                            protecao+=event.getDamage();
                            player.getPersistentDataContainer().set(MISSAOHULK.key, PersistentDataType.DOUBLE, protecao);
                            tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                        }
                    }
                }
            }
            if(player.isBlocking()){
                stack = player.getInventory().getItemInMainHand();
                if(stack.getType().equals(Material.SHIELD)){
                    data = stack.getPersistentDataContainer();
                    if(data.has(NEXUS.key,PersistentDataType.STRING)){
                        String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                        if(nome!=null && !nome.isBlank() && nome.equals("protetor")){
                            Nexus n = ItemsRegistro.getFromNome(nome);
                            if(n!=null){
                                int protecao = player.getPersistentDataContainer().getOrDefault(MISSAOPROTETOR.key, PersistentDataType.INTEGER, 0);
                                int level = player.getPersistentDataContainer().getOrDefault(PROTETOR.key,PersistentDataType.INTEGER,1);
                                protecao++;
                                player.getPersistentDataContainer().set(MISSAOPROTETOR.key, PersistentDataType.INTEGER, protecao);
                                tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }else{
                    stack = player.getInventory().getItemInOffHand();
                    if(stack.getType().equals(Material.SHIELD)){
                        data = stack.getPersistentDataContainer();
                        if(data.has(NEXUS.key,PersistentDataType.STRING)){
                            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                            if(nome!=null && !nome.isBlank() && nome.equals("protetor")){
                                Nexus n = ItemsRegistro.getFromNome(nome);
                                if(n!=null){
                                    int protecao = player.getPersistentDataContainer().getOrDefault(MISSAOPROTETOR.key, PersistentDataType.INTEGER, 0);
                                    int level = player.getPersistentDataContainer().getOrDefault(PROTETOR.key,PersistentDataType.INTEGER,1);
                                    protecao++;
                                    player.getPersistentDataContainer().set(MISSAOPROTETOR.key, PersistentDataType.INTEGER, protecao);
                                    tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                                }
                            }
                        }
                    }
                }
            }
            stack = player.getInventory().getItemInMainHand();
            data = stack.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome!=null && !nome.isBlank() && nome.equals("sculk")){
                    Nexus n = ItemsRegistro.getFromNome(nome);
                    if(n!=null && atacante instanceof Warden war){
                        int protecao = player.getPersistentDataContainer().getOrDefault(MISSAOSCULK.key, PersistentDataType.INTEGER, 0);
                        int level = player.getPersistentDataContainer().getOrDefault(SCULK.key,PersistentDataType.INTEGER,1);
                        protecao++;
                        event.setDamage(0);
                        war.setAnger(player,0);
                        player.getPersistentDataContainer().set(MISSAOSCULK.key, PersistentDataType.INTEGER, protecao);
                        tentarEvoluir(player,n.getItem(level),level,getSlotOfItem(player,stack));
                    }
                }
            }
        }
    }
    @EventHandler
    public void colher(BlockBreakEvent event){
        Block bloco = event.getBlock();
        Player player = event.getPlayer();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        ItemMeta meta = stack.getItemMeta();
        if(meta==null)return;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if(data.has(NEXUS.key,PersistentDataType.STRING)){
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank() && nome.equals("fazendeiro")){
                int level = dataPlayer.getOrDefault(FAZENDEIRO.key,PersistentDataType.INTEGER,1);
                if(bloco.getBlockData() instanceof Ageable ageable && ageable.getAge()==ageable.getMaximumAge()){
                    int colher = player.getPersistentDataContainer().getOrDefault(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, 0);
                    colher++;
                    dataPlayer.set(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, colher);
                    tentarEvoluir(player,stack,level,getSlotOfItem(player,stack));
                }
            }
            if(nome!=null && !nome.isBlank() && nome.equals("mineiro")){
                int level = dataPlayer.getOrDefault(MINEIRO.key,PersistentDataType.INTEGER,1);
                if(eMinerio(bloco.getBlockData().getMaterial())){
                    int colher = player.getPersistentDataContainer().getOrDefault(MISSAOMINEIRO.key, PersistentDataType.INTEGER, 0);
                    colher++;
                    dataPlayer.set(MISSAOMINEIRO.key, PersistentDataType.INTEGER, colher);
                    tentarEvoluir(player,stack,level,getSlotOfItem(player,stack));
                }
            }
        }
    }
    private boolean eMinerio(Material m){
        List<Material> mat = List.of(
                Material.COAL_ORE,
                Material.COPPER_ORE,
                Material.DIAMOND_ORE,
                Material.LAPIS_ORE,
                Material.GOLD_ORE,
                Material.EMERALD_ORE,
                Material.IRON_ORE,
                Material.REDSTONE_ORE,
                Material.DEEPSLATE_COAL_ORE,
                Material.DEEPSLATE_COPPER_ORE,
                Material.DEEPSLATE_DIAMOND_ORE,
                Material.DEEPSLATE_LAPIS_ORE,
                Material.DEEPSLATE_GOLD_ORE,
                Material.DEEPSLATE_EMERALD_ORE,
                Material.DEEPSLATE_IRON_ORE,
                Material.DEEPSLATE_REDSTONE_ORE,
                Material.ANCIENT_DEBRIS
        );
        return mat.contains(m);
    }
    @EventHandler
    public void atingiu(ProjectileHitEvent event){
        Projectile projetiu = event.getEntity();
        Entity entity = event.getHitEntity();
        if(entity instanceof Boss || entity instanceof Monster){
            if(projetiu instanceof Arrow flecha){
                UUID uuid = flecha.getOwnerUniqueId();
                if(uuid!=null && !flecha.getPersistentDataContainer().has(SPECIAL.key,PersistentDataType.STRING)){
                    Player player = Bukkit.getPlayer(uuid);
                    if(player!=null){
                        ItemStack stack = player.getInventory().getItemInMainHand();
                        ItemMeta meta = stack.getItemMeta();
                        PersistentDataContainer data = meta.getPersistentDataContainer();
                        if(data.has(NEXUS.key,PersistentDataType.STRING)){
                            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                            if(nome!=null && nome.equals("arqueiro")){
                                int q = player.getPersistentDataContainer().getOrDefault(MISSAOARQUEIRO.key,PersistentDataType.INTEGER,0);
                                q++;
                                player.getPersistentDataContainer().set(MISSAOARQUEIRO.key,PersistentDataType.INTEGER,q);
                                int l = player.getPersistentDataContainer().getOrDefault(ARQUEIRO.key,PersistentDataType.INTEGER,1);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                            if(nome!=null && nome.equals("cacador")){
                                int q = player.getPersistentDataContainer().getOrDefault(MISSAOCACADOR.key,PersistentDataType.INTEGER,0);
                                q++;
                                player.getPersistentDataContainer().set(MISSAOCACADOR.key,PersistentDataType.INTEGER,q);
                                int l = player.getPersistentDataContainer().getOrDefault(CACADOR.key,PersistentDataType.INTEGER,1);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void booster(PlayerElytraBoostEvent event){
        Player player = event.getPlayer();
        ItemStack peitoral = player.getInventory().getChestplate();
        if(peitoral!=null){
            ItemMeta meta = peitoral.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome!=null && nome.equals("fenix")){
                    int l = player.getPersistentDataContainer().getOrDefault(FENIX.key,PersistentDataType.INTEGER,1);
                    Random r = new Random();
                    int i = r.nextInt(0,100);
                    if(i>=100-l){
                        event.setShouldConsume(false);
                        Firework fr = event.getFirework();
                        event.getFirework().setTicksFlown(fr.getTicksFlown()+l);
                    }
                    int q = player.getPersistentDataContainer().getOrDefault(MISSAOFENIX.key,PersistentDataType.INTEGER,0);
                    q++;
                    player.getPersistentDataContainer().set(MISSAOFENIX.key,PersistentDataType.INTEGER,q);
                    tentarEvoluir(player,peitoral,l,getSlotOfItem(player,peitoral));
                }
            }
        }
    }
    public int getSlotOfItem(Player player, ItemStack targetItem) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].isSimilar(targetItem)) {
                return i;
            }
        }
        return -1;
    }
    @EventHandler
    public void correr(PlayerMoveEvent event){
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getBoots();
        if(stack!=null && player.isSprinting()){
            if (stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && !nome.isBlank()) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        if(item.getNome().equals("flash")){
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,40,0));
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void bebeu(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        ItemStack pocao = event.getItem();
        if(pocao.getType().equals(Material.POTION)){
            for (int i = 0; i <= 8; i++) {
                ItemStack stack = inv.getItem(i);
                if(stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                    String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && !nome.isBlank()) {
                        Nexus item = ItemsRegistro.getFromNome(nome);
                        if(item!=null){
                            if(item.getNome().equals("mago")){
                                int l=dataPlayer.getOrDefault(MAGO.key,PersistentDataType.INTEGER,1);
                                int usos=dataPlayer.getOrDefault(MISSAOMAGO.key,PersistentDataType.INTEGER,0);
                                usos++;
                                dataPlayer.set(MISSAOMAGO.key,PersistentDataType.INTEGER,usos);
                                tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void domar(EntityTameEvent event){
        UUID uuid = event.getOwner().getUniqueId();
        Player player = Bukkit.getPlayer(uuid);
        if(player==null)return;
        PlayerInventory inv = player.getInventory();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = inv.getItem(i);
            if(stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && !nome.isBlank()) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        if(item.getNome().equals("domador")){
                            int l=dataPlayer.getOrDefault(DOMADOR.key,PersistentDataType.INTEGER,1);
                            int usos=dataPlayer.getOrDefault(MISSAODOMADOR.key,PersistentDataType.INTEGER,0);
                            usos++;
                            dataPlayer.set(MISSAODOMADOR.key,PersistentDataType.INTEGER,usos);
                            tentarEvoluir(player,stack,l,getSlotOfItem(player,stack));
                        }
                    }
                }
            }
        }
    }
}