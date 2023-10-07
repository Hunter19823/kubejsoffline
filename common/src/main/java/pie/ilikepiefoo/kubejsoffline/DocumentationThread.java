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
import pie.ilikepiefoo.kubejsoffline.util.ClassFinder;
import pie.ilikepiefoo.kubejsoffline.util.json.ClassJSON;
import pie.ilikepiefoo.kubejsoffline.util.json.ClassJSONManager;
import pie.ilikepiefoo.kubejsoffline.util.json.CompressionJSON;
import pie.ilikepiefoo.kubejsoffline.util.json.PackageJSONManager;
import pie.ilikepiefoo.kubejsoffline.util.json.RelationsJSON;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

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
        // Log the bindings.
        int step = 0;
        final int totalSteps = 10;
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Initializing ClassFinder and Reflections Library...", ++step, totalSteps));
        final long start = System.currentTimeMillis();
        long timeMillis = System.currentTimeMillis();
        // Setup the ClassFinder
        ClassFinder.INSTANCE.addToSearch(KubeJSOffline.HELPER.getClasses());

        timeMillis = System.currentTimeMillis() - timeMillis;
        sendMessage(String.format("[KJS Offline] [Step %d/%d] ClassFinder setup in %,dms", ++step, totalSteps, timeMillis));

        // Start the ClassFinder
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Starting ClassFinder...", ++step, totalSteps));
        timeMillis = System.currentTimeMillis();
        while (!ClassFinder.INSTANCE.isFinished()) {
            ClassFinder.INSTANCE.searchCurrentDepth();
        }
        timeMillis = System.currentTimeMillis() - timeMillis;
        sendMessage(String.format("[KJS Offline] [Step %d/%d] ClassFinder finished in %,dms", step, totalSteps, timeMillis));

        // Dump connections.
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Generating JSON with class dependencies...", ++step, totalSteps));
        timeMillis = System.currentTimeMillis();
        this.jsonifyConnections();
        timeMillis = System.currentTimeMillis() - timeMillis;
        sendMessage(String.format("[KJS Offline] [Step %d/%d] JSON with class dependencies generated in %,dms", step, totalSteps, timeMillis));


        // Dump ClassTree to JSON
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Generating JSON with full list of class data..", ++step, totalSteps));
        timeMillis = System.currentTimeMillis();
        this.jsonifyClasses();
        timeMillis = System.currentTimeMillis() - timeMillis;
        sendMessage(String.format("[KJS Offline] [Step %d/%d] JSON with full list of class data generated in %,dms", step, totalSteps, timeMillis));

        // Create index.html
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Generating index.html...", ++step, totalSteps));
        timeMillis = System.currentTimeMillis();
        final var output = this.createIndexPage();
        timeMillis = System.currentTimeMillis() - timeMillis;
        if (null != output) {
            sendMessage(String.format("[KJS Offline] [Step %d/%d] index.html generated in %,dms", step, totalSteps, timeMillis));
        } else {
            sendMessage(String.format("[KJS Offline] [Step %d/%d] index.html failed to generate after %,dms!", step, totalSteps, timeMillis));
        }


        final int totalClassSize = ClassFinder.INSTANCE.CLASS_SEARCH.size();
        final int totalRelationSize = ClassFinder.INSTANCE.RELATIONSHIPS.size();
        // Clear and de-reference any data that is no longer needed.
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Clearing and de-referencing data...", ++step, totalSteps));
        timeMillis = System.currentTimeMillis();
        ClassFinder.INSTANCE.clear();
        ClassJSONManager.getInstance().clear();
        DocumentationConfig.clearInstance();
        CompressionJSON.getInstance().clear();
        PackageJSONManager.getInstance().clear();
        timeMillis = System.currentTimeMillis() - timeMillis;
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Data cleared and dereferenced in %,dms", step, totalSteps, timeMillis));
        final long end = System.currentTimeMillis();
        sendMessage(String.format("[KJS Offline] [Step %d/%d] Documentation Thread finished in %,dms", ++step, totalSteps, end - start));
        sendMessage(String.format("[KJS Offline] [Step %d/%d] %,d classes found, %,d relationships found", ++step, totalSteps, totalClassSize, totalRelationSize));
        if (null != output) {
            sendLink(String.format("[KJS Offline] [Step %d/%d] The Documentation page can be found at kubejs/documentation/index.html or by clicking ", step, totalSteps), "here", "kubejs/documentation/index.html");
            sendMessage(String.format("[KJS Offline] [Step %d/%d] Total File Size: ~%,.3fMb", ++step, totalSteps, (double) getFile().length() / 1024.0 / 1024.0));
        } else {
            sendMessage(String.format("[KJS Offline] [Step %d/%d] Documentation page failed to generate!", step, totalSteps - 1));
        }
    }

    private void jsonifyConnections() {
        RelationsJSON.of(ClassFinder.INSTANCE.getRelationships());
    }

    private void jsonifyClasses() {
        ClassFinder.INSTANCE.CLASS_SEARCH.entrySet().parallelStream().forEach((entry) -> {
            try {
                if (ClassFinder.SearchState.SEARCHED == entry.getValue()) {
                    ClassJSON.of(entry.getKey());
                }
            } catch (final Throwable ignored) {
            }
        });
    }

    @Nullable
    private File createIndexPage() {
        final IndexPage page = new IndexPage(GSON);
        return writeHTMLPage(page);
    }
}
