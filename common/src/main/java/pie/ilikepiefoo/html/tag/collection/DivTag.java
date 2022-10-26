package pie.ilikepiefoo.html.tag.collection;


import pie.ilikepiefoo.html.tag.base.BaseTag;

public class DivTag extends BaseTag<DivTag> {
	public DivTag() {
		super("div", true);
	}

	public DivTag(String id) {
		super("div", true);
		this.setAttributeString("id", id);
	}
}
