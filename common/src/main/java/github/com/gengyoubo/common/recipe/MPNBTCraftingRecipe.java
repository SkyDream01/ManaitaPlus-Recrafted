package github.com.gengyoubo.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import github.com.gengyoubo.common.MPGCommon;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MPNBTCraftingRecipe implements CraftingRecipe {
    private static RecipeSerializer<?> registeredSerializer;
    private static final StringRepresentable.EnumCodec<CraftingBookCategory> CATEGORY_CODEC =
            StringRepresentable.fromEnum(CraftingBookCategory::values);

    private final String group;
    private final CraftingBookCategory category;
    private final int width;
    private final int height;
    private final IngredientSpec[] ingredients;
    private final ItemStack result;
    private final boolean showNotification;

    public MPNBTCraftingRecipe(String group, CraftingBookCategory category, int width, int height, IngredientSpec[] ingredients, ItemStack result, boolean showNotification) {
        this.group = group;
        this.category = category;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.result = result;
        this.showNotification = showNotification;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        if (registeredSerializer == null) {
            throw new IllegalStateException("Manaita NBT crafting serializer is not registered yet");
        }
        return registeredSerializer;
    }

    @Override
    public @NotNull String getGroup() {
        return group;
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return category;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public boolean showNotification() {
        return showNotification;
    }

    @Override
    public boolean canCraftInDimensions(int gridWidth, int gridHeight) {
        return gridWidth >= width && gridHeight >= height;
    }

    @Override
    public boolean matches(CraftingInput container, @NotNull Level level) {
        for (int x = 0; x <= container.width() - width; ++x) {
            for (int y = 0; y <= container.height() - height; ++y) {
                if (matches(container, x, y, true) || matches(container, x, y, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matches(CraftingInput container, int offsetX, int offsetY, boolean mirrored) {
        for (int x = 0; x < container.width(); ++x) {
            for (int y = 0; y < container.height(); ++y) {
                int patternX = x - offsetX;
                int patternY = y - offsetY;
                IngredientSpec expected = IngredientSpec.EMPTY;
                if (patternX >= 0 && patternY >= 0 && patternX < width && patternY < height) {
                    if (mirrored) {
                        expected = ingredients[width - patternX - 1 + patternY * width];
                    } else {
                        expected = ingredients[patternX + patternY * width];
                    }
                }

                if (!expected.test(container.getItem(x + y * container.width()))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput container, @NotNull HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> displayIngredients = NonNullList.createWithCapacity(this.ingredients.length);
        for (IngredientSpec ingredient : this.ingredients) {
            displayIngredients.add(ingredient.toDisplayIngredient());
        }
        return displayIngredients;
    }

    public static class Serializer implements RecipeSerializer<MPNBTCraftingRecipe> {
        public Serializer() {
            registeredSerializer = this;
        }
        private static final MapCodec<MPNBTCraftingRecipe> CODEC = new MapCodec<>() {
            @Override
            public <T> DataResult<MPNBTCraftingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
                return decodeRecipe(ops, input);
            }

            @Override
            public <T> RecordBuilder<T> encode(MPNBTCraftingRecipe input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return encodeRecipe(input, ops, prefix);
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of("group", "category", "pattern", "key", "result", "show_notification").map(ops::createString);
            }
        };
        private static final StreamCodec<RegistryFriendlyByteBuf, MPNBTCraftingRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::encodeToNetwork, Serializer::decodeFromNetwork);

        @Override
        public @NotNull MapCodec<MPNBTCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, MPNBTCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static MPNBTCraftingRecipe decodeFromNetwork(RegistryFriendlyByteBuf buf) {
            int width = buf.readVarInt();
            int height = buf.readVarInt();
            String group = buf.readUtf();
            CraftingBookCategory category = CraftingBookCategory.STREAM_CODEC.decode(buf);
            IngredientSpec[] ingredients = new IngredientSpec[width * height];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = IngredientSpec.STREAM_CODEC.decode(buf);
            }
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            boolean showNotification = buf.readBoolean();
            return new MPNBTCraftingRecipe(group, category, width, height, ingredients, result, showNotification);
        }

        private static void encodeToNetwork(RegistryFriendlyByteBuf buf, MPNBTCraftingRecipe recipe) {
            buf.writeVarInt(recipe.width);
            buf.writeVarInt(recipe.height);
            buf.writeUtf(recipe.group);
            CraftingBookCategory.STREAM_CODEC.encode(buf, recipe.category);
            for (IngredientSpec ingredient : recipe.ingredients) {
                IngredientSpec.STREAM_CODEC.encode(buf, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            buf.writeBoolean(recipe.showNotification);
        }

        private static <T> DataResult<MPNBTCraftingRecipe> decodeRecipe(DynamicOps<T> ops, MapLike<T> input) {
            try {
                JsonObject json = toJsonObject(ops, input);
                String group = GsonHelper.getAsString(json, "group", "");
                CraftingBookCategory category = CATEGORY_CODEC.byName(GsonHelper.getAsString(json, "category", null), CraftingBookCategory.MISC);
                String[] pattern = shrink(patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
                if (pattern.length == 0) {
                    throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
                }
                int width = pattern[0].length();
                int height = pattern.length;
                Map<Character, IngredientSpec> key = keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
                IngredientSpec[] ingredients = dissolvePattern(pattern, key, width, height);
                ItemStack result = resultFromJson(GsonHelper.getAsJsonObject(json, "result"));
                boolean showNotification = GsonHelper.getAsBoolean(json, "show_notification", true);
                return DataResult.success(new MPNBTCraftingRecipe(group, category, width, height, ingredients, result, showNotification));
            } catch (Exception exception) {
                return DataResult.error(exception::getMessage);
            }
        }

        private static <T> RecordBuilder<T> encodeRecipe(MPNBTCraftingRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> builder) {
            builder.add("group", ops.createString(recipe.group));
            builder.add("category", ops.createString(recipe.category.getSerializedName()));
            builder.add("width", ops.createInt(recipe.width));
            builder.add("height", ops.createInt(recipe.height));
            builder.add("ingredients", JsonOps.INSTANCE.convertTo(ops, encodeIngredients(recipe.ingredients, recipe.width, recipe.height)));
            builder.add("result", JsonOps.INSTANCE.convertTo(ops, encodeResult(recipe.result)));
            builder.add("show_notification", ops.createBoolean(recipe.showNotification));
            return builder;
        }

        private static JsonObject encodeResult(ItemStack stack) {
            JsonObject result = (JsonObject) ItemStack.STRICT_CODEC.encodeStart(JsonOps.INSTANCE, stack)
                    .getOrThrow(JsonSyntaxException::new);
            int type = github.com.gengyoubo.common.util.MPGItemStackData.getInt(stack, MPGNBTData.ItemType);
            if (type != 0) {
                JsonObject nbt = new JsonObject();
                nbt.addProperty(MPGNBTData.ItemType, type);
                result.add("nbt", nbt);
            }
            return result;
        }

        private static JsonArray encodeIngredients(IngredientSpec[] ingredients, int width, int height) {
            JsonArray array = new JsonArray();
            for (int y = 0; y < height; y++) {
                JsonArray row = new JsonArray();
                for (int x = 0; x < width; x++) {
                    row.add(ingredients[x + y * width].toJson());
                }
                array.add(row);
            }
            return array;
        }

        private static ItemStack resultFromJson(JsonObject json) {
            JsonObject stackJson = json.deepCopy();
            JsonObject nbt = stackJson.has("nbt") ? GsonHelper.getAsJsonObject(stackJson, "nbt") : null;
            if (stackJson.has("item") && !stackJson.has("id")) {
                stackJson.add("id", stackJson.get("item"));
                stackJson.remove("item");
            }
            String itemId = GsonHelper.getAsString(stackJson, "id", null);
            if (itemId != null) {
                ResourceLocation resultId = ResourceLocation.tryParse(itemId);
                if (resultId == null || !BuiltInRegistries.ITEM.containsKey(resultId)) {
                    org.slf4j.LoggerFactory.getLogger(MPGCommon.MOD_ID)
                            .warn("Skipping custom recipe result for missing item {}", itemId);
                    return ItemStack.EMPTY;
                }
            }
            stackJson.remove("nbt");
            ItemStack stack = ItemStack.STRICT_CODEC.parse(JsonOps.INSTANCE, stackJson).getOrThrow(JsonSyntaxException::new);
            if (nbt != null) {
                int type = readType(nbt);
                if (type >= 0) {
                    github.com.gengyoubo.common.util.MPGItemStackData.putInt(stack, MPGNBTData.ItemType, type);
                }
            }
            return stack;
        }

        private static int readType(JsonObject json) {
            if (json.has(MPGNBTData.ItemType)) {
                return GsonHelper.getAsInt(json, MPGNBTData.ItemType);
            }
            if (json.has("ManaitaPlusLegacyType")) {
                return GsonHelper.getAsInt(json, "ManaitaPlusLegacyType");
            }
            if (json.has("ManaitaType")) {
                return GsonHelper.getAsInt(json, "ManaitaType");
            }
            return -1;
        }

        private static IngredientSpec[] dissolvePattern(String[] pattern, Map<Character, IngredientSpec> key, int width, int height) {
            IngredientSpec[] ingredients = new IngredientSpec[width * height];
            Arrays.fill(ingredients, IngredientSpec.EMPTY);
            Map<Character, IngredientSpec> remaining = new HashMap<>(key);
            remaining.remove(' ');

            for (int y = 0; y < pattern.length; ++y) {
                for (int x = 0; x < pattern[y].length(); ++x) {
                    char symbol = pattern[y].charAt(x);
                    IngredientSpec ingredient = key.get(symbol);
                    if (ingredient == null) {
                        throw new JsonSyntaxException("Pattern references symbol '" + symbol + "' but it's not defined in the key");
                    }
                    remaining.remove(symbol);
                    ingredients[x + width * y] = ingredient;
                }
            }

            if (!remaining.isEmpty()) {
                throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + remaining.keySet());
            }
            return ingredients;
        }

        private static Map<Character, IngredientSpec> keyFromJson(JsonObject json) {
            Map<Character, IngredientSpec> map = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getKey().length() != 1) {
                    throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol");
                }
                char symbol = entry.getKey().charAt(0);
                if (symbol == ' ') {
                    throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
                }
                map.put(symbol, IngredientSpec.fromJson(GsonHelper.convertToJsonObject(entry.getValue(), "key")));
            }
            map.put(' ', IngredientSpec.EMPTY);
            return map;
        }

        private static String[] patternFromJson(JsonArray json) {
            String[] pattern = new String[json.size()];
            if (pattern.length == 0) {
                throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
            }
            if (pattern.length > 3) {
                throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
            }

            for (int i = 0; i < pattern.length; ++i) {
                String row = GsonHelper.convertToString(json.get(i), "pattern[" + i + "]");
                if (row.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }
                if (i > 0 && pattern[0].length() != row.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }
                pattern[i] = row;
            }
            return pattern;
        }

        private static String[] shrink(String... pattern) {
            int firstColumn = Integer.MAX_VALUE;
            int lastColumn = 0;
            int leadingEmptyRows = 0;
            int trailingEmptyRows = 0;

            for (int row = 0; row < pattern.length; ++row) {
                String line = pattern[row];
                firstColumn = Math.min(firstColumn, firstNonSpace(line));
                int last = lastNonSpace(line);
                lastColumn = Math.max(lastColumn, last);
                if (last < 0) {
                    if (leadingEmptyRows == row) {
                        ++leadingEmptyRows;
                    }
                    ++trailingEmptyRows;
                } else {
                    trailingEmptyRows = 0;
                }
            }

            if (pattern.length == trailingEmptyRows) {
                return new String[0];
            }

            String[] shrunk = new String[pattern.length - trailingEmptyRows - leadingEmptyRows];
            for (int i = 0; i < shrunk.length; ++i) {
                shrunk[i] = pattern[i + leadingEmptyRows].substring(firstColumn, lastColumn + 1);
            }
            return shrunk;
        }

        private static int firstNonSpace(String line) {
            int i = 0;
            while (i < line.length() && line.charAt(i) == ' ') {
                i++;
            }
            return i;
        }

        private static int lastNonSpace(String line) {
            int i = line.length() - 1;
            while (i >= 0 && line.charAt(i) == ' ') {
                --i;
            }
            return i;
        }

        private static <T> JsonObject toJsonObject(DynamicOps<T> ops, MapLike<T> input) {
            JsonObject json = new JsonObject();
            input.entries().forEach(entry -> {
                JsonElement key = ops.convertTo(JsonOps.INSTANCE, entry.getFirst());
                JsonElement value = ops.convertTo(JsonOps.INSTANCE, entry.getSecond());
                json.add(key.getAsString(), value);
            });
            return json;
        }
    }

    private record IngredientSpec(Ingredient ingredient, int requiredType) {
        private static final IngredientSpec EMPTY = new IngredientSpec(Ingredient.EMPTY, Integer.MIN_VALUE);
        private static final StreamCodec<RegistryFriendlyByteBuf, IngredientSpec> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                IngredientSpec::ingredient,
                ByteBufCodecs.INT,
                IngredientSpec::requiredType,
                IngredientSpec::new
        );

        private boolean test(ItemStack stack) {
            if (this == EMPTY) {
                return stack.isEmpty();
            }
            if (!ingredient.test(stack)) {
                return false;
            }
            if (requiredType == Integer.MIN_VALUE) {
                return true;
            }
            return github.com.gengyoubo.common.util.MPGItemStackData.getInt(stack, MPGNBTData.ItemType) == requiredType;
        }

        private JsonObject toJson() {
            JsonObject json = ingredient.isEmpty()
                    ? new JsonObject()
                    : (JsonObject) Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow(JsonSyntaxException::new);
            if (requiredType != Integer.MIN_VALUE) {
                json.addProperty("type", requiredType);
            }
            return json;
        }

        private static IngredientSpec fromJson(JsonObject json) {
            JsonObject ingredientJson = json.deepCopy();
            ingredientJson.remove("type");
            Ingredient ingredient = Ingredient.CODEC.parse(JsonOps.INSTANCE, ingredientJson).getOrThrow(JsonSyntaxException::new);
            int requiredType = Integer.MIN_VALUE;
            if (json.has("type")) {
                requiredType = GsonHelper.getAsInt(json, "type");
            }
            return new IngredientSpec(ingredient, requiredType);
        }

        private Ingredient toDisplayIngredient() {
            if (this == EMPTY || ingredient.isEmpty() || requiredType == Integer.MIN_VALUE) {
                return ingredient;
            }

            ItemStack[] stacks = Arrays.stream(ingredient.getItems())
                    .map(ItemStack::copy)
                    .peek(stack -> github.com.gengyoubo.common.util.MPGItemStackData.putInt(stack, MPGNBTData.ItemType, requiredType))
                    .toArray(ItemStack[]::new);
            return stacks.length == 0 ? ingredient : Ingredient.of(stacks);
        }
    }
}
