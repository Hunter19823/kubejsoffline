package pie.ilikepiefoo.html.tag.collection;

import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.html.tag.base.BaseTag;

import javax.annotation.Nonnull;

public class DescribableTags extends BaseTag<DescribableTags> {

	public DescribableTags() {
		super("dl");
	}

	public DescriptedTag addItem(@Nonnull Tag<?> item) {
		DescriptedTag itemTag = new DescriptedTag();
		itemTag.add(item);
		add(itemTag);
		return itemTag;
	}

	public DescriptionTag addDescription(@Nonnull Tag<?> tag) {
		DescriptionTag descriptionTag = new DescriptionTag();
		descriptionTag.add(tag);
		add(descriptionTag);
		return descriptionTag;
	}

	public DescribableTags addPair(@Nonnull Tag<?> item, @Nonnull Tag<?> description) {
		DescriptionTag descriptionTag = new DescriptionTag();
		DescriptedTag itemTag = new DescriptedTag();
		itemTag.add(item);
		descriptionTag.add(description);
		add(itemTag);
		add(descriptionTag);
		return this;
	}
}
