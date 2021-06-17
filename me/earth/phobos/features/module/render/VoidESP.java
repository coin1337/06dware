package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.RotationUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class VoidESP extends Module {
   private final Setting<Float> radius = this.register(new Setting("Radius", 8.0F, 0.0F, 50.0F));
   public Setting<Boolean> air = this.register(new Setting("OnlyAir", true));
   public Setting<Boolean> noEnd = this.register(new Setting("NoEnd", true));
   private Setting<Integer> updates = this.register(new Setting("Updates", 500, 0, 1000));
   private Setting<Integer> voidCap = this.register(new Setting("VoidCap", 500, 0, 1000));
   public Setting<Boolean> box = this.register(new Setting("Box", true));
   public Setting<Boolean> outline = this.register(new Setting("Outline", true));
   public Setting<Boolean> colorSync = this.register(new Setting("Sync", false));
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
   private final Timer timer = new Timer();
   private List<BlockPos> voidHoles = new CopyOnWriteArrayList();

   public VoidESP() {
      super("VoidEsp", "Esps the void", Module.Category.RENDER, true, false, false);
   }

   public void onToggle() {
      this.timer.reset();
   }

   public void onLogin() {
      this.timer.reset();
   }

   public void onTick() {
      if (!fullNullCheck() && (!(Boolean)this.noEnd.getValue() || mc.field_71439_g.field_71093_bK != 1) && this.timer.passedMs((long)(Integer)this.updates.getValue())) {
         this.voidHoles.clear();
         this.voidHoles = this.findVoidHoles();
         if (this.voidHoles.size() > (Integer)this.voidCap.getValue()) {
            this.voidHoles.clear();
         }

         this.timer.reset();
      }

   }

   public void onRender3D(Render3DEvent event) {
      if (!fullNullCheck() && (!(Boolean)this.noEnd.getValue() || mc.field_71439_g.field_71093_bK != 1)) {
         Iterator var2 = this.voidHoles.iterator();

         while(var2.hasNext()) {
            BlockPos pos = (BlockPos)var2.next();
            if (RotationUtil.isInFov(pos)) {
               RenderUtil.drawBoxESP(pos, new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), (Boolean)this.customOutline.getValue(), new Color((Integer)this.cRed.getValue(), (Integer)this.cGreen.getValue(), (Integer)this.cBlue.getValue(), (Integer)this.cAlpha.getValue()), (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), true, (Double)this.height.getValue());
            }
         }

      }
   }

   private List<BlockPos> findVoidHoles() {
      BlockPos playerPos = EntityUtil.getPlayerPos(mc.field_71439_g);
      return (List)BlockUtil.getDisc(playerPos.func_177982_a(0, -playerPos.func_177956_o(), 0), (Float)this.radius.getValue()).stream().filter(this::isVoid).collect(Collectors.toList());
   }

   private boolean isVoid(BlockPos pos) {
      return (mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150350_a || !(Boolean)this.air.getValue() && mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150357_h) && pos.func_177956_o() < 1 && pos.func_177956_o() >= 0;
   }
}
