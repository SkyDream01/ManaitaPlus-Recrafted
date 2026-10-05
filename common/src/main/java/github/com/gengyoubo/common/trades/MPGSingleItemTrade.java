package github.com.gengyoubo.common.trades;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

/** One-input trade offer helper; trades themselves are data-driven in MC 26.3. */
public record MPGSingleItemTrade(ItemStack input, ItemStack output, int maxUses, int xp, float priceMultiplier) {
    public MerchantOffer getOffer() {
        return new MerchantOffer(new ItemCost(input.getItem(), input.getCount()), output.copy(),
                maxUses, xp, priceMultiplier);
    }
}
