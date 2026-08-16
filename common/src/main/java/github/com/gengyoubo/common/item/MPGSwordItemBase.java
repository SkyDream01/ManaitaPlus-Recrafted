package github.com.gengyoubo.common.item;

import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Shared Manaita sword tier, stack data, text and key handling. */
public abstract class MPGSwordItemBase extends SwordItem implements IMPGKey, IMPGDoubling {
    private static final String SWEEP_KEY = "Sweep";

    protected MPGSwordItemBase() {
        super(new ItemManaitaSwordTier(), new Item.Properties().fireResistant());
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull Level level, @NotNull Entity entity,
                              int slot, boolean selected) {
        stack.setPopTime(0);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                translate("mode.manaita_sword") + ":" + getSweep(stack))));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal(MPText.manaita_infinity.formatting(translate("info.attack"))));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.literal(MPText.manaita_infinity.formatting(translate("item.manaita_sword.name")));
    }

    public static int getSweep(ItemStack stack) {
        return Math.max(1, MPGItemStackData.getInt(stack, SWEEP_KEY));
    }

    public static void setSweep(ItemStack stack, int sweep) {
        MPGItemStackData.putInt(stack, SWEEP_KEY, Math.max(1, sweep));
    }

    protected void performSweep(Player player, ItemStack stack) {
        Vec3 look = player.getLookAngle();
        for (int distance = 0; distance < getSweep(stack); distance++) {
            AABB area = player.getBoundingBox().expandTowards(3.0D, 3.0D, 3.0D)
                    .move(look.x * distance, look.y * distance, look.z * distance);
            for (Entity target : player.level().getEntities(player, area, ignored -> true)) {
                if (target instanceof LivingEntity living) {
                    if (!player.level().isClientSide) {
                        living.hurt(living.damageSources().playerAttack(player), Float.MAX_VALUE);
                        living.setHealth(Float.NaN);
                    }
                    for (int event = 0; event < 5; event++) {
                        living.handleEntityEvent((byte) 2);
                    }
                    for (int event = 47; event < 53; event++) {
                        living.handleEntityEvent((byte) event);
                    }
                    living.handleEntityEvent((byte) 3);
                }
            }

            double xOffset = -Mth.sin(player.getYRot() * Mth.DEG_TO_RAD);
            double zOffset = Mth.cos(player.getYRot() * Mth.DEG_TO_RAD);
            double x = player.getX() + xOffset + look.x * distance;
            double y = player.getY(0.5D) + look.y * distance;
            double z = player.getZ() + zOffset + look.z * distance;
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, x, y, z,
                        0, xOffset, 0.0D, zOffset, 0.0D);
            }
            player.level().playSound(null, x, y, z, SoundEvents.PLAYER_ATTACK_SWEEP,
                    player.getSoundSource(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onManaitaKeyPress(ItemStack stack) {
        setSweep(stack, (getSweep(stack) % 10) + 1);
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack stack, Player player) {
        onManaitaKeyPress(stack);
        String message = MPText.manaita_mode.formatting(String.format("[%s] %s: %d",
                translate("item.manaita_sword.name"), translate("mode.manaita_sword"), getSweep(stack)));
        player.displayClientMessage(Component.literal(message), keyMessageUsesOverlay());
    }

    protected boolean keyMessageUsesOverlay() {
        return false;
    }

    protected static String translate(String key) {
        return Component.translatable(key).getString();
    }

    public static final class ItemManaitaSwordTier implements Tier {
        @Override
        public int getUses() {
            return -1;
        }

        @Override
        public float getSpeed() {
            return Float.MAX_VALUE;
        }

        @Override
        public float getAttackDamageBonus() {
            return Float.MAX_VALUE;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Items.NETHERITE_INGOT);
        }

        @Override
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        }
    }
}
