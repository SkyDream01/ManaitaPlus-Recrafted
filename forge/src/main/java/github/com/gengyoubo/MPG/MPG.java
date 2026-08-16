package github.com.gengyoubo.MPG;

import com.mojang.logging.LogUtils;
import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import github.com.gengyoubo.MPG.core.*;
import github.com.gengyoubo.MPG.network.Networking;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;

@Mod(MPG.MODID)
public class MPG {
    public static final String MODID = MPGCommon.MOD_ID;
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTE_TYPE = DeferredRegister.create(Registries.ATTRIBUTE, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_DEFERRED_REGISTER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB_TYPES = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> MANAITA_PLUS_TAB = CREATIVE_MODE_TAB_TYPES.register("manaita_plus_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> MPGBlockCore.CraftingBlockItem.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.ManaitaPlusTab"))
            .displayItems((parameters, output) -> {
                acceptMPGType(MPGBlockCore.CraftingBlockItem.get(), output, 8);
                acceptMPGType(MPGBlockCore.FurnaceBlockItem.get(), output, 8);
                acceptMPGType(MPGBlockCore.BrewingBlockItem.get(), output, 8);
                acceptMPGType(MPGBlockCore.HookBlockItem.get(), output, 7);

                acceptMPGType(MPGItemCore.ManaitaCraftingPortable.get(), output, 8);
                acceptMPGType(MPGItemCore.ManaitaFurnacePortable.get(), output, 8);
                acceptMPGType(MPGItemCore.ManaitaBrewingPortable.get(), output, 8);
                if (BaublesCapability.isEnabled()) {
                    acceptMPGType(MPGItemCore.ManaitaCraftingRing.get(), output, 8);
                    acceptMPGType(MPGItemCore.ManaitaFurnaceRing.get(), output, 8);
                    acceptMPGType(MPGItemCore.ManaitaBrewingRing.get(), output, 8);
                }

                output.accept(MPGItemCore.ManaitaSwordGod.get());
                output.accept(MPGItemCore.ManaitaSword.get());
                output.accept(MPGItemCore.ManaitaBow.get());
                output.accept(MPGItemCore.ManaitaShovel.get());
                output.accept(MPGItemCore.ManaitaPickaxe.get());
                output.accept(MPGItemCore.ManaitaAxe.get());
                output.accept(MPGItemCore.ManaitaPaxel.get());
                output.accept(MPGItemCore.ManaitaHoe.get());
                output.accept(MPGItemCore.ManaitaShears.get());
                output.accept(MPGItemCore.ManaitaHelmet.get());
                output.accept(MPGItemCore.ManaitaChestplate.get());
                output.accept(MPGItemCore.ManaitaLeggings.get());
                output.accept(MPGItemCore.ManaitaBoots.get());
                output.accept(MPGItemCore.ManaitaHook.get());
                output.accept(MPGItemCore.ManaitaSource.get());
            }).build());

    @SuppressWarnings("removal")
    public MPG() {
        MPGCommon.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModContainer modContainer = FMLJavaModLoadingContext.get().getContainer();
        Networking.registerMessage();

        // Force-load all registry holder classes before RegisterEvent starts firing.
        MPGBlockCore.init();
        MPGItemCore.init();
        MPGEntityCore.init();
        MPGBlockEntityCore.init();
        MPGMenuCore.init();
        MPGRecipeSerializerCore.init();
        MPGAttributeCore.init();
        MPGConditionCore.init();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ATTRIBUTE_TYPE.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        CREATIVE_MODE_TAB_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        RECIPE_SERIALIZER_DEFERRED_REGISTER.register(modEventBus);
        MPGConditionCore.CONDITION_CODECS.register(modEventBus);

        modContainer.addConfig(new ModConfig(Type.COMMON, MPGConfig.SPEC, modContainer));
    }

    private static void acceptMPGType(Item item, CreativeModeTab.Output output, int maxType) {
        for (int type = 0; type <= maxType; type++) {
            ItemStack stack = new ItemStack(item);
            MPGItemStackData.putInt(stack, MPGNBTData.ItemType, type);
            output.accept(stack);
        }
    }

}
