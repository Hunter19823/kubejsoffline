package pie.ilikepiefoo.kubejsoffline.html.tag.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pie.ilikepiefoo.kubejsoffline.html.tag.Tag;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class BaseTag<TYPE extends Tag<TYPE>> implements Tag<TYPE> {
	protected final String name;
	protected String content = "";
	protected boolean closingTag;
	protected List<Tag<?>> children = new ArrayList<>();
	protected Tag<?> parent = null;
	protected Map<String, String> attributes = new HashMap<>();

	public BaseTag(@Nonnull String name) {
		this.name = name;
		this.closingTag = true;
	}

	public BaseTag(@Nonnull String name, boolean closingTag) {
		this.name = name;
		this.closingTag = closingTag;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getContent() {
		return this.content;
	}

	public TYPE setContent(@NotNull String content) {
		this.content = content;
		return (TYPE) this;
	}

	@Override
	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	@Override
	public <T extends Tag<T>> Tag<T> add(@Nonnull Tag<T> tag) {
		tag.setParent(this);

		this.children.add(tag);
		return tag;
	}

	@Nullable
	@Override
	public Tag<?> getParent() {
		return this.parent;
	}

	@Override
	public void setParent(@Nonnull Tag<?> parent) {
		this.parent = parent;
	}

	@Override
	public boolean hasClosingTag() {
		return this.closingTag;
	}

	@Override
	public List<Tag<?>> getChildren() {
		return this.children;
	}
}
