package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.EntityToEgg;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.*;

import static org.dantesys.reliquiasNexus.util.NexusKeys.SPECIAL;

public class Mago {
    private static void fireBall(int level,Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        Fireball bola = player.launchProjectile(Fireball.class);
        bola.setGlowing(true);
        Vector vec = player.getEyeLocation().getDirection();
        bola.setVelocity(vec.multiply(2));
        bola.setIsIncendiary(true);
        bola.setYield(level/2f);
        dataPlayer.set(SPECIAL.key, PersistentDataType.INTEGER,8);
    }
    private static void shield(int level,Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,600+20*level,level-1));
        dataPlayer.set(SPECIAL.key, PersistentDataType.INTEGER,20*level+10);
    }
    private static void iceArrow(int level,Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        Arrow flecha = player.launchProjectile(Arrow.class);
        flecha.setColor(Color.BLUE);
        flecha.setGlowing(true);
        flecha.setMetadata("flecha_gelo", new FixedMetadataValue(ReliquiasNexus.getPlugin(ReliquiasNexus.class), level));
        flecha.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        Vector vec = player.getEyeLocation().getDirection();
        flecha.setVelocity(vec.multiply(2));
        dataPlayer.set(SPECIAL.key, PersistentDataType.INTEGER,10);
    }
    private static void correnteArcanaInicio(int level, Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        List<Entity> entities = player.getNearbyEntities(20, 20, 20); // raio de busca
        LivingEntity alvoInicial = null;

        for (Entity e : entities) {
            if (!(e instanceof LivingEntity)) continue;

            // Pega o primeiro que está na linha de visão
            if (player.hasLineOfSight(e)) {
                alvoInicial = (LivingEntity) e;
                break;
            }
        }

        if (alvoInicial != null) {
            correnteArcana(player,alvoInicial,level,level,new HashSet<>());
        }
        dataPlayer.set(SPECIAL.key, PersistentDataType.INTEGER,12);
    }
    private static void correnteArcana(Player player, LivingEntity alvoInicial, int nivel, int saltosRestantes, Set<LivingEntity> atingidos) {
        if (saltosRestantes <= 0 || alvoInicial == null) return;

        // Dano escalável
        double dano = 3 + (nivel * 1.5);
        alvoInicial.damage(dano, player);

        // Partículas no alvo
        int quantidadeParticulas = 10 + (nivel * 2);
        player.getWorld().spawnParticle(
                Particle.ELECTRIC_SPARK,
                alvoInicial.getLocation().add(0, 1, 0),
                quantidadeParticulas, 0.3, 0.5, 0.3, 0.05
        );

        // Marca como atingido
        atingidos.add(alvoInicial);

        // Procura próximo alvo num raio crescente conforme o nível
        double raio = 4.0 + (nivel * 0.5);
        List<LivingEntity> proximos = alvoInicial.getNearbyEntities(raio, raio, raio).stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> !atingidos.contains(e)) // não repetir
                .toList();

        if (!proximos.isEmpty()) {
            LivingEntity proximo = proximos.get(new Random().nextInt(proximos.size()));

            // Linha entre os alvos
            drawLine(alvoInicial.getLocation().add(0,1,0), proximo.getLocation().add(0,1,0), nivel);

            // Chama o próximo salto
            correnteArcana(player, proximo, nivel, saltosRestantes - 1, atingidos);
        }
    }
    private static void drawLine(Location from, Location to, int nivel) {
        Vector dir = to.toVector().subtract(from.toVector());
        double length = dir.length();
        dir.normalize();

        double step = 0.4 - Math.min(0.25, nivel * 0.02); // quanto maior o nível, mais denso
        for (double d = 0; d < length; d += step) {
            Location point = from.clone().add(dir.clone().multiply(d));

            // Alterna partículas para dar efeito mágico mais forte
            from.getWorld().spawnParticle(Particle.END_ROD, point, 1, 0, 0, 0, 0);
            if (nivel >= 5) {
                from.getWorld().spawnParticle(Particle.ENCHANTED_HIT, point, 1, 0.05, 0.05, 0.05, 0);
            }
            if (nivel >= 10) {
                from.getWorld().spawnParticle(Particle.CRIT, point, 1, 0, 0, 0, 0.01);
            }
        }
    }
    private static void ondaCaos(int level,Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        double raio = 4 + (level * 0.5);   // Raio inicial 4 blocos, cresce 0,5 por nível
        double dano = 2 + (level * 0.5);   // Dano mágico base
        double knockback = 0.5 + (level * 0.1); // Força do empurrão
        List<Entity> alvos = player.getNearbyEntities(raio, raio, raio);
        for (Entity e : alvos) {
            if (e instanceof LivingEntity alvo) {
                // Aplica dano
                alvo.damage(dano, player);

                // Aplica knockback (empurra a partir do centro do player)
                Vector v = alvo.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(knockback);
                alvo.setVelocity(v);

                // Opcional: aplica um efeito tipo lentidão leve
                alvo.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0));
            }
        }
        dataPlayer.set(SPECIAL.key, PersistentDataType.INTEGER,20);
    }
    private static void tempestadeArcana(int level, Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        Location target = player.getLocation().add(player.getLocation().getDirection().multiply(3));
        target.setY(player.getLocation().getY() + 1); // altura ligeiramente acima do chão
        double raio = 4 + (level * 0.3);     // raio da tempestade
        double duracao = 3 + (level * 0.2);  // segundos
        double danoPorTick = 1 + (level * 0.5); // dano por tick
        List<LivingEntity> alvos = target.getWorld().getNearbyEntities(target, raio, raio, raio).stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .toList();
        int ticks = (int) (duracao * 20); // 20 ticks = 1 segundo

        new BukkitRunnable() {
            int contador = 0;

            @Override
            public void run() {
                if (contador >= ticks) {
                    cancel();
                    return;
                }
                for (double angle = 0; angle < 360; angle += 15) {
                    double x = Math.cos(Math.toRadians(angle)) * raio;
                    double z = Math.sin(Math.toRadians(angle)) * raio;
                    target.getWorld().spawnParticle(Particle.ENCHANT, target.clone().add(x, 1, z), 5, 0.2, 0.2, 0.2, 0.1);
                }
                target.getWorld().playSound(target, Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1f);

                for (LivingEntity alvo : alvos) {
                    alvo.damage(danoPorTick, player);
                    // efeito visual opcional: pequenas partículas no alvo
                    alvo.getWorld().spawnParticle(Particle.DRAGON_BREATH, alvo.getLocation().add(0,1,0), 5, 0.2, 0.2, 0.2, 0.05);
                }

                contador++;
            }
        }.runTaskTimer(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 0, 1); // roda a cada tick
        dataPlayer.set(SPECIAL.key, PersistentDataType.INTEGER,25);
    }
    private static void vooarcano(int level, Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        // Duração e velocidade escaláveis
        int duracaoTicks = 20 * (5 + level); // 5s base + 1s por nível

        // Ativa voo
        player.setAllowFlight(true);
        player.setFlying(true);

        // Mensagem opcional
        player.sendMessage("§bFly on " + (duracaoTicks / 20) + " s!");

        // Partículas enquanto voa
        BukkitRunnable particulas = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !player.isFlying()) {
                    cancel();
                    return;
                }
                player.getWorld().spawnParticle(
                        Particle.ENCHANT,
                        player.getLocation().add(0,1,0),
                        5, 0.3, 0.3, 0.3, 0.05
                );
            }
        };
        particulas.runTaskTimer(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 0, 2); // roda a cada 2 ticks

        // Task para desligar o voo após a duração
        new BukkitRunnable() {
            int contador = 0;

            @Override
            public void run() {
                contador++;
                if (contador >= duracaoTicks) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                    particulas.cancel();
                    player.sendMessage("§bFly end!");
                    cancel();
                }
            }
        }.runTaskTimer(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 0, 1); // conta cada tick
        dataPlayer.set(SPECIAL.key, PersistentDataType.INTEGER,30+duracaoTicks/20);
    }
    private static void eggification(int level,Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        final int finalRange = level;
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final double damage = level/2d;
        final List<LivingEntity> atingidos = new ArrayList<>();
        Temporizador timer = new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 1,
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
                    if(vivo instanceof Player pl){
                        if(pl != player){
                            vivo.damage(damage,player);
                        }
                    }else{
                        Material m = EntityToEgg.getEntityEgg(vivo.getType());
                        if(m!=null){
                            ItemStack stack = new ItemStack(m);
                            vivo.getWorld().dropItemNaturally(vivo.getLocation(),stack);
                            vivo.setHealth(0d);
                        }
                    }
                }
                pressf.remove(surdo);
            }
        });
        timer.scheduleTimer(1L);
        dataPlayer.set(SPECIAL.key, PersistentDataType.INTEGER,60);
    }
    private static void fragmentoCaos(int level,Player player){
        PersistentDataContainer dataPlayer = player.getPersistentDataContainer();
        int quantidadeFragmentos = 3 + level; // aumenta com nível
        int duracaoTicks = 20 * (5 + level); // 5s base + 1s por nível
        double raioFragmento = 2 + (level * 0.2); // distância do mago
        double danoPorContato = 1 + (level * 0.5);
        Location centro = player.getLocation().add(0, 1.5, 0); // altura dos fragmentos

        new BukkitRunnable() {
            int contador = 0;
            double angulo = 0;

            @Override
            public void run() {
                if (contador >= duracaoTicks) {
                    cancel();
                    return;
                }

                angulo += 10; // velocidade de rotação
                for (int i = 0; i < quantidadeFragmentos; i++) {
                    double theta = angulo + (360.0 / quantidadeFragmentos) * i;
                    double x = Math.cos(Math.toRadians(theta)) * raioFragmento;
                    double z = Math.sin(Math.toRadians(theta)) * raioFragmento;
                    Location fragLoc = centro.clone().add(x, 0, z);

                    // Partículas
                    player.getWorld().spawnParticle(Particle.CRIT, fragLoc, 3, 0.1, 0.1, 0.1, 0.05);

                    // Dano em inimigos próximos do fragmento
                    for (Entity e : fragLoc.getWorld().getNearbyEntities(fragLoc, 0.5, 0.5, 0.5)) {
                        if (e instanceof LivingEntity alvo && !alvo.equals(player)) {
                            alvo.damage(danoPorContato, player);
                        }
                    }
                }

                contador++;
            }
        }.runTaskTimer(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 0, 1);

        dataPlayer.set(SPECIAL.key, PersistentDataType.INTEGER,60);
    }
    public static void getPassivabyLevel(int level, Player player){
        if(level>5){
            if(level<10){
                player.giveExp(1);
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,600,0));
            }else if(level<15){
                player.giveExp(2);
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,600,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK,600,0));
            }else if(level<20){
                player.giveExp(3);
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,600,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,600,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK,600,1));
            }else{
                player.giveExp(4);
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING,600,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,600,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,600,0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK,600,2));
            }
        }
    }
    public static void getSpecialbyLevel(int level, Player player,int slot){
        if(level<6){//1-5
            basica(level,player,slot);
        }else if(level<11){//6-10
            media(level,player,slot);
        }else if(level<16){//11-15
            avancada(level,player,slot);
        }else{//16-20
            arcana(level,player,slot);
        }
    }
    private static void basica(int level,Player player,int slot){
        switch (slot){
            case 0 -> fireBall(level,player);
            case 1 -> shield(level,player);
            case 2 -> iceArrow(level,player);
        }
    }
    private static void media(int level,Player player,int slot){
        if(slot<3)basica(level,player,slot);
        else{
            switch (slot){
                case 3 -> correnteArcanaInicio(level,player);
                case 4 -> ondaCaos(level,player);
            }
        }
    }
    private static void avancada(int level,Player player,int slot){
        if(slot<5)media(level,player,slot);
        else{
            switch (slot){
                case 5 -> tempestadeArcana(level,player);
                case 6 -> vooarcano(level,player);
            }
        }
    }
    private static void arcana(int level,Player player,int slot){
        if(slot<7)avancada(level,player,slot);
        else{
            switch (slot){
                case 7 -> eggification(level,player);
                case 8 -> fragmentoCaos(level,player);
            }
        }
    }
}
