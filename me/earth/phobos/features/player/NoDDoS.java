package me.earth.phobos.features.modules.player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoDDoS extends Module {
   public final Setting<Boolean> full = this.register(new Setting("Full", false));
   public Setting<String> newIP = this.register(new Setting("NewServer", "Add Server...", (v) -> {
      return !(Boolean)this.full.getValue();
   }));
   public Setting<Boolean> showServer = this.register(new Setting("ShowServers", false, (v) -> {
      return !(Boolean)this.full.getValue();
   }));
   private static NoDDoS instance;
   private final Map<String, Setting> servers = new ConcurrentHashMap();

   public NoDDoS() {
      super("AntiDDoS", "Prevents DDoS attacks", Module.Category.PLAYER, false, false, true);
      instance = this;
   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (!Phobos.configManager.loadingConfig && !Phobos.configManager.savingConfig) {
         if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this)) {
            Setting setting;
            if (event.getSetting().equals(this.newIP) && !this.shouldntPing((String)this.newIP.getPlannedValue()) && !event.getSetting().getPlannedValue().equals(event.getSetting().getDefaultValue())) {
               setting = this.register(new Setting((String)this.newIP.getPlannedValue(), true, (v) -> {
                  return (Boolean)this.showServer.getValue() && !(Boolean)this.full.getValue();
               }));
               this.registerServer(setting);
               Command.sendMessage("<NoDDoS> Added new Server: " + (String)this.newIP.getPlannedValue());
               event.setCanceled(true);
            } else {
               setting = event.getSetting();
               if (setting.equals(this.enabled) || setting.equals(this.drawn) || setting.equals(this.bind) || setting.equals(this.newIP) || setting.equals(this.showServer) || setting.equals(this.full)) {
                  return;
               }

               if (setting.getValue() instanceof Boolean && !(Boolean)setting.getPlannedValue()) {
                  this.servers.remove(setting.getName().toLowerCase());
                  this.unregister(setting);
                  event.setCanceled(true);
               }
            }
         }

      }
   }

   public static NoDDoS getInstance() {
      if (instance == null) {
         instance = new NoDDoS();
      }

      return instance;
   }

   public void registerServer(Setting setting) {
      this.servers.put(setting.getName().toLowerCase(), setting);
   }

   public boolean shouldntPing(String ip) {
      return !this.isOff() && ((Boolean)this.full.getValue() || this.servers.get(ip.toLowerCase()) != null);
   }
}
