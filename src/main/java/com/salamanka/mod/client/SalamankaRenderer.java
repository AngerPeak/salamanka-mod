package com.salamanka.mod.client;

import com.salamanka.mod.Salamanka;
import com.salamanka.mod.entity.SalamankaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SalamankaRenderer extends HumanoidMobRenderer<SalamankaEntity, SalamankaModel> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Salamanka.MODID, "textures/entity/salamanka.png");

    public SalamankaRenderer(EntityRendererProvider.Context context) {
        super(context, new SalamankaModel(context.bakeLayer(ClientModEvents.SALAMANKA_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(SalamankaEntity entity) {
        return TEXTURE;
    }
}
