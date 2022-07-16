package pie.ilikepiefoo.html;

import pie.ilikepiefoo.html.tag.CustomTag;
import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.html.tag.base.StringTag;
import pie.ilikepiefoo.html.tag.collection.DivTag;
import pie.ilikepiefoo.html.tag.collection.UnorderedListTag;
import pie.ilikepiefoo.html.tag.text.HeaderTag;
import pie.ilikepiefoo.util.ClassCluster;
import pie.ilikepiefoo.util.ClassTree;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ClassTreePage extends HTMLFile {
	public ClassTreePage(ClassTree tree) {
		addCSS();
		ClassCluster cluster = tree.getExtensionRoot();
		Tag<?> tag = new CustomTag("ul").id("tree");

		Stack<ClassCluster> stack = new Stack<>();

		Set<ClassCluster> seen = new HashSet<>();

		stack.push(cluster);

		Tag<?> inside = tag;
		Tag<?> outside = tag;
		ClassCluster temp = null;
		while(!stack.isEmpty()) {
			temp = stack.pop();
			if(!seen.contains(temp)){
				inside = addNest(temp);
				outside.add(inside.getParent());

				// Add to the seen stack.
				// If we come across again, we know
				// To go to parent.
				seen.add(temp);

				for (int i = temp.getClusters().size()-1; i >= 0; i--) {
					stack.add(temp.getClusters().get(i));
				}
			} else {
				outside = inside.getParent();
			}
		}

//		Tag<?> inside = addNest(cluster);
//		tag.add(inside.getParent());
//
//		for(var clusters : cluster.getClusters()) {
//			inside.add(addNest(clusters).getParent());
//		}
		this.BODY_TAG.add(tag);
		addScript();
	}

	public Tag<?> addNest(ClassCluster cluster) {
		Tag<?> tag = new CustomTag("li");
		tag.add(new CustomTag("span").setContent(cluster.getFullName("/")).setClass("expandable"));
		tag = tag.add(new CustomTag("ul").setClass("nested"));
		tag.add(new CustomTag("span").setContent("classes").setClass("expandable"));
		tag = tag.add(new CustomTag("ul").setClass("nested"));

		for(var target : cluster.getClasses()) {
			tag.add(new CustomTag("li").setContent(target.getName()));
		}

		return tag.getParent();
	}

	public void addCluster(ClassCluster cluster) {
		Tag<?> tag = this.BODY_TAG.add(new DivTag());
		tag.id(cluster.getFullName("/"));
		tag.add(new HeaderTag(3).add(new StringTag(cluster.getFullName("/"))));
		var list = new UnorderedListTag();
		tag.add(list);

		for(var target : cluster) {
			list.addItem(createBasicLinkTag(target));
		}
	}

	public static Tag<?> createBasicLinkTag(Class<?> target) {
		Tag<?> tag = new StringTag(target.getName());
		tag.setAttributeString(target.getName(), target.getName());
		return tag;
	}

	public void addCSS() {
		CustomTag tag = new CustomTag("style");
		tag.setContent("/* Remove default bullets */\n" +
				"ul, #tree {\n" +
				"  list-style-type: none;\n" +
				"}\n" +
				"\n" +
				"/* Remove margins and padding from the parent ul */\n" +
				"#tree {\n" +
				"  margin: 0;\n" +
				"  padding: 0;\n" +
				"}\n" +
				"/* Style the caret/arrow */\n" +
				".expandable {\n" +
				"  cursor: pointer;\n" +
				"  user-select: none; /* Prevent text selection */\n" +
				"}\n" +
				"\n" +
				"/* Create the caret/arrow with a unicode, and style it */\n" +
				".expandable::before {\n" +
				"  content: \"\\25B6\";\n" +
				"  color: black;\n" +
				"  display: inline-block;\n" +
				"  margin-right: 6px;\n" +
				"}\n" +
				"\n" +
				"/* Rotate the caret/arrow icon when clicked on (using JavaScript) */\n" +
				".expandable-down::before {\n" +
				"  transform: rotate(90deg);\n" +
				"}\n" +
				"\n" +
				"/* Hide the nested list */\n" +
				".nested {\n" +
				"  display: none;\n" +
				"}\n" +
				"\n" +
				"/* Show the nested list when the user clicks on the caret/arrow (with JavaScript) */\n" +
				".active {\n" +
				"  display: block;\n" +
				"}");
		this.HEADER_TAG.add(tag);
	}

	public void addScript() {
		CustomTag tag = new CustomTag("script");
		tag.setContent("var toggler = document.getElementsByClassName(\"expandable\");\n" +
				"var i;\n" +
				"\n" +
				"for (i = 0; i < toggler.length; i++) {\n" +
				"  toggler[i].addEventListener(\"click\", function() {\n" +
				"    this.parentElement.querySelector(\".nested\").classList.toggle(\"active\");\n" +
				"    this.classList.toggle(\"expandable-down\");\n" +
				"  });\n" +
				"}");
		this.BODY_TAG.add(tag);
	}
}
