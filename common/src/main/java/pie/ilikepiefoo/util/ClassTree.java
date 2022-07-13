package pie.ilikepiefoo.util;

import com.google.common.base.Strings;

public class ClassTree {
	private final ClassCluster root;

	public ClassTree() {
		this.root = new ClassCluster("root");
	}

	public void addClass(Class<?> target) {
		if(target == null)
			return;
		if(target.isPrimitive())
			this.root.getCluster("primitive");
		while(target.isArray())
			target = target.getComponentType();
		String fullName = target.getName();
		ClassCluster current = root;
		if(fullName.indexOf('.') != -1) {
			int start = fullName.indexOf('.');
			int end = fullName.lastIndexOf('.');
			while(start != end) {
				String clusterName = fullName.substring(0,start);
				fullName = fullName.substring(start+1);
				current = current.getCluster(clusterName);
				start = fullName.indexOf('.');
				end = fullName.lastIndexOf('.');
			}
		}
		current.addClass(target.getSimpleName(), target);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String spacing = "\n";
		int depth = 0;

		for(Class<?> target : root) {
			spacing = "\n"+(Strings.repeat("  ",countCharacter(target.getName(),".")));
			builder.append(spacing).append(target.getName());
		}

		return builder.toString();
	}

	private static int countCharacter(String target, String sequence){
		int start = 0;
		int sum = 0;
		while((start = target.indexOf(sequence)) != -1){
			sum++;
			target = target.substring(start+1);
		}
		return sum;
	}
}
