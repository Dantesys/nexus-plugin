package org.dantesys.reliquiasNexus.missoes;

public enum MissaoDificuldade {
    FACIL(0,24,1),
    MEDIO(25,49,2),
    DIFICIL(50,74,3),
    EXPERT(74,99,4),
    INSANO(100,-1,5);

    public final int min;
    public final int max;
    public final int dificuldade;

    MissaoDificuldade(int min, int max,int dificuldade) {
        this.min=min;
        this.max=max;
        this.dificuldade=dificuldade;
    }

    public static MissaoDificuldade getByLevel(int level){
        for(MissaoDificuldade dif: values()){
            if(dif.min<=level && (dif.max==-1 || level<=dif.max)){
                return dif;
            }
        }
        return FACIL;
    }

    public static MissaoDificuldade getByDif(int difi) {
        for(MissaoDificuldade dif: values()){
            if(dif.dificuldade==difi){
                return dif;
            }
        }
        return FACIL;
    }
}
