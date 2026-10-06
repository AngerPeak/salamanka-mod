package com.salamanka.mod.registry;

import com.salamanka.mod.Salamanka;
import com.salamanka.mod.item.TripEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Salamanka.MODID);

    /** Visual "trip" effect. Amplifier 0 = light hallucinations, amplifier 1+ = TOTAL TRIP. */
    public static final RegistryObject<MobEffect> TRIP = EFFECTS.register("trip", TripEffect::new);
}
