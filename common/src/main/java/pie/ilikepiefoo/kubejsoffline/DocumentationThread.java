package pie.ilikepiefoo.kubejsoffline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import pie.ilikepiefoo.kubejsoffline.html.page.IndexPage;
import pie.ilikepiefoo.kubejsoffline.html.tag.Tag;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.impl.TypeManager;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

public class DocumentationThread extends Thread {
    public static final Logger LOG = LogManager.getLogger();

    private static final Gson GSON = new GsonBuilder().create();
    private String outputFile;

    public DocumentationThread() {
        super("KJSOffline DocThread");
    }

    private static void sendMessage(final String message) {
        Minecraft.getInstance().gui.getChat().addMessage(new TextComponent(message));
    }

    private static void sendLink(final String message, final String linkText, final String link) {
        Minecraft.getInstance().gui.getChat().addMessage(new TextComponent(message).append(new TextComponent(linkText).withStyle((style) -> {
            return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, link)).withUnderlined(true).withColor(ChatFormatting.AQUA);
        })));
    }

    private static Path getOutputPath() {
        return KubeJSOffline.HELPER.getWorkingDirectory().resolve("kubejs/documentation");
    }

    @Nullable
    private static File writeHTMLPage(final Tag<?> content) {
        final File output = getFile();

        try (final Writer writer = new FileWriter(output, StandardCharsets.UTF_8)) {
            content.writeHTML(writer);
            writer.flush();
        } catch (final IOException e) {
            LOG.error("Failed to write file: " + "index.html" + " to " + output.getPath(), e);
            return null;
        }
        return output;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File getFile() {
        final Path outputPath = getOutputPath().toAbsolutePath();
        if (!outputPath.toFile().exists()) {
            outputPath.toFile().mkdirs();
        }

        return outputPath.resolve("index.html").toFile();
    }

    @Override
    public void run() {
        // Wait for Helper to be initialized.
        LOG.info("Starting Documentation Thread...");
        while (null == KubeJSOffline.HELPER) {
            LOG.info("Documentation Thread idling until Helper becomes available...");
            try {
                this.wait(5000);
            } catch (final InterruptedException e) {
                LOG.error(e);
            }
        }
        CollectionGroup.INSTANCE.clear();
        // Log the bindings.
        int step = 0;
        final int totalSteps = 7;
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Initializing Reflections Library...", ++step, totalSteps));
        final long start = System.currentTimeMillis();
        long timeMillis = System.currentTimeMillis();

        // Initialize the Reflections Library
        var classes = KubeJSOffline.HELPER.getClasses();

        timeMillis = System.currentTimeMillis() - timeMillis;
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Reflections Library setup in %,dms", ++step, totalSteps, timeMillis));

        // Start the ClassFinder
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Now adding classes to indexer...", ++step, totalSteps));
        timeMillis = System.currentTimeMillis();

        Arrays.stream(classes).parallel().forEach((clazz) -> SafeOperations.tryGet(() -> TypeManager.INSTANCE.getID(clazz)));

        timeMillis = System.currentTimeMillis() - timeMillis;
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Finished adding %s classes to indexer in %,dms", step, totalSteps, classes.length, timeMillis));

        // Create index.html
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Generating index.html and transforming all dependants... (this may take a while)", ++step, totalSteps));
        timeMillis = System.currentTimeMillis();
        final var output = this.createIndexPage();
        timeMillis = System.currentTimeMillis() - timeMillis;
        if (null != output) {
            sendMessage(String.format("[KJS Offline] [Step %d/%d] index.html generated in %,dms", step, totalSteps, timeMillis));
        } else {
            sendMessage(String.format("[KJS Offline] [Step %d/%d] index.html failed to generate after %,dms!", step, totalSteps, timeMillis));
        }

        final int totalTypesSize = CollectionGroup.INSTANCE.types().getAllTypes().size();
        final int totalRawClassSize = CollectionGroup.INSTANCE.types().getAllRawTypes().size();
        final int totalWildcardSize = CollectionGroup.INSTANCE.types().getAllWildcardTypes().size();
        final int totalParameterizedTypeSize = CollectionGroup.INSTANCE.types().getAllParameterizedTypes().size();
        final int totalTypeVariableSize = CollectionGroup.INSTANCE.types().getAllTypeVariables().size();
        final int totalParameterSize = CollectionGroup.INSTANCE.parameters().getAllParameters().size();
        final int totalNameSize = CollectionGroup.INSTANCE.names().getAllNames().size();
        final int totalAnnotationSize = CollectionGroup.INSTANCE.annotations().getAllAnnotations().size();
        final int totalPackageSize = CollectionGroup.INSTANCE.packages().getAllPackages().size();


        // Clear and de-reference any data that is no longer needed.
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Clearing and de-referencing data...", ++step, totalSteps));
        timeMillis = System.currentTimeMillis();
        CollectionGroup.INSTANCE.clear();
        timeMillis = System.currentTimeMillis() - timeMillis;
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Data cleared and de-referenced in %,dms", step, totalSteps, timeMillis));
        final long end = System.currentTimeMillis();
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Documentation Thread finished in %,dms", ++step, totalSteps, end - start));
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Data Collection Summary: %d", step, totalSteps, totalTypesSize));
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Total Types: %d", step, totalSteps, totalTypesSize));
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Total Raw Types: %d", step, totalSteps, totalRawClassSize));
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Total Wildcard Types: %d", step, totalSteps, totalWildcardSize));
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Total Parameterized Types: %d", step, totalSteps, totalParameterizedTypeSize));
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Total Type Variables: %d", step, totalSteps, totalTypeVariableSize));
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Total Parameters: %d", step, totalSteps, totalParameterSize));
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Total Names: %d", step, totalSteps, totalNameSize));
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Total Packages: %d", step, totalSteps, totalPackageSize));
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Total Annotations: %d", step, totalSteps, totalAnnotationSize));
        if (null != output) {
            sendLink(String.format("[KJS Offline] [Step %d/%d] The Documentation page can be found at kubejs/documentation/index.html or by clicking ", step, totalSteps), "here", "kubejs/documentation/index.html");
            sendMessage(String.format("[KJS Offline] [Step %d/%d] Total File Size: ~%,.3fMb", ++step, totalSteps, (double) getFile().length() / 1024.0 / 1024.0));
        } else {
            sendMessage(String.format("[KJS Offline] [Step %d/%d] Documentation page failed to generate!", step, totalSteps - 1));
        }
    }

    @Nullable
    private File createIndexPage() {
        final IndexPage page = new IndexPage(GSON);
        return writeHTMLPage(page);
    }
}
