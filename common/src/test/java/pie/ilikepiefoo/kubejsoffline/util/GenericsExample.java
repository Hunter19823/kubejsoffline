package pie.ilikepiefoo.kubejsoffline.util;

import dev.latvian.mods.kubejs.event.EventJS;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GenericsExample<EXAMPLE> {
	public EXAMPLE example;
	public Map<String, EXAMPLE> exampleMap;

	public Map<EXAMPLE, EXAMPLE> exampleMap2;
	public Set<EXAMPLE> exampleSet;
	public Set<? extends EXAMPLE> exampleSet2;
	public Set<? super EXAMPLE> exampleSet3;

	public EXAMPLE getExample() {
		return this.example;
	}

	public void setExample(final EXAMPLE example) {
		this.example = example;
	}

	public Set<EXAMPLE> getExampleSet() {
		return this.exampleSet;
	}

	public void setExampleSet(final Set<EXAMPLE> exampleSet) {
		this.exampleSet = exampleSet;
	}

	public Set<? extends EXAMPLE> getExampleSet2() {
		return this.exampleSet2;
	}

	public void setExampleSet2(final Set<? extends EXAMPLE> exampleSet2) {
		this.exampleSet2 = exampleSet2;
	}

	public Set<? super EXAMPLE> getExampleSet3() {
		return this.exampleSet3;
	}

	public void setExampleSet3(final Set<? super EXAMPLE> exampleSet3) {
		this.exampleSet3 = exampleSet3;
	}

	public Map<String, EXAMPLE> getExampleMap() {
		return this.exampleMap;
	}

	public void setExampleMap(final Map<String, EXAMPLE> exampleMap) {
		this.exampleMap = exampleMap;
	}

	public Map<EXAMPLE, EXAMPLE> getExampleMap2() {
		return this.exampleMap2;
	}

	public void setExampleMap2(final Map<EXAMPLE, EXAMPLE> exampleMap2) {
		this.exampleMap2 = exampleMap2;
	}

	public <T> T get(final T t) {
		return t;
	}

	public <T extends Number> T getNumber(final T t) {
		return t;
	}

	public <T extends Number & Comparable<T>> T getNumberComparable(final T t) {
		return t;
	}

	public <T extends Number & Comparable<T> & Cloneable> T getNumberComparableCloneable(final T t) {
		return t;
	}

	public <T extends Number, U extends Number> T getNumberNumber(final T t, final U u) {
		return t;
	}

	public <T extends Number, U extends Number & Comparable<U>> T getNumberNumberComparable(final T t, final U u) {
		return t;
	}

	public <T extends Number, U extends Number & Comparable<U> & Cloneable> T getNumberNumberComparableCloneable(final T t, final U u) {
		return t;
	}

	public <T extends String, V extends EventJS> Map<T, V> getMap(final T t, final V v) {
		return new HashMap<>();
	}

    public <G extends ExampleInterface<G> & Map<String,ExampleInterface<? extends G[]>[]>, T extends G> Map<T[],G[][][][][]>[] getMap2( final T... t ) {
        return null;
    }

    public class ExampleInterface<CONFUSING_INNER_CLASS_GENERIC> {
        public CONFUSING_INNER_CLASS_GENERIC getClassVariable() {
            return null;
        }

        public EXAMPLE getExampleVariable() {
            return null;
        }

        public <CONFUSING_INNER_CLASS_GENERIC extends Number> CONFUSING_INNER_CLASS_GENERIC getGenericMethodWithHiddenVariableName(
                CONFUSING_INNER_CLASS_GENERIC t ) {
            return t;
        }

        public <T extends GenericsExample<? extends GenericsExample<? super GenericsExample<EXAMPLE>[]>[]>, EXAMPLE extends T> Map<EXAMPLE[],
                T[][][][][]>[] getGenericHell(
                T... t ) {
            return null;
        }

    }
}
