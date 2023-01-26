package pie.ilikepiefoo.kubejsoffline.html.tag;

import org.jetbrains.annotations.NotNull;
import pie.ilikepiefoo.kubejsoffline.html.tag.base.BaseTag;

public class CustomTag extends BaseTag<CustomTag> {
	private final StringBuilder builder = new StringBuilder();
	public CustomTag(@NotNull String name) {
		super(name);
	}

	public CustomTag(@NotNull String name, boolean closingTag) {
		super(name, closingTag);
	}

	@Override
	public CustomTag setClass(@NotNull String name) {
		if(!builder.isEmpty())
			builder.append(" ");
		builder.append(name);
		setAttributeString("class", builder.toString());
		return this;
	}
}
