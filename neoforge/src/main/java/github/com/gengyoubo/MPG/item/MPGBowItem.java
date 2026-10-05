package github.com.gengyoubo.MPG.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.entity.MPGEntityArrow;
import github.com.gengyoubo.common.item.MPGBowItemBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPGBowItem extends MPGBowItemBase {
    public MPGBowItem(Item.Properties props) {
        super(props, -1);
    }

    // InteractionResultHolder is deleted in 26.3: Item#use returns InteractionResult directly.
    @Override
    public @NotNull InteractionResult use(Level level, @NotNull Player player,
                                          @NotNull InteractionHand hand) {
        if (!level.isClientSide()) {
            AbstractArrow arrow = MPGEntityArrow.create(level, player);
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 10.0F, 1.0F);
            arrow.setCritArrow(true);
            level.addFreshEntity(arrow);
        }
        return super.use(level, player, hand);
    }
}
