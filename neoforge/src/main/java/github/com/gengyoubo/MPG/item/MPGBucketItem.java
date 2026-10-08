// SPDX-License-Identifier: GPL-3.0-only
package github.com.gengyoubo.MPG.item;

import github.com.gengyoubo.common.item.data.IMPGOffhandKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/** A bucket that collects source blocks and releases them singly or in a selected region. */
public class MPGBucketItem extends Item implements IMPGOffhandKey {
    public static final int CAPACITY = 1000;
    public static final int MODE_COLLECT = 0;
    public static final int MODE_SINGLE = 1;
    public static final int MODE_BATCH = 2;
    private static final int MAX_RANGE = 11;
    private static final int MAX_SELECTION_SIDE = 32;

    public MPGBucketItem(Item.Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }

    @Override
    public Component getName(ItemStack stack) {
        return styledLine(MPText.manaita_infinity, "item.manaita_plus_recrafted.manaita_bucket");
    }

    public static int range(ItemStack stack) {
        int value = MPGItemStackData.getInt(stack, MPGNBTData.BucketRange);
        return value <= 0 ? 1 : Math.min(value | 1, MAX_RANGE);
    }

    public static int mode(ItemStack stack) {
        if (!MPGItemStackData.contains(stack, MPGNBTData.BucketMode)) {
            // Buckets made before the three-mode update used a collect/release boolean.
            return MPGItemStackData.getBoolean(stack, MPGNBTData.BucketRelease) ? MODE_SINGLE : MODE_COLLECT;
        }
        return Math.clamp(MPGItemStackData.getInt(stack, MPGNBTData.BucketMode), MODE_COLLECT, MODE_BATCH);
    }

    public static int amount(ItemStack stack) {
        return Math.clamp(MPGItemStackData.getInt(stack, MPGNBTData.BucketAmount), 0, CAPACITY);
    }

    /** Item-model state: empty, then partial/full water, lava and other bucketable fluids. */
    public static int visualState(ItemStack stack) {
        int amount = amount(stack);
        Fluid fluid = storedFluid(stack);
        if (amount == 0 || fluid == Fluids.EMPTY) {
            return 0;
        }
        int type = fluid.isSame(Fluids.WATER) ? 1 : fluid.isSame(Fluids.LAVA) ? 3 : 5;
        return type + (amount == CAPACITY ? 1 : 0);
    }

    private static Fluid storedFluid(ItemStack stack) {
        var tag = MPGItemStackData.getTag(stack);
        if (tag == null) {
            return Fluids.EMPTY;
        }
        Identifier id = Identifier.tryParse(tag.getStringOr(MPGNBTData.BucketFluid, ""));
        Fluid fluid = id == null ? null : BuiltInRegistries.FLUID.getValue(id);
        return fluid == null ? Fluids.EMPTY : fluid;
    }

    private static void setContents(ItemStack stack, Fluid fluid, int amount) {
        MPGItemStackData.editTag(stack, tag -> {
            if (amount == 0) {
                tag.remove(MPGNBTData.BucketFluid);
                tag.remove(MPGNBTData.BucketAmount);
            } else {
                tag.putString(MPGNBTData.BucketFluid, BuiltInRegistries.FLUID.getKey(fluid).toString());
                tag.putInt(MPGNBTData.BucketAmount, amount);
            }
        });
    }

