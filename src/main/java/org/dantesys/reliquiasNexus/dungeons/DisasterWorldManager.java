package org.dantesys.reliquiasNexus.dungeons;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.Listener;

import java.util.*;

public class DisasterWorldManager implements Listener {
    private final String name = "DisasterWorld";
    private World world;
    private final Map<String, Biome> biomas = new HashMap<>();
    public DisasterWorldManager(){
        biomas.put("Earth",Biome.STONY_PEAKS);
        biomas.put("Magma",Biome.NETHER_WASTES);
        biomas.put("Snow",Biome.ICE_SPIKES);
        biomas.put("Solar",Biome.DESERT);
        biomas.put("Tempest",Biome.PLAINS);
        biomas.put("Toxic",Biome.SWAMP);
        biomas.put("Umbra",Biome.DEEP_DARK);
        biomas.put("Water",Biome.OCEAN);
    }
}
