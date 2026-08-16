package github.com.gengyoubo.MPG.gui;

import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.menu.MPGFurnaceMenu;
import github.com.gengyoubo.common.client.gui.MPGFurnaceScreenBase;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FurnaceManaitaScreen extends MPGFurnaceScreenBase<MPGFurnaceMenu> {
    private static final ResourceLocation UNIFORM_FONT =
            ResourceLocation.withDefaultNamespace("uniform");

    public FurnaceManaitaScreen(MPGFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, MPGConfig.furnace_doubling_value);
    }

    @Override
    protected Component getDoublingComponent() {
        return super.getDoublingComponent().copy().withStyle(style -> style.withFont(UNIFORM_FONT));
    }
}
