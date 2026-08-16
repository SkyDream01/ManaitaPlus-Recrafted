package github.com.gengyoubo.MPG.jei;

import net.minecraft.world.item.ItemStack;

public record SourceCopyJeiRecipe(
        ItemStack source,
        ItemStack input,
        ItemStack output
) {
}
