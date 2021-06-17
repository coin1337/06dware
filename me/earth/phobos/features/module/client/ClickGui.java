package me.earth.phobos.features.modules.client;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui extends Module {
   public Setting<Boolean> colorSync = this.register(new Setting("Sync", false));
   public Setting<Boolean> rainbowRolling = this.register(new Setting("RollingRainbow", false, (v) -> {
      return (Boolean)this.colorSync.getValue() && (Boolean)Colors.INSTANCE.rainbow.getValue();
   }));
   public Setting<String> prefix = this.register((new Setting("Prefix", ".")).setRenderName(true));
   public Setting<Integer> red = this.register(new Setting("Red", 255, 0, 255));
   public Setting<Integer> green = this.register(new Setting("Green", 0, 0, 255));
   public Setting<Integer> blue = this.register(new Setting("Blue", 0, 0, 255));
   public Setting<Integer> hoverAlpha = this.register(new Setting("Alpha", 180, 0, 255));
   public Setting<Integer> alpha = this.register(new Setting("HoverAlpha", 240, 0, 255));
   public Setting<Boolean> customFov = this.register(new Setting("CustomFov", false));
   public Setting<Float> fov = this.register(new Setting("Fov", 150.0F, -180.0F, 180.0F, (v) -> {
      return (Boolean)this.customFov.getValue();
   }));
   public Setting<Boolean> openCloseChange = this.register(new Setting("Open/Close", false));
   public Setting<String> open = this.register((new Setting("Open:", "", (v) -> {
      return (Boolean)this.openCloseChange.getValue();
   })).setRenderName(true));
   public Setting<String> close = this.register((new Setting("Close:", "", (v) -> {
      return (Boolean)this.openCloseChange.getValue();
   })).setRenderName(true));
   public Setting<String> moduleButton = this.register((new Setting("Buttons:", "", (v) -> {
      return !(Boolean)this.openCloseChange.getValue();
   })).setRenderName(true));
   public Setting<Boolean> devSettings = this.register(new Setting("DevSettings", false));
   public Setting<Integer> topRed = this.register(new Setting("TopRed", 255, 0, 255, (v) -> {
      return (Boolean)this.devSettings.getValue();
   }));
   public Setting<Integer> topGreen = this.register(new Setting("TopGreen", 0, 0, 255, (v) -> {
      return (Boolean)this.devSettings.getValue();
   }));
   public Setting<Integer> topBlue = this.register(new Setting("TopBlue", 0, 0, 255, (v) -> {
      return (Boolean)this.devSettings.getValue();
   }));
   public Setting<Integer> topAlpha = this.register(new Setting("TopAlpha", 255, 0, 255, (v) -> {
      return (Boolean)this.devSettings.getValue();
   }));
   private static ClickGui INSTANCE = new ClickGui();

   public ClickGui() {
      super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static ClickGui getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ClickGui();
      }

      return INSTANCE;
   }

   public void onUpdate() {
      if ((Boolean)this.customFov.getValue()) {
         mc.field_71474_y.func_74304_a(Options.FOV, (Float)this.fov.getValue());
      }

   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
         if (event.getSetting().equals(this.prefix)) {
            Phobos.commandManager.setPrefix((String)this.prefix.getPlannedValue());
            Command.sendMessage("Prefix set to §a" + Phobos.commandManager.getPrefix());
         }

         Phobos.colorManager.setColor((Integer)this.red.getPlannedValue(), (Integer)this.green.getPlannedValue(), (Integer)this.blue.getPlannedValue(), (Integer)this.hoverAlpha.getPlannedValue());
      }

   }

   public void onEnable() {
      mc.func_147108_a(new PhobosGui());
   }

   public void onLoad() {
      if ((Boolean)this.colorSync.getValue()) {
         Phobos.colorManager.setColor(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), (Integer)this.hoverAlpha.getValue());
      } else {
         Phobos.colorManager.setColor((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.hoverAlpha.getValue());
      }

      Phobos.commandManager.setPrefix((String)this.prefix.getValue());
   }

   public void onTick() {
      if (!(mc.field_71462_r instanceof PhobosGui)) {
         this.disable();
      }

   }

   public void onDisable() {
      if (mc.field_71462_r instanceof PhobosGui) {
         mc.func_147108_a((GuiScreen)null);
      }

   }
}
