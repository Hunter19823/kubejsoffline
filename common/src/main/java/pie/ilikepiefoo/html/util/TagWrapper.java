package pie.ilikepiefoo.html.util;

import pie.ilikepiefoo.html.tag.CustomTag;
import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.html.tag.base.EmptyTag;
import pie.ilikepiefoo.html.tag.base.StringTag;
import pie.ilikepiefoo.html.tag.text.ParagraphTag;

import java.util.List;
import java.util.stream.Collectors;

public class TagWrapper {
	public static Tag<?> ofList(Object obj) {
		if (obj instanceof Tag<?> tag) {
			return tag;
		} else if (obj instanceof CharSequence string) {
			return switch (string.toString()) {
				case "p" -> new ParagraphTag();
				case "a" -> new CustomTag("a", true);
				default -> new StringTag(string.toString());
			};
		}

		return EmptyTag.INSTANCE;
	}

	public static List<Tag<?>> ofList(Object... objs) {
		return List.of(objs).parallelStream().map(TagWrapper::ofList).collect(Collectors.toList());
	}
}
