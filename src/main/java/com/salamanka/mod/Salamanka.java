package com.salamanka.mod;

import com.salamanka.mod.registry.ModBlocks;
import com.salamanka.mod.registry.ModEffects;
import com.salamanka.mod.registry.ModEntities;
import com.salamanka.mod.registry.ModItems;
import com.salamanka.mod.registry.ModSounds;
import com.salamanka.mod.registry.ModTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Salamanka.MODID)
public class Salamanka {
    public static final String MODID = "salamanka";

    public Salamanka() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModSounds.SOUNDS.register(bus);
        ModEffects.EFFECTS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModItems.ITEMS.register(bus);
        ModTabs.TABS.register(bus);
    }
}
