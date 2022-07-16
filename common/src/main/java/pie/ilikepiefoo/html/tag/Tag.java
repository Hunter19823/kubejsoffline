package pie.ilikepiefoo.html.tag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public interface Tag<TYPE extends Tag<TYPE>> {
	public static final Logger LOG = LogManager.getLogger();
	public default String toHTML() {
		StringBuilder sb = new StringBuilder();
		// Add the Head tag.
		sb.append(getFrontHTML());
		// Add Content to the front of the children.
		sb.append(getContent());

		// Iteratively add all child tags to the HTML document
		Stack<Tag<?>> hierarchy = new Stack<Tag<?>>();
		Set<Tag<?>> tagSet = new HashSet<Tag<?>>();
		hierarchy.addAll(getChildren());
		int counter = 0;
		while(hierarchy.size() > 0) {
			counter++;
//			if(hierarchy.size() < 10 && counter < 50) {
//				LOG.info(sb.toString());
//				LOG.info(hierarchy);
//			}
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

	public default String getFrontHTML() {
		return "\n<"+getName()+" "+getAttributeHTML()+">";
	}

	public default String getAttributeHTML() {
		StringBuilder sb = new StringBuilder();
		if(getAttributes() != null) {
			getAttributes().forEach(
					(key, value) -> sb.append(key)
							.append("=")
							.append(value)
							.append(" ")
			);
		}
		return sb.toString();
	}

	public default String getEndHTML() {
		if(hasClosingTag()) {
			return "\n</"+getName()+">";
		}
		return "";
	}


	public String getName();

	public String getContent();

	public boolean hasClosingTag();

	public @Nullable Tag<?> getParent();
	public void setParent(Tag<?> parent);

	public List<Tag<?>> getChildren();

	public Map<String, String> getAttributes();

	public <T extends Tag<T>> Tag<T> add(Tag<T> tag);

	public default Tag<TYPE> setAttribute(String name, String value) {
		getAttributes().put(name, value);
		return this;
	}

	public default TYPE setAttributeString(@Nonnull String name, @Nonnull String value) {
		setAttribute(name, "\""+value+"\"");
		return (TYPE) this;
	}

	public default TYPE setAttributeNumber(@Nonnull String name, @Nonnull String value) {
		setAttribute(name, value);
		return (TYPE) this;
	}

	public default TYPE setAttributeNumber(@Nonnull String name, @Nonnull Number value) {
		setAttribute(name, value.toString());
		return (TYPE) this;
	}

	public default TYPE setClass(@Nonnull String name) {
		setAttributeString("class", name);
		return (TYPE) this;
	}

	public default TYPE id(@Nonnull String id) {
		setAttributeString("id", id);
		return (TYPE) this;
	}

	public default TYPE href(@Nonnull String href) {
		setAttributeString("href", href);
		return (TYPE) this;
	}

	public default TYPE src(@Nonnull String src) {
		setAttributeString("src", src);
		return (TYPE) this;
	}

	public default TYPE title(@Nonnull String title) {
		setAttributeString("title", title);
		return (TYPE) this;
	}

	public default TYPE height(@Nonnull String height) {
		setAttributeString("height", height);
		return (TYPE) this;
	}

	public default TYPE width(@Nonnull String width) {
		setAttributeString("width", width);
		return (TYPE) this;
	}

	public default TYPE style(@Nonnull String style) {
		setAttributeString("style", style);
		return (TYPE) this;
	}
}
