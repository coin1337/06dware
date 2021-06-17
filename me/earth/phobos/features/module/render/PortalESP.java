package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class PortalESP extends Module {
   private int cooldownTicks;
   private final ArrayList<BlockPos> blockPosArrayList = new ArrayList();
   private final Setting<Integer> distance = this.register(new Setting("Distance", 60, 10, 100));
   private final Setting<Boolean> box = this.register(new Setting("Box", false));
   private final Setting<Integer> boxAlpha = this.register(new Setting("BoxAlpha", 125, 0, 255, (v) -> {
      return (Boolean)this.box.getValue();
   }));
   private final Setting<Boolean> outline = this.register(new Setting("Outline", true));
   private final Setting<Float> lineWidth = this.register(new Setting("LineWidth", 1.0F, 0.1F, 5.0F, (v) -> {
      return (Boolean)this.outline.getValue();
   }));

   public PortalESP() {
      super("PortalESP", "Draws portals", Module.Category.RENDER, true, false, false);
   }

   @SubscribeEvent
   public void onTickEvent(ClientTickEvent event) {
      if (mc.field_71441_e != null) {
         if (this.cooldownTicks < 1) {
            this.blockPosArrayList.clear();
            this.compileDL();
            this.cooldownTicks = 80;
         }

         --this.cooldownTicks;
      }
   }

   public void onRender3D(Render3DEvent event) {
      if (mc.field_71441_e != null) {
         Iterator var2 = this.blockPosArrayList.iterator();

         while(var2.hasNext()) {
            BlockPos pos = (BlockPos)var2.next();
            RenderUtil.drawBoxESP(pos, new Color(204, 0, 153, 255), false, new Color(204, 0, 153, 255), (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), false);
         }

      }
   }

   private void compileDL() {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         for(int x = (int)mc.field_71439_g.field_70165_t - (Integer)this.distance.getValue(); x <= (int)mc.field_71439_g.field_70165_t + (Integer)this.distance.getValue(); ++x) {
            for(int y = (int)mc.field_71439_g.field_70163_u - (Integer)this.distance.getValue(); y <= (int)mc.field_71439_g.field_70163_u + (Integer)this.distance.getValue(); ++y) {
               for(int z = (int)Math.max(mc.field_71439_g.field_70161_v - (double)(Integer)this.distance.getValue(), 0.0D); (double)z <= Math.min(mc.field_71439_g.field_70161_v + (double)(Integer)this.distance.getValue(), 255.0D); ++z) {
                  BlockPos pos = new BlockPos(x, y, z);
                  Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
                  if (block instanceof BlockPortal) {
                     this.blockPosArrayList.add(pos);
                  }
               }
            }
         }

      }
   }
}
