package org.dantesys.reliquiasNexus.missoes;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

public class MissaoScoreboard {
    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;

    public MissaoScoreboard(Player player) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("missao", "dummy", Component.text("§lMissão"));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Linhas iniciais
        setLine(5, "§7⏰ "+ReliquiasNexus.getLang().getString("missao.tempoRestante","Tempo Restante")+": §f--:--");
        setLine(4, "§7"+ReliquiasNexus.getLang().getString("missao.menu","Missão")+": §f--");
        setLine(3, "§7Meta: §f--");
        setLine(2, "§7"+ReliquiasNexus.getLang().getString("missao.progresso","Progresso")+": §f0/0");
        setLine(1, "§7§m----------------");

        player.setScoreboard(scoreboard);
    }

    public void updateTempo(String tempo) {
        setLine(5, "§7⏰ "+ReliquiasNexus.getLang().getString("missao.tempoRestante","Tempo Restante")+": §f" + tempo);
    }

    public void updateMissao(String missao) {
        setLine(4, "§7"+ReliquiasNexus.getLang().getString("missao.menu","Missão")+": §f" + missao);
    }

    public void updateMeta(String meta) {
        setLine(3, "§7Meta: §f" + meta);
    }

    public void updateProgresso(int atual, int total) {
        setLine(2, "§7"+ReliquiasNexus.getLang().getString("missao.progresso","Progresso")+": §f" + atual + "/" + total);
    }

    private void setLine(int line, String text) {
        // Apaga linha antiga
        for (String entry : scoreboard.getEntries()) {
            if (objective.getScore(entry).getScore() == line) {
                scoreboard.resetScores(entry);
            }
        }
        objective.getScore(text).setScore(line);
    }

    public void remove() {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}