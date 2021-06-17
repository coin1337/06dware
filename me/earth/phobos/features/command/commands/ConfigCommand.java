package me.earth.phobos.features.command.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;

public class ConfigCommand extends Command {
   public ConfigCommand() {
      super("config", new String[]{"<save/load>"});
   }

   public void execute(String[] commands) {
      if (commands.length == 1) {
         sendMessage("You`ll find the config files in your gameProfile directory under phobos/config");
      } else {
         String configs;
         if (commands.length == 2) {
            if ("list".equals(commands[0])) {
               configs = "Configs: ";
               File file = new File("phobos/");
               List<File> directories = (List)Arrays.stream(file.listFiles()).filter(File::isDirectory).filter((f) -> {
                  return !f.getName().equals("util");
               }).collect(Collectors.toList());
               StringBuilder builder = new StringBuilder(configs);
               Iterator var6 = directories.iterator();

               while(var6.hasNext()) {
                  File file1 = (File)var6.next();
                  builder.append(file1.getName() + ", ");
               }

               configs = builder.toString();
               sendMessage("§a" + configs);
            } else {
               sendMessage("§cNot a valid command... Possible usage: <list>");
            }
         }

         if (commands.length >= 3) {
            configs = commands[0];
            byte var8 = -1;
            switch(configs.hashCode()) {
            case 3327206:
               if (configs.equals("load")) {
                  var8 = 1;
               }
               break;
            case 3522941:
               if (configs.equals("save")) {
                  var8 = 0;
               }
            }

            switch(var8) {
            case 0:
               Phobos.configManager.saveConfig(commands[1]);
               sendMessage("§aConfig has been saved.");
               break;
            case 1:
               Phobos.moduleManager.onUnload();
               Phobos.configManager.loadConfig(commands[1]);
               Phobos.moduleManager.onLoad();
               sendMessage("§aConfig has been loaded.");
               break;
            default:
               sendMessage("§cNot a valid command... Possible usage: <save/load>");
            }
         }

      }
   }
}
