package github.com.gengyoubo.MPG.baubles.common.capability;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.baubles.api.BaubleType;
import github.com.gengyoubo.MPG.baubles.common.container.InventoryBaubles;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = MPG.MODID)
public final class BaublesCapability {
    public static final Capability<InventoryBaubles> BAUBLES = CapabilityManager.get(new CapabilityToken<>() {});
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MPG.MODID, "baubles_inventory");

    private BaublesCapability() {
    }

    public static boolean isEnabled() {
        return !ModList.get().isLoaded("curios") && !ModList.get().isLoaded("accessories");
    }

    public static InventoryBaubles get(Player player) {
        return player.getCapability(BAUBLES)
                .orElseThrow(() -> new IllegalStateException("Missing baubles capability for player " + player.getScoreboardName()));
    }

    public static Optional<InventoryBaubles> find(Player player) {
        return player.getCapability(BAUBLES).resolve();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!isEnabled() || !(event.getObject() instanceof Player player)) {
            return;
        }
        event.addCapability(ID, new Provider(player));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!isEnabled()) {
            return;
        }
        find(event.getOriginal()).ifPresent(oldInventory -> find(event.getEntity()).ifPresent(newInventory -> newInventory.copyFrom(oldInventory)));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!isEnabled() || event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        find(event.player).ifPresent(inventory -> inventory.tick(event.player));
    }

    public static Optional<net.minecraft.world.item.ItemStack> findPrimaryRing(Player player) {
        return find(player).flatMap(inventory -> inventory.findFirstEquipped(BaubleType.RING));
    }

    private static final class Provider implements ICapabilitySerializable<CompoundTag> {
        private final InventoryBaubles inventory;
        private final LazyOptional<InventoryBaubles> optional;

        private Provider(Player player) {
            this.inventory = new InventoryBaubles(player);
            this.optional = LazyOptional.of(() -> inventory);
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == BAUBLES ? optional.cast() : LazyOptional.empty();
        }

        @Override
        @SuppressWarnings("deprecation")
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            return ((INBTSerializable<CompoundTag>) inventory).serializeNBT(provider);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            ((INBTSerializable<CompoundTag>) inventory).deserializeNBT(provider, nbt);
        }
    }
}
