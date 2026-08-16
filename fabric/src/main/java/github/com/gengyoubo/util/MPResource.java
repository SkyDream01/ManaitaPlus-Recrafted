package github.com.gengyoubo.util;

import github.com.gengyoubo.MPG;
import net.minecraft.resources.ResourceLocation;

public final class MPResource {
    private MPResource() {
    }

    public static ResourceLocation id(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation id(String path) {
        return id(MPG.MODID, path);
    }

    public static ResourceLocation parse(String id) {
        return ResourceLocation.parse(id);
    }
}

