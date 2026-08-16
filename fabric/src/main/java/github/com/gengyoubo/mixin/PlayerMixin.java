package github.com.gengyoubo.mixin;

import github.com.gengyoubo.item.armor.MPArmor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void manaitaPlusGeneral$syncArmorState(CallbackInfo ci) {
        MPArmor.syncArmorState((Player) (Object) this);
    }
}

