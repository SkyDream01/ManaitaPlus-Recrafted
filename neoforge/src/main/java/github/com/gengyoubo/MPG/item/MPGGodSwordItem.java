package github.com.gengyoubo.MPG.item;

import net.minecraft.world.item.Item;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import github.com.gengyoubo.MPG.entity.MPGLightningBolt;
import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.MPG.item.tier.MPGToolTier;
import github.com.gengyoubo.MPG.network.Networking;
import github.com.gengyoubo.common.network.payload.MPGChangeEntityDataPayload;
import github.com.gengyoubo.common.entity.MPGEntityData;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import github.com.gengyoubo.MPG.util.MPUtils;

import java.util.Random;
import java.util.function.Consumer;

import static github.com.gengyoubo.MPG.core.MPGEntityCore.ManaitaLightningBolt;

// SwordItem is deleted in 26.3: the sword behaviour is now Item.Properties#sword below.
public class MPGGodSwordItem extends Item implements IMPGKey, IMPGDoubling {
    public static final IClientItemExtensions CLIENT_EXTENSIONS = new IClientItemExtensions() {
        @Nullable
        @Override
        public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
            if (entityLiving.getUsedItemHand() == hand && entityLiving.getUseItemRemainingTicks() > 0) {
                return HumanoidModel.ArmPose.BLOCK;
            }
            return null;
        }

        @Override
        public boolean applyForgeHandTransform(PoseStack poseStack, PlayerRenderState playerRenderState, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
            // The live LocalPlayer is gone from this hook in 26.3; the render state carries the
            // same using-item information the old checks read.
            AvatarRenderState avatarRenderState = playerRenderState.avatarRenderState;
            if (avatarRenderState != null && avatarRenderState.isUsingItem
                    && playerRenderState.firstPersonHandsAndItems.useItemRemainingTicks > 0
                    && avatarRenderState.useItemHand == (arm == HumanoidArm.LEFT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND)) {
                int side = arm == HumanoidArm.RIGHT ? 1 : -1;
                double f = Mth.sin(swingProcess * swingProcess * Mth.PI);
                double f1 = Mth.sin(Mth.sqrt(swingProcess) * Mth.PI);
                poseStack.translate(side * 0.56, -0.52 + equipProcess * -0.6, -0.72);
                poseStack.translate(side * -0.1414214, 0.08, 0.1414214);
                // PoseStack#mulPose(Axis#rotationDegrees) is now PoseStack#rotateDegrees.
                poseStack.rotateDegrees(Axis.XP, (float) (-102.25F - f1 * 80.0F));
                poseStack.rotateDegrees(Axis.YP, (float) (side * 13.365F - f * 20.0F));
                poseStack.rotateDegrees(Axis.ZP, (float) (side * 78.050003F - f1 * 20.0F));
                return true;
            }
            return false;
        }
    };

    public MPGGodSwordItem(Item.Properties props) {
        // 3.0F/-2.4F are the old SwordItem attack damage and swing speed baselines (the material's
        // max bonus still applies); the tier's uses = -1 durability keeps the sword unbreakable,
        // exactly like the old SwordItem(Tier, Properties) constructor.
        super(props.fireResistant()
                .sword(new MPGToolTier().material(), 3.0F, -2.4F));
    }




    @Override
    public boolean canPerformAction(@NotNull ItemInstance stack, @NotNull ItemAbility itemAbility) {
        // SwordItem used to answer yes for SWORD_SWEEP (DEFAULT_SWORD_ACTIONS); it is the only
        // surviving sword ability in 26.3.
        return itemAbility == ItemAbilities.SWORD_SWEEP;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        Networking.sendToSameLevelPlayers(player.level(), new MPGChangeEntityDataPayload(player.getId(), -MPGEntityData.death.getFlag()));
        return super.onDroppedByPlayer(item, player);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack p_41404_, @NotNull ServerLevel p_41405_, @NotNull Entity p_41406_, @Nullable EquipmentSlot p_41407_) {
        if (p_41406_ instanceof  Player player) {
            player.setHealth(player.getMaxHealth());
        }
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof Player player) {
            MPUtils.godKill(player,isRemove(stack),player.isShiftKeyDown());
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        MPUtils.attack(entity, player,isRemove(stack));
        return true;
    }

    @Override
    public @NotNull InteractionResult use(Level p_41432_, Player player, @NotNull InteractionHand p_41434_) {
        ItemStack itemstack = player.getItemInHand(p_41434_);
        player.startUsingItem(p_41434_);
        if (!p_41432_.isClientSide()) {
            Random random = new Random();
            Vec3 position = player.position();
            for (int i = 0; i < 100; i++) {
                // EntityType#create(Level) is gone in 26.3; TRIGGERED is the vanilla reason for
                // effect-spawned entities such as lightning.
                MPGLightningBolt bolt = ManaitaLightningBolt.get().create(p_41432_, EntitySpawnReason.TRIGGERED);
                if (bolt != null) {
                    float angle = random.nextFloat() * 62.831852F;
                    double distance = random.nextGaussian() * 100.0D;
                    double x = Mth.sin(angle) * distance + position.x;
                    double z = Mth.cos(angle) * distance + position.z;

                    int y = p_41432_.getHeight(Heightmap.Types.WORLD_SURFACE_WG, (int) x, (int) z);

                    bolt.setPos(x, y, z);
                    p_41432_.addFreshEntity(bolt);
                }
            }
        }
        MPUtils.godKill(player,isRemove(itemstack),player.isShiftKeyDown());
        // InteractionResultHolder is deleted in 26.3: pass maps to InteractionResult.PASS.
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack p_41421_, @NotNull TooltipContext context, @NotNull TooltipDisplay p_41422_, @NotNull Consumer<Component> p_41423_, @NotNull TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, context, p_41422_, p_41423_, p_41424_);
        p_41423_.accept(Component.literal(MPText.manaita_mode.formatting(I18n.get("mode.doubling") + ":" + (isDoubling(p_41421_) ? I18n.get("info.on") : I18n.get("info.off")))));
        p_41423_.accept(Component.literal(MPText.manaita_mode.formatting(I18n.get("mode.remove.name") + ":" + (isRemove(p_41421_) ? I18n.get("info.on") : I18n.get("info.off")))));
        p_41423_.accept(Component.empty());
        p_41423_.accept(Component.literal(MPText.manaita_enchantment.formatting(I18n.get("info.item.manaita_sword_god.1"))));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack p_41458_) {
        return Component.literal(MPText.manaita_infinity.formatting(I18n.get("item.manaita_sword_god.name")));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack p_41454_, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack p_41452_) {
        // UseAnim.CUSTOM is gone in 26.3: NONE means "no vanilla arm transform", while the custom
        // pose comes from CLIENT_EXTENSIONS (applyForgeHandTransform/getArmPose) as before.
        return ItemUseAnimation.NONE;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack p_41453_) {
        return true;
    }

    // Item#isEnchantable is gone in 26.3; Item.Properties#sword already wires the ENCHANTABLE
    // component (enchantment value 0), which keeps ItemStack#isEnchantable() returning true.

