package pie.ilikepiefoo.html.tag.text;

import org.jetbrains.annotations.NotNull;
import pie.ilikepiefoo.html.tag.base.BaseTag;

public class HeaderTag extends BaseTag<HeaderTag> {
	public HeaderTag(@NotNull int importance) {
		super("h"+importance);
	}
}
