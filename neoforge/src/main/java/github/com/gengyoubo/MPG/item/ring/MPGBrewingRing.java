package github.com.gengyoubo.MPG.item.ring;

import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import github.com.gengyoubo.MPG.item.portable.MPGBrewingPortable;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;

public class MPGBrewingRing extends MPGBrewingPortable implements ICurioItem {
    public MPGBrewingRing(Item.Properties props) {
        super(props);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable("item.ringBrewing." + MPGItemStackData.getInt(stack, MPGNBTData.ItemType) + ".name");
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return "ring".equals(slotContext.identifier());
    }
}
