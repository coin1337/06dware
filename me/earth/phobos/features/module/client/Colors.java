package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;

public class Colors extends Module {
   public Setting<Boolean> rainbow = this.register(new Setting("Rainbow", false, "Rainbow colors."));
   public Setting<Integer> rainbowSpeed = this.register(new Setting("Speed", 20, 0, 100, (v) -> {
      return (Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> rainbowSaturation = this.register(new Setting("Saturation", 255, 0, 255, (v) -> {
      return (Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> rainbowBrightness = this.register(new Setting("Brightness", 255, 0, 255, (v) -> {
      return (Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> red = this.register(new Setting("Red", 255, 0, 255, (v) -> {
      return !(Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255, (v) -> {
      return !(Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> blue = this.register(new Setting("Blue", 255, 0, 255, (v) -> {
      return !(Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255, (v) -> {
      return !(Boolean)this.rainbow.getValue();
   }));
   public float hue;
   public Map<Integer, Integer> colorHeightMap = new HashMap();
   public static Colors INSTANCE;

   public Colors() {
      super("Colors", "Universal colors.", Module.Category.CLIENT, true, false, true);
      INSTANCE = this;
   }

   public void onTick() {
      int colorSpeed = 101 - (Integer)this.rainbowSpeed.getValue();
      this.hue = (float)(System.currentTimeMillis() % (long)(360 * colorSpeed)) / (360.0F * (float)colorSpeed);
      float tempHue = this.hue;

      for(int i = 0; i <= 510; ++i) {
         this.colorHeightMap.put(i, Color.HSBtoRGB(tempHue, (float)(Integer)this.rainbowSaturation.getValue() / 255.0F, (float)(Integer)this.rainbowBrightness.getValue() / 255.0F));
         tempHue += 0.0013071896F;
      }

      if ((Boolean)ClickGui.getInstance().colorSync.getValue()) {
         Phobos.colorManager.setColor(INSTANCE.getCurrentColor().getRed(), INSTANCE.getCurrentColor().getGreen(), INSTANCE.getCurrentColor().getBlue(), (Integer)ClickGui.getInstance().hoverAlpha.getValue());
      }

   }

   public int getCurrentColorHex() {
      return (Boolean)this.rainbow.getValue() ? Color.HSBtoRGB(this.hue, (float)(Integer)this.rainbowSaturation.getValue() / 255.0F, (float)(Integer)this.rainbowBrightness.getValue() / 255.0F) : ColorUtil.toARGB((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue());
   }

   public Color getCurrentColor() {
      return (Boolean)this.rainbow.getValue() ? Color.getHSBColor(this.hue, (float)(Integer)this.rainbowSaturation.getValue() / 255.0F, (float)(Integer)this.rainbowBrightness.getValue() / 255.0F) : new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue());
   }
}
