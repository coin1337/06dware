package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.RotationUtil;
import net.minecraft.util.math.BlockPos;

public class HoleESP extends Module {
   private Setting<Integer> holes = this.register(new Setting("Holes", 3, 1, 500));
   public Setting<Boolean> box = this.register(new Setting("Box", true));
   public Setting<Boolean> outline = this.register(new Setting("Outline", true));
   public Setting<Double> height = this.register(new Setting("Height", 0.0D, -2.0D, 2.0D));
   private Setting<Integer> red = this.register(new Setting("Red", 0, 0, 255));
   private Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255));
   private Setting<Integer> blue = this.register(new Setting("Blue", 0, 0, 255));
   private Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255));
   private Setting<Integer> boxAlpha = this.register(new Setting("BoxAlpha", 125, 0, 255, (v) -> {
      return (Boolean)this.box.getValue();
   }));
   private Setting<Float> lineWidth = this.register(new Setting("LineWidth", 1.0F, 0.1F, 5.0F, (v) -> {
      return (Boolean)this.outline.getValue();
   }));
   public Setting<Boolean> safeColor = this.register(new Setting("SafeColor", false));
   private Setting<Integer> safeRed = this.register(new Setting("SafeRed", 0, 0, 255, (v) -> {
      return (Boolean)this.safeColor.getValue();
   }));
   private Setting<Integer> safeGreen = this.register(new Setting("SafeGreen", 255, 0, 255, (v) -> {
      return (Boolean)this.safeColor.getValue();
   }));
   private Setting<Integer> safeBlue = this.register(new Setting("SafeBlue", 0, 0, 255, (v) -> {
      return (Boolean)this.safeColor.getValue();
   }));
   private Setting<Integer> safeAlpha = this.register(new Setting("SafeAlpha", 255, 0, 255, (v) -> {
      return (Boolean)this.safeColor.getValue();
   }));
   public Setting<Boolean> customOutline = this.register(new Setting("CustomLine", false, (v) -> {
      return (Boolean)this.outline.getValue();
   }));
   private Setting<Integer> cRed = this.register(new Setting("OL-Red", 0, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
   }));
   private Setting<Integer> cGreen = this.register(new Setting("OL-Green", 0, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
   }));
   private Setting<Integer> cBlue = this.register(new Setting("OL-Blue", 255, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
   }));
   private Setting<Integer> cAlpha = this.register(new Setting("OL-Alpha", 255, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
   }));
   private Setting<Integer> safecRed = this.register(new Setting("OL-SafeRed", 0, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue() && (Boolean)this.safeColor.getValue();
   }));
   private Setting<Integer> safecGreen = this.register(new Setting("OL-SafeGreen", 255, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue() && (Boolean)this.safeColor.getValue();
   }));
   private Setting<Integer> safecBlue = this.register(new Setting("OL-SafeBlue", 0, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue() && (Boolean)this.safeColor.getValue();
   }));
   private Setting<Integer> safecAlpha = this.register(new Setting("OL-SafeAlpha", 255, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue() && (Boolean)this.safeColor.getValue();
   }));
   private static HoleESP INSTANCE = new HoleESP();

   public HoleESP() {
      super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static HoleESP getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new HoleESP();
      }

      return INSTANCE;
   }

   public void onRender3D(Render3DEvent event) {
      int drawnHoles = 0;
      Iterator var3 = Phobos.holeManager.getSortedHoles().iterator();

      while(var3.hasNext()) {
         BlockPos pos = (BlockPos)var3.next();
         if (drawnHoles >= (Integer)this.holes.getValue()) {
            break;
         }

         if (!pos.equals(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v)) && RotationUtil.isInFov(pos)) {
            if ((Boolean)this.safeColor.getValue() && Phobos.holeManager.isSafe(pos)) {
               RenderUtil.drawBoxESP(pos, new Color((Integer)this.safeRed.getValue(), (Integer)this.safeGreen.getValue(), (Integer)this.safeBlue.getValue(), (Integer)this.safeAlpha.getValue()), (Boolean)this.customOutline.getValue(), new Color((Integer)this.safecRed.getValue(), (Integer)this.safecGreen.getValue(), (Integer)this.safecBlue.getValue(), (Integer)this.safecAlpha.getValue()), (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), true, (Double)this.height.getValue());
            } else {
               RenderUtil.drawBoxESP(pos, new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), (Boolean)this.customOutline.getValue(), new Color((Integer)this.cRed.getValue(), (Integer)this.cGreen.getValue(), (Integer)this.cBlue.getValue(), (Integer)this.cAlpha.getValue()), (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), true, (Double)this.height.getValue());
            }

            ++drawnHoles;
         }
      }

   }
}
