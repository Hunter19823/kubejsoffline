package pie.ilikepiefoo.util;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class ClassCluster implements Iterable<Class<?>> {
	private final String name;
	private ClassCluster parent;
	private final Map<String, Class<?>> classMap;
	private final Map<String, ClassCluster> clusterMap;

	public ClassCluster(String name) {
		this.name = name;
		this.classMap = new ConcurrentHashMap<>();
		this.clusterMap = new ConcurrentHashMap<>();
		this.parent = null;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public int getShallowCount() {
		return this.classMap.size();
	}

	@Nullable
	public ClassCluster getParent() {
		return parent;
	}

	public String getFullName(String separator) {
		StringBuilder builder = new StringBuilder();
		for(ClassCluster cluster : getLineage()) {
			builder.append(cluster.getName());
			builder.append(separator);
		}

		return builder.toString();
	}

	public Stack<ClassCluster> getLineage() {
		Stack<ClassCluster> lineage = new Stack<>();
		ClassCluster current = this;
		do{
			lineage.add(current);
		}while((current = current.getParent()) != null);

		return lineage;
	}

	public boolean containsClass(String target) {
		return this.classMap.containsKey(target);
	}

	public boolean containsCluster(String target) {
		return this.clusterMap.containsKey(target);
	}

	@Nonnull
	public ClassCluster getCluster(String target) {
		ClassCluster cluster;
		if(containsCluster(target)){
			cluster = this.clusterMap.get(target);
		}else{
			cluster = new ClassCluster(target);
			cluster.parent = this;
			this.clusterMap.put(target,cluster);
		}

		return cluster;
	}

	@Nullable
	public Class<?> getClass(String target) {
		Class<?> subject = null;
		if(containsClass(target)){
			subject = this.classMap.get(target);
		}
		return subject;
	}

	public Collection<Class<?>> getClasses() {
		return this.classMap.values();
	}

	public Collection<ClassCluster> getClusters() {
		return this.clusterMap.values();
	}

	public void addClass(String target, Class<?> subject) {
		this.classMap.put(target, subject);
	}

	@NotNull
	@Override
	public Iterator<Class<?>> iterator() {
		return new ClassIterator();
	}

	private class ClassIterator implements Iterator<Class<?>> {
		private Iterator<Class<?>> classMapIterator;
		private final Iterator<ClassCluster> clusterIterator;

		public ClassIterator(){
			this.classMapIterator = classMap.values().iterator();
			this.clusterIterator = clusterMap.values().iterator();
		}

		@Override
		public boolean hasNext() {
			if(this.classMapIterator.hasNext())
				return true;
			while(this.clusterIterator.hasNext() && !this.classMapIterator.hasNext()){
				this.classMapIterator = this.clusterIterator.next().iterator();
			}

			return this.classMapIterator.hasNext();
		}

		@Override
		public Class<?> next() {
			return this.classMapIterator.next();
		}
	}

}
