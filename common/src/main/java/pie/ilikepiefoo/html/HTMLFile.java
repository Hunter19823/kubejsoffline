package pie.ilikepiefoo.html;

import pie.ilikepiefoo.html.tag.base.BodyTag;
import pie.ilikepiefoo.html.tag.base.DoctypeTag;
import pie.ilikepiefoo.html.tag.base.HTMLTag;
import pie.ilikepiefoo.html.tag.base.HeadTag;

public class HTMLFile {
	public final DoctypeTag DOCTYPE_TAG = new DoctypeTag();
	public final HTMLTag HTML_TAG = new HTMLTag();
	public final HeadTag HEADER_TAG = new HeadTag();
	public final BodyTag BODY_TAG = new BodyTag();

	public HTMLFile() {
		DOCTYPE_TAG.add(HTML_TAG);
		HTML_TAG.add(HEADER_TAG);
		HTML_TAG.add(BODY_TAG);
	}

	public String toHTML() {
		return DOCTYPE_TAG.toHTML();
	}

	@Override
	public String toString() {
		return toHTML();
	}
}
