package github.com.gengyoubo.item;

import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.item.tier.MPToolTier;
import github.com.gengyoubo.network.MPNetworking;
import github.com.gengyoubo.network.server.MPChangeEntityDataPacket;
import github.com.gengyoubo.util.MPEntityData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MPGodSwordItem extends SwordItem implements IMPGKey, IMPGDoubling {
    public MPGodSwordItem() {
        super(new MPToolTier(), new Item.Properties().fireResistant());
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (entity instanceof Player player) {
            player.getAbilities().mayfly = true;
            player.setHealth(player.getMaxHealth());
        }
        MPEntityData.manaita.add(entity);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (!level.isClientSide) {
            summonLightning(level, player.position());
            godKill(player, isRemove(itemStack), player.isShiftKeyDown());
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (attacker instanceof Player player && !player.level().isClientSide) {
            attack(target, player, isRemove(stack));
            godKill(player, isRemove(stack), player.isShiftKeyDown());
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(Component.translatable("mode.doubling").getString() + ":" + (isDoubling(stack) ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))));
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(Component.translatable("mode.remove.name").getString() + ":" + (isRemove(stack) ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal(MPText.manaita_enchantment.formatting(Component.translatable("info.item.manaita_sword_god.1").getString())));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.literal(MPText.manaita_infinity.formatting(Component.translatable("item.manaita_sword_god.name").getString()));
    }

    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public void onManaitaKeyPress(ItemStack itemStack) {
        toggleDoubling(itemStack);
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
        if (player.isShiftKeyDown()) {
            boolean remove = !isRemove(itemStack);
            setRemove(itemStack, remove);
            player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(String.format("[%s] %s: %s",
                    Component.translatable("item.manaita_sword_god.name").getString(),
                    Component.translatable("mode.remove.name").getString(),
                    remove ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))), true);
        } else {
            boolean doubling = toggleDoubling(itemStack);
            player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(String.format("[%s] %s: %s",
                    Component.translatable("item.manaita_sword_god.name").getString(),
                    Component.translatable("mode.doubling").getString(),
                    doubling ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))), true);
        }
    }

    public static boolean isRemove(ItemStack itemStack) {
        if (!github.com.gengyoubo.common.util.MPGItemStackData.hasTag(itemStack)) {
            return false;
        }
        return github.com.gengyoubo.common.util.MPGItemStackData.getTag(itemStack) != null && github.com.gengyoubo.common.util.MPGItemStackData.getTag(itemStack).getBoolean(MPGNBTData.Remove);
    }

    public static void setRemove(ItemStack itemStack, boolean remove) {
        github.com.gengyoubo.common.util.MPGItemStackData.putBoolean(itemStack, MPGNBTData.Remove, remove);
    }

    private static void godKill(Player player, boolean remove, boolean includeEverything) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        List<Entity> pickups = new ArrayList<>();
        for (Entity entity : serverLevel.getAllEntities()) {
            if (entity == player || !entity.isAlive()) {
                continue;
            }

            if (entity instanceof ItemEntity || entity instanceof ExperienceOrb) {
                pickups.add(entity);
                continue;
            }

            if (!includeEverything && entity.getType().getCategory() != MobCategory.MONSTER) {
                continue;
            }

            attack(entity, player, remove);
        }

        for (Entity entity : pickups) {
            if (entity instanceof ItemEntity itemEntity) {
                itemEntity.setNoPickUpDelay();
                itemEntity.playerTouch(player);
            } else if (entity instanceof ExperienceOrb experienceOrb) {
                player.takeXpDelay = 0;
                experienceOrb.playerTouch(player);
            }
        }
    }

    private static void attack(Entity target, Player player, boolean remove) {
        if (target == null || !target.isAlive()) {
            return;
        }

        MPEntityData.death.add(target);
        MPNetworking.sendToSameLevelPlayers(player.level(), new MPChangeEntityDataPacket(target.getId(), MPEntityData.death.getFlag()));

        if (remove) {
            MPEntityData.remove.add(target);
            MPNetworking.sendToSameLevelPlayers(player.level(), new MPChangeEntityDataPacket(target.getId(), MPEntityData.remove.getFlag()));
            target.discard();
            return;
        }

        if (target instanceof LivingEntity living) {
            living.hurt(living.damageSources().playerAttack(player), Float.MAX_VALUE);
            living.setHealth(0.0F);
            living.die(living.damageSources().generic());
        } else {
            target.discard();
        }
    }

    private static void summonLightning(Level level, Vec3 position) {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            LightningBolt bolt = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(level);
            if (bolt == null) {
                continue;
            }

            float angle = random.nextFloat() * 62.831852F;
            double distance = random.nextGaussian() * 100.0D;
            double x = Mth.sin(angle) * distance + position.x;
            double z = Mth.cos(angle) * distance + position.z;
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, (int) x, (int) z);

            bolt.setPos(x, y, z);
            level.addFreshEntity(bolt);
        }
    }
}