// --娉ㄩ噴鎺夋鏌?START (2026/4/24 23:35):
//    public void onManaitaKeyPress(ItemStack itemStack, Player player) {
//        if (player.isShiftKeyDown()) {
//            boolean remove = !isRemove(itemStack);
//            setRemove(itemStack, remove);
//        } else {
//            boolean doubling = !isDoubling(itemStack);
//            setDoubling(itemStack, doubling);
//        }
//    }
// --娉ㄩ噴鎺夋鏌?STOP (2026/4/24 23:35)

    @Override
    public void onManaitaKeyPress(ItemStack itemStack) {
        toggleDoubling(itemStack);
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
        if (player.isShiftKeyDown()) {
            boolean remove = !isRemove(itemStack);
            setRemove(itemStack, remove);
            MPUtils.chat(player, Component.literal(MPText.manaita_mode.formatting(String.format("[%s] %s: %s", I18n.get("item.manaita_sword_god.name"), I18n.get("mode.remove.name"), (remove ? I18n.get("info.on") : I18n.get("info.off"))))));
        } else {
            boolean doubling = toggleDoubling(itemStack);
            MPUtils.chat(player, Component.literal(MPText.manaita_mode.formatting(String.format("[%s] %s: %s", I18n.get("item.manaita_sword_god.name"), I18n.get("mode.doubling"), (doubling ? I18n.get("info.on") : I18n.get("info.off"))))));
        }
    }


    public static boolean isRemove(ItemStack itemStack) {
        return MPGItemStackData.getBoolean(itemStack, MPGNBTData.Remove);
    }

    public static void setRemove(ItemStack itemStack,boolean remove) {
        MPGItemStackData.putBoolean(itemStack, MPGNBTData.Remove, remove);
    }
}
