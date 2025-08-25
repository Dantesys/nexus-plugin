package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.entity.Player;

public class Cozinheiro {
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            sonar(level,player);
        }else if(level<16){//8-15
            sonicboom(level,player);
        }else{//16-20
            ultimatesonicboom(level,player);
        }
    }
    private static void sonar(int level, Player player){
    }
    private static void sonicboom(int level, Player player){
    }
    private static void ultimatesonicboom(int level, Player player){
    }
}
