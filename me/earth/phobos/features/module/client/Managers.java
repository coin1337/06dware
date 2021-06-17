package me.earth.phobos.features.modules.client;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.TextUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Managers extends Module {
   public Setting<Boolean> betterFrames = this.register(new Setting("BetterMaxFPS", false));
   private static Managers INSTANCE = new Managers();
   public Setting<String> commandBracket = this.register(new Setting("Bracket", "<"));
   public Setting<String> commandBracket2 = this.register(new Setting("Bracket2", ">"));
   public Setting<String> command = this.register(new Setting("Command", "Phobos.eu"));
   public Setting<TextUtil.Color> bracketColor;
   public Setting<TextUtil.Color> commandColor;
   public Setting<Integer> betterFPS;
   public Setting<Boolean> potions;
   public Setting<Integer> textRadarUpdates;
   public Setting<Integer> respondTime;
   public Setting<Integer> moduleListUpdates;
   public Setting<Float> holeRange;
   public Setting<Integer> holeUpdates;
   public Setting<Integer> holeSync;
   public Setting<Boolean> safety;
   public Setting<Integer> safetyCheck;
   public Setting<Integer> safetySync;
   public Setting<Managers.ThreadMode> holeThread;
   public Setting<Boolean> speed;
   public Setting<Boolean> oneDot15;
   public Setting<Boolean> tRadarInv;
   public Setting<Boolean> unfocusedCpu;
   public Setting<Integer> cpuFPS;
   public Setting<Integer> baritoneTimeOut;
   public Setting<Boolean> oneChunk;

   public Managers() {
      super("Management", "ClientManagement", Module.Category.CLIENT, false, false, true);
      this.bracketColor = this.register(new Setting("BColor", TextUtil.Color.BLUE));
      this.commandColor = this.register(new Setting("CColor", TextUtil.Color.BLUE));
      this.betterFPS = this.register(new Setting("MaxFPS", 300, 30, 1000, (v) -> {
         return (Boolean)this.betterFrames.getValue();
      }));
      this.potions = this.register(new Setting("Potions", true));
      this.textRadarUpdates = this.register(new Setting("TRUpdates", 500, 0, 1000));
      this.respondTime = this.register(new Setting("SeverTime", 500, 0, 1000));
      this.moduleListUpdates = this.register(new Setting("ALUpdates", 1000, 0, 1000));
      this.holeRange = this.register(new Setting("HoleRange", 6.0F, 1.0F, 256.0F));
      this.holeUpdates = this.register(new Setting("HoleUpdates", 100, 0, 1000));
      this.holeSync = this.register(new Setting("HoleSync", 10000, 1, 10000));
      this.safety = this.register(new Setting("SafetyPlayer", false));
      this.safetyCheck = this.register(new Setting("SafetyCheck", 50, 1, 150));
      this.safetySync = this.register(new Setting("SafetySync", 250, 1, 10000));
      this.holeThread = this.register(new Setting("HoleThread", Managers.ThreadMode.WHILE));
      this.speed = this.register(new Setting("Speed", true));
      this.oneDot15 = this.register(new Setting("1.15", false));
      this.tRadarInv = this.register(new Setting("TRadarInv", true));
      this.unfocusedCpu = this.register(new Setting("UnfocusedCPU", false));
      this.cpuFPS = this.register(new Setting("UnfocusedFPS", 60, 1, 60, (v) -> {
         return (Boolean)this.unfocusedCpu.getValue();
      }));
      this.baritoneTimeOut = this.register(new Setting("Baritone", 5, 1, 20));
      this.oneChunk = this.register(new Setting("OneChunk", false));
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Managers getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Managers();
      }

      return INSTANCE;
   }

   public void onLoad() {
      Phobos.commandManager.setClientMessage(this.getCommandMessage());
   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (event.getStage() == 2) {
         if ((Boolean)this.oneChunk.getPlannedValue()) {
            mc.field_71474_y.field_151451_c = 1;
         }

         if (event.getSetting() != null && this.equals(event.getSetting().getFeature())) {
            if (event.getSetting().equals(this.holeThread)) {
               Phobos.holeManager.settingChanged();
            }

            Phobos.commandManager.setClientMessage(this.getCommandMessage());
         }
      }

   }

   public String getCommandMessage() {
      return TextUtil.coloredString((String)this.commandBracket.getPlannedValue(), (TextUtil.Color)this.bracketColor.getPlannedValue()) + TextUtil.coloredString((String)this.command.getPlannedValue(), (TextUtil.Color)this.commandColor.getPlannedValue()) + TextUtil.coloredString((String)this.commandBracket2.getPlannedValue(), (TextUtil.Color)this.bracketColor.getPlannedValue());
   }

   public String getRawCommandMessage() {
      return (String)this.commandBracket.getValue() + (String)this.command.getValue() + (String)this.commandBracket2.getValue();
   }

   public static enum ThreadMode {
      POOL,
      WHILE,
      NONE;
   }
}
