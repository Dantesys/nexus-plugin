package org.dantesys.reliquiasNexus.util;

import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class AlquimistaUtils {
    public static final Map<PotionEffectType, PotionEffectType> NEGATIVE_TO_POSITIVE = new HashMap<>() {{
        put(PotionEffectType.SLOWNESS, PotionEffectType.SPEED);
        put(PotionEffectType.MINING_FATIGUE, PotionEffectType.HASTE);
        put(PotionEffectType.WEAKNESS, PotionEffectType.STRENGTH);
        put(PotionEffectType.POISON, PotionEffectType.REGENERATION);
        put(PotionEffectType.HUNGER, PotionEffectType.SATURATION);
        put(PotionEffectType.BLINDNESS, PotionEffectType.NIGHT_VISION);
        put(PotionEffectType.NAUSEA, PotionEffectType.REGENERATION);
        put(PotionEffectType.WITHER, PotionEffectType.REGENERATION);
        put(PotionEffectType.DARKNESS, PotionEffectType.NIGHT_VISION);
        put(PotionEffectType.LEVITATION, PotionEffectType.SLOW_FALLING);
        put(PotionEffectType.INSTANT_DAMAGE, PotionEffectType.INSTANT_HEALTH);
        put(PotionEffectType.UNLUCK, PotionEffectType.LUCK);
    }};

    /**
     * Retorna o efeito positivo correspondente ao efeito negativo, ou null se não existir.
     */
    public static PotionEffectType getPositiveEffect(PotionEffectType negativeEffect){
        return NEGATIVE_TO_POSITIVE.getOrDefault(negativeEffect, negativeEffect);
    }
}