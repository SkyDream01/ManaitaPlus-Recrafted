package github.com.gengyoubo.common.integration.jei;

import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

public final class MPGJeiSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final MPGJeiSubtypeInterpreter INSTANCE = new MPGJeiSubtypeInterpreter();

    private MPGJeiSubtypeInterpreter() {
    }

    @Override
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return MPGItemStackData.getInt(ingredient, MPGNBTData.ItemType);
    }

    @Override
    @Deprecated(since = "19.9.0")
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return "type:" + MPGItemStackData.getInt(ingredient, MPGNBTData.ItemType);
    }
}
