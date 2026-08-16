package github.com.gengyoubo.MPG.trades;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public  class MPGSwordGodVillagerTrade implements VillagerTrades.ItemListing {
    private final ItemStack input;
    private final ItemStack input1 = new ItemStack(Items.NETHER_STAR);
    private final ItemStack output;
    private final int maxUses;
    private final int xp;
    private final float priceMultiplier;

    public MPGSwordGodVillagerTrade(ItemStack input, ItemStack output, int maxUses, int xp, float priceMultiplier) {
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
                Optional.of(new ItemCost(input1.getItem(), input1.getCount())),
                output.copy(),
                maxUses,
                xp,
                priceMultiplier
        );
    }
}
