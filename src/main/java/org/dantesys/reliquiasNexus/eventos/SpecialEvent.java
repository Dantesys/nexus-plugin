package org.dantesys.reliquiasNexus.eventos;

import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;
import org.dantesys.reliquiasNexus.util.EntityToEgg;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.*;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class SpecialEvent implements Listener {
    private final ReliquiasNexus plugin;
    public SpecialEvent(ReliquiasNexus plugin){
        this.plugin=plugin;
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
                            case "pescador" -> pescador(player,item);
                            case "ladrao" -> {
                                ladrao(player,item);
                                event.setCancelled(true);
                            }
                            case "domador" -> domador(player,item);
                            case "mago" -> {
                                mago(player,item);
                                event.setCancelled(true);
                            }
                        }
                        if(!item.getNome().equals("protetor") && !item.getNome().equals("mago")){
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
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(stack.getPersistentDataContainer().has(DONO.key, PersistentDataType.STRING) && stack.getType()==Material.WRITTEN_BOOK){
            String d = stack.getPersistentDataContainer().get(DONO.key, PersistentDataType.STRING);
            if(d!=null && d.equals("nexus")){
                BookMeta meta = (BookMeta) stack.getItemMeta();
                meta.setGeneration(BookMeta.Generation.ORIGINAL);
                meta.pages(Collections.emptyList());
                meta.addPages(Component.text("Todas as Reliquias precisam de Xp para evoluir\nAs que possuem Special Manual para ativar tem que está agachado"));
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
                    if(bloco.getType()==Material.END_PORTAL_FRAME){
                        player.getWorld().dropItemNaturally(bloco.getLocation(),new ItemStack(Material.END_PORTAL_FRAME));
                        bloco.setType(Material.AIR);
                    }
                    if(bloco.getType()==Material.SPAWNER){
                        player.getWorld().dropItemNaturally(bloco.getLocation(),new ItemStack(Material.SPAWNER));
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
    private String getDesc(String nome){
        String desc="";
        switch (nome){
            case "guerreiro" -> desc="§l§6Relíquia do Guerreiro\n§r§0Special (Manual):\nUm corte especial que atravessa blocos chega até 50 blocos de distância!\nPara evoluir precisa derrotar Monstros ou Bosses";
            case "ceifador" -> desc="§l§6Relíquia do Ceifador\n§r§0Special (Manual):\nUm ataque especial que atravessa blocos chega até 50 blocos de distância e pode coletar a alma dos fracos!\nPara evoluir precisa roubar vida!";
            case "vida" -> desc="§l§6Relíquia da Vida\n§r§0Special (Automatico):\nUma segunda vida!\nPara evoluir precisa recuperar vida";
            case "mares" -> desc="§l§6Relíquia dos Mares\n§r§0Special (Manual):\nCria uma onda em area de vacuo removendo a respiração de todos!\nPara evoluir precisa derrotar criaturas marinha, monstros ou bosses";
            case "barbaro" -> desc="§l§6Relíquia do Barbaro\n§r§0Special (Manual):\nAtiva um efeito de furia!\nPara evoluir precisa derrotar Monstros ou Bosses";
            case "fazendeiro" -> desc="§l§6Relíquia do Fazendeiro\n§r§0Special (Manual):\nCria uma onda em area que transforma parte da vida dos inimigos em alimento!\nPara evoluir precisa colher plantações";
            case "espiao" -> desc="§l§6Relíquia do Espião\n§r§0Special (Manual):\nVocê separa sua alma do seu corpo para espiar lugares secreto!\nPara evoluir precisa usar a habilidade";
            case "arqueiro" -> desc="§l§6Relíquia do Arqueiro\n§r§0Special (Manual):\nCria e dispara uma flecha com uma velocidade de uma bala!\nPara evoluir precisa acerta a flecha em monstros ou bosses";
            case "cacador" -> desc="§l§6Relíquia do Caçador\n§r§0Special (Manual):\nCria e dispara uma sequencia de flechas!\nPara evoluir precisa acerta a flecha em monstros ou bosses";
            case "tempestade" -> desc="§l§6Relíquia da Tempestade\n§r§0Special (Manual):\nCria uma tempestade dee raios a sua volta!\nPara evoluir precisa derrotar Monstros ou Bosses";
            case "mineiro" -> desc="§l§6Relíquia do Mineiro\n§r§0Special (Manual):\nCria uma onda em area que transforma parte da vida dos enemigos em minerio!\nPara evoluir precisa minerar minerios";
            case "fenix" -> desc="§l§6Relíquia da Fenix\n§r§0Special (Manual):\nCria uma onda de calor que queima os inimigos proximos!\nPara evoluir precisa voar com fogos de artificios";
            case "protetor" -> desc="§l§6Relíquia do Protetor\n§r§0Special (Manual):\nCria um campo de reflexão que faz seus atacantes receberem o dano de volta!\nPara evoluir precisa se defender usando o escudo";
            case "hulk" -> desc="§l§6Relíquia do Hulk\n§r§0Special (Manual):\nCria uma explosão e você fica maior e mais forte!\nPara evoluir precisa receber dano de monstros ou bosses";
            case "sculk" -> desc="§l§6Relíquia do Sculk\n§r§0Special (Manual):\nCria uma explosão sonica igual a do Warden!\nPara evoluir precisa ser atacado pelo Warden e sobreviver";
            case "pescador" -> desc="§l§6Relíquia do Pescador\n§r§0Special (Manual):\nCria um peixe a partir da vida no alvo!\nPara evoluir precisa acertar o anzol em animais marinhos";
            case "flash" -> desc="§l§6Relíquia do Flash\n§r§0Special (Manual):\nUm teleporte para alguns blocos a frente!\nPara evoluir precisa usar a habilidade";
            case "mago" -> desc="§l§6Relíquia do Mago\n§r§0Special (Manual):\nA habilidade pode variar dependendo do slot que ele vai esta!\nPara evoluir precisa beber poções";
            case "ladrao" -> desc="§l§6Relíquia do Ladrão\n§r§0Special (Manual):\nVocê foge para seu ponto de spawn!\nPara evoluir precisa roubar itens com a reliquia";
            case "domador" -> desc="§l§6Relíquia do Domador\n§r§0Special (Manual):\nVocê cria um lobo companheiro!\nPara evoluir precisa domesticas animais/pets";
        }
        return desc;
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
        Temporizador timer = new Temporizador(plugin, 10,
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
                                    vivo.damage(l+5);
                                }
                            }
                        }else{
                            if(vivo.getHealth()/max<=0.2){
                                Location ld = vivo.getLocation();
                                World wd = vivo.getWorld();
                                wd.dropItemNaturally(ld,new ItemStack(Material.TOTEM_OF_UNDYING));
                                vivo.setHealth(0);
                            }else{
                                vivo.damage(l+5);
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
        Temporizador timer = new Temporizador(plugin, 10,
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
                            vivo.damage(damage);
                        }
                    }else{
                        vivo.damage(damage);
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
        Temporizador timer = new Temporizador(plugin, 10,
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
                            vivo.damage(finalDamage);
                        }
                    }else{
                        vivo.damage(finalDamage);
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
        Temporizador timer = new Temporizador(plugin, 10,
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
                            vivo.damage(damage);
                        }
                    }else{
                        vivo.setRemainingAir(0);
                        vivo.damage(damage);
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
        Temporizador timer = new Temporizador(plugin, 10+l,
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
        Temporizador timer = new Temporizador(plugin, 10,
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
                    if(vivo instanceof Player p){
                        if(p!=player){
                            vivo.damage(damage);
                            vworld.strikeLightning(vloc);
                        }
                    }else{
                        vivo.damage(damage);
                        vworld.strikeLightning(vloc);
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
        Temporizador timer = new Temporizador(plugin, 10,
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
                                    vivo.damage(damage);
                                    world.dropItemNaturally(ld,new ItemStack(finalM));
                                }
                            }
                        }else{
                            if(vivo.getHealth()/max<=0.2){
                                ld.getBlock().setType(finalM);
                                vivo.setHealth(0);
                            }else{
                                vivo.damage(damage);
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
        Temporizador timer = new Temporizador(plugin, 10,
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
                                vivo.damage(damage);
                                vivo.setFireTicks(20+l);
                            }
                        }else{
                            vivo.damage(damage);
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
        Temporizador timer = new Temporizador(plugin, 9+l,
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
        Temporizador timer = new Temporizador(plugin, 10,
                ()->{},()-> {},(t)->{
            double area = (double) finalRange /(t.getSegundosRestantes());
            for (double i = 0; i <= 2*Math.PI*area; i += 0.05) {
                double x = (area * Math.cos(i)) + location.getX();
                double z = (location.getZ() + area * Math.sin(i));
                Location particle = new Location(world, x, location.getY() + 1, z);
                world.spawnParticle(Particle.SMOKE,particle,1);
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
                                vivo.damage(damage);
                            }
                        }else{
                            vivo.damage(damage);
                        }
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
        Temporizador timer2 = new Temporizador(plugin, 10+l,
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
        int l = dataPlayer.getOrDefault(SCULK.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        final int finalRange = 50;
        final double finalDamage = 20+l;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        final double[] tp = {0};
        Temporizador timer = new Temporizador(plugin, 10,
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
                            vivo.damage(finalDamage);
                        }
                    }else{
                        vivo.damage(finalDamage);
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
    private void pescador(Player player,Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(PESCADOR.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        final int finalRange = 50;
        final double finalDamage = l;
        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize();
        final double[] tp = {0};
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(plugin, 10,
                ()->{
                },()-> {
        },(t)->{
            tp[0] = tp[0]+3.4;
            double x = direction.getX()*tp[0];
            double y = direction.getY()*tp[0]+1.4;
            double z = direction.getZ()*tp[0];
            location.add(x,y,z);
            location.getWorld().spawnParticle(Particle.BUBBLE_POP,location,1,0,0,0,0);
            location.getWorld().playSound(location, Sound.BLOCK_WATER_AMBIENT,0.5f,0.7f);
            Collection<Entity> pressf = location.getWorld().getNearbyEntities(location,2,2,2);
            while(pressf.iterator().hasNext()){
                Entity surdo = pressf.iterator().next();
                if(surdo instanceof LivingEntity vivo && atingidos.contains(vivo)){
                    AttributeInstance at = vivo.getAttribute(Attribute.MAX_HEALTH);
                    atingidos.add(vivo);
                    if(at != null){
                        double max = at.getBaseValue();
                        if(vivo instanceof Player pl){
                            if(pl != player){
                                if(vivo.getHealth()/max<=0.5){
                                    Location ld = vivo.getLocation();
                                    World wd = vivo.getWorld();
                                    EntityType et = peixe();
                                    if(et.getEntityClass()==null)return;
                                    Entity e = wd.spawn(ld,et.getEntityClass());
                                    vivo.getPersistentDataContainer().set(PROTECAO.key,PersistentDataType.STRING,e.getName());
                                    vivo.setHealth(0);
                                }else{
                                    vivo.damage(finalDamage);
                                }
                            }
                        }else{
                            if(vivo.getHealth()/max<=0.2){
                                Location ld = vivo.getLocation();
                                World wd = vivo.getWorld();
                                EntityType et = peixe();
                                if(et.getEntityClass()==null)return;
                                wd.spawn(ld,et.getEntityClass());
                                vivo.setHealth(0);
                            }else{
                                vivo.damage(finalDamage);
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
    private void ladrao(Player player, Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(LADRAO.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        Location loc = player.getRespawnLocation();
        if(loc==null)loc=player.getWorld().getSpawnLocation();
        player.teleport(loc);
    }
    private void domador(Player player,Nexus item){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(DOMADOR.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
        Location loc = player.getLocation();
        Wolf wolf = player.getWorld().spawn(loc,Wolf.class);
        wolf.setOwner(player);
        wolf.getAttribute(Attribute.ARMOR).setBaseValue(l);
        wolf.getAttribute(Attribute.ARMOR_TOUGHNESS).setBaseValue(l);
        wolf.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(l);
        wolf.getAttribute(Attribute.MAX_HEALTH).setBaseValue(l);
        wolf.getAttribute(Attribute.SCALE).setBaseValue(1.25);
    }
    private void mago(Player player,Nexus item){
        int tempo=120;
        PlayerInventory inv = player.getInventory();
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(MAGO.key,PersistentDataType.INTEGER,1);
        item.setLevel(l);
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
        switch (pos){
            case 0 -> {
                Fireball bola = player.launchProjectile(Fireball.class);
                bola.setGlowing(true);
                Vector vec = player.getEyeLocation().getDirection();
                bola.setVelocity(vec.multiply(l));
                tempo=60;
            }
            case 1 -> {
                WindCharge bola = player.launchProjectile(WindCharge.class);
                bola.setGlowing(true);
                Vector vec = player.getEyeLocation().getDirection();
                bola.getPersistentDataContainer().set(SPECIAL.key,PersistentDataType.INTEGER,l);
                bola.setVelocity(vec.multiply(l));
                tempo=20;
            }
            case 2 -> {
                Snowball bola = player.launchProjectile(Snowball.class);
                bola.setGlowing(true);
                Vector vec = player.getEyeLocation().getDirection();
                bola.getPersistentDataContainer().set(SPECIAL.key,PersistentDataType.INTEGER,l);
                bola.setVelocity(vec.multiply(l));
                tempo=10;
            }
            case 3 -> {
                Egg bola = player.launchProjectile(Egg.class);
                bola.setGlowing(true);
                Vector vec = player.getEyeLocation().getDirection();
                bola.getPersistentDataContainer().set(SPECIAL.key,PersistentDataType.INTEGER,l);
                bola.setVelocity(vec.multiply(l));
                tempo=20;
            }
            case 4 -> {
                SpectralArrow bola = player.launchProjectile(SpectralArrow.class);
                bola.setGlowing(true);
                Vector vec = player.getEyeLocation().getDirection();
                bola.setVelocity(vec.multiply(l));
                tempo=30;
            }
            case 5 -> {
                final int finalRange = 50;
                final double finalDamage = 20+l;
                final Location location = player.getLocation();
                final Vector direction = location.getDirection().normalize();
                final double[] tp = {0};
                Temporizador timer = new Temporizador(plugin, 10,
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
                                    vivo.damage(finalDamage);
                                }
                            }else{
                                vivo.damage(finalDamage);
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
            case 6 -> {
                EnderPearl bola = player.launchProjectile(EnderPearl.class);
                bola.setGlowing(true);
                Vector vec = player.getEyeLocation().getDirection();
                bola.setVelocity(vec.multiply(l));
                tempo=10;
            }
            case 7 -> {
                final int finalRange = 30;
                final Location location = player.getLocation();
                final World world = player.getWorld();
                final List<LivingEntity> atingidos = new ArrayList<>();
                Temporizador timer = new Temporizador(plugin, 10,
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
                                    vivo.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,20+l,l));
                                }
                            }else{
                                vivo.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,20+l,l));
                            }
                        }
                        pressf.remove(surdo);
                    }
                });
                timer.scheduleTimer(1L);
                tempo=60;
            }
            case 8 -> {
                World w = player.getWorld();
                w.setStorm(true);
                w.setThundering(true);
                final int finalRange = 30;
                final double damage = 10+l;
                final Location location = player.getLocation();
                final World world = player.getWorld();
                final List<LivingEntity> atingidos = new ArrayList<>();
                Temporizador timer = new Temporizador(plugin, 10,
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
                                    vivo.damage(damage);
                                }
                            }else{
                                vivo.damage(damage);
                            }
                        }
                        pressf.remove(surdo);
                    }
                });
                timer.scheduleTimer(1L);
            }
        }
        dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,tempo);
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
            player.getPersistentDataContainer().set(PROTECAO.key,PersistentDataType.STRING,"");
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
                    Temporizador timer = new Temporizador(plugin,
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
        }
        Entity atacante = event.getDamager();
        if(atacante instanceof FishHook vara){
            UUID uuid = vara.getOwnerUniqueId();
            if(uuid!=null){
                Player player = Bukkit.getPlayer(uuid);
                if(player!=null){
                    lancaPeixe(player,atacado);
                }
            }
        }else if(atacante instanceof Player player){
            lancaPeixe(player,atacado);
        }
    }
    private EntityType peixe(){
        List<EntityType> m = List.of(
                EntityType.SQUID,
                EntityType.COD,
                EntityType.DOLPHIN,
                EntityType.PUFFERFISH,
                EntityType.SALMON,
                EntityType.TROPICAL_FISH,
                EntityType.AXOLOTL,
                EntityType.GLOW_SQUID,
                EntityType.TADPOLE,
                EntityType.TURTLE
        );
        Random r = new Random();
        int i = r.nextInt(0,m.size()-1);
        return m.get(i);
    }
    private void lancaPeixe(Player player,Entity atacado){
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(stack.getPersistentDataContainer().has(NEXUS.key,PersistentDataType.STRING)){
            String nome = stack.getPersistentDataContainer().get(NEXUS.key,PersistentDataType.STRING);
            if(nome!=null && nome.equals("pescador")){
                Nexus n = ItemsRegistro.getFromNome(nome);
                if(n!=null){
                    PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
                    int level = dataPlayer.getOrDefault(PESCADOR.key,PersistentDataType.INTEGER,1);
                    n.setLevel(level);
                    World w = player.getWorld();
                    EntityType et = peixe();
                    if(et.getEntityClass()==null)return;
                    Entity en = w.spawn(atacado.getLocation(),et.getEntityClass());
                    if(!et.hasDefaultAttributes() && et.getDefaultAttributes().getAttribute(Attribute.MAX_HEALTH)==null)return;
                    double dano = et.getDefaultAttributes().getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                    if(atacado instanceof LivingEntity vivo){
                        vivo.damage(dano,en);
                    }
                }
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
}
