package github.com.gengyoubo.common.block.entity;

import com.mojang.serialization.Codec;
import github.com.gengyoubo.common.config.MPGConfigValues;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/** Shared unlimited-furnace inventory, processing, persistence and experience behavior. */
public abstract class MPGFurnaceBlockEntityBase extends AbstractFurnaceBlockEntity {
    private static final int[] SLOTS_FOR_UP = {0};
    private static final int[] SLOTS_FOR_DOWN = {2, 1};
    private static final int[] SLOTS_FOR_SIDES = {1};
    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC =
            Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);

    private final Object2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Object2IntOpenHashMap<>();
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;

    protected MPGFurnaceBlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, RecipeType.SMELTING);
        quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.furnace_manaita");
    }

    @Override
    public int getMaxStackSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        recipesUsed.clear();
        input.read("RecipesUsed", RECIPES_USED_CODEC).ifPresent(recipesUsed::putAll);
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.store("RecipesUsed", RECIPES_USED_CODEC, recipesUsed);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MPGFurnaceBlockEntityBase entity) {
        if (level instanceof ServerLevel serverLevel && entity.processAll(serverLevel)) {
            setChanged(level, pos, state);
        }
    }

    protected final boolean processAll(ServerLevel level) {
        boolean changed = false;
        ItemStack input = items.get(0);
        if (!input.isEmpty()) {
            RecipeHolder<? extends AbstractCookingRecipe> recipe = quickCheck
                    .getRecipeFor(new SingleRecipeInput(input), level)
                    .orElse(null);
            while (canBurn(recipe)) {
                burn(recipe);
                setRecipeUsed(recipe);
                changed = true;
            }
        }
        return changed;
    }

    private boolean canBurn(@Nullable RecipeHolder<?> recipe) {
        ItemStack input = items.get(0);
        if (input.isEmpty()) {
            return false;
        }
        ItemStack assembled = MPGFurnaceLogic.assemble(recipe, input);
        if (assembled.isEmpty()) {
            return false;
        }
        ItemStack output = items.get(2);
        if (!output.isEmpty() && !ItemStack.isSameItemSameComponents(output, assembled)) {
            return false;
        }
        long added = (long) assembled.getCount() * MPGConfigValues.furnace_doubling_value;
        return added <= Integer.MAX_VALUE
                && (output.isEmpty() || (long) output.getCount() + added <= Integer.MAX_VALUE);
    }

    private void burn(RecipeHolder<?> recipe) {
        ItemStack result = MPGFurnaceLogic.assemble(recipe, items.get(0));
        int resultCount = Math.multiplyExact(result.getCount(), MPGConfigValues.furnace_doubling_value);
        ItemStack output = items.get(2);
        if (output.isEmpty()) {
            ItemStack copy = result.copy();
            copy.setCount(resultCount);
            items.set(2, copy);
        } else {
            output.grow(resultCount);
        }
        items.get(0).shrink(1);
    }

    @Override
    protected int getBurnDuration(@NotNull ServerLevel level, @NotNull ItemStack stack) {
        return 0;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction direction) {
        return direction == Direction.DOWN ? SLOTS_FOR_DOWN : direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction direction) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack current = items.get(slot);
        boolean same = !stack.isEmpty() && ItemStack.isSameItemSameComponents(current, stack);
        items.set(slot, stack);
        if (slot == 0 && !same) {
            setChanged();
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return slot != 2;
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            recipesUsed.addTo(recipe.id(), 1);
        }
    }

    @Override
    public void awardUsedRecipesAndPopExperience(@NotNull ServerPlayer player) {
        MPGFurnaceLogic.awardRecipesAndExperience(player, items, recipesUsed);
    }

    @Override
    public @NotNull List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(@NotNull ServerLevel level,
                                                                            @NotNull Vec3 pos) {
        return MPGFurnaceLogic.recipesAndExperience(level, pos, recipesUsed);
    }
}
