package pie.ilikepiefoo.html.tag.functional;

import pie.ilikepiefoo.html.tag.base.BaseTag;

import javax.annotation.Nonnull;

public class LinkTag extends BaseTag<LinkTag> {
	public LinkTag(@Nonnull String href) {
		super("link");

		setAttribute("href", "\""+href+"\"");
	}
}
