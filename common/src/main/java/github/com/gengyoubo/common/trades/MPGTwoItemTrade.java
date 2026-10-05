package github.com.gengyoubo.common.trades;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Optional;

/** Two-input trade offer helper; trades themselves are data-driven in MC 26.3. */
public record MPGTwoItemTrade(ItemStack firstInput, ItemStack secondInput, ItemStack output,
                              int maxUses, int xp, float priceMultiplier) {
    public MerchantOffer getOffer() {
        return new MerchantOffer(
                new ItemCost(firstInput.getItem(), firstInput.getCount()),
                Optional.of(new ItemCost(secondInput.getItem(), secondInput.getCount())),
                output.copy(), maxUses, xp, priceMultiplier);
    }
}
