package github.com.gengyoubo.common.block.entity;

import github.com.gengyoubo.common.block.MPGBrewingStandBlockBase;
import github.com.gengyoubo.common.config.MPGConfigValues;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BrewingInput;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

/** Shared brewing inventory, processing, persistence and automation behavior. */
public abstract class MPGBrewingStandBlockEntityBase extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int BREW_TIME = 1;
    private static final int INGREDIENT_SLOT = 3;
    private static final int FUEL_SLOT = 4;
    private static final int[] SLOTS_FOR_UP = {INGREDIENT_SLOT};
    private static final int[] SLOTS_FOR_DOWN = {0, 1, 2, INGREDIENT_SLOT};
    private static final int[] SLOTS_FOR_SIDES = {0, 1, 2, FUEL_SLOT};

    protected NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    protected int brewTime;
    protected int fuel;
    @Nullable
    protected Item ingredient;
    private boolean[] lastPotionCount;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> brewTime;
                case 1 -> fuel;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> brewTime = value;
                case 1 -> fuel = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    protected MPGBrewingStandBlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.brewing_manaita");
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  MPGBrewingStandBlockEntityBase entity) {
        ItemStack fuelStack = entity.items.get(FUEL_SLOT);
        if (entity.fuel <= 0 && fuelStack.is(Items.BLAZE_POWDER)) {
            entity.fuel = 20;
            fuelStack.shrink(1);
            setChanged(level, pos, state);
        }

        if (level instanceof ServerLevel serverLevel) {
            boolean brewable = isBrewable(serverLevel, entity.items);
            ItemStack ingredientStack = entity.items.get(INGREDIENT_SLOT);
            if (entity.brewTime > 0) {
                entity.brewTime--;
                if (entity.brewTime == 0 && brewable) {
                    entity.performBrew(serverLevel, pos.getX(), pos.getY(), pos.getZ());
                } else if (!brewable || !ingredientStack.is(entity.ingredient)) {
                    entity.brewTime = 0;
                }
                setChanged(level, pos, state);
            } else if (brewable && entity.fuel > 0) {
                entity.fuel--;
                entity.brewTime = BREW_TIME;
                entity.ingredient = ingredientStack.getItem();
                setChanged(level, pos, state);
            }
        }

        boolean[] potionBits = entity.getPotionBits();
        if (!Arrays.equals(potionBits, entity.lastPotionCount)) {
            entity.lastPotionCount = potionBits;
            BlockState updatedState = state;
            for (int i = 0; i < MPGBrewingStandBlockBase.HAS_BOTTLE.length; i++) {
                if (updatedState.hasProperty(MPGBrewingStandBlockBase.HAS_BOTTLE[i])) {
                    updatedState = updatedState.setValue(MPGBrewingStandBlockBase.HAS_BOTTLE[i], potionBits[i]);
                }
            }
            level.setBlock(pos, updatedState, 2);
        }
    }

    protected static boolean isBrewable(ServerLevel level, NonNullList<ItemStack> items) {
        ItemStack ingredient = items.get(INGREDIENT_SLOT);
        if (ingredient.isEmpty() || !level.recipeAccess().propertySet(RecipePropertySet.BREWING_REAGENTS).test(ingredient)) {
            return false;
        }
        for (int slot = 0; slot < 3; slot++) {
            ItemStack input = items.get(slot);
            if (!input.isEmpty() && findBrewRecipe(level, input, ingredient).isPresent()) {
                return true;
            }
        }
        return false;
    }

    private static Optional<RecipeHolder<BrewingRecipe>> findBrewRecipe(ServerLevel level, ItemStack input, ItemStack reagent) {
        return level.recipeAccess().getRecipeFor(RecipeType.BREWING, new BrewingInput(input, reagent), level);
    }

    protected final boolean performBrew(ServerLevel level, double x, double y, double z) {
        if (beforeBrew(items)) {
            return false;
        }
        ItemStack ingredientStack = items.get(INGREDIENT_SLOT);
        for (int slot = 0; slot < 3; slot++) {
            ItemStack input = items.get(slot);
            if (input.isEmpty()) {
                continue;
            }
            BrewingInput brewingInput = new BrewingInput(input, ingredientStack);
            Optional<RecipeHolder<BrewingRecipe>> recipe = findBrewRecipe(level, input, ingredientStack);
            if (recipe.isEmpty()) {
                continue;
            }
            ItemStack brewed = recipe.get().value().assemble(brewingInput);
            if (!brewed.isEmpty() && !ItemStack.matches(input, brewed)) {
                long multiplied = (long) brewed.getCount() * MPGConfigValues.brewing_doubling_value;
                brewed.setCount((int) Math.min(Integer.MAX_VALUE, Math.max(1L, multiplied)));
            }
            items.set(slot, brewed);
        }
        finishBrew(level, x, y, z, items);
        level.levelEvent(1035, BlockPos.containing(x, y, z), 0);
        return true;
    }

    protected boolean beforeBrew(NonNullList<ItemStack> items) {
        return false;
    }

    protected abstract boolean isPotionInput(RecipeAccess recipeAccess, ItemStack stack);

    protected abstract void finishBrew(Level level, double x, double y, double z, NonNullList<ItemStack> items);

    private boolean[] getPotionBits() {
        boolean[] bits = new boolean[3];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = !items.get(i).isEmpty();
        }
        return bits;
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
        brewTime = input.getShortOr("BrewTime", (short) 0);
        ingredient = brewTime > 0 ? items.get(INGREDIENT_SLOT).getItem() : null;
        fuel = input.getByteOr("Fuel", (byte) 0);
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.putShort("BrewTime", (short) brewTime);
        ContainerHelper.saveAllItems(output, items);
        output.putByte("Fuel", (byte) fuel);
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if (slot == FUEL_SLOT) {
            return stack.is(Items.BLAZE_POWDER);
        }
        if (level == null) {
            return false;
        }
        RecipeAccess recipeAccess = level.recipeAccess();
        if (slot == INGREDIENT_SLOT) {
            return recipeAccess.propertySet(RecipePropertySet.BREWING_REAGENTS).test(stack);
        }
        return isPotionInput(recipeAccess, stack) && getItem(slot).isEmpty();
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction direction) {
        return direction == Direction.UP ? SLOTS_FOR_UP : direction == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction direction) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction direction) {
        return slot != INGREDIENT_SLOT || stack.is(Items.GLASS_BOTTLE);
    }
}
