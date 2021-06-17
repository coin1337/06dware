package me.earth.phobos.features.modules.movement;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class LongJump extends Module {
   private final Setting<Integer> timeout = this.register(new Setting("TimeOut", 2000, 0, 5000));
   private final Setting<Float> boost = this.register(new Setting("Boost", 4.48F, 1.0F, 20.0F));
   private final Setting<LongJump.Mode> mode;
   private final Setting<Boolean> lagOff;
   private final Setting<Boolean> autoOff;
   private final Setting<Boolean> disableStrafe;
   private final Setting<Boolean> strafeOff;
   private final Setting<Boolean> step;
   private int stage;
   private int lastHDistance;
   private int airTicks;
   private int headStart;
   private int groundTicks;
   private double moveSpeed;
   private double lastDist;
   private boolean isSpeeding;
   private final Timer timer;
   private boolean beganJump;

   public LongJump() {
      super("LongJump", "Jumps long", Module.Category.MOVEMENT, true, false, false);
      this.mode = this.register(new Setting("Mode", LongJump.Mode.VIRTUE));
      this.lagOff = this.register(new Setting("LagOff", false));
      this.autoOff = this.register(new Setting("AutoOff", false));
      this.disableStrafe = this.register(new Setting("DisableStrafe", false));
      this.strafeOff = this.register(new Setting("StrafeOff", false));
      this.step = this.register(new Setting("SetStep", false));
      this.timer = new Timer();
      this.beganJump = false;
   }

   public void onEnable() {
      this.timer.reset();
      this.headStart = 4;
      this.groundTicks = 0;
      this.stage = 0;
      this.beganJump = false;
      if (Strafe.getInstance().isOn() && (Boolean)this.disableStrafe.getValue()) {
         Strafe.getInstance().disable();
      }

   }

   public void onDisable() {
      Phobos.timerManager.setTimer(1.0F);
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if ((Boolean)this.lagOff.getValue() && event.getPacket() instanceof SPacketPlayerPosLook) {
         this.disable();
      }

   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if (event.getStage() == 0) {
         if (!this.timer.passedMs((long)(Integer)this.timeout.getValue())) {
            event.setX(0.0D);
            event.setY(0.0D);
            event.setZ(0.0D);
         } else {
            if ((Boolean)this.step.getValue()) {
               mc.field_71439_g.field_70138_W = 0.6F;
            }

            this.doVirtue(event);
         }
      }
   }

   @SubscribeEvent
   public void onTickEvent(ClientTickEvent event) {
      if (!fullNullCheck() && event.phase == net.minecraftforge.fml.common.gameevent.TickEvent.Phase.START) {
         if (Strafe.getInstance().isOn() && (Boolean)this.strafeOff.getValue()) {
            this.disable();
         } else {
            switch((LongJump.Mode)this.mode.getValue()) {
            case TICK:
               this.doNormal((UpdateWalkingPlayerEvent)null);
            default:
            }
         }
      }
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0) {
         if (!this.timer.passedMs((long)(Integer)this.timeout.getValue())) {
            event.setCanceled(true);
         } else {
            this.doNormal(event);
         }
      }
   }

   private void doNormal(UpdateWalkingPlayerEvent event) {
      if ((Boolean)this.autoOff.getValue() && this.beganJump && mc.field_71439_g.field_70122_E) {
         this.disable();
      } else {
         switch((LongJump.Mode)this.mode.getValue()) {
         case TICK:
            if (event != null) {
               return;
            }
         case DIRECT:
            if (!EntityUtil.isInLiquid() && !EntityUtil.isOnLiquid()) {
               if (mc.field_71439_g.field_70122_E) {
                  this.lastHDistance = 0;
               }

               float direction = mc.field_71439_g.field_70177_z + (float)(mc.field_71439_g.field_191988_bg < 0.0F ? 180 : 0) + (mc.field_71439_g.field_70702_br > 0.0F ? -90.0F * (mc.field_71439_g.field_191988_bg < 0.0F ? -0.5F : (mc.field_71439_g.field_191988_bg > 0.0F ? 0.5F : 1.0F)) : 0.0F) - (mc.field_71439_g.field_70702_br < 0.0F ? -90.0F * (mc.field_71439_g.field_191988_bg < 0.0F ? -0.5F : (mc.field_71439_g.field_191988_bg > 0.0F ? 0.5F : 1.0F)) : 0.0F);
               float xDir = (float)Math.cos((double)(direction + 90.0F) * 3.141592653589793D / 180.0D);
               float zDir = (float)Math.sin((double)(direction + 90.0F) * 3.141592653589793D / 180.0D);
               EntityPlayerSP var10000;
               if (!mc.field_71439_g.field_70124_G) {
                  ++this.airTicks;
                  this.isSpeeding = true;
                  if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(0.0D, 2.147483647E9D, 0.0D, false));
                  }

                  this.groundTicks = 0;
                  if (!mc.field_71439_g.field_70124_G) {
                     if (mc.field_71439_g.field_70181_x == -0.07190068807140403D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.3499999940395355D;
                     } else if (mc.field_71439_g.field_70181_x == -0.10306193759436909D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.550000011920929D;
                     } else if (mc.field_71439_g.field_70181_x == -0.13395038817442878D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.6700000166893005D;
                     } else if (mc.field_71439_g.field_70181_x == -0.16635183030382D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.6899999976158142D;
                     } else if (mc.field_71439_g.field_70181_x == -0.19088711097794803D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.7099999785423279D;
                     } else if (mc.field_71439_g.field_70181_x == -0.21121925191528862D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.20000000298023224D;
                     } else if (mc.field_71439_g.field_70181_x == -0.11979897632390576D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.9300000071525574D;
                     } else if (mc.field_71439_g.field_70181_x == -0.18758479151225355D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.7200000286102295D;
                     } else if (mc.field_71439_g.field_70181_x == -0.21075983825251726D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.7599999904632568D;
                     }

                     if (mc.field_71439_g.field_70181_x < -0.2D && mc.field_71439_g.field_70181_x > -0.24D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.7D;
                     }

                     if (mc.field_71439_g.field_70181_x < -0.25D && mc.field_71439_g.field_70181_x > -0.32D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.8D;
                     }

                     if (mc.field_71439_g.field_70181_x < -0.35D && mc.field_71439_g.field_70181_x > -0.8D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.98D;
                     }

                     if (mc.field_71439_g.field_70181_x < -0.8D && mc.field_71439_g.field_70181_x > -1.6D) {
                        var10000 = mc.field_71439_g;
                        var10000.field_70181_x *= 0.99D;
                     }
                  }

                  Phobos.timerManager.setTimer(0.85F);
                  double[] speedVals = new double[]{0.420606D, 0.417924D, 0.415258D, 0.412609D, 0.409977D, 0.407361D, 0.404761D, 0.402178D, 0.399611D, 0.39706D, 0.394525D, 0.392D, 0.3894D, 0.38644D, 0.383655D, 0.381105D, 0.37867D, 0.37625D, 0.37384D, 0.37145D, 0.369D, 0.3666D, 0.3642D, 0.3618D, 0.35945D, 0.357D, 0.354D, 0.351D, 0.348D, 0.345D, 0.342D, 0.339D, 0.336D, 0.333D, 0.33D, 0.327D, 0.324D, 0.321D, 0.318D, 0.315D, 0.312D, 0.309D, 0.307D, 0.305D, 0.303D, 0.3D, 0.297D, 0.295D, 0.293D, 0.291D, 0.289D, 0.287D, 0.285D, 0.283D, 0.281D, 0.279D, 0.277D, 0.275D, 0.273D, 0.271D, 0.269D, 0.267D, 0.265D, 0.263D, 0.261D, 0.259D, 0.257D, 0.255D, 0.253D, 0.251D, 0.249D, 0.247D, 0.245D, 0.243D, 0.241D, 0.239D, 0.237D};
                  if (mc.field_71474_y.field_74351_w.field_74513_e) {
                     try {
                        mc.field_71439_g.field_70159_w = (double)xDir * speedVals[this.airTicks - 1] * 3.0D;
                        mc.field_71439_g.field_70179_y = (double)zDir * speedVals[this.airTicks - 1] * 3.0D;
                     } catch (ArrayIndexOutOfBoundsException var7) {
                        return;
                     }
                  } else {
                     mc.field_71439_g.field_70159_w = 0.0D;
                     mc.field_71439_g.field_70179_y = 0.0D;
                  }
               } else {
                  Phobos.timerManager.setTimer(1.0F);
                  this.airTicks = 0;
                  ++this.groundTicks;
                  --this.headStart;
                  var10000 = mc.field_71439_g;
                  var10000.field_70159_w /= 13.0D;
                  var10000 = mc.field_71439_g;
                  var10000.field_70179_y /= 13.0D;
                  if (this.groundTicks == 1) {
                     this.updatePosition(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
                     this.updatePosition(mc.field_71439_g.field_70165_t + 0.0624D, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
                     this.updatePosition(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.419D, mc.field_71439_g.field_70161_v);
                     this.updatePosition(mc.field_71439_g.field_70165_t + 0.0624D, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
                     this.updatePosition(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.419D, mc.field_71439_g.field_70161_v);
                  } else if (this.groundTicks > 2) {
                     this.groundTicks = 0;
                     mc.field_71439_g.field_70159_w = (double)xDir * 0.3D;
                     mc.field_71439_g.field_70179_y = (double)zDir * 0.3D;
                     mc.field_71439_g.field_70181_x = 0.42399999499320984D;
                     this.beganJump = true;
                  }
               }
            }
            break;
         case VIRTUE:
            if (mc.field_71439_g.field_191988_bg == 0.0F && mc.field_71439_g.field_70702_br == 0.0F) {
               event.setCanceled(true);
            } else {
               double xDist = mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q;
               double zDist = mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s;
               this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
            }
         }

      }
   }

   private void doVirtue(MoveEvent event) {
      if (this.mode.getValue() != LongJump.Mode.VIRTUE || mc.field_71439_g.field_191988_bg == 0.0F && (mc.field_71439_g.field_70702_br == 0.0F || EntityUtil.isOnLiquid() || EntityUtil.isInLiquid())) {
         if (this.stage > 0) {
            this.disable();
         }
      } else {
         if (this.stage == 0) {
            this.moveSpeed = (double)(Float)this.boost.getValue() * this.getBaseMoveSpeed();
         } else if (this.stage == 1) {
            mc.field_71439_g.field_70181_x = 0.42D;
            event.setY(0.42D);
            this.moveSpeed *= 2.149D;
         } else if (this.stage == 2) {
            double difference = 0.66D * (this.lastDist - this.getBaseMoveSpeed());
            this.moveSpeed = this.lastDist - difference;
         } else {
            this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
         }

         this.moveSpeed = Math.max(this.getBaseMoveSpeed(), this.moveSpeed);
         this.setMoveSpeed(event, this.moveSpeed);
         List<AxisAlignedBB> collidingList = mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, mc.field_71439_g.field_70181_x, 0.0D));
         List<AxisAlignedBB> collidingList2 = mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, -0.4D, 0.0D));
         if (!mc.field_71439_g.field_70124_G && (collidingList.size() > 0 || collidingList2.size() > 0)) {
            mc.field_71439_g.field_70181_x = -0.001D;
            event.setY(-0.001D);
         }

         ++this.stage;
      }

   }

   private void invalidPacket() {
      this.updatePosition(0.0D, 2.147483647E9D, 0.0D);
   }

   private void updatePosition(double x, double y, double z) {
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(x, y, z, mc.field_71439_g.field_70122_E));
   }

   private Block getBlock(BlockPos pos) {
      return mc.field_71441_e.func_180495_p(pos).func_177230_c();
   }

   private double getDistance(EntityPlayer player, double distance) {
      List<AxisAlignedBB> boundingBoxes = player.field_70170_p.func_184144_a(player, player.func_174813_aQ().func_72317_d(0.0D, -distance, 0.0D));
      if (boundingBoxes.isEmpty()) {
         return 0.0D;
      } else {
         double y = 0.0D;
         Iterator var7 = boundingBoxes.iterator();

         while(var7.hasNext()) {
            AxisAlignedBB boundingBox = (AxisAlignedBB)var7.next();
            if (boundingBox.field_72337_e > y) {
               y = boundingBox.field_72337_e;
            }
         }

         return player.field_70163_u - y;
      }
   }

   private void setMoveSpeed(MoveEvent event, double speed) {
      MovementInput movementInput = mc.field_71439_g.field_71158_b;
      double forward = (double)movementInput.field_192832_b;
      double strafe = (double)movementInput.field_78902_a;
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

         event.setX(forward * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F))));
         event.setZ(forward * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F))));
      }

   }

   private double getBaseMoveSpeed() {
      double baseSpeed = 0.2873D;
      if (mc.field_71439_g != null && mc.field_71439_g.func_70644_a(MobEffects.field_76424_c)) {
         int amplifier = ((PotionEffect)Objects.requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76424_c))).func_76458_c();
         baseSpeed *= 1.0D + 0.2D * (double)(amplifier + 1);
      }

      return baseSpeed;
   }

   public static enum Mode {
      VIRTUE,
      DIRECT,
      TICK;
   }
}
