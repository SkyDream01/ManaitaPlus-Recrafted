package github.com.gengyoubo.MPG.item;

import github.com.gengyoubo.common.item.MPGSwordItemBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.NotNull;

public class MPGSwordItem extends MPGSwordItemBase {
    public boolean onEntitySwing(@NotNull ItemStack stack, @NotNull LivingEntity entity,
                                 @NotNull InteractionHand hand) {
        if (entity instanceof Player player) {
            performSweep(player, stack);
        }
        return false;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ToolAction toolAction) {
        return true;
    }

    @Override
    public boolean onLeftClickEntity(@NotNull ItemStack stack, @NotNull Player player, Entity entity) {
        entity.hurt(entity.damageSources().playerAttack(player), 10000);
        return super.onLeftClickEntity(stack, player, entity);
    }
}
