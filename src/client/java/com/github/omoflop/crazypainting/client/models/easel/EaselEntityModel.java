package com.github.omoflop.crazypainting.client.models.easel;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

public class EaselEntityModel extends EntityModel<EaselEntityRenderState>  {
    public EaselEntityModel(ModelPart root) {
        super(root.getChild("group"));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData group = modelPartData.addChild("group", ModelPartBuilder.create().uv(8, 49).cuboid(-11.0F, -1.0F, -3.0F, 14.0F, 1.0F, 14.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-11.0F, -10.0F, -1.0F, 14.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(4.0F, 24.0F, -4.0F));

        ModelPartData arm3_r1 = group.addChild("arm3_r1", ModelPartBuilder.create().uv(40, 0).cuboid(-1.0F, -28.0F, -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, 8.0F, 0.1309F, 0.0F, 0.0F));

        ModelPartData arm2_r1 = group.addChild("arm2_r1", ModelPartBuilder.create().uv(56, 0).cuboid(-1.0F, -28.0F, -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -0.096F, 0.0F, -0.1134F));

        ModelPartData arm1_r1 = group.addChild("arm1_r1", ModelPartBuilder.create().uv(48, 0).cuboid(-1.0F, -28.0F, -1.0F, 2.0F, 28.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, 0.0F, 0.0F, -0.096F, 0.0F, 0.1134F));
        return TexturedModelData.of(modelData, 64, 64);
    }
}
