package github.com.gengyoubo;

import github.com.gengyoubo.blockentity.RenderMPBrewingBlockEntity;
import github.com.gengyoubo.blockentity.RenderMPCraftingBlockEntity;
import github.com.gengyoubo.blockentity.RenderMPFurnaceBlockEntity;
import github.com.gengyoubo.core.MPBlockEntityCore;
import github.com.gengyoubo.core.MPEntityCore;
import github.com.gengyoubo.core.MPMenuCore;
import github.com.gengyoubo.entity.MPLightningBoltRenderer;
import github.com.gengyoubo.entity.RenderMPGArrow;
import github.com.gengyoubo.gui.MPBrewingStandScreen;
import github.com.gengyoubo.gui.MPCraftingScreen;
import github.com.gengyoubo.gui.MPFurnaceScreen;
import github.com.gengyoubo.common.network.MPGClientPayloadHandler;
import github.com.gengyoubo.network.MPNetworking;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.registry.MPGRegistryIds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MPGClient implements ClientModInitializer {
    private static final ResourceLocation TYPE_PREDICATE = github.com.gengyoubo.util.MPResource.id(MPG.MODID, MPGNBTData.Type);
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
            context.client().execute(() -> MPGClientPayloadHandler.handleDestroyBlock(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(MPNetworking.CHANGE_ENTITY_DATA.type(), (payload, context) -> {
            context.client().execute(() -> MPGClientPayloadHandler.handleChangeEntityData(payload));
        });
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> registerTypePredicates());
    }

    private static void registerTypePredicates() {
        if (predicatesRegistered) {
            return;
        }

        for (String itemPath : MPGRegistryIds.TYPED_ITEM_IDS) {
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

    private static void registerEntityRenderers() {
        EntityRendererRegistry.register(MPEntityCore.ManaitaArrow.get(), RenderMPGArrow::new);
        EntityRendererRegistry.register(MPEntityCore.ManaitaLightningBolt.get(), MPLightningBoltRenderer::new);
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
