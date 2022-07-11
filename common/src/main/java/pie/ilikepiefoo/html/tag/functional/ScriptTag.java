package pie.ilikepiefoo.html.tag.functional;

import pie.ilikepiefoo.html.tag.base.BaseTag;

public class ScriptTag extends BaseTag<ScriptTag> {

	public ScriptTag() {
		super("script", true);
	}

	public ScriptTag src(String src) {
		setAttribute("src", "\""+src+"\"");
		return this;
	}
}
