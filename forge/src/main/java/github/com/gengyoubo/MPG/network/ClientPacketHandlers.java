package github.com.gengyoubo.MPG.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.MPG.network.server.ChangeEntityDataPacket;
import github.com.gengyoubo.MPG.network.server.DestroyBlockPacket;
import github.com.gengyoubo.MPG.util.MPGEntityData;
import github.com.gengyoubo.MPG.util.MPUtils;

public final class ClientPacketHandlers {
    private ClientPacketHandlers() {}

    public static void handleDestroyBlock(DestroyBlockPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) return;
        if (!(packet.item() instanceof IMPGDestroy des)) return;

        BlockPos blockPos = packet.blockPos();
        int range = packet.range();
        int xM = blockPos.getX() + range;
        int yM = blockPos.getY() + range;
        int zM = blockPos.getZ() + range;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int x = blockPos.getX() - range; x <= xM; x++) {
            for (int y = blockPos.getY() - range; y <= yM; y++) {
                for (int z = blockPos.getZ() - range; z <= zM; z++) {
                    BlockState blockState = level.getBlockState(mutableBlockPos.set(x, y, z));
                    if (des.accept(blockState)) continue;

                    MPUtils.setBlock(level, mutableBlockPos, level.getFluidState(mutableBlockPos).createLegacyBlock(), 10);
                    SoundType soundtype = blockState.getSoundType(level, mutableBlockPos, mc.player);
                    mc.getSoundManager().play(new SimpleSoundInstance(soundtype.getHitSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F, SoundInstance.createUnseededRandom(), mutableBlockPos));
                }
            }
        }
    }

    public static void handleChangeEntityData(ChangeEntityDataPacket packet) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        Entity entity = level.getEntity(packet.id());
        if (entity == null) return;

        boolean remove = packet.flag() < 0;
        int flags = remove ? -packet.flag() : packet.flag();
        for (MPGEntityData entityList : MPGEntityData.values()) {
            if ((entityList.getFlag() & flags) != 0) {
                if (remove) {
                    entityList.remove(entity);
                } else {
                    entityList.add(entity);
                }
            }
        }
    }
}
