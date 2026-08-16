package github.com.gengyoubo.MPG.block.item;

import github.com.gengyoubo.MPG.block.MPHookBlock;
import github.com.gengyoubo.common.block.item.MPGHookBlockItemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import static github.com.gengyoubo.MPG.core.MPGBlockCore.HookBlock;

public class MPHookBlockItem extends MPGHookBlockItemBase {
    public MPHookBlockItem() {
        super(HookBlock.get(), MPHookBlock.class);
    }

    @Override
    protected SoundType getPlacementSoundType(BlockState state, Level level, BlockPos pos, Player player) {
        return state.getSoundType(level, pos, player);
    }

    @Override
    protected SoundEvent getPlacementSound(BlockState state, Level level, BlockPos pos, Player player) {
        return getPlaceSound(state, level, pos, player);
    }
}
