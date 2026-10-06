package com.salamanka.mod.registry;

import com.salamanka.mod.Salamanka;
import com.salamanka.mod.item.CokeItem;
import com.salamanka.mod.item.MauserItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Salamanka.MODID);

    public static final RegistryObject<Item> COKE_1 = ITEMS.register("coke_1",
            () -> new CokeItem(1, new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> COKE_3 = ITEMS.register("coke_3",
            () -> new CokeItem(3, new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> COKE_5 = ITEMS.register("coke_5",
            () -> new CokeItem(5, new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> MAUSER = ITEMS.register("mauser",
            () -> new MauserItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    // La Chona is ~199.1 s long -> 3983 ticks
    public static final RegistryObject<Item> LA_CHONA_RECORD = ITEMS.register("la_chona_record",
            () -> new RecordItem(14, ModSounds.LA_CHONA, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 3983));

    public static final RegistryObject<Item> SALAMANKA_SPAWN_EGG = ITEMS.register("salamanka_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SALAMANKA, 0x1E1E22, 0xB0643F, new Item.Properties()));

    public static final RegistryObject<Item> COKE_BLOCK = ITEMS.register("coke_block",
            () -> new BlockItem(ModBlocks.COKE_BLOCK.get(), new Item.Properties()));
}
