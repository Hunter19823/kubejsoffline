package pie.ilikepiefoo.html.tag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@SuppressWarnings("unchecked")
public interface Tag<TYPE extends Tag<TYPE>> {
	Logger LOG = LogManager.getLogger();
	default String toHTML() {
		StringBuilder sb = new StringBuilder();
		// Add the Head tag.
		sb.append(getFrontHTML());
		// Add Content to the front of the children.
		sb.append(getContent());

		// Iteratively add all child tags to the HTML document
		Stack<Tag<?>> hierarchy = new Stack<>();
		Set<Tag<?>> tagSet = new HashSet<>();
		hierarchy.addAll(getChildren());
		int counter = 0;
		while(hierarchy.size() > 0) {
			counter++;
			Tag<?> tag = hierarchy.pop();
			if(!tagSet.contains(tag)) {
				sb.append(tag.getFrontHTML());
				hierarchy.push(tag);
				tagSet.add(tag);
				sb.append(tag.getContent());
			}else {
				sb.append(tag.getEndHTML());
				continue;
			}

			if(tag.getChildren().size() > 0) {
				for (int i = tag.getChildren().size() - 1; i >= 0; i--) {
					hierarchy.push(tag.getChildren().get(i));
				}
			}
		}
		if(hasClosingTag()){
			sb.append("</");
			sb.append(getName());
			sb.append(">");
		}
		return sb.toString();
	}

	default void writeHTML(Writer writer) throws IOException {
		// Add the Head tag.
		writer.write(getFrontHTML());
		// Add Content to the front of the children.
		writeContent(writer);

		// Iteratively add all child tags to the HTML document
		Stack<Tag<?>> hierarchy = new Stack<>();
		Set<Tag<?>> tagSet = new HashSet<>();
		hierarchy.addAll(getChildren());
		int counter = 0;
		while(hierarchy.size() > 0) {
			counter++;
			Tag<?> tag = hierarchy.pop();
			if(!tagSet.contains(tag)) {
				writer.write(tag.getFrontHTML());
				hierarchy.push(tag);
				tagSet.add(tag);
				tag.writeContent(writer);
			}else {
				writer.write(tag.getEndHTML());
				continue;
			}

			if(tag.getChildren().size() > 0) {
				for (int i = tag.getChildren().size() - 1; i >= 0; i--) {
					hierarchy.push(tag.getChildren().get(i));
				}
			}
		}
		if(hasClosingTag()){
			writer.append("</");
			writer.append(getName());
			writer.append(">");
		}
		writer.flush();
	}

	default String getFrontHTML() {
		return "<"+getName()+getAttributeHTML()+">";
	}

	default String getAttributeHTML() {
		StringBuilder sb = new StringBuilder();
		if(getAttributes() != null) {
			getAttributes().forEach(
					(key, value) -> sb.append(" ").append(key)
							.append("=")
							.append(value)
			);
		}
		return sb.toString();
	}

	default String getEndHTML() {
		if(hasClosingTag()) {
			return "</"+getName()+">";
		}
		return "";
	}


	String getName();

	String getContent();

	default void writeContent(Writer writer) throws IOException {
		writer.write(getContent());
	}

	boolean hasClosingTag();

	@Nullable Tag<?> getParent();
	void setParent(Tag<?> parent);

	List<Tag<?>> getChildren();

	Map<String, String> getAttributes();

	<T extends Tag<T>> Tag<T> add(Tag<T> tag);

	default Tag<TYPE> setAttribute(String name, String value) {
		getAttributes().put(name, value);
		return this;
	}

	default TYPE setAttributeString(@Nonnull String name, @Nonnull String value) {
		setAttribute(name, "\""+value+"\"");
		return (TYPE) this;
	}

	default TYPE setAttributeNumber(@Nonnull String name, @Nonnull String value) {
		setAttribute(name, value);
		return (TYPE) this;
	}

	default TYPE setAttributeNumber(@Nonnull String name, @Nonnull Number value) {
		setAttribute(name, value.toString());
		return (TYPE) this;
	}

	default TYPE setClass(@Nonnull String name) {
		setAttributeString("class", name);
		return (TYPE) this;
	}

	default TYPE id(@Nonnull String id) {
		setAttributeString("id", id);
		return (TYPE) this;
	}

	default TYPE href(@Nonnull String href) {
		setAttributeString("href", href);
		return (TYPE) this;
	}

	default TYPE src(@Nonnull String src) {
		setAttributeString("src", src);
		return (TYPE) this;
	}

	default TYPE title(@Nonnull String title) {
		setAttributeString("title", title);
		return (TYPE) this;
	}

	default TYPE height(@Nonnull String height) {
		setAttributeString("height", height);
		return (TYPE) this;
	}

	default TYPE width(@Nonnull String width) {
		setAttributeString("width", width);
		return (TYPE) this;
	}

	default TYPE style(@Nonnull String style) {
		setAttributeString("style", style);
		return (TYPE) this;
	}

	default TYPE br() {
		add(new CustomTag("br", false));
		return (TYPE) this;
	}

	default TYPE add(String content) {
		add(new CustomTag("span", true).setContent(content));
		return (TYPE) this;
	}
}
