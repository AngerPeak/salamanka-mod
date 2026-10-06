package com.salamanka.mod.registry;

import com.salamanka.mod.Salamanka;
import com.salamanka.mod.entity.SalamankaEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Salamanka.MODID);

    public static final RegistryObject<EntityType<SalamankaEntity>> SALAMANKA = ENTITIES.register("salamanka",
            () -> EntityType.Builder.of(SalamankaEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build(Salamanka.MODID + ":salamanka"));
}
