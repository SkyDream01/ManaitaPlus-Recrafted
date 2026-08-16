package github.com.gengyoubo.MPG.event;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.MPG.util.MPUtils;
import github.com.gengyoubo.common.entity.MPGEntityData;
import github.com.gengyoubo.common.event.MPGEventLogic;
import github.com.gengyoubo.common.trades.MPGSingleItemTrade;
import github.com.gengyoubo.common.trades.MPGTwoItemTrade;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;

@EventBusSubscriber(modid = MPG.MODID)
public class EventHandler {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        MPGEventLogic.filterKeyItemTooltip(event.getItemStack(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
            Player player = event.getEntity();
            MPUtils.destroyBlocks(player.getMainHandItem(), event.getLevel(), event.getPos(), player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        MPGEntityData.death.remove(event.getEntity());
        MPGEntityData.remove.remove(event.getEntity());
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        MPGEventLogic.findKiller(event.getEntity(), event.getSource().getEntity()).ifPresent(player ->
                MPGEventLogic.copyDrops(player, event.getDrops(), MPGConfig.item_drops_doubling_value)
                        .ifPresent(copies -> {
                            copies.forEach(stack -> ItemHandlerHelper.giveItemToPlayer(player, stack));
                            event.getDrops().clear();
                            event.setCanceled(true);
                        }));
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player == null) {
            return;
        }
        MPGEventLogic.redirectedExperience(player, event.getDroppedExperience(),
                MPGConfig.experience_drops_doubling_value).ifPresent(experience -> {
                    player.giveExperiencePoints(experience);
                    event.setDroppedExperience(0);
                    event.setCanceled(true);
                });
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player
                && (MPUtils.isManaitaArmorPart(player) || MPUtils.isManaita(player))) {
            event.setCanceled(true);
            MPGEventLogic.resetPlayerDamageState(player);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player
                && (MPUtils.isManaitaArmor(player) || MPUtils.isManaita(player))) {
            event.setCanceled(true);
            MPGEventLogic.resetPlayerDamageState(player);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player
                && (MPUtils.isManaitaArmor(player) || MPUtils.isManaita(player))) {
            event.setCanceled(true);
            MPGEventLogic.resetPlayerDamageState(player);
        }
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if (event.getType() != VillagerProfession.WEAPONSMITH) {
            return;
        }
        List<VillagerTrades.ItemListing> trades = event.getTrades().get(5);
        trades.add(new MPGSingleItemTrade(new ItemStack(MPGBlockCore.CraftingBlockItem.get(), 64),
                new ItemStack(MPGItemCore.ManaitaBow.get()), 1, 0, 1));
        trades.add(new MPGSingleItemTrade(new ItemStack(MPGBlockCore.FurnaceBlockItem.get(), 64),
                new ItemStack(MPGItemCore.ManaitaBow.get()), 1, 0, 1));
        trades.add(new MPGSingleItemTrade(new ItemStack(MPGBlockCore.BrewingBlock.get(), 64),
                new ItemStack(MPGItemCore.ManaitaBow.get()), 1, 0, 1));
        trades.add(new MPGTwoItemTrade(new ItemStack(MPGItemCore.ManaitaBow.get()), new ItemStack(Items.NETHER_STAR),
                new ItemStack(MPGItemCore.ManaitaSwordGod.get()), 1, 0, 1));
    }
}
