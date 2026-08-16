package net.minecraftforge.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ForgeRegistries {
    public static final Registry<Block> BLOCKS = BuiltInRegistries.BLOCK;
    public static final Registry<Item> ITEMS = BuiltInRegistries.ITEM;
    public static final Registry<MenuType<?>> MENU_TYPES = BuiltInRegistries.MENU;
    public static final Registry<Attribute> ATTRIBUTES = BuiltInRegistries.ATTRIBUTE;
    public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZERS = BuiltInRegistries.RECIPE_SERIALIZER;
    public static final Registry<EntityType<?>> ENTITY_TYPES = BuiltInRegistries.ENTITY_TYPE;
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = BuiltInRegistries.BLOCK_ENTITY_TYPE;

    private ForgeRegistries() {
    }
}
