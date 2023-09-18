package pie.ilikepiefoo.kubejsoffline.data.populate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import pie.ilikepiefoo.kubejsoffline.data.ClassData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class DataPopulateTest {
    public static final Logger LOG = LogManager.getLogger();

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

        LOG.info(classData.toJSON());

    }

}