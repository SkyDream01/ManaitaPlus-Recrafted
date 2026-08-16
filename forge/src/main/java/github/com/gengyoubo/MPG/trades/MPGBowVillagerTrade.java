package github.com.gengyoubo.MPG.trades;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

public  class MPGBowVillagerTrade implements VillagerTrades.ItemListing {
    private final ItemStack input;
    private final ItemStack output;
    private final int maxUses;
    private final int xp;
    private final float priceMultiplier;

    public MPGBowVillagerTrade(ItemStack input, ItemStack output, int maxUses, int xp, float priceMultiplier) {
        this.input = input;
        this.output = output;
        this.maxUses = maxUses;
        this.xp = xp;
        this.priceMultiplier = priceMultiplier;
    }

    @Override
    public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource rand) {
        return new MerchantOffer(
                new ItemCost(input.getItem(), input.getCount()),
                output.copy(),
                maxUses,
                xp,
                priceMultiplier
        );
    }
}
