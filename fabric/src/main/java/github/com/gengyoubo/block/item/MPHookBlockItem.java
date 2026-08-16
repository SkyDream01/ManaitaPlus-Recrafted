package github.com.gengyoubo.block.item;

import github.com.gengyoubo.block.MPHookBlock;
import github.com.gengyoubo.common.block.item.MPGHookBlockItemBase;

import static github.com.gengyoubo.core.MPBlockCore.HookBlock;

public class MPHookBlockItem extends MPGHookBlockItemBase {
    public MPHookBlockItem() {
        super(HookBlock.get(), MPHookBlock.class);
    }
}
