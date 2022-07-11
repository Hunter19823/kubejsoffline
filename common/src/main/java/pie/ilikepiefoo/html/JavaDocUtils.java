package pie.ilikepiefoo.html;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class JavaDocUtils {
	public static final Logger LOG = LogManager.getLogger();

	public static <T> String getClassName(Class<T> subject) {


		if(subject.componentType()!=null) {
			getClassName(subject.componentType());
		}



		return subject.getCanonicalName();
	}

	public static String getLink(Class<?> subject) {
		return "";
	}

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("Test");
		JavaDocUtils.getClassName(list.getClass());
		JavaDocUtils.getClassName(String.class);
		JavaDocUtils.getClassName(Logger.class);
		JavaDocUtils.getClassName(EventJS.class);
		JavaDocUtils.getClassName(Minecraft.class);
		JavaDocUtils.getClassName(LootContextParam.class);
		JavaDocUtils.getClassName(args.getClass());
	}
}
