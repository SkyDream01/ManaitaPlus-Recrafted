package github.com.gengyoubo.MPG.event;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.MPG.util.MPUtils;
import github.com.gengyoubo.common.entity.MPGEntityData;
import github.com.gengyoubo.common.event.MPGEventLogic;
import github.com.gengyoubo.common.event.MPGToolMiningLogic;
import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.common.item.armor.MPGArmorItemBase;
import github.com.gengyoubo.common.trades.MPGSingleItemTrade;
import github.com.gengyoubo.common.trades.MPGTwoItemTrade;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MPG.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        MPGEventLogic.filterKeyItemTooltip(event.getItemStack(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
            Player player = event.getEntity();
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof IMPGDestroy destroyItem && !destroyItem.canHarvest(stack)) {
                event.setCanceled(true);
                return;
            }
            if (event.getLevel() instanceof ServerLevel level && player instanceof ServerPlayer serverPlayer) {
                MPGToolMiningLogic.Result result = MPGToolMiningLogic.destroyBlocks(level, serverPlayer, stack,
                        event.getPos(), event.getFace(), MPGConfig.destroy_doubling_value,
                        MPGConfig.creative_range_destroy_value);
                if (result != MPGToolMiningLogic.Result.PASS) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        MPGEntityData.death.remove(event.getEntity());
        MPGEntityData.remove.remove(event.getEntity());
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        MPGEventLogic.findKiller(event.getEntity(), event.getSource().getEntity()).ifPresent(player -> {
            MPGEventLogic.createBeheadingDrop(event.getEntity(), player).ifPresent(stack ->
                    event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(event.getEntity().level(),
                            event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack)));
            MPGEventLogic.multiplyDrops(player, event.getDrops(), MPGConfig.item_drops_doubling_value);
        });
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
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (MPGArmorItemBase.hasManaitaBoots(player)) {
            event.setCanceled(true);
            MPGEventLogic.resetPlayerFallState(player);
        } else if (MPGArmorItemBase.hasManaitaChestplate(player) || MPUtils.isManaita(player)) {
            event.setDamageMultiplier(0.0F);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingAttackEvent event) {
        protectPlayer(event.getEntity() instanceof Player player ? player : null,
                event.getSource(), event::setCanceled);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        protectPlayer(event.getEntity() instanceof Player player ? player : null,
                event.getSource(), event::setCanceled);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        protectPlayer(event.getEntity() instanceof Player player ? player : null,
                event.getSource(), event::setCanceled);
    }

    private static void protectPlayer(Player player, net.minecraft.world.damagesource.DamageSource source,
                                      java.util.function.Consumer<Boolean> cancel) {
        if (player != null
                && (MPGArmorItemBase.shouldCancelDamage(player, source) || MPUtils.isManaita(player))) {
            cancel.accept(true);
            MPGEventLogic.resetPlayerDamageState(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        MPGArmorItemBase.syncArmorState(event.player);
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
