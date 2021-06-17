package me.earth.phobos.features.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;

public abstract class Command extends Feature {
   protected String name;
   protected String[] commands;

   public Command(String name) {
      super(name);
      this.name = name;
      this.commands = new String[]{""};
   }

   public Command(String name, String[] commands) {
      super(name);
      this.name = name;
      this.commands = commands;
   }

   public abstract void execute(String[] var1);

   public static void sendMessage(String message, boolean notification) {
      sendSilentMessage(Phobos.commandManager.getClientMessage() + " " + "§r" + message);
      if (notification) {
         Phobos.notificationManager.addNotification(message, 3000L);
      }

   }

   public static void sendMessage(String message) {
      sendSilentMessage(Phobos.commandManager.getClientMessage() + " " + "§r" + message);
   }

   public static void sendSilentMessage(String message) {
      if (!nullCheck()) {
         mc.field_71439_g.func_145747_a(new Command.ChatMessage(message));
      }
   }

   public static void sendOverwriteMessage(String message, int id, boolean notification) {
      TextComponentString component = new TextComponentString(message);
      mc.field_71456_v.func_146158_b().func_146234_a(component, id);
      if (notification) {
         Phobos.notificationManager.addNotification(message, 3000L);
      }

   }

   public String getName() {
      return this.name;
   }

   public String[] getCommands() {
      return this.commands;
   }

   public static String getCommandPrefix() {
      return Phobos.commandManager.getPrefix();
   }

   public static class ChatMessage extends TextComponentBase {
      private final String text;

      public ChatMessage(String text) {
         Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
         Matcher matcher = pattern.matcher(text);
         StringBuffer stringBuffer = new StringBuffer();

         while(matcher.find()) {
            String replacement = "§" + matcher.group().substring(1);
            matcher.appendReplacement(stringBuffer, replacement);
         }

         matcher.appendTail(stringBuffer);
         this.text = stringBuffer.toString();
      }

      public String func_150261_e() {
         return this.text;
      }

      public ITextComponent func_150259_f() {
         return new Command.ChatMessage(this.text);
      }
   }
}
