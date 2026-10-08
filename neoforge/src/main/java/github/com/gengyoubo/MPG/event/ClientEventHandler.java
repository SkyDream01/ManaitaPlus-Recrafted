package github.com.gengyoubo.MPG.event;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.core.MPGKeyBoardCore;
import github.com.gengyoubo.MPG.item.MPGBucketItem;
import github.com.gengyoubo.MPG.network.Networking;
import github.com.gengyoubo.common.event.MPGClientEventLogic;
import github.com.gengyoubo.common.network.payload.MPGKeyPressPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Optional;

public class ClientEventHandler {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private static final ContextKey<Optional<MPGBucketItem.Selection>> BUCKET_PREVIEW = new ContextKey<>(
            Identifier.fromNamespaceAndPath(MPG.MODID, "bucket_preview"));
    private static boolean registered;
    private static boolean legacyBindingsChecked;
    private OptionsScreen configOptionsScreen;
    private Button configButton;
    private Button doneButton;

    public static void register() {
        if (!registered) {
            NeoForge.EVENT_BUS.register(new ClientEventHandler());
            registered = true;
            MPG.LOGGER.info("Registered client runtime event handlers");
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (MINECRAFT.player == null) {
            return;
        }
        while (MPGKeyBoardCore.MESSAGE_KEY != null && MPGKeyBoardCore.MESSAGE_KEY.consumeClick()) {
            MPGClientEventLogic.handleMainHandKey(MINECRAFT.player);
            Networking.sendToServer(new MPGKeyPressPayload((byte) 0));
        }
        while (MPGKeyBoardCore.MESSAGE_ARMOR_KEY != null && MPGKeyBoardCore.MESSAGE_ARMOR_KEY.consumeClick()) {
            MPGClientEventLogic.handleArmorKey(MINECRAFT.player);
            Networking.sendToServer(new MPGKeyPressPayload((byte) 1));
        }
        while (MPGKeyBoardCore.PAXEL_KEY != null && MPGKeyBoardCore.PAXEL_KEY.consumeClick()) {
            MPGClientEventLogic.handlePaxelKey(MINECRAFT.player);
            Networking.sendToServer(new MPGKeyPressPayload((byte) 2));
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        migrateLegacyBindings();
    }

    @SubscribeEvent
    public void onOptionsInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof OptionsScreen optionsScreen)) {
            return;
        }
        Button done = event.getListenersList().stream()
                .filter(Button.class::isInstance)
                .map(Button.class::cast)
                .filter(button -> button.getMessage().equals(CommonComponents.GUI_DONE))
                .findFirst().orElse(null);
        if (done == null) {
            return;
        }
        configOptionsScreen = optionsScreen;
        doneButton = done;
        configButton = Button.builder(Component.translatable("options.manaita_plus_recrafted.customize"),
                button -> ModList.get().getModContainerById(MPG.MODID).ifPresent(container ->
                        MINECRAFT.gui.setScreen(new ConfigurationScreen(container, optionsScreen)))).build();
        positionConfigButton();
        event.addListener(configButton);
    }

    @SubscribeEvent
    public void onOptionsRender(ScreenEvent.Render.Pre event) {
        if (event.getScreen() == configOptionsScreen && configButton != null) {
            positionConfigButton();
        }
    }

    private void positionConfigButton() {
        int center = configOptionsScreen.width / 2;
        int buttonWidth = Math.min(150, Math.max(70, (configOptionsScreen.width - 16) / 2));
        doneButton.setWidth(buttonWidth);
        doneButton.setX(center + 4);
        configButton.setWidth(buttonWidth);
        configButton.setX(center - buttonWidth - 4);
        configButton.setY(doneButton.getY());
    }

    @SubscribeEvent
    public void onExtractLevel(ExtractLevelRenderStateEvent event) {
        MPGBucketItem.Selection selection = null;
        if (MINECRAFT.player != null) {
            ItemStack stack = MPGBucketItem.activeBatchStack(MINECRAFT.player);
            if (stack != null) {
                selection = MPGBucketItem.selection(stack, event.getLevel());
            }
        }
        event.getRenderState().setRenderData(BUCKET_PREVIEW, Optional.ofNullable(selection));
    }

    @SubscribeEvent
    public void onSubmitGeometry(SubmitCustomGeometryEvent event) {
        Optional<MPGBucketItem.Selection> stored = event.getLevelRenderState().getRenderData(BUCKET_PREVIEW);
        if (stored == null || stored.isEmpty()) {
            return;
        }
        MPGBucketItem.Selection selection = stored.get();
        DrawableGizmoPrimitives preview = new DrawableGizmoPrimitives();
        BlockPos first = selection.first();
        if (selection.second() == null) {
            double x = first.getX() + 0.5;
            double y = first.getY() + 0.5;
            double z = first.getZ() + 0.5;
            int color = 0xFF36DFFF;
            preview.addLine(new Vec3(x - 0.5, y, z), new Vec3(x + 0.5, y, z), color, 3.0F);
            preview.addLine(new Vec3(x, y - 0.5, z), new Vec3(x, y + 0.5, z), color, 3.0F);
            preview.addLine(new Vec3(x, y, z - 0.5), new Vec3(x, y, z + 0.5), color, 3.0F);
        } else {
            addPreviewBox(preview, selection);
        }
        preview.submit(event.getSubmitNodeCollector(), event.getLevelRenderState().cameraRenderState, false);
    }

    private static void addPreviewBox(DrawableGizmoPrimitives preview, MPGBucketItem.Selection selection) {
        BlockPos first = selection.first();
        BlockPos second = selection.second();
        double x0 = Math.min(first.getX(), second.getX());
        double y0 = Math.min(first.getY(), second.getY());
        double z0 = Math.min(first.getZ(), second.getZ());
        double x1 = Math.max(first.getX(), second.getX()) + 1.0;
        double y1 = Math.max(first.getY(), second.getY()) + 1.0;
        double z1 = Math.max(first.getZ(), second.getZ()) + 1.0;
        int edge = selection.valid() ? 0xFF36DFFF : 0xFFFF5555;
        int face = selection.valid() ? 0x3036DFFF : 0x30FF5555;

        for (double x : new double[]{x0, x1}) {
            for (double y : new double[]{y0, y1}) {
                preview.addLine(new Vec3(x, y, z0), new Vec3(x, y, z1), edge, 2.0F);
            }
        }
        for (double x : new double[]{x0, x1}) {
            for (double z : new double[]{z0, z1}) {
                preview.addLine(new Vec3(x, y0, z), new Vec3(x, y1, z), edge, 2.0F);
            }
        }
        for (double y : new double[]{y0, y1}) {
            for (double z : new double[]{z0, z1}) {
                preview.addLine(new Vec3(x0, y, z), new Vec3(x1, y, z), edge, 2.0F);
            }
        }
        preview.addQuad(new Vec3(x0, y0, z0), new Vec3(x1, y0, z0),
                new Vec3(x1, y0, z1), new Vec3(x0, y0, z1), face);
        preview.addQuad(new Vec3(x0, y1, z0), new Vec3(x1, y1, z0),
                new Vec3(x1, y1, z1), new Vec3(x0, y1, z1), face);
        preview.addQuad(new Vec3(x0, y0, z0), new Vec3(x1, y0, z0),
                new Vec3(x1, y1, z0), new Vec3(x0, y1, z0), face);
        preview.addQuad(new Vec3(x0, y0, z1), new Vec3(x1, y0, z1),
                new Vec3(x1, y1, z1), new Vec3(x0, y1, z1), face);
        preview.addQuad(new Vec3(x0, y0, z0), new Vec3(x0, y0, z1),
                new Vec3(x0, y1, z1), new Vec3(x0, y1, z0), face);
        preview.addQuad(new Vec3(x1, y0, z0), new Vec3(x1, y0, z1),
                new Vec3(x1, y1, z1), new Vec3(x1, y1, z0), face);
    }

    private static void migrateLegacyBindings() {
        if (legacyBindingsChecked || MPGKeyBoardCore.MESSAGE_KEY == null
                || MPGKeyBoardCore.MESSAGE_ARMOR_KEY == null || MPGKeyBoardCore.PAXEL_KEY == null) {
            return;
        }
        legacyBindingsChecked = true;

        InputConstants.Type keyboard = InputConstants.Type.KEYBOARD;
        // Only migrate the complete set of incorrect defaults from the first 26.3 port.
        // A single matching key may be an intentional user binding.
        if (!MPGKeyBoardCore.MESSAGE_KEY.getKey().equals(keyboard.getOrCreate(88))
                || !MPGKeyBoardCore.MESSAGE_ARMOR_KEY.getKey().equals(keyboard.getOrCreate(86))
                || !MPGKeyBoardCore.PAXEL_KEY.getKey().equals(keyboard.getOrCreate(67))) {
            return;
        }

        MPGKeyBoardCore.MESSAGE_KEY.setKey(keyboard.getOrCreate(InputConstants.KEY_X));
        MPGKeyBoardCore.MESSAGE_ARMOR_KEY.setKey(keyboard.getOrCreate(InputConstants.KEY_V));
        MPGKeyBoardCore.PAXEL_KEY.setKey(keyboard.getOrCreate(InputConstants.KEY_C));
        KeyMapping.resetMapping();
        MINECRAFT.options.save();
        MPG.LOGGER.info("Migrated legacy Manaita key bindings to X, V, and C");
    }
}
