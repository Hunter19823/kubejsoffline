package pie.ilikepiefoo.kubejsoffline.mixin;

import dev.latvian.mods.kubejs.script.BindingRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pie.ilikepiefoo.kubejsoffline.OfflinePlugin;

import java.util.ArrayList;
import java.util.Map;

@Mixin(value = BindingRegistry.class, remap = false)
public class BindingsEventMixin {
    @Unique
    private BindingRegistry kubejsoffline$getSelf() {
        return (BindingRegistry) (Object) this;
    }

    @Inject(method = "add", at = @At("RETURN"))
    private void addBinding(String name, Object value, CallbackInfo ci) {
        if (name == null || value == null) {
            return;
        }
        var type = kubejsoffline$getSelf().type();
        OfflinePlugin.BINDINGS.computeIfAbsent(type, (key) -> new ArrayList<>());
        OfflinePlugin.BINDINGS.get(type).add(Map.entry(name, value));
    }
}
