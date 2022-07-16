package pie.ilikepiefoo.html;

import pie.ilikepiefoo.html.tag.CustomTag;
import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.html.tag.base.StringTag;
import pie.ilikepiefoo.html.tag.collection.DivTag;
import pie.ilikepiefoo.html.tag.collection.UnorderedListTag;
import pie.ilikepiefoo.html.tag.text.HeaderTag;
import pie.ilikepiefoo.util.ClassCluster;
import pie.ilikepiefoo.util.ClassTree;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class ClassTreePage extends HTMLFile {
	public ClassTreePage(ClassTree tree) {
		addCSS();
		Tag<?> tag = new CustomTag("ul").id("tree");
		this.BODY_TAG.add(tag);
		addCluster(tree.getExtensionRoot(), tag);
		addCluster(tree.getFileRoot(), tag);
		addScript();
	}

	private class Pair {
		private Tag<?> tag;
		private ClassCluster cluster;
		public Pair(Tag<?> tag, ClassCluster cluster) {
			this.tag = tag;
			this.cluster = cluster;
		}

		public Tag<?> getTag() {
			return tag;
		}

		public ClassCluster getCluster() {
			return cluster;
		}

		public List<Pair> getChildPairs() {
			Tag<?> inside = addNest(cluster);
			tag.add(inside.getParent());
			return cluster.getClusters().stream().sorted().map((cluster) -> new Pair(inside, cluster)).collect(Collectors.toList());
		}
	}

	public Tag<?> addNest(ClassCluster cluster) {
		Tag<?> tag = new CustomTag("li");
		tag.add(new CustomTag("span").setContent(cluster.getFullName("/")).setClass("expandable"));
		tag = tag.add(new CustomTag("ul").setClass("nested"));
		if(cluster.getClasses().size() > 0) {
			tag.add(new CustomTag("span").setContent("classes").setClass("expandable"));
			tag = tag.add(new CustomTag("ul").setClass("nested"));

			Tag<?> finalTag = tag;
			cluster.getClasses().stream().sorted(Comparator.comparing(Class::getName)).forEachOrdered((target) -> finalTag.add(getClassTag(target)));
			return tag.getParent();
		}

		return tag;
	}

	public Tag<?> getClassTag(Class<?> target) {
		return new CustomTag("li").setContent(target.getName());
	}

	public void addCluster(ClassCluster cluster, Tag<?> tag) {
		Stack<Pair> stack = new Stack<>();

		Set<ClassCluster> seen = new HashSet<>();

		stack.push(new Pair(tag, cluster));

		Pair temp = null;
		while(!stack.isEmpty()) {
			temp = stack.pop();
			stack.addAll(temp.getChildPairs());
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
