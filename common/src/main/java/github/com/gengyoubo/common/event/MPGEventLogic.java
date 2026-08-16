package github.com.gengyoubo.common.event;

import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/** Pure gameplay actions called by loader-specific event subscribers. */
public final class MPGEventLogic {
    private MPGEventLogic() {
    }

    public static void filterKeyItemTooltip(ItemStack stack, List<Component> tooltip) {
        if (!(stack.getItem() instanceof IMPGKey)) {
            return;
        }
        Iterator<Component> iterator = tooltip.iterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            if (component instanceof MutableComponent mutable
                    && mutable.getContents() instanceof TranslatableContents translatable
                    && translatable.getKey().startsWith("item.modifiers.")) {
                while (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
                return;
            }
        }
    }

    public static Optional<Player> findKiller(LivingEntity target, net.minecraft.world.entity.Entity directSource) {
        if (directSource instanceof Player player) {
            return Optional.of(player);
        }
        return target.getKillCredit() instanceof Player player ? Optional.of(player) : Optional.empty();
    }

    public static Optional<List<ItemStack>> copyDrops(Player player, Collection<ItemEntity> drops, int multiplier) {
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof IMPGDoubling doublingItem)) {
            return Optional.empty();
        }
        int factor = doublingItem.isDoubling(weapon) ? multiplier : 1;
        List<ItemStack> copies = new ArrayList<>(drops.size());
        for (ItemEntity drop : drops) {
            ItemStack copy = drop.getItem().copy();
            long count = (long) copy.getCount() * factor;
            copy.setCount((int) Math.min(Integer.MAX_VALUE, count));
            copies.add(copy);
        }
        return Optional.of(copies);
    }

    public static OptionalInt redirectedExperience(Player player, int experience, int multiplier) {
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof IMPGDoubling doublingItem)) {
            return OptionalInt.empty();
        }
        int factor = doublingItem.isDoubling(weapon) ? multiplier : 1;
        long result = (long) experience * factor;
        return OptionalInt.of((int) Math.min(Integer.MAX_VALUE, result));
    }

    public static void resetPlayerDamageState(Player player) {
        player.setHealth(player.getMaxHealth());
        player.fallDistance = 0;
        player.hurtTime = 0;
        player.deathTime = 0;
    }
}
