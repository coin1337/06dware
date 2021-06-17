package me.earth.phobos.features.modules.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.PushEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.Util;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class Flight extends Module {
   public Setting<Flight.Mode> mode;
   public Setting<Boolean> better;
   public Setting<Flight.Format> format;
   public Setting<Flight.PacketMode> type;
   public Setting<Boolean> phase;
   public Setting<Float> speed;
   public Setting<Boolean> noKick;
   public Setting<Boolean> noClip;
   public Setting<Boolean> groundSpoof;
   public Setting<Boolean> antiGround;
   public Setting<Integer> cooldown;
   public Setting<Boolean> ascend;
   private List<CPacketPlayer> packets;
   private int teleportId;
   private static Flight INSTANCE = new Flight();
   private int counter;
   private final Flight.Fly flySwitch;
   private double moveSpeed;
   private double lastDist;
   private int level;
   private Timer delayTimer;

   public Flight() {
      super("Flight", "Makes you fly.", Module.Category.MOVEMENT, true, false, false);
      this.mode = this.register(new Setting("Mode", Flight.Mode.PACKET));
      this.better = this.register(new Setting("Better", false, (v) -> {
         return this.mode.getValue() == Flight.Mode.PACKET;
      }));
      this.format = this.register(new Setting("Format", Flight.Format.DAMAGE, (v) -> {
         return this.mode.getValue() == Flight.Mode.DAMAGE;
      }));
      this.type = this.register(new Setting("Type", Flight.PacketMode.Y, (v) -> {
         return this.mode.getValue() == Flight.Mode.PACKET;
      }));
      this.phase = this.register(new Setting("Phase", false, (v) -> {
         return this.mode.getValue() == Flight.Mode.PACKET && (Boolean)this.better.getValue();
      }));
      this.speed = this.register(new Setting("Speed", 0.1F, 0.0F, 10.0F, (v) -> {
         return this.mode.getValue() == Flight.Mode.PACKET || this.mode.getValue() == Flight.Mode.DESCEND || this.mode.getValue() == Flight.Mode.DAMAGE;
      }, "The speed."));
      this.noKick = this.register(new Setting("NoKick", false, (v) -> {
         return this.mode.getValue() == Flight.Mode.PACKET || this.mode.getValue() == Flight.Mode.DAMAGE;
      }));
      this.noClip = this.register(new Setting("NoClip", false, (v) -> {
         return this.mode.getValue() == Flight.Mode.DAMAGE;
      }));
      this.groundSpoof = this.register(new Setting("GroundSpoof", false, (v) -> {
         return this.mode.getValue() == Flight.Mode.SPOOF;
      }));
      this.antiGround = this.register(new Setting("AntiGround", true, (v) -> {
         return this.mode.getValue() == Flight.Mode.SPOOF;
      }));
      this.cooldown = this.register(new Setting("Cooldown", 1, (v) -> {
         return this.mode.getValue() == Flight.Mode.DESCEND;
      }));
      this.ascend = this.register(new Setting("Ascend", false, (v) -> {
         return this.mode.getValue() == Flight.Mode.DESCEND;
      }));
      this.packets = new ArrayList();
      this.teleportId = 0;
      this.counter = 0;
      this.flySwitch = new Flight.Fly();
      this.delayTimer = new Timer();
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Flight getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Flight();
      }

      return INSTANCE;
   }

   @SubscribeEvent
   public void onTickEvent(ClientTickEvent event) {
      if (!fullNullCheck() && this.mode.getValue() == Flight.Mode.DESCEND) {
         if (event.phase == net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END) {
            if (!mc.field_71439_g.func_184613_cA()) {
               if (this.counter < 1) {
                  this.counter += (Integer)this.cooldown.getValue();
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 0.03D, mc.field_71439_g.field_70161_v, true));
               } else {
                  --this.counter;
               }
            }
         } else if ((Boolean)this.ascend.getValue()) {
            mc.field_71439_g.field_70181_x = (double)(Float)this.speed.getValue();
         } else {
            mc.field_71439_g.field_70181_x = (double)(-(Float)this.speed.getValue());
         }

      }
   }

   public void onEnable() {
      if (!fullNullCheck()) {
         Position bounds;
         if (this.mode.getValue() == Flight.Mode.PACKET) {
            this.teleportId = 0;
            this.packets.clear();
            bounds = new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E);
            this.packets.add(bounds);
            mc.field_71439_g.field_71174_a.func_147297_a(bounds);
         }

         if (this.mode.getValue() == Flight.Mode.CREATIVE) {
            mc.field_71439_g.field_71075_bZ.field_75100_b = true;
            if (mc.field_71439_g.field_71075_bZ.field_75098_d) {
               return;
            }

            mc.field_71439_g.field_71075_bZ.field_75101_c = true;
         }

         if (this.mode.getValue() == Flight.Mode.SPOOF) {
            this.flySwitch.enable();
         }

         if (this.mode.getValue() == Flight.Mode.DAMAGE) {
            this.level = 0;
            if (this.format.getValue() == Flight.Format.PACKET && mc.field_71441_e != null) {
               this.teleportId = 0;
               this.packets.clear();
               bounds = new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u <= 10.0D ? 255.0D : 1.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E);
               this.packets.add(bounds);
               mc.field_71439_g.field_71174_a.func_147297_a(bounds);
            }
         }

      }
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      double[] dir;
      EntityPlayerSP var10000;
      if (this.mode.getValue() == Flight.Mode.DAMAGE) {
         double posY;
         if (this.format.getValue() == Flight.Format.DAMAGE) {
            if (event.getStage() == 0) {
               mc.field_71439_g.field_70181_x = 0.0D;
               posY = 0.41999998688697815D;
               if (mc.field_71439_g.field_70122_E) {
                  if (mc.field_71439_g.func_70644_a(MobEffects.field_76430_j)) {
                     posY += (double)((float)(mc.field_71439_g.func_70660_b(MobEffects.field_76430_j).func_76458_c() + 1) * 0.1F);
                  }

                  Phobos.positionManager.setPlayerPosition(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70181_x = posY, mc.field_71439_g.field_70161_v);
                  this.moveSpeed *= 2.149D;
               }
            }

            if (mc.field_71439_g.field_70173_aa % 2 == 0) {
               mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + MathUtil.getRandom(1.2354235325235235E-14D, 1.2354235325235233E-13D), mc.field_71439_g.field_70161_v);
            }

            if (mc.field_71474_y.field_74314_A.func_151470_d()) {
               var10000 = mc.field_71439_g;
               var10000.field_70181_x += (double)((Float)this.speed.getValue() / 2.0F);
            }

            if (mc.field_71474_y.field_74311_E.func_151470_d()) {
               var10000 = mc.field_71439_g;
               var10000.field_70181_x -= (double)((Float)this.speed.getValue() / 2.0F);
            }
         }

         if (this.format.getValue() == Flight.Format.NORMAL) {
            if (mc.field_71474_y.field_74314_A.func_151470_d()) {
               mc.field_71439_g.field_70181_x = (double)(Float)this.speed.getValue();
            } else if (mc.field_71474_y.field_74311_E.func_151470_d()) {
               mc.field_71439_g.field_70181_x = (double)(-(Float)this.speed.getValue());
            } else {
               mc.field_71439_g.field_70181_x = 0.0D;
            }

            if ((Boolean)this.noKick.getValue() && mc.field_71439_g.field_70173_aa % 5 == 0) {
               Phobos.positionManager.setPlayerPosition(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 0.03125D, mc.field_71439_g.field_70161_v, true);
            }

            dir = EntityUtil.forward((double)(Float)this.speed.getValue());
            mc.field_71439_g.field_70159_w = dir[0];
            mc.field_71439_g.field_70179_y = dir[1];
         }

         Position bounds;
         double posY;
         if (this.format.getValue() == Flight.Format.PACKET) {
            if (this.teleportId <= 0) {
               bounds = new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u <= 10.0D ? 255.0D : 1.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E);
               this.packets.add(bounds);
               mc.field_71439_g.field_71174_a.func_147297_a(bounds);
               return;
            }

            mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
            posY = -1.0E-8D;
            if (!mc.field_71474_y.field_74314_A.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d()) {
               if (EntityUtil.isMoving()) {
                  for(posY = 0.0625D; posY < (double)(Float)this.speed.getValue(); posY += 0.262D) {
                     double[] dir = EntityUtil.forward(posY);
                     mc.field_71439_g.func_70016_h(dir[0], posY, dir[1]);
                     this.move(dir[0], posY, dir[1]);
                  }
               }
            } else {
               int i;
               if (mc.field_71474_y.field_74314_A.func_151470_d()) {
                  for(i = 0; i <= 3; ++i) {
                     mc.field_71439_g.func_70016_h(0.0D, mc.field_71439_g.field_70173_aa % 20 == 0 ? -0.03999999910593033D : (double)(0.062F * (float)i), 0.0D);
                     this.move(0.0D, mc.field_71439_g.field_70173_aa % 20 == 0 ? -0.03999999910593033D : (double)(0.062F * (float)i), 0.0D);
                  }
               } else if (mc.field_71474_y.field_74311_E.func_151470_d()) {
                  for(i = 0; i <= 3; ++i) {
                     mc.field_71439_g.func_70016_h(0.0D, posY - 0.0625D * (double)i, 0.0D);
                     this.move(0.0D, posY - 0.0625D * (double)i, 0.0D);
                  }
               }
            }
         }

         if (this.format.getValue() == Flight.Format.SLOW) {
            posY = mc.field_71439_g.field_70165_t;
            posY = mc.field_71439_g.field_70163_u;
            double posZ = mc.field_71439_g.field_70161_v;
            boolean ground = mc.field_71439_g.field_70122_E;
            mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
            if (!mc.field_71474_y.field_74314_A.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d()) {
               double[] dir = EntityUtil.forward(0.0625D);
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(posY + dir[0], posY, posZ + dir[1], ground));
               mc.field_71439_g.func_70634_a(posY + dir[0], posY, posZ + dir[1]);
            } else if (mc.field_71474_y.field_74314_A.func_151470_d()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(posY, posY + 0.0625D, posZ, ground));
               mc.field_71439_g.func_70634_a(posY, posY + 0.0625D, posZ);
            } else if (mc.field_71474_y.field_74311_E.func_151470_d()) {
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(posY, posY - 0.0625D, posZ, ground));
               mc.field_71439_g.func_70634_a(posY, posY - 0.0625D, posZ);
            }

            mc.field_71439_g.field_71174_a.func_147297_a(new Position(posY + mc.field_71439_g.field_70159_w, mc.field_71439_g.field_70163_u <= 10.0D ? 255.0D : 1.0D, posZ + mc.field_71439_g.field_70179_y, ground));
         }

         if (this.format.getValue() == Flight.Format.DELAY) {
            if (this.delayTimer.passedMs(1000L)) {
               this.delayTimer.reset();
            }

            if (this.delayTimer.passedMs(600L)) {
               mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
               return;
            }

            if (this.teleportId <= 0) {
               bounds = new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u <= 10.0D ? 255.0D : 1.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E);
               this.packets.add(bounds);
               mc.field_71439_g.field_71174_a.func_147297_a(bounds);
               return;
            }

            mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
            posY = -1.0E-8D;
            if (!mc.field_71474_y.field_74314_A.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d()) {
               if (EntityUtil.isMoving()) {
                  double[] dir = EntityUtil.forward(0.2D);
                  mc.field_71439_g.func_70016_h(dir[0], posY, dir[1]);
                  this.move(dir[0], posY, dir[1]);
               }
            } else if (mc.field_71474_y.field_74314_A.func_151470_d()) {
               mc.field_71439_g.func_70016_h(0.0D, 0.06199999898672104D, 0.0D);
               this.move(0.0D, 0.06199999898672104D, 0.0D);
            } else if (mc.field_71474_y.field_74311_E.func_151470_d()) {
               mc.field_71439_g.func_70016_h(0.0D, 0.0625D, 0.0D);
               this.move(0.0D, 0.0625D, 0.0D);
            }
         }

         if ((Boolean)this.noClip.getValue()) {
            mc.field_71439_g.field_70145_X = true;
         }
      }

      if (event.getStage() == 0) {
         if (this.mode.getValue() == Flight.Mode.CREATIVE) {
            mc.field_71439_g.field_71075_bZ.func_75092_a((Float)this.speed.getValue());
            mc.field_71439_g.field_71075_bZ.field_75100_b = true;
            if (mc.field_71439_g.field_71075_bZ.field_75098_d) {
               return;
            }

            mc.field_71439_g.field_71075_bZ.field_75101_c = true;
         }

         if (this.mode.getValue() == Flight.Mode.VANILLA) {
            mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
            mc.field_71439_g.field_70747_aH = (Float)this.speed.getValue();
            if ((Boolean)this.noKick.getValue() && mc.field_71439_g.field_70173_aa % 4 == 0) {
               mc.field_71439_g.field_70181_x = -0.03999999910593033D;
            }

            dir = MathUtil.directionSpeed((double)(Float)this.speed.getValue());
            if (mc.field_71439_g.field_71158_b.field_78902_a == 0.0F && mc.field_71439_g.field_71158_b.field_192832_b == 0.0F) {
               mc.field_71439_g.field_70159_w = 0.0D;
               mc.field_71439_g.field_70179_y = 0.0D;
            } else {
               mc.field_71439_g.field_70159_w = dir[0];
               mc.field_71439_g.field_70179_y = dir[1];
            }

            if (mc.field_71474_y.field_74314_A.func_151470_d()) {
               if ((Boolean)this.noKick.getValue()) {
                  mc.field_71439_g.field_70181_x = mc.field_71439_g.field_70173_aa % 20 == 0 ? -0.03999999910593033D : (double)(Float)this.speed.getValue();
               } else {
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x += (double)(Float)this.speed.getValue();
               }
            }

            if (mc.field_71474_y.field_74311_E.func_151470_d()) {
               var10000 = mc.field_71439_g;
               var10000.field_70181_x -= (double)(Float)this.speed.getValue();
            }
         }

         if (this.mode.getValue() == Flight.Mode.PACKET && !(Boolean)this.better.getValue()) {
            this.doNormalPacketFly();
         }

         if (this.mode.getValue() == Flight.Mode.PACKET && (Boolean)this.better.getValue()) {
            this.doBetterPacketFly();
         }
      }

   }

   private void doNormalPacketFly() {
      if (this.teleportId <= 0) {
         CPacketPlayer bounds = new Position(mc.field_71439_g.field_70165_t, 0.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E);
         this.packets.add(bounds);
         mc.field_71439_g.field_71174_a.func_147297_a(bounds);
      } else {
         mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
         if (mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72321_a(-0.0625D, 0.0D, -0.0625D)).isEmpty()) {
            double ySpeed = 0.0D;
            if (mc.field_71474_y.field_74314_A.func_151470_d()) {
               if ((Boolean)this.noKick.getValue()) {
                  ySpeed = mc.field_71439_g.field_70173_aa % 20 == 0 ? -0.03999999910593033D : 0.06199999898672104D;
               } else {
                  ySpeed = 0.06199999898672104D;
               }
            } else if (mc.field_71474_y.field_74311_E.func_151470_d()) {
               ySpeed = -0.062D;
            } else {
               ySpeed = mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72321_a(-0.0625D, -0.0625D, -0.0625D)).isEmpty() ? (mc.field_71439_g.field_70173_aa % 4 == 0 ? (double)((Boolean)this.noKick.getValue() ? -0.04F : 0.0F) : 0.0D) : 0.0D;
            }

            double[] directionalSpeed = MathUtil.directionSpeed((double)(Float)this.speed.getValue());
            if (!mc.field_71474_y.field_74314_A.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d() && !mc.field_71474_y.field_74351_w.func_151470_d() && !mc.field_71474_y.field_74368_y.func_151470_d() && !mc.field_71474_y.field_74366_z.func_151470_d() && !mc.field_71474_y.field_74370_x.func_151470_d()) {
               if ((Boolean)this.noKick.getValue() && mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72321_a(-0.0625D, -0.0625D, -0.0625D)).isEmpty()) {
                  mc.field_71439_g.func_70016_h(0.0D, mc.field_71439_g.field_70173_aa % 2 == 0 ? 0.03999999910593033D : -0.03999999910593033D, 0.0D);
                  this.move(0.0D, mc.field_71439_g.field_70173_aa % 2 == 0 ? 0.03999999910593033D : -0.03999999910593033D, 0.0D);
               }
            } else if (directionalSpeed[0] != 0.0D || directionalSpeed[1] != 0.0D) {
               int i;
               if (mc.field_71439_g.field_71158_b.field_78901_c && (mc.field_71439_g.field_70702_br != 0.0F || mc.field_71439_g.field_191988_bg != 0.0F)) {
                  mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
                  this.move(0.0D, 0.0D, 0.0D);

                  for(i = 0; i <= 3; ++i) {
                     mc.field_71439_g.func_70016_h(0.0D, ySpeed * (double)i, 0.0D);
                     this.move(0.0D, ySpeed * (double)i, 0.0D);
                  }
               } else if (mc.field_71439_g.field_71158_b.field_78901_c) {
                  mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
                  this.move(0.0D, 0.0D, 0.0D);

                  for(i = 0; i <= 3; ++i) {
                     mc.field_71439_g.func_70016_h(0.0D, ySpeed * (double)i, 0.0D);
                     this.move(0.0D, ySpeed * (double)i, 0.0D);
                  }
               } else {
                  for(i = 0; i <= 2; ++i) {
                     mc.field_71439_g.func_70016_h(directionalSpeed[0] * (double)i, ySpeed * (double)i, directionalSpeed[1] * (double)i);
                     this.move(directionalSpeed[0] * (double)i, ySpeed * (double)i, directionalSpeed[1] * (double)i);
                  }
               }
            }
         }

      }
   }

   private void doBetterPacketFly() {
      if (this.teleportId <= 0) {
         CPacketPlayer bounds = new Position(mc.field_71439_g.field_70165_t, 10000.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70122_E);
         this.packets.add(bounds);
         mc.field_71439_g.field_71174_a.func_147297_a(bounds);
      } else {
         mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
         if (mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72321_a(-0.0625D, 0.0D, -0.0625D)).isEmpty()) {
            double ySpeed = 0.0D;
            if (mc.field_71474_y.field_74314_A.func_151470_d()) {
               if ((Boolean)this.noKick.getValue()) {
                  ySpeed = mc.field_71439_g.field_70173_aa % 20 == 0 ? -0.03999999910593033D : 0.06199999898672104D;
               } else {
                  ySpeed = 0.06199999898672104D;
               }
            } else if (mc.field_71474_y.field_74311_E.func_151470_d()) {
               ySpeed = -0.062D;
            } else {
               ySpeed = mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72321_a(-0.0625D, -0.0625D, -0.0625D)).isEmpty() ? (mc.field_71439_g.field_70173_aa % 4 == 0 ? (double)((Boolean)this.noKick.getValue() ? -0.04F : 0.0F) : 0.0D) : 0.0D;
            }

            double[] directionalSpeed = MathUtil.directionSpeed((double)(Float)this.speed.getValue());
            if (!mc.field_71474_y.field_74314_A.func_151470_d() && !mc.field_71474_y.field_74311_E.func_151470_d() && !mc.field_71474_y.field_74351_w.func_151470_d() && !mc.field_71474_y.field_74368_y.func_151470_d() && !mc.field_71474_y.field_74366_z.func_151470_d() && !mc.field_71474_y.field_74370_x.func_151470_d()) {
               if ((Boolean)this.noKick.getValue() && mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72321_a(-0.0625D, -0.0625D, -0.0625D)).isEmpty()) {
                  mc.field_71439_g.func_70016_h(0.0D, mc.field_71439_g.field_70173_aa % 2 == 0 ? 0.03999999910593033D : -0.03999999910593033D, 0.0D);
                  this.move(0.0D, mc.field_71439_g.field_70173_aa % 2 == 0 ? 0.03999999910593033D : -0.03999999910593033D, 0.0D);
               }
            } else if (directionalSpeed[0] != 0.0D || directionalSpeed[1] != 0.0D) {
               int i;
               if (mc.field_71439_g.field_71158_b.field_78901_c && (mc.field_71439_g.field_70702_br != 0.0F || mc.field_71439_g.field_191988_bg != 0.0F)) {
                  mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
                  this.move(0.0D, 0.0D, 0.0D);

                  for(i = 0; i <= 3; ++i) {
                     mc.field_71439_g.func_70016_h(0.0D, ySpeed * (double)i, 0.0D);
                     this.move(0.0D, ySpeed * (double)i, 0.0D);
                  }
               } else if (mc.field_71439_g.field_71158_b.field_78901_c) {
                  mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
                  this.move(0.0D, 0.0D, 0.0D);

                  for(i = 0; i <= 3; ++i) {
                     mc.field_71439_g.func_70016_h(0.0D, ySpeed * (double)i, 0.0D);
                     this.move(0.0D, ySpeed * (double)i, 0.0D);
                  }
               } else {
                  for(i = 0; i <= 2; ++i) {
                     mc.field_71439_g.func_70016_h(directionalSpeed[0] * (double)i, ySpeed * (double)i, directionalSpeed[1] * (double)i);
                     this.move(directionalSpeed[0] * (double)i, ySpeed * (double)i, directionalSpeed[1] * (double)i);
                  }
               }
            }
         }

      }
   }

   public void onUpdate() {
      if (this.mode.getValue() == Flight.Mode.SPOOF) {
         if (fullNullCheck()) {
            return;
         }

         if (!mc.field_71439_g.field_71075_bZ.field_75101_c) {
            this.flySwitch.disable();
            this.flySwitch.enable();
            mc.field_71439_g.field_71075_bZ.field_75100_b = false;
         }

         mc.field_71439_g.field_71075_bZ.func_75092_a(0.05F * (Float)this.speed.getValue());
      }

   }

   public void onDisable() {
      if (this.mode.getValue() == Flight.Mode.CREATIVE && mc.field_71439_g != null) {
         mc.field_71439_g.field_71075_bZ.field_75100_b = false;
         mc.field_71439_g.field_71075_bZ.func_75092_a(0.05F);
         if (mc.field_71439_g.field_71075_bZ.field_75098_d) {
            return;
         }

         mc.field_71439_g.field_71075_bZ.field_75101_c = false;
      }

      if (this.mode.getValue() == Flight.Mode.SPOOF) {
         this.flySwitch.disable();
      }

      if (this.mode.getValue() == Flight.Mode.DAMAGE) {
         Phobos.timerManager.reset();
         mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
         this.moveSpeed = Strafe.getBaseMoveSpeed();
         this.lastDist = 0.0D;
         if ((Boolean)this.noClip.getValue()) {
            mc.field_71439_g.field_70145_X = false;
         }
      }

   }

   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   public void onLogout() {
      if (this.isOn()) {
         this.disable();
      }

   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if (event.getStage() == 0 && this.mode.getValue() == Flight.Mode.DAMAGE && this.format.getValue() == Flight.Format.DAMAGE) {
         double forward = (double)mc.field_71439_g.field_71158_b.field_192832_b;
         double strafe = (double)mc.field_71439_g.field_71158_b.field_78902_a;
         float yaw = mc.field_71439_g.field_70177_z;
         if (forward == 0.0D && strafe == 0.0D) {
            event.setX(0.0D);
            event.setZ(0.0D);
         }

         if (forward != 0.0D && strafe != 0.0D) {
            forward *= Math.sin(0.7853981633974483D);
            strafe *= Math.cos(0.7853981633974483D);
         }

         double difference;
         if (this.level == 1 && (mc.field_71439_g.field_191988_bg != 0.0F || mc.field_71439_g.field_70702_br != 0.0F)) {
            this.level = 2;
            difference = mc.field_71439_g.func_70644_a(MobEffects.field_76424_c) ? 1.86D : 2.05D;
            this.moveSpeed = difference * Strafe.getBaseMoveSpeed() - 0.01D;
         } else if (this.level == 2) {
            ++this.level;
         } else if (this.level == 3) {
            ++this.level;
            difference = (mc.field_71439_g.field_70173_aa % 2 == 0 ? -0.05D : 0.1D) * (this.lastDist - Strafe.getBaseMoveSpeed());
            this.moveSpeed = this.lastDist - difference;
         } else {
            if (mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, mc.field_71439_g.field_70181_x, 0.0D)).size() > 0 || mc.field_71439_g.field_70124_G) {
               this.level = 1;
            }

            this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
         }

         this.moveSpeed = Math.max(this.moveSpeed, Strafe.getBaseMoveSpeed());
         difference = -Math.sin(Math.toRadians((double)yaw));
         double mz = Math.cos(Math.toRadians((double)yaw));
         event.setX(forward * this.moveSpeed * difference + strafe * this.moveSpeed * mz);
         event.setZ(forward * this.moveSpeed * mz - strafe * this.moveSpeed * difference);
      }

   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getStage() == 0) {
         CPacketPlayer packet;
         if (this.mode.getValue() == Flight.Mode.PACKET) {
            if (fullNullCheck()) {
               return;
            }

            if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof Position)) {
               event.setCanceled(true);
            }

            if (event.getPacket() instanceof CPacketPlayer) {
               packet = (CPacketPlayer)event.getPacket();
               if (this.packets.contains(packet)) {
                  this.packets.remove(packet);
                  return;
               }

               event.setCanceled(true);
            }
         }

         if (this.mode.getValue() == Flight.Mode.SPOOF) {
            if (fullNullCheck()) {
               return;
            }

            if (!(Boolean)this.groundSpoof.getValue() || !(event.getPacket() instanceof CPacketPlayer) || !mc.field_71439_g.field_71075_bZ.field_75100_b) {
               return;
            }

            packet = (CPacketPlayer)event.getPacket();
            if (!packet.field_149480_h) {
               return;
            }

            AxisAlignedBB range = mc.field_71439_g.func_174813_aQ().func_72321_a(0.0D, -mc.field_71439_g.field_70163_u, 0.0D).func_191195_a(0.0D, (double)(-mc.field_71439_g.field_70131_O), 0.0D);
            List<AxisAlignedBB> collisionBoxes = mc.field_71439_g.field_70170_p.func_184144_a(mc.field_71439_g, range);
            AtomicReference<Double> newHeight = new AtomicReference(0.0D);
            collisionBoxes.forEach((box) -> {
               newHeight.set(Math.max((Double)newHeight.get(), box.field_72337_e));
            });
            packet.field_149477_b = (Double)newHeight.get();
            packet.field_149474_g = true;
         }

         if (this.mode.getValue() == Flight.Mode.DAMAGE && (this.format.getValue() == Flight.Format.PACKET || this.format.getValue() == Flight.Format.DELAY)) {
            if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof Position)) {
               event.setCanceled(true);
            }

            if (event.getPacket() instanceof CPacketPlayer) {
               packet = (CPacketPlayer)event.getPacket();
               if (this.packets.contains(packet)) {
                  this.packets.remove(packet);
                  return;
               }

               event.setCanceled(true);
            }
         }
      }

   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.getStage() == 0) {
         SPacketPlayerPosLook packet;
         if (this.mode.getValue() == Flight.Mode.PACKET) {
            if (fullNullCheck()) {
               return;
            }

            if (event.getPacket() instanceof SPacketPlayerPosLook) {
               packet = (SPacketPlayerPosLook)event.getPacket();
               if (mc.field_71439_g.func_70089_S() && mc.field_71441_e.func_175667_e(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v)) && !(mc.field_71462_r instanceof GuiDownloadTerrain)) {
                  if (this.teleportId <= 0) {
                     this.teleportId = packet.func_186965_f();
                  } else {
                     event.setCanceled(true);
                  }
               }
            }
         }

         if (this.mode.getValue() == Flight.Mode.SPOOF) {
            if (fullNullCheck()) {
               return;
            }

            if (!(Boolean)this.antiGround.getValue() || !(event.getPacket() instanceof SPacketPlayerPosLook) || !mc.field_71439_g.field_71075_bZ.field_75100_b) {
               return;
            }

            packet = (SPacketPlayerPosLook)event.getPacket();
            double oldY = mc.field_71439_g.field_70163_u;
            mc.field_71439_g.func_70107_b(packet.field_148940_a, packet.field_148938_b, packet.field_148939_c);
            AxisAlignedBB range = mc.field_71439_g.func_174813_aQ().func_72321_a(0.0D, (double)(256.0F - mc.field_71439_g.field_70131_O) - mc.field_71439_g.field_70163_u, 0.0D).func_191195_a(0.0D, (double)mc.field_71439_g.field_70131_O, 0.0D);
            List<AxisAlignedBB> collisionBoxes = mc.field_71439_g.field_70170_p.func_184144_a(mc.field_71439_g, range);
            AtomicReference<Double> newY = new AtomicReference(256.0D);
            collisionBoxes.forEach((box) -> {
               newY.set(Math.min((Double)newY.get(), box.field_72338_b - (double)mc.field_71439_g.field_70131_O));
            });
            packet.field_148938_b = Math.min(oldY, (Double)newY.get());
         }

         if (this.mode.getValue() == Flight.Mode.DAMAGE && (this.format.getValue() == Flight.Format.PACKET || this.format.getValue() == Flight.Format.DELAY) && event.getPacket() instanceof SPacketPlayerPosLook) {
            packet = (SPacketPlayerPosLook)event.getPacket();
            if (mc.field_71439_g.func_70089_S() && mc.field_71441_e.func_175667_e(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v)) && !(mc.field_71462_r instanceof GuiDownloadTerrain)) {
               if (this.teleportId <= 0) {
                  this.teleportId = packet.func_186965_f();
               } else {
                  event.setCanceled(true);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && this.isEnabled() && !event.getSetting().equals(this.enabled)) {
         this.disable();
      }

   }

   @SubscribeEvent
   public void onPush(PushEvent event) {
      if (event.getStage() == 1 && this.mode.getValue() == Flight.Mode.PACKET && (Boolean)this.better.getValue() && (Boolean)this.phase.getValue()) {
         event.setCanceled(true);
      }

   }

   private void move(double x, double y, double z) {
      CPacketPlayer pos = new Position(mc.field_71439_g.field_70165_t + x, mc.field_71439_g.field_70163_u + y, mc.field_71439_g.field_70161_v + z, mc.field_71439_g.field_70122_E);
      this.packets.add(pos);
      mc.field_71439_g.field_71174_a.func_147297_a(pos);
      Object bounds;
      if ((Boolean)this.better.getValue()) {
         bounds = this.createBoundsPacket(x, y, z);
      } else {
         bounds = new Position(mc.field_71439_g.field_70165_t + x, 0.0D, mc.field_71439_g.field_70161_v + z, mc.field_71439_g.field_70122_E);
      }

      this.packets.add(bounds);
      mc.field_71439_g.field_71174_a.func_147297_a((Packet)bounds);
      ++this.teleportId;
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(this.teleportId - 1));
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(this.teleportId));
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(this.teleportId + 1));
   }

   private CPacketPlayer createBoundsPacket(double x, double y, double z) {
      switch((Flight.PacketMode)this.type.getValue()) {
      case Up:
         return new Position(mc.field_71439_g.field_70165_t + x, 10000.0D, mc.field_71439_g.field_70161_v + z, mc.field_71439_g.field_70122_E);
      case Down:
         return new Position(mc.field_71439_g.field_70165_t + x, -10000.0D, mc.field_71439_g.field_70161_v + z, mc.field_71439_g.field_70122_E);
      case Zero:
         return new Position(mc.field_71439_g.field_70165_t + x, 0.0D, mc.field_71439_g.field_70161_v + z, mc.field_71439_g.field_70122_E);
      case Y:
         return new Position(mc.field_71439_g.field_70165_t + x, mc.field_71439_g.field_70163_u + y <= 10.0D ? 255.0D : 1.0D, mc.field_71439_g.field_70161_v + z, mc.field_71439_g.field_70122_E);
      case X:
         return new Position(mc.field_71439_g.field_70165_t + x + 75.0D, mc.field_71439_g.field_70163_u + y, mc.field_71439_g.field_70161_v + z, mc.field_71439_g.field_70122_E);
      case Z:
         return new Position(mc.field_71439_g.field_70165_t + x, mc.field_71439_g.field_70163_u + y, mc.field_71439_g.field_70161_v + z + 75.0D, mc.field_71439_g.field_70122_E);
      case XZ:
         return new Position(mc.field_71439_g.field_70165_t + x + 75.0D, mc.field_71439_g.field_70163_u + y, mc.field_71439_g.field_70161_v + z + 75.0D, mc.field_71439_g.field_70122_E);
      default:
         return new Position(mc.field_71439_g.field_70165_t + x, 2000.0D, mc.field_71439_g.field_70161_v + z, mc.field_71439_g.field_70122_E);
      }
   }

   private static class Fly {
      private Fly() {
      }

      protected void enable() {
         Util.mc.func_152344_a(() -> {
            if (Util.mc.field_71439_g != null && Util.mc.field_71439_g.field_71075_bZ != null) {
               Util.mc.field_71439_g.field_71075_bZ.field_75101_c = true;
               Util.mc.field_71439_g.field_71075_bZ.field_75100_b = true;
            }
         });
      }

      protected void disable() {
         Util.mc.func_152344_a(() -> {
            if (Util.mc.field_71439_g != null && Util.mc.field_71439_g.field_71075_bZ != null) {
               PlayerCapabilities gmCaps = new PlayerCapabilities();
               Util.mc.field_71442_b.func_178889_l().func_77147_a(gmCaps);
               PlayerCapabilities capabilities = Util.mc.field_71439_g.field_71075_bZ;
               capabilities.field_75101_c = gmCaps.field_75101_c;
               capabilities.field_75100_b = gmCaps.field_75101_c && capabilities.field_75100_b;
               capabilities.func_75092_a(gmCaps.func_75093_a());
            }
         });
      }

      // $FF: synthetic method
      Fly(Object x0) {
         this();
      }
   }

   private static enum PacketMode {
      Up,
      Down,
      Zero,
      Y,
      X,
      Z,
      XZ;
   }

   public static enum Format {
      DAMAGE,
      SLOW,
      DELAY,
      NORMAL,
      PACKET;
   }

   public static enum Mode {
      CREATIVE,
      VANILLA,
      PACKET,
      SPOOF,
      DESCEND,
      DAMAGE;
   }
}
