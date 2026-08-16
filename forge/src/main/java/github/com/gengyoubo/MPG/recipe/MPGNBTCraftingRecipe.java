package github.com.gengyoubo.MPG.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.JsonOps;
import github.com.gengyoubo.MPG.core.MPGRecipeSerializerCore;
import github.com.gengyoubo.MPG.recipe.ingredient.MPGNBTIngredient;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MPGNBTCraftingRecipe implements CraftingRecipe {
    static final int MAX_WIDTH = 3;
    static final int MAX_HEIGHT = 3;

    private static final MapCodec<MPGNBTCraftingRecipe> CODEC = new MapCodec<>() {
        @Override
        public <T> DataResult<MPGNBTCraftingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
            try {
                String group = getOptionalString(ops, input, "group", "");
                CraftingBookCategory category = parseCategory(getOptionalString(ops, input, "category", null));
                String[] pattern = shrink(patternFromJson(getRequiredJsonArray(ops, input, "pattern")));
                int width = pattern[0].length();
                int height = pattern.length;
                Map<String, Ingredient> key = keyFromJson(getRequiredJsonObject(ops, input, "key"));
                NonNullList<Ingredient> ingredients = dissolvePattern(pattern, key, width, height);
                ItemStack result = itemStackFromJson(getRequiredJsonObject(ops, input, "result"));
                boolean showNotification = getOptionalBoolean(ops, input, "show_notification", true);
                return DataResult.success(new MPGNBTCraftingRecipe(group, category, width, height, ingredients, result, showNotification));
            } catch (RuntimeException exception) {
                return DataResult.error(exception::getMessage);
            }
        }

        @Override
        public <T> RecordBuilder<T> encode(MPGNBTCraftingRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            if (!recipe.group.isEmpty()) {
                prefix.add("group", ops.createString(recipe.group));
            }
            prefix.add("category", ops.createString(recipe.category.getSerializedName()));
            prefix.add("pattern", JsonOps.INSTANCE.convertTo(ops, patternToJson(recipe)));
            prefix.add("key", JsonOps.INSTANCE.convertTo(ops, keyToJson(recipe)));
            prefix.add("result", JsonOps.INSTANCE.convertTo(ops, itemStackToJson(recipe.result)));
            prefix.add("show_notification", ops.createBoolean(recipe.showNotification));
            return prefix;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of("group", "category", "pattern", "key", "result", "show_notification").map(ops::createString);
        }
    };

    private static final StreamCodec<RegistryFriendlyByteBuf, MPGNBTCraftingRecipe> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MPGNBTCraftingRecipe decode(RegistryFriendlyByteBuf buffer) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            String group = buffer.readUtf();
            CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
            boolean showNotification = buffer.readBoolean();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            return new MPGNBTCraftingRecipe(group, category, width, height, ingredients, result, showNotification);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, MPGNBTCraftingRecipe recipe) {
            buffer.writeVarInt(recipe.width);
            buffer.writeVarInt(recipe.height);
            buffer.writeUtf(recipe.group);
            buffer.writeEnum(recipe.category);
            buffer.writeBoolean(recipe.showNotification);
            for (Ingredient ingredient : recipe.recipeItems) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
        }
    };

    final int width;
    final int height;
    final NonNullList<Ingredient> recipeItems;
    final ItemStack result;
    final String group;
    final CraftingBookCategory category;
    final boolean showNotification;

    public MPGNBTCraftingRecipe(String group, CraftingBookCategory category, int width, int height, NonNullList<Ingredient> recipeItems, ItemStack result, boolean showNotification) {
        this.group = group;
        this.category = category;
        this.width = width;
        this.height = height;
        this.recipeItems = recipeItems;
        this.result = result;
        this.showNotification = showNotification;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return MPGRecipeSerializerCore.NBTCraftingRecipe.get();
    }

    @Override
    public @NotNull String getGroup() {
        return this.group;
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return this.result.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    private static CraftingBookCategory parseCategory(@Nullable String name) {
        if (name == null) {
            return CraftingBookCategory.MISC;
        }
        for (CraftingBookCategory category : CraftingBookCategory.values()) {
            if (category.getSerializedName().equals(name)) {
                return category;
            }
        }
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public boolean matches(CraftingInput input, @NotNull Level level) {
        for (int x = 0; x <= input.width() - this.width; ++x) {
            for (int y = 0; y <= input.height() - this.height; ++y) {
                if (this.matches(input, x, y, true) || this.matches(input, x, y, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matches(CraftingInput input, int xOffset, int yOffset, boolean mirrored) {
        for (int x = 0; x < input.width(); ++x) {
            for (int y = 0; y < input.height(); ++y) {
                int recipeX = x - xOffset;
                int recipeY = y - yOffset;
                Ingredient ingredient = Ingredient.EMPTY;
                if (recipeX >= 0 && recipeY >= 0 && recipeX < this.width && recipeY < this.height) {
                    ingredient = mirrored
                            ? this.recipeItems.get(this.width - recipeX - 1 + recipeY * this.width)
                            : this.recipeItems.get(recipeX + recipeY * this.width);
                }

                if (!matchesIngredient(ingredient, input.getItem(x, y))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean matchesIngredient(Ingredient ingredient, ItemStack stack) {
        if (ingredient.isEmpty()) {
            return stack.isEmpty();
        }
        if (stack.isEmpty()) {
            return false;
        }
        for (ItemStack expected : ingredient.getItems()) {
            if (MPGNBTIngredient.matchesType(expected, stack)) {
                return true;
            }
        }
        return ingredient.test(stack);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, @NotNull HolderLookup.Provider provider) {
        return this.result.copy();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> key, int width, int height) {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
        Set<String> unused = Sets.newHashSet(key.keySet());
        unused.remove(" ");

        for (int y = 0; y < pattern.length; ++y) {
            for (int x = 0; x < pattern[y].length(); ++x) {
                String symbol = pattern[y].substring(x, x + 1);
                Ingredient ingredient = key.get(symbol);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + symbol + "' but it's not defined in the key");
                }
                unused.remove(symbol);
                ingredients.set(x + width * y, ingredient);
            }
        }

        if (!unused.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + unused);
        }
        return ingredients;
    }

    @VisibleForTesting
    static String[] shrink(String... pattern) {
        int first = Integer.MAX_VALUE;
        int last = 0;
        int top = 0;
        int bottom = 0;

        for (int i = 0; i < pattern.length; ++i) {
            String row = pattern[i];
            first = Math.min(first, firstNonSpace(row));
            int rowLast = lastNonSpace(row);
            last = Math.max(last, rowLast);
            if (rowLast < 0) {
                if (top == i) {
                    ++top;
                }
                ++bottom;
            } else {
                bottom = 0;
            }
        }

        if (pattern.length == bottom) {
            return new String[0];
        }

        String[] result = new String[pattern.length - bottom - top];
        for (int i = 0; i < result.length; ++i) {
            result[i] = pattern[i + top].substring(first, last + 1);
        }
        return result;
    }

    @Override
    public boolean isIncomplete() {
        NonNullList<Ingredient> ingredients = this.getIngredients();
        return ingredients.isEmpty() || ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(Ingredient::isEmpty);
    }

    private static int firstNonSpace(String row) {
        int i = 0;
        while (i < row.length() && row.charAt(i) == ' ') {
            i++;
        }
        return i;
    }

    private static int lastNonSpace(String row) {
        int i = row.length() - 1;
        while (i >= 0 && row.charAt(i) == ' ') {
            --i;
        }
        return i;
    }

    static String[] patternFromJson(JsonArray patternArray) {
        String[] pattern = new String[patternArray.size()];
        if (pattern.length > MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        }
        if (pattern.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        }

        for (int i = 0; i < pattern.length; ++i) {
            String row = GsonHelper.convertToString(patternArray.get(i), "pattern[" + i + "]");
            if (row.length() > MAX_WIDTH) {
                throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
            }
            if (i > 0 && pattern[0].length() != row.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }
            pattern[i] = row;
        }

        return pattern;
    }

    private static JsonObject getRequiredJsonObject(DynamicOps<?> ops, MapLike<?> input, String key) {
        JsonElement element = convertToJson(ops, input.get(key));
        if (element == null || !element.isJsonObject()) {
            throw new JsonSyntaxException("Missing or invalid '" + key + "' object");
        }
        return element.getAsJsonObject();
    }

    private static JsonArray getRequiredJsonArray(DynamicOps<?> ops, MapLike<?> input, String key) {
        JsonElement element = convertToJson(ops, input.get(key));
        if (element == null || !element.isJsonArray()) {
            throw new JsonSyntaxException("Missing or invalid '" + key + "' array");
        }
        return element.getAsJsonArray();
    }

    private static String getOptionalString(DynamicOps<?> ops, MapLike<?> input, String key, @Nullable String fallback) {
        JsonElement element = convertToJson(ops, input.get(key));
        return element == null || element.isJsonNull() ? fallback : GsonHelper.convertToString(element, key);
    }

    private static boolean getOptionalBoolean(DynamicOps<?> ops, MapLike<?> input, String key, boolean fallback) {
        JsonElement element = convertToJson(ops, input.get(key));
        return element == null || element.isJsonNull() ? fallback : GsonHelper.convertToBoolean(element, key);
    }

    private static JsonElement convertToJson(DynamicOps<?> ops, Object value) {
        if (value == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        DynamicOps<Object> castOps = (DynamicOps<Object>) ops;
        return castOps.convertTo(JsonOps.INSTANCE, value);
    }

    private static Map<String, Ingredient> keyFromJson(JsonObject json) {
        Map<String, Ingredient> key = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            key.put(entry.getKey(), fromJson(entry.getValue(), false));
        }

        key.put(" ", Ingredient.EMPTY);
        return key;
    }

    public static Ingredient fromJson(@Nullable JsonElement element, boolean allowEmpty) {
        if (element == null || element.isJsonNull()) {
            throw new JsonSyntaxException("Item cannot be null");
        }
        if (element.isJsonObject()) {
            return ingredientFromJsonObject(element.getAsJsonObject());
        }
        if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            if (jsonArray.isEmpty() && !allowEmpty) {
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            }
            ItemStack[] stacks = StreamSupport.stream(jsonArray.spliterator(), false)
                    .map(jsonElement -> itemStackFromIngredient(GsonHelper.convertToJsonObject(jsonElement, "item")))
                    .toArray(ItemStack[]::new);
            return Ingredient.of(stacks);
        }
        throw new JsonSyntaxException("Expected item to be object or array of objects");
    }

    private static Ingredient ingredientFromJsonObject(JsonObject json) {
        if (json.has("tag")) {
            return ingredientFromTag(json);
        }
        return Ingredient.of(itemStackFromIngredient(json));
    }

    private static Ingredient ingredientFromTag(JsonObject json) {
        String tagId = GsonHelper.getAsString(json, "tag");
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, ResourceLocation.parse(tagId));
        int type = GsonHelper.getAsInt(json, "type", 0);
        ItemStack[] stacks = BuiltInRegistries.ITEM.getTag(tagKey)
                .orElseThrow(() -> new JsonSyntaxException("Unknown item tag '" + tagId + "'"))
                .stream()
                .map(holder -> {
                    ItemStack stack = new ItemStack(holder.value());
                    if (type != 0) {
                        MPGItemStackData.putInt(stack, MPGNBTData.ItemType, type);
                    }
                    return stack;
                })
                .toArray(ItemStack[]::new);
        if (stacks.length == 0) {
            throw new JsonSyntaxException("Item tag '" + tagId + "' has no values");
        }
        return Ingredient.of(stacks);
    }

    private static ItemStack itemStackFromIngredient(JsonObject json) {
        if (!json.has("item")) {
            throw new JsonParseException("An ingredient entry needs an item");
        }
        Item item = itemFromJson(json);
        ItemStack stack = new ItemStack(item);
        if (json.has("type")) {
            MPGItemStackData.putInt(stack, MPGNBTData.ItemType, GsonHelper.getAsInt(json, "type"));
        }
        return stack;
    }

    private static Item itemFromJson(JsonObject json) {
        String itemId = GsonHelper.getAsString(json, json.has("id") ? "id" : "item");
        ResourceLocation resourceLocation = ResourceLocation.parse(itemId);
        Item item = BuiltInRegistries.ITEM.get(resourceLocation);
        if (item == null) {
            throw new JsonSyntaxException("Unknown item '" + itemId + "'");
        }
        return item;
    }

    private static ItemStack itemStackFromJson(JsonObject json) {
        Item item = itemFromJson(json);
        int count = GsonHelper.getAsInt(json, "count", 1);
        ItemStack stack = new ItemStack(item, count);
        if (json.has("nbt")) {
            JsonObject nbt = GsonHelper.getAsJsonObject(json, "nbt");
            if (nbt.has("ManaitaType")) {
                MPGItemStackData.putInt(stack, MPGNBTData.ItemType, GsonHelper.getAsInt(nbt, "ManaitaType"));
            }
        }
        return stack;
    }

    private static JsonArray patternToJson(MPGNBTCraftingRecipe recipe) {
        JsonArray pattern = new JsonArray();
        for (int y = 0; y < recipe.height; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < recipe.width; x++) {
                Ingredient ingredient = recipe.recipeItems.get(x + y * recipe.width);
                row.append(ingredient.isEmpty() ? ' ' : (char) ('A' + x + y * recipe.width));
            }
            pattern.add(row.toString());
        }
        return pattern;
    }

    private static JsonObject keyToJson(MPGNBTCraftingRecipe recipe) {
        JsonObject key = new JsonObject();
        for (int y = 0; y < recipe.height; y++) {
            for (int x = 0; x < recipe.width; x++) {
                Ingredient ingredient = recipe.recipeItems.get(x + y * recipe.width);
                if (ingredient.isEmpty()) {
                    continue;
                }
                String symbol = String.valueOf((char) ('A' + x + y * recipe.width));
                key.add(symbol, ingredientToJson(ingredient));
            }
        }
        return key;
    }

    private static JsonObject ingredientToJson(Ingredient ingredient) {
        ItemStack[] stacks = ingredient.getItems();
        if (stacks.length == 0) {
            throw new JsonSyntaxException("Cannot encode empty ingredient");
        }
        ItemStack stack = stacks[0];
        JsonObject json = new JsonObject();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        json.addProperty("item", itemId.toString());
        int type = MPGNBTIngredient.readType(stack);
        if (type != 0) {
            json.addProperty("type", type);
        }
        return json;
    }

    private static JsonObject itemStackToJson(ItemStack stack) {
        JsonObject json = new JsonObject();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        json.addProperty("id", itemId.toString());
        if (stack.getCount() != 1) {
            json.addProperty("count", stack.getCount());
        }
        int type = MPGItemStackData.getInt(stack, MPGNBTData.ItemType);
        if (type != 0) {
            JsonObject nbt = new JsonObject();
            nbt.addProperty("ManaitaType", type);
            json.add("nbt", nbt);
        }
        return json;
    }

    public static class Serializer implements RecipeSerializer<MPGNBTCraftingRecipe> {
        @Override
        public @NotNull MapCodec<MPGNBTCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, MPGNBTCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
