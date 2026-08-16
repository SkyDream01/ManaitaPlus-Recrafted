package net.minecraftforge.registries;

import net.minecraft.core.Registry;
import github.com.gengyoubo.util.MPResource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class DeferredRegister<T> {
    private final Registry<T> registry;
    private final String modId;
    private final List<Entry<T>> entries = new ArrayList<>();

    private DeferredRegister(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    public static <T> DeferredRegister<T> create(Registry<T> registry, String modId) {
        return new DeferredRegister<>(registry, modId);
    }

    public RegistryObject<T> register(String path, Supplier<T> supplier) {
        RegistryObject<T> object = new RegistryObject<>(modId + ":" + path, supplier);
        entries.add(new Entry<>(path, object));
        return object;
    }

    public void registerAll() {
        for (Entry<T> entry : entries) {
            entry.object.initialize();
            Registry.register(registry, MPResource.id(modId, entry.path), entry.object.get());
        }
    }

    private record Entry<T>(String path, RegistryObject<T> object) {
    }
}
