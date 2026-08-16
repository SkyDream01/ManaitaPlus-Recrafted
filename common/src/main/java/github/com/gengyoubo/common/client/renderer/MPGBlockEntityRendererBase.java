package github.com.gengyoubo.common.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import github.com.gengyoubo.common.block.MPGBlockData;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/** Shared mounted-block renderer; loader modules only supply registered item and hook states. */
public abstract class MPGBlockEntityRendererBase<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private final ItemStack displayStack;
    private final BlockState hookBlockTemplate;

    protected MPGBlockEntityRendererBase(ItemStack displayStack, BlockState hookBlockTemplate) {
        this.displayStack = displayStack;
        this.hookBlockTemplate = hookBlockTemplate;
        MPGItemStackData.setTag(displayStack, new CompoundTag());
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        Direction facing = state.getValue(MPGBlockData.FACING);
        Direction wall = state.getValue(MPGBlockData.WALL);

        poseStack.pushPose();
        applyWallTransform(poseStack, wall, facing);
        renderMainItem(blockEntity, state, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();

        renderHook(state, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderMainItem(T blockEntity, BlockState state, PoseStack poseStack,
                                MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        MPGItemStackData.putInt(displayStack, MPGNBTData.ItemType, state.getValue(MPGBlockData.TYPES));
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = renderer.getModel(displayStack, blockEntity.getLevel(), null, 0);
        renderer.render(displayStack, ItemDisplayContext.FIXED, true, poseStack,
                bufferSource, packedLight, packedOverlay, model);
    }

    private void renderHook(BlockState state, PoseStack poseStack, MultiBufferSource bufferSource,
                            int packedLight, int packedOverlay) {
        int hookType = state.getValue(MPGBlockData.HOOK);
        if (hookType == 8) {
            return;
        }

        BlockRenderDispatcher renderer = Minecraft.getInstance().getBlockRenderer();
        BlockState hookState = hookBlockTemplate
                .setValue(MPGBlockData.FACING, state.getValue(MPGBlockData.FACING))
                .setValue(MPGBlockData.TYPES, hookType);
        renderer.renderSingleBlock(hookState, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private static void applyWallTransform(PoseStack poseStack, Direction wall, Direction facing) {
        switch (wall) {
            case NORTH -> {
                poseStack.translate(0.5F, 0.5F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }
            case SOUTH -> {
                poseStack.translate(0.5F, 0.5F, 1.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }
            case WEST -> {
                poseStack.translate(0.0F, 0.5F, 0.5F);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            case EAST -> {
                poseStack.translate(1.0F, 0.5F, 0.5F);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            case UP -> {
                poseStack.translate(0.5F, 1.0F, 0.5F);
                applyVerticalFacing(poseStack, facing);
            }
            case DOWN -> {
                poseStack.translate(0.5F, 0.0F, 0.5F);
                applyVerticalFacing(poseStack, facing);
            }
        }
    }

    private static void applyVerticalFacing(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            case WEST -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }
            case EAST -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }
            default -> {
            }
        }
    }
}
