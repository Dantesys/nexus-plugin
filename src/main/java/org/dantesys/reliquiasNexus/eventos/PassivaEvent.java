package org.dantesys.reliquiasNexus.eventos;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.SpeciaisPassivas.*;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.AlquimistaUtils;
import org.dantesys.reliquiasNexus.util.BlocoUtils;
import org.dantesys.reliquiasNexus.util.CoresUtils;
import org.dantesys.reliquiasNexus.util.EntityToEgg;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class PassivaEvent implements Listener {
    private static boolean inAssassino=false;
    private static UUID assassino=null;
    public static void setAssassino(UUID player){
        assassino=player;
        inAssassino=true;
    }
    public static void removerAssassino(){
        inAssassino=false;
    }
    @EventHandler
    public void reviver(EntityResurrectEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        player.getPersistentDataContainer().set(PROTECAO.key, PersistentDataType.STRING, "");
        PlayerInventory pinv = player.getInventory();
        ItemStack main = pinv.getItemInMainHand();
        ItemStack off  = pinv.getItemInOffHand();

        // pega os PDCs dos itens
        PersistentDataContainerView dataMain = main.getItemMeta() != null ? main.getItemMeta().getPersistentDataContainer() : null;
        PersistentDataContainerView dataOff  = off.getItemMeta() != null ? off.getItemMeta().getPersistentDataContainer() : null;

        // checa se um deles é o seu totem
        String nome = null;
        if (dataMain != null && dataMain.has(NEXUS.key, PersistentDataType.STRING)) {
            nome = dataMain.get(NEXUS.key, PersistentDataType.STRING);
        }
        if ((nome == null || nome.isBlank() || !nome.equals("vida")) && dataOff != null) {
            nome = dataOff.get(NEXUS.key, PersistentDataType.STRING);
        }
        if (nome == null || !nome.equals("vida")) return;

        Nexus n = ItemsRegistro.getFromNome(nome);
        if (n == null) return;

        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int countDown = dataPlayer.getOrDefault(TOTEM.key, PersistentDataType.INTEGER, 0);

        if (countDown > 0) {
            e.setCancelled(true); // em cooldown
            return;
        }

        // seta cooldown
        int tempo = 120;
        dataPlayer.set(TOTEM.key, PersistentDataType.INTEGER, tempo);

        // CLONA antes que o Minecraft consuma
        final ItemStack restoreMain = main.clone();
        final ItemStack restoreOff  = off.clone();

        Bukkit.getScheduler().runTaskLater(ReliquiasNexus.getPlugin(ReliquiasNexus.class), () -> {
            // devolve exatamente o que foi clonado
            player.getInventory().setItemInMainHand(restoreMain);
            player.getInventory().setItemInOffHand(restoreOff);
            player.updateInventory(); // garante atualização visual
        }, 1L);
    }

    @EventHandler
    public void tick(ServerTickEndEvent event){
        int tick = event.getTickNumber();
        if(tick%20==0){
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                PersistentDataContainer conteiner = player.getPersistentDataContainer();
                LimitadorEvent.checkLimit(player);
                if(conteiner.has(SPECIAL.key,PersistentDataType.INTEGER)){
                    int tempo = conteiner.getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,0);
                    if(tempo>0){
                        tempo--;
                        conteiner.set(SPECIAL.key,PersistentDataType.INTEGER,tempo);
                        player.sendActionBar(Component.text("Special "+tempo+"s"));
                    }
                }else{
                    conteiner.set(SPECIAL.key, PersistentDataType.INTEGER,0);
                }
                Integer tempoCD = conteiner.get(MISSAOCOOLDOWN.key, PersistentDataType.INTEGER);
                if (tempoCD != null) {
                    if (tempoCD > 0) {
                        conteiner.set(MISSAOCOOLDOWN.key, PersistentDataType.INTEGER, tempoCD - 1);
                    } else {
                        conteiner.set(MISSAOCOOLDOWN.key, PersistentDataType.INTEGER, 0);
                    }
                } else {
                    conteiner.set(MISSAOCOOLDOWN.key, PersistentDataType.INTEGER, 0);
                }
                Integer tpaCD = conteiner.get(TPACOOLDOWN.key, PersistentDataType.INTEGER);
                if (tpaCD != null) {
                    if (tpaCD > 0) {
                        conteiner.set(TPACOOLDOWN.key, PersistentDataType.INTEGER, tpaCD - 1);
                    } else {
                        conteiner.set(TPACOOLDOWN.key, PersistentDataType.INTEGER, 0);
                    }
                } else {
                    conteiner.set(TPACOOLDOWN.key, PersistentDataType.INTEGER, 0);
                }
                if(player.getPersistentDataContainer().has(RUGIDO.key,PersistentDataType.INTEGER)){
                    int rg = player.getPersistentDataContainer().getOrDefault(RUGIDO.key,PersistentDataType.INTEGER,0);
                    if(rg>0){
                        player.getPersistentDataContainer().set(RUGIDO.key,PersistentDataType.INTEGER,rg-1);
                    }
                }
                if(conteiner.has(DRENO.key,PersistentDataType.INTEGER)){
                    int tempo = conteiner.getOrDefault(DRENO.key,PersistentDataType.INTEGER,0);
                    if(tempo>0){
                        tempo--;
                        conteiner.set(DRENO.key,PersistentDataType.INTEGER,tempo);
                        player.sendActionBar(Component.text("♡ "+tempo+"s"));
                    }
                }
                if(conteiner.has(TOTEM.key,PersistentDataType.INTEGER)){
                    int tempo = conteiner.getOrDefault(TOTEM.key,PersistentDataType.INTEGER,0);
                    if(tempo>0){
                        tempo--;
                        conteiner.set(TOTEM.key,PersistentDataType.INTEGER,tempo);
                        player.sendActionBar(Component.text("♡ "+tempo+"s"));
                    }
                }
                if(conteiner.has(RENASCER.key,PersistentDataType.INTEGER)){
                    int tempo = conteiner.getOrDefault(RENASCER.key,PersistentDataType.INTEGER,0);
                    if(tempo>0){
                        tempo--;
                        conteiner.set(RENASCER.key,PersistentDataType.INTEGER,tempo);
                        player.sendActionBar(Component.text("\uD83D\uDC26\u200D\uD83D\uDD25 "+tempo+"s"));
                    }else{
                        player.getAttribute(Attribute.SCALE).setBaseValue(1);
                    }
                }

                PlayerInventory pinv = player.getInventory();
                pinv.forEach(stack -> {
                    if(stack!=null){
                        PersistentDataContainerView data = stack.getPersistentDataContainer();
                        aplicaEfeito(data,player);
                    }
                });
            });
        }
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if(assassino!=null){
                Player p = Bukkit.getPlayer(assassino);
                if(inAssassino && !temReliquia(player,"espiao")){
                    if(p!=null){
                        player.hidePlayer(ReliquiasNexus.getPlugin(ReliquiasNexus.class),p);
                    }
                }else{
                    if(p!=null){
                        player.showPlayer(ReliquiasNexus.getPlugin(ReliquiasNexus.class),p);
                    }
                }
            }
            PlayerInventory inv = player.getInventory();
            for (int i = 0; i <= 8; i++) {
                ItemStack stack = inv.getItem(i);
                if(stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                    String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                    if (nome != null && nome.equals("mago")) {
                        ItemMeta meta = stack.getItemMeta();
                        NamespacedKey key = new NamespacedKey("nexus_texture","mago_base");
                        int level = player.getPersistentDataContainer().getOrDefault(MAGO.key,PersistentDataType.INTEGER,1);
                        switch (i){
                            case 0 -> key = new NamespacedKey("nexus_texture","mago_vm1");
                            case 1 -> key = new NamespacedKey("nexus_texture","mago_vm2");
                            case 2 -> key = new NamespacedKey("nexus_texture","mago_vm3");
                            case 3 -> key = level>5?new NamespacedKey("nexus_texture","mago_vm4"):new NamespacedKey("nexus_texture","mago_vm10");
                            case 4 -> key = level>5?new NamespacedKey("nexus_texture","mago_vm5"):new NamespacedKey("nexus_texture","mago_vm10");
                            case 5 -> key = level>10?new NamespacedKey("nexus_texture","mago_vm6"):new NamespacedKey("nexus_texture","mago_vm10");
                            case 6 -> key = level>10?new NamespacedKey("nexus_texture","mago_vm7"):new NamespacedKey("nexus_texture","mago_vm10");
                            case 7 -> key = level>15?new NamespacedKey("nexus_texture","mago_vm8"):new NamespacedKey("nexus_texture","mago_vm10");
                            case 8 -> key = level>15?new NamespacedKey("nexus_texture","mago_vm9"):new NamespacedKey("nexus_texture","mago_vm10");
                        }
                        meta.setItemModel(key);
                        stack.setItemMeta(meta);
                        break;
                    }
                    if (nome != null && nome.equals("construtor")) {
                        ItemStack corante = player.getInventory().getItemInOffHand();
                        DyeColor cor = CoresUtils.getDyeColorFromItem(corante);
                        ItemMeta meta = stack.getItemMeta();
                        NamespacedKey key = new NamespacedKey("nexus_texture","construtor");
                        switch (i){
                            case 0 -> key = BlocoUtils.getBlocoColorido(Material.WHITE_CONCRETE,cor).getKey();
                            case 1 -> key = BlocoUtils.getBlocoColorido(Material.GLASS,cor).getKey();
                            case 2 -> key = BlocoUtils.getBlocoColorido(Material.GLASS_PANE,cor).getKey();
                            case 3 -> key = BlocoUtils.getBlocoColorido(Material.TERRACOTTA,cor).getKey();
                            case 4 -> key = BlocoUtils.getBlocoColorido(Material.WHITE_GLAZED_TERRACOTTA,cor).getKey();
                            case 5 -> key = BlocoUtils.getBlocoColorido(Material.WHITE_WOOL,cor).getKey();
                        }
                        meta.setItemModel(key);
                        stack.setItemMeta(meta);
                        break;
                    }
                }
            }
        });
    }
    @EventHandler
    public void ataqueMobs(EntityDamageByEntityEvent event){
        Entity entity = event.getDamager();
        Entity atacado = event.getEntity();
        if(entity instanceof Player player){
            if(temReliquia(player,"assassino") && event.isCritical()){
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,60,0));
                Entity entidade = event.getEntity();
                if(entidade instanceof LivingEntity vivo){
                    PotionEffect efeito = vivo.getPotionEffect(PotionEffectType.POISON);
                    int amplificador = 0;
                    if(efeito!=null){
                        amplificador=efeito.getAmplifier()+1;
                    }
                    vivo.addPotionEffect(new PotionEffect(PotionEffectType.POISON,100,amplificador));
                }
            }
            if(temReliquia(player,"alquimista")){
                ItemStack stack = player.getInventory().getItemInMainHand();
                if(stack.getPersistentDataContainer().getOrDefault(NEXUS.key,PersistentDataType.STRING,"").equals("alquimista") && event.getEntity() instanceof LivingEntity vivo){
                    Material mat = EntityToEgg.getEntityEgg(vivo.getType());
                    if(mat!=null){
                        vivo.getWorld().dropItemNaturally(vivo.getLocation(),new ItemStack(mat));
                        vivo.remove();
                    }
                }
            }
        }
        if(atacado instanceof Player player){
            if(temReliquia(player,"dragao")){
                if(entity instanceof LivingEntity vivo && vivo.getType()==EntityType.ENDER_DRAGON){
                    player.heal(20);
                    event.setCancelled(true);
                }
            }
            if(temReliquia(player,"golem")){
                if(entity instanceof LivingEntity vivo && vivo.getType()==EntityType.IRON_GOLEM){
                    player.heal(20);
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void recuperacaoFenix(EntityDamageEvent event){
        Entity e = event.getEntity();
        if(e instanceof Player player){
            if(temReliquia(player,"fenix")){
                if(event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)){
                    player.heal(2d);
                    event.setCancelled(true);
                }
            }
            if(temReliquia(player,"cronosombra")){
                for(Entity ent:player.getNearbyEntities(5,5,5)){
                    if(ent instanceof LivingEntity vivo && vivo!=player){
                        vivo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,200,1));
                    }
                }
            }
            if(temReliquia(player,"abissal")){
                int chance = ThreadLocalRandom.current().nextInt(1,101);
                if(chance>90){
                    player.heal(event.getDamage()*2);
                    event.setDamage(0d);
                }else if(chance>80){
                    player.heal(event.getDamage());
                    event.setDamage(0d);
                }else if(chance>70){
                    player.heal(event.getDamage()/2);
                    event.setDamage(0d);
                }else if(chance>60){
                    event.setDamage(0d);
                }else if(chance>50){
                    event.setDamage(event.getDamage()/4);
                }else if(chance>40){
                    event.setDamage(event.getDamage()/2);
                }
            }
            if(temReliquia(player,"frostis")){
                if(event.getCause().equals(EntityDamageEvent.DamageCause.FREEZE)){
                    player.heal(1);
                    event.setCancelled(true);
                }
            }
            if(temReliquia(player,"dragao")){
                DamageCause cause = event.getCause();
                List<DamageCause> causes = List.of(DamageCause.CAMPFIRE,DamageCause.FIRE,DamageCause.FIRE_TICK,DamageCause.HOT_FLOOR,
                        DamageCause.LAVA,DamageCause.VOID,DamageCause.WORLD_BORDER,DamageCause.CONTACT,DamageCause.MAGIC,DamageCause.DRAGON_BREATH);
                if(causes.contains(cause)){
                    player.heal(20);
                    event.setCancelled(true);
                }
            }
        }
    }
    private boolean temReliquia(Player player,String nome) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .anyMatch(item -> item.getPersistentDataContainer().getOrDefault(NEXUS.key,PersistentDataType.STRING,"").equals(nome));
    }
    private void aplicaEfeito(PersistentDataContainerView data, Player player){
        if(data.has(NEXUS.key,PersistentDataType.STRING)){
            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
            Nexus nexus = ItemsRegistro.getFromNome(nome!=null?nome:"");
            if(nexus!=null && nome!=null){
                switch(nome){
                    case "guerreiro" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(GUERREIRO.key,PersistentDataType.INTEGER,1);
                        Guerreiro.getPassivabyLevel(level,player);
                    }
                    case "ceifador" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(CEIFADOR.key,PersistentDataType.INTEGER,1);
                        Ceifador.getPassivabyLevel(level,player);
                    }
                    case "vida" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(VIDA.key,PersistentDataType.INTEGER,1);
                        Vida.getPassivabyLevel(level,player);
                    }
                    case "mares" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(MARES.key,PersistentDataType.INTEGER,1);
                        Mares.getPassivabyLevel(level,player);
                    }
                    case "barbaro" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(BARBARO.key,PersistentDataType.INTEGER,1);
                        Barbaro.getPassivabyLevel(level,player);
                    }
                    case "fazendeiro" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(FAZENDEIRO.key,PersistentDataType.INTEGER,1);
                        Fazendeiro.getPassivabyLevel(level,player);
                    }
                    case "espiao" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(ESPIAO.key,PersistentDataType.INTEGER,1);
                        Espiao.getPassivabyLevel(level,player);
                    }
                    case "arqueiro" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(ARQUEIRO.key,PersistentDataType.INTEGER,1);
                        Arqueiro.getPassivabyLevel(level,player);
                    }
                    case "cacador" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(CACADOR.key,PersistentDataType.INTEGER,1);
                        Cacador.getPassivabyLevel(level,player);
                    }
                    case "tempestade" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(TEMPESTADE.key,PersistentDataType.INTEGER,1);
                        Tempestade.getPassivabyLevel(level,player);
                    }
                    case "mineiro" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(MINEIRO.key,PersistentDataType.INTEGER,1);
                        Mineiro.getPassivabyLevel(level,player);
                    }
                    case "fenix" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(FENIX.key,PersistentDataType.INTEGER,1);
                        Fenix.getPassivabyLevel(level,player);
                        if(player.isInLava() || player.getFireTicks()>0){
                            player.heal(1d);
                        }
                    }
                    case "hulk" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(HULK.key,PersistentDataType.INTEGER,1);
                        Hulk.getPassivabyLevel(level,player);
                    }
                    case "sculk" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(SCULK.key,PersistentDataType.INTEGER,1);
                        Sculk.getPassivabyLevel(level,player);
                    }
                    case "pescador" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(PESCADOR.key,PersistentDataType.INTEGER,1);
                        Pescador.getPassivabyLevel(level,player);
                    }
                    case "flash" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(FLASH.key,PersistentDataType.INTEGER,1);
                        Flash.getPassivabyLevel(level,player);
                    }
                    case "mago" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(MAGO.key,PersistentDataType.INTEGER,1);
                        Mago.getPassivabyLevel(level,player);
                    }
                    case "ladrao" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(LADRAO.key,PersistentDataType.INTEGER,1);
                        Ladrao.getPassivabyLevel(level,player);
                    }
                    case "frostis" -> {
                        if(player.hasPotionEffect(PotionEffectType.SLOWNESS)){
                            player.removePotionEffect(PotionEffectType.SLOWNESS);
                            player.heal(20);
                        }
                    }
                    case "necromante" -> {
                        if(player.hasPotionEffect(PotionEffectType.WITHER)){
                            player.removePotionEffect(PotionEffectType.WITHER);
                            player.heal(20);
                        }
                    }
                    case "alquimista" -> swapPotion(player);
                    case "dragao" -> {
                        int level = player.getPersistentDataContainer().getOrDefault(DRAGAO.key,PersistentDataType.INTEGER,1);
                        if(level>5){
                            player.setAllowFlight(true);
                        }
                        double max = player.getAttribute(Attribute.MAX_HEALTH)!=null?player.getAttribute(Attribute.MAX_HEALTH).getValue():20;
                        if(player.getHealth()<=max*0.25){
                            if(player.getPersistentDataContainer().has(RUGIDO.key,PersistentDataType.INTEGER)){
                                int rg = player.getPersistentDataContainer().getOrDefault(RUGIDO.key,PersistentDataType.INTEGER,0);
                                if(rg<=0){
                                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f, 1.0f);
                                    player.getWorld().spawnParticle(Particle.DRAGON_BREATH, player.getLocation(), 200, 3, 1, 3, 0.1);
                                    for (Entity e : player.getNearbyEntities(10, 10, 10)) {
                                        if (e instanceof LivingEntity && e != player) {
                                            Vector knockback = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.5);
                                            e.setVelocity(knockback);
                                            ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                                            ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
                                        }
                                    }
                                    player.getPersistentDataContainer().set(RUGIDO.key,PersistentDataType.INTEGER,120);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private void swapPotion(Player player){
        player.getActivePotionEffects().forEach(potion -> {
            int duracao = potion.getDuration();
            int amplificador = potion.getAmplifier();
            PotionEffectType type = AlquimistaUtils.getPositiveEffect(potion.getType());
            player.removePotionEffect(potion.getType());
            player.addPotionEffect(new PotionEffect(type,duracao,amplificador));
        });
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInMainHand();
        // Checa se é a relíquia do Construtor
        if (stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
            String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
            if (nome != null && nome.equals("construtor")) {
                int slot = player.getInventory().getHeldItemSlot();
                // Define slots que podem colocar blocos (ex: 0 a 6)
                if (slot < 0 || slot >= 6) return;
                // Bloco base que o jogador quer colocar (pode ser definido por slot ou item)
                ItemStack corante = player.getInventory().getItemInOffHand();
                DyeColor cor = CoresUtils.getDyeColorFromItem(corante);
                Material bloco = switch(slot){
                    case 1 -> BlocoUtils.getBlocoColorido(Material.GLASS,cor);
                    case 2 -> BlocoUtils.getBlocoColorido(Material.GLASS_PANE,cor);
                    case 3 -> BlocoUtils.getBlocoColorido(Material.TERRACOTTA,cor);
                    case 4 -> BlocoUtils.getBlocoColorido(Material.WHITE_GLAZED_TERRACOTTA,cor);
                    case 5 -> BlocoUtils.getBlocoColorido(Material.WHITE_WOOL,cor);
                    default -> BlocoUtils.getBlocoColorido(Material.WHITE_CONCRETE,cor);
                };

                // Local alvo (ponto que o jogador está olhando)
                Block alvo = event.getClickedBlock();
                if(alvo==null){
                    alvo=player.getTargetBlockExact(1);
                }
                if(alvo==null)return;
                Block lugar = alvo.getRelative(event.getBlockFace());
                ItemStack blocoItem = new ItemStack(bloco); // bloco é o Material escolhido

                BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(
                        lugar,                  // bloco que será colocado
                        lugar.getState(),        // estado atual
                        lugar,                   // bloco colocado “do ponto de vista do jogador”
                        blocoItem,               // item usado
                        player,                  // jogador
                        true,                    // canBuild
                        player.getActiveItemHand()         // mão usada
                );

                Bukkit.getPluginManager().callEvent(blockPlaceEvent);

                if (!blockPlaceEvent.isCancelled()) {
                    lugar.setType(bloco); // só coloca se o evento não foi cancelado
                }

                // Cancela o evento para não quebrar blocos se for clique direito
                event.setCancelled(true);
            }
        }
    }
    private boolean isNecromanteSlave(LivingEntity mob) {
        String val = mob.getPersistentDataContainer().getOrDefault(SLAVE.key, PersistentDataType.STRING, "");
        return val.equals("necromante");
    }
    private boolean eOsso(Entity entity){
        if (entity == null) return false;

        EntityType type = entity.getType();

        return type == EntityType.SKELETON
                || type == EntityType.STRAY
                || type == EntityType.WITHER_SKELETON
                || type == EntityType.SKELETON_HORSE
                || type == EntityType.BOGGED
                || type == EntityType.WITHER;
    }
    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if(eOsso(e.getEntity())){
            if (e.getTarget() instanceof Player p) {
                if (temReliquia(p,"necromante")) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        if (!(e.getEntity() instanceof LivingEntity mob)) return;
        if (!isNecromanteSlave(mob)) return;

        // Não atacar o jogador dono
        if (e.getTarget() instanceof Player p) {
            if (temReliquia(p,"necromante")) {
                e.setCancelled(true);
                return;
            }
        }

        // Se não tiver alvo, escolher um inimigo próximo
        if (e.getTarget() == null) {
            mob.getLocation().getWorld()
                    .getNearbyEntities(mob.getLocation(), 10, 10, 10).stream()
                    .filter(ent -> ent instanceof Monster && !eOsso(ent)) // evita atacar outros esqueletos
                    .map(ent -> (LivingEntity) ent)
                    .findAny().ifPresent(e::setTarget);

        }
        if(e.getEntity().getType() == EntityType.ENDER_DRAGON){
            if(e.getTarget() instanceof Player player && temReliquia(player,"dragao")){
                e.setCancelled(true);
            }
        }
        if(e.getEntity().getType() == EntityType.IRON_GOLEM){
            if(e.getTarget() instanceof Player player && temReliquia(player,"golem")){
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPotionConsume(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        if(temReliquia(player,"alquimista")){
            ItemStack item = event.getItem();
            if(item.getType().equals(Material.POTION)){
                if(ThreadLocalRandom.current().nextInt(100) < 10){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*5, 0));
                }
            }
        }
    }
}