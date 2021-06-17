package me.earth.phobos.features.modules.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.FileManager;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Notifications extends Module {
   public Setting<Boolean> totemPops = this.register(new Setting("TotemPops", false));
   public Setting<Boolean> totemNoti = this.register(new Setting("TotemNoti", true, (v) -> {
      return (Boolean)this.totemPops.getValue();
   }));
   public Setting<Integer> delay = this.register(new Setting("Delay", 2000, 0, 5000, (v) -> {
      return (Boolean)this.totemPops.getValue();
   }, "Delays messages."));
   public Setting<Boolean> clearOnLogout = this.register(new Setting("LogoutClear", false));
   public Setting<Boolean> moduleMessage = this.register(new Setting("ModuleMessage", false));
   public Setting<Boolean> list = this.register(new Setting("List", false, (v) -> {
      return (Boolean)this.moduleMessage.getValue();
   }));
   private Setting<Boolean> readfile = this.register(new Setting("LoadFile", false, (v) -> {
      return (Boolean)this.moduleMessage.getValue();
   }));
   public Setting<Boolean> watermark = this.register(new Setting("Watermark", true, (v) -> {
      return (Boolean)this.moduleMessage.getValue();
   }));
   public Setting<Boolean> visualRange = this.register(new Setting("VisualRange", false));
   public Setting<Boolean> coords = this.register(new Setting("Coords", true, (v) -> {
      return (Boolean)this.visualRange.getValue();
   }));
   public Setting<Boolean> leaving = this.register(new Setting("Leaving", false, (v) -> {
      return (Boolean)this.visualRange.getValue();
   }));
   public Setting<Boolean> pearls = this.register(new Setting("PearlNotifs", false));
   public Setting<Boolean> crash = this.register(new Setting("Crash", false));
   public Setting<Boolean> popUp = this.register(new Setting("PopUpVisualRange", false));
   private List<EntityPlayer> knownPlayers = new ArrayList();
   private static List<String> modules = new ArrayList();
   private static final String fileName = "phobos/util/ModuleMessage_List.txt";
   private final Timer timer = new Timer();
   public Timer totemAnnounce = new Timer();
   private boolean check;
   private static Notifications INSTANCE = new Notifications();

   public Notifications() {
      super("Notifications", "Sends Messages.", Module.Category.CLIENT, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public void onLoad() {
      this.check = true;
      this.loadFile();
      this.check = false;
   }

   public void onEnable() {
      this.knownPlayers = new ArrayList();
      if (!this.check) {
         this.loadFile();
      }

   }

   public void onUpdate() {
      if ((Boolean)this.readfile.getValue()) {
         if (!this.check) {
            Command.sendMessage("Loading File...");
            this.timer.reset();
            this.loadFile();
         }

         this.check = true;
      }

      if (this.check && this.timer.passedMs(750L)) {
         this.readfile.setValue(false);
         this.check = false;
      }

      if ((Boolean)this.visualRange.getValue()) {
         List<EntityPlayer> tickPlayerList = new ArrayList(mc.field_71441_e.field_73010_i);
         Iterator var2;
         EntityPlayer player;
         if (tickPlayerList.size() > 0) {
            var2 = tickPlayerList.iterator();

            while(var2.hasNext()) {
               player = (EntityPlayer)var2.next();
               if (!player.func_70005_c_().equals(mc.field_71439_g.func_70005_c_()) && !this.knownPlayers.contains(player)) {
                  this.knownPlayers.add(player);
                  if (Phobos.friendManager.isFriend(player)) {
                     Command.sendMessage("Player §a" + player.func_70005_c_() + "§r" + " entered your visual range" + ((Boolean)this.coords.getValue() ? " at (" + (int)player.field_70165_t + ", " + (int)player.field_70163_u + ", " + (int)player.field_70161_v + ")!" : "!"), (Boolean)this.popUp.getValue());
                  } else {
                     Command.sendMessage("Player §c" + player.func_70005_c_() + "§r" + " entered your visual range" + ((Boolean)this.coords.getValue() ? " at (" + (int)player.field_70165_t + ", " + (int)player.field_70163_u + ", " + (int)player.field_70161_v + ")!" : "!"), (Boolean)this.popUp.getValue());
                  }

                  return;
               }
            }
         }

         if (this.knownPlayers.size() > 0) {
            var2 = this.knownPlayers.iterator();

            while(var2.hasNext()) {
               player = (EntityPlayer)var2.next();
               if (!tickPlayerList.contains(player)) {
                  this.knownPlayers.remove(player);
                  if ((Boolean)this.leaving.getValue()) {
                     if (Phobos.friendManager.isFriend(player)) {
                        Command.sendMessage("Player §a" + player.func_70005_c_() + "§r" + " left your visual range" + ((Boolean)this.coords.getValue() ? " at (" + (int)player.field_70165_t + ", " + (int)player.field_70163_u + ", " + (int)player.field_70161_v + ")!" : "!"), (Boolean)this.popUp.getValue());
                     } else {
                        Command.sendMessage("Player §c" + player.func_70005_c_() + "§r" + " left your visual range" + ((Boolean)this.coords.getValue() ? " at (" + (int)player.field_70165_t + ", " + (int)player.field_70163_u + ", " + (int)player.field_70161_v + ")!" : "!"), (Boolean)this.popUp.getValue());
                     }
                  }

                  return;
               }
            }
         }
      }

   }

   public void loadFile() {
      List<String> fileInput = FileManager.readTextFileAllLines("phobos/util/ModuleMessage_List.txt");
      Iterator<String> i = fileInput.iterator();
      modules.clear();

      while(i.hasNext()) {
         String s = (String)i.next();
         if (!s.replaceAll("\\s", "").isEmpty()) {
            modules.add(s);
         }
      }

   }

   @SubscribeEvent
   public void onReceivePacket(PacketEvent.Receive event) {
      if (event.getPacket() instanceof SPacketSpawnObject && (Boolean)this.pearls.getValue()) {
         SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
         EntityPlayer player = mc.field_71441_e.func_184137_a(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e(), 1.0D, false);
         if (player == null) {
            return;
         }

         if (packet.func_149001_c() == 85) {
            Command.sendMessage("§cPearl thrown by " + player.func_70005_c_() + " at X:" + (int)packet.func_186880_c() + " Y:" + (int)packet.func_186882_d() + " Z:" + (int)packet.func_186881_e());
         }
      }

   }

   @SubscribeEvent
   public void onToggleModule(ClientEvent event) {
      if ((Boolean)this.moduleMessage.getValue()) {
         Module module;
         int moduleNumber;
         char[] var4;
         int var5;
         int var6;
         char character;
         TextComponentString component;
         if (event.getStage() == 0) {
            module = (Module)event.getFeature();
            if (!module.equals(this) && (modules.contains(module.getDisplayName()) || !(Boolean)this.list.getValue())) {
               moduleNumber = 0;
               var4 = module.getDisplayName().toCharArray();
               var5 = var4.length;

               for(var6 = 0; var6 < var5; ++var6) {
                  character = var4[var6];
                  moduleNumber += character;
                  moduleNumber *= 10;
               }

               if ((Boolean)this.watermark.getValue()) {
                  component = new TextComponentString(Phobos.commandManager.getClientMessage() + " " + "§r" + "§c" + module.getDisplayName() + " disabled.");
                  mc.field_71456_v.func_146158_b().func_146234_a(component, moduleNumber);
               } else {
                  component = new TextComponentString("§c" + module.getDisplayName() + " disabled.");
                  mc.field_71456_v.func_146158_b().func_146234_a(component, moduleNumber);
               }
            }
         }

         if (event.getStage() == 1) {
            module = (Module)event.getFeature();
            if (modules.contains(module.getDisplayName()) || !(Boolean)this.list.getValue()) {
               moduleNumber = 0;
               var4 = module.getDisplayName().toCharArray();
               var5 = var4.length;

               for(var6 = 0; var6 < var5; ++var6) {
                  character = var4[var6];
                  moduleNumber += character;
                  moduleNumber *= 10;
               }

               if ((Boolean)this.watermark.getValue()) {
                  component = new TextComponentString(Phobos.commandManager.getClientMessage() + " " + "§r" + "§a" + module.getDisplayName() + " enabled.");
                  mc.field_71456_v.func_146158_b().func_146234_a(component, moduleNumber);
               } else {
                  component = new TextComponentString("§a" + module.getDisplayName() + " enabled.");
                  mc.field_71456_v.func_146158_b().func_146234_a(component, moduleNumber);
               }
            }
         }

      }
   }

   public static Notifications getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Notifications();
      }

      return INSTANCE;
   }

   public static void displayCrash(Exception e) {
      Command.sendMessage("§cException caught: " + e.getMessage());
   }
}
