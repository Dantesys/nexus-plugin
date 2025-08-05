package org.dantesys.reliquiasNexus.eventos;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class EvoluirEvent implements Listener {
    public void tentarEvoluir(Player player, ItemStack nexusItem, int levelAtual,int slot) {
        ItemMeta meta = nexusItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if(data.has(NEXUS.key,PersistentDataType.STRING)){
            String msg = "";
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && !nome.isBlank()){
                String condicao = podeEvoluir(player,nome,levelAtual);
                int level = player.getLevel();
                if(condicao.isEmpty()){
                    if(level>=levelAtual*10){
                        player.setLevel(player.getLevel()-(10*levelAtual));
                        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                        switch (nome){
                            case "barbaro" -> {
                                dataPlayer.set(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
                                dataPlayer.set(BARBARO.key,PersistentDataType.INTEGER,levelAtual+1);
                            }
                            case "ceifador" -> {
                                dataPlayer.set(MISSAOCEIFADOR.key, PersistentDataType.INTEGER, 0);
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
                                dataPlayer.set(MISSAOVIDA.key, PersistentDataType.INTEGER, 0);
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
                        }
                        Nexus n = ItemsRegistro.getFromNome(nome);
                        if(n!=null){
                            nexusItem=n.getItem(levelAtual+1);
                            if(meta.hasEnchants()){
                                meta.getEnchants().forEach((nexusItem::addEnchantment));
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
        String condicao="Não";
        switch (nome){
            case "barbaro" -> {
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOBARBARO.key, PersistentDataType.INTEGER, 0);
                if(kills >= level){
                    condicao="";
                }else{
                    condicao="derrotar mais "+(level-kills)+" monstros ou bosses";
                }
            }
            case "ceifador" -> {
                double recuperacaoN=10*level;
                double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOCEIFADOR.key, PersistentDataType.DOUBLE, 0d);
                if(recuperacao>=recuperacaoN){
                    condicao="";
                }else{
                    int qtd = (int) ((recuperacaoN)-recuperacao);
                    condicao="roube mais "+qtd+" pontos de vida";
                }
            }
            case "fazendeiro" -> {
                int colheitasN = 5 * level;
                int colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOFAZENDEIRO.key, PersistentDataType.INTEGER, 0);
                if(colheitas>=colheitasN){
                    condicao="";
                }else{
                    int qtd = colheitasN-colheitas;
                    condicao="colha mais "+qtd+" plantações";
                }
            }
            case "guerreiro" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOGUERREIRO.key, PersistentDataType.INTEGER, 0);
                if(kills >= killsN){
                    condicao="";
                }else{
                    condicao="derrotar mais "+(killsN-kills)+" monstros ou bosses";
                }
            }
            case "mares" -> {
                int killsN = 10 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOMARES.key, PersistentDataType.INTEGER, 0);
                if(kills >= killsN){
                    condicao="";
                }else{
                    condicao="derrotar mais "+(killsN-kills)+" seres aquaticos, monstros ou bosses";
                }
            }
            case "vida" -> {
                double recuperacaoN=10*level;
                double recuperacao = player.getPersistentDataContainer().getOrDefault(MISSAOVIDA.key, PersistentDataType.DOUBLE, 0d);
                if(recuperacao>=recuperacaoN){
                    condicao="";
                }else{
                    int qtd = (int) ((recuperacaoN)-recuperacao);
                    condicao="recupere mais "+qtd+" pontos de vida";
                }
            }
            case "espiao" -> {
                int hab = player.getPersistentDataContainer().getOrDefault(MISSAOESPIAO.key, PersistentDataType.INTEGER, 0);
                if(hab >= level){
                    condicao="";
                }else{
                    condicao="use a habilidade mais "+(level-hab)+" vezes";
                }
            }
            case "arqueiro" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOARQUEIRO.key, PersistentDataType.INTEGER, 0);
                if(kills >= killsN){
                    condicao="";
                }else{
                    condicao="atingir mais "+(killsN-kills)+" monstros ou bosses";
                }
            }
            case "cacador" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOCACADOR.key, PersistentDataType.INTEGER, 0);
                if(kills >= killsN){
                    condicao="";
                }else{
                    condicao="atingir mais "+(killsN-kills)+" monstros ou bosses";
                }
            }
            case "tempestade" -> {
                int killsN = 5 * level;
                int kills = player.getPersistentDataContainer().getOrDefault(MISSAOTEMPESTADE.key, PersistentDataType.INTEGER, 0);
                if(kills >= killsN){
                    condicao="";
                }else{
                    condicao="derrotar mais "+(killsN-kills)+" monstros ou bosses";
                }
            }
            case "mineiro" -> {
                int colheitasN = 5 * level;
                int colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOMINEIRO.key, PersistentDataType.INTEGER, 0);
                if(colheitas>=colheitasN){
                    condicao="";
                }else{
                    int qtd = colheitasN-colheitas;
                    condicao="quebre mais "+qtd+" minerios";
                }
            }
            case "fenix" -> {
                int colheitasN = 5 * level;
                int colheitas = player.getPersistentDataContainer().getOrDefault(MISSAOFENIX.key, PersistentDataType.INTEGER, 0);
                if(colheitas>=colheitasN){
                    condicao="";
                }else{
                    int qtd = colheitasN-colheitas;
                    condicao="use mais "+qtd+" foguetes";
                }
            }
        }
        return condicao;
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
                                int usos=dataPlayer.getOrDefault(ESPIAO.key,PersistentDataType.INTEGER,0);
                                Location loc = player.getLocation();
                                usos++;
                                Component comp = player.playerListName();

                                Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class),
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
        if(atacante instanceof Player player){
            ItemStack stack = player.getInventory().getItemInMainHand();
            PersistentDataContainerView data = stack.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome!=null && !nome.isBlank() && nome.equals("ceifador")){
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
                    int i = r.nextInt(0,10);
                    if(i<=1){
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
    private int getSlotOfItem(Player player, ItemStack targetItem) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].isSimilar(targetItem)) {
                return i;
            }
        }
        return -1;
    }
}