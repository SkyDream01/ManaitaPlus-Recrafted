package net.minecraftforge.registries;

import java.util.Objects;
import java.util.function.Supplier;

public final class RegistryObject<T> implements Supplier<T> {
    private final String id;
    private final Supplier<T> supplier;
    private T value;

    RegistryObject(String id, Supplier<T> supplier) {
        this.id = id;
        this.supplier = supplier;
    }

    void initialize() {
        if (value == null) {
            value = Objects.requireNonNull(supplier.get(), "Registry supplier returned null for " + id);
        }
    }

    public String getId() {
        return id;
    }

    @Override
    public T get() {
        if (value == null) {
            throw new IllegalStateException("Registry object not initialized: " + id);
        }
        return value;
    }
}
