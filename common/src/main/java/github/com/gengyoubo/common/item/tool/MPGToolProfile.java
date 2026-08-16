package github.com.gengyoubo.common.item.tool;

/** Per-tool feature set matching the original Manaita tool configuration. */
public enum MPGToolProfile {
    GENERIC(true, false, false, false, 27),
    PAXEL(true, false, false, false, 19),
    HOE(false, false, false, false, 27),
    AXE(true, true, true, false, 27),
    PICKAXE(true, true, true, true, 27),
    SHOVEL(true, true, true, true, 27);

    private final boolean silkTouch;
    private final boolean canHarvest;
    private final boolean depth;
    private final boolean digUnderPlayer;
    private final int maxRange;

    MPGToolProfile(boolean silkTouch, boolean canHarvest, boolean depth, boolean digUnderPlayer, int maxRange) {
        this.silkTouch = silkTouch;
        this.canHarvest = canHarvest;
        this.depth = depth;
        this.digUnderPlayer = digUnderPlayer;
        this.maxRange = maxRange;
    }

    public boolean supportsSilkTouch() {
        return silkTouch;
    }

    public boolean supportsCanHarvest() {
        return canHarvest;
    }

    public boolean supportsDepth() {
        return depth;
    }

    public boolean supportsDigUnderPlayer() {
        return digUnderPlayer;
    }

    public int maxRange() {
        return maxRange;
    }

    public boolean usesDedicatedDoublingKey() {
        return this == PAXEL;
    }
}
