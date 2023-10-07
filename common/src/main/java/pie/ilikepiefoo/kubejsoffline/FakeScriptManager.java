package pie.ilikepiefoo.kubejsoffline;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptType;

public class FakeScriptManager extends ScriptManager {

    public FakeScriptManager(ScriptType t) {
        super(t, null, null);
    }

    @Override
    public void unload() {

    }

    @Override
    public void loadFromDirectory() {

    }

    @Override
    public boolean isClassAllowed(String name) {
        return false;
    }

    @Override
    public void load() {

    }

}
