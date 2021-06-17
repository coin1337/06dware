package me.earth.phobos.features.modules.movement;

import java.util.Objects;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TPSpeed extends Module {
   private Setting<TPSpeed.Mode> mode;
   private Setting<Double> speed;
   private Setting<Double> fallSpeed;
   private Setting<Boolean> turnOff;
   private Setting<Integer> tpLimit;
   private int tps;
   private double[] selectedPositions;

   public TPSpeed() {
      super("TpSpeed", "Teleports you.", Module.Category.MOVEMENT, true, false, false);
      this.mode = this.register(new Setting("Mode", TPSpeed.Mode.NORMAL));
      this.speed = this.register(new Setting("Speed", 0.25D, 0.1D, 10.0D));
      this.fallSpeed = this.register(new Setting("FallSpeed", 0.25D, 0.1D, 10.0D, (v) -> {
         return this.mode.getValue() == TPSpeed.Mode.STEP;
      }));
      this.turnOff = this.register(new Setting("Off", false));
      this.tpLimit = this.register(new Setting("Limit", 2, 1, 10, (v) -> {
         return (Boolean)this.turnOff.getValue();
      }, "Turn it off."));
      this.tps = 0;
      this.selectedPositions = new double[]{0.42D, 0.75D, 1.0D};
   }

   public void onEnable() {
      this.tps = 0;
   }

   @SubscribeEvent
   public void onUpdatePlayerWalking(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0) {
         double pawnY;
         double[] lastStep;
         if (this.mode.getValue() == TPSpeed.Mode.NORMAL) {
            if ((Boolean)this.turnOff.getValue() && this.tps >= (Integer)this.tpLimit.getValue()) {
               this.disable();
               return;
            }

            if (mc.field_71439_g.field_191988_bg != 0.0F || mc.field_71439_g.field_70702_br != 0.0F && mc.field_71439_g.field_70122_E) {
               for(pawnY = 0.0625D; pawnY < (Double)this.speed.getValue(); pawnY += 0.262D) {
                  lastStep = MathUtil.directionSpeed(pawnY);
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + lastStep[0], mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v + lastStep[1], mc.field_71439_g.field_70122_E));
               }

               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + mc.field_71439_g.field_70159_w, 0.0D, mc.field_71439_g.field_70161_v + mc.field_71439_g.field_70179_y, mc.field_71439_g.field_70122_E));
               ++this.tps;
            }
         } else if ((mc.field_71439_g.field_191988_bg != 0.0F || mc.field_71439_g.field_70702_br != 0.0F) && mc.field_71439_g.field_70122_E) {
            pawnY = 0.0D;
            lastStep = MathUtil.directionSpeed(0.262D);

            for(double x = 0.0625D; x < (Double)this.speed.getValue(); x += 0.262D) {
               double[] dir = MathUtil.directionSpeed(x);

               AxisAlignedBB bb;
               for(bb = ((AxisAlignedBB)Objects.requireNonNull(mc.field_71439_g.func_174813_aQ())).func_72317_d(dir[0], pawnY, dir[1]); collidesHorizontally(bb); bb = ((AxisAlignedBB)Objects.requireNonNull(mc.field_71439_g.func_174813_aQ())).func_72317_d(dir[0], pawnY, dir[1])) {
                  double[] var9 = this.selectedPositions;
                  int var10 = var9.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     double position = var9[var11];
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + dir[0] - lastStep[0], mc.field_71439_g.field_70163_u + pawnY + position, mc.field_71439_g.field_70161_v + dir[1] - lastStep[1], true));
                  }

                  ++pawnY;
               }

               if (!mc.field_71441_e.func_72829_c(bb.func_72314_b(0.0125D, 0.0D, 0.0125D).func_72317_d(0.0D, -1.0D, 0.0D))) {
                  for(double i = 0.0D; i <= 1.0D; i += (Double)this.fallSpeed.getValue()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + dir[0], mc.field_71439_g.field_70163_u + pawnY - i, mc.field_71439_g.field_70161_v + dir[1], true));
                  }

                  --pawnY;
               }

               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + dir[0], mc.field_71439_g.field_70163_u + pawnY, mc.field_71439_g.field_70161_v + dir[1], mc.field_71439_g.field_70122_E));
            }

            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t + mc.field_71439_g.field_70159_w, 0.0D, mc.field_71439_g.field_70161_v + mc.field_71439_g.field_70179_y, mc.field_71439_g.field_70122_E));
         }

      }
   }

   private static boolean collidesHorizontally(AxisAlignedBB bb) {
      if (!mc.field_71441_e.func_184143_b(bb)) {
         return false;
      } else {
         Vec3d center = bb.func_189972_c();
         BlockPos blockpos = new BlockPos(center.field_72450_a, bb.field_72338_b, center.field_72449_c);
         return mc.field_71441_e.func_175665_u(blockpos.func_177976_e()) || mc.field_71441_e.func_175665_u(blockpos.func_177974_f()) || mc.field_71441_e.func_175665_u(blockpos.func_177978_c()) || mc.field_71441_e.func_175665_u(blockpos.func_177968_d()) || mc.field_71441_e.func_175665_u(blockpos);
      }
   }

   public static enum Mode {
      NORMAL,
      STEP;
   }
}
