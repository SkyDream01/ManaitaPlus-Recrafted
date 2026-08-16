package github.com.gengyoubo.MPG.block;

import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.MPG.block.entity.MPCraftingBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.menu.MPGCraftingMenu;
import github.com.gengyoubo.common.block.MPGCraftingBlockBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MPCraftingBlock extends MPGCraftingBlockBase {
    public static final MapCodec<MPCraftingBlock> CODEC = simpleCodec(MPCraftingBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting_manaita");

    public MPCraftingBlock() {
        this(BlockBehaviour.Properties.of().noOcclusion());
    }

    private MPCraftingBlock(BlockBehaviour.Properties properties) {
        super(properties, () -> MPGBlockCore.HookBlockItem.get());
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new SimpleMenuProvider(
                (containerId, inventory, player) -> new MPGCraftingMenu(
                        containerId, inventory, ContainerLevelAccess.create(level, pos)),
                CONTAINER_TITLE);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MPCraftingBlockEntity(pos, state);
    }
}
