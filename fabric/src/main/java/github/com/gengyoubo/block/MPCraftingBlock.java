package github.com.gengyoubo.block;

import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.block.entity.MPCraftingBlockEntity;
import github.com.gengyoubo.common.block.MPGCraftingBlockBase;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.menu.MPCraftingMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MPCraftingBlock extends MPGCraftingBlockBase {
    private static final MapCodec<MPCraftingBlock> CODEC = MapCodec.unit(MPCraftingBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting_manaita");

    public MPCraftingBlock() {
        super(() -> MPBlockCore.HookBlockItem.get());
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return new ExtendedScreenHandlerFactory<BlockPos>() {
            @Override
            public @NotNull Component getDisplayName() {
                return CONTAINER_TITLE;
            }

            @Override
            public BlockPos getScreenOpeningData(ServerPlayer player) {
                return pos;
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory,
                                                              @NotNull Player player) {
                return new MPCraftingMenu(containerId, inventory, pos);
            }
        };
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MPCraftingBlockEntity(pos, state);
    }
}
