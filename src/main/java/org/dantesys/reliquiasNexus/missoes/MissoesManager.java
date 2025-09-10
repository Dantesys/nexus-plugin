package org.dantesys.reliquiasNexus.missoes;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MissoesManager implements Listener {
    private static final Map<UUID, Missao> missaoAtiva = new ConcurrentHashMap<>();
    private final JavaPlugin plugin;
    public MissoesManager(JavaPlugin plugin){
        this.plugin=plugin;
    }
    public List<Missao> gerarMissoes(Player player){
        Random rd = new Random();
        int level = player.getLevel();
        MissaoDificuldade missaoDificuldade = MissaoDificuldade.getByLevel(level);
        int dif = missaoDificuldade.dificuldade;
        Material coleta = ColetaDif.getByDif(rd.nextInt(1,dif));
        Missao missaoColeta = new Missao(missaoDificuldade,720*level,rd.nextInt(1,dif),plugin,coleta,false,false);
        Material mineracao = MineracaoDif.getByDif(rd.nextInt(1,dif));
        Missao missaoMineracao = new Missao(missaoDificuldade,720*level,rd.nextInt(1,dif),plugin,mineracao,true,false);
        Material lenhador = LenhadorDif.getByDif(rd.nextInt(1,dif));
        Missao missaoLenhador = new Missao(missaoDificuldade,720*level,rd.nextInt(1,dif),plugin,lenhador,false,true);
        EntityType caca = CacaDif.getByDif(rd.nextInt(1,dif));
        Missao missaoCaca = new Missao(missaoDificuldade,720*level,rd.nextInt(1,dif),plugin,caca);
        StructureType structure = ExploracaoStructureDif.getByDif(rd.nextInt(1,dif));
        Missao missaoStructure = new Missao(missaoDificuldade,720*level*2,1,plugin,structure);
        Biome biome = ExploracaoBiomeDif.getByDif(rd.nextInt(1,dif));
        Missao missaoBiome = new Missao(missaoDificuldade,720*level*2,1,plugin,biome);
        return List.of(missaoColeta,missaoMineracao,missaoLenhador,missaoCaca,missaoStructure,missaoBiome);
    }
    public void aceitarMissao(UUID uuid,Missao missao){
        missaoAtiva.put(uuid,missao);
    }
    public void aceitarMissao(Player player,Missao missao){
        missao.iniciar(player);
        aceitarMissao(player.getUniqueId(),missao);
    }
    public void reiniciarMissao(Player player){
        missaoAtiva.get(player.getUniqueId()).reiniciar();
    }
    public void pausarMIssao(Player player){
        missaoAtiva.get(player.getUniqueId()).pausar();
    }
    public void cancelarMissao(Player player){
        missaoAtiva.remove(player.getUniqueId()).cancelar();
    }
    public void save(YamlConfiguration missaoAtivaBK){
        List<String> uuids = new ArrayList<>();
        missaoAtiva.forEach((uuid,missao) ->{
            uuids.add(uuid.toString());
            missao.pausar();
            missaoAtivaBK.set("missoes."+ uuid,missao);
        });
        missaoAtivaBK.set("players",uuids);
        File ms = new File(plugin.getDataFolder(), "missaoAtiva.yml");
        try {
            missaoAtivaBK.save(ms);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @EventHandler
    public void pickUp(EntityPickupItemEvent event){
        ItemStack item = event.getItem().getItemStack();
        if(event.getEntity() instanceof Player player && !event.getItem().hasMetadata("DROPADO")){
            if(missaoAtiva.containsKey(player.getUniqueId())){
                Missao m = missaoAtiva.get(player.getUniqueId());
                if(m.getTipo().equals(MissaoTipo.COLETA.nome)){
                    Material mat = (Material) m.get("material");
                    if(mat.equals(item.getType())){
                        m.atualizaCondicao(item.getAmount());
                        missaoAtiva.replace(player.getUniqueId(),m);
                    }
                }
            }
        }
    }
    @EventHandler
    public void breakBlock(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(missaoAtiva.containsKey(player.getUniqueId())){
            Missao m = missaoAtiva.get(player.getUniqueId());
            String tipo = m.getTipo();
            if(tipo.equals(MissaoTipo.MINERACAO.nome) || tipo.equals(MissaoTipo.LENHADOR.nome)){
                Material mat = (Material) m.get("material");
                Block bloco = event.getBlock();
                if(bloco.getType().equals(mat)){
                    m.atualizaCondicao();
                    missaoAtiva.replace(player.getUniqueId(),m);
                }
            }
        }
    }
    @EventHandler
    public void derrotou(EntityDeathEvent event){
        if (!(event.getEntity().getKiller() instanceof Player player)) return;
        if(missaoAtiva.containsKey(player.getUniqueId())){
            Missao m = missaoAtiva.get(player.getUniqueId());
            String tipo = m.getTipo();
            if(tipo.equals(MissaoTipo.MINERACAO.nome) || tipo.equals(MissaoTipo.LENHADOR.nome)){
                EntityType mat = (EntityType) m.get("entity");
                EntityType entity = event.getEntity().getType();
                if(entity.equals(mat)){
                    m.atualizaCondicao();
                    missaoAtiva.replace(player.getUniqueId(),m);
                }
            }
        }
    }
    @EventHandler
    public void achou(ServerTickEndEvent event){
        if(event.getTickNumber()%20==0){
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                if(missaoAtiva.containsKey(player.getUniqueId())){
                    Missao m = missaoAtiva.get(player.getUniqueId());
                    String tipo = m.getTipo();
                    if(tipo.equals(MissaoTipo.EXPLORACAO.nome)){
                        StructureType mat = (StructureType) m.get("structure");
                        Location loc = player.getLocation();
                        if(mat==null){
                            Biome bio = (Biome) m.get("biome");
                            if(player.getWorld().getBiome(loc).equals(bio)){
                                m.atualizaCondicao();
                                missaoAtiva.replace(player.getUniqueId(),m);
                            }
                        }else{
                            if(player.getWorld().locateNearestStructure(loc,mat,50,false)!=null){
                                m.atualizaCondicao();
                                missaoAtiva.replace(player.getUniqueId(),m);
                            }
                        }
                    }
                }
            });
        }
    }
}