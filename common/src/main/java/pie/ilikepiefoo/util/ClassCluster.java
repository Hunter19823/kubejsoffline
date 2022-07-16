package pie.ilikepiefoo.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ClassCluster implements Iterable<Class<?>> {
	private final String name;
	private ClassCluster parent;
	private Set<Class<?>> classSet;
	private Map<String, ClassCluster> clusterMap;

	public ClassCluster(String name) {
		this.name = name;
		this.classSet = Collections.synchronizedSet(new HashSet<>());
		this.clusterMap = new ConcurrentHashMap<>();
		this.parent = null;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	public int getShallowCount() {
		return this.classSet.size();
	}

	@Nullable
	public ClassCluster getParent() {
		return parent;
	}

	public String getFullName(String separator) {
		StringBuilder builder = new StringBuilder();
		builder.append(separator);
		Stack<ClassCluster> clusters = getLineage();
		ClassCluster cluster = null;
		while(!clusters.isEmpty()){
			cluster = clusters.pop();
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

	public boolean containsClass(Class<?> target) {
		return this.classSet.contains(target);
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

	public Collection<Class<?>> getClasses() {
		return this.classSet;
	}

	public List<ClassCluster> getClusters() {
		return this.clusterMap.values().parallelStream().toList();
	}

	public void addClass(Class<?> subject) {
		this.classSet.add(subject);
	}

	public String toTree() {
		StringBuilder builder = new StringBuilder();
		String padding = "\n";//+Strings.repeat("  ",getLineage().size());
		builder.append(padding);
		builder.append(getFullName("/"));

		padding+="  - ";
		for(Class<?> target : this) {
			builder.append(padding);
			builder.append(target.getName());
		}


		return builder.toString();
	}

	private static int countCharacter(String target, String sequence){
		int start;
		int sum = 0;
		while((start = target.indexOf(sequence)) != -1){
			sum++;
			target = target.substring(start+1);
		}
		return sum;
	}

	public Stream<ClassCluster> stream() {
		return Stream.concat(Stream.of(this), this.clusterMap.values().stream().flatMap(ClassCluster::stream));
	}

	@NotNull
	@Override
	public Iterator<Class<?>> iterator() {
		return this.classSet.iterator();
	}

	public void compress() {
		this.clusterMap = ImmutableMap.copyOf(this.clusterMap);
		this.classSet = ImmutableSet.copyOf(this.classSet);
	}

}
