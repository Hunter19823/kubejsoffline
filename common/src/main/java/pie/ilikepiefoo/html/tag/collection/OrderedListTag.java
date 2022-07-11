package pie.ilikepiefoo.html.tag.collection;

import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.html.tag.base.BaseTag;

public class OrderedListTag extends BaseTag<OrderedListTag> {
	public OrderedListTag() {
		super("ol");
	}

	public ListItemTag addItem(Tag<?> tag) {
		var item = new ListItemTag();
		item.add(tag);
		return item;
	}
}
