package me.earth.phobos.features.modules.movement;

import io.netty.util.internal.ConcurrentSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.PushEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TestPhase extends Module {
   public Setting<Boolean> flight = this.register(new Setting("Flight", true));
   public Setting<Integer> flightMode = this.register(new Setting("FMode", 0, 0, 1));
   public Setting<Boolean> doAntiFactor = this.register(new Setting("Factorize", true));
   public Setting<Double> antiFactor = this.register(new Setting("AntiFactor", 2.5D, 0.1D, 3.0D));
   public Setting<Double> extraFactor = this.register(new Setting("ExtraFactor", 1.0D, 0.1D, 3.0D));
   public Setting<Boolean> strafeFactor = this.register(new Setting("StrafeFactor", true));
   public Setting<Integer> loops = this.register(new Setting("Loops", 1, 1, 10));
   public Setting<Boolean> clearTeleMap = this.register(new Setting("ClearMap", true));
   public Setting<Integer> mapTime = this.register(new Setting("ClearTime", 30, 1, 500));
   public Setting<Boolean> clearIDs = this.register(new Setting("ClearIDs", true));
   public Setting<Boolean> setYaw = this.register(new Setting("SetYaw", true));
   public Setting<Boolean> setID = this.register(new Setting("SetID", true));
   public Setting<Boolean> setMove = this.register(new Setting("SetMove", false));
   public Setting<Boolean> nocliperino = this.register(new Setting("NoClip", false));
   public Setting<Boolean> sendTeleport = this.register(new Setting("Teleport", true));
   public Setting<Boolean> resetID = this.register(new Setting("ResetID", true));
   public Setting<Boolean> setPos = this.register(new Setting("SetPos", false));
   public Setting<Boolean> invalidPacket = this.register(new Setting("InvalidPacket", true));
   private final Set<CPacketPlayer> packets = new ConcurrentSet();
   private final Map<Integer, TestPhase.IDtime> teleportmap = new ConcurrentHashMap();
   private int flightCounter = 0;
   private int teleportID = 0;
   private static TestPhase instance;

   public TestPhase() {
      super("Packetfly", "Uses packets to fly!", Module.Category.MOVEMENT, true, false, false);
      instance = this;
   }

   public static TestPhase getInstance() {
      if (instance == null) {
         instance = new TestPhase();
      }

      return instance;
   }

   public void onToggle() {
   }

   public void onTick() {
      this.teleportmap.entrySet().removeIf((idTime) -> {
         return (Boolean)this.clearTeleMap.getValue() && ((TestPhase.IDtime)idTime.getValue()).getTimer().passedS((double)(Integer)this.mapTime.getValue());
      });
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() != 1) {
         mc.field_71439_g.func_70016_h(0.0D, 0.0D, 0.0D);
         double speed = 0.0D;
         boolean checkCollisionBoxes = this.checkHitBoxes();
         if (mc.field_71439_g.field_71158_b.field_78901_c && (checkCollisionBoxes || !EntityUtil.isMoving())) {
            if ((Boolean)this.flight.getValue() && !checkCollisionBoxes) {
               if ((Integer)this.flightMode.getValue() == 0) {
                  speed = this.resetCounter(10) ? -0.032D : 0.062D;
               } else {
                  speed = this.resetCounter(20) ? -0.032D : 0.062D;
               }
            } else {
               speed = 0.062D;
            }
         } else if (mc.field_71439_g.field_71158_b.field_78899_d) {
            speed = -0.062D;
         } else if (!checkCollisionBoxes) {
            speed = this.resetCounter(4) ? ((Boolean)this.flight.getValue() ? -0.04D : 0.0D) : 0.0D;
         } else {
            speed = 0.0D;
         }

         if ((Boolean)this.doAntiFactor.getValue() && checkCollisionBoxes && EntityUtil.isMoving() && speed != 0.0D) {
            speed /= (Double)this.antiFactor.getValue();
         }

         double[] strafing = this.getMotion((Boolean)this.strafeFactor.getValue() && checkCollisionBoxes ? 0.031D : 0.26D);

         for(int i = 1; i < (Integer)this.loops.getValue() + 1; ++i) {
            mc.field_71439_g.field_70159_w = strafing[0] * (double)i * (Double)this.extraFactor.getValue();
            mc.field_71439_g.field_70181_x = speed * (double)i;
            mc.field_71439_g.field_70179_y = strafing[1] * (double)i * (Double)this.extraFactor.getValue();
            this.sendPackets(mc.field_71439_g.field_70159_w, mc.field_71439_g.field_70181_x, mc.field_71439_g.field_70179_y, (Boolean)this.sendTeleport.getValue());
         }

      }
   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if ((Boolean)this.setMove.getValue() && this.flightCounter != 0) {
         event.setX(mc.field_71439_g.field_70159_w);
         event.setY(mc.field_71439_g.field_70181_x);
         event.setZ(mc.field_71439_g.field_70179_y);
         if ((Boolean)this.nocliperino.getValue() && this.checkHitBoxes()) {
            mc.field_71439_g.field_70145_X = true;
         }
      }

   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getPacket() instanceof CPacketPlayer) {
         CPacketPlayer packet = (CPacketPlayer)event.getPacket();
         if (!this.packets.remove(packet)) {
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void onPushOutOfBlocks(PushEvent event) {
      if (event.getStage() == 1) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.getPacket() instanceof SPacketPlayerPosLook && !fullNullCheck()) {
         SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
         if (mc.field_71439_g.func_70089_S()) {
            BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
            if (mc.field_71441_e.func_175668_a(pos, false) && !(mc.field_71462_r instanceof GuiDownloadTerrain) && (Boolean)this.clearIDs.getValue()) {
               this.teleportmap.remove(packet.func_186965_f());
            }
         }

         if ((Boolean)this.setYaw.getValue()) {
            packet.field_148936_d = mc.field_71439_g.field_70177_z;
            packet.field_148937_e = mc.field_71439_g.field_70125_A;
         }

         if ((Boolean)this.setID.getValue()) {
            this.teleportID = packet.func_186965_f();
         }
      }

   }

   private boolean checkHitBoxes() {
      return !mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72321_a(-0.0625D, -0.0625D, -0.0625D)).isEmpty();
   }

   private boolean resetCounter(int counter) {
      if (++this.flightCounter >= counter) {
         this.flightCounter = 0;
         return true;
      } else {
         return false;
      }
   }

   private double[] getMotion(double speed) {
      float moveForward = mc.field_71439_g.field_71158_b.field_192832_b;
      float moveStrafe = mc.field_71439_g.field_71158_b.field_78902_a;
      float rotationYaw = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
      if (moveForward != 0.0F) {
         if (moveStrafe > 0.0F) {
            rotationYaw += (float)(moveForward > 0.0F ? -45 : 45);
         } else if (moveStrafe < 0.0F) {
            rotationYaw += (float)(moveForward > 0.0F ? 45 : -45);
         }

         moveStrafe = 0.0F;
         if (moveForward > 0.0F) {
            moveForward = 1.0F;
         } else if (moveForward < 0.0F) {
            moveForward = -1.0F;
         }
      }

      double posX = (double)moveForward * speed * -Math.sin(Math.toRadians((double)rotationYaw)) + (double)moveStrafe * speed * Math.cos(Math.toRadians((double)rotationYaw));
      double posZ = (double)moveForward * speed * Math.cos(Math.toRadians((double)rotationYaw)) - (double)moveStrafe * speed * -Math.sin(Math.toRadians((double)rotationYaw));
      return new double[]{posX, posZ};
   }

   private void sendPackets(double x, double y, double z, boolean teleport) {
      Vec3d vec = new Vec3d(x, y, z);
      Vec3d position = mc.field_71439_g.func_174791_d().func_178787_e(vec);
      Vec3d outOfBoundsVec = this.outOfBoundsVec(vec, position);
      this.packetSender(new Position(position.field_72450_a, position.field_72448_b, position.field_72449_c, mc.field_71439_g.field_70122_E));
      if ((Boolean)this.invalidPacket.getValue()) {
         this.packetSender(new Position(outOfBoundsVec.field_72450_a, outOfBoundsVec.field_72448_b, outOfBoundsVec.field_72449_c, mc.field_71439_g.field_70122_E));
      }

      if ((Boolean)this.setPos.getValue()) {
         mc.field_71439_g.func_70107_b(position.field_72450_a, position.field_72448_b, position.field_72449_c);
      }

      this.teleportPacket(position, teleport);
   }

   private void teleportPacket(Vec3d pos, boolean shouldTeleport) {
      if (shouldTeleport) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(++this.teleportID));
         this.teleportmap.put(this.teleportID, new TestPhase.IDtime(pos, new Timer()));
      }

   }

   private Vec3d outOfBoundsVec(Vec3d offset, Vec3d position) {
      return position.func_72441_c(0.0D, 1337.0D, 0.0D);
   }

   private void packetSender(CPacketPlayer packet) {
      this.packets.add(packet);
      mc.field_71439_g.field_71174_a.func_147297_a(packet);
   }

   private void clean() {
      this.teleportmap.clear();
      this.flightCounter = 0;
      if ((Boolean)this.resetID.getValue()) {
         this.teleportID = 0;
      }

      this.packets.clear();
   }

   public static class IDtime {
      private final Vec3d pos;
      private final Timer timer;

      public IDtime(Vec3d pos, Timer timer) {
         this.pos = pos;
         this.timer = timer;
         this.timer.reset();
      }

      public Vec3d getPos() {
         return this.pos;
      }

      public Timer getTimer() {
         return this.timer;
      }
   }
}
