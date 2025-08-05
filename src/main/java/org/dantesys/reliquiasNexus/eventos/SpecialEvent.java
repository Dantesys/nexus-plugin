package org.dantesys.reliquiasNexus.eventos;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.*;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class SpecialEvent implements Listener {
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
                            case "barbaro" -> barbaro(player,item);
                            case "ceifador" -> ceifador(player,item);
                            case "fazendeiro" -> fazendeiro(player,item);
                            case "guerreiro" -> guerreiro(player,item);
                            case "mares" -> mares(player,item);
                            case "arqueiro" -> {
                                arqueiro(player,item);
                                event.setCancelled(true);
                            }
                            case "cacador" -> {
                                cacador(player,item);
                                event.setCancelled(true);
                            }
                            case "tempestade" -> tempestade(player,item);
                            case "mineiro" -> mineiro(player,item);
                            case "sculk" -> sculk(player,item);
                            case "protetor" -> protetor(player,item);
                        }
                        if(item.getNome()!="protetor"){
                            dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,120);
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
                        fenix(player,item);
                        dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,120);
                    }
                }
            }
            stack = player.getInventory().getItemInOffHand();
            if (stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && nome.equals("protetor")) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        protetor(player,item);
                    }
                }
            }
            stack = player.getInventory().getLeggings();
            if (stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && nome.equals("hulk")) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        hulk(player,item);
                        dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,120);
                    }
                }
            }
        }
    }
    private void barbaro(Player player,Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(BARBARO.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,200*l,9));
    }
    private void ceifador(Player player,Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(CEIFADOR.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        final int finalRange = 50;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        final double[] tp = {0};
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{
                },()-> {
        },(t)->{
            tp[0] = tp[0]+3.4;
            double x = direction.getX()*tp[0];
            double y = direction.getY()*tp[0]+1.4;
            double z = direction.getZ()*tp[0];
            location.add(x,y,z);
            location.getWorld().spawnParticle(Particle.SOUL,location,10,0,0,0,0);
            location.getWorld().playSound(location, Sound.BLOCK_SOUL_SOIL_STEP,0.5f,0.7f);
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,2,2,2);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    AttributeInstance at = vivo.getAttribute(Attribute.MAX_HEALTH);
                    atingidos.add(vivo);
                    if(at != null){
                        double max = at.getBaseValue();
                        if(vivo instanceof Player pl){
                            if(pl != player){
                                if(vivo.getHealth()/max<=0.2){
                                    Location ld = vivo.getLocation();
                                    World wd = vivo.getWorld();
                                    wd.dropItemNaturally(ld,new ItemStack(Material.TOTEM_OF_UNDYING));
                                    vivo.setHealth(0);
                                }else{
                                    vivo.damage(l+5,player);
                                }
                            }
                        }else{
                            if(vivo.getHealth()/max<=0.2){
                                Location ld = vivo.getLocation();
                                World wd = vivo.getWorld();
                                wd.dropItemNaturally(ld,new ItemStack(Material.TOTEM_OF_UNDYING));
                                vivo.setHealth(0);
                            }else{
                                vivo.damage(l+5,player);
                            }
                        }
                    }
                }
                pressf.remove(surdo);
            }
            location.subtract(x,y,z);
            if(t.getSegundosRestantes()>finalRange){
                t.stop();
            }
        });
        timer.scheduleTimer(1L);
    }
    private void fazendeiro(Player player,Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(FAZENDEIRO.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        final int finalRange = 30;
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final double damage = 5+l;
        Material finalM = getPlanta();
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{
                },()-> {
        },(t)->{
            double area = (double) finalRange /(t.getSegundosRestantes());
            for (double i = 0; i <= 2*Math.PI*area; i += 0.05) {
                double x = (area * Math.cos(i)) + location.getX();
                double z = (location.getZ() + area * Math.sin(i));
                Location particle = new Location(world, x, location.getY() + 1, z);
                world.spawnParticle(Particle.COMPOSTER,particle,1);
            }
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    if(vivo instanceof Player p){
                        if(p!=player) {
                            vivo.damage(damage,player);
                        }
                    }else{
                        vivo.damage(damage,player);
                    }
                    Location locV = vivo.getLocation();
                    world.dropItemNaturally(locV,new ItemStack(finalM,l));
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
    }
    private void guerreiro(Player player,Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(GUERREIRO.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        int range = 50;
        double damage = 10+l;
        final int finalRange = range;
        final double finalDamage = damage;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        final double[] tp = {0};
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{
                },()-> {
        },(t)->{
            tp[0] = tp[0]+3.4;
            double x = direction.getX()*tp[0];
            double y = direction.getY()*tp[0]+1.4;
            double z = direction.getZ()*tp[0];
            location.add(x,y,z);
            location.getWorld().spawnParticle(Particle.SWEEP_ATTACK,location,1,0,0,0,0);
            location.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP,0.5f,0.7f);
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,2,2,2);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    if(vivo instanceof Player pl){
                        if(pl != player){
                            vivo.damage(finalDamage,player);
                        }
                    }else{
                        vivo.damage(finalDamage,player);
                    }
                }
                pressf.remove(surdo);
            }
            location.subtract(x,y,z);
            if(t.getSegundosRestantes()>finalRange){
                t.stop();
            }
        });
        timer.scheduleTimer(1L);
    }
    private void mares(Player player,Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(MARES.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        final int finalRange = 50;
        final double damage = 5+l;
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{
                },()-> {
        },(t)->{
            double area = (double) finalRange /(t.getSegundosRestantes());
            for (double i = 0; i <= 2*Math.PI*area; i += 0.05) {
                double x = (area * Math.cos(i)) + location.getX();
                double z = (location.getZ() + area * Math.sin(i));
                Location particle = new Location(world, x, location.getY() + 1, z);
                world.spawnParticle(Particle.END_ROD,particle,1);
            }
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    if(vivo instanceof Player p){
                        if(p!=player){
                            vivo.setRemainingAir(0);
                            vivo.damage(damage,player);
                        }
                    }else{
                        vivo.setRemainingAir(0);
                        vivo.damage(damage,player);
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
    }
    private void arqueiro(Player player, Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(ARQUEIRO.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(true);
        arrow.setGlowing(true);
        arrow.setColor(Color.YELLOW);
        Vector vec = player.getLocation().getDirection();
        arrow.setVelocity(vec.multiply(20+l));
    }
    private void cacador(Player player, Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(CACADOR.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10+l,
                ()->player.sendActionBar(Component.text("Modo Minigum Ativado!")),
                ()->{},
                (t)->{
                    player.sendActionBar(Component.text("Modo Minigun acaba em "+(t.getSegundosRestantes())+" segundos"));
                    Vector vec = player.getEyeLocation().getDirection();
                    Arrow flecha = player.launchProjectile(Arrow.class);
                    flecha.setCritical(true);
                    flecha.setGlowing(true);
                    flecha.setColor(Color.YELLOW);
                    flecha.setVelocity(vec.multiply(10+l));
                }
        );
        timer.scheduleTimer(5L);
    }
    private void tempestade(Player player,Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(TEMPESTADE.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        World w = player.getWorld();
        w.setStorm(true);
        w.setThundering(true);
        final int finalRange = 30;
        final double damage = 2+l;
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{
                },()-> {
        },(t)->{
            double area = (double) finalRange /(t.getSegundosRestantes());
            for (double i = 0; i <= 2*Math.PI*area; i += 0.05) {
                double x = (area * Math.cos(i)) + location.getX();
                double z = (location.getZ() + area * Math.sin(i));
                Location particle = new Location(world, x, location.getY() + 1, z);
                world.spawnParticle(Particle.FALLING_WATER,particle,1);
            }
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    Location vloc = vivo.getLocation();
                    World vworld = vivo.getWorld();
                    vworld.strikeLightning(vloc);
                    if(vivo instanceof Player p){
                        if(p!=player){
                            vivo.damage(damage,player);
                        }
                    }else{
                        vivo.damage(damage,player);
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
    }
    private void mineiro(Player player,Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(MINEIRO.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        final int finalRange = 30;
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final double damage = 5+l;
        Material finalM = getMinerio();
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{
                },()-> {
        },(t)->{
            double area = (double) finalRange /(t.getSegundosRestantes());
            for (double i = 0; i <= 2*Math.PI*area; i += 0.05) {
                double x = (area * Math.cos(i)) + location.getX();
                double z = (location.getZ() + area * Math.sin(i));
                Location particle = new Location(world, x, location.getY() + 1, z);
                world.spawnParticle(Particle.COMPOSTER,particle,1);
            }
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    AttributeInstance at = vivo.getAttribute(Attribute.MAX_HEALTH);
                    if(at != null){
                        double max = at.getBaseValue();
                        Location ld = vivo.getLocation();
                        if(vivo instanceof Player pl){
                            if(pl != player){
                                if(vivo.getHealth()/max<=0.2){
                                    ld.getBlock().setType(finalM);
                                    vivo.setHealth(0);
                                }else{
                                    vivo.damage(damage,player);
                                    world.dropItemNaturally(ld,new ItemStack(finalM));
                                }
                            }
                        }else{
                            if(vivo.getHealth()/max<=0.2){
                                ld.getBlock().setType(finalM);
                                vivo.setHealth(0);
                            }else{
                                vivo.damage(damage,player);
                                world.dropItemNaturally(ld,new ItemStack(finalM));
                            }
                        }
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
    }
    private void fenix(Player player, Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(FENIX.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        final int finalRange = 30;
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final double damage = 5+l;
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{
                },()-> {
        },(t)->{
            double area = (double) finalRange /(t.getSegundosRestantes());
            for (double i = 0; i <= 2*Math.PI*area; i += 0.05) {
                double x = (area * Math.cos(i)) + location.getX();
                double z = (location.getZ() + area * Math.sin(i));
                Location particle = new Location(world, x, location.getY() + 1, z);
                world.spawnParticle(Particle.FLAME,particle,1);
            }
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    AttributeInstance at = vivo.getAttribute(Attribute.MAX_HEALTH);
                    if(at != null){
                        if(vivo instanceof Player pl){
                            if(pl != player){
                                vivo.damage(damage,player);
                                vivo.setFireTicks(20+l);
                            }
                        }else{
                            vivo.damage(damage,player);
                            vivo.setFireTicks(20+l);
                        }
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
    }
    private void protetor(Player player, Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l=dataPlayer.getOrDefault(PROTETOR.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 9+l,
                () -> {
                    player.sendActionBar(Component.text("Habilidade do Nexus do Protetor Ativado!"));
                    dataPlayer.set(PROTECAO.key,PersistentDataType.BOOLEAN,true);
                },
                () -> {
                    player.setGameMode(GameMode.SURVIVAL);
                    dataPlayer.set(PROTECAO.key,PersistentDataType.BOOLEAN,false);
                },
                (t) -> player.sendActionBar(Component.text("Modo Reversão acaba em "+t.getSegundosRestantes()+" segundos!"))
        );
        timer.scheduleTimer(20L);
        dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,120+l+9);
    }
    private void hulk(Player player, Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(HULK.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        final int finalRange = 20;
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final double damage = 10+l;
        final List<LivingEntity> atingidos = new ArrayList<>();
        double baseD=player.getAttribute(Attribute.ATTACK_DAMAGE).getBaseValue();
        double baseT=player.getAttribute(Attribute.SCALE).getBaseValue();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{},()-> {},(t)->{
            double area = (double) finalRange /(t.getSegundosRestantes());
            for (double i = 0; i <= 2*Math.PI*area; i += 0.05) {
                double x = (area * Math.cos(i)) + location.getX();
                double z = (location.getZ() + area * Math.sin(i));
                Location particle = new Location(world, x, location.getY() + 1, z);
                world.spawnParticle(Particle.EXPLOSION,particle,1);
            }
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,area,2,area);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && !atingidos.contains(vivo)){
                    atingidos.add(vivo);
                    AttributeInstance at = vivo.getAttribute(Attribute.MAX_HEALTH);
                    if(at != null){
                        if(vivo instanceof Player pl){
                            if(pl != player){
                                vivo.damage(damage,player);
                            }
                        }else{
                            vivo.damage(damage,player);
                        }
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
        Temporizador timer2 = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10+l,
                ()->{
                    player.sendActionBar(Component.text("Modo Hulk Ativado!"));
                    player.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(baseD+l);
                    player.getAttribute(Attribute.SCALE).setBaseValue(baseT+0.25);
                },
                ()->{
                    player.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(baseD);
                    player.getAttribute(Attribute.SCALE).setBaseValue(baseT);
                },
                (t)->{
                    player.sendActionBar(Component.text("Modo Hulk acaba em "+(t.getSegundosRestantes())+" segundos"));
                }
        );
        timer2.scheduleTimer(20L);
    }
    private void sculk(Player player,Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(HULK.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        final int finalRange = 50;
        final double finalDamage = 20+l;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        final double[] tp = {0};
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 10,
                ()->{
                },()-> {
        },(t)->{
            tp[0] = tp[0]+3.4;
            double x = direction.getX()*tp[0];
            double y = direction.getY()*tp[0]+1.4;
            double z = direction.getZ()*tp[0];
            location.add(x,y,z);
            location.getWorld().spawnParticle(Particle.SONIC_BOOM,location,1,0,0,0,0);
            location.getWorld().playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM,0.5f,0.7f);
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,2,2,2);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo){
                    if(vivo instanceof Player pl){
                        if(pl != player){
                            vivo.damage(finalDamage,player);
                        }
                    }else{
                        vivo.damage(finalDamage,player);
                    }
                }
                pressf.remove(surdo);
            }
            location.subtract(x,y,z);
            if(t.getSegundosRestantes()>finalRange){
                t.stop();
            }
        });
        timer.scheduleTimer(1L);
    }
    private Material getPlanta(){
        List<Material> m = List.of(
                Material.WHEAT,
                Material.CARROT,
                Material.BEETROOT,
                Material.POTATO,
                Material.PUMPKIN,
                Material.MELON,
                Material.GLOW_BERRIES,
                Material.SWEET_BERRIES,
                Material.CACTUS,
                Material.SUGAR_CANE
        );
        Random r = new Random();
        int i = r.nextInt(0,m.size()-1);
        return m.get(i);
    }
    private Material getMinerio(){
        List<Material> m = List.of(
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
        Random r = new Random();
        int i = r.nextInt(0,m.size()-1);
        return m.get(i);
    }
    @EventHandler
    public void reviver(EntityResurrectEvent e) {
        LivingEntity deadEntity = e.getEntity();
        if(deadEntity instanceof Player player){
            PlayerInventory pinv = player.getInventory();
            ItemStack item = pinv.getItemInMainHand();
            ItemStack item2 = pinv.getItemInOffHand();
            PersistentDataContainerView data = item.getPersistentDataContainer();
            PersistentDataContainerView data2 = item2.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING) || data2.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome==null || nome.isBlank() || !nome.equals("vida")){
                    nome = data2.get(NEXUS.key,PersistentDataType.STRING);
                    if(nome==null || nome.isBlank()|| !nome.equals("vida")){
                        return;
                    }
                }
                PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                Nexus n = ItemsRegistro.getFromNome(nome);
                if(n==null)return;
                if(dataPlayer.has(SPECIAL.key, PersistentDataType.INTEGER)){
                    int countDown = dataPlayer.getOrDefault(SPECIAL.key, PersistentDataType.INTEGER,0);
                    if(countDown>0){
                        e.setCancelled(true);
                        return;
                    }
                    int l=dataPlayer.getOrDefault(VIDA.key,PersistentDataType.INTEGER,1);
                    int tempo = 120;
                    player.getInventory().setItemInMainHand(item);
                    player.getInventory().setItemInOffHand(item2);
                    Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class),
                            tempo,
                            () -> {
                                player.sendActionBar(Component.text("Habilidade do Nexus da Vida Ativado!"));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,20+l, l));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,20+l, 2));
                            },
                            () -> {
                            },
                            (t) -> {}
                    );
                    timer.scheduleTimer(20L);
                    dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,tempo);
                }
            }
        }
    }
    @EventHandler
    public void reversao(EntityDamageByEntityEvent event){
        Entity atacado = event.getEntity();
        if(atacado instanceof Player player){
            PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
            if(dataPlayer.getOrDefault(PROTECAO.key,PersistentDataType.BOOLEAN,false)){
                int l=dataPlayer.getOrDefault(PROTETOR.key,PersistentDataType.INTEGER,1);
                double damage = event.getDamage();
                event.setDamage(0);
                damage+=l;
                Entity e = event.getDamager();
                if(e instanceof LivingEntity atacante){
                    atacante.damage(damage,e);
                }else if(e instanceof Projectile projetil){
                    UUID uuid = projetil.getOwnerUniqueId();
                    if(uuid!=null){
                        Entity atirador = e.getWorld().getEntity(uuid);
                        if(atirador instanceof LivingEntity atacante){
                            atacante.damage(damage,e);
                        }
                    }
                }
            }
        }
    }
}