    @Override
    public void onManaitaKeyPress(ItemStack stack, Player player) {
        if (player.isShiftKeyDown()) {
            int next = (mode(stack) + 1) % (MODE_BATCH + 1);
            MPGItemStackData.putInt(stack, MPGNBTData.BucketMode, next);
            if (next != MODE_BATCH) {
                clearSelection(stack);
            }
        } else if (mode(stack) == MODE_BATCH) {
            if (player.level() instanceof ServerLevel level) {
                confirmSelection(level, player, stack);
            }
        } else {
            int current = range(stack);
            MPGItemStackData.putInt(stack, MPGNBTData.BucketRange,
                    current >= MAX_RANGE ? 1 : current + 2);
        }
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack stack, Player player) {
        if (!player.isShiftKeyDown() && mode(stack) == MODE_BATCH) {
            return;
        }
        onManaitaKeyPress(stack, player);
        Component value = player.isShiftKeyDown()
                ? Component.translatable(modeKey(mode(stack)))
                : Component.literal(range(stack) + "x" + range(stack) + "x" + range(stack));
        showMessage(player, stack,
                player.isShiftKeyDown() ? "mode.bucket_mode_message" : "mode.bucket_range_message", value);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        int size = range(stack);
        tooltip.accept(styledLine(MPText.manaita_mode, "mode.bucket_mode_message",
                Component.translatable(modeKey(mode(stack)))));
        tooltip.accept(styledLine(MPText.manaita_mode, "mode.bucket_range_message",
                size + "x" + size + "x" + size));
        Fluid fluid = storedFluid(stack);
        if (amount(stack) == 0 || fluid == Fluids.EMPTY) {
            tooltip.accept(styledLine(MPText.manaita_mode, "info.bucket_empty_contents", CAPACITY));
        } else {
            tooltip.accept(styledLine(MPText.manaita_mode, "info.bucket_contents",
                    fluid.getFluidType().getDescription(), amount(stack), CAPACITY));
        }
        Selection selection = mode(stack) == MODE_BATCH ? selection(stack, null) : null;
        if (selection != null) {
            BlockPos corner = selection.first();
            tooltip.accept(styledLine(MPText.manaita_enchantment, "info.bucket_corner_set",
                    corner.getX(), corner.getY(), corner.getZ()));
            if (selection.second() != null) {
                tooltip.accept(selection.valid()
                        ? styledLine(MPText.manaita_enchantment, "info.bucket_preview_ready", selection.volume())
                        : Component.translatable("info.bucket_region_too_large", CAPACITY)
                                .withStyle(ChatFormatting.RED));
            }
        }
        tooltip.accept(Component.empty());
        tooltip.accept(styledLine(MPText.manaita_infinity, "info.bucket"));
        tooltip.accept(styledLine(MPText.manaita_enchantment, "info.bucket_controls"));
        if (mode(stack) == MODE_COLLECT) {
            tooltip.accept(styledLine(MPText.manaita_enchantment, "info.bucket_collect_controls"));
        } else if (mode(stack) == MODE_SINGLE) {
            tooltip.accept(styledLine(MPText.manaita_enchantment, "info.bucket_single_controls"));
        }
        if (mode(stack) == MODE_BATCH) {
            tooltip.accept(styledLine(MPText.manaita_enchantment, "info.bucket_batch_controls"));
            tooltip.accept(styledLine(MPText.manaita_enchantment, "info.bucket_batch_adjust"));
        }
    }

    private static Component styledLine(MPText style, String key, Object... args) {
        return Component.literal(style.formatting(Component.translatable(key, args).getString()));
    }

    private static void showMessage(Player player, ItemStack stack, String key, Object... args) {
        Component message = Component.translatable(key, args);
        String prefix = stack.getDisplayName().getString() + " ";
        if (player.level().isClientSide()) {
            player.sendSystemMessage(Component.literal(MPText.manaita_mode.formatting(prefix + message.getString())));
        } else {
            player.sendSystemMessage(Component.literal(MPText.manaita_mode.formatting(prefix))
                    .append(message.copy().withStyle(ChatFormatting.GOLD)));
        }
    }

    private static String modeKey(int mode) {
        return switch (mode) {
            case MODE_SINGLE -> "mode.bucket_release_single";
            case MODE_BATCH -> "mode.bucket_release_batch";
            default -> "mode.bucket_collect";
        };
    }

    public static BlockPos cornerPosition(Level level, Player player, BlockPos clicked,
                                          Direction face, ItemStack stack) {
        BlockState state = level.getBlockState(clicked);
        Fluid fluid = storedFluid(stack);
        return state.isAir() || state.getBlock() instanceof LiquidBlockContainer container
                && container.canPlaceLiquid(player, level, clicked, state, fluid)
                ? clicked : clicked.relative(face);
    }

