package github.com.gengyoubo;

import github.com.gengyoubo.common.MPGCommon;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import github.com.gengyoubo.core.*;
import github.com.gengyoubo.network.MPNetworking;
import github.com.gengyoubo.resource.EasyModeResourceCondition;
import github.com.gengyoubo.common.util.MPGNBTData;

public class MPG implements ModInitializer {
    public static final String MODID = MPGCommon.MOD_ID;
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTE_TYPE = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_DEFERRED_REGISTER =  DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static CreativeModeTab MANAITA_PLUS_TAB;

    @Override
    public void onInitialize() {
        MPGCommon.init();
        initFabric();
    }

    public static void initFabric() {
        MPGConfig.load();

        // Force-load all registry holder classes before registration.
        MPBlockCore.init();
        MPItemCore.init();
        MPBlockEntityCore.init();
        MPMenuCore.init();
        MPEntityCore.init();
        MPAttributeCore.init();
        MPRecipeSerializerCore.init();
        MPSynchedDataCore.init();
        registerResourceConditions();
        MPNetworking.initCommon();
        MPNetworking.initServer();

        BLOCKS.registerAll();
        ITEMS.registerAll();
        ATTRIBUTE_TYPE.registerAll();
        ENTITY_TYPES.registerAll();
        MENU_TYPES.registerAll();
        BLOCK_ENTITY_TYPES.registerAll();
        RECIPE_SERIALIZER_DEFERRED_REGISTER.registerAll();

        MANAITA_PLUS_TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, github.com.gengyoubo.util.MPResource.id(MODID, "manaita_plus_tab"), FabricItemGroup.builder()
            .icon(() -> MPBlockCore.CraftingBlockItem.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.MPTab"))
            .displayItems((context, entries) -> {
                acceptMPGType(MPBlockCore.CraftingBlockItem.get(), entries, 8);
                acceptMPGType(MPBlockCore.FurnaceBlockItem.get(), entries, 8);
                acceptMPGType(MPBlockCore.BrewingBlockItem.get(), entries, 8);
                acceptMPGType(MPBlockCore.HookBlockItem.get(), entries, 7);

                acceptMPGType(MPItemCore.ManaitaCraftingPortable.get(), entries, 8);
                acceptMPGType(MPItemCore.ManaitaFurnacePortable.get(), entries, 8);
                acceptMPGType(MPItemCore.ManaitaBrewingPortable.get(), entries, 8);

                entries.accept(MPItemCore.ManaitaSword.get());
                entries.accept(MPItemCore.ManaitaSwordGod.get());
                entries.accept(MPItemCore.ManaitaBow.get());
                entries.accept(MPItemCore.ManaitaAxe.get());
                entries.accept(MPItemCore.ManaitaHoe.get());
                entries.accept(MPItemCore.ManaitaPaxel.get());
                entries.accept(MPItemCore.ManaitaPickaxe.get());
                entries.accept(MPItemCore.ManaitaShears.get());
                entries.accept(MPItemCore.ManaitaShovel.get());
                entries.accept(MPItemCore.ManaitaHelmet.get());
                entries.accept(MPItemCore.ManaitaChestplate.get());
                entries.accept(MPItemCore.ManaitaLeggings.get());
                entries.accept(MPItemCore.ManaitaBoots.get());
                entries.accept(MPItemCore.ManaitaSource.get());
            }).build());
    }

    private static void registerResourceConditions() {
        ResourceConditions.register(EasyModeResourceCondition.TYPE);
    }

    private static void acceptMPGType(Item item, CreativeModeTab.Output entries, int maxType) {
        for (int type = 0; type <= maxType; type++) {
            ItemStack stack = new ItemStack(item);
            github.com.gengyoubo.common.util.MPGItemStackData.putInt(stack, MPGNBTData.ItemType, type);
            entries.accept(stack);
        }
    }
}
