package pie.ilikepiefoo.kubejsoffline.html.page;

import pie.ilikepiefoo.kubejsoffline.html.tag.base.BodyTag;
import pie.ilikepiefoo.kubejsoffline.html.tag.base.DoctypeTag;
import pie.ilikepiefoo.kubejsoffline.html.tag.base.HTMLTag;
import pie.ilikepiefoo.kubejsoffline.html.tag.base.HeadTag;

public class HTMLFile extends DoctypeTag {
    public final DoctypeTag DOCTYPE_TAG = this;
    public final HTMLTag HTML_TAG = new HTMLTag();
    public final HeadTag HEADER_TAG = new HeadTag();
    public final BodyTag BODY_TAG = new BodyTag();

    public HTMLFile() {
        DOCTYPE_TAG.add(HTML_TAG);
        HTML_TAG.add(HEADER_TAG);
        HTML_TAG.add(BODY_TAG);
    }

    @Override
    public String toString() {
        return toHTML();
    }

    public String toHTML() {
        return DOCTYPE_TAG.toHTML();
    }
}
