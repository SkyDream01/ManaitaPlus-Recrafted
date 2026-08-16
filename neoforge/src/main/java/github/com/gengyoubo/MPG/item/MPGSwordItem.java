package github.com.gengyoubo.MPG.item;

import github.com.gengyoubo.common.item.MPGSwordItemBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

public class MPGSwordItem extends MPGSwordItemBase {
    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility itemAbility) {
        return true;
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull Player player, Entity entity) {
        if (entity instanceof LivingEntity living) {
            performPlayerAttack(player, stack, living);
            return true;
        }
        return false;
    }
}
