package me.earth.phobos.features.modules.render;

import java.awt.Color;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;

public class BlockHighlight extends Module {
   public Setting<Boolean> colorSync = this.register(new Setting("Sync", false));
   public Setting<Boolean> rolling = this.register(new Setting("Rolling", false, (v) -> {
      return (Boolean)this.colorSync.getValue();
   }));
   public Setting<Boolean> box = this.register(new Setting("Box", false));
   public Setting<Boolean> outline = this.register(new Setting("Outline", true));
   private final Setting<Integer> red = this.register(new Setting("Red", 0, 0, 255));
   private final Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255));
   private final Setting<Integer> blue = this.register(new Setting("Blue", 0, 0, 255));
   private final Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255));
   private final Setting<Integer> boxAlpha = this.register(new Setting("BoxAlpha", 125, 0, 255, (v) -> {
      return (Boolean)this.box.getValue();
   }));
   private final Setting<Float> lineWidth = this.register(new Setting("LineWidth", 1.0F, 0.1F, 5.0F, (v) -> {
      return (Boolean)this.outline.getValue();
   }));
   public Setting<Boolean> customOutline = this.register(new Setting("CustomLine", false, (v) -> {
      return (Boolean)this.outline.getValue();
   }));
   private final Setting<Integer> cRed = this.register(new Setting("OL-Red", 255, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
   }));
   private final Setting<Integer> cGreen = this.register(new Setting("OL-Green", 255, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
   }));
   private final Setting<Integer> cBlue = this.register(new Setting("OL-Blue", 255, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
   }));
   private final Setting<Integer> cAlpha = this.register(new Setting("OL-Alpha", 255, 0, 255, (v) -> {
      return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
   }));

   public BlockHighlight() {
      super("BlockHighlight", "Highlights the block u look at.", Module.Category.RENDER, false, false, false);
   }

   public void onRender3D(Render3DEvent event) {
      RayTraceResult ray = mc.field_71476_x;
      if (ray != null && ray.field_72313_a == Type.BLOCK) {
         BlockPos blockpos = ray.func_178782_a();
         if ((Boolean)this.rolling.getValue()) {
            RenderUtil.drawProperGradientBlockOutline(blockpos, new Color((Integer)HUD.getInstance().colorMap.get(0)), new Color((Integer)HUD.getInstance().colorMap.get(this.renderer.scaledHeight / 4)), new Color((Integer)HUD.getInstance().colorMap.get(this.renderer.scaledHeight / 2)), 1.0F);
         } else {
            RenderUtil.drawBoxESP(blockpos, (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), (Boolean)this.customOutline.getValue(), new Color((Integer)this.cRed.getValue(), (Integer)this.cGreen.getValue(), (Integer)this.cBlue.getValue(), (Integer)this.cAlpha.getValue()), (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), false);
         }
      }

   }
}
