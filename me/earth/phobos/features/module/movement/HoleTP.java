package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleTP extends Module {
   private static HoleTP INSTANCE = new HoleTP();
   private final double[] oneblockPositions = new double[]{0.42D, 0.75D};
   private int packets;
   private boolean jumped = false;

   public HoleTP() {
      super("HoleTP", "Teleports you in a hole.", Module.Category.MOVEMENT, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static HoleTP getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new HoleTP();
      }

      return INSTANCE;
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 1 && (Speed.getInstance().isOff() || Speed.getInstance().mode.getValue() == Speed.Mode.INSTANT) && Strafe.getInstance().isOff() && LagBlock.getInstance().isOff()) {
         if (!mc.field_71439_g.field_70122_E) {
            if (mc.field_71474_y.field_74314_A.func_151470_d()) {
               this.jumped = true;
            }
         } else {
            this.jumped = false;
         }

         if (!this.jumped && (double)mc.field_71439_g.field_70143_R < 0.5D && BlockUtil.isInHole() && mc.field_71439_g.field_70163_u - BlockUtil.getNearestBlockBelow() <= 1.125D && mc.field_71439_g.field_70163_u - BlockUtil.getNearestBlockBelow() <= 0.95D && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid()) {
            if (!mc.field_71439_g.field_70122_E) {
               ++this.packets;
            }

            if (!mc.field_71439_g.field_70122_E && !mc.field_71439_g.func_70055_a(Material.field_151586_h) && !mc.field_71439_g.func_70055_a(Material.field_151587_i) && !mc.field_71474_y.field_74314_A.func_151470_d() && !mc.field_71439_g.func_70617_f_() && this.packets > 0) {
               BlockPos blockPos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
               double[] var3 = this.oneblockPositions;
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  double position = var3[var5];
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position((double)((float)blockPos.func_177958_n() + 0.5F), mc.field_71439_g.field_70163_u - position, (double)((float)blockPos.func_177952_p() + 0.5F), true));
               }

               mc.field_71439_g.func_70107_b((double)((float)blockPos.func_177958_n() + 0.5F), BlockUtil.getNearestBlockBelow() + 0.1D, (double)((float)blockPos.func_177952_p() + 0.5F));
               this.packets = 0;
            }
         }
      }

   }
}
