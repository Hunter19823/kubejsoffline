package pie.ilikepiefoo.html.tag.functional;

import org.jetbrains.annotations.NotNull;
import pie.ilikepiefoo.html.tag.base.BaseTag;

public class IFrameTag extends BaseTag<IFrameTag> {
	public IFrameTag(@NotNull String url) {
		super("iframe");
		src(url);
	}
}
