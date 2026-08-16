package github.com.gengyoubo.MPG.baubles.common.items;

import github.com.gengyoubo.MPG.baubles.api.BaubleType;
import github.com.gengyoubo.MPG.baubles.api.IBauble;
import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;
import github.com.gengyoubo.MPG.baubles.common.lib.PlayerHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemRing extends Item implements IBauble {
    public ItemRing() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant());
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer) || !BaublesCapability.isEnabled()) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return PlayerHandler.equipItem(player, stack)
                ? InteractionResultHolder.success(stack)
                : InteractionResultHolder.fail(stack);
    }

    @Override
    public void onWornTick(ItemStack itemStack, Player player) {
        if (!player.hasEffect(MobEffects.DIG_SPEED)) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 40, 0, true, false, true));
        }
    }
}
