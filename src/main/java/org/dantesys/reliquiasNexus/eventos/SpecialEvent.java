package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.SpeciaisPassivas.*;
import org.dantesys.reliquiasNexus.SpeciaisPassivas.Golem;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.EntityToEgg;

import java.util.*;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class SpecialEvent implements Listener {
    private final ReliquiasNexus plugin;

    public SpecialEvent(ReliquiasNexus plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void special(PlayerInteractEvent event){
        Player player = event.getPlayer();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int cd = dataPlayer.getOrDefault(SPECIAL.key, PersistentDataType.INTEGER,0);

        if(player.isSneaking() && cd<=0){
            ItemStack stack = player.getInventory().getItemInMainHand();

            if (stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && !nome.isBlank()) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        switch (item.getNome()) {
                            case "barbaro" -> Barbaro.getSpecialbyLevel(dataPlayer.getOrDefault(BARBARO.key, PersistentDataType.INTEGER, 1), player);
                            case "ceifador" -> Ceifador.getSpecialbyLevel(dataPlayer.getOrDefault(CEIFADOR.key, PersistentDataType.INTEGER, 1), player);
                            case "fazendeiro" -> Fazendeiro.getSpecialbyLevel(dataPlayer.getOrDefault(FAZENDEIRO.key, PersistentDataType.INTEGER, 1), player);
                            case "guerreiro" -> Guerreiro.getSpecialbyLevel(dataPlayer.getOrDefault(GUERREIRO.key, PersistentDataType.INTEGER, 1), player);
                            case "vida" -> Vida.getSpecialbyLevel(dataPlayer.getOrDefault(VIDA.key, PersistentDataType.INTEGER, 1), player);
                            case "mares" -> Mares.getSpecialbyLevel(dataPlayer.getOrDefault(MARES.key, PersistentDataType.INTEGER, 1), player);
                            case "arqueiro" -> {
                                Arqueiro.getSpecialbyLevel(dataPlayer.getOrDefault(ARQUEIRO.key, PersistentDataType.INTEGER, 1), player);
                                event.setCancelled(true);
                            }
                            case "cacador" -> {
                                Cacador.getSpecialbyLevel(dataPlayer.getOrDefault(CACADOR.key, PersistentDataType.INTEGER, 1), player);
                                event.setCancelled(true);
                            }
                            case "tempestade" -> Tempestade.getSpecialbyLevel(dataPlayer.getOrDefault(TEMPESTADE.key, PersistentDataType.INTEGER, 1), player);
                            case "mineiro" -> Mineiro.getSpecialbyLevel(dataPlayer.getOrDefault(MINEIRO.key, PersistentDataType.INTEGER, 1), player);
                            case "sculk" -> Sculk.getSpecialbyLevel(dataPlayer.getOrDefault(SCULK.key, PersistentDataType.INTEGER, 1), player);
                            case "protetor" -> Protetor.getSpecialbyLevel(dataPlayer.getOrDefault(PROTETOR.key, PersistentDataType.INTEGER, 1), player);
                            case "pescador" -> Pescador.getSpecialbyLevel(dataPlayer.getOrDefault(PESCADOR.key, PersistentDataType.INTEGER, 1), player);
                            case "ladrao" -> {
                                Ladrao.getSpecialbyLevel(dataPlayer.getOrDefault(LADRAO.key, PersistentDataType.INTEGER, 1), player);
                                event.setCancelled(true);
                            }
                            case "domador" -> Domador.getSpecialbyLevel(dataPlayer.getOrDefault(DOMADOR.key, PersistentDataType.INTEGER, 1), player);
                            case "mago" -> {
                                mago(player);
                                event.setCancelled(true);
                            }
                            case "cozinheiro" -> Cozinheiro.getSpecialbyLevel(dataPlayer.getOrDefault(COZINHEIRO.key, PersistentDataType.INTEGER, 1), player);
                            case "construtor" -> Construtor.getSpecialbyLevel(dataPlayer.getOrDefault(CONSTRUTOR.key, PersistentDataType.INTEGER, 1), player);
                            case "abissal" -> Abissal.getSpecialbyLevel(dataPlayer.getOrDefault(ABISSAL.key, PersistentDataType.INTEGER, 1), player);
                            case "assassino" -> Assassino.getSpecialbyLevel(dataPlayer.getOrDefault(ASSASSINO.key, PersistentDataType.INTEGER, 1), player);
                            case "frostis" -> Frostis.getSpecialbyLevel(dataPlayer.getOrDefault(FROSTIS.key, PersistentDataType.INTEGER, 1), player);
                            case "necromante" -> Necromante.getSpecialbyLevel(dataPlayer.getOrDefault(NECROMANTE.key, PersistentDataType.INTEGER, 1), player);
                            case "alquimista" -> Alquimista.getSpecialbyLevel(dataPlayer.getOrDefault(ALQUIMISTA.key, PersistentDataType.INTEGER, 1), player);
                            case "golem" -> Golem.getSpecialbyLevel(dataPlayer.getOrDefault(GOLEM.key, PersistentDataType.INTEGER, 1), player);
                            case "dragao" -> Dragao.getSpecialbyLevel(dataPlayer.getOrDefault(DRAGAO.key, PersistentDataType.INTEGER, 1), player);
                        }
                        if(!item.getNome().equals("mago")){
                            dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,60);
                        }
                    }
                }
            }
            stack = player.getInventory().getChestplate();
            if (stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && nome.equals("fenix")) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        Fenix.getSpecialbyLevel(dataPlayer.getOrDefault(FENIX.key, PersistentDataType.INTEGER, 1), player);
                        dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,60);
                    }
                }
                if (nome != null && nome.equals("golem")) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        Golem.getSpecialbyLevel(dataPlayer.getOrDefault(GOLEM.key, PersistentDataType.INTEGER, 1), player);
                        dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,60);
                    }
                }
            }
            stack = player.getInventory().getItemInOffHand();
            if (stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if(nome!=null){
                    if (nome.equals("protetor")) {
                        Nexus item = ItemsRegistro.getFromNome(nome);
                        if(item!=null){
                            Protetor.getSpecialbyLevel(dataPlayer.getOrDefault(PROTETOR.key, PersistentDataType.INTEGER, 1), player);
                            dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,60);
                        }
                    }else if(nome.equals("vida")){
                        Vida.getSpecialbyLevel(dataPlayer.getOrDefault(VIDA.key, PersistentDataType.INTEGER, 1), player);
                    }
                }
            }
            stack = player.getInventory().getLeggings();
            if (stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && nome.equals("hulk")) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        Hulk.getSpecialbyLevel(dataPlayer.getOrDefault(HULK.key, PersistentDataType.INTEGER, 1), player);
                        dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,60);
                    }
                }
            }
            stack = player.getInventory().getHelmet();
            if (stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && nome.equals("dragao")) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        Dragao.getSpecialbyLevel(dataPlayer.getOrDefault(DRAGAO.key, PersistentDataType.INTEGER, 1), player);
                        dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,60);
                    }
                }
            }
        }
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(stack.getPersistentDataContainer().has(DONO.key, PersistentDataType.STRING) && stack.getType()==Material.WRITTEN_BOOK){
            String d = stack.getPersistentDataContainer().get(DONO.key, PersistentDataType.STRING);
            if(d!=null && d.equals("nexus")){
                BookMeta meta = (BookMeta) stack.getItemMeta();
                meta.setGeneration(BookMeta.Generation.ORIGINAL);
                meta.pages(Collections.emptyList());
                String msg = ReliquiasNexus.getLang().getString("livro.base");
                if(msg==null){
                    msg="Todas as Reliquias precisam de Xp para evoluir.\nAs que possuem Special Manual para ativar tem que está agachado";
                }
                msg=msg.replace("<break>","\n");
                meta.addPages(Component.text(msg));
                ConfigurationSection secao = ReliquiasNexus.getNexusConfig().getConfigurationSection("nexus");
                if(secao!=null){
                    for(String nexus: secao.getKeys(false)){
                        String uuidStr = ReliquiasNexus.getNexusConfig().getString("nexus."+nexus);
                        if(uuidStr != null && uuidStr.equals(player.getUniqueId().toString())){
                            meta.addPages(Component.text(getDesc(nexus)));
                        }
                    }
                }
                stack.setItemMeta(meta);
            }
        }
        if(stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
            String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
            if (nome != null && nome.equals("mineiro")) {
                Block bloco = event.getClickedBlock();
                if(bloco!=null){
                    if(bloco.getType()==Material.BEDROCK){
                        player.getWorld().dropItemNaturally(bloco.getLocation(),new ItemStack(Material.BEDROCK));
                        bloco.setType(Material.AIR);
                    }
                    if(bloco.getType()==Material.REINFORCED_DEEPSLATE){
                        player.getWorld().dropItemNaturally(bloco.getLocation(),new ItemStack(Material.REINFORCED_DEEPSLATE));
                        bloco.setType(Material.AIR);
                    }
                    if(bloco.getType()==Material.END_PORTAL_FRAME){
                        player.getWorld().dropItemNaturally(bloco.getLocation(),new ItemStack(Material.END_PORTAL_FRAME));
                        bloco.setType(Material.AIR);
                    }
                    if(bloco.getType()==Material.SPAWNER){
                        player.getWorld().dropItemNaturally(bloco.getLocation(),new ItemStack(Material.SPAWNER));
                        bloco.setType(Material.AIR);
                    }
                    if(bloco.getType()==Material.TRIAL_SPAWNER){
                        player.getWorld().dropItemNaturally(bloco.getLocation(),new ItemStack(Material.TRIAL_SPAWNER));
                        bloco.setType(Material.AIR);
                    }
                    if(bloco.getType()==Material.VAULT){
                        player.getWorld().dropItemNaturally(bloco.getLocation(),new ItemStack(Material.VAULT));
                        bloco.setType(Material.AIR);
                    }
                }
            }
        }
    }
    private void mago(Player player){

        PlayerInventory inv = player.getInventory();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(MAGO.key,PersistentDataType.INTEGER,1);

        int pos=0;
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = inv.getItem(i);
            if(stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)){
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && nome.equals("mago")) {
                    pos=i;
                    break;
                }
            }
        }
        Mago.getSpecialbyLevel(l,player,pos);
    }
    private String getDesc(String nome){
        String desc;
        String msg=ReliquiasNexus.getLang().getString("livro."+nome);
        String r=ReliquiasNexus.getLang().getString("livro.reliquia");
        if(msg!=null){
            msg=msg.replace("<break>","\n");
        }
        if(r==null){
            r="Nexus do";
        }
        if(msg==null)msg="Descrição de special não disponível";
        desc = r+" "+nome+"\n§r§0Special (Manual):\n"+msg;
        return desc;
    }
    @EventHandler
    public void reversao(EntityDamageByEntityEvent event){
        Entity atacado = event.getEntity();
        if(atacado instanceof Player player){
            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
            if(dataPlayer.has(PROTECAO.key,PersistentDataType.BOOLEAN)){
                boolean protecao = dataPlayer.getOrDefault(PROTECAO.key,PersistentDataType.BOOLEAN,false);
                if(protecao){
                    int l=dataPlayer.getOrDefault(PROTETOR.key,PersistentDataType.INTEGER,1);
                    double damage = event.getDamage();
                    event.setDamage(0);
                    damage+=l;
                    Entity e = event.getDamager();
                    if(e instanceof LivingEntity atacante){
                        atacante.damage(damage);
                    }else if(e instanceof Projectile projetil){
                        UUID uuid = projetil.getOwnerUniqueId();
                        if(uuid!=null){
                            Entity atirador = e.getWorld().getEntity(uuid);
                            if(atirador instanceof LivingEntity atacante){
                                atacante.damage(damage);
                            }
                        }
                    }
                }
            }
            if(dataPlayer.has(CHARGE.key,PersistentDataType.FLOAT)){
                float explosion = dataPlayer.getOrDefault(CHARGE.key,PersistentDataType.FLOAT,0f);
                explosion += (float) event.getDamage()/2;
                dataPlayer.set(CHARGE.key,PersistentDataType.FLOAT,explosion);
                event.setDamage(event.getDamage()/2);
            }
        }
    }
    @EventHandler
    public void amigo(EntityTargetEvent event){
        Entity alvo = event.getTarget();
        if(alvo instanceof Player player){
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && nome.equals("domador")) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void acertou(ProjectileHitEvent event){
        if(event.getEntity() instanceof Arrow arrow){
            if (arrow.hasMetadata(SPECIAL.key.getKey())){
                int forca = arrow.getMetadata(SPECIAL.key.getKey()).getFirst().asInt();
                World w = arrow.getWorld();
                w.createExplosion(arrow,forca,false,false);
            }
            if (arrow.hasMetadata("flecha_gelo")){
                int level = arrow.getMetadata("flecha_gelo").getFirst().asInt();
                if (event.getHitEntity() instanceof LivingEntity target) {
                    int duration = 60 + (20 * level);
                    int amplifier = Math.min(1 + (level / 3), 4);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, amplifier));
                    target.getWorld().spawnParticle(Particle.SNOWFLAKE, target.getLocation(), 30, 0.5, 1, 0.5);
                    target.getWorld().playSound(target.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
                    target.setFreezeTicks(20*level);
                }
                if (event.getHitBlock() != null && event.getHitBlockFace()!=null) {
                    Block block = event.getHitBlock().getRelative(event.getHitBlockFace());
                    if (block.getType() == Material.WATER) {
                        block.setType(Material.ICE);
                    } else {
                        block.setType(Material.BLUE_ICE);
                    }
                }
                arrow.remove();
            }
        }
        if(event.getEntity() instanceof WindCharge bola){
            if(bola.getPersistentDataContainer().has(SPECIAL.key,PersistentDataType.INTEGER)){
                int efeito = bola.getPersistentDataContainer().getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,1);
                Entity e = event.getHitEntity();
                if(e instanceof LivingEntity vivo){
                    vivo.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,100,efeito));
                }
            }
        }
        if(event.getEntity() instanceof Snowball bola){
            if(bola.getPersistentDataContainer().has(SPECIAL.key,PersistentDataType.INTEGER)){
                int efeito = bola.getPersistentDataContainer().getOrDefault(SPECIAL.key,PersistentDataType.INTEGER,1);
                Entity e = event.getHitEntity();
                if(e instanceof LivingEntity vivo){
                    vivo.setFreezeTicks(20*efeito);
                }
            }
        }
        if(event.getEntity() instanceof Egg bola){
            if(bola.getPersistentDataContainer().has(SPECIAL.key,PersistentDataType.INTEGER)){
                Entity e = event.getHitEntity();
                if(e instanceof LivingEntity vivo){
                    EntityType entityType = vivo.getType();
                    Material egg = EntityToEgg.getEntityEgg(entityType);
                    if(egg!=null){
                        Location loc = vivo.getEyeLocation();
                        vivo.getWorld().dropItemNaturally(loc,new ItemStack(egg));
                        vivo.remove();
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerLand(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        loc.subtract(0,1,0);
        if (player.getWorld().getBlockAt(loc).isSolid() && player.hasMetadata("saltoColossal")) {
            player.removeMetadata("saltoColossal", plugin);
            player.getWorld().createExplosion(player.getLocation(), 0F, false, false);
            double radius = 5.0;
            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof LivingEntity && entity != player) {
                    ((LivingEntity) entity).damage(8.0, player);
                    entity.setVelocity(entity.getLocation().toVector()
                            .subtract(player.getLocation().toVector())
                            .normalize().multiply(1.0));
                }
            }
        }
        if (player.getWorld().getBlockAt(loc).isSolid() && player.hasMetadata("hulkUltimate")) {
            player.removeMetadata("hulkUltimate", plugin);
            player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 50, 1, 0.5, 1, 0.1);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.8f);
            double raio = 15;
            for (Entity entity : player.getNearbyEntities(raio, 3, raio)) {
                if (entity instanceof LivingEntity && entity != player) {
                    ((LivingEntity) entity).damage(50, player);
                    Vector knockback = entity.getLocation().toVector()
                            .subtract(player.getLocation().toVector())
                            .normalize()
                            .multiply(1.5);
                    knockback.setY(1.0);
                    entity.setVelocity(knockback);
                    ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
                }
            }
        }
    }
}