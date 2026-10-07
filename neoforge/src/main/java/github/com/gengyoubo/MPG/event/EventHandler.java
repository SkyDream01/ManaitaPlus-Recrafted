package github.com.gengyoubo.MPG.event;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.item.MPGShieldItem;
import github.com.gengyoubo.MPG.util.MPUtils;
import github.com.gengyoubo.common.entity.MPGEntityData;
import github.com.gengyoubo.common.event.MPGEventLogic;
import github.com.gengyoubo.common.event.MPGToolMiningLogic;
import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.common.item.armor.MPGArmorItemBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * Villager trades are data-driven in MC 26.3 ({@code villager_trade}/{@code trade_set} registries),
 * so the old {@code VillagerTradesEvent} hook (addCustomTrades) is gone. The manaita weaponsmith
 * trades are now shipped as datapack entries instead; see
 * {@code data/manaita_plus_general/villager_trade/manaita/*.json} and
 * {@code data/minecraft/tags/villager_trade/weaponsmith/level_5.json}.
 */
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
        if (MPGShieldItem.protects(player) || MPGArmorItemBase.hasManaitaBoots(player)) {
            event.setCanceled(true);
            MPGEventLogic.resetPlayerFallState(player);
        } else if (MPGArmorItemBase.hasManaitaChestplate(player) || MPUtils.isManaita(player)) {
            event.setDamageMultiplier(0.0F);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (MPGShieldItem.protects(event.getEntity())) {
            event.setCanceled(true);
            if (event.getEntity() instanceof Player player) {
                MPGEventLogic.resetPlayerDamageState(player);
            }
            return;
        }
        if (event.getEntity() instanceof Player player
                && (MPGArmorItemBase.shouldCancelDamage(player, event.getSource()) || MPUtils.isManaita(player))) {
            event.setCanceled(true);
            MPGEventLogic.resetPlayerDamageState(player);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (MPGShieldItem.protects(event.getEntity())) {
            event.setNewDamage(0.0F);
            if (event.getEntity() instanceof Player player) {
                MPGEventLogic.resetPlayerDamageState(player);
            }
            return;
        }
        if (event.getEntity() instanceof Player player
                && (MPGArmorItemBase.shouldCancelDamage(player, event.getSource()) || MPUtils.isManaita(player))) {
            event.setNewDamage(0.0F);
            MPGEventLogic.resetPlayerDamageState(player);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (MPGShieldItem.protects(event.getEntity())) {
            event.setCanceled(true);
            if (event.getEntity() instanceof Player player) {
                MPGEventLogic.resetPlayerDamageState(player);
            } else {
                event.getEntity().setHealth(event.getEntity().getMaxHealth());
                event.getEntity().deathTime = 0;
            }
            return;
        }
        if (event.getEntity() instanceof Player player
                && (MPGArmorItemBase.shouldCancelDamage(player, event.getSource()) || MPUtils.isManaita(player))) {
            event.setCanceled(true);
            MPGEventLogic.resetPlayerDamageState(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        MPGArmorItemBase.syncArmorState(event.getEntity(), MPUtils.isManaita(event.getEntity()));
        MPGShieldItem.tickFloating(event.getEntity());
    }

    @SubscribeEvent
    public static void onEntityTickPre(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof Projectile projectile
                && MPGShieldItem.blockProjectile(projectile,
                        projectile.getBoundingBox().getCenter().add(projectile.getDeltaMovement()))) {
            event.setCanceled(true);
        } else {
            MPGShieldItem.repelHostile(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityTickPost(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Projectile projectile) {
            MPGShieldItem.blockProjectile(projectile, projectile.getBoundingBox().getCenter());
        } else {
            MPGShieldItem.repelHostile(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (MPGShieldItem.blockProjectile(event.getProjectile(), event.getRayTraceResult().getLocation())) {
            event.setCanceled(true);
        }
    }

    // addCustomTrades(VillagerTradesEvent) removed: VillagerTrades.ItemListing and the
    // RegisterVillagerTradesEvent-style hook no longer exist in 26.3. The weaponsmith level-5
    // trades (64 crafting/furnace/brewing blocks -> manaita bow, manaita bow + nether star ->
    // god sword) are provided by the datapack villager_trade entries instead; the trade helper
    // records keep getOffer() for mods that still want runtime MerchantOffers.
}
