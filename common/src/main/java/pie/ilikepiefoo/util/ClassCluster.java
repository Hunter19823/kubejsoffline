package pie.ilikepiefoo.util;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

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
		if(!this.classMap.containsKey(target))
			this.classMap.put(target, subject);
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
		return this.classMap.values().iterator();
	}

}
