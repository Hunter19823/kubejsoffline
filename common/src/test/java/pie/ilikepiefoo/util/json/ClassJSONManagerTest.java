package pie.ilikepiefoo.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.loot.LootEventJS;
import org.junit.jupiter.api.Test;
import pie.ilikepiefoo.util.SafeOperations;

import java.util.function.Consumer;


public class ClassJSONManagerTest {
	private Gson gson() {
		return new GsonBuilder().setPrettyPrinting().create();
	}

	@Test
	public void testObjectClass() {
		var out = ClassJSONManager.getInstance().getTypeData(Object.class);
		assert out.get("id").isJsonPrimitive();
		assert out.get("id").getAsInt() == 0;
		assert out.get("type").isJsonPrimitive();
		assert out.get("type").getAsString().equals("java.lang.Object");
	}

	@Test
	public void safeOperationsTest() {
		ClassJSON.of(SafeOperations.class);
		var out = ClassJSONManager.getInstance().getTypeData(SafeOperations.class);
		assert out.get("id").isJsonPrimitive();

		System.out.println(gson().toJson(ClassJSONManager.getInstance().getTypeData()));
	}

	public static class TestClass {
		public boolean[] test() {
			return null;
		}
		public boolean test2() {
			return false;
		}

		public Consumer<TestClass> test3() {
			return null;
		}

		public TestClass test4(Consumer<TestClass> test) {
			return null;
		}
	}

	@Test
	public void eventJSTest() {
		ClassJSON.of(EventJS.class);
		var out = ClassJSONManager.getInstance().getTypeData(EventJS.class);
		assert out.get("id").isJsonPrimitive();

		System.out.println(gson().toJson(ClassJSONManager.getInstance().getTypeData()));
	}

	@Test
	public void testClass() {
		ClassJSON.of(TestClass.class);
		var out = ClassJSONManager.getInstance().getTypeData(TestClass.class);
		assert out.get("id").isJsonPrimitive();

		System.out.println(gson().toJson(ClassJSONManager.getInstance().getTypeData()));
	}

	@Test
	public void testLootEventJS() {
		ClassJSON.of(LootEventJS.class);
		var out = ClassJSONManager.getInstance().getTypeData(LootEventJS.class);
		assert out.get("id").isJsonPrimitive();

		System.out.println(gson().toJson(ClassJSONManager.getInstance().getTypeData()));
	}
}