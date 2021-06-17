package me.earth.phobos.features.modules.movement;

import java.util.Random;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speed extends Module {
   public Setting<Speed.Mode> mode;
   public Setting<Boolean> strafeJump;
   public Setting<Boolean> noShake;
   public Setting<Boolean> useTimer;
   private static Speed INSTANCE = new Speed();
   private double highChainVal;
   private double lowChainVal;
   private boolean oneTime;
   public double startY;
   public boolean antiShake;
   private double bounceHeight;
   private float move;

   public Speed() {
      super("Speed", "Makes you faster", Module.Category.MOVEMENT, true, false, false);
      this.mode = this.register(new Setting("Mode", Speed.Mode.INSTANT));
      this.strafeJump = this.register(new Setting("Jump", false, (v) -> {
         return this.mode.getValue() == Speed.Mode.INSTANT;
      }));
      this.noShake = this.register(new Setting("NoShake", true, (v) -> {
         return this.mode.getValue() != Speed.Mode.INSTANT;
      }));
      this.useTimer = this.register(new Setting("UseTimer", false, (v) -> {
         return this.mode.getValue() != Speed.Mode.INSTANT;
      }));
      this.highChainVal = 0.0D;
      this.lowChainVal = 0.0D;
      this.oneTime = false;
      this.startY = 0.0D;
      this.antiShake = false;
      this.bounceHeight = 0.4D;
      this.move = 0.26F;
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Speed getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Speed();
      }

      return INSTANCE;
   }

   private boolean shouldReturn() {
      return Phobos.moduleManager.isModuleEnabled("Freecam") || Phobos.moduleManager.isModuleEnabled("Phase") || Phobos.moduleManager.isModuleEnabled("ElytraFlight") || Phobos.moduleManager.isModuleEnabled("Strafe") || Phobos.moduleManager.isModuleEnabled("Flight");
   }

   public void onUpdate() {
      if (!this.shouldReturn() && !mc.field_71439_g.func_70093_af() && !mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_180799_ab()) {
         switch((Speed.Mode)this.mode.getValue()) {
         case BOOST:
            this.doBoost();
            break;
         case ACCEL:
            this.doAccel();
            break;
         case ONGROUND:
            this.doOnground();
         }

      }
   }

   private void doBoost() {
      this.bounceHeight = 0.4D;
      this.move = 0.26F;
      if (mc.field_71439_g.field_70122_E) {
         this.startY = mc.field_71439_g.field_70163_u;
      }

      if (EntityUtil.getEntitySpeed(mc.field_71439_g) <= 1.0D) {
         this.lowChainVal = 1.0D;
         this.highChainVal = 1.0D;
      }

      if (EntityUtil.isEntityMoving(mc.field_71439_g) && !mc.field_71439_g.field_70123_F && !BlockUtil.isBlockAboveEntitySolid(mc.field_71439_g) && BlockUtil.isBlockBelowEntitySolid(mc.field_71439_g)) {
         this.oneTime = true;
         this.antiShake = (Boolean)this.noShake.getValue() && mc.field_71439_g.func_184187_bx() == null;
         Random random = new Random();
         boolean rnd = random.nextBoolean();
         if (mc.field_71439_g.field_70163_u >= this.startY + this.bounceHeight) {
            mc.field_71439_g.field_70181_x = -this.bounceHeight;
            ++this.lowChainVal;
            if (this.lowChainVal == 1.0D) {
               this.move = 0.075F;
            }

            if (this.lowChainVal == 2.0D) {
               this.move = 0.15F;
            }

            if (this.lowChainVal == 3.0D) {
               this.move = 0.175F;
            }

            if (this.lowChainVal == 4.0D) {
               this.move = 0.2F;
            }

            if (this.lowChainVal == 5.0D) {
               this.move = 0.225F;
            }

            if (this.lowChainVal == 6.0D) {
               this.move = 0.25F;
            }

            if (this.lowChainVal >= 7.0D) {
               this.move = 0.27895F;
            }

            if ((Boolean)this.useTimer.getValue()) {
               Phobos.timerManager.setTimer(1.0F);
            }
         }

         if (mc.field_71439_g.field_70163_u == this.startY) {
            mc.field_71439_g.field_70181_x = this.bounceHeight;
            ++this.highChainVal;
            if (this.highChainVal == 1.0D) {
               this.move = 0.075F;
            }

            if (this.highChainVal == 2.0D) {
               this.move = 0.175F;
            }

            if (this.highChainVal == 3.0D) {
               this.move = 0.325F;
            }

            if (this.highChainVal == 4.0D) {
               this.move = 0.375F;
            }

            if (this.highChainVal == 5.0D) {
               this.move = 0.4F;
            }

            if (this.highChainVal >= 6.0D) {
               this.move = 0.43395F;
            }

            if ((Boolean)this.useTimer.getValue()) {
               if (rnd) {
                  Phobos.timerManager.setTimer(1.3F);
               } else {
                  Phobos.timerManager.setTimer(1.0F);
               }
            }
         }

         EntityUtil.moveEntityStrafe((double)this.move, mc.field_71439_g);
      } else {
         if (this.oneTime) {
            mc.field_71439_g.field_70181_x = -0.1D;
            this.oneTime = false;
         }

         this.highChainVal = 0.0D;
         this.lowChainVal = 0.0D;
         this.antiShake = false;
         this.speedOff();
      }

   }

   private void doAccel() {
      this.bounceHeight = 0.4D;
      this.move = 0.26F;
      if (mc.field_71439_g.field_70122_E) {
         this.startY = mc.field_71439_g.field_70163_u;
      }

      if (EntityUtil.getEntitySpeed(mc.field_71439_g) <= 1.0D) {
         this.lowChainVal = 1.0D;
         this.highChainVal = 1.0D;
      }

      if (EntityUtil.isEntityMoving(mc.field_71439_g) && !mc.field_71439_g.field_70123_F && !BlockUtil.isBlockAboveEntitySolid(mc.field_71439_g) && BlockUtil.isBlockBelowEntitySolid(mc.field_71439_g)) {
         this.oneTime = true;
         this.antiShake = (Boolean)this.noShake.getValue() && mc.field_71439_g.func_184187_bx() == null;
         Random random = new Random();
         boolean rnd = random.nextBoolean();
         if (mc.field_71439_g.field_70163_u >= this.startY + this.bounceHeight) {
            mc.field_71439_g.field_70181_x = -this.bounceHeight;
            ++this.lowChainVal;
            if (this.lowChainVal == 1.0D) {
               this.move = 0.075F;
            }

            if (this.lowChainVal == 2.0D) {
               this.move = 0.175F;
            }

            if (this.lowChainVal == 3.0D) {
               this.move = 0.275F;
            }

            if (this.lowChainVal == 4.0D) {
               this.move = 0.35F;
            }

            if (this.lowChainVal == 5.0D) {
               this.move = 0.375F;
            }

            if (this.lowChainVal == 6.0D) {
               this.move = 0.4F;
            }

            if (this.lowChainVal == 7.0D) {
               this.move = 0.425F;
            }

            if (this.lowChainVal == 8.0D) {
               this.move = 0.45F;
            }

            if (this.lowChainVal == 9.0D) {
               this.move = 0.475F;
            }

            if (this.lowChainVal == 10.0D) {
               this.move = 0.5F;
            }

            if (this.lowChainVal == 11.0D) {
               this.move = 0.5F;
            }

            if (this.lowChainVal == 12.0D) {
               this.move = 0.525F;
            }

            if (this.lowChainVal == 13.0D) {
               this.move = 0.525F;
            }

            if (this.lowChainVal == 14.0D) {
               this.move = 0.535F;
            }

            if (this.lowChainVal == 15.0D) {
               this.move = 0.535F;
            }

            if (this.lowChainVal == 16.0D) {
               this.move = 0.545F;
            }

            if (this.lowChainVal >= 17.0D) {
               this.move = 0.545F;
            }

            if ((Boolean)this.useTimer.getValue()) {
               Phobos.timerManager.setTimer(1.0F);
            }
         }

         if (mc.field_71439_g.field_70163_u == this.startY) {
            mc.field_71439_g.field_70181_x = this.bounceHeight;
            ++this.highChainVal;
            if (this.highChainVal == 1.0D) {
               this.move = 0.075F;
            }

            if (this.highChainVal == 2.0D) {
               this.move = 0.175F;
            }

            if (this.highChainVal == 3.0D) {
               this.move = 0.375F;
            }

            if (this.highChainVal == 4.0D) {
               this.move = 0.6F;
            }

            if (this.highChainVal == 5.0D) {
               this.move = 0.775F;
            }

            if (this.highChainVal == 6.0D) {
               this.move = 0.825F;
            }

            if (this.highChainVal == 7.0D) {
               this.move = 0.875F;
            }

            if (this.highChainVal == 8.0D) {
               this.move = 0.925F;
            }

            if (this.highChainVal == 9.0D) {
               this.move = 0.975F;
            }

            if (this.highChainVal == 10.0D) {
               this.move = 1.05F;
            }

            if (this.highChainVal == 11.0D) {
               this.move = 1.1F;
            }

            if (this.highChainVal == 12.0D) {
               this.move = 1.1F;
            }

            if (this.highChainVal == 13.0D) {
               this.move = 1.15F;
            }

            if (this.highChainVal == 14.0D) {
               this.move = 1.15F;
            }

            if (this.highChainVal == 15.0D) {
               this.move = 1.175F;
            }

            if (this.highChainVal == 16.0D) {
               this.move = 1.175F;
            }

            if (this.highChainVal >= 17.0D) {
               this.move = 1.175F;
            }

            if ((Boolean)this.useTimer.getValue()) {
               if (rnd) {
                  Phobos.timerManager.setTimer(1.3F);
               } else {
                  Phobos.timerManager.setTimer(1.0F);
               }
            }
         }

         EntityUtil.moveEntityStrafe((double)this.move, mc.field_71439_g);
      } else {
         if (this.oneTime) {
            mc.field_71439_g.field_70181_x = -0.1D;
            this.oneTime = false;
         }

         this.antiShake = false;
         this.highChainVal = 0.0D;
         this.lowChainVal = 0.0D;
         this.speedOff();
      }

   }

   private void doOnground() {
      this.bounceHeight = 0.4D;
      this.move = 0.26F;
      if (mc.field_71439_g.field_70122_E) {
         this.startY = mc.field_71439_g.field_70163_u;
      }

      if (EntityUtil.getEntitySpeed(mc.field_71439_g) <= 1.0D) {
         this.lowChainVal = 1.0D;
         this.highChainVal = 1.0D;
      }

      if (EntityUtil.isEntityMoving(mc.field_71439_g) && !mc.field_71439_g.field_70123_F && !BlockUtil.isBlockAboveEntitySolid(mc.field_71439_g) && BlockUtil.isBlockBelowEntitySolid(mc.field_71439_g)) {
         this.oneTime = true;
         this.antiShake = (Boolean)this.noShake.getValue() && mc.field_71439_g.func_184187_bx() == null;
         Random random = new Random();
         boolean rnd = random.nextBoolean();
         if (mc.field_71439_g.field_70163_u >= this.startY + this.bounceHeight) {
            mc.field_71439_g.field_70181_x = -this.bounceHeight;
            ++this.lowChainVal;
            if (this.lowChainVal == 1.0D) {
               this.move = 0.075F;
            }

            if (this.lowChainVal == 2.0D) {
               this.move = 0.175F;
            }

            if (this.lowChainVal == 3.0D) {
               this.move = 0.275F;
            }

            if (this.lowChainVal == 4.0D) {
               this.move = 0.35F;
            }

            if (this.lowChainVal == 5.0D) {
               this.move = 0.375F;
            }

            if (this.lowChainVal == 6.0D) {
               this.move = 0.4F;
            }

            if (this.lowChainVal == 7.0D) {
               this.move = 0.425F;
            }

            if (this.lowChainVal == 8.0D) {
               this.move = 0.45F;
            }

            if (this.lowChainVal == 9.0D) {
               this.move = 0.475F;
            }

            if (this.lowChainVal == 10.0D) {
               this.move = 0.5F;
            }

            if (this.lowChainVal == 11.0D) {
               this.move = 0.5F;
            }

            if (this.lowChainVal == 12.0D) {
               this.move = 0.525F;
            }

            if (this.lowChainVal == 13.0D) {
               this.move = 0.525F;
            }

            if (this.lowChainVal == 14.0D) {
               this.move = 0.535F;
            }

            if (this.lowChainVal == 15.0D) {
               this.move = 0.535F;
            }

            if (this.lowChainVal == 16.0D) {
               this.move = 0.545F;
            }

            if (this.lowChainVal >= 17.0D) {
               this.move = 0.545F;
            }

            if ((Boolean)this.useTimer.getValue()) {
               Phobos.timerManager.setTimer(1.0F);
            }
         }

         if (mc.field_71439_g.field_70163_u == this.startY) {
            mc.field_71439_g.field_70181_x = this.bounceHeight;
            ++this.highChainVal;
            if (this.highChainVal == 1.0D) {
               this.move = 0.075F;
            }

            if (this.highChainVal == 2.0D) {
               this.move = 0.175F;
            }

            if (this.highChainVal == 3.0D) {
               this.move = 0.375F;
            }

            if (this.highChainVal == 4.0D) {
               this.move = 0.6F;
            }

            if (this.highChainVal == 5.0D) {
               this.move = 0.775F;
            }

            if (this.highChainVal == 6.0D) {
               this.move = 0.825F;
            }

            if (this.highChainVal == 7.0D) {
               this.move = 0.875F;
            }

            if (this.highChainVal == 8.0D) {
               this.move = 0.925F;
            }

            if (this.highChainVal == 9.0D) {
               this.move = 0.975F;
            }

            if (this.highChainVal == 10.0D) {
               this.move = 1.05F;
            }

            if (this.highChainVal == 11.0D) {
               this.move = 1.1F;
            }

            if (this.highChainVal == 12.0D) {
               this.move = 1.1F;
            }

            if (this.highChainVal == 13.0D) {
               this.move = 1.15F;
            }

            if (this.highChainVal == 14.0D) {
               this.move = 1.15F;
            }

            if (this.highChainVal == 15.0D) {
               this.move = 1.175F;
            }

            if (this.highChainVal == 16.0D) {
               this.move = 1.175F;
            }

            if (this.highChainVal >= 17.0D) {
               this.move = 1.2F;
            }

            if ((Boolean)this.useTimer.getValue()) {
               if (rnd) {
                  Phobos.timerManager.setTimer(1.3F);
               } else {
                  Phobos.timerManager.setTimer(1.0F);
               }
            }
         }

         EntityUtil.moveEntityStrafe((double)this.move, mc.field_71439_g);
      } else {
         if (this.oneTime) {
            mc.field_71439_g.field_70181_x = -0.1D;
            this.oneTime = false;
         }

         this.antiShake = false;
         this.highChainVal = 0.0D;
         this.lowChainVal = 0.0D;
         this.speedOff();
      }

   }

   public void onDisable() {
      if (this.mode.getValue() == Speed.Mode.ONGROUND || this.mode.getValue() == Speed.Mode.BOOST) {
         mc.field_71439_g.field_70181_x = -0.1D;
      }

      Phobos.timerManager.setTimer(1.0F);
      this.highChainVal = 0.0D;
      this.lowChainVal = 0.0D;
      this.antiShake = false;
   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (event.getStage() == 2 && event.getSetting().equals(this.mode) && this.mode.getPlannedValue() == Speed.Mode.INSTANT) {
         mc.field_71439_g.field_70181_x = -0.1D;
      }

   }

   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   @SubscribeEvent
   public void onMode(MoveEvent event) {
      if (!this.shouldReturn() && event.getStage() == 0 && this.mode.getValue() == Speed.Mode.INSTANT && !nullCheck() && !mc.field_71439_g.func_70093_af() && !mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_180799_ab() && (mc.field_71439_g.field_71158_b.field_192832_b != 0.0F || mc.field_71439_g.field_71158_b.field_78902_a != 0.0F)) {
         if (mc.field_71439_g.field_70122_E && (Boolean)this.strafeJump.getValue()) {
            mc.field_71439_g.field_70181_x = 0.4D;
            event.setY(0.4D);
         }

         MovementInput movementInput = mc.field_71439_g.field_71158_b;
         float moveForward = movementInput.field_192832_b;
         float moveStrafe = movementInput.field_78902_a;
         float rotationYaw = mc.field_71439_g.field_70177_z;
         if ((double)moveForward == 0.0D && (double)moveStrafe == 0.0D) {
            event.setX(0.0D);
            event.setZ(0.0D);
         } else {
            if ((double)moveForward != 0.0D) {
               if ((double)moveStrafe > 0.0D) {
                  rotationYaw += (float)((double)moveForward > 0.0D ? -45 : 45);
               } else if ((double)moveStrafe < 0.0D) {
                  rotationYaw += (float)((double)moveForward > 0.0D ? 45 : -45);
               }

               moveStrafe = 0.0F;
               moveForward = moveForward == 0.0F ? moveForward : ((double)moveForward > 0.0D ? 1.0F : -1.0F);
            }

            moveStrafe = moveStrafe == 0.0F ? moveStrafe : ((double)moveStrafe > 0.0D ? 1.0F : -1.0F);
            event.setX((double)moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians((double)(rotationYaw + 90.0F))) + (double)moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians((double)(rotationYaw + 90.0F))));
            event.setZ((double)moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians((double)(rotationYaw + 90.0F))) - (double)moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians((double)(rotationYaw + 90.0F))));
         }
      }

   }

   private void speedOff() {
      float yaw = (float)Math.toRadians((double)mc.field_71439_g.field_70177_z);
      EntityPlayerSP var10000;
      if (BlockUtil.isBlockAboveEntitySolid(mc.field_71439_g)) {
         if (mc.field_71474_y.field_74351_w.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d() && mc.field_71439_g.field_70122_E) {
            var10000 = mc.field_71439_g;
            var10000.field_70159_w -= (double)MathUtil.sin(yaw) * 0.15D;
            var10000 = mc.field_71439_g;
            var10000.field_70179_y += (double)MathUtil.cos(yaw) * 0.15D;
         }
      } else if (mc.field_71439_g.field_70123_F) {
         if (mc.field_71474_y.field_74351_w.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d() && mc.field_71439_g.field_70122_E) {
            var10000 = mc.field_71439_g;
            var10000.field_70159_w -= (double)MathUtil.sin(yaw) * 0.03D;
            var10000 = mc.field_71439_g;
            var10000.field_70179_y += (double)MathUtil.cos(yaw) * 0.03D;
         }
      } else if (!BlockUtil.isBlockBelowEntitySolid(mc.field_71439_g)) {
         if (mc.field_71474_y.field_74351_w.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d() && mc.field_71439_g.field_70122_E) {
            var10000 = mc.field_71439_g;
            var10000.field_70159_w -= (double)MathUtil.sin(yaw) * 0.03D;
            var10000 = mc.field_71439_g;
            var10000.field_70179_y += (double)MathUtil.cos(yaw) * 0.03D;
         }
      } else {
         mc.field_71439_g.field_70159_w = 0.0D;
         mc.field_71439_g.field_70179_y = 0.0D;
      }

   }

   public static enum Mode {
      INSTANT,
      ONGROUND,
      ACCEL,
      BOOST;
   }
}
