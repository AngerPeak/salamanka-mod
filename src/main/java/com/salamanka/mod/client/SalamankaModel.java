package com.salamanka.mod.client;

import com.salamanka.mod.entity.SalamankaEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** Humanoid body + a big sombrero instead of the normal "hat" layer. Texture is 64x64. */
public class SalamankaModel extends HumanoidModel<SalamankaEntity> {
    public SalamankaModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-7.0F, -7.0F, -7.0F, 14.0F, 1.0F, 14.0F)   // brim
                        .texOffs(0, 48).addBox(-4.5F, -11.0F, -4.5F, 9.0F, 4.0F, 9.0F),   // crown
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void prepareMobModel(SalamankaEntity entity, float limbSwing, float limbSwingAmount, float partialTick) {
        // aim the Mauser with both arms when angry
        this.rightArmPose = entity.isAggressive() ? ArmPose.BOW_AND_ARROW : ArmPose.EMPTY;
        this.leftArmPose = ArmPose.EMPTY;
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }
}
