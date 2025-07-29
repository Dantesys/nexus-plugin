package org.dantesys.reliquiasNexus.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class Temporizador implements Runnable {
    private final JavaPlugin plugin;
    private Integer taskId;
    private final int segundos;
    private int segundosrestante;
    private final Consumer<Temporizador> porsegundo;
    private final Runnable antes;
    private final Runnable depois;

    public Temporizador(JavaPlugin plugin, int seconds,
                        Runnable beforeTimer, Runnable afterTimer,
                        Consumer<Temporizador> everySecond) {
        this.plugin = plugin;

        this.segundos = seconds;
        this.segundosrestante = seconds;

        this.antes = beforeTimer;
        this.depois = afterTimer;
        this.porsegundo = everySecond;
    }
    @Override
    public void run() {
        if (segundosrestante < 1) {
            depois.run();
            if (taskId != null) Bukkit.getScheduler().cancelTask(taskId);
            return;
        }
        if (segundosrestante == segundos) antes.run();
        porsegundo.accept(this);
        segundosrestante--;
    }
    public int getSegundosRestantes() {
        return segundosrestante;
    }
    public void scheduleTimer(Long periodo) {
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, periodo);
    }
    public void stop(){
        this.taskId = null;
    }

}