    public static @Nullable ItemStack activeBatchStack(Player player) {
        ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof MPGBucketItem && mode(main) == MODE_BATCH) {
            return main;
        }
        ItemStack offhand = player.getOffhandItem();
        return offhand.getItem() instanceof MPGBucketItem && mode(offhand) == MODE_BATCH ? offhand : null;
    }

    public static void selectFirstCorner(Player player, BlockPos pos) {
        ItemStack stack = activeBatchStack(player);
        Level level = player.level();
        if (stack == null
                || !level.isLoaded(pos) || !level.isInWorldBounds(pos)
                || !player.isWithinBlockInteractionRange(pos, 1.5)
                || !level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, Direction.UP, stack)) {
            return;
        }
        MPGItemStackData.editTag(stack, tag -> {
            if (!level.dimension().identifier().toString().equals(
                    tag.getStringOr(MPGNBTData.BucketDimension, ""))) {
                tag.remove(MPGNBTData.BucketSecondCorner);
            }
            tag.putLong(MPGNBTData.BucketCorner, pos.asLong());
            tag.putString(MPGNBTData.BucketDimension, level.dimension().identifier().toString());
        });
        showMessage(player, stack, "info.bucket_corner_set", pos.getX(), pos.getY(), pos.getZ());
    }

    public record Selection(BlockPos first, @Nullable BlockPos second, long volume, boolean valid) {
    }

    public static @Nullable Selection selection(ItemStack stack, @Nullable Level level) {
        var tag = MPGItemStackData.getTag(stack);
        if (tag == null || !tag.contains(MPGNBTData.BucketCorner)) {
            return null;
        }
        if (level != null && !level.dimension().identifier().toString().equals(
                tag.getStringOr(MPGNBTData.BucketDimension, ""))) {
            return null;
        }
        BlockPos first = BlockPos.of(tag.getLongOr(MPGNBTData.BucketCorner, 0L));
        BlockPos second = tag.contains(MPGNBTData.BucketSecondCorner)
                ? BlockPos.of(tag.getLongOr(MPGNBTData.BucketSecondCorner, 0L)) : null;
        long width = second == null ? 0 : Math.abs((long) first.getX() - second.getX()) + 1;
        long height = second == null ? 0 : Math.abs((long) first.getY() - second.getY()) + 1;
        long depth = second == null ? 0 : Math.abs((long) first.getZ() - second.getZ()) + 1;
        boolean valid = second != null && width <= MAX_SELECTION_SIDE && height <= MAX_SELECTION_SIDE
                && depth <= MAX_SELECTION_SIDE && width * height * depth <= CAPACITY;
        long volume = valid ? width * height * depth : 0;
        return new Selection(first, second, volume, valid);
    }

    private static void clearSelection(ItemStack stack) {
        if (MPGItemStackData.contains(stack, MPGNBTData.BucketCorner)) {
            MPGItemStackData.editTag(stack, tag -> {
                tag.remove(MPGNBTData.BucketCorner);
                tag.remove(MPGNBTData.BucketSecondCorner);
                tag.remove(MPGNBTData.BucketDimension);
            });
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }
        BlockPos clicked = hit.getBlockPos();
        if (!level.mayInteract(player, clicked) || !player.mayUseItemAt(clicked, hit.getDirection(), stack)) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        int currentMode = mode(stack);
        Fluid fluid = storedFluid(stack);
        BlockPos target = currentMode == MODE_COLLECT ? clicked
                : cornerPosition(level, player, clicked, hit.getDirection(), stack);
        if (currentMode == MODE_BATCH) {
            Selection current = selection(stack, level);
            if (current == null) {
                clearSelection(stack);
                showMessage(player, stack, "info.bucket_corner_missing");
                return InteractionResult.FAIL;
            }
            MPGItemStackData.editTag(stack, tag -> tag.putLong(MPGNBTData.BucketSecondCorner, target.asLong()));
            Selection updated = selection(stack, level);
            showMessage(player, stack, updated.valid()
                    ? "info.bucket_preview_ready" : "info.bucket_region_too_large",
                    updated.valid() ? updated.volume() : CAPACITY);
            return InteractionResult.SUCCESS_SERVER;
        }
        int changed = currentMode == MODE_COLLECT
                ? collect(serverLevel, player, stack, target)
                : releaseRegion(serverLevel, player, stack, target, target);
        if (changed == 0) {
            return InteractionResult.FAIL;
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        Fluid sounded = currentMode == MODE_COLLECT ? storedFluid(stack) : fluid;
        playTransferSound(serverLevel, clicked, sounded, currentMode != MODE_COLLECT);
        return InteractionResult.SUCCESS_SERVER;
    }

    private void confirmSelection(ServerLevel level, Player player, ItemStack stack) {
        Selection selected = selection(stack, level);
        if (selected == null) {
            clearSelection(stack);
            showMessage(player, stack, "info.bucket_corner_missing");
            return;
        }
        if (selected.second() == null) {
            showMessage(player, stack, "info.bucket_corner_second_missing");
            return;
        }
        if (!selected.valid()) {
            showMessage(player, stack, "info.bucket_region_too_large", CAPACITY);
            return;
        }
        Fluid fluid = storedFluid(stack);
        int changed = releaseRegion(level, player, stack, selected.first(), selected.second());
        if (changed == 0) {
            showMessage(player, stack, "info.bucket_nothing_released");
            return;
        }
        clearSelection(stack);
        player.awardStat(Stats.ITEM_USED.get(this));
        playTransferSound(level, selected.first(), fluid, true);
        showMessage(player, stack, "info.bucket_released", changed);
    }

    private static void playTransferSound(ServerLevel level, BlockPos pos, Fluid fluid, boolean release) {
        level.playSound(null, pos,
                release
                        ? (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY)
                        : (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL),
                SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static int collect(ServerLevel level, Player player, ItemStack stack, BlockPos topCenter) {
        int total = amount(stack);
        if (total >= CAPACITY) {
            return 0;
        }
        Fluid selected = total == 0 ? collectableFluid(level, topCenter) : storedFluid(stack);
        if (total > 0 && selected == Fluids.EMPTY) {
            return 0;
        }
        int changed = 0;
        int radius = range(stack) / 2;
        for (int y = topCenter.getY(); y >= topCenter.getY() - radius * 2 && total < CAPACITY; y--) {
            for (int x = topCenter.getX() - radius; x <= topCenter.getX() + radius && total < CAPACITY; x++) {
                for (int z = topCenter.getZ() - radius; z <= topCenter.getZ() + radius && total < CAPACITY; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!canEdit(level, player, stack, pos)) {
                        continue;
                    }
                    Fluid fluid = collectableFluid(level, pos);
                    if (fluid == Fluids.EMPTY || selected != Fluids.EMPTY && !selected.isSame(fluid)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(pos);
                    ItemStack picked = ((BucketPickup) state.getBlock()).pickupBlock(player, level, pos, state);
                    if (picked.isEmpty()) {
                        continue;
                    }
                    if (selected == Fluids.EMPTY) {
                        selected = fluid;
                    }
                    total++;
                    changed++;
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
                }
            }
        }
        if (changed > 0) {
            setContents(stack, selected, total);
        }
        return changed;
    }

    private static Fluid collectableFluid(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return Fluids.EMPTY;
        }
        BlockState state = level.getBlockState(pos);
        FluidState fluidState = state.getFluidState();
        Fluid fluid = fluidState.getType();
        return fluidState.isSource() && fluid.getBucket() instanceof BucketItem
                && state.getBlock() instanceof BucketPickup ? fluid : Fluids.EMPTY;
    }

    private static int releaseRegion(ServerLevel level, Player player, ItemStack stack,
                                     BlockPos first, BlockPos second) {
        int total = amount(stack);
        Fluid fluid = storedFluid(stack);
        if (total == 0 || !(fluid instanceof FlowingFluid flowing)) {
            return 0;
        }
        int changed = 0;
        // Place from the bottom upward, so every successful source consumes exactly one unit.
        for (int y = Math.min(first.getY(), second.getY()); y <= Math.max(first.getY(), second.getY()) && total > 0; y++) {
            for (int x = Math.min(first.getX(), second.getX()); x <= Math.max(first.getX(), second.getX()) && total > 0; x++) {
                for (int z = Math.min(first.getZ(), second.getZ()); z <= Math.max(first.getZ(), second.getZ()) && total > 0; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!canEdit(level, player, stack, pos)) {
                        continue;
                    }
                    if (fluid.is(FluidTags.WATER)
                            && level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(pos);
                    boolean placed = false;
                    if (state.isAir()) {
                        placed = level.setBlock(pos, flowing.getSource(false).createLegacyBlock(), 11);
                    } else if (state.getBlock() instanceof LiquidBlockContainer container
                            && container.canPlaceLiquid(player, level, pos, state, fluid)) {
                        container.placeLiquid(level, pos, state, flowing.getSource(false));
                        placed = level.getFluidState(pos).isSourceOfType(fluid);
                    }
                    if (placed) {
                        total--;
                        changed++;
                        level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
                    }
                }
            }
        }
        if (changed > 0) {
            setContents(stack, fluid, total);
        }
        return changed;
    }

    private static boolean canEdit(ServerLevel level, Player player, ItemStack stack, BlockPos pos) {
        return level.isLoaded(pos) && level.isInWorldBounds(pos) && level.mayInteract(player, pos)
                && player.mayUseItemAt(pos, Direction.UP, stack);
    }
}
