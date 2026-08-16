package github.com.gengyoubo.common.item.armor;

import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MPGArmorItemBase extends ArmorItem {
    public static final Holder<ArmorMaterial> MANAITA_ARMOR_MATERIAL = Holder.direct(
            new ArmorMaterial(
                    Map.of(
                            Type.HELMET, 0,
                            Type.CHESTPLATE, 0,
                            Type.LEGGINGS, 0,
                            Type.BOOTS, 0,
                            Type.BODY, 0
                    ),
                    0,
                    SoundEvents.ARMOR_EQUIP_TURTLE,
                    () -> Ingredient.EMPTY,
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(
                            "manaita_plus_general", "manaita_armor"))),
                    0.0F,
                    0.0F
            )
    );

    private static final float DEFAULT_WALKING_SPEED = 0.1F;
    private static final float DEFAULT_FLYING_SPEED = 0.05F;

    protected MPGArmorItemBase(Holder<ArmorMaterial> material, Type type) {
        super(material, type, new Item.Properties().fireResistant());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(MPText.manaita_infinity.formatting(text("info.armor"))));
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

            Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED))
                    .setBaseValue(DEFAULT_WALKING_SPEED);
            player.getAbilities().setWalkingSpeed(DEFAULT_WALKING_SPEED);
            player.getAbilities().setFlyingSpeed(DEFAULT_FLYING_SPEED);

            if (abilityChanged && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.onUpdateAbilities();
            }
        }
    }

    public static class Helmet extends MPGArmorItemBase implements IMPGKey {
        public Helmet(Holder<ArmorMaterial> material) {
            super(material, Type.HELMET);
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                    List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.nightvision") + ": "
                            + (getNightVision(stack) ? text("info.on") : text("info.off")))));
            super.appendHoverText(stack, context, tooltip, flag);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.helmet.name");
        }

        @Override
        public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity,
                                  int slot, boolean selected) {
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
            showMessage(player, String.format("[%s] %s: %s",
                    text("item.helmet.name"), text("mode.nightvision"),
                    getNightVision(itemStack) ? text("info.on") : text("info.off")));
        }

        protected void showMessage(Player player, String message) {
            player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(message)),
                    messageUsesOverlay());
        }

        protected boolean messageUsesOverlay() {
            return true;
        }
    }

    public static class Chestplate extends MPGArmorItemBase {
        public Chestplate(Holder<ArmorMaterial> material) {
            super(material, Type.CHESTPLATE);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.chestplate.name");
        }

        @Override
        public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity,
                                  int slot, boolean selected) {
            if (slot == 2 && entity instanceof Player player) {
                List<Holder<MobEffect>> harmfulEffects = new ArrayList<>();
                for (MobEffectInstance effect : player.getActiveEffects()) {
                    if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                        harmfulEffects.add(effect.getEffect());
                    }
                }
                harmfulEffects.forEach(player::removeEffect);
            }
        }
    }

    public static class Leggings extends MPGArmorItemBase implements IMPGKey {
        public Leggings(Holder<ArmorMaterial> material) {
            super(material, Type.LEGGINGS);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.leggings.name");
        }

        @Override
        public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity,
                                  int slot, boolean selected) {
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
        public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                    List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.invisibility") + ": "
                            + (getInvisibility(stack) ? text("info.on") : text("info.off")))));
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
            player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(String.format(
                    "[%s] %s: %s", text("item.leggings.name"), text("mode.invisibility"),
                    getInvisibility(itemStack) ? text("info.on") : text("info.off")))), messageUsesOverlay());
        }

        protected boolean messageUsesOverlay() {
            return true;
        }
    }

    public static class Boots extends MPGArmorItemBase implements IMPGKey {
        public Boots(Holder<ArmorMaterial> material) {
            super(material, Type.BOOTS);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.boots.name");
        }

        @Override
        public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity,
                                  int slot, boolean selected) {
            if (slot == 0 && entity instanceof Player player) {
                if (!player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                }
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
            player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(String.format(
                    "[%s] %s: %d", text("item.boots.name"), text("mode.speed"), getSpeed(itemStack)))),
                    messageUsesOverlay());
        }

        protected boolean messageUsesOverlay() {
            return true;
        }
    }

    private static String text(String translationKey) {
        return Component.translatable(translationKey).getString();
    }
}
