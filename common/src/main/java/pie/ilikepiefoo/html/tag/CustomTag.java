package pie.ilikepiefoo.html.tag;

import org.jetbrains.annotations.NotNull;
import pie.ilikepiefoo.html.tag.base.BaseTag;

public class CustomTag extends BaseTag<CustomTag> {
	public CustomTag(@NotNull String name) {
		super(name);
	}

	public CustomTag(@NotNull String name, boolean closingTag) {
		super(name, closingTag);
	}
}
