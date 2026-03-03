package com.xkingdark.bob.client.render.entities.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.Unit;

public class SpearEntityModel extends Model<Unit> {

    public SpearEntityModel(ModelPart root) {
        super(root, RenderLayers::entitySolid);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(
            "main",
            ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(
                    -0.5F,
                    -23.0F,
                    -0.5F,
                    1.0F,
                    23.0F,
                    1.0F,
                    new Dilation(0.0F)
                )
                .uv(4, 10)
                .cuboid(
                    -1.0F,
                    -23.0F,
                    -1.0F,
                    2.0F,
                    1.0F,
                    2.0F,
                    new Dilation(0.0F)
                )
                .uv(12, 0)
                .cuboid(
                    -1.0F,
                    -7.0F,
                    -1.0F,
                    2.0F,
                    1.0F,
                    2.0F,
                    new Dilation(0.0F)
                )
                .uv(12, 3)
                .cuboid(
                    -1.0F,
                    -11.0F,
                    -1.0F,
                    2.0F,
                    1.0F,
                    2.0F,
                    new Dilation(0.0F)
                )
                .uv(4, 0)
                .cuboid(
                    0.0F,
                    -29.0F,
                    -2.0F,
                    0.0F,
                    6.0F,
                    4.0F,
                    new Dilation(0.0F)
                ),
            ModelTransform.origin(0.0F, 24.0F, 0.0F)
        );
        return TexturedModelData.of(modelData, 32, 32);
    }
}
