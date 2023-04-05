package net.gotzi.bungee.api.chat.hover.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.gotzi.bungee.api.chat.HoverEvent;
import net.gotzi.bungee.api.chat.ItemTag;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class Item extends Content
{

    /**
     * Namespaced item ID. Will use 'minecraft:air' if null.
     */
    private String id;
    /**
     * Optional. Size of the item stack.
     */
    private int count;
    /**
     * Optional. Item tag.
     */
    private ItemTag tag;

    @Override
    public HoverEvent.Action requiredAction()
    {
        return HoverEvent.Action.SHOW_ITEM;
    }
}
