package me.earth.phobos.features.modules.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.FileManager;

public class Announcer extends Module {
   private final Setting<Boolean> join = this.register(new Setting("Join", true));
   private final Setting<Boolean> leave = this.register(new Setting("Leave", true));
   private final Setting<Boolean> eat = this.register(new Setting("Eat", true));
   private final Setting<Boolean> walk = this.register(new Setting("Walk", true));
   private final Setting<Boolean> mine = this.register(new Setting("Mine", true));
   private final Setting<Boolean> place = this.register(new Setting("Place", true));
   private final Setting<Boolean> totem = this.register(new Setting("TotemPop", true));
   private final Setting<Boolean> random = this.register(new Setting("Random", true));
   private final Setting<Boolean> greentext = this.register(new Setting("Greentext", false));
   private final Setting<Boolean> loadFiles = this.register(new Setting("LoadFiles", false));
   private final Setting<Integer> delay = this.register(new Setting("SendDelay", 40));
   private final Setting<Integer> queueSize = this.register(new Setting("QueueSize", 5, 1, 100));
   private final Setting<Integer> mindistance = this.register(new Setting("Min Distance", 10, 1, 100));
   private final Setting<Boolean> clearQueue = this.register(new Setting("ClearQueue", false));
   private static final String directory = "phobos/announcer/";
   private Map<Announcer.Action, ArrayList<String>> loadedMessages = new HashMap();
   private Map<Announcer.Action, Announcer.Message> queue = new HashMap();

   public Announcer() {
      super("Announcer", "How to get muted quick.", Module.Category.MISC, true, false, false);
   }

   public void onLoad() {
      this.loadMessages();
   }

   public void onEnable() {
      this.loadMessages();
   }

   public void onUpdate() {
      if ((Boolean)this.loadFiles.getValue()) {
         this.loadMessages();
         Command.sendMessage("<Announcer> Loaded messages.");
         this.loadFiles.setValue(false);
      }

   }

   public void loadMessages() {
      HashMap<Announcer.Action, ArrayList<String>> newLoadedMessages = new HashMap();
      Announcer.Action[] var2 = Announcer.Action.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Announcer.Action action = var2[var4];
         String fileName = "phobos/announcer/" + action.getName() + ".txt";
         List<String> fileInput = FileManager.readTextFileAllLines(fileName);
         Iterator<String> i = fileInput.iterator();
         ArrayList msgs = new ArrayList();

         while(i.hasNext()) {
            String string = (String)i.next();
            if (!string.replaceAll("\\s", "").isEmpty()) {
               msgs.add(string);
            }
         }

         if (msgs.isEmpty()) {
            msgs.add(action.getStandartMessage());
         }

         newLoadedMessages.put(action, msgs);
      }

      this.loadedMessages = newLoadedMessages;
   }

   private String getMessage(Announcer.Action action, int number, String info) {
      return "";
   }

   private Announcer.Action getRandomAction() {
      Random rnd = new Random();
      int index = rnd.nextInt(7);
      int i = 0;
      Announcer.Action[] var4 = Announcer.Action.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Announcer.Action action = var4[var6];
         if (i == index) {
            return action;
         }

         ++i;
      }

      return Announcer.Action.WALK;
   }

   public static enum Action {
      JOIN("Join", "Welcome _!"),
      LEAVE("Leave", "Goodbye _!"),
      EAT("Eat", "I just ate % _!"),
      WALK("Walk", "I just walked % Blocks!"),
      MINE("Mine", "I mined % _!"),
      PLACE("Place", "I just placed % _!"),
      TOTEM("Totem", "_ just popped % Totems!");

      private final String name;
      private final String standartMessage;

      private Action(String name, String standartMessage) {
         this.name = name;
         this.standartMessage = standartMessage;
      }

      public String getName() {
         return this.name;
      }

      public String getStandartMessage() {
         return this.standartMessage;
      }
   }

   public static class Message {
      public final Announcer.Action action;
      public final String name;
      public final int amount;

      public Message(Announcer.Action action, String name, int amount) {
         this.action = action;
         this.name = name;
         this.amount = amount;
      }
   }
}
