package me.earth.phobos.features.command.commands;

import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;

public class HelpCommand extends Command {
   public HelpCommand() {
      super("commands");
   }

   public void execute(String[] commands) {
      sendMessage("You can use following commands: ");
      Iterator var2 = Phobos.commandManager.getCommands().iterator();

      while(var2.hasNext()) {
         Command command = (Command)var2.next();
         sendMessage(Phobos.commandManager.getPrefix() + command.getName());
      }

   }
}
