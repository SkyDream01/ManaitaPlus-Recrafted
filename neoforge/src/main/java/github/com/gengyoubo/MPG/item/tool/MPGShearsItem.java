package github.com.gengyoubo.MPG.item.tool;

import github.com.gengyoubo.common.item.tool.MPGShearsItemBase;

public class MPGShearsItem extends MPGShearsItemBase {
    public MPGShearsItem() {
        super(-1);
    }

    @Override
    protected boolean messageUsesOverlay() {
        return false;
    }
}
