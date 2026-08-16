package github.com.gengyoubo.common.network;

import github.com.gengyoubo.common.entity.MPGEntityData;
import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.common.network.payload.MPGChangeEntityDataPayload;
import github.com.gengyoubo.common.network.payload.MPGDestroyBlockPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/** Client-side effects of shared payloads; platform modules only schedule it on the client thread. */
public final class MPGClientPayloadHandler {
    private MPGClientPayloadHandler() {
    }

    public static void handleDestroyBlock(MPGDestroyBlockPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.player == null || !(payload.item() instanceof IMPGDestroy destroyItem)) {
            return;
        }

        BlockPos center = payload.blockPos();
        int range = payload.range();
        BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();
        for (int x = center.getX() - range; x <= center.getX() + range; x++) {
            for (int y = center.getY() - range; y <= center.getY() + range; y++) {
                for (int z = center.getZ() - range; z <= center.getZ() + range; z++) {
                    position.set(x, y, z);
                    BlockState state = level.getBlockState(position);
                    if (destroyItem.accept(state)) {
                        continue;
                    }
                    level.setBlock(position, level.getFluidState(position).createLegacyBlock(), 10);
                    SoundType sound = state.getSoundType();
                    minecraft.getSoundManager().play(new SimpleSoundInstance(
                            sound.getHitSound(), SoundSource.BLOCKS,
                            (sound.getVolume() + 1.0F) / 8.0F, sound.getPitch() * 0.5F,
                            SoundInstance.createUnseededRandom(), position));
                }
            }
        }
    }

    public static void handleChangeEntityData(MPGChangeEntityDataPayload payload) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(payload.entityId());
        if (entity == null) {
            return;
        }

        boolean remove = payload.flags() < 0;
        int flags = remove ? -payload.flags() : payload.flags();
        for (MPGEntityData data : MPGEntityData.values()) {
            if ((data.getFlag() & flags) != 0) {
                if (remove) {
                    data.remove(entity);
                } else {
                    data.add(entity);
                }
            }
        }
    }
}
