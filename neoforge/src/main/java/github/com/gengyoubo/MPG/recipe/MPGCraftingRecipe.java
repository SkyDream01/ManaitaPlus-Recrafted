package github.com.gengyoubo.MPG.recipe;

import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.MPG.core.MPGRecipeSerializerCore;
import github.com.gengyoubo.common.recipe.MPGSourceCopyRecipeBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MPGCraftingRecipe extends MPGSourceCopyRecipeBase {
    public MPGCraftingRecipe(CraftingBookCategory category) {
        super(category, MPGItemCore.ManaitaSource, () -> MPGConfig.source_doubling_value,
                () -> MPGRecipeSerializerCore.CraftingRecipe.get());
    }

    /** SimpleCraftingRecipeSerializer is gone in 26.3; serializers are records of a MapCodec and a StreamCodec. */
    public static class Serializer {
        private static final MapCodec<MPGCraftingRecipe> CODEC = CraftingBookCategory.CODEC
                .optionalFieldOf("category", CraftingBookCategory.MISC)
                .xmap(MPGCraftingRecipe::new, MPGCraftingRecipe::category);
        private static final StreamCodec<RegistryFriendlyByteBuf, MPGCraftingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> CraftingBookCategory.STREAM_CODEC.encode(buf, recipe.category()),
                buf -> new MPGCraftingRecipe(CraftingBookCategory.STREAM_CODEC.decode(buf)));

        /** Creates the registered serializer instance, replacing the old constructor hook. */
        public static RecipeSerializer<MPGCraftingRecipe> create() {
            return new RecipeSerializer<>(CODEC, STREAM_CODEC);
        }
    }
}
