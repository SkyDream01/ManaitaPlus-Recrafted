package github.com.gengyoubo;

import github.com.gengyoubo.blockentity.RenderMPBrewingBlockEntity;
import github.com.gengyoubo.blockentity.RenderMPCraftingBlockEntity;
import github.com.gengyoubo.blockentity.RenderMPFurnaceBlockEntity;
import github.com.gengyoubo.core.MPBlockEntityCore;
import github.com.gengyoubo.core.MPMenuCore;
import github.com.gengyoubo.entity.RenderMPGArrow;
import github.com.gengyoubo.gui.MPBrewingStandScreen;
import github.com.gengyoubo.gui.MPCraftingScreen;
import github.com.gengyoubo.gui.MPFurnaceScreen;
import github.com.gengyoubo.network.MPClientPacketHandlers;
import github.com.gengyoubo.network.MPNetworking;
import github.com.gengyoubo.network.server.MPChangeEntityDataPacket;
import github.com.gengyoubo.network.server.MPDestroyBlockPacket;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MPGClient implements ClientModInitializer {
    private static final ResourceLocation TYPE_PREDICATE = github.com.gengyoubo.util.MPResource.id(MPG.MODID, MPGNBTData.Type);
    private static final String[] TYPE_ITEMS = {
            "block_crafting_manaita",
            "block_furnace_manaita",
            "block_brewing_manaita",
            "block_hook_manaita",
            "manaita_crafting_portable",
            "manaita_furnace_portable",
            "manaita_brewing_portable"
    };
    private static boolean predicatesRegistered;

    @Override
    public void onInitializeClient() {
        MPNetworking.initCommon();
        registerTypePredicates();
        MPGKeyBindings.init();
        MenuScreens.register(MPMenuCore.CraftingManaita.get(), MPCraftingScreen::new);
        MenuScreens.register(MPMenuCore.FurnaceManaita.get(), MPFurnaceScreen::new);
        MenuScreens.register(MPMenuCore.BrewingStandManaita.get(), MPBrewingStandScreen::new);
        BlockEntityRenderers.register(MPBlockEntityCore.CRAFTING_BLOCK_ENTITY.get(), RenderMPCraftingBlockEntity::new);
        BlockEntityRenderers.register(MPBlockEntityCore.FURNACE_BLOCK_ENTITY.get(), RenderMPFurnaceBlockEntity::new);
        BlockEntityRenderers.register(MPBlockEntityCore.BREWING_BLOCK_ENTITY.get(), RenderMPBrewingBlockEntity::new);
        registerEntityRenderers();
        ClientPlayNetworking.registerGlobalReceiver(MPNetworking.DESTROY_BLOCK.type(), (payload, context) -> {
            context.client().execute(() -> MPClientPacketHandlers.handleDestroyBlock(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(MPNetworking.CHANGE_ENTITY_DATA.type(), (payload, context) -> {
            context.client().execute(() -> MPClientPacketHandlers.handleChangeEntityData(payload));
        });
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> registerTypePredicates());
    }

    private static void registerTypePredicates() {
        if (predicatesRegistered) {
            return;
        }

        for (String itemPath : TYPE_ITEMS) {
            ResourceLocation id = github.com.gengyoubo.util.MPResource.id(MPG.MODID, itemPath);
            Item item = BuiltInRegistries.ITEM.get(id);
            if (item == net.minecraft.world.item.Items.AIR) {
                MPG.LOGGER.warn("Skipped predicate registration for missing item {}", id);
                continue;
            }
            ItemProperties.register(item, TYPE_PREDICATE, MPGClient::readTypeValue);
            MPG.LOGGER.info("Registered type predicate {} for {}", TYPE_PREDICATE, id);
        }

        predicatesRegistered = true;
    }

    @SuppressWarnings("unchecked")
    private static void registerEntityRenderers() {
        ResourceLocation arrowId = github.com.gengyoubo.util.MPResource.id(MPG.MODID, "manaita_arrow");
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(arrowId)) {
            MPG.LOGGER.warn("Skipped renderer registration for missing entity {}", arrowId);
            return;
        }
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(arrowId);
        EntityRendererRegistry.register((EntityType<? extends github.com.gengyoubo.entity.MPGEntityArrow>) entityType, RenderMPGArrow::new);
    }

    private static float readTypeValue(ItemStack stack, net.minecraft.client.multiplayer.ClientLevel level, net.minecraft.world.entity.LivingEntity entity, int seed) {
        if (!github.com.gengyoubo.common.util.MPGItemStackData.hasTag(stack) || github.com.gengyoubo.common.util.MPGItemStackData.getTag(stack) == null) {
            return 0.0F;
        }
        return normalizeTypeValue(github.com.gengyoubo.common.util.MPGItemStackData.getTag(stack).getInt(MPGNBTData.ItemType));
    }

    private static float normalizeTypeValue(int type) {
        return switch (type) {
            case 1, 2, 3, 4, 5, 6, 7, 8 -> type / 8.0F;
            default -> 0.0F;
        };
    }
}
