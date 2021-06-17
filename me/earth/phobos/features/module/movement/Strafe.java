package me.earth.phobos.features.modules.movement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.Freecam;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Strafe extends Module {
   private final Setting<Strafe.Mode> mode;
   private final Setting<Boolean> limiter;
   private final Setting<Boolean> bhop2;
   private final Setting<Boolean> limiter2;
   private final Setting<Boolean> noLag;
   private final Setting<Integer> specialMoveSpeed;
   private final Setting<Integer> potionSpeed;
   private final Setting<Integer> potionSpeed2;
   private final Setting<Integer> dFactor;
   private final Setting<Integer> acceleration;
   private final Setting<Float> speedLimit;
   private final Setting<Float> speedLimit2;
   private final Setting<Integer> yOffset;
   private final Setting<Boolean> potion;
   private final Setting<Boolean> wait;
   private final Setting<Boolean> hopWait;
   private final Setting<Integer> startStage;
   private final Setting<Boolean> setPos;
   private final Setting<Boolean> setNull;
   private final Setting<Integer> setGroundLimit;
   private final Setting<Integer> groundFactor;
   private final Setting<Integer> step;
   private final Setting<Boolean> setGroundNoLag;
   private int stage;
   private double moveSpeed;
   private double lastDist;
   private int cooldownHops;
   private boolean waitForGround;
   private Timer timer;
   private int hops;
   private static Strafe INSTANCE;

   public Strafe() {
      super("Strafe", "AirControl etc.", Module.Category.MOVEMENT, true, false, false);
      this.mode = this.register(new Setting("Mode", Strafe.Mode.NCP));
      this.limiter = this.register(new Setting("SetGround", true));
      this.bhop2 = this.register(new Setting("Hop", true));
      this.limiter2 = this.register(new Setting("Bhop", false));
      this.noLag = this.register(new Setting("NoLag", false));
      this.specialMoveSpeed = this.register(new Setting("Speed", 100, 0, 150));
      this.potionSpeed = this.register(new Setting("Speed1", 130, 0, 150));
      this.potionSpeed2 = this.register(new Setting("Speed2", 125, 0, 150));
      this.dFactor = this.register(new Setting("DFactor", 159, 100, 200));
      this.acceleration = this.register(new Setting("Accel", 2149, 1000, 2500));
      this.speedLimit = this.register(new Setting("SpeedLimit", 35.0F, 20.0F, 60.0F));
      this.speedLimit2 = this.register(new Setting("SpeedLimit2", 60.0F, 20.0F, 60.0F));
      this.yOffset = this.register(new Setting("YOffset", 400, 350, 500));
      this.potion = this.register(new Setting("Potion", false));
      this.wait = this.register(new Setting("Wait", true));
      this.hopWait = this.register(new Setting("HopWait", true));
      this.startStage = this.register(new Setting("Stage", 2, 0, 4));
      this.setPos = this.register(new Setting("SetPos", true));
      this.setNull = this.register(new Setting("SetNull", false));
      this.setGroundLimit = this.register(new Setting("GroundLimit", 138, 0, 1000));
      this.groundFactor = this.register(new Setting("GroundFactor", 13, 0, 50));
      this.step = this.register(new Setting("SetStep", 1, 0, 2, (v) -> {
         return this.mode.getValue() == Strafe.Mode.BHOP;
      }));
      this.setGroundNoLag = this.register(new Setting("NoGroundLag", true));
      this.stage = 1;
      this.cooldownHops = 0;
      this.waitForGround = false;
      this.timer = new Timer();
      this.hops = 0;
      INSTANCE = this;
   }

   public static Strafe getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Strafe();
      }

      return INSTANCE;
   }

   public void onEnable() {
      if (!mc.field_71439_g.field_70122_E) {
         this.waitForGround = true;
      }

      this.hops = 0;
      this.timer.reset();
      this.moveSpeed = getBaseMoveSpeed();
   }

   public void onDisable() {
      this.hops = 0;
      this.moveSpeed = 0.0D;
      this.stage = (Integer)this.startStage.getValue();
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0) {
         this.lastDist = Math.sqrt((mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) * (mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) + (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s) * (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s));
      }

   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if (event.getStage() == 0 && !this.shouldReturn()) {
         if (!mc.field_71439_g.field_70122_E) {
            if ((Boolean)this.wait.getValue() && this.waitForGround) {
               return;
            }
         } else {
            this.waitForGround = false;
         }

         if (this.mode.getValue() == Strafe.Mode.NCP) {
            this.doNCP(event);
         } else if (this.mode.getValue() == Strafe.Mode.BHOP) {
            float moveForward = mc.field_71439_g.field_71158_b.field_192832_b;
            float moveStrafe = mc.field_71439_g.field_71158_b.field_78902_a;
            float rotationYaw = mc.field_71439_g.field_70177_z;
            if ((Integer)this.step.getValue() == 1) {
               mc.field_71439_g.field_70138_W = 0.6F;
            }

            if ((Boolean)this.limiter2.getValue() && mc.field_71439_g.field_70122_E && Phobos.speedManager.getSpeedKpH() < (double)(Float)this.speedLimit2.getValue()) {
               this.stage = 2;
            }

            if ((Boolean)this.limiter.getValue() && round(mc.field_71439_g.field_70163_u - (double)((int)mc.field_71439_g.field_70163_u), 3) == round((double)(Integer)this.setGroundLimit.getValue() / 1000.0D, 3) && (!(Boolean)this.setGroundNoLag.getValue() || EntityUtil.isEntityMoving(mc.field_71439_g))) {
               if ((Boolean)this.setNull.getValue()) {
                  mc.field_71439_g.field_70181_x = 0.0D;
               } else {
                  EntityPlayerSP var10000 = mc.field_71439_g;
                  var10000.field_70181_x -= (double)(Integer)this.groundFactor.getValue() / 100.0D;
                  event.setY(event.getY() - (double)(Integer)this.groundFactor.getValue() / 100.0D);
                  if ((Boolean)this.setPos.getValue()) {
                     var10000 = mc.field_71439_g;
                     var10000.field_70163_u -= (double)(Integer)this.groundFactor.getValue() / 100.0D;
                  }
               }
            }

            double motionX;
            if (this.stage == 1 && EntityUtil.isMoving()) {
               this.stage = 2;
               this.moveSpeed = (double)this.getMultiplier() * getBaseMoveSpeed() - 0.01D;
            } else if (this.stage == 2 && EntityUtil.isMoving()) {
               this.stage = 3;
               mc.field_71439_g.field_70181_x = (double)(Integer)this.yOffset.getValue() / 1000.0D;
               event.setY((double)(Integer)this.yOffset.getValue() / 1000.0D);
               if (this.cooldownHops > 0) {
                  --this.cooldownHops;
               }

               ++this.hops;
               this.moveSpeed *= (double)(Integer)this.acceleration.getValue() / 1000.0D;
            } else if (this.stage == 3) {
               this.stage = 4;
               motionX = 0.66D * (this.lastDist - getBaseMoveSpeed());
               this.moveSpeed = this.lastDist - motionX;
            } else {
               if (mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, mc.field_71439_g.field_70181_x, 0.0D)).size() > 0 || mc.field_71439_g.field_70124_G && this.stage > 0) {
                  if ((Boolean)this.bhop2.getValue() && Phobos.speedManager.getSpeedKpH() >= (double)(Float)this.speedLimit.getValue()) {
                     this.stage = 0;
                  } else {
                     this.stage = mc.field_71439_g.field_191988_bg == 0.0F && mc.field_71439_g.field_70702_br == 0.0F ? 0 : 1;
                  }
               }

               this.moveSpeed = this.lastDist - this.lastDist / (double)(Integer)this.dFactor.getValue();
            }

            this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
            if ((Boolean)this.hopWait.getValue() && (Boolean)this.limiter2.getValue() && this.hops < 2) {
               this.moveSpeed = EntityUtil.getMaxSpeed();
            }

            if (moveForward == 0.0F && moveStrafe == 0.0F) {
               event.setX(0.0D);
               event.setZ(0.0D);
               this.moveSpeed = 0.0D;
            } else if (moveForward != 0.0F) {
               if (moveStrafe >= 1.0F) {
                  rotationYaw += moveForward > 0.0F ? -45.0F : 45.0F;
                  moveStrafe = 0.0F;
               } else if (moveStrafe <= -1.0F) {
                  rotationYaw += moveForward > 0.0F ? 45.0F : -45.0F;
                  moveStrafe = 0.0F;
               }

               if (moveForward > 0.0F) {
                  moveForward = 1.0F;
               } else if (moveForward < 0.0F) {
                  moveForward = -1.0F;
               }
            }

            motionX = Math.cos(Math.toRadians((double)(rotationYaw + 90.0F)));
            double motionZ = Math.sin(Math.toRadians((double)(rotationYaw + 90.0F)));
            if (this.cooldownHops == 0) {
               event.setX((double)moveForward * this.moveSpeed * motionX + (double)moveStrafe * this.moveSpeed * motionZ);
               event.setZ((double)moveForward * this.moveSpeed * motionZ - (double)moveStrafe * this.moveSpeed * motionX);
            }

            if ((Integer)this.step.getValue() == 2) {
               mc.field_71439_g.field_70138_W = 0.6F;
            }

            if (moveForward == 0.0F && moveStrafe == 0.0F) {
               this.timer.reset();
               event.setX(0.0D);
               event.setZ(0.0D);
            }
         }

      }
   }

   private void doNCP(MoveEvent event) {
      if (!(Boolean)this.limiter.getValue() && mc.field_71439_g.field_70122_E) {
         this.stage = 2;
      }

      double forward;
      switch(this.stage) {
      case 0:
         ++this.stage;
         this.lastDist = 0.0D;
         break;
      case 1:
      default:
         if (mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, mc.field_71439_g.field_70181_x, 0.0D)).size() > 0 || mc.field_71439_g.field_70124_G && this.stage > 0) {
            if ((Boolean)this.bhop2.getValue() && Phobos.speedManager.getSpeedKpH() >= (double)(Float)this.speedLimit.getValue()) {
               this.stage = 0;
            } else {
               this.stage = mc.field_71439_g.field_191988_bg == 0.0F && mc.field_71439_g.field_70702_br == 0.0F ? 0 : 1;
            }
         }

         this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
         break;
      case 2:
         forward = 0.40123128D;
         if ((mc.field_71439_g.field_191988_bg != 0.0F || mc.field_71439_g.field_70702_br != 0.0F) && mc.field_71439_g.field_70122_E) {
            if (mc.field_71439_g.func_70644_a(MobEffects.field_76430_j)) {
               forward += (double)((float)(mc.field_71439_g.func_70660_b(MobEffects.field_76430_j).func_76458_c() + 1) * 0.1F);
            }

            event.setY(mc.field_71439_g.field_70181_x = forward);
            this.moveSpeed *= 2.149D;
         }
         break;
      case 3:
         this.moveSpeed = this.lastDist - 0.76D * (this.lastDist - getBaseMoveSpeed());
      }

      this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
      forward = (double)mc.field_71439_g.field_71158_b.field_192832_b;
      double strafe = (double)mc.field_71439_g.field_71158_b.field_78902_a;
      double yaw = (double)mc.field_71439_g.field_70177_z;
      if (forward == 0.0D && strafe == 0.0D) {
         event.setX(0.0D);
         event.setZ(0.0D);
      } else if (forward != 0.0D && strafe != 0.0D) {
         forward *= Math.sin(0.7853981633974483D);
         strafe *= Math.cos(0.7853981633974483D);
      }

      event.setX((forward * this.moveSpeed * -Math.sin(Math.toRadians(yaw)) + strafe * this.moveSpeed * Math.cos(Math.toRadians(yaw))) * 0.99D);
      event.setZ((forward * this.moveSpeed * Math.cos(Math.toRadians(yaw)) - strafe * this.moveSpeed * -Math.sin(Math.toRadians(yaw))) * 0.99D);
      ++this.stage;
   }

   public static double getBaseMoveSpeed() {
      double baseSpeed = 0.272D;
      if (mc.field_71439_g.func_70644_a(MobEffects.field_76424_c)) {
         int amplifier = ((PotionEffect)Objects.requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76424_c))).func_76458_c();
         baseSpeed *= 1.0D + 0.2D * (double)amplifier;
      }

      return baseSpeed;
   }

   private float getMultiplier() {
      float baseSpeed = (float)(Integer)this.specialMoveSpeed.getValue();
      if ((Boolean)this.potion.getValue() && mc.field_71439_g.func_70644_a(MobEffects.field_76424_c)) {
         int amplifier = ((PotionEffect)Objects.requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76424_c))).func_76458_c() + 1;
         if (amplifier >= 2) {
            baseSpeed = (float)(Integer)this.potionSpeed2.getValue();
         } else {
            baseSpeed = (float)(Integer)this.potionSpeed.getValue();
         }
      }

      return baseSpeed / 100.0F;
   }

   private boolean shouldReturn() {
      return Phobos.moduleManager.isModuleEnabled(Freecam.class) || Phobos.moduleManager.isModuleEnabled(Phase.class) || Phobos.moduleManager.isModuleEnabled(ElytraFlight.class) || Phobos.moduleManager.isModuleEnabled(Flight.class);
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.getPacket() instanceof SPacketPlayerPosLook && (Boolean)this.noLag.getValue()) {
         if (this.mode.getValue() != Strafe.Mode.BHOP || !(Boolean)this.limiter2.getValue() && !(Boolean)this.bhop2.getValue()) {
            this.stage = 4;
         } else {
            this.stage = 1;
         }
      }

   }

   public String getDisplayInfo() {
      if (this.mode.getValue() != Strafe.Mode.NONE) {
         return this.mode.getValue() == Strafe.Mode.NCP ? this.mode.currentEnumName().toUpperCase() : this.mode.currentEnumName();
      } else {
         return null;
      }
   }

   public static double round(double value, int places) {
      if (places < 0) {
         throw new IllegalArgumentException();
      } else {
         BigDecimal bigDecimal = (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP);
         return bigDecimal.doubleValue();
      }
   }

   public static enum Mode {
      NONE,
      NCP,
      BHOP;
   }
}
