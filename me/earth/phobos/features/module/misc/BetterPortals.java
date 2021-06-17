package me.earth.phobos.features.modules.misc;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BetterPortals extends Module {
   public Setting<Boolean> portalChat = this.register(new Setting("Chat", true, "Allows you to chat in portals."));
   public Setting<Boolean> godmode = this.register(new Setting("Godmode", false, "Portal Godmode."));
   public Setting<Boolean> fastPortal = this.register(new Setting("FastPortal", false));
   public Setting<Integer> cooldown = this.register(new Setting("Cooldown", 5, 1, 10, (v) -> {
      return (Boolean)this.fastPortal.getValue();
   }, "Portal cooldown."));
   public Setting<Integer> time = this.register(new Setting("Time", 5, 0, 80, (v) -> {
      return (Boolean)this.fastPortal.getValue();
   }, "Time in Portal"));
   private static BetterPortals INSTANCE = new BetterPortals();

   public BetterPortals() {
      super("BetterPortals", "Tweaks for Portals", Module.Category.MISC, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static BetterPortals getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new BetterPortals();
      }

      return INSTANCE;
   }

   public String getDisplayInfo() {
      return (Boolean)this.godmode.getValue() ? "Godmode" : null;
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getStage() == 0 && (Boolean)this.godmode.getValue() && event.getPacket() instanceof CPacketConfirmTeleport) {
         event.setCanceled(true);
      }

   }
}
