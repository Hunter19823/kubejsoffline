package pie.ilikepiefoo.html.tag.base;

import javax.annotation.Nonnull;

public class StringTag extends BaseTag<StringTag> {

	public StringTag(@Nonnull String content) {
		super("basicstringtag");
		setContent(content);
	}
}
