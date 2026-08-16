package github.com.gengyoubo.MPG.baubles.common;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Baubles {
    public static final String MODID = MPG.MODID + "_baubles";
    public static final Logger LOGGER = LoggerFactory.getLogger("ManaitaPlusBaubles");

    private Baubles() {
    }

    public static boolean isEnabled() {
        return BaublesCapability.isEnabled();
    }
}
