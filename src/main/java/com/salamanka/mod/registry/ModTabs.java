package com.salamanka.mod.registry;

import com.salamanka.mod.Salamanka;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Salamanka.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.salamanka"))
                    .icon(() -> new ItemStack(ModItems.COKE_5.get()))
                    .displayItems((params, out) -> {
                        out.accept(ModItems.SALAMANKA_SPAWN_EGG.get());
                        out.accept(ModItems.COKE_1.get());
                        out.accept(ModItems.COKE_3.get());
                        out.accept(ModItems.COKE_5.get());
                        out.accept(ModItems.MAUSER.get());
                        out.accept(ModItems.LA_CHONA_RECORD.get());
                        out.accept(ModItems.COKE_BLOCK.get());
                    })
                    .build());
}
