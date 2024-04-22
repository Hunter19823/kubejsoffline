package pie.ilikepiefoo.kubejsoffline.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DocumentationBridge {
    private final Supplier<ResourceManager> resourceManagerSupplier;
    private final Consumer<Component> messenger;

    public DocumentationBridge(Supplier<ResourceManager> resourceManagerSupplier, Consumer<Component> messenger) {
        this.resourceManagerSupplier = resourceManagerSupplier;
        this.messenger = messenger;
    }

    public Supplier<ResourceManager> getResourceManagerSupplier() {
        return this.resourceManagerSupplier;
    }

    public Consumer<Component> getMessenger() {
        return this.messenger;
    }

    public void sendMessage(Component message) {
        this.messenger.accept(message);
    }


    public boolean hasResource(ResourceLocation location) {
        return this.resourceManagerSupplier.get().hasResource(location);
    }

    public InputStream getResource(ResourceLocation location) throws IOException {
        return this.resourceManagerSupplier.get().getResource(location).getInputStream();
    }

}
