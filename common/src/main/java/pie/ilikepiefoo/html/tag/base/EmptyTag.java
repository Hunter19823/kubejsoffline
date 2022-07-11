package pie.ilikepiefoo.html.tag.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pie.ilikepiefoo.html.tag.Tag;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmptyTag implements Tag<EmptyTag> {
	public static final EmptyTag INSTANCE = new EmptyTag();
	@Override
	public String toHTML() {
		return "";
	}

	@Override
	public String getFrontHTML() {
		return "";
	}

	@Override
	public String getAttributeHTML() {
		return "";
	}

	@Override
	public String getEndHTML() {
		return "";
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public String getContent() {
		return "";
	}

	@Override
	public boolean hasClosingTag() {
		return false;
	}

	@Nullable
	@Override
	public Tag<?> getParent() {
		return null;
	}

	@Override
	public void setParent(Tag<?> parent) {

	}

	@Override
	public List<Tag<?>> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public Map<String, String> getAttributes() {
		return Collections.emptyMap();
	}

	@Override
	public <T extends Tag<T>> Tag<T> add(Tag<T> tag) {
		return tag;
	}

	@Override
	public Tag<EmptyTag> setAttribute(String name, String value) {
		return this;
	}

	@Override
	public EmptyTag setAttributeString(@NotNull String name, @NotNull String value) {
		return this;
	}

	@Override
	public EmptyTag setAttributeNumber(@NotNull String name, @NotNull String value) {
		return this;
	}

	@Override
	public EmptyTag setAttributeNumber(@NotNull String name, @NotNull Number value) {
		return this;
	}

	@Override
	public EmptyTag setClass(@NotNull String name) {
		return this;
	}

	@Override
	public EmptyTag id(@NotNull String id) {
		return this;
	}

	@Override
	public EmptyTag href(@NotNull String href) {
		return this;
	}

	@Override
	public EmptyTag src(@NotNull String src) {
		return this;
	}

	@Override
	public EmptyTag title(@NotNull String title) {
		return this;
	}

	@Override
	public EmptyTag height(@NotNull String height) {
		return this;
	}

	@Override
	public EmptyTag width(@NotNull String width) {
		return this;
	}

	@Override
	public EmptyTag style(@NotNull String style) {
		return this;
	}
}
