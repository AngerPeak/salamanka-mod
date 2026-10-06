package com.salamanka.mod.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/** Marker effect: all the visual madness is rendered client-side in TripClientEvents. */
public class TripEffect extends MobEffect {
    public TripEffect() {
        super(MobEffectCategory.NEUTRAL, 0xD64BFF);
    }
}
