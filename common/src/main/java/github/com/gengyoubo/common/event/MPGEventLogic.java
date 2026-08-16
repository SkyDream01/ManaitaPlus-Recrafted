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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import github.com.gengyoubo.common.item.MPGSwordItemBase;

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
        if (!doublingItem.shouldMultiplyDrops(weapon, player)) {
            return Optional.empty();
        }
        int factor = Math.max(1, multiplier);
        List<ItemStack> copies = new ArrayList<>(drops.size());
        for (ItemEntity drop : drops) {
            ItemStack copy = drop.getItem().copy();
            long count = (long) copy.getCount() * factor;
            copy.setCount((int) Math.min(Integer.MAX_VALUE, count));
            copies.add(copy);
        }
        return Optional.of(copies);
    }

    public static boolean multiplyDrops(Player player, Collection<ItemEntity> drops, int multiplier) {
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof IMPGDoubling doublingItem)
                || !doublingItem.shouldMultiplyDrops(weapon, player)) {
            return false;
        }
        int factor = Math.max(1, multiplier);
        for (ItemEntity drop : drops) {
            ItemStack stack = drop.getItem();
            long count = (long) stack.getCount() * factor;
            stack.setCount((int) Math.min(Integer.MAX_VALUE, count));
        }
        return true;
    }

    public static Optional<ItemStack> createBeheadingDrop(LivingEntity target, Player killer) {
        if (!(killer.getMainHandItem().getItem() instanceof MPGSwordItemBase)
                || target.getRandom().nextFloat() >= 0.10F) {
            return Optional.empty();
        }

        ItemStack head;
        if (target instanceof Player playerTarget) {
            head = new ItemStack(Items.PLAYER_HEAD);
            head.set(DataComponents.PROFILE, new ResolvableProfile(playerTarget.getGameProfile()));
        } else if (target.getType() == EntityType.WITHER_SKELETON) {
            head = new ItemStack(Items.WITHER_SKELETON_SKULL);
        } else if (target.getType() == EntityType.SKELETON) {
            head = new ItemStack(Items.SKELETON_SKULL);
        } else if (target.getType() == EntityType.ZOMBIE) {
            head = new ItemStack(Items.ZOMBIE_HEAD);
        } else if (target.getType() == EntityType.CREEPER) {
            head = new ItemStack(Items.CREEPER_HEAD);
        } else if (target.getType() == EntityType.PIGLIN || target.getType() == EntityType.PIGLIN_BRUTE) {
            head = new ItemStack(Items.PIGLIN_HEAD);
        } else if (target.getType() == EntityType.ENDER_DRAGON) {
            head = new ItemStack(Items.DRAGON_HEAD);
        } else {
            return Optional.empty();
        }
        return Optional.of(head);
    }

    public static void handleFabricDeathDrops(LivingEntity target, DamageSource source, int multiplier) {
        if (!(target.level() instanceof ServerLevel level)) {
            return;
        }
        findKiller(target, source.getEntity()).ifPresent(player -> {
            createBeheadingDrop(target, player).ifPresent(target::spawnAtLocation);
            List<ItemEntity> freshDrops = level.getEntitiesOfClass(ItemEntity.class,
                    target.getBoundingBox().inflate(2.0D), item -> item.tickCount <= 1);
            multiplyDrops(player, freshDrops, multiplier);
        });
    }

    public static OptionalInt redirectedExperience(Player player, int experience, int multiplier) {
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof IMPGDoubling doublingItem)) {
            return OptionalInt.empty();
        }
        if (!doublingItem.shouldMultiplyExperience(weapon, player)) {
            return OptionalInt.empty();
        }
        int factor = Math.max(1, multiplier);
        long result = (long) experience * factor;
        return OptionalInt.of((int) Math.min(Integer.MAX_VALUE, result));
    }

    public static void resetPlayerDamageState(Player player) {
        player.setHealth(player.getMaxHealth());
        resetPlayerFallState(player);
        player.hurtTime = 0;
        player.deathTime = 0;
    }

    public static void resetPlayerFallState(Player player) {
        player.fallDistance = 0;
    }
}
