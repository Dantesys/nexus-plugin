package org.dantesys.reliquiasNexus.eventos;

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
                            case "barbaro" -> barbaro(player);
                            case "ceifador" -> ceifador(player);
                            case "fazendeiro" -> fazendeiro(player);
                            case "guerreiro" -> guerreiro(player);
                            case "vida" -> vida(player);
                            case "mares" -> mares(player);
                            case "arqueiro" -> {
                                arqueiro(player);
                                event.setCancelled(true);
                            }
                            case "cacador" -> {
                                cacador(player);
                                event.setCancelled(true);
                            }
                            case "tempestade" -> tempestade(player);
                            case "mineiro" -> mineiro(player);
                            case "sculk" -> sculk(player,item);
                            case "protetor" -> protetor(player);
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
                        fenix(player);
                        dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,120);
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
                            protetor(player);
                            dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,120);
                        }
                    }else if(nome.equals("vida")){
                        vida(player);
                    }
                }
            }
            stack = player.getInventory().getLeggings();
            if (stack!=null && stack.getPersistentDataContainer().has(NEXUS.key, PersistentDataType.STRING)) {
                String nome = stack.getPersistentDataContainer().get(NEXUS.key, PersistentDataType.STRING);
                if (nome != null && nome.equals("hulk")) {
                    Nexus item = ItemsRegistro.getFromNome(nome);
                    if(item!=null){
                        hulk(player);
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
                String msg = ReliquiasNexus.getLang().getString("livro.base");
                if(msg==null){
                    msg="Todas as Reliquias precisam de Xp para evoluir.<break>As que possuem Special Manual para ativar tem que está agachado";
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
        String msg=ReliquiasNexus.getLang().getString("livro."+nome);
        String r=ReliquiasNexus.getLang().getString("livro.reliquia");
        if(msg!=null){
            msg=msg.replace("<break>","\n");
        }
        if(r==null){
            r="Nexus do";
        }
        switch (nome){
            case "guerreiro" -> {
                if(msg==null){
                    msg="Um corte especial que atravessa blocos chega até 50 blocos de distância!\nPara evoluir precisa derrotar Monstros ou Bosses";
                }
                desc="§l§6"+r+" Guerreiro\n§r§0Special (Manual):\n"+msg;
            }
            case "ceifador" -> {
                if(msg==null){
                    msg="Um corte especial que atravessa blocos chega até 50 blocos de distância!\nPara evoluir precisa derrotar Monstros ou Bosses";
                }
                desc="§l§6"+r+" Ceifador\n§r§0Special (Manual):\n"+msg;
            }
            case "vida" -> {
                if(msg==null){
                    msg="Uma segunda vida!\nPara evoluir precisa recuperar vida";
                }
                desc="§l§6"+r+" Vida\n§r§0Special (Automatico):\n"+msg;
            }
            case "mares" -> {
                if(msg==null){
                    msg="Cria uma onda em area de vacuo removendo a respiração de todos!\nPara evoluir precisa derrotar criaturas marinha, monstros ou bosses";
                }
                desc="§l§6"+r+" Mares\n§r§0Special (Manual):\n"+msg;
            }
            case "barbaro" -> {
                if(msg==null){
                    msg="Ativa um efeito de furia!\nPara evoluir precisa derrotar Monstros ou Bosses";
                }
                desc="§l§6"+r+" Barbaro\n§r§0Special (Manual):\n"+msg;
            }
            case "fazendeiro" -> {
                if(msg==null){
                    msg="Cria uma onda em area que transforma parte da vida dos inimigos em alimento!\nPara evoluir precisa colher plantações";
                }
                desc="§l§6"+r+" Fazendeiro\n§r§0Special (Manual):\n"+msg;
            }
            case "espiao" -> {
                if(msg==null){
                    msg="Você separa sua alma do seu corpo para espiar lugares secreto!\nPara evoluir precisa usar a habilidade";
                }
                desc="§l§6"+r+" Espião\n§r§0Special (Manual):\n"+msg;
            }
            case "arqueiro" -> {
                if(msg==null){
                    msg="Cria e dispara uma flecha com uma velocidade de uma bala!\nPara evoluir precisa acerta a flecha em monstros ou bosses";
                }
                desc="§l§6"+r+" Arqueiro\n§r§0Special (Manual):\n"+msg;
            }
            case "cacador" -> {
                if(msg==null){
                    msg="Cria e dispara uma sequencia de flechas!\nPara evoluir precisa acerta a flecha em monstros ou bosses";
                }
                desc="§l§6"+r+" Caçador\n§r§0Special (Manual):\n"+msg;
            }
            case "tempestade" -> {
                if(msg==null){
                    msg="Cria uma tempestade dee raios a sua volta!\nPara evoluir precisa derrotar Monstros ou Bosses";
                }
                desc="§l§6"+r+" Tempestade\n§r§0Special (Manual):\n"+msg;
            }
            case "mineiro" -> {
                if(msg==null){
                    msg="Cria uma onda em area que transforma parte da vida dos enemigos em minerio!\nPara evoluir precisa minerar minerios";
                }
                desc="§l§6"+r+" Mineiro\n§r§0Special (Manual):\n"+msg;
            }
            case "fenix" -> {
                if(msg==null){
                    msg="Cria uma onda de calor que queima os inimigos proximos!\nPara evoluir precisa voar com fogos de artificios";
                }
                desc="§l§6"+r+" Fenix\n§r§0Special (Manual):\n"+msg;
            }
            case "protetor" -> {
                if(msg==null){
                    msg="Cria um campo de reflexão que faz seus atacantes receberem o dano de volta!\nPara evoluir precisa se defender usando o escudo";
                }
                desc="§l§6"+r+" Protetor\n§r§0Special (Manual):\n"+msg;
            }
            case "hulk" -> {
                if(msg==null){
                    msg="Cria uma explosão e você fica maior e mais forte!\nPara evoluir precisa receber dano de monstros ou bosses";
                }
                desc="§l§6"+r+" Hulk\n§r§0Special (Manual):\n"+msg;
            }
            case "sculk" -> {
                if(msg==null){
                    msg="Cria uma explosão sonica igual a do Warden!\nPara evoluir precisa ser atacado pelo Warden e sobreviver";
                }
                desc="§l§6"+r+" Sculk\n§r§0Special (Manual):\n"+msg;
            }
            case "pescador" -> {
                if(msg==null){
                    msg="Cria um peixe a partir da vida no alvo!\nPara evoluir precisa acertar o anzol em animais marinhos";
                }
                desc="§l§6"+r+" Pescador\n§r§0Special (Manual):\n"+msg;
            }
            case "flash" -> {
                if(msg==null){
                    msg="Um teleporte para alguns blocos a frente!\nPara evoluir precisa usar a habilidade";
                }
                desc="§l§6"+r+" Flash\n§r§0Special (Manual):\n"+msg;
            }
            case "mago" -> {
                if(msg==null){
                    msg="A habilidade pode variar dependendo do slot que ele vai esta!\nPara evoluir precisa beber poções";
                }
                desc="§l§6"+r+" Mago\n§r§0Special (Manual):\n"+msg;
            }
            case "ladrao" -> {
                if(msg==null){
                    msg="Você foge para seu ponto de spawn!\nPara evoluir precisa roubar itens com a reliquia";
                }
                desc="§l§6"+r+" Ladrão\n§r§0Special (Manual):\n"+msg;
            }
            case "domador" -> {
                if(msg==null){
                    msg="Você cria um lobo companheiro!\nPara evoluir precisa domesticas animais/pets";
                }
                desc="§l§6"+r+" Domador\n§r§0Special (Manual):\n"+msg;
            }
        }
        return desc;
    }
    private void barbaro(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(BARBARO.key,PersistentDataType.INTEGER,1);
        Barbaro.getSpecialbyLevel(l,player);
    }
    private void ceifador(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(CEIFADOR.key,PersistentDataType.INTEGER,1);
        Ceifador.getSpecialbyLevel(l,player);
    }
    private void vida(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(VIDA.key,PersistentDataType.INTEGER,1);
        Vida.getSpecialbyLevel(l,player);
    }
    private void fazendeiro(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(FAZENDEIRO.key,PersistentDataType.INTEGER,1);
        Fazendeiro.getSpecialbyLevel(l,player);
    }
    private void guerreiro(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(GUERREIRO.key,PersistentDataType.INTEGER,1);
        Guerreiro.getSpecialbyLevel(l,player);
    }
    private void mares(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(MARES.key,PersistentDataType.INTEGER,1);
        Mares.getSpecialbyLevel(l,player);
    }
    private void arqueiro(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(ARQUEIRO.key,PersistentDataType.INTEGER,1);
        Arqueiro.getSpecialbyLevel(l,player);
    }
    private void cacador(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(CACADOR.key,PersistentDataType.INTEGER,1);
        Cacador.getSpecialbyLevel(l,player);
    }
    private void tempestade(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(TEMPESTADE.key,PersistentDataType.INTEGER,1);
        Tempestade.getSpecialbyLevel(l,player);
    }
    private void mineiro(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(MINEIRO.key,PersistentDataType.INTEGER,1);
        Mineiro.getSpecialbyLevel(l,player);
    }
    private void fenix(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(FENIX.key,PersistentDataType.INTEGER,1);
        Fenix.getSpecialbyLevel(l,player);
    }
    private void protetor(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l=dataPlayer.getOrDefault(PROTETOR.key,PersistentDataType.INTEGER,1);
        Protetor.getSpecialbyLevel(l,player);
    }
    private void hulk(Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int l = dataPlayer.getOrDefault(HULK.key,PersistentDataType.INTEGER,1);
        Hulk.getSpecialbyLevel(l,player);
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
        }
        dataPlayer.set(SPECIAL.key,PersistentDataType.INTEGER,tempo);
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
        if(event.getEntity() instanceof Arrow arrow){
            if (arrow.hasMetadata(SPECIAL.key.getKey())){
                int forca = arrow.getMetadata(SPECIAL.key.getKey()).getFirst().asInt();
                World w = arrow.getWorld();
                w.createExplosion(arrow,forca,false,false);
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