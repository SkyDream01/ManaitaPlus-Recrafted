package github.com.gengyoubo.common;

import java.util.concurrent.atomic.AtomicBoolean;

/** Shared Architectury bootstrap used by supported platform modules. */
public final class MPGCommon {
    public static final String MOD_ID = "manaita_plus_general";
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean();

    private MPGCommon() {
    }

    public static boolean init() {
        return INITIALIZED.compareAndSet(false, true);
    }
}
