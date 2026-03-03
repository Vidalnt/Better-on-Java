package com.xkingdark.bob.client.render.entities;

import com.xkingdark.bob.client.render.entities.models.SpearEntityModel;
import com.xkingdark.bob.entities.SpearEntity;
import java.util.List;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.RotationAxis;

public class SpearEntityRenderer
    extends EntityRenderer<SpearEntity, TridentEntityRenderState>
{

    public final Identifier texture;
    private final SpearEntityModel model;

    public SpearEntityRenderer(
        EntityRendererFactory.Context context,
        EntityModelLayer layer
    ) {
        super(context);
        this.texture = layer
            .id()
            .withPath(path -> "textures/item/spears/entity/" + path + ".png");
        this.model = new SpearEntityModel(context.getPart(layer));
    }

    public void render(
        TridentEntityRenderState renderState,
        MatrixStack matrixStack,
        OrderedRenderCommandQueue orderedRenderCommandQueue,
        CameraRenderState cameraRenderState
    ) {
        matrixStack.push();
        matrixStack.multiply(
            RotationAxis.POSITIVE_Y.rotationDegrees(renderState.yaw - 90.0F)
        );
        matrixStack.multiply(
            RotationAxis.POSITIVE_Z.rotationDegrees(renderState.pitch + 90.0F)
        );
        List<RenderLayer> list = ItemRenderer.getGlintRenderLayers(
            RenderLayers.entityCutout(this.texture),
            false,
            renderState.enchanted
        );

        for (int i = 0; i < list.size(); ++i) {
            orderedRenderCommandQueue
                .getBatchingQueue(i)
                .submitModel(
                    this.model,
                    Unit.INSTANCE,
                    matrixStack,
                    list.get(i),
                    renderState.light,
                    OverlayTexture.DEFAULT_UV,
                    -1,
                    null,
                    renderState.outlineColor,
                    null
                );
        }

        matrixStack.pop();
        super.render(
            renderState,
            matrixStack,
            orderedRenderCommandQueue,
            cameraRenderState
        );
    }

    public TridentEntityRenderState createRenderState() {
        return new TridentEntityRenderState();
    }

    public void updateRenderState(
        SpearEntity entity,
        TridentEntityRenderState renderState,
        float f
    ) {
        super.updateRenderState(entity, renderState, f);
        renderState.yaw = entity.getLerpedYaw(f);
        renderState.pitch = entity.getLerpedPitch(f);
        renderState.enchanted = entity.isEnchanted();
    }
}
