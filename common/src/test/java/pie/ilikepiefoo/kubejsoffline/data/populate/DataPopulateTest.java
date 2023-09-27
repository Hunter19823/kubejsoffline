package pie.ilikepiefoo.kubejsoffline.data.populate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import pie.ilikepiefoo.kubejsoffline.data.ClassData;
import pie.ilikepiefoo.kubejsoffline.util.ClassFinder;
import pie.ilikepiefoo.kubejsoffline.util.GenericNumberExample;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class DataPopulateTest {
    public static final Logger LOG = LogManager.getLogger();
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Test
    void wrapObject() {
        DataPopulate dataPopulate = new DataPopulate();

        var data = dataPopulate.wrap(Object.class);
        assertEquals("java.lang.Object", data.getName());
        if (!( data instanceof ClassData classData )) {
            fail("Data is not ClassData");
            return;
        }
        assertEquals(0, classData.getSuperClasses().size());
        assertEquals(0, classData.getImplementedInterfaces().size());
        assertEquals(0, classData.getFields().size());
        assertEquals(0, classData.getMethods().size());
        assertEquals(0, classData.getConstructors().size());
        assertTrue(Modifier.isPublic(classData.getModifiers()));
        assertFalse(Modifier.isPrivate(classData.getModifiers()));
        assertFalse(Modifier.isProtected(classData.getModifiers()));
        assertFalse(Modifier.isStatic(classData.getModifiers()));

		LOG.info(GSON.toJson(classData.toJSON()));

    }

	@Test
	void populateObject() {
		DataPopulate dataPopulate = new DataPopulate();
		var data = dataPopulate.wrap(Object.class);
		dataPopulate.populate((ClassData) data);

		assertEquals("java.lang.Object", data.getName());
		assertEquals(11, ((ClassData) data).getMethods().size());
		assertEquals(0, ((ClassData) data).getFields().size());
		assertEquals(0, ((ClassData) data).getConstructors().size());
		assertEquals(0, ((ClassData) data).getAnnotations().size());

		LOG.info(GSON.toJson(dataPopulate.toJsonArray()));
	}

	@Test
	void populateGenericExample() {
		DataPopulate dataPopulate = new DataPopulate();
		GenericNumberExample example = new GenericNumberExample();
		var data = dataPopulate.wrap(example.getClass());
		dataPopulate.populateTree((ClassData) data);


		LOG.info(dataPopulate.toJsonArray());
	}

	@Test
	void classFinderTest() {
		DataPopulate dataPopulate = new DataPopulate();
		GenericNumberExample example = new GenericNumberExample();
		ClassFinder finder = ClassFinder.INSTANCE;
		finder.addToSearch(example.getClass());
		finder.onSearched(dataPopulate::wrap);
		while (!finder.isFinished()) {
			finder.searchCurrentDepth();
		}

		LOG.info(GSON.toJson(dataPopulate.toJsonArray()));

	}

}