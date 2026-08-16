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
import github.com.gengyoubo.common.event.MPGToolMiningLogic;
import github.com.gengyoubo.common.item.data.IMPGDestroy;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import github.com.gengyoubo.common.event.MPGEventLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import github.com.gengyoubo.common.item.MPGSwordItemBase;

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
        registerToolMining();
        registerLivingDrops();
        registerSwordAttacks();

        BLOCKS.registerAll();
        ITEMS.registerAll();
        ATTRIBUTE_TYPE.registerAll();
        ENTITY_TYPES.registerAll();
        MENU_TYPES.registerAll();
        BLOCK_ENTITY_TYPES.registerAll();
        RECIPE_SERIALIZER_DEFERRED_REGISTER.registerAll();

        MANAITA_PLUS_TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, github.com.gengyoubo.util.MPResource.id(MODID, "manaita_plus_tab"), FabricItemGroup.builder()
            .icon(() -> MPBlockCore.CraftingBlockItem.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.ManaitaPlusTab"))
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
                entries.accept(MPItemCore.ManaitaHook.get());
                entries.accept(MPItemCore.ManaitaSource.get());
            }).build());
    }

    private static void registerResourceConditions() {
        ResourceConditions.register(EasyModeResourceCondition.TYPE);
    }

    private static void registerToolMining() {
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof IMPGDestroy destroyItem)) {
                return InteractionResult.PASS;
            }
            if (!destroyItem.canHarvest(stack)) {
                return InteractionResult.FAIL;
            }
            if (level.isClientSide || !(level instanceof ServerLevel serverLevel)
                    || !(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.PASS;
            }
            MPGToolMiningLogic.Result result = MPGToolMiningLogic.destroyBlocks(serverLevel, serverPlayer, stack,
                    pos, direction, MPGConfig.destroy_doubling_value, MPGConfig.creative_range_destroy_value);
            return result == MPGToolMiningLogic.Result.PASS ? InteractionResult.PASS : InteractionResult.SUCCESS;
        });
    }

    private static void registerLivingDrops() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) ->
                MPGEventLogic.handleFabricDeathDrops(entity, source, MPGConfig.item_drops_doubling_value));
    }

    private static void registerSwordAttacks() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof MPGSwordItemBase sword) || !(entity instanceof LivingEntity living)) {
                return InteractionResult.PASS;
            }
            if (level.isClientSide) {
                return InteractionResult.PASS;
            }
            sword.performPlayerAttack(player, stack, living);
            return InteractionResult.SUCCESS;
        });
    }

    private static void acceptMPGType(Item item, CreativeModeTab.Output entries, int maxType) {
        for (int type = 0; type <= maxType; type++) {
            ItemStack stack = new ItemStack(item);
            github.com.gengyoubo.common.util.MPGItemStackData.putInt(stack, MPGNBTData.ItemType, type);
            entries.accept(stack);
        }
    }
}
