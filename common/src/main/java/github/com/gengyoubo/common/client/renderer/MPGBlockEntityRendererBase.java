package github.com.gengyoubo.common.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import github.com.gengyoubo.common.block.MPGBlockData;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/** Shared mounted-block renderer; loader modules only supply registered item and hook states. */
public abstract class MPGBlockEntityRendererBase<T extends BlockEntity>
        implements BlockEntityRenderer<T, MPGBlockEntityRendererBase.MountedBlockRenderState> {
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private static final int NO_HOOK = 8;

    private final Supplier<ItemStack> displayStackFactory;
    private @Nullable ItemStack displayStack;
    private final BlockState hookBlockTemplate;
    private final ItemModelResolver itemModelResolver = Minecraft.getInstance().getItemModelResolver();
    private final BlockModelResolver blockModelResolver =
            new BlockModelResolver(Minecraft.getInstance().getModelManager());

    protected MPGBlockEntityRendererBase(ItemStack displayStack, BlockState hookBlockTemplate) {
        this(() -> displayStack, hookBlockTemplate);
    }

    protected MPGBlockEntityRendererBase(Supplier<ItemStack> displayStackFactory, BlockState hookBlockTemplate) {
        this.displayStackFactory = displayStackFactory;
        this.hookBlockTemplate = hookBlockTemplate;
    }

    @Override
    public MountedBlockRenderState createRenderState() {
        return new MountedBlockRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, MountedBlockRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        BlockState blockState = blockEntity.getBlockState();
        state.wall = blockState.getValue(MPGBlockData.WALL);
        state.facing = blockState.getValue(MPGBlockData.FACING);
        state.hookType = blockState.getValue(MPGBlockData.HOOK);

        ItemStack displayStack = getDisplayStack();
        MPGItemStackData.putInt(displayStack, MPGNBTData.ItemType, blockState.getValue(MPGBlockData.TYPES));
        itemModelResolver.updateForTopItem(state.item, displayStack, ItemDisplayContext.FIXED,
                blockEntity.getLevel(), null, 0);

        if (state.hookType == NO_HOOK) {
            state.hook.clear();
            return;
        }
        BlockState hookState = hookBlockTemplate
                .setValue(MPGBlockData.FACING, blockState.getValue(MPGBlockData.FACING))
                .setValue(MPGBlockData.TYPES, state.hookType);
        blockModelResolver.update(state.hook, hookState, BLOCK_DISPLAY_CONTEXT);
    }

    private ItemStack getDisplayStack() {
        // Resource reload constructs renderers before item components are bound in 26.3.
        // Create the display stack only once a world block is actually being rendered.
        if (displayStack == null) {
            displayStack = displayStackFactory.get();
            MPGItemStackData.setTag(displayStack, new CompoundTag());
        }
        return displayStack;
    }

    @Override
    public void submit(MountedBlockRenderState state, @NotNull PoseStack poseStack,
                       @NotNull SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        applyWallTransform(poseStack, state.wall, state.facing);
        submitMainItem(state, poseStack, submitNodeCollector);
        poseStack.popPose();

        submitHook(state, poseStack, submitNodeCollector);
    }

    private void submitMainItem(MountedBlockRenderState state, PoseStack poseStack,
                                SubmitNodeCollector submitNodeCollector) {
        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
    }

    private void submitHook(MountedBlockRenderState state, PoseStack poseStack,
                            SubmitNodeCollector submitNodeCollector) {
        if (state.hookType == NO_HOOK) {
            return;
        }
        state.hook.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
    }

    private static void applyWallTransform(PoseStack poseStack, Direction wall, Direction facing) {
        switch (wall) {
            case NORTH -> {
                poseStack.translate(0.5F, 0.5F, 0.0F);
                poseStack.rotateDegrees(Axis.ZP, -90.0F);
            }
            case SOUTH -> {
                poseStack.translate(0.5F, 0.5F, 1.0F);
                poseStack.rotateDegrees(Axis.ZP, 90.0F);
            }
            case WEST -> {
                poseStack.translate(0.0F, 0.5F, 0.5F);
                poseStack.rotateDegrees(Axis.XP, 90.0F);
                poseStack.rotateDegrees(Axis.YP, 90.0F);
            }
            case EAST -> {
                poseStack.translate(1.0F, 0.5F, 0.5F);
                poseStack.rotateDegrees(Axis.XP, 90.0F);
                poseStack.rotateDegrees(Axis.YP, 90.0F);
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
            case NORTH -> poseStack.rotateDegrees(Axis.XP, 90.0F);
            case SOUTH -> poseStack.rotateDegrees(Axis.XP, -90.0F);
            case WEST -> {
                poseStack.rotateDegrees(Axis.XP, 90.0F);
                poseStack.rotateDegrees(Axis.ZP, 90.0F);
            }
            case EAST -> {
                poseStack.rotateDegrees(Axis.XP, 90.0F);
                poseStack.rotateDegrees(Axis.ZP, -90.0F);
            }
            default -> {
            }
        }
    }

    /** Render state holding the extracted hook/item data for the submit phase. */
    public static class MountedBlockRenderState extends BlockEntityRenderState {
        public Direction wall = Direction.NORTH;
        public Direction facing = Direction.NORTH;
        public int hookType;
        public final ItemStackRenderState item = new ItemStackRenderState();
        public final BlockModelRenderState hook = new BlockModelRenderState();
    }
}
