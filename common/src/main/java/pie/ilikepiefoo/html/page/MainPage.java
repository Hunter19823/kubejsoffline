package pie.ilikepiefoo.html.page;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.html.tag.CustomTag;
import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.html.tag.collection.DivTag;
import pie.ilikepiefoo.html.tag.formatting.StyleTag;
import pie.ilikepiefoo.html.tag.functional.ScriptTag;
import pie.ilikepiefoo.html.tag.text.HeaderTag;
import pie.ilikepiefoo.html.tag.text.SpanTag;
import pie.ilikepiefoo.util.ClassCluster;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class MainPage extends HTMLFile {
	private static final Logger LOG = LogManager.getLogger();

	public final Tag<?> SIDE_NAV;

	public MainPage() {
		modifyHTML();
		modifyHead();
		SIDE_NAV = new DivTag().setClass("sidenav");
		modifyBody();
	}

	private void modifyHTML() {
		HTML_TAG.setAttributeString("lang","en");
	}

	private void modifyHead() {
		HEADER_TAG.add(new CustomTag("meta").setAttributeString("charset","UTF-8"));
		HEADER_TAG.add(new StyleTag().setContent(TempResourceClasses.STYLE));
		HEADER_TAG.add(new ScriptTag().setContent(TempResourceClasses.SCRIPT));
	}

	private void modifyBody() {
		addTopBar();
		addTopNavigationBar();
		addProjectNavigationBar();
		addContentWindow();
	}

	private void addTopBar() {
		Tag<?> tag = new DivTag("top-bar");
		String LOGO = "https://kubejs.com/logo_title.png";
		tag.add(new CustomTag("img").setAttributeString("src", LOGO).id("logo-img"));
		tag.add(new HeaderTag(3).id("page-name").setContent("KubeJS Offline"));
		BODY_TAG.add(tag);
	}

	private void addTopNavigationBar() {
		Tag<?> div = BODY_TAG.add(
				new DivTag("top-navigation-bar")
						.setContent("Top Navigation Bar")
		);
		div.add(new CustomTag("label").setAttributeString("for", "searchbar").setContent("Search"));
		div.add(new CustomTag("input", false)
						.setAttributeString("type", "search")
						.setAttributeString("oninput", "liveSearch()")
						.id("searchbar")
				);
	}

	private void addProjectNavigationBar() {
		Tag<?> tag = new DivTag("project-navigation");
		BODY_TAG.add(tag);
		tag.add(SIDE_NAV);
	}

	private void addContentWindow() {
		Tag<?> tag = new DivTag("content-window");
		tag.add(new CustomTag("iframe").id("content-page"));
		BODY_TAG.add(tag);
	}

	public Tag<?> getName(ClassCluster cluster) {
		return new SpanTag().setContent(cluster.getName());
	}

	public Tag<?> getName(Class<?> target) {
		return new SpanTag().setContent(target.getName());
	}

	private Group createTree(String name) {
		Tag<?> tree = new CustomTag("ul");
		tree.setAttributeString("role", "tree");
		tree.setAttributeString("aria-labelledby", name);

		Tag<?> li = new CustomTag("li");
		markDirectory(li);
		li.add(new SpanTag().setContent(name));

		Tag<?> group = createGroup();

		li.add(group);
		tree.add(li);

		return new Group(tree, group);
	}

	private Tag<?> createGroup() {
		Tag<?> group = new CustomTag("ul");
		group.setAttributeString("role", "group");
		return group;
	}



	public Tag<?> addCluster(ClassCluster cluster, String name) {
		Group parent = createTree(name);

		Stack<Pair> stack = new Stack<>();

		stack.push(new Pair(parent.getGroup(), cluster));

		Pair temp = null;
		while(!stack.isEmpty()) {
			temp = stack.pop();
			//LOG.info("{}: {} classes {} clusters",temp.cluster.getName(), temp.cluster.getClasses().size(), temp.cluster.getClusters().size());

			stack.addAll(temp.getChildPairs());
		}

		return parent.getParent();
	}

	private Group createItem(ClassCluster cluster) {
		Tag<?> parent;
		Tag<?> group;
		Tag<?> innerGroup;

		// Create li tag
		parent = new CustomTag("li");
		markEmpty(parent);
		parent.setAttributeString("data-src", cluster.getFullName("/"));

		// <li>
		// </li>



		// Add Span / Name For Folder
		parent.add(getName(cluster));

		// <li>
		// <span></span>
		// </li>

		group = parent;


		// If there is anything inside line item, mark as directory
		if(cluster.shallowClusterCount() > 0 || cluster.shallowClassCount() > 0) {
			// Ensure tag is a directory
			markDirectory(parent);

			Tag<?> inGroup = createGroup();
			parent.add(inGroup);

			if(cluster.shallowClassCount()>0){
				// Create Directory for classes
				Tag<?> groupItem = new CustomTag("li");
				groupItem.add(new SpanTag().setContent("classes"));
				markDirectory(groupItem);

				// Create Group
				innerGroup = createGroup();

				// Add group to classes directory
				groupItem.add(innerGroup);

				// Add all classes to group
				for (Class<?> target : cluster.getClasses()){
					innerGroup.add(createItem(target));
				}


				// Add directory to cluster directory.
				inGroup.add(groupItem);
			}

			if(cluster.shallowClusterCount() > 0) {
				// Create Directory for Children Clusters
				Tag<?> groupItem = new CustomTag("li");
				groupItem.add(new SpanTag().setContent("children"));
				markDirectory(groupItem);

				// Create Group directory for children clusters
				innerGroup = createGroup();

				// Add Group to Children Directory
				groupItem.add(innerGroup);

				// Add directory to cluster directory.
				inGroup.add(groupItem);

				group = innerGroup;
			}
		}



		return new Group(parent, group);
	}


	private void markDirectory(Tag<?> tag) {
		tag.setAttributeString("role", "ti");
		tag.setAttributeString("aria-expanded", "false");
		tag.setAttributeString("aria-selected", "false");
	}

	private void markEmpty(Tag<?> tag) {
		tag.setAttributeString("role", "ti");
		tag.setAttributeString("aria-selected", "false");
		tag.setAttributeString("aria-expanded", "true");
	}

	private void markItem(Tag<?> tag) {
		tag.setAttributeString("role", "ti");
		tag.setAttributeString("aria-selected", "false");
		tag.setClass("doc");
	}




	private Tag<?> createItem(Class<?> target) {
		Tag<?> tag = new CustomTag("li");
		tag.setAttributeString("role", "ti");
		tag.setAttributeString("aria-selected", "false");
		tag.setAttributeString("data-target", target.getName());
		tag.setClass("doc");
		tag.add(getName(target));
		return tag;
	}


	private class Pair {
		private final Tag<?> group;
		private final ClassCluster cluster;

		public Pair(Tag<?> group, ClassCluster cluster) {
			this.group = group;
			this.cluster = cluster;
		}

		public Tag<?> getGroup() {
			return group;
		}

		public ClassCluster getCluster() {
			return cluster;
		}

		public List<Pair> getChildPairs() {
			Group itemGroup = createItem(cluster);

			this.group.add(itemGroup.getParent());
			return cluster.getClusters().stream().sorted().map((clust) -> new Pair(itemGroup.getGroup(), clust)).collect(Collectors.toList());
		}
	}

	private static class Group {
		private final Tag<?> group;
		private final Tag<?> parent;

		public Group(Tag<?> parent, Tag<?> group) {
			this.parent = parent;
			this.group = group;
		}

		public Tag<?> getGroup() {
			return group;
		}

		public Tag<?> getParent() {
			return parent;
		}
	}
}
