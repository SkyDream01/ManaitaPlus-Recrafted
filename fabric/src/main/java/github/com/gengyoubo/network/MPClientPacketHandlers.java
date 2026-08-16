package github.com.gengyoubo.network;

import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.network.server.MPChangeEntityDataPacket;
import github.com.gengyoubo.network.server.MPDestroyBlockPacket;
import github.com.gengyoubo.util.MPEntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public final class MPClientPacketHandlers {
    private MPClientPacketHandlers() {
    }

    public static void handleDestroyBlock(MPDestroyBlockPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) {
            return;
        }
        if (!(packet.item() instanceof IMPGDestroy destroyItem)) {
            return;
        }

        BlockPos blockPos = packet.blockPos();
        int range = packet.range();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int x = blockPos.getX() - range; x <= blockPos.getX() + range; x++) {
            for (int y = blockPos.getY() - range; y <= blockPos.getY() + range; y++) {
                for (int z = blockPos.getZ() - range; z <= blockPos.getZ() + range; z++) {
                    mutableBlockPos.set(x, y, z);
                    BlockState blockState = level.getBlockState(mutableBlockPos);
                    if (destroyItem.accept(blockState)) {
                        continue;
                    }

                    level.setBlock(mutableBlockPos, level.getFluidState(mutableBlockPos).createLegacyBlock(), 10);
                    SoundType soundType = blockState.getSoundType();
                    mc.getSoundManager().play(new SimpleSoundInstance(soundType.getHitSound(), SoundSource.BLOCKS,
                            (soundType.getVolume() + 1.0F) / 8.0F, soundType.getPitch() * 0.5F,
                            SoundInstance.createUnseededRandom(), mutableBlockPos));
                }
            }
        }
    }

    public static void handleChangeEntityData(MPChangeEntityDataPacket packet) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(packet.id());
        if (entity == null) {
            return;
        }

        boolean remove = packet.flag() < 0;
        int flags = remove ? -packet.flag() : packet.flag();
        for (MPEntityData entityData : MPEntityData.values()) {
            if ((entityData.getFlag() & flags) != 0) {
                if (remove) {
                    entityData.remove(entity);
                } else {
                    entityData.add(entity);
                }
            }
        }
    }
}
