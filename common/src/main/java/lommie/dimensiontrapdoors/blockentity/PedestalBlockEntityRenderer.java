package lommie.dimensiontrapdoors.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class PedestalBlockEntityRenderer implements BlockEntityRenderer<@NotNull PedestalBlockEntity, @NotNull PedestalBlockEntityRenderState> {
    private final ItemModelResolver itemModelResolver;

    public PedestalBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public PedestalBlockEntityRenderState createRenderState() {
        return new PedestalBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(PedestalBlockEntity blockEntity, PedestalBlockEntityRenderState blockEntityRenderState, float f, @NotNull Vec3 vec3, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        // why does ItemStackRenderState need BlockPos?
        int i = HashCommon.long2int(blockEntity.getBlockPos().asLong());

        BlockEntityRenderer.super.extractRenderState(blockEntity, blockEntityRenderState, f, vec3, crumblingOverlay);
        itemModelResolver.updateForTopItem(blockEntityRenderState.item,
                blockEntity.item, ItemDisplayContext.NONE, blockEntity.getLevel(), blockEntity, i);
    }

    @Override
    public void submit(PedestalBlockEntityRenderState blockEntityRenderState, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, @NotNull CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(.5,1.5,.5);
        poseStack.scale(.5f,.5f,.5f);
        Vec3 relativeCamPos = cameraRenderState.pos.subtract(blockEntityRenderState.blockPos.getCenter());
        poseStack.mulPose(Axis.YP.rotation((float) (Math.PI/2-Math.atan2(relativeCamPos.z, relativeCamPos.x))));
        // k = outlineColor
        blockEntityRenderState.item.submit(poseStack,submitNodeCollector,blockEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY,0);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }
}
