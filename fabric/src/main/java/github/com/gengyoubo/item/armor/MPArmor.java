package github.com.gengyoubo.item.armor;

import com.google.common.collect.Lists;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class MPArmor extends ArmorItem {
    private static final float DEFAULT_WALKING_SPEED = 0.1F;
    private static final float DEFAULT_FLYING_SPEED = 0.05F;

    protected MPArmor(Type type) {
        super(ArmorMaterials.NETHERITE, type, new Item.Properties().fireResistant());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(MPText.manaita_infinity.formatting(Component.translatable("info.armor").getString())));
    }

    public static void syncArmorState(Player player) {
        ItemStack helmet = player.getInventory().armor.get(3);
        ItemStack leggings = player.getInventory().armor.get(1);
        ItemStack boots = player.getInventory().armor.get(0);

        if (!(helmet.getItem() instanceof Helmet) && player.hasEffect(MobEffects.NIGHT_VISION)) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }

        if (!(leggings.getItem() instanceof Leggings) && !player.hasEffect(MobEffects.INVISIBILITY)) {
            player.setInvisible(false);
        }

        if (!(boots.getItem() instanceof Boots)) {
            boolean abilityChanged = false;
            if (!player.isCreative() && !player.isSpectator()) {
                if (player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = false;
                    abilityChanged = true;
                }
                if (player.getAbilities().flying) {
                    player.getAbilities().flying = false;
                    abilityChanged = true;
                }
            }

            Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(DEFAULT_WALKING_SPEED);
            player.getAbilities().setWalkingSpeed(DEFAULT_WALKING_SPEED);
            player.getAbilities().setFlyingSpeed(DEFAULT_FLYING_SPEED);

            if (abilityChanged && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.onUpdateAbilities();
            }
        }
    }

    public static class Helmet extends MPArmor implements IMPGKey {
        public Helmet() {
            super(Type.HELMET);
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.literal(
                    MPText.manaita_mode.formatting(
                            Component.translatable("mode.nightvision").getString() + ": " + (getNightVision(stack) ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))));
            super.appendHoverText(stack, context, tooltip, flag);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.helmet.name");
        }

        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_1.png";
        }

        @Override
        public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
            if (slot == 3 && entity instanceof Player player) {
                player.setAirSupply(300);
                FoodData foodData = player.getFoodData();
                if (foodData.getFoodLevel() < 20) {
                    foodData.setFoodLevel(20);
                }
                if (foodData.getSaturationLevel() < 20) {
                    foodData.setSaturation(20);
                }
                foodData.setExhaustion(0);
                if (getNightVision(stack)) {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false));
                }
            }
        }

        public static boolean getNightVision(ItemStack itemStack) {
            return MPGItemStackData.getBoolean(itemStack, "NightVision");
        }

        @Override
        public void onManaitaKeyPress(ItemStack itemStack) {
            MPGItemStackData.putBoolean(itemStack, "NightVision", !getNightVision(itemStack));
        }

        @Override
        public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
            onManaitaKeyPress(itemStack);
            player.displayClientMessage(Component.literal(
                    MPText.manaita_mode.formatting(String.format("[%s] %s: %s",
                            Component.translatable("item.helmet.name").getString(), Component.translatable("mode.nightvision").getString(),
                            getNightVision(itemStack) ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))), true);
        }
    }

    public static class Chestplate extends MPArmor {
        public Chestplate() {
            super(Type.CHESTPLATE);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.chestplate.name");
        }

        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_1.png";
        }

        @Override
        public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
            if (slot == 2 && entity instanceof Player player) {
                List<Holder<MobEffect>> badEffects = Lists.newArrayList();
                for (MobEffectInstance effect : player.getActiveEffects()) {
                    if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                        badEffects.add(effect.getEffect());
                    }
                }
                for (Holder<MobEffect> effect : badEffects) {
                    player.removeEffect(effect);
                }
            }
        }
    }

    public static class Leggings extends MPArmor implements IMPGKey {
        public Leggings() {
            super(Type.LEGGINGS);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.leggings.name");
        }

        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_2.png";
        }

        @Override
        public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
            if (slot == 1 && entity instanceof Player player) {
                player.setRemainingFireTicks(0);
                if (getInvisibility(stack)) {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 400, 0, false, false));
                    player.setInvisible(true);
                } else {
                    player.setInvisible(false);
                }
            }
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.literal(
                    MPText.manaita_mode.formatting(
                            Component.translatable("mode.invisibility").getString() + ": " + (getInvisibility(stack) ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))));
            super.appendHoverText(stack, context, tooltip, flag);
        }

        public static boolean getInvisibility(ItemStack itemStack) {
            return MPGItemStackData.getBoolean(itemStack, "Invisibility");
        }

        @Override
        public void onManaitaKeyPress(ItemStack itemStack) {
            MPGItemStackData.putBoolean(itemStack, "Invisibility", !getInvisibility(itemStack));
        }

        @Override
        public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
            onManaitaKeyPress(itemStack);
            player.displayClientMessage(Component.literal(
                    MPText.manaita_mode.formatting(String.format("[%s] %s: %s",
                            Component.translatable("item.leggings.name").getString(), Component.translatable("mode.invisibility").getString(),
                            getInvisibility(itemStack) ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))), true);
        }
    }

    public static class Boots extends MPArmor implements IMPGKey {
        public Boots() {
            super(Type.BOOTS);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.boots.name");
        }

        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_2.png";
        }

        @Override
        public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
            if (slot == 0 && entity instanceof Player player) {
                player.getAbilities().mayfly = true;
                int speed = getSpeed(stack);
                float baseSpeed = 0.1F * speed;
                Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(baseSpeed);
                player.getAbilities().setWalkingSpeed(baseSpeed);
                player.getAbilities().setFlyingSpeed(baseSpeed / 2.0F);
            }
        }

        public static int getSpeed(ItemStack itemStack) {
            return Math.max(MPGItemStackData.getInt(itemStack, "Speed"), 1);
        }

        @Override
        public void onManaitaKeyPress(ItemStack itemStack) {
            int next = Math.max(1, MPGItemStackData.getInt(itemStack, "Speed") + 1) % 10;
            MPGItemStackData.putInt(itemStack, "Speed", next == 0 ? 1 : next);
        }

        @Override
        public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
            onManaitaKeyPress(itemStack);
            player.displayClientMessage(Component.literal(
                    MPText.manaita_mode.formatting(String.format("[%s] %s: %d",
                            Component.translatable("item.boots.name").getString(), Component.translatable("mode.speed").getString(), getSpeed(itemStack)))), true);
        }
    }
}
