package me.earth.phobos.features.command.commands;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.client.ClickGui;

public class PrefixCommand extends Command {
   public PrefixCommand() {
      super("prefix", new String[]{"<char>"});
   }

   public void execute(String[] commands) {
      if (commands.length == 1) {
         Command.sendMessage("§cSpecify a new prefix.");
      } else {
         ((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).prefix.setValue(commands[0]);
         Command.sendMessage("Prefix set to §a" + Phobos.commandManager.getPrefix());
      }
   }
}
