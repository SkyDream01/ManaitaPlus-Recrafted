package github.com.gengyoubo.MPG.util;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.NamedPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.loading.ModDirTransformerDiscoverer;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.module.ResolvedModule;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Helper {
    private static final Logger LOGGER = LogManager.getLogger("ManaitaPlusBootstrap");
    public static final Unsafe UNSAFE;
    private static final MethodHandles.Lookup lookup;
    private static final Object internalUNSAFE;
    private static MethodHandle objectFieldOffsetInternal;

    static {
        UNSAFE = getUnsafe();
        lookup = getFieldValue(MethodHandles.Lookup.class, "IMPL_LOOKUP");
        internalUNSAFE = getInternalUNSAFE();
        try {
            Class<?> internalUNSAFEClass = null;
            if (lookup != null) {
                internalUNSAFEClass = lookup.findClass("jdk.internal.misc.Unsafe");
            }
            objectFieldOffsetInternal = Objects.requireNonNull(lookup).findVirtual(internalUNSAFEClass, "objectFieldOffset", MethodType.methodType(long.class, Field.class)).bindTo(internalUNSAFE);
        } catch (Exception e) {
            LOGGER.error(String.valueOf(e));
        }
    }

    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            LOGGER.error(String.valueOf(e));
        }
        return null;
    }

    private static Object getInternalUNSAFE() {
        try {
            Class<?> clazz = lookup.findClass("jdk.internal.misc.Unsafe");
            return lookup.findStatic(clazz, "getUnsafe", MethodType.methodType(clazz)).invoke();
        } catch (Throwable e) {
            LOGGER.error(String.valueOf(e));
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public static <T> T getFieldValue(Field f, Object target) {
        try {
            long offset;
            if (Modifier.isStatic(f.getModifiers())) {
                target = MethodHandles.lookup().unreflectVarHandle(f);
                offset = UNSAFE.staticFieldOffset(f);
            } else offset = objectFieldOffset(f);
            return (T) UNSAFE.getObject(target, offset);
        } catch (Throwable e) {
            LOGGER.error(String.valueOf(e));
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static long objectFieldOffset(Field f) {
        try {
            return UNSAFE.objectFieldOffset(f);
        } catch (Throwable e) {
            try {
                return (long) objectFieldOffsetInternal.invoke(f);
            } catch (Throwable t1) {
                LOGGER.error(String.valueOf(t1));
            }
        }
        return 0L;
    }

    public static <T> T getFieldValue(Object target, String fieldName) {
        try {
            return getFieldValue(target.getClass().getDeclaredField(fieldName), target);
        } catch (Throwable e) {
            LOGGER.error(String.valueOf(e));
        }
        return null;
    }

    public static <T> T getFieldValue(Class<?> target, String fieldName) {
        try {
            return getFieldValue(target.getDeclaredField(fieldName), (Object) null);
        } catch (Throwable e) {
            LOGGER.error(String.valueOf(e));
        }
        return null;
    }

    public static void setFieldValue(Object target, Class<?> value) {
        try {
            int aVolatile = UNSAFE.getIntVolatile(UNSAFE.allocateInstance(value), 8L);
            UNSAFE.putIntVolatile(target,8L,aVolatile);
        } catch (Throwable e) {
            LOGGER.error(String.valueOf(e));
        }
    }

    public static void setFieldValue(Object target, String fieldName, Object value) {
        try {
            setFieldValue(target.getClass().getDeclaredField(fieldName), target, value);
        } catch (Throwable e) {
            LOGGER.error(String.valueOf(e));
        }
    }


    @SuppressWarnings("deprecation")
    public static void setFieldValue(Field f, Object target, Object value) {
        try {
            long offset;
            if (Modifier.isStatic(f.getModifiers())) {
                target = MethodHandles.lookup().unreflectVarHandle(f);
                offset = UNSAFE.staticFieldOffset(f);
            } else offset = objectFieldOffset(f);
            UNSAFE.putObject(target, offset, value);
        } catch (Throwable e) {
            LOGGER.error(String.valueOf(e));
        }
    }

    public static String getJarPath(Class<?> clazz) {
        String file = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (!file.isEmpty()) {
            if (file.startsWith("union:"))
                file = file.substring(6);
            if (file.startsWith("/"))
                file = file.substring(1);
            file = file.substring(0, file.lastIndexOf(".jar") + 4);
            file = file.replaceAll("/", "\\\\");
        }
        return URLDecoder.decode(file, StandardCharsets.UTF_8);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked", "rawtypes"})
    public static void coexistenceCoreAndMod() {
        List<NamedPath> found = Helper.getFieldValue(ModDirTransformerDiscoverer.class, "found");
        if (found == null) {
            LOGGER.warn("Skipping coexistenceCoreAndMod cleanup because ModDirTransformerDiscoverer.found is not available yet");
            return;
        }
        found.removeIf(namedPath -> Helper.getJarPath(Helper.class).equals(namedPath.paths()[0].toString()));

        Object moduleLayerHandler = Helper.getFieldValue(Launcher.INSTANCE, "moduleLayerHandler");
        if (moduleLayerHandler == null) {
            LOGGER.warn("Skipping coexistenceCoreAndMod cleanup because moduleLayerHandler is not available yet");
            return;
        }
        Map<?, ?> completedLayers = Helper.getFieldValue(moduleLayerHandler, "completedLayers");
        if (completedLayers == null) {
            LOGGER.warn("Skipping coexistenceCoreAndMod cleanup because completedLayers is not available yet");
            return;
        }
        completedLayers.values().forEach(layerInfo -> {
            ModuleLayer layer = Helper.getFieldValue(layerInfo, "layer");
            if (layer == null) {
                return;
            }

            layer.modules().forEach(module -> {
                if (module.getName().equals(Helper.class.getModule().getName())) {
                    Set<ResolvedModule> modules = new HashSet<>(Helper.getFieldValue(layer.configuration(), "modules"));
                    Map<String, ResolvedModule> nameToModule = new HashMap(Helper.getFieldValue(layer.configuration(), "nameToModule"));

                    modules.remove(nameToModule.remove(Helper.class.getModule().getName()));

                    Helper.setFieldValue(layer.configuration(), "modules", modules);
                    Helper.setFieldValue(layer.configuration(), "nameToModule", nameToModule);
                }
            });
        });
    }

}

