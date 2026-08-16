package github.com.gengyoubo.MPG.item.portable;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import github.com.gengyoubo.MPG.block.entity.MPGLogicHelper;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.core.MPGBlockEntityCore;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.menu.MPGFurnaceMenu;
import github.com.gengyoubo.common.util.MPGItemStackData;

public class MPGFurnacePortable extends MPGPortableItem {
    public MPGFurnacePortable() {
        super("item.portableFurnace.");
    }

    @Override
    protected void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level) {
        openPortableScreen(serverPlayer, itemInHand, level, "container.furnace_manaita",
                (containerId, inventory, player, heldStack, world) -> {
                    ManaitaPlusFurnaceBlockEntity block = new ManaitaPlusFurnaceBlockEntity(player, heldStack);
                    return block.createMenu(containerId, inventory, player);
                });
    }


    public static class ManaitaPlusFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
        private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
        private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;
        private final Player player;
        private final ItemStack stack;
        protected final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

        public ManaitaPlusFurnaceBlockEntity(Player player,ItemStack stack) {
            super(MPGBlockEntityCore.FURNACE_BLOCK_ENTITY.get(), player.blockPosition(), MPGBlockCore.FurnaceBlock.get().defaultBlockState(), RecipeType.SMELTING);
            this.quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);
            this.player = player;
            this.stack = stack;
            CompoundTag tag = MPGItemStackData.getTag(stack);
            if (tag != null) {
                loadAdditional(tag, player.level().registryAccess());
            }
        }

        protected @NotNull Component getDefaultName() {
            return Component.translatable("container.furnace_manaita");
        }

        public @NotNull AbstractContainerMenu createMenu(int p_59293_, @NotNull Inventory p_59294_) {
            return new MPGFurnaceMenu(p_59293_, p_59294_, this, this.dataAccess);
        }

        @Override
        public int getMaxStackSize() {
            return Integer.MAX_VALUE;
        }

        @Override
        protected void loadAdditional(@NotNull CompoundTag p_155025_, HolderLookup.@NotNull Provider provider) {
            super.loadAdditional(p_155025_, provider);
            ContainerHelper.loadAllItems(p_155025_, this.items, provider);
            CompoundTag compoundtag = p_155025_.getCompound("RecipesUsed");

            for(String s : compoundtag.getAllKeys()) {
                this.recipesUsed.put(ResourceLocation.parse(s), compoundtag.getInt(s));
            }

        }

        @Override
        protected void saveAdditional(@NotNull CompoundTag p_187452_, HolderLookup.@NotNull Provider provider) {
            ContainerHelper.saveAllItems(p_187452_, this.items, provider);
            CompoundTag compoundtag = new CompoundTag();
            this.recipesUsed.forEach((p_187449_, p_187450_) -> compoundtag.putInt(p_187449_.toString(), p_187450_));
            p_187452_.put("RecipesUsed", compoundtag);
        }



        private boolean canBurn(RegistryAccess registryAccess, RecipeHolder<?> recipe, NonNullList<ItemStack> items) {
            return MPGLogicHelper.canBurn(registryAccess, recipe, items, this.getMaxStackSize(), this);
        }

        private boolean burn(RegistryAccess p_266740_, RecipeHolder<?> p_266780_, NonNullList<ItemStack> p_267073_) {
            if (p_266780_ != null && this.canBurn(p_266740_, p_266780_, p_267073_)) {
                ItemStack itemstack = p_267073_.get(0);
                ItemStack itemstack1 = MPGLogicHelper.assembleResult(p_266780_, this.getItem(0), p_266740_);
                ItemStack itemstack2 = p_267073_.get(2);
                if (itemstack2.isEmpty()) {
                    ItemStack copy = itemstack1.copy();
                    copy.setCount(copy.getCount() * MPGConfig.furnace_doubling_value);
                    p_267073_.set(2, copy);
                } else if (ItemStack.isSameItemSameComponents(itemstack2, itemstack1)) {
                    itemstack2.grow(itemstack1.getCount() * MPGConfig.furnace_doubling_value);
                }

                itemstack.shrink(1);
                return true;
            } else {
                return false;
            }
        }

        protected int getBurnDuration(@NotNull ItemStack p_58343_) {
            return 0;
        }



        public boolean canPlaceItemThroughFace(int p_58336_, @NotNull ItemStack p_58337_, @Nullable Direction p_58338_) {
            return this.canPlaceItem(p_58336_, p_58337_);
        }

        public int getContainerSize() {
            return this.items.size();
        }

        public boolean isEmpty() {
            for(ItemStack itemstack : this.items) {
                if (!itemstack.isEmpty()) {
                    return false;
                }
            }

            return true;
        }

        public @NotNull ItemStack getItem(int p_58328_) {
            return this.items.get(p_58328_);
        }

        public @NotNull ItemStack removeItem(int p_58330_, int p_58331_) {
            return ContainerHelper.removeItem(this.items, p_58330_, p_58331_);
        }

        public @NotNull ItemStack removeItemNoUpdate(int p_58387_) {
            return ContainerHelper.takeItem(this.items, p_58387_);
        }

        public void setItem(int p_58333_, @NotNull ItemStack p_58334_) {
            this.items.set(p_58333_, p_58334_);
            if (!this.items.getFirst().isEmpty()) {
                Level level = player.level();
                RecipeHolder<?> recipe = this.quickCheck.getRecipeFor(new SingleRecipeInput(this.items.getFirst()), level).orElse(null);
                while (this.canBurn(level.registryAccess(), recipe, this.items)) {
                    if (this.burn(level.registryAccess(), recipe, this.items)) {
                        this.setRecipeUsed(recipe);
                    }
                }
            }
//            save
            CompoundTag tag = MPGItemStackData.getTag(stack);
            if (tag == null) {
                tag = new CompoundTag();
            }
            saveAdditional(tag, player.level().registryAccess());
            MPGItemStackData.setTag(stack, tag);
        }

        public boolean stillValid(@NotNull Player p_58340_) {
            return true;
        }

        public boolean canPlaceItem(int p_58389_, @NotNull ItemStack p_58390_) {
            return p_58389_ != 2;
        }

        public void clearContent() {
            this.items.clear();
        }

        public void setRecipeUsed(RecipeHolder<?> p_58345_) {
            if (p_58345_ != null) {
                ResourceLocation resourcelocation = p_58345_.id();
                this.recipesUsed.addTo(resourcelocation, 1);
            }
        }

        public void awardUsedRecipesAndPopExperience(@NotNull ServerPlayer p_155004_) {
            MPGLogicHelper.awardUsedRecipesAndPopExperience(p_155004_, this.items, this.recipesUsed);
        }

        public @NotNull java.util.List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(@NotNull ServerLevel p_154996_, @NotNull Vec3 p_154997_) {
            return MPGLogicHelper.getRecipesToAwardAndPopExperience(p_154996_, p_154997_, this.recipesUsed);
        }

        public void fillStackedContents(@NotNull StackedContents p_58342_) {
            for(ItemStack itemstack : this.items) {
                p_58342_.accountStack(itemstack);
            }

        }
    }
}


