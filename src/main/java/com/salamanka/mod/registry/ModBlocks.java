package com.salamanka.mod.registry;

import com.salamanka.mod.Salamanka;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Salamanka.MODID);

    public static final RegistryObject<Block> COKE_BLOCK = BLOCKS.register("coke_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .strength(0.8F)
                    .sound(SoundType.WOOL)));
}
