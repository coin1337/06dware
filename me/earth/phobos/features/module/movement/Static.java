package me.earth.phobos.features.modules.movement;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;

public class Static extends Module {
   private final Setting<Static.Mode> mode;
   private final Setting<Boolean> disabler;
   private final Setting<Boolean> ySpeed;
   private final Setting<Float> speed;
   private final Setting<Float> height;

   public Static() {
      super("Static", "Stops any movement. Glitches you up.", Module.Category.MOVEMENT, false, false, false);
      this.mode = this.register(new Setting("Mode", Static.Mode.ROOF));
      this.disabler = this.register(new Setting("Disable", true, (v) -> {
         return this.mode.getValue() == Static.Mode.ROOF;
      }));
      this.ySpeed = this.register(new Setting("YSpeed", false, (v) -> {
         return this.mode.getValue() == Static.Mode.STATIC;
      }));
      this.speed = this.register(new Setting("Speed", 0.1F, 0.0F, 10.0F, (v) -> {
         return (Boolean)this.ySpeed.getValue() && this.mode.getValue() == Static.Mode.STATIC;
      }));
      this.height = this.register(new Setting("Height", 3.0F, 0.0F, 256.0F, (v) -> {
         return this.mode.getValue() == Static.Mode.NOVOID;
      }));
   }

   public void onUpdate() {
      if (!fullNullCheck()) {
         switch((Static.Mode)this.mode.getValue()) {
         case STATIC:
            mc.field_71439_g.field_71075_bZ.field_75100_b = false;
            mc.field_71439_g.field_70159_w = 0.0D;
            mc.field_71439_g.field_70181_x = 0.0D;
            mc.field_71439_g.field_70179_y = 0.0D;
            if ((Boolean)this.ySpeed.getValue()) {
               mc.field_71439_g.field_70747_aH = (Float)this.speed.getValue();
               EntityPlayerSP var10000;
               if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x += (double)(Float)this.speed.getValue();
               }

               if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x -= (double)(Float)this.speed.getValue();
               }
            }
            break;
         case ROOF:
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 10000.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E));
            if ((Boolean)this.disabler.getValue()) {
               this.disable();
            }
            break;
         case NOVOID:
            if (!mc.field_71439_g.field_70145_X && mc.field_71439_g.field_70163_u <= (double)(Float)this.height.getValue()) {
               RayTraceResult trace = mc.field_71441_e.func_147447_a(mc.field_71439_g.func_174791_d(), new Vec3d(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v), false, false, false);
               if (trace != null && trace.field_72313_a == Type.BLOCK) {
                  return;
               }

               if (Phobos.moduleManager.isModuleEnabled(Phase.class) || Phobos.moduleManager.isModuleEnabled(Flight.class)) {
                  return;
               }

               mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
               if (mc.field_71439_g.func_184187_bx() != null) {
                  mc.field_71439_g.func_184187_bx().func_70016_h(0.0D, 0.0D, 0.0D);
               }
            }
         }

      }
   }

   public String getDisplayInfo() {
      if (this.mode.getValue() == Static.Mode.ROOF) {
         return "Roof";
      } else {
         return this.mode.getValue() == Static.Mode.NOVOID ? "NoVoid" : null;
      }
   }

   public static enum Mode {
      STATIC,
      ROOF,
      NOVOID;
   }
}
