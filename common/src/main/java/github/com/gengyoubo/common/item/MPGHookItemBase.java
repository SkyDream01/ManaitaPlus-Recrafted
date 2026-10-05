package github.com.gengyoubo.common.item;

import net.minecraft.world.item.Item;

public class MPGHookItemBase extends Item {
    public MPGHookItemBase(Item.Properties props) {
        super(props.fireResistant());
    }
}
