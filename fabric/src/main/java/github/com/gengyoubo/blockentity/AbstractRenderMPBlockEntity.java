package github.com.gengyoubo.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import github.com.gengyoubo.block.data.MPBlockData;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRenderMPBlockEntity<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private final ItemStack displayStack;
    private final BlockState hookBlockTemplate;

    protected AbstractRenderMPBlockEntity(ItemStack displayStack) {
        this.displayStack = displayStack;
        this.hookBlockTemplate = MPBlockCore.HookBlock.get().defaultBlockState();
        github.com.gengyoubo.common.util.MPGItemStackData.setTag(this.displayStack, new net.minecraft.nbt.CompoundTag());
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = blockEntity.getBlockState();
        Direction direction = blockState.getValue(MPBlockData.FACING);
        Direction wall = blockState.getValue(MPBlockData.WALL);

        poseStack.pushPose();
        applyWallTransform(poseStack, wall, direction);
        renderMainBlockItem(blockEntity, blockState, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();

        renderHook(blockState, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderMainBlockItem(T blockEntity, BlockState blockState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        github.com.gengyoubo.common.util.MPGItemStackData.putInt(displayStack, MPGNBTData.ItemType, blockState.getValue(MPBlockData.TYPES));
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(displayStack, blockEntity.getLevel(), null, 0);
        itemRenderer.render(displayStack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, packedLight, packedOverlay, bakedModel);
    }

    private void renderHook(BlockState blockState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        int hookType = blockState.getValue(MPBlockData.HOOK);
        if (hookType == 8) {
            return;
        }

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BlockState hookBlock = hookBlockTemplate
                .setValue(MPBlockData.FACING, blockState.getValue(MPBlockData.FACING))
                .setValue(MPBlockData.TYPES, hookType);
        blockRenderer.renderSingleBlock(hookBlock, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private static void applyWallTransform(PoseStack poseStack, Direction wall, Direction direction) {
        switch (wall) {
            case NORTH:
                poseStack.translate(0.5F, 0.5F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
                break;
            case SOUTH:
                poseStack.translate(0.5F, 0.5F, 1.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                break;
            case WEST:
                poseStack.translate(0.0F, 0.5F, 0.5F);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                break;
            case EAST:
                poseStack.translate(1.0F, 0.5F, 0.5F);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                break;
            case UP:
                poseStack.translate(0.5F, 1.0F, 0.5F);
                applyVerticalFacing(poseStack, direction);
                break;
            case DOWN:
                poseStack.translate(0.5F, 0.0F, 0.5F);
                applyVerticalFacing(poseStack, direction);
                break;
        }
    }

    private static void applyVerticalFacing(PoseStack poseStack, Direction direction) {
        switch (direction) {
            case NORTH:
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                break;
            case SOUTH:
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                break;
            case WEST:
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                break;
            case EAST:
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
                break;
        }
    }
}
