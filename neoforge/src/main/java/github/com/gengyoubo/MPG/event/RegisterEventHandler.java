package github.com.gengyoubo.MPG.event;

import net.neoforged.api.distmarker.Dist;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.core.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import github.com.gengyoubo.MPG.gui.BrewingStandScreen;
import github.com.gengyoubo.MPG.gui.CraftingManaitaScreen;
import github.com.gengyoubo.MPG.gui.FurnaceManaitaScreen;
import github.com.gengyoubo.MPG.blockEntity.RenderBrewingManaitaBlockEntity;
import github.com.gengyoubo.MPG.blockEntity.RenderCraftingManaitaBlockEntity;
import github.com.gengyoubo.MPG.blockEntity.RenderFurnaceManaitaBlockEntity;
import github.com.gengyoubo.MPG.entity.MPLightningBoltRenderer;
import github.com.gengyoubo.MPG.item.MPGGodSwordItem;
import github.com.gengyoubo.MPG.entity.RenderManaitaArrow;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import org.jetbrains.annotations.Nullable;

import static github.com.gengyoubo.MPG.core.MPGEntityCore.ManaitaArrow;
import static github.com.gengyoubo.MPG.core.MPGEntityCore.ManaitaLightningBolt;

@EventBusSubscriber(modid = MPG.MODID, value = Dist.CLIENT)
public class RegisterEventHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ClientEventHandler::register);
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MPGMenuCore.CraftingManaita.get(), CraftingManaitaScreen::new);
        event.register(MPGMenuCore.FurnaceManaita.get(), FurnaceManaitaScreen::new);
        event.register(MPGMenuCore.BrewingStandManaita.get(), BrewingStandScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ManaitaLightningBolt.get(), MPLightningBoltRenderer::new);
        event.registerEntityRenderer(ManaitaArrow.get(), RenderManaitaArrow::new);

        event.registerBlockEntityRenderer(MPGBlockEntityCore.FURNACE_BLOCK_ENTITY.get(), RenderFurnaceManaitaBlockEntity::new);
        event.registerBlockEntityRenderer(MPGBlockEntityCore.BREWING_BLOCK_ENTITY.get(), RenderBrewingManaitaBlockEntity::new);
        event.registerBlockEntityRenderer(MPGBlockEntityCore.CRAFTING_BLOCK_ENTITY.get(), RenderCraftingManaitaBlockEntity::new);
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(MPGGodSwordItem.CLIENT_EXTENSIONS, MPGItemCore.ManaitaSwordGod.get());
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
    {
        // KeyMapping now takes a KeyMapping.Category instead of the KeyConflictContext/category string
        // pair; IN_GAME was the default conflict context, so dropping it keeps the old behavior.
        MPGKeyBoardCore.MESSAGE_KEY = new KeyMapping("key.manaita", InputConstants.Type.KEYBOARD, 88, KeyMapping.Category.MISC);
        MPGKeyBoardCore.MESSAGE_ARMOR_KEY = new KeyMapping("key.manaita.armor", InputConstants.Type.KEYBOARD, 86, KeyMapping.Category.MISC);
        MPGKeyBoardCore.PAXEL_KEY = new KeyMapping("key.manaita.doubling", InputConstants.Type.KEYBOARD, 67, KeyMapping.Category.MISC);
        event.register(MPGKeyBoardCore.MESSAGE_KEY);
        event.register(MPGKeyBoardCore.MESSAGE_ARMOR_KEY);
        event.register(MPGKeyBoardCore.PAXEL_KEY);
    }

    /**
     * ItemProperties/ItemPropertyFunction are gone in 26.3; item model predicates are now
     * range-select item model properties registered by id and referenced from the item model
     * definitions ({@code assets/<ns>/items/<item_id>.json}, {@code minecraft:range_dispatch}).
     * Expected property id: {@code manaita_plus_general:manaita_plus_general_type}, a float
     * 0..8 read from the custom_data key "ManaitaPlusGeneralType".
     */
    @SubscribeEvent
    public static void onRegisterItemModelProperty(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(MPG.MODID, MPGNBTData.Type), ManaitaTypeProperty.CODEC);
    }

    /** Range-select property exposing the custom_data "ManaitaPlusGeneralType" value (0..8). */
    private record ManaitaTypeProperty() implements RangeSelectItemModelProperty {
        private static final ManaitaTypeProperty INSTANCE = new ManaitaTypeProperty();
        private static final MapCodec<ManaitaTypeProperty> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public float get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
            return MPGItemStackData.getInt(itemStack, MPGNBTData.ItemType);
        }

        @Override
        public MapCodec<? extends RangeSelectItemModelProperty> type() {
            return CODEC;
        }
    }
}
