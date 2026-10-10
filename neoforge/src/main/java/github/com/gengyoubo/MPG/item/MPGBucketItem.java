// SPDX-License-Identifier: GPL-3.0-only
package github.com.gengyoubo.MPG.item;

import github.com.gengyoubo.common.item.data.IMPGOffhandKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/** Stores one kind of bucket content; sulfur cubes release individually and milk is drinkable. */
public class MPGBucketItem extends Item implements IMPGOffhandKey {
    public static final int CAPACITY = 10_000;
    public static final int MODE_COLLECT = 0;
    public static final int MODE_SINGLE = 1;
    public static final int MODE_BATCH = 2;
    public static final int MODE_DRINK = 3;
    private static final int MAX_RANGE = 11;
    private static final int MAX_SELECTION_SIDE = 32;

    private enum ContentType {
        EMPTY, FLUID, POWDER_SNOW, SULFUR_CUBE, MILK
    }

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
        // Buckets made before the three-mode update used a collect/release boolean.
        int storedMode = MPGItemStackData.contains(stack, MPGNBTData.BucketMode)
                ? MPGItemStackData.getInt(stack, MPGNBTData.BucketMode)
                : MPGItemStackData.getBoolean(stack, MPGNBTData.BucketRelease) ? MODE_SINGLE : MODE_COLLECT;
        ContentType content = contentType(stack);
        if (content == ContentType.MILK) {
            return storedMode == MODE_COLLECT ? MODE_COLLECT : MODE_DRINK;
        }
        int mode = Math.clamp(storedMode, MODE_COLLECT, MODE_BATCH);
        return mode == MODE_BATCH && content == ContentType.SULFUR_CUBE ? MODE_SINGLE : mode;
    }

    public static int amount(ItemStack stack) {
        return Math.clamp(MPGItemStackData.getInt(stack, MPGNBTData.BucketAmount), 0, CAPACITY);
    }

    /** Item-model state: empty, then partial/full water, lava, other fluids, snow, sulfur cubes and milk. */
    public static int visualState(ItemStack stack) {
        int amount = amount(stack);
        ContentType content = contentType(stack);
        if (content == ContentType.EMPTY) {
            return 0;
        }
        Fluid fluid = storedFluid(stack);
        int type = switch (content) {
            case POWDER_SNOW -> 7;
            case SULFUR_CUBE -> 9;
            case MILK -> 11;
            default -> fluid.isSame(Fluids.WATER) ? 1 : fluid.isSame(Fluids.LAVA) ? 3 : 5;
        };
        return type + (amount == CAPACITY ? 1 : 0);
    }

    private static ContentType contentType(ItemStack stack) {
        if (amount(stack) == 0) {
            return ContentType.EMPTY;
        }
        var tag = MPGItemStackData.getTag(stack);
        return switch (tag.getStringOr(MPGNBTData.BucketContent, "")) {
            case "powder_snow" -> ContentType.POWDER_SNOW;
            case "sulfur_cube" -> ContentType.SULFUR_CUBE;
            case "milk" -> ContentType.MILK;
            // Existing buckets only stored a fluid identifier and amount.
            default -> storedFluid(stack) == Fluids.EMPTY ? ContentType.EMPTY : ContentType.FLUID;
        };
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

    private static void setContents(ItemStack stack, ContentType content, Fluid fluid, int amount) {
        MPGItemStackData.editTag(stack, tag -> {
            if (amount == 0 || content != ContentType.FLUID) {
                tag.remove(MPGNBTData.BucketFluid);
            } else {
                tag.putString(MPGNBTData.BucketFluid, BuiltInRegistries.FLUID.getKey(fluid).toString());
            }
            if (amount == 0 || content == ContentType.FLUID) {
                tag.remove(MPGNBTData.BucketContent);
            } else {
                tag.putString(MPGNBTData.BucketContent, switch (content) {
                    case POWDER_SNOW -> "powder_snow";
                    case SULFUR_CUBE -> "sulfur_cube";
                    case MILK -> "milk";
                    default -> "";
                });
            }
            if (amount == 0) {
                tag.remove(MPGNBTData.BucketAmount);
            } else {
                tag.putInt(MPGNBTData.BucketAmount, amount);
            }
            if (content == ContentType.SULFUR_CUBE || content == ContentType.MILK) {
                if (content == ContentType.MILK) {
                    tag.putInt(MPGNBTData.BucketMode,
                            amount == 0 || tag.getIntOr(MPGNBTData.BucketMode, MODE_COLLECT) == MODE_COLLECT
                                    ? MODE_COLLECT : MODE_DRINK);
                } else if (tag.getIntOr(MPGNBTData.BucketMode, MODE_COLLECT) == MODE_BATCH) {
                    tag.putInt(MPGNBTData.BucketMode, MODE_SINGLE);
                }
                tag.remove(MPGNBTData.BucketCorner);
                tag.remove(MPGNBTData.BucketSecondCorner);
                tag.remove(MPGNBTData.BucketDimension);
            }
        });
        if (amount == 0 || content != ContentType.SULFUR_CUBE) {
            stack.remove(DataComponents.BUCKET_ENTITY_DATA);
        }
        if (amount > 0 && content == ContentType.MILK) {
            stack.set(DataComponents.CONSUMABLE, Consumables.MILK_BUCKET);
        } else {
            stack.remove(DataComponents.CONSUMABLE);
        }
    }

    @Override
    public void onManaitaKeyPress(ItemStack stack, Player player) {
        ContentType content = contentType(stack);
        if (player.isShiftKeyDown()) {
            int next = content == ContentType.MILK
                    ? (mode(stack) == MODE_COLLECT ? MODE_DRINK : MODE_COLLECT)
                    : (mode(stack) + 1) % (MODE_BATCH + 1);
            if (next == MODE_BATCH && content == ContentType.SULFUR_CUBE) {
                next = MODE_COLLECT;
            }
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
        ContentType content = contentType(stack);
        int size = range(stack);
        tooltip.accept(styledLine(MPText.manaita_mode, "mode.bucket_mode_message",
                Component.translatable(modeKey(mode(stack)))));
        tooltip.accept(styledLine(MPText.manaita_mode, "mode.bucket_range_message",
                size + "x" + size + "x" + size));
        if (content == ContentType.EMPTY) {
            tooltip.accept(styledLine(MPText.manaita_mode, "info.bucket_empty_contents", CAPACITY));
        } else {
            Component description = switch (content) {
                case POWDER_SNOW -> Blocks.POWDER_SNOW.getName();
                case SULFUR_CUBE -> EntityTypes.SULFUR_CUBE.getDescription();
                case MILK -> Component.translatable("info.bucket_milk_contents");
                default -> storedFluid(stack).getFluidType().getDescription();
            };
            tooltip.accept(styledLine(MPText.manaita_mode, "info.bucket_contents",
                    description, amount(stack), CAPACITY));
            if (content == ContentType.SULFUR_CUBE) {
                tooltip.accept(styledLine(MPText.manaita_enchantment, "info.bucket_sulfur_cube_single_only"));
            } else if (content == ContentType.MILK) {
                tooltip.accept(styledLine(MPText.manaita_enchantment, "info.bucket_milk_drink_only"));
            }
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
        if (content == ContentType.MILK) {
            tooltip.accept(styledLine(MPText.manaita_enchantment, mode(stack) == MODE_DRINK
                    ? "info.bucket_milk_drink_controls" : "info.bucket_milk_collect_controls"));
        } else if (mode(stack) == MODE_COLLECT) {
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
            case MODE_DRINK -> "mode.bucket_drink";
            default -> "mode.bucket_collect";
        };
    }

    public static BlockPos cornerPosition(Level level, Player player, BlockPos clicked,
                                          Direction face, ItemStack stack) {
        BlockState state = level.getBlockState(clicked);
        Fluid fluid = storedFluid(stack);
        ContentType content = contentType(stack);
        return state.isAir() || content == ContentType.POWDER_SNOW && state.canBeReplaced()
                || content == ContentType.FLUID && state.getBlock() instanceof LiquidBlockContainer container
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
        ContentType content = contentType(stack);
        if (content == ContentType.SULFUR_CUBE || content == ContentType.MILK) {
            return null;
        }
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
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
                                                  InteractionHand hand) {
        boolean milkAnimal = target instanceof AbstractCow || target instanceof Goat;
        if (!milkAnimal && !(target instanceof SulfurCube)) {
            return InteractionResult.PASS;
        }
        // Creative entity interactions pass a copy; contents must change on the actual held bucket.
        stack = player.getItemInHand(hand);
        if (!stack.is(this) || mode(stack) != MODE_COLLECT) {
            return InteractionResult.PASS;
        }
        if (milkAnimal) {
            return collectMilk(stack, player, (AgeableMob) target);
        }
        SulfurCube cube = (SulfurCube) target;
        int total = amount(stack);
        ContentType content = contentType(stack);
        if (!cube.isAlive() || cube.isBaby() || cube.isPrimed() || total >= CAPACITY
                || total > 0 && content != ContentType.SULFUR_CUBE) {
            return InteractionResult.FAIL;
        }
        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel level = (ServerLevel) player.level();
        BlockPos pos = cube.blockPosition();
        if (!canEdit(level, player, stack, pos)) {
            return InteractionResult.FAIL;
        }

        ItemStack bucket = cube.getBucketItemStack();
        cube.saveToBucketTag(bucket);
        var encoded = ItemStack.CODEC.encodeStart(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), bucket)
                .result().orElse(null);
        if (encoded == null) {
            return InteractionResult.FAIL;
        }
        // Keep the list in a separate component so tooltips and model state only copy small metadata.
        CompoundTag data = stack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY).copyTag();
        var entities = data.getListOrEmpty(MPGNBTData.BucketEntities);
        if (entities.size() != total) {
            return InteractionResult.FAIL;
        }
        entities.add(encoded);
        data.put(MPGNBTData.BucketEntities, entities);
        stack.set(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(data));
        setContents(stack, ContentType.SULFUR_CUBE, Fluids.EMPTY, total + 1);
        cube.dropLeash();
        cube.discard();
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, bucket);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        playTransferSound(level, pos, ContentType.SULFUR_CUBE, Fluids.EMPTY, false);
        level.gameEvent(player, GameEvent.ENTITY_INTERACT, pos);
        return InteractionResult.SUCCESS_SERVER;
    }

    private InteractionResult collectMilk(ItemStack stack, Player player, AgeableMob animal) {
        int total = amount(stack);
        if (!animal.isAlive() || animal.isBaby() || total >= CAPACITY
                || total > 0 && contentType(stack) != ContentType.MILK) {
            return InteractionResult.FAIL;
        }
        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel level = (ServerLevel) player.level();
        BlockPos pos = animal.blockPosition();
        if (!canEdit(level, player, stack, pos)) {
            return InteractionResult.FAIL;
        }
        int radius = range(stack) / 2;
        // Match block collection: the clicked animal's feet anchor the cube's top layer.
        AABB bounds = new AABB(pos.getX() - radius, pos.getY() - radius * 2, pos.getZ() - radius,
                pos.getX() + radius + 1, pos.getY() + 1, pos.getZ() + radius + 1);
        List<AgeableMob> animals = radius == 0 ? List.of(animal)
                : level.getEntitiesOfClass(AgeableMob.class, bounds, candidate ->
                        (candidate instanceof AbstractCow || candidate instanceof Goat)
                                && candidate.isAlive() && !candidate.isBaby()
                                && bounds.contains(candidate.position()));
        int changed = 0;
        for (AgeableMob candidate : animals) {
            if (total >= CAPACITY) {
                break;
            }
            BlockPos milkPos = candidate.blockPosition();
            if (!canEdit(level, player, stack, milkPos)) {
                continue;
            }
            total++;
            changed++;
            var sound = candidate instanceof Goat goat
                    ? (goat.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_MILK : SoundEvents.GOAT_MILK)
                    : SoundEvents.COW_MILK;
            level.playSound(null, milkPos, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.ENTITY_INTERACT, milkPos);
        }
        if (changed == 0) {
            return InteractionResult.FAIL;
        }
        setContents(stack, ContentType.MILK, Fluids.EMPTY, total);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (contentType(stack) == ContentType.MILK && mode(stack) == MODE_DRINK && !level.isClientSide()) {
            // Apply vanilla drinking effects to a copy, retaining the reusable bucket itself.
            Consumables.MILK_BUCKET.onConsume(level, entity, stack.copy());
            setContents(stack, ContentType.MILK, Fluids.EMPTY, amount(stack) - 1);
        }
        return stack;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ContentType content = contentType(stack);
        if (content == ContentType.MILK) {
            return mode(stack) == MODE_DRINK
                    ? Consumables.MILK_BUCKET.startConsuming(player, stack, hand) : InteractionResult.FAIL;
        }
        BlockHitResult hit = getPlayerPOVHitResult(level, player,
                mode(stack) != MODE_COLLECT && (content == ContentType.POWDER_SNOW || content == ContentType.SULFUR_CUBE)
                        ? ClipContext.Fluid.NONE : ClipContext.Fluid.SOURCE_ONLY);
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
                : content == ContentType.SULFUR_CUBE
                        ? releaseSulfurCube(serverLevel, player, stack, target)
                        : releaseRegion(serverLevel, player, stack, target, target);
        if (changed == 0) {
            return InteractionResult.FAIL;
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        Fluid sounded = currentMode == MODE_COLLECT ? storedFluid(stack) : fluid;
        playTransferSound(serverLevel, clicked,
                currentMode == MODE_COLLECT ? contentType(stack) : content, sounded, currentMode != MODE_COLLECT);
        return InteractionResult.SUCCESS_SERVER;
    }

    private void confirmSelection(ServerLevel level, Player player, ItemStack stack) {
        ContentType stored = contentType(stack);
        if (stored == ContentType.SULFUR_CUBE || stored == ContentType.MILK) {
            clearSelection(stack);
            showMessage(player, stack, stored == ContentType.MILK
                    ? "info.bucket_milk_drink_only" : "info.bucket_sulfur_cube_single_only");
            return;
        }
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
        ContentType content = contentType(stack);
        int changed = releaseRegion(level, player, stack, selected.first(), selected.second());
        if (changed == 0) {
            showMessage(player, stack, "info.bucket_nothing_released");
            return;
        }
        clearSelection(stack);
        player.awardStat(Stats.ITEM_USED.get(this));
        playTransferSound(level, selected.first(), content, fluid, true);
        showMessage(player, stack, "info.bucket_released", changed);
    }

    private static void playTransferSound(ServerLevel level, BlockPos pos, ContentType content,
                                          Fluid fluid, boolean release) {
        var sound = switch (content) {
            case POWDER_SNOW -> release ? SoundEvents.BUCKET_EMPTY_POWDER_SNOW : SoundEvents.BUCKET_FILL_POWDER_SNOW;
            case SULFUR_CUBE -> release ? SoundEvents.BUCKET_EMPTY_SULFUR_CUBE : SoundEvents.BUCKET_FILL_SULFUR_CUBE;
            default -> release
                    ? (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY)
                    : (fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL);
        };
        level.playSound(null, pos, sound,
                content == ContentType.SULFUR_CUBE ? SoundSource.NEUTRAL : SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static int collect(ServerLevel level, Player player, ItemStack stack, BlockPos topCenter) {
        int total = amount(stack);
        if (total >= CAPACITY) {
            return 0;
        }
        ContentType selectedContent = total == 0 ? collectableContent(level, topCenter) : contentType(stack);
        Fluid selected = total == 0 ? collectableFluid(level, topCenter) : storedFluid(stack);
        if (selectedContent == ContentType.SULFUR_CUBE || selectedContent == ContentType.MILK
                || total > 0 && selectedContent == ContentType.EMPTY) {
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
                    ContentType content = collectableContent(level, pos);
                    if (content == ContentType.EMPTY || selectedContent != ContentType.EMPTY && selectedContent != content
                            || content == ContentType.FLUID && selected != Fluids.EMPTY && !selected.isSame(fluid)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(pos);
                    ItemStack picked = ((BucketPickup) state.getBlock()).pickupBlock(player, level, pos, state);
                    if (picked.isEmpty()) {
                        continue;
                    }
                    if (selectedContent == ContentType.EMPTY) {
                        selectedContent = content;
                        selected = fluid;
                    }
                    total++;
                    changed++;
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
                }
            }
        }
        if (changed > 0) {
            setContents(stack, selectedContent, selected, total);
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

    private static ContentType collectableContent(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return ContentType.EMPTY;
        }
        if (level.getBlockState(pos).is(Blocks.POWDER_SNOW)) {
            return ContentType.POWDER_SNOW;
        }
        return collectableFluid(level, pos) == Fluids.EMPTY ? ContentType.EMPTY : ContentType.FLUID;
    }

    private static int releaseSulfurCube(ServerLevel level, Player player, ItemStack stack, BlockPos pos) {
        int total = amount(stack);
        if (total == 0 || contentType(stack) != ContentType.SULFUR_CUBE || mode(stack) != MODE_SINGLE
                || !canEdit(level, player, stack, pos)
                || !level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
            return 0;
        }
        CompoundTag data = stack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY).copyTag();
        var entities = data.getListOrEmpty(MPGNBTData.BucketEntities);
        if (entities.size() != total) {
            return 0;
        }
        ItemStack bucket = ItemStack.CODEC.parse(level.registryAccess().createSerializationContext(NbtOps.INSTANCE),
                entities.get(total - 1)).result().orElse(ItemStack.EMPTY);
        if (!bucket.is(Items.SULFUR_CUBE_BUCKET)) {
            return 0;
        }
        SulfurCube cube = EntityTypes.SULFUR_CUBE.create(level, EntityType.createDefaultStackConfig(level, bucket, player),
                pos, EntitySpawnReason.BUCKET, true, false);
        if (cube == null) {
            return 0;
        }
        cube.loadFromBucketTag(bucket.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY).copyTag());
        cube.setFromBucket(true);
        if (!level.addFreshEntity(cube)) {
            return 0;
        }
        entities.remove(total - 1);
        data.put(MPGNBTData.BucketEntities, entities);
        stack.set(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(data));
        setContents(stack, ContentType.SULFUR_CUBE, Fluids.EMPTY, total - 1);
        cube.playAmbientSound();
        level.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
        return 1;
    }

    private static int releaseRegion(ServerLevel level, Player player, ItemStack stack,
                                     BlockPos first, BlockPos second) {
        int total = amount(stack);
        ContentType content = contentType(stack);
        Fluid fluid = storedFluid(stack);
        FlowingFluid flowing = fluid instanceof FlowingFluid value ? value : null;
        if (total == 0 || content != ContentType.FLUID && content != ContentType.POWDER_SNOW
                || content == ContentType.FLUID && flowing == null) {
            return 0;
        }
        int changed = 0;
        // Place from the bottom upward, so every successful placement consumes exactly one unit.
        for (int y = Math.min(first.getY(), second.getY()); y <= Math.max(first.getY(), second.getY()) && total > 0; y++) {
            for (int x = Math.min(first.getX(), second.getX()); x <= Math.max(first.getX(), second.getX()) && total > 0; x++) {
                for (int z = Math.min(first.getZ(), second.getZ()); z <= Math.max(first.getZ(), second.getZ()) && total > 0; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!canEdit(level, player, stack, pos)) {
                        continue;
                    }
                    if (content == ContentType.FLUID && fluid.is(FluidTags.WATER)
                            && level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, pos)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(pos);
                    boolean placed = false;
                    if (content == ContentType.POWDER_SNOW) {
                        BlockState snow = Blocks.POWDER_SNOW.defaultBlockState();
                        if (state.canBeReplaced() && snow.canSurvive(level, pos)) {
                            placed = level.setBlock(pos, snow, 11);
                        }
                    } else if (state.isAir()) {
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
            setContents(stack, content, fluid, total);
        }
        return changed;
    }

    private static boolean canEdit(ServerLevel level, Player player, ItemStack stack, BlockPos pos) {
        return level.isLoaded(pos) && level.isInWorldBounds(pos) && level.mayInteract(player, pos)
                && player.mayUseItemAt(pos, Direction.UP, stack);
    }
}
