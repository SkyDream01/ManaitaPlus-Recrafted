package github.com.gengyoubo.item;

import github.com.gengyoubo.common.item.MPGSwordItemBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MPSwordItem extends MPGSwordItemBase {
    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target,
                             @NotNull LivingEntity attacker) {
        if (attacker instanceof Player player) {
            performSweep(player, stack);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    protected boolean keyMessageUsesOverlay() {
        return true;
    }

    public void onDestroyed(ItemEntity itemEntity) {
    }
}
