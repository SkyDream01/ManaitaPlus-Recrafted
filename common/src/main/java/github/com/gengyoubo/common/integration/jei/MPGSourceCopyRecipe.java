package github.com.gengyoubo.common.integration.jei;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record MPGSourceCopyRecipe(List<ItemStack> sources, List<ItemStack> inputs,
                                  List<ItemStack> outputs, int multiplier) {
}
