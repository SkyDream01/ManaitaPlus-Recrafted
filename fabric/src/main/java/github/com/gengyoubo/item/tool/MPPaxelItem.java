package github.com.gengyoubo.item.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import github.com.gengyoubo.item.tool.base.MPToolActionHelper;
import github.com.gengyoubo.item.tool.base.MPToolBase;
import github.com.gengyoubo.common.entity.MPGEntityData;
import github.com.gengyoubo.common.util.MPText;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;

import java.util.List;

public class MPPaxelItem extends MPToolBase {
    public static final net.minecraft.tags.TagKey<Block> MINEABLE = BlockTags.MINEABLE_WITH_PICKAXE;

    public MPPaxelItem() {
        super(MINEABLE, MPGToolProfile.PAXEL);
    }
    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        MPGEntityData.death.add(target);
        target.hurt(target.damageSources().mobAttack(attacker), 10000);
        target.setHealth(0F);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.empty());
        tooltip.add(Component.literal(MPText.manaita_infinity.formatting(Component.translatable("info.attack").getString())));
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        stack.setPopTime(0);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        int range = getRange(context.getItemInHand()) >> 1;
        boolean changed = MPToolActionHelper.applyInRange(context, range, (pos, state) ->
                MPToolActionHelper.applyAxeActions(context, pos, state)
                        | MPToolActionHelper.applyGrowPlantAction(context, pos, state)
                        | MPToolActionHelper.applyShovelAction(context, pos, state));
        return changed ? InteractionResult.sidedSuccess(context.getLevel().isClientSide) : InteractionResult.PASS;
    }
}
