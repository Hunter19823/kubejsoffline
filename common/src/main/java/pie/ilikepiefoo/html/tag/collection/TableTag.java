package pie.ilikepiefoo.html.tag.collection;

import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.html.tag.base.BaseTag;

import java.util.List;

public class TableTag extends BaseTag<TableTag> {

	public TableTag() {
		super("table");
	}

	public TableRowTag addHeader(List<Tag<?>> headers) {
		var head = new TableRowTag(true);
		head.addAll(headers);
		add(head);
		return head;
	}

	public TableRowTag addRow(List<Tag<?>> data) {
		var row = new TableRowTag(false);
		row.addAll(data);
		add(row);
		return row;
	}

}
