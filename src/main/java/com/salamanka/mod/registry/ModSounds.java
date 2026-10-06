package com.salamanka.mod.registry;

import com.salamanka.mod.Salamanka;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Salamanka.MODID);

    public static final RegistryObject<SoundEvent> AMBIENT = reg("salamanka.ambient");
    public static final RegistryObject<SoundEvent> HURT = reg("salamanka.hurt");
    public static final RegistryObject<SoundEvent> DEATH = reg("salamanka.death");
    public static final RegistryObject<SoundEvent> SPAWN = reg("salamanka.spawn");
    public static final RegistryObject<SoundEvent> LA_CHONA = reg("music_disc.la_chona");

    private static RegistryObject<SoundEvent> reg(String name) {
        return SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Salamanka.MODID, name)));
    }
}
