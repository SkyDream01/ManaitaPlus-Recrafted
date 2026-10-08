// SPDX-License-Identifier: GPL-3.0-only
package github.com.gengyoubo.common.network.payload;

import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/** Client left-click selection; the server validates the held item and reach. */
public record MPGBucketCornerPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<MPGBucketCornerPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(MPGCommon.MOD_ID, "bucket_corner"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MPGBucketCornerPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull MPGBucketCornerPayload decode(RegistryFriendlyByteBuf buffer) {
            return new MPGBucketCornerPayload(BlockPos.STREAM_CODEC.decode(buffer));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, MPGBucketCornerPayload payload) {
            BlockPos.STREAM_CODEC.encode(buffer, payload.pos());
        }
    };

    @Override
    public @NotNull Type<MPGBucketCornerPayload> type() {
        return TYPE;
    }
}
