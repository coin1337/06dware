package me.earth.phobos.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.command.commands.BaritoneNoStop;
import me.earth.phobos.features.command.commands.BindCommand;
import me.earth.phobos.features.command.commands.BookCommand;
import me.earth.phobos.features.command.commands.ConfigCommand;
import me.earth.phobos.features.command.commands.CrashCommand;
import me.earth.phobos.features.command.commands.FriendCommand;
import me.earth.phobos.features.command.commands.HelpCommand;
import me.earth.phobos.features.command.commands.HistoryCommand;
import me.earth.phobos.features.command.commands.ModuleCommand;
import me.earth.phobos.features.command.commands.PeekCommand;
import me.earth.phobos.features.command.commands.PrefixCommand;
import me.earth.phobos.features.command.commands.ReloadCommand;
import me.earth.phobos.features.command.commands.ReloadSoundCommand;
import me.earth.phobos.features.command.commands.UnloadCommand;
import me.earth.phobos.features.command.commands.XrayCommand;

public class CommandManager extends Feature {
   private String clientMessage = "<Phobos.eu>";
   private String prefix = ".";
   private ArrayList<Command> commands = new ArrayList();

   public CommandManager() {
      super("Command");
      this.commands.add(new BindCommand());
      this.commands.add(new ModuleCommand());
      this.commands.add(new PrefixCommand());
      this.commands.add(new ConfigCommand());
      this.commands.add(new FriendCommand());
      this.commands.add(new HelpCommand());
      this.commands.add(new ReloadCommand());
      this.commands.add(new UnloadCommand());
      this.commands.add(new ReloadSoundCommand());
      this.commands.add(new PeekCommand());
      this.commands.add(new XrayCommand());
      this.commands.add(new BookCommand());
      this.commands.add(new CrashCommand());
      this.commands.add(new HistoryCommand());
      this.commands.add(new BaritoneNoStop());
   }

   public void executeCommand(String command) {
      String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
      String name = parts[0].substring(1);
      String[] args = removeElement(parts, 0);

      for(int i = 0; i < args.length; ++i) {
         if (args[i] != null) {
            args[i] = strip(args[i], "\"");
         }
      }

      Iterator var7 = this.commands.iterator();

      Command c;
      do {
         if (!var7.hasNext()) {
            Command.sendMessage("Unknown command. try 'commands' for a list of commands.");
            return;
         }

         c = (Command)var7.next();
      } while(!c.getName().equalsIgnoreCase(name));

      c.execute(parts);
   }

   public static String[] removeElement(String[] input, int indexToDelete) {
      List result = new LinkedList();

      for(int i = 0; i < input.length; ++i) {
         if (i != indexToDelete) {
            result.add(input[i]);
         }
      }

      return (String[])((String[])result.toArray(input));
   }

   private static String strip(String str, String key) {
      return str.startsWith(key) && str.endsWith(key) ? str.substring(key.length(), str.length() - key.length()) : str;
   }

   public Command getCommandByName(String name) {
      Iterator var2 = this.commands.iterator();

      Command command;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         command = (Command)var2.next();
      } while(!command.getName().equals(name));

      return command;
   }

   public ArrayList<Command> getCommands() {
      return this.commands;
   }

   public String getClientMessage() {
      return this.clientMessage;
   }

   public void setClientMessage(String clientMessage) {
      this.clientMessage = clientMessage;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }
}
