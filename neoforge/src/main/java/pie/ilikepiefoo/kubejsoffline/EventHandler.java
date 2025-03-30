package pie.ilikepiefoo.kubejsoffline;


import pie.ilikepiefoo.kubejsoffline.command.DocumentCommand;

public class EventHandler {

    public static void init() {
        DocumentCommand.EVENT.register(new DocumentCommand());
    }
}
