package github.com.gengyoubo.common.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import github.com.gengyoubo.common.entity.MPGLightningBoltBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Random;

/** Shared renderer for the visual-only MPG lightning entity. */
public class MPGLightningBoltRendererBase<T extends MPGLightningBoltBase> extends EntityRenderer<T> {
    public MPGLightningBoltRendererBase(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T bolt, float yaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight) {
        float[] xOffsets = new float[8];
        float[] zOffsets = new float[8];
        float x = 0.0F;
        float z = 0.0F;
        RandomSource pathRandom = RandomSource.create(bolt.seed);

        for (int i = 7; i >= 0; i--) {
            xOffsets[i] = x;
            zOffsets[i] = z;
            x += pathRandom.nextInt(11) - 5;
            z += pathRandom.nextInt(11) - 5;
        }

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();
        Random colorRandom = new Random(bolt.seed);
        for (int layer = 0; layer < 4; layer++) {
            RandomSource branchRandom = RandomSource.create(bolt.seed);
            for (int branch = 0; branch < 3; branch++) {
                int start = branch == 0 ? 7 : 7 - branch;
                int end = branch == 0 ? 0 : start - 2;
                float branchX = xOffsets[start] - x;
                float branchZ = zOffsets[start] - z;

                for (int segment = start; segment >= end; segment--) {
                    float previousX = branchX;
                    float previousZ = branchZ;
                    if (branch == 0) {
                        branchX += branchRandom.nextInt(11) - 5;
                        branchZ += branchRandom.nextInt(11) - 5;
                    } else {
                        branchX += branchRandom.nextInt(31) - 15;
                        branchZ += branchRandom.nextInt(31) - 15;
                    }

                    float upperRadius = 0.1F + layer * 0.2F;
                    float lowerRadius = 0.1F + layer * 0.2F;
                    if (branch == 0) {
                        upperRadius *= segment * 0.1F + 1.0F;
                        lowerRadius *= (segment - 1.0F) * 0.1F + 1.0F;
                    }

                    float red = colorRandom.nextFloat();
                    float green = colorRandom.nextFloat();
                    float blue = colorRandom.nextFloat();
                    float alpha = 0.375F;
                    quad(matrix, consumer, branchX, branchZ, segment, previousX, previousZ,
                            red, green, blue, alpha, upperRadius, lowerRadius,
                            false, false, true, false);
                    quad(matrix, consumer, branchX, branchZ, segment, previousX, previousZ,
                            red, green, blue, alpha, upperRadius, lowerRadius,
                            true, false, true, true);
                    quad(matrix, consumer, branchX, branchZ, segment, previousX, previousZ,
                            red, green, blue, alpha, upperRadius, lowerRadius,
                            true, true, false, true);
                    quad(matrix, consumer, branchX, branchZ, segment, previousX, previousZ,
                            red, green, blue, alpha, upperRadius, lowerRadius,
                            false, true, false, false);
                }
            }
        }
    }

    private static void quad(Matrix4f matrix, VertexConsumer consumer, float x, float z, int segment,
                             float previousX, float previousZ, float red, float green, float blue,
                             float alpha, float upperRadius, float lowerRadius,
                             boolean xLowerPositive, boolean zLowerPositive,
                             boolean xUpperPositive, boolean zUpperPositive) {
        consumer.addVertex(matrix, x + (xLowerPositive ? lowerRadius : -lowerRadius), segment * 16.0F,
                z + (zLowerPositive ? lowerRadius : -lowerRadius)).setColor(red, green, blue, alpha);
        consumer.addVertex(matrix, previousX + (xLowerPositive ? upperRadius : -upperRadius),
                (segment + 1) * 16.0F, previousZ + (zLowerPositive ? upperRadius : -upperRadius))
                .setColor(red, green, blue, alpha);
        consumer.addVertex(matrix, previousX + (xUpperPositive ? upperRadius : -upperRadius),
                (segment + 1) * 16.0F, previousZ + (zUpperPositive ? upperRadius : -upperRadius))
                .setColor(red, green, blue, alpha);
        consumer.addVertex(matrix, x + (xUpperPositive ? lowerRadius : -lowerRadius), segment * 16.0F,
                z + (zUpperPositive ? lowerRadius : -lowerRadius)).setColor(red, green, blue, alpha);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
