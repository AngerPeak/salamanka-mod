package com.salamanka.mod.client;

import com.salamanka.mod.Salamanka;
import com.salamanka.mod.registry.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Salamanka.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    public static final ModelLayerLocation SALAMANKA_LAYER =
            new ModelLayerLocation(new ResourceLocation(Salamanka.MODID, "salamanka"), "main");

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SALAMANKA.get(), SalamankaRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SALAMANKA_LAYER, SalamankaModel::createBodyLayer);
    }
}
