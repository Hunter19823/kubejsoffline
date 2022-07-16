package pie.ilikepiefoo.util;

import java.util.LinkedList;
import java.util.Stack;

public class ClassTree {
	private final ClassCluster root;
	private final ClassCluster extension;

	public ClassTree() {
		this.root = new ClassCluster("root");
		this.extension = new ClassCluster("root");
	}

	public void addClass(Class<?> target) {
		if(target == null)
			return;
		while(target.isArray())
			target = target.getComponentType();
		addToDirectoryTree(target);
		addToExtensionTree(target);
		addToImplementationTree(target);
	}

	public ClassCluster getFileRoot() {
		return root;
	}

	public ClassCluster getExtensionRoot() {
		return extension;
	}

	private void addToImplementationTree(Class<?> target) {
		LinkedList<ClassCluster> clusters;
		for(Class<?> inter : target.getInterfaces()) {
			clusters = getExtensionLineage(inter);
			clusters.removeFirst();
			clusters.removeFirst();
			for(ClassCluster cluster : clusters) {
				cluster.addClass(target);
			}
		}
	}

	public ClassCluster findDirectoryCluster(Class<?> target) {
		return getDirectoryLineage(target).getLast();
	}

	public ClassCluster findExtensionCluster(Class<?> target) {
		return getExtensionLineage(target).getLast();
	}

	private ClassCluster addToExtensionTree(Class<?> target) {
		Stack<Class<?>> lineage = new Stack<>();
		lineage.add(target);
		while(lineage.peek().getSuperclass() != null){
			lineage.add(lineage.peek().getSuperclass());
		}
		ClassCluster cluster;
		if(target.isInterface()){
			cluster = extension.getCluster("interface");
		}else if(target.isPrimitive()){
			cluster = extension.getCluster("primitive");
		}else {
			cluster = extension;
		}
		Class<?> subject;
		do {
			subject = lineage.pop();
			cluster = cluster.getCluster(subject.getSimpleName());
			cluster.addClass(subject);
			cluster.addClass(target);
		} while(!lineage.empty());
		return cluster;
	}
	private LinkedList<ClassCluster> getExtensionLineage(Class<?> target) {
		LinkedList<Class<?>> lineage = new LinkedList<>();
		LinkedList<ClassCluster> clusterLineage = new LinkedList<>();
		lineage.add(target);
		while(lineage.getLast().getSuperclass() != null){
			lineage.add(lineage.getLast().getSuperclass());
		}
		ClassCluster cluster;
		if(target.isInterface()){
			cluster = extension.getCluster("interface");
		}else if(target.isPrimitive()){
			cluster = extension.getCluster("primitive");
		}else {
			cluster = extension;
		}
		clusterLineage.add(cluster);
		Class<?> subject;
		do {
			subject = lineage.removeLast();
			cluster = cluster.getCluster(subject.getName());
			clusterLineage.add(cluster);
		} while(!lineage.isEmpty());

		return clusterLineage;
	}

	private LinkedList<ClassCluster> getDirectoryLineage(Class<?> target) {
		LinkedList<ClassCluster> clusters = new LinkedList<>();
		ClassCluster current = root;

		if(target.isPrimitive())
			current = current.getCluster("primitive");

		String fullName = target.getName();
		clusters.add(current);
		if(fullName.indexOf('.') != -1) {
			int start = fullName.indexOf('.');
			int end = fullName.lastIndexOf('.');
			while(start != end) {
				String clusterName = fullName.substring(0,start);
				fullName = fullName.substring(start+1);
				current = current.getCluster(clusterName);
				clusters.add(current);
				start = fullName.indexOf('.');
				end = fullName.lastIndexOf('.');
			}
		}
		return clusters;
	}

	private void addToDirectoryTree(Class<?> target) {
		getDirectoryLineage(target).getLast().addClass(target);
	}


	public void compress() {
		root.stream().forEach((cluster) -> cluster.compress());
		extension.stream().forEach((cluster) -> cluster.compress());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		extension.stream().forEachOrdered((cluster)->builder.append(cluster.toTree()));
		return builder.toString();
	}
}
