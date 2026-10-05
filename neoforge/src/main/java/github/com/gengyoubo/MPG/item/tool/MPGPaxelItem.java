package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.world.item.Item;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyToolActionHelper;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyToolBase;
import github.com.gengyoubo.common.entity.MPGEntityData;
import github.com.gengyoubo.common.util.MPText;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;

import java.util.function.Consumer;

public class MPGPaxelItem extends ManaitaPlusLegacyToolBase {
    public static final TagKey<Block> MINEABLE =
            // BlockTags#create is private in 26.3; TagKey.create keeps the same minecraft:mineable tag.
            TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("mineable"));
    public MPGPaxelItem(Item.Properties props) {
        super(props, MINEABLE, MPGToolProfile.PAXEL);
    }

    @Override
    public boolean canPerformAction(ItemInstance stack, net.neoforged.neoforge.common.ItemAbility toolAction) {
        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, @NotNull BlockState state) {
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        MPGEntityData.death.add(entity);
        entity.hurt(entity.damageSources().playerAttack(player), 10000);
        if (entity instanceof LivingEntity living) {
            living.setHealth(0F);
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        tooltip.accept(Component.empty());
        tooltip.accept(Component.literal(MPText.manaita_infinity.formatting(I18n.get("info.attack"))));
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        stack.setPopTime(0);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        int range = getRange(context.getItemInHand()) >> 1;
        boolean changed = ManaitaPlusLegacyToolActionHelper.applyInRange(context, range, (pos, state) ->
                ManaitaPlusLegacyToolActionHelper.applyAxeActions(context, pos, state)
                        | ManaitaPlusLegacyToolActionHelper.applyGrowPlantAction(context, pos, state)
                        | ManaitaPlusLegacyToolActionHelper.applyShovelAction(context, pos, state));
        // InteractionResult.sidedSuccess is gone in 26.3.
        return changed
                ? (context.getLevel().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER)
                : InteractionResult.PASS;
    }
}
