package github.com.gengyoubo.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class MPNBTCraftingRecipe implements CraftingRecipe {
    private static RecipeSerializer<MPNBTCraftingRecipe> registeredSerializer;
    private static final StringRepresentable.EnumCodec<CraftingBookCategory> CATEGORY_CODEC =
            StringRepresentable.fromEnum(CraftingBookCategory::values);

    private final String group;
    private final CraftingBookCategory category;
    private final int width;
    private final int height;
    private final IngredientSpec[] ingredients;
    private final ItemStackTemplate result;
    private final boolean showNotification;
    private @Nullable PlacementInfo placementInfo;

    public MPNBTCraftingRecipe(String group, CraftingBookCategory category, int width, int height, IngredientSpec[] ingredients, ItemStackTemplate result, boolean showNotification) {
        this.group = group;
        this.category = category;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.result = result;
        this.showNotification = showNotification;
    }

    @Override
    public @NotNull RecipeSerializer<? extends CraftingRecipe> getSerializer() {
        if (registeredSerializer == null) {
            throw new IllegalStateException("Manaita NBT crafting serializer is not registered yet");
        }
        return registeredSerializer;
    }

    @Override
    public @NotNull String group() {
        return group;
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return category;
    }

    @Override
    public boolean showNotification() {
        return showNotification;
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
    public @NotNull ItemStack assemble(@NotNull CraftingInput container) {
        return result.create();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (placementInfo == null) {
            List<Optional<Ingredient>> displayIngredients = new ArrayList<>(this.ingredients.length);
            for (IngredientSpec ingredient : this.ingredients) {
                displayIngredients.add(ingredient.toOptionalIngredient());
            }
            placementInfo = PlacementInfo.createFromOptionals(displayIngredients);
        }
        return placementInfo;
    }

    @Override
    public @NotNull List<RecipeDisplay> display() {
        List<SlotDisplay> slotDisplays = new ArrayList<>(this.ingredients.length);
        for (IngredientSpec ingredient : this.ingredients) {
            slotDisplays.add(ingredient.toDisplaySlotDisplay());
        }
        return List.of(new ShapedCraftingRecipeDisplay(
                width,
                height,
                slotDisplays,
                new SlotDisplay.ItemStackSlotDisplay(result),
                new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }

    public static class Serializer {
        private Serializer() {
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

        /** Creates (once) the registered serializer instance. Recipe serializers are records in 26.3,
         * so this factory replaces the old {@code implements RecipeSerializer} constructor hook. */
        public static RecipeSerializer<MPNBTCraftingRecipe> create() {
            if (registeredSerializer == null) {
                registeredSerializer = new RecipeSerializer<>(CODEC, STREAM_CODEC);
            }
            return registeredSerializer;
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
            ItemStackTemplate result = ItemStackTemplate.STREAM_CODEC.decode(buf);
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
            ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.result);
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
                DynamicOps<JsonElement> jsonOps = registryJsonOps(ops);
                Map<Character, IngredientSpec> key = keyFromJson(GsonHelper.getAsJsonObject(json, "key"), jsonOps);
                IngredientSpec[] ingredients = dissolvePattern(pattern, key, width, height);
                ItemStackTemplate result = resultFromJson(GsonHelper.getAsJsonObject(json, "result"), jsonOps);
                boolean showNotification = GsonHelper.getAsBoolean(json, "show_notification", true);
                return DataResult.success(new MPNBTCraftingRecipe(group, category, width, height, ingredients, result, showNotification));
            } catch (Exception exception) {
                return DataResult.error(exception::getMessage);
            }
        }

        private static <T> RecordBuilder<T> encodeRecipe(MPNBTCraftingRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> builder) {
            builder.add("group", ops.createString(recipe.group));
            builder.add("category", ops.createString(recipe.category.getSerializedName()));
            DynamicOps<JsonElement> jsonOps = registryJsonOps(ops);
            JsonArray pattern = new JsonArray();
            JsonObject key = new JsonObject();
            for (int y = 0; y < recipe.height; y++) {
                StringBuilder row = new StringBuilder(recipe.width);
                for (int x = 0; x < recipe.width; x++) {
                    int index = x + y * recipe.width;
                    IngredientSpec ingredient = recipe.ingredients[index];
                    if (ingredient.ingredient == null) {
                        row.append(' ');
                    } else {
                        char symbol = (char) ('A' + index);
                        row.append(symbol);
                        key.add(String.valueOf(symbol), ingredient.toJson(jsonOps));
                    }
                }
                pattern.add(row.toString());
            }
            builder.add("pattern", JsonOps.INSTANCE.convertTo(ops, pattern));
            builder.add("key", JsonOps.INSTANCE.convertTo(ops, key));
            builder.add("result", JsonOps.INSTANCE.convertTo(ops, encodeResult(recipe.result, jsonOps)));
            builder.add("show_notification", ops.createBoolean(recipe.showNotification));
            return builder;
        }

        private static DynamicOps<JsonElement> registryJsonOps(DynamicOps<?> ops) {
            return ops instanceof RegistryOps<?> registryOps ? registryOps.withParent(JsonOps.INSTANCE) : JsonOps.INSTANCE;
        }

        private static JsonObject encodeResult(ItemStackTemplate stack, DynamicOps<JsonElement> ops) {
            return ItemStackTemplate.CODEC.encodeStart(ops, stack).getOrThrow(JsonSyntaxException::new).getAsJsonObject();
        }

        private static ItemStackTemplate resultFromJson(JsonObject json, DynamicOps<JsonElement> ops) {
            JsonObject stackJson = json.deepCopy();
            JsonObject nbt = stackJson.has("nbt") ? GsonHelper.getAsJsonObject(stackJson, "nbt") : null;
            if (stackJson.has("item") && !stackJson.has("id")) {
                stackJson.add("id", stackJson.get("item"));
                stackJson.remove("item");
            }
            stackJson.remove("nbt");
            if (nbt != null) {
                int type = readType(nbt);
                if (type >= 0) {
                    JsonObject components = GsonHelper.getAsJsonObject(stackJson, "components", new JsonObject());
                    CustomData data = components.has("minecraft:custom_data")
                            ? CustomData.CODEC.parse(ops, components.get("minecraft:custom_data")).getOrThrow(JsonSyntaxException::new)
                            : CustomData.EMPTY;
                    data = data.update(tag -> tag.putInt(MPGNBTData.ItemType, type));
                    components.add("minecraft:custom_data", CustomData.CODEC.encodeStart(ops, data).getOrThrow(JsonSyntaxException::new));
                    stackJson.add("components", components);
                }
            }
            // Recipes load before item components in 26.3; construct stacks only when crafting.
            return ItemStackTemplate.CODEC.parse(ops, stackJson).getOrThrow(JsonSyntaxException::new);
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

        private static Map<Character, IngredientSpec> keyFromJson(JsonObject json, DynamicOps<JsonElement> ops) {
            Map<Character, IngredientSpec> map = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getKey().length() != 1) {
                    throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol");
                }
                char symbol = entry.getKey().charAt(0);
                if (symbol == ' ') {
                    throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
                }
                map.put(symbol, IngredientSpec.fromJson(entry.getValue(), ops));
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

    private record IngredientSpec(@Nullable Ingredient ingredient, int requiredType) {
        private static final IngredientSpec EMPTY = new IngredientSpec(null, Integer.MIN_VALUE);
        private static final StreamCodec<RegistryFriendlyByteBuf, IngredientSpec> STREAM_CODEC = StreamCodec.composite(
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
                spec -> Optional.ofNullable(spec.ingredient),
                ByteBufCodecs.INT,
                IngredientSpec::requiredType,
                (ingredient, requiredType) -> new IngredientSpec(ingredient.orElse(null), requiredType)
        );

        private boolean test(ItemStack stack) {
            if (this == EMPTY) {
                return stack.isEmpty();
            }
            boolean itemMatches = ingredient == null ? stack.isEmpty() : ingredient.test(stack);
            if (!itemMatches) {
                return false;
            }
            if (requiredType == Integer.MIN_VALUE) {
                return true;
            }
            return github.com.gengyoubo.common.util.MPGItemStackData.getInt(stack, MPGNBTData.ItemType) == requiredType;
        }

        private JsonElement toJson(DynamicOps<JsonElement> ops) {
            JsonElement json = ingredient == null
                    ? new JsonObject()
                    : Ingredient.CODEC.encodeStart(ops, ingredient).getOrThrow(JsonSyntaxException::new);
            if (requiredType != Integer.MIN_VALUE) {
                JsonObject typed = new JsonObject();
                typed.add("ingredient", json);
                typed.addProperty("type", requiredType);
                return typed;
            }
            return json;
        }

        private static IngredientSpec fromJson(JsonElement json, DynamicOps<JsonElement> ops) {
            int requiredType = Integer.MIN_VALUE;
            if (json.isJsonObject() && json.getAsJsonObject().has("type")) {
                requiredType = GsonHelper.getAsInt(json.getAsJsonObject(), "type");
            }
            JsonElement ingredientJson = normalizeIngredient(json);
            if (ingredientJson.isJsonObject() && ingredientJson.getAsJsonObject().entrySet().isEmpty()) {
                return new IngredientSpec(null, requiredType);
            }
            Ingredient ingredient = Ingredient.CODEC.parse(ops, ingredientJson).getOrThrow(JsonSyntaxException::new);
            return new IngredientSpec(ingredient, requiredType);
        }

        /** Keep legacy typed recipes readable while using the 26.3 ingredient codec and registry context. */
        private static JsonElement normalizeIngredient(JsonElement json) {
            if (json.isJsonArray()) {
                JsonArray items = new JsonArray();
                json.getAsJsonArray().forEach(entry -> items.add(normalizeIngredient(entry)));
                return items;
            }
            if (json.isJsonObject()) {
                JsonObject object = json.getAsJsonObject();
                if (object.has("ingredient")) {
                    return normalizeIngredient(object.get("ingredient"));
                }
                if (object.has("item")) {
                    return object.get("item");
                }
                if (object.has("tag")) {
                    return new JsonPrimitive("#" + GsonHelper.getAsString(object, "tag"));
                }
            }
            return json;
        }

        private Optional<Ingredient> toOptionalIngredient() {
            return Optional.ofNullable(ingredient);
        }

        /** 26.3: {@link Ingredient} can no longer carry per-stack custom data, so the typed display
         * variants are expressed as stack slot displays instead of a decorated ingredient. */
        @SuppressWarnings("deprecation")
        private SlotDisplay toDisplaySlotDisplay() {
            if (this == EMPTY || ingredient == null) {
                return SlotDisplay.Empty.INSTANCE;
            }
            if (requiredType == Integer.MIN_VALUE) {
                return ingredient.display();
            }

            List<SlotDisplay> typedStacks = new ArrayList<>();
            ingredient.items().forEach(item -> {
                CompoundTag tag = new CompoundTag();
                tag.putInt(MPGNBTData.ItemType, requiredType);
                DataComponentPatch patch = DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(tag)).build();
                typedStacks.add(new SlotDisplay.ItemStackSlotDisplay(new ItemStackTemplate(item, 1, patch)));
            });
            return typedStacks.isEmpty() ? ingredient.display() : new SlotDisplay.Composite(typedStacks);
        }
    }
}
