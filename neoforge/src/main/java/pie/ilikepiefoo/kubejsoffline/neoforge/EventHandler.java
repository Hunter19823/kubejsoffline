package pie.ilikepiefoo.kubejsoffline.neoforge;


import pie.ilikepiefoo.kubejsoffline.neoforge.command.DocumentCommand;

public class EventHandler {

    public static void init() {
        DocumentCommand.EVENT.register(new DocumentCommand());
    }
}
