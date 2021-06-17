package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class Chams extends Module {
   private static Chams INSTANCE = new Chams();
   public Setting<Boolean> colorSync = this.register(new Setting("Sync", false));
   public Setting<Boolean> colored = this.register(new Setting("Colored", false));
   public Setting<Boolean> rainbow = this.register(new Setting("Rainbow", false, (v) -> {
      return (Boolean)this.colored.getValue();
   }));
   public Setting<Integer> saturation = this.register(new Setting("Saturation", 50, 0, 100, (v) -> {
      return (Boolean)this.colored.getValue() && (Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> brightness = this.register(new Setting("Brightness", 100, 0, 100, (v) -> {
      return (Boolean)this.colored.getValue() && (Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> speed = this.register(new Setting("Speed", 40, 1, 100, (v) -> {
      return (Boolean)this.colored.getValue() && (Boolean)this.rainbow.getValue();
   }));
   public Setting<Boolean> xqz = this.register(new Setting("XQZ", false, (v) -> {
      return (Boolean)this.colored.getValue() && !(Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> red = this.register(new Setting("Red", 0, 0, 255, (v) -> {
      return (Boolean)this.colored.getValue() && !(Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255, (v) -> {
      return (Boolean)this.colored.getValue() && !(Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> blue = this.register(new Setting("Blue", 0, 0, 255, (v) -> {
      return (Boolean)this.colored.getValue() && !(Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255, (v) -> {
      return (Boolean)this.colored.getValue();
   }));
   public Setting<Integer> hiddenRed = this.register(new Setting("Hidden Red", 255, 0, 255, (v) -> {
      return (Boolean)this.colored.getValue() && (Boolean)this.xqz.getValue() && !(Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> hiddenGreen = this.register(new Setting("Hidden Green", 0, 0, 255, (v) -> {
      return (Boolean)this.colored.getValue() && (Boolean)this.xqz.getValue() && !(Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> hiddenBlue = this.register(new Setting("Hidden Blue", 255, 0, 255, (v) -> {
      return (Boolean)this.colored.getValue() && (Boolean)this.xqz.getValue() && !(Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> hiddenAlpha = this.register(new Setting("Hidden Alpha", 255, 0, 255, (v) -> {
      return (Boolean)this.colored.getValue() && (Boolean)this.xqz.getValue() && !(Boolean)this.rainbow.getValue();
   }));

   public Chams() {
      super("Chams", "Renders players through walls.", Module.Category.RENDER, false, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Chams getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Chams();
      }

      return INSTANCE;
   }
}
