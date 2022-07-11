package pie.ilikepiefoo.html.tag.collection;

import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.html.tag.base.BaseTag;

public class UnorderedListTag extends BaseTag<UnorderedListTag> {
	public UnorderedListTag() {
		super("ul");
	}

	public ListItemTag addItem(Tag<?> tag) {
		var item = new ListItemTag();
		item.add(tag);
		return item;
	}

}
