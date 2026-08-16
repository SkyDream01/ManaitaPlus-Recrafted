package github.com.gengyoubo.MPG.item.ring;

import github.com.gengyoubo.MPG.baubles.api.BaubleType;
import github.com.gengyoubo.MPG.baubles.api.IBauble;
import github.com.gengyoubo.MPG.baubles.common.lib.PlayerHandler;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.MPG.item.portable.MPGPortableItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class MPGRingItem extends MPGPortableItem implements IBauble, IMPGKey {
    protected MPGRingItem(String translationPrefix) {
        super(translationPrefix);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && PlayerHandler.equipItem(player, itemInHand)) {
                return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide);
            }
            return InteractionResultHolder.fail(itemInHand);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void onManaitaKeyPress(ItemStack itemStack, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            openPortableMenu(serverPlayer, itemStack, player.level());
        }
    }
}
