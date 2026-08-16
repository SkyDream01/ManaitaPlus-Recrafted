package github.com.gengyoubo.MPG.event;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.common.item.data.IMPGKey;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.MPG.trades.MPGBowVillagerTrade;
import github.com.gengyoubo.MPG.trades.MPGSwordGodVillagerTrade;
import github.com.gengyoubo.MPG.util.MPGEntityData;
import github.com.gengyoubo.MPG.util.MPUtils;

import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = MPG.MODID)
public class EventHandler {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        if (item instanceof IMPGKey) {
            List<Component> toolTip = event.getToolTip();
            Iterator<Component> iterator = toolTip.iterator();
            while (iterator.hasNext()) {
                Component component = iterator.next();
                if (component instanceof MutableComponent mutableComponent) {
                    ComponentContents contents = mutableComponent.getContents();
                    if (contents instanceof TranslatableContents translatableContents) {
                        if (translatableContents.getKey().startsWith("item.modifiers.")) {
                            while (iterator.hasNext()) {
                                iterator.next();
                                iterator.remove();
                            }
                            break;
                        }
                    }
                }
            }
            if (!toolTip.isEmpty()) {
                toolTip.removeLast();
            }
            if (!toolTip.isEmpty()) {
                toolTip.removeLast();
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START) return;
        Player player = event.getEntity();
        MPUtils.destroyBlocks(player.getMainHandItem(), event.getLevel(), event.getPos(), player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        MPGEntityData.death.remove(event.getEntity());
        MPGEntityData.remove.remove(event.getEntity());
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Player player;
        if (event.getSource().getEntity() instanceof Player) {
            player = (Player) event.getSource().getEntity();
        } else {
            LivingEntity killCredit = event.getEntity().getKillCredit();
            if (killCredit instanceof Player) {
                player = (Player) killCredit;
            } else {
                return;
            }
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof IMPGDoubling doublingItem) {
            if (doublingItem.isDoubling(mainHandItem)) {
                int magnification = MPGConfig.item_drops_doubling_value;
                for (ItemEntity drop : event.getDrops()) {
                    ItemStack dropStack = drop.getItem().copy();
                    dropStack.setCount(dropStack.getCount() * magnification);
                    ItemHandlerHelper.giveItemToPlayer(player, dropStack);
                }
            } else {
                for (ItemEntity drop : event.getDrops()) {
                    ItemHandlerHelper.giveItemToPlayer(player, drop.getItem().copy());
                }
            }
            event.getDrops().clear();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        Player attackingPlayer = event.getAttackingPlayer();
        if (attackingPlayer == null) return;

        ItemStack mainHandItem = attackingPlayer.getMainHandItem();
        if (mainHandItem.getItem() instanceof IMPGDoubling doublingItem) {
            if (doublingItem.isDoubling(mainHandItem)) {
                attackingPlayer.giveExperiencePoints(event.getDroppedExperience() * MPGConfig.experience_drops_doubling_value);
            } else {
                attackingPlayer.giveExperiencePoints(event.getDroppedExperience());
            }
            event.setDroppedExperience(0);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player) || !hasManaitaProtectionForFall(player)) return;
        event.setCanceled(true);
        resetPlayerDamageState(player);
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || hasManaitaProtection(player)) return;
        event.setCanceled(true);
        resetPlayerDamageState(player);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player) || hasManaitaProtection(player)) return;
        event.setCanceled(true);
        resetPlayerDamageState(player);
    }

    private static boolean hasManaitaProtection(Player player) {
        return !MPUtils.isManaitaArmor(player) && !MPUtils.isManaita(player);
    }

    private static boolean hasManaitaProtectionForFall(Player player) {
        return MPUtils.isManaitaArmorPart(player) || MPUtils.isManaita(player);
    }

    private static void resetPlayerDamageState(Player player) {
        player.setHealth(player.getMaxHealth());
        player.fallDistance = 0;
        player.hurtTime = 0;
        player.deathTime = 0;
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.WEAPONSMITH) {
            List<VillagerTrades.ItemListing> tradesTier = event.getTrades().get(5);

            tradesTier.add(new MPGBowVillagerTrade(
                    new ItemStack(MPGBlockCore.CraftingBlockItem.get(), 64),
                    new ItemStack(MPGItemCore.ManaitaBow.get(), 1),
                    1, 0, 1));
            tradesTier.add(new MPGBowVillagerTrade(
                    new ItemStack(MPGBlockCore.FurnaceBlockItem.get(), 64),
                    new ItemStack(MPGItemCore.ManaitaBow.get(), 1),
                    1, 0, 1));
            tradesTier.add(new MPGBowVillagerTrade(
                    new ItemStack(MPGBlockCore.BrewingBlock.get(), 64),
                    new ItemStack(MPGItemCore.ManaitaBow.get(), 1),
                    1, 0, 1));

            tradesTier.add(new MPGSwordGodVillagerTrade(
                    new ItemStack(MPGItemCore.ManaitaBow.get(), 1),
                    new ItemStack(MPGItemCore.ManaitaSwordGod.get(), 1),
                    1, 0, 1));
        }
    }
}

