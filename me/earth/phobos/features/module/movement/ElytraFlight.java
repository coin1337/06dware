package me.earth.phobos.features.modules.movement;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ElytraFlight extends Module {
   public Setting<ElytraFlight.Mode> mode;
   public Setting<Integer> devMode;
   public Setting<Float> speed;
   public Setting<Float> vSpeed;
   public Setting<Float> hSpeed;
   public Setting<Float> glide;
   public Setting<Float> tooBeeSpeed;
   public Setting<Boolean> autoStart;
   public Setting<Boolean> disableInLiquid;
   public Setting<Boolean> infiniteDura;
   public Setting<Boolean> noKick;
   public Setting<Boolean> allowUp;
   public Setting<Boolean> lockPitch;
   private static ElytraFlight INSTANCE = new ElytraFlight();
   private final Timer timer;
   private final Timer bypassTimer;
   private boolean vertical;
   private Double posX;
   private Double flyHeight;
   private Double posZ;

   public ElytraFlight() {
      super("ElytraFlight", "Makes Elytra Flight better.", Module.Category.MOVEMENT, true, false, false);
      this.mode = this.register(new Setting("Mode", ElytraFlight.Mode.FLY));
      this.devMode = this.register(new Setting("Type", 2, 1, 3, (v) -> {
         return this.mode.getValue() == ElytraFlight.Mode.BYPASS || this.mode.getValue() == ElytraFlight.Mode.BETTER;
      }, "EventMode"));
      this.speed = this.register(new Setting("Speed", 1.0F, 0.0F, 10.0F, (v) -> {
         return this.mode.getValue() != ElytraFlight.Mode.FLY && this.mode.getValue() != ElytraFlight.Mode.BOOST && this.mode.getValue() != ElytraFlight.Mode.BETTER && this.mode.getValue() != ElytraFlight.Mode.OHARE;
      }, "The Speed."));
      this.vSpeed = this.register(new Setting("VSpeed", 0.3F, 0.0F, 10.0F, (v) -> {
         return this.mode.getValue() == ElytraFlight.Mode.BETTER || this.mode.getValue() == ElytraFlight.Mode.OHARE;
      }, "Vertical Speed"));
      this.hSpeed = this.register(new Setting("HSpeed", 1.0F, 0.0F, 10.0F, (v) -> {
         return this.mode.getValue() == ElytraFlight.Mode.BETTER || this.mode.getValue() == ElytraFlight.Mode.OHARE;
      }, "Horizontal Speed"));
      this.glide = this.register(new Setting("Glide", 1.0E-4F, 0.0F, 0.2F, (v) -> {
         return this.mode.getValue() == ElytraFlight.Mode.BETTER;
      }, "Glide Speed"));
      this.tooBeeSpeed = this.register(new Setting("TooBeeSpeed", 1.8000001F, 1.0F, 2.0F, (v) -> {
         return this.mode.getValue() == ElytraFlight.Mode.TOOBEE;
      }, "Speed for flight on 2b2t"));
      this.autoStart = this.register(new Setting("AutoStart", true));
      this.disableInLiquid = this.register(new Setting("NoLiquid", true));
      this.infiniteDura = this.register(new Setting("InfiniteDura", false));
      this.noKick = this.register(new Setting("NoKick", false, (v) -> {
         return this.mode.getValue() == ElytraFlight.Mode.PACKET;
      }));
      this.allowUp = this.register(new Setting("AllowUp", true, (v) -> {
         return this.mode.getValue() == ElytraFlight.Mode.BETTER;
      }));
      this.lockPitch = this.register(new Setting("LockPitch", false));
      this.timer = new Timer();
      this.bypassTimer = new Timer();
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static ElytraFlight getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ElytraFlight();
      }

      return INSTANCE;
   }

   public void onEnable() {
      if (this.mode.getValue() == ElytraFlight.Mode.BETTER && !(Boolean)this.autoStart.getValue() && (Integer)this.devMode.getValue() == 1) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
      }

      this.flyHeight = null;
      this.posX = null;
      this.posZ = null;
   }

   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   public void onUpdate() {
      if (this.mode.getValue() == ElytraFlight.Mode.BYPASS && (Integer)this.devMode.getValue() == 1 && mc.field_71439_g.func_184613_cA()) {
         mc.field_71439_g.field_70159_w = 0.0D;
         mc.field_71439_g.field_70181_x = -1.0E-4D;
         mc.field_71439_g.field_70179_y = 0.0D;
         double forwardInput = (double)mc.field_71439_g.field_71158_b.field_192832_b;
         double strafeInput = (double)mc.field_71439_g.field_71158_b.field_78902_a;
         double[] result = this.forwardStrafeYaw(forwardInput, strafeInput, (double)mc.field_71439_g.field_70177_z);
         double forward = result[0];
         double strafe = result[1];
         double yaw = result[2];
         if (forwardInput != 0.0D || strafeInput != 0.0D) {
            mc.field_71439_g.field_70159_w = forward * (double)(Float)this.speed.getValue() * Math.cos(Math.toRadians(yaw + 90.0D)) + strafe * (double)(Float)this.speed.getValue() * Math.sin(Math.toRadians(yaw + 90.0D));
            mc.field_71439_g.field_70179_y = forward * (double)(Float)this.speed.getValue() * Math.sin(Math.toRadians(yaw + 90.0D)) - strafe * (double)(Float)this.speed.getValue() * Math.cos(Math.toRadians(yaw + 90.0D));
         }

         if (mc.field_71474_y.field_74311_E.func_151470_d()) {
            mc.field_71439_g.field_70181_x = -1.0D;
         }
      }

   }

   @SubscribeEvent
   public void onSendPacket(PacketEvent.Send event) {
      CPacketPlayer packet;
      if (event.getPacket() instanceof CPacketPlayer && this.mode.getValue() == ElytraFlight.Mode.TOOBEE) {
         packet = (CPacketPlayer)event.getPacket();
         if (mc.field_71439_g.func_184613_cA()) {
         }
      }

      if (event.getPacket() instanceof CPacketPlayer && this.mode.getValue() == ElytraFlight.Mode.TOOBEEBYPASS) {
         packet = (CPacketPlayer)event.getPacket();
         if (mc.field_71439_g.func_184613_cA()) {
         }
      }

   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if (this.mode.getValue() == ElytraFlight.Mode.OHARE) {
         ItemStack itemstack = mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST);
         if (itemstack.func_77973_b() == Items.field_185160_cR && ItemElytra.func_185069_d(itemstack) && mc.field_71439_g.func_184613_cA()) {
            event.setY(mc.field_71474_y.field_74314_A.func_151470_d() ? (double)(Float)this.vSpeed.getValue() : (mc.field_71474_y.field_74311_E.func_151470_d() ? (double)(-(Float)this.vSpeed.getValue()) : 0.0D));
            mc.field_71439_g.func_70024_g(0.0D, mc.field_71474_y.field_74314_A.func_151470_d() ? (double)(Float)this.vSpeed.getValue() : (mc.field_71474_y.field_74311_E.func_151470_d() ? (double)(-(Float)this.vSpeed.getValue()) : 0.0D), 0.0D);
            mc.field_71439_g.field_184835_a = 0.0F;
            mc.field_71439_g.field_184836_b = 0.0F;
            mc.field_71439_g.field_184837_c = 0.0F;
            mc.field_71439_g.field_70701_bs = mc.field_71474_y.field_74314_A.func_151470_d() ? (Float)this.vSpeed.getValue() : (mc.field_71474_y.field_74311_E.func_151470_d() ? -(Float)this.vSpeed.getValue() : 0.0F);
            double forward = (double)mc.field_71439_g.field_71158_b.field_192832_b;
            double strafe = (double)mc.field_71439_g.field_71158_b.field_78902_a;
            float yaw = mc.field_71439_g.field_70177_z;
            if (forward == 0.0D && strafe == 0.0D) {
               event.setX(0.0D);
               event.setZ(0.0D);
            } else {
               if (forward != 0.0D) {
                  if (strafe > 0.0D) {
                     yaw += (float)(forward > 0.0D ? -45 : 45);
                  } else if (strafe < 0.0D) {
                     yaw += (float)(forward > 0.0D ? 45 : -45);
                  }

                  strafe = 0.0D;
                  if (forward > 0.0D) {
                     forward = 1.0D;
                  } else if (forward < 0.0D) {
                     forward = -1.0D;
                  }
               }

               double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
               double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
               event.setX(forward * (double)(Float)this.hSpeed.getValue() * cos + strafe * (double)(Float)this.hSpeed.getValue() * sin);
               event.setZ(forward * (double)(Float)this.hSpeed.getValue() * sin - strafe * (double)(Float)this.hSpeed.getValue() * cos);
            }
         }
      } else {
         double yaw;
         if (event.getStage() == 0 && this.mode.getValue() == ElytraFlight.Mode.BYPASS && (Integer)this.devMode.getValue() == 3) {
            if (mc.field_71439_g.func_184613_cA()) {
               event.setX(0.0D);
               event.setY(-1.0E-4D);
               event.setZ(0.0D);
               yaw = (double)mc.field_71439_g.field_71158_b.field_192832_b;
               double strafeInput = (double)mc.field_71439_g.field_71158_b.field_78902_a;
               double[] result = this.forwardStrafeYaw(yaw, strafeInput, (double)mc.field_71439_g.field_70177_z);
               double forward = result[0];
               double strafe = result[1];
               double yaw = result[2];
               if (yaw != 0.0D || strafeInput != 0.0D) {
                  event.setX(forward * (double)(Float)this.speed.getValue() * Math.cos(Math.toRadians(yaw + 90.0D)) + strafe * (double)(Float)this.speed.getValue() * Math.sin(Math.toRadians(yaw + 90.0D)));
                  event.setY(forward * (double)(Float)this.speed.getValue() * Math.sin(Math.toRadians(yaw + 90.0D)) - strafe * (double)(Float)this.speed.getValue() * Math.cos(Math.toRadians(yaw + 90.0D)));
               }

               if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                  event.setY(-1.0D);
               }
            }
         } else if (this.mode.getValue() == ElytraFlight.Mode.TOOBEE) {
            if (!mc.field_71439_g.func_184613_cA()) {
               return;
            }

            if (mc.field_71439_g.field_71158_b.field_78901_c) {
               return;
            }

            if (mc.field_71439_g.field_71158_b.field_78899_d) {
               mc.field_71439_g.field_70181_x = (double)(-((Float)this.tooBeeSpeed.getValue() / 2.0F));
               event.setY((double)(-((Float)this.speed.getValue() / 2.0F)));
            } else if (event.getY() != -1.01E-4D) {
               event.setY(-1.01E-4D);
               mc.field_71439_g.field_70181_x = -1.01E-4D;
            }

            this.setMoveSpeed(event, (double)(Float)this.tooBeeSpeed.getValue());
         } else if (this.mode.getValue() == ElytraFlight.Mode.TOOBEEBYPASS) {
            if (!mc.field_71439_g.func_184613_cA()) {
               return;
            }

            if (mc.field_71439_g.field_71158_b.field_78901_c) {
               return;
            }

            if ((Boolean)this.lockPitch.getValue()) {
               mc.field_71439_g.field_70125_A = 4.0F;
            }

            if (Phobos.speedManager.getSpeedKpH() > 180.0D) {
               return;
            }

            yaw = Math.toRadians((double)mc.field_71439_g.field_70177_z);
            EntityPlayerSP var10000 = mc.field_71439_g;
            var10000.field_70159_w -= (double)mc.field_71439_g.field_71158_b.field_192832_b * Math.sin(yaw) * 0.04D;
            var10000 = mc.field_71439_g;
            var10000.field_70179_y += (double)mc.field_71439_g.field_71158_b.field_192832_b * Math.cos(yaw) * 0.04D;
         }
      }

   }

   private void setMoveSpeed(MoveEvent event, double speed) {
      double forward = (double)mc.field_71439_g.field_71158_b.field_192832_b;
      double strafe = (double)mc.field_71439_g.field_71158_b.field_78902_a;
      float yaw = mc.field_71439_g.field_70177_z;
      if (forward == 0.0D && strafe == 0.0D) {
         event.setX(0.0D);
         event.setZ(0.0D);
         mc.field_71439_g.field_70159_w = 0.0D;
         mc.field_71439_g.field_70179_y = 0.0D;
      } else {
         if (forward != 0.0D) {
            if (strafe > 0.0D) {
               yaw += (float)(forward > 0.0D ? -45 : 45);
            } else if (strafe < 0.0D) {
               yaw += (float)(forward > 0.0D ? 45 : -45);
            }

            strafe = 0.0D;
            if (forward > 0.0D) {
               forward = 1.0D;
            } else if (forward < 0.0D) {
               forward = -1.0D;
            }
         }

         double x = forward * speed * -Math.sin(Math.toRadians((double)yaw)) + strafe * speed * Math.cos(Math.toRadians((double)yaw));
         double z = forward * speed * Math.cos(Math.toRadians((double)yaw)) - strafe * speed * -Math.sin(Math.toRadians((double)yaw));
         event.setX(x);
         event.setZ(z);
         mc.field_71439_g.field_70159_w = x;
         mc.field_71439_g.field_70179_y = z;
      }

   }

   public void onTick() {
      if (mc.field_71439_g.func_184613_cA()) {
         switch((ElytraFlight.Mode)this.mode.getValue()) {
         case BOOST:
            if (mc.field_71439_g.func_70090_H()) {
               mc.func_147114_u().func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
               return;
            }

            EntityPlayerSP var10000;
            if (mc.field_71474_y.field_74314_A.func_151470_d()) {
               var10000 = mc.field_71439_g;
               var10000.field_70181_x += 0.08D;
            } else if (mc.field_71474_y.field_74311_E.func_151470_d()) {
               var10000 = mc.field_71439_g;
               var10000.field_70181_x -= 0.04D;
            }

            float yaw;
            if (mc.field_71474_y.field_74351_w.func_151470_d()) {
               yaw = (float)Math.toRadians((double)mc.field_71439_g.field_70177_z);
               var10000 = mc.field_71439_g;
               var10000.field_70159_w -= (double)(MathHelper.func_76126_a(yaw) * 0.05F);
               var10000 = mc.field_71439_g;
               var10000.field_70179_y += (double)(MathHelper.func_76134_b(yaw) * 0.05F);
            } else if (mc.field_71474_y.field_74368_y.func_151470_d()) {
               yaw = (float)Math.toRadians((double)mc.field_71439_g.field_70177_z);
               var10000 = mc.field_71439_g;
               var10000.field_70159_w += (double)(MathHelper.func_76126_a(yaw) * 0.05F);
               var10000 = mc.field_71439_g;
               var10000.field_70179_y -= (double)(MathHelper.func_76134_b(yaw) * 0.05F);
            }
            break;
         case FLY:
            mc.field_71439_g.field_71075_bZ.field_75100_b = true;
         }

      }
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b() == Items.field_185160_cR) {
         switch(event.getStage()) {
         case 0:
            if ((Boolean)this.disableInLiquid.getValue() && (mc.field_71439_g.func_70090_H() || mc.field_71439_g.func_180799_ab())) {
               if (mc.field_71439_g.func_184613_cA()) {
                  mc.func_147114_u().func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
               }

               return;
            }

            if ((Boolean)this.autoStart.getValue() && mc.field_71474_y.field_74314_A.func_151470_d() && !mc.field_71439_g.func_184613_cA() && mc.field_71439_g.field_70181_x < 0.0D && this.timer.passedMs(250L)) {
               mc.func_147114_u().func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
               this.timer.reset();
            }

            EntityPlayerSP var10000;
            if (this.mode.getValue() == ElytraFlight.Mode.BETTER) {
               double[] dir = MathUtil.directionSpeed((Integer)this.devMode.getValue() == 1 ? (double)(Float)this.speed.getValue() : (double)(Float)this.hSpeed.getValue());
               switch((Integer)this.devMode.getValue()) {
               case 1:
                  mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
                  mc.field_71439_g.field_70747_aH = (Float)this.speed.getValue();
                  if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                     var10000 = mc.field_71439_g;
                     var10000.field_70181_x += (double)(Float)this.speed.getValue();
                  }

                  if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                     var10000 = mc.field_71439_g;
                     var10000.field_70181_x -= (double)(Float)this.speed.getValue();
                  }

                  if (mc.field_71439_g.field_71158_b.field_78902_a == 0.0F && mc.field_71439_g.field_71158_b.field_192832_b == 0.0F) {
                     mc.field_71439_g.field_70159_w = 0.0D;
                     mc.field_71439_g.field_70179_y = 0.0D;
                     break;
                  }

                  mc.field_71439_g.field_70159_w = dir[0];
                  mc.field_71439_g.field_70179_y = dir[1];
                  break;
               case 2:
                  if (!mc.field_71439_g.func_184613_cA()) {
                     this.flyHeight = null;
                     return;
                  }

                  if (this.flyHeight == null) {
                     this.flyHeight = mc.field_71439_g.field_70163_u;
                  }

                  if ((Boolean)this.noKick.getValue()) {
                     this.flyHeight = this.flyHeight - (double)(Float)this.glide.getValue();
                  }

                  this.posX = 0.0D;
                  this.posZ = 0.0D;
                  if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F || mc.field_71439_g.field_71158_b.field_192832_b != 0.0F) {
                     this.posX = dir[0];
                     this.posZ = dir[1];
                  }

                  if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                     this.flyHeight = mc.field_71439_g.field_70163_u + (double)(Float)this.vSpeed.getValue();
                  }

                  if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                     this.flyHeight = mc.field_71439_g.field_70163_u - (double)(Float)this.vSpeed.getValue();
                  }

                  mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t + this.posX, this.flyHeight, mc.field_71439_g.field_70161_v + this.posZ);
                  mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
                  break;
               case 3:
                  if (!mc.field_71439_g.func_184613_cA()) {
                     this.flyHeight = null;
                     this.posX = null;
                     this.posZ = null;
                     return;
                  }

                  if (this.flyHeight == null || this.posX == null || this.posX == 0.0D || this.posZ == null || this.posZ == 0.0D) {
                     this.flyHeight = mc.field_71439_g.field_70163_u;
                     this.posX = mc.field_71439_g.field_70165_t;
                     this.posZ = mc.field_71439_g.field_70161_v;
                  }

                  if ((Boolean)this.noKick.getValue()) {
                     this.flyHeight = this.flyHeight - (double)(Float)this.glide.getValue();
                  }

                  if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F || mc.field_71439_g.field_71158_b.field_192832_b != 0.0F) {
                     this.posX = this.posX + dir[0];
                     this.posZ = this.posZ + dir[1];
                  }

                  if ((Boolean)this.allowUp.getValue() && mc.field_71474_y.field_74314_A.func_151470_d()) {
                     this.flyHeight = mc.field_71439_g.field_70163_u + (double)((Float)this.vSpeed.getValue() / 10.0F);
                  }

                  if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                     this.flyHeight = mc.field_71439_g.field_70163_u - (double)((Float)this.vSpeed.getValue() / 10.0F);
                  }

                  mc.field_71439_g.func_70107_b(this.posX, this.flyHeight, this.posZ);
                  mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
               }
            }

            double rotationYaw = Math.toRadians((double)mc.field_71439_g.field_70177_z);
            if (mc.field_71439_g.func_184613_cA()) {
               switch((ElytraFlight.Mode)this.mode.getValue()) {
               case VANILLA:
                  float speedScaled = (Float)this.speed.getValue() * 0.05F;
                  if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                     var10000 = mc.field_71439_g;
                     var10000.field_70181_x += (double)speedScaled;
                  }

                  if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                     var10000 = mc.field_71439_g;
                     var10000.field_70181_x -= (double)speedScaled;
                  }

                  if (mc.field_71474_y.field_74351_w.func_151470_d()) {
                     var10000 = mc.field_71439_g;
                     var10000.field_70159_w -= Math.sin(rotationYaw) * (double)speedScaled;
                     var10000 = mc.field_71439_g;
                     var10000.field_70179_y += Math.cos(rotationYaw) * (double)speedScaled;
                  }

                  if (mc.field_71474_y.field_74368_y.func_151470_d()) {
                     var10000 = mc.field_71439_g;
                     var10000.field_70159_w += Math.sin(rotationYaw) * (double)speedScaled;
                     var10000 = mc.field_71439_g;
                     var10000.field_70179_y -= Math.cos(rotationYaw) * (double)speedScaled;
                  }
                  break;
               case PACKET:
                  this.freezePlayer(mc.field_71439_g);
                  this.runNoKick(mc.field_71439_g);
                  double[] directionSpeedPacket = MathUtil.directionSpeed((double)(Float)this.speed.getValue());
                  if (mc.field_71439_g.field_71158_b.field_78901_c) {
                     mc.field_71439_g.field_70181_x = (double)(Float)this.speed.getValue();
                  }

                  if (mc.field_71439_g.field_71158_b.field_78899_d) {
                     mc.field_71439_g.field_70181_x = (double)(-(Float)this.speed.getValue());
                  }

                  if (mc.field_71439_g.field_71158_b.field_78902_a != 0.0F || mc.field_71439_g.field_71158_b.field_192832_b != 0.0F) {
                     mc.field_71439_g.field_70159_w = directionSpeedPacket[0];
                     mc.field_71439_g.field_70179_y = directionSpeedPacket[1];
                  }

                  mc.func_147114_u().func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
                  mc.func_147114_u().func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
                  break;
               case BYPASS:
                  if ((Integer)this.devMode.getValue() == 3) {
                     if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                        mc.field_71439_g.field_70181_x = 0.019999999552965164D;
                     }

                     if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                        mc.field_71439_g.field_70181_x = -0.20000000298023224D;
                     }

                     if (mc.field_71439_g.field_70173_aa % 8 == 0 && mc.field_71439_g.field_70163_u <= 240.0D) {
                        mc.field_71439_g.field_70181_x = 0.019999999552965164D;
                     }

                     mc.field_71439_g.field_71075_bZ.field_75100_b = true;
                     mc.field_71439_g.field_71075_bZ.func_75092_a(0.025F);
                     double[] directionSpeedBypass = MathUtil.directionSpeed(0.5199999809265137D);
                     if (mc.field_71439_g.field_71158_b.field_78902_a == 0.0F && mc.field_71439_g.field_71158_b.field_192832_b == 0.0F) {
                        mc.field_71439_g.field_70159_w = 0.0D;
                        mc.field_71439_g.field_70179_y = 0.0D;
                     } else {
                        mc.field_71439_g.field_70159_w = directionSpeedBypass[0];
                        mc.field_71439_g.field_70179_y = directionSpeedBypass[1];
                     }
                  }
               }
            }

            if ((Boolean)this.infiniteDura.getValue()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
            }
            break;
         case 1:
            if ((Boolean)this.infiniteDura.getValue()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
            }
         }

      }
   }

   private double[] forwardStrafeYaw(double forward, double strafe, double yaw) {
      double[] result = new double[]{forward, strafe, yaw};
      if ((forward != 0.0D || strafe != 0.0D) && forward != 0.0D) {
         if (strafe > 0.0D) {
            result[2] += (double)(forward > 0.0D ? -45 : 45);
         } else if (strafe < 0.0D) {
            result[2] += (double)(forward > 0.0D ? 45 : -45);
         }

         result[1] = 0.0D;
         if (forward > 0.0D) {
            result[0] = 1.0D;
         } else if (forward < 0.0D) {
            result[0] = -1.0D;
         }
      }

      return result;
   }

   private void freezePlayer(EntityPlayer player) {
      player.field_70159_w = 0.0D;
      player.field_70181_x = 0.0D;
      player.field_70179_y = 0.0D;
   }

   private void runNoKick(EntityPlayer player) {
      if ((Boolean)this.noKick.getValue() && !player.func_184613_cA() && player.field_70173_aa % 4 == 0) {
         player.field_70181_x = -0.03999999910593033D;
      }

   }

   public void onDisable() {
      if (!fullNullCheck() && !mc.field_71439_g.field_71075_bZ.field_75098_d) {
         mc.field_71439_g.field_71075_bZ.field_75100_b = false;
      }
   }

   public static enum Mode {
      VANILLA,
      PACKET,
      BOOST,
      FLY,
      BYPASS,
      BETTER,
      OHARE,
      TOOBEE,
      TOOBEEBYPASS;
   }
}
