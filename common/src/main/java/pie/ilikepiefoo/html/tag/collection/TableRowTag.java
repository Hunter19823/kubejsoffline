package pie.ilikepiefoo.html.tag.collection;

import org.jetbrains.annotations.NotNull;
import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.html.tag.base.BaseTag;

import java.util.List;

public class TableRowTag extends BaseTag<TableRowTag> {

	protected boolean HEADER_ROW;

	public TableRowTag(boolean HEADER_ROW) {
		super("tr", true);
		this.HEADER_ROW = HEADER_ROW;
	}

	@Override
	public <T extends Tag<T>> Tag<T> add(@NotNull Tag<T> tag) {
		if(HEADER_ROW) {
			return super.add(new TableHeaderTag()).add(tag);
		}else {
			return super.add(new TableDataTag()).add(tag);
		}
	}

	public TableRowTag addAll(@NotNull List<Tag<?>> rows) {
		rows.forEach(this::add);
		return this;
	}
}
