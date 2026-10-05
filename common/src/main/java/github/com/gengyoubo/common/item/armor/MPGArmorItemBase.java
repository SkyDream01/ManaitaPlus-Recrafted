package github.com.gengyoubo.common.item.armor;

import github.com.gengyoubo.common.config.MPGConfigValues;
import github.com.gengyoubo.common.entity.MPGEntityData;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MPGArmorItemBase extends Item {
    // enchantmentValue 1 replaces the old zero: Enchantable now rejects non-positive
    // values, and 1 keeps isEnchantable() true as before.
    public static final ArmorMaterial MANAITA_ARMOR_MATERIAL = new ArmorMaterial(
            0,
            Map.of(
                    ArmorType.HELMET, 0,
                    ArmorType.CHESTPLATE, 0,
                    ArmorType.LEGGINGS, 0,
                    ArmorType.BOOTS, 0,
                    ArmorType.BODY, 0
            ),
            1,
            SoundEvents.ARMOR_EQUIP_TURTLE,
            0.0F,
            0.0F,
            // Repair used to be () -> Ingredient.EMPTY; the record wants a repair tag and
            // an unpopulated tag repairs nothing, exactly like before.
            TagKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath("manaita_plus_general", "manaita_armor_repairable")),
            ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(
                    "manaita_plus_general", "manaita_armor"))
    );

    private static final int FAST_REGENERATION_DURATION = 60;
    private static final int FAST_REGENERATION_AMPLIFIER = 4;
    private static final double[] BOOTS_JUMP_STRENGTH = {
            0.0D, 0.4635D, 0.6175D, 0.6850D, 0.8030D, 0.9095D, 1.0075D, 1.0985D, 1.1850D
    };
    private static final String HELMET_REGENERATION_TAG = "manaita_plus_general.helmet_regeneration";
    private static final String HELMET_WATER_BREATHING_TAG = "manaita_plus_general.helmet_water_breathing";
    private static final String HELMET_NIGHT_VISION_TAG = "manaita_plus_general.helmet_night_vision";
    private static final String CHESTPLATE_FLIGHT_TAG = "manaita_plus_general.chestplate_flight";
    private static final String CHESTPLATE_FALL_TAG = "manaita_plus_general.chestplate_fall";
    private static final String CHESTPLATE_BIG_FALL_TAG = "manaita_plus_general.chestplate_big_fall";
    private static final String LEGGINGS_INVISIBILITY_TAG = "manaita_plus_general.leggings_invisibility";
    private static final Identifier BOOTS_SPEED_MODIFIER_ID = Identifier.fromNamespaceAndPath(
            "manaita_plus_general", "boots_speed");
    private static final Identifier BOOTS_JUMP_MODIFIER_ID = Identifier.fromNamespaceAndPath(
            "manaita_plus_general", "boots_jump");

    protected MPGArmorItemBase(Item.Properties props, ArmorMaterial material, ArmorType type) {
        // humanoidArmor always wires durability components now; UNBREAKABLE keeps the old
        // "no damage components" never-breaks behaviour, with its vanilla tooltip line
        // hidden to keep the previous tooltip.
        super(props.fireResistant()
                .humanoidArmor(material, type)
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                .component(DataComponents.TOOLTIP_DISPLAY,
                        TooltipDisplay.DEFAULT.withHidden(DataComponents.UNBREAKABLE, true)));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.accept(Component.literal(MPText.manaita_infinity.formatting(text("info.armor"))));
    }

    public static void syncArmorState(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        if (!player.level().isClientSide()) {
            if (helmet.getItem() instanceof Helmet) {
                applyHelmetEffects(player);
            } else {
                removeHelmetEffects(player);
            }
            syncChestplate(player, chestplate);
            syncLeggings(player, leggings);
            syncBoots(player, boots);
        }
    }

    public static boolean hasManaitaHelmet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof Helmet;
    }

    public static boolean hasManaitaChestplate(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof Chestplate;
    }

    public static boolean hasManaitaLeggings(Player player) {
        return player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof Leggings;
    }

    public static boolean hasManaitaBoots(Player player) {
        return player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof Boots;
    }

    public static boolean hasManaitaArmorPiece(Player player) {
        return hasManaitaHelmet(player) || hasManaitaChestplate(player)
                || hasManaitaLeggings(player) || hasManaitaBoots(player);
    }

    public static boolean shouldCancelDamage(Player player, DamageSource source) {
        if (!hasManaitaArmorPiece(player)) {
            return false;
        }
        boolean onlyLeggings = hasManaitaLeggings(player)
                && !hasManaitaHelmet(player)
                && !hasManaitaChestplate(player)
                && !hasManaitaBoots(player);
        return !onlyLeggings || !source.is(DamageTypeTags.IS_FIRE) || !isTouchingFire(player);
    }

    private static void syncChestplate(Player player, ItemStack chestplate) {
        if (chestplate.getItem() instanceof Chestplate) {
            removeHarmfulEffects(player);
            syncChestplateLandingSound(player);
            if (!player.isCreative() && !player.isSpectator()) {
                player.addTag(CHESTPLATE_FLIGHT_TAG);
            }
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                updateAbilities(player);
            }
            return;
        }

        if (player.entityTags().contains(CHESTPLATE_FLIGHT_TAG)) {
            player.removeTag(CHESTPLATE_FLIGHT_TAG);
            if (!player.isCreative() && !player.isSpectator()
                    && !MPGEntityData.manaita.accept(player)) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                updateAbilities(player);
            }
        }
        player.removeTag(CHESTPLATE_FALL_TAG);
        player.removeTag(CHESTPLATE_BIG_FALL_TAG);
    }

    private static void syncChestplateLandingSound(Player player) {
        if (!player.onGround() && player.fallDistance > 3.0F) {
            player.addTag(CHESTPLATE_FALL_TAG);
            if (player.fallDistance > 7.0F) {
                player.addTag(CHESTPLATE_BIG_FALL_TAG);
            }
            return;
        }
        if (player.onGround() && player.entityTags().contains(CHESTPLATE_FALL_TAG)) {
            if (!hasManaitaBoots(player)) {
                boolean bigFall = player.entityTags().contains(CHESTPLATE_BIG_FALL_TAG);
                player.playSound(bigFall ? SoundEvents.GENERIC_BIG_FALL : SoundEvents.GENERIC_SMALL_FALL,
                        1.0F, 1.0F);
            }
            player.removeTag(CHESTPLATE_FALL_TAG);
            player.removeTag(CHESTPLATE_BIG_FALL_TAG);
        }
    }

    private static void removeHarmfulEffects(Player player) {
        List<Holder<MobEffect>> harmfulEffects = new ArrayList<>();
        for (MobEffectInstance effect : player.getActiveEffects()) {
            if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
                harmfulEffects.add(effect.getEffect());
            }
        }
        harmfulEffects.forEach(player::removeEffect);
    }

    private static void syncLeggings(Player player, ItemStack leggings) {
        if (!(leggings.getItem() instanceof Leggings)) {
            removeHelmetEffect(player, MobEffects.INVISIBILITY, LEGGINGS_INVISIBILITY_TAG);
            return;
        }

        if (!isTouchingFire(player)) {
            player.setRemainingFireTicks(0);
        }

        if (MPGConfigValues.leggings_invisibility_value) {
            addHelmetEffect(player, MobEffects.INVISIBILITY, MobEffectInstance.INFINITE_DURATION,
                    0, LEGGINGS_INVISIBILITY_TAG);
        } else {
            removeHelmetEffect(player, MobEffects.INVISIBILITY, LEGGINGS_INVISIBILITY_TAG);
        }
    }

    private static boolean isTouchingFire(Player player) {
        return player.isInLava()
                || player.level().getBlockState(player.blockPosition()).is(BlockTags.FIRE)
                || player.level().getBlockState(player.blockPosition().above()).is(BlockTags.FIRE);
    }

    private static void syncBoots(Player player, ItemStack boots) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance jumpAttribute = player.getAttribute(Attributes.JUMP_STRENGTH);
        if (!(boots.getItem() instanceof Boots bootsItem)) {
            if (speedAttribute != null) {
                speedAttribute.removeModifier(BOOTS_SPEED_MODIFIER_ID);
            }
            if (jumpAttribute != null) {
                jumpAttribute.removeModifier(BOOTS_JUMP_MODIFIER_ID);
            }
            return;
        }

        int speedLevel = bootsItem.getSpeed(boots);
        int jumpLevel = bootsItem.getJump(boots);
        if (speedAttribute != null) {
            speedAttribute.addOrUpdateTransientModifier(new AttributeModifier(
                    BOOTS_SPEED_MODIFIER_ID, speedLevel * 0.2D,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
        if (jumpAttribute != null) {
            double jumpIncrease = BOOTS_JUMP_STRENGTH[jumpLevel] - 0.42D;
            jumpAttribute.addOrUpdateTransientModifier(new AttributeModifier(
                    BOOTS_JUMP_MODIFIER_ID, jumpIncrease, AttributeModifier.Operation.ADD_VALUE));
        }

        if (MPGConfigValues.boots_auto_jump_value
                && player.onGround()
                && player.horizontalCollision
                && !player.isShiftKeyDown()
                && (Math.abs(player.xxa) > 0.01F || Math.abs(player.zza) > 0.01F)) {
            player.jumpFromGround();
        }
    }

    private static void updateAbilities(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.onUpdateAbilities();
        }
    }

    private static void applyHelmetEffects(Player player) {
        player.setAirSupply(player.getMaxAirSupply());

        FoodData foodData = player.getFoodData();
        foodData.setFoodLevel(20);
        foodData.setSaturation(20.0F);
        // FoodData.setExhaustion no longer exists; pinning food and saturation each sync
        // already keeps the player fed, only the exhaustion counter is no longer reset.

        addHelmetEffect(player, MobEffects.REGENERATION, FAST_REGENERATION_DURATION,
                FAST_REGENERATION_AMPLIFIER, HELMET_REGENERATION_TAG);
        addHelmetEffect(player, MobEffects.WATER_BREATHING, MobEffectInstance.INFINITE_DURATION,
                0, HELMET_WATER_BREATHING_TAG);

        if (MPGConfigValues.helmet_night_vision_value) {
            addHelmetEffect(player, MobEffects.NIGHT_VISION, MobEffectInstance.INFINITE_DURATION,
                    0, HELMET_NIGHT_VISION_TAG);
        } else {
            removeHelmetEffect(player, MobEffects.NIGHT_VISION, HELMET_NIGHT_VISION_TAG);
        }
    }

    private static void addHelmetEffect(Player player, Holder<MobEffect> effect, int duration,
                                        int amplifier, String ownerTag) {
        MobEffectInstance current = player.getEffect(effect);
        boolean needsRefresh = current == null
                || current.getAmplifier() < amplifier
                || duration == MobEffectInstance.INFINITE_DURATION && !current.isInfiniteDuration()
                || duration != MobEffectInstance.INFINITE_DURATION && current.getDuration() <= 20;
        if (needsRefresh) {
            player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
            player.addTag(ownerTag);
        }
    }

    private static void removeHelmetEffects(Player player) {
        removeHelmetEffect(player, MobEffects.REGENERATION, HELMET_REGENERATION_TAG);
        removeHelmetEffect(player, MobEffects.WATER_BREATHING, HELMET_WATER_BREATHING_TAG);
        removeHelmetEffect(player, MobEffects.NIGHT_VISION, HELMET_NIGHT_VISION_TAG);
    }

    private static void removeHelmetEffect(Player player, Holder<MobEffect> effect, String ownerTag) {
        if (player.entityTags().contains(ownerTag)) {
            player.removeEffect(effect);
            player.removeTag(ownerTag);
        }
    }

    public static class Helmet extends MPGArmorItemBase {
        public Helmet(Item.Properties props, ArmorMaterial material) {
            super(props, material, ArmorType.HELMET);
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                    @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip,
                                    @NotNull TooltipFlag flag) {
            tooltip.accept(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.nightvision") + ": "
                            + (MPGConfigValues.helmet_night_vision_value
                            ? text("info.on") : text("info.off")))));
            super.appendHoverText(stack, context, display, tooltip, flag);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.helmet.name");
        }

    }

    public static class Chestplate extends MPGArmorItemBase {
        public Chestplate(Item.Properties props, ArmorMaterial material) {
            super(props, material, ArmorType.CHESTPLATE);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.chestplate.name");
        }

    }

    public static class Leggings extends MPGArmorItemBase {
        public Leggings(Item.Properties props, ArmorMaterial material) {
            super(props, material, ArmorType.LEGGINGS);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.leggings.name");
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                    @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip,
                                    @NotNull TooltipFlag flag) {
            tooltip.accept(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.invisibility") + ": "
                            + (MPGConfigValues.leggings_invisibility_value
                            ? text("info.on") : text("info.off")))));
            super.appendHoverText(stack, context, display, tooltip, flag);
        }
    }

    public static class Boots extends MPGArmorItemBase implements IMPGKey {
        public Boots(Item.Properties props, ArmorMaterial material) {
            super(props, material, ArmorType.BOOTS);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.boots.name");
        }

        public static int getSpeed(ItemStack itemStack) {
            int storedLevel = MPGItemStackData.getInt(itemStack, "Speed");
            return clampLevel(storedLevel > 0 ? storedLevel : MPGConfigValues.boots_speed_level_value);
        }

        public static int getJump(ItemStack itemStack) {
            int storedLevel = MPGItemStackData.getInt(itemStack, "Jump");
            return clampLevel(storedLevel > 0 ? storedLevel : MPGConfigValues.boots_jump_level_value);
        }

        @Override
        public void onManaitaKeyPress(ItemStack itemStack) {
            MPGItemStackData.putInt(itemStack, "Speed", nextLevel(getSpeed(itemStack)));
            MPGItemStackData.putInt(itemStack, "Jump", nextLevel(getJump(itemStack)));
        }

        @Override
        public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
            onManaitaKeyPress(itemStack);
            Component message = Component.literal(MPText.manaita_mode.formatting(String.format(
                    "[%s] %s: %d, %s: %d", text("item.boots.name"),
                    text("mode.speed"), getSpeed(itemStack), text("mode.jump"), getJump(itemStack))));
            if (messageUsesOverlay()) {
                player.sendOverlayMessage(message);
            } else {
                player.sendSystemMessage(message);
            }
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                    @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip,
                                    @NotNull TooltipFlag flag) {
            tooltip.accept(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.autojump") + ": "
                            + (MPGConfigValues.boots_auto_jump_value ? text("info.on") : text("info.off")))));
            tooltip.accept(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.speed") + ": " + getSpeed(stack))));
            tooltip.accept(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.jump") + ": " + getJump(stack))));
            super.appendHoverText(stack, context, display, tooltip, flag);
        }

        private static int nextLevel(int level) {
            return level >= 8 ? 1 : level + 1;
        }

        private static int clampLevel(int level) {
            return Math.max(1, Math.min(8, level));
        }

        protected boolean messageUsesOverlay() {
            return true;
        }
    }

    private static String text(String translationKey) {
        return Component.translatable(translationKey).getString();
    }
}
