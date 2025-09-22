package org.dantesys.reliquiasNexus.raids;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CommomEvent implements Listener {
    protected final Location location;
    protected EventStatus status=EventStatus.PENDING;
    protected final RaidDificuldade dificuldade;
    protected final JavaPlugin plugin;

    public CommomEvent(Location location, JavaPlugin plugin) {
        this.location = location;
        this.plugin = plugin;
        dificuldade=RaidDificuldade.getRandom();
    }

    public void start(){
        changeStatus(EventStatus.RUNNING);
    };
    public void stop(){
        changeStatus(EventStatus.LOSE);
    };
    public void changeStatus(EventStatus status){
        this.status=status;
    }
    public Location getLocation() {
        return location.clone();
    }
    public boolean isRunning() {
        return status == EventStatus.RUNNING;
    }
    public boolean isFinished() {
        return status == EventStatus.WIN || status == EventStatus.LOSE;
    }
    public void broadcast(Component comp){
        Bukkit.broadcast(comp);
    }
    public void handleDamage(EntityDamageByEntityEvent e) {}
}