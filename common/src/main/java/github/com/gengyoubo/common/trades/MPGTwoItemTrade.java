package github.com.gengyoubo.common.trades;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record MPGTwoItemTrade(ItemStack firstInput, ItemStack secondInput, ItemStack output,
                              int maxUses, int xp, float priceMultiplier)
        implements VillagerTrades.ItemListing {
    @Override
    public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource random) {
        return new MerchantOffer(
                new ItemCost(firstInput.getItem(), firstInput.getCount()),
                Optional.of(new ItemCost(secondInput.getItem(), secondInput.getCount())),
                output.copy(), maxUses, xp, priceMultiplier);
    }
}
