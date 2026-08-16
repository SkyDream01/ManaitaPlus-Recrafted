package github.com.gengyoubo.MPG.block.item;

import github.com.gengyoubo.MPG.block.MPHookBlock;
import github.com.gengyoubo.common.block.item.MPGTypedBlockItemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MPTypedBlockItem extends MPGTypedBlockItemBase {
    protected MPTypedBlockItem(Block block, Properties properties, String translationPrefix,
                               Class<? extends Block> typedBlockClass) {
        super(block, properties, translationPrefix, typedBlockClass, MPHookBlock.class);
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
