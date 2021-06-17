package me.earth.phobos.features.modules.movement;

import io.netty.util.internal.ConcurrentSet;
import java.util.Set;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.PushEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Phase extends Module {
   public Setting<Phase.Mode> mode;
   public Setting<Phase.PacketFlyMode> type;
   public Setting<Integer> xMove;
   public Setting<Integer> yMove;
   public Setting<Boolean> extra;
   public Setting<Integer> offset;
   public Setting<Boolean> fallPacket;
   public Setting<Boolean> teleporter;
   public Setting<Boolean> boundingBox;
   public Setting<Integer> teleportConfirm;
   public Setting<Boolean> ultraPacket;
   public Setting<Boolean> updates;
   public Setting<Boolean> setOnMove;
   public Setting<Boolean> cliperino;
   public Setting<Boolean> scanPackets;
   public Setting<Boolean> resetConfirm;
   public Setting<Boolean> posLook;
   public Setting<Boolean> cancel;
   public Setting<Boolean> cancelType;
   public Setting<Boolean> onlyY;
   public Setting<Integer> cancelPacket;
   private static Phase INSTANCE = new Phase();
   private Set<CPacketPlayer> packets;
   private boolean teleport;
   private int teleportIds;
   private int posLookPackets;

   public Phase() {
      super("Phase", "Makes you able to phase through blocks.", Module.Category.MOVEMENT, true, false, false);
      this.mode = this.register(new Setting("Mode", Phase.Mode.PACKETFLY));
      this.type = this.register(new Setting("Type", Phase.PacketFlyMode.SETBACK, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY;
      }));
      this.xMove = this.register(new Setting("HMove", 625, 1, 1000, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }, "XMovement speed."));
      this.yMove = this.register(new Setting("YMove", 625, 1, 1000, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }, "YMovement speed."));
      this.extra = this.register(new Setting("ExtraPacket", true, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }));
      this.offset = this.register(new Setting("Offset", 1337, -1337, 1337, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK && (Boolean)this.extra.getValue();
      }, "Up speed."));
      this.fallPacket = this.register(new Setting("FallPacket", true, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }));
      this.teleporter = this.register(new Setting("Teleport", true, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }));
      this.boundingBox = this.register(new Setting("BoundingBox", true, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }));
      this.teleportConfirm = this.register(new Setting("Confirm", 2, 0, 4, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }));
      this.ultraPacket = this.register(new Setting("DoublePacket", false, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }));
      this.updates = this.register(new Setting("Update", false, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }));
      this.setOnMove = this.register(new Setting("SetMove", false, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }));
      this.cliperino = this.register(new Setting("NoClip", false, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK && (Boolean)this.setOnMove.getValue();
      }));
      this.scanPackets = this.register(new Setting("ScanPackets", false, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }));
      this.resetConfirm = this.register(new Setting("Reset", false, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }));
      this.posLook = this.register(new Setting("PosLook", false, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK;
      }));
      this.cancel = this.register(new Setting("Cancel", false, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK && (Boolean)this.posLook.getValue();
      }));
      this.cancelType = this.register(new Setting("SetYaw", false, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK && (Boolean)this.posLook.getValue() && (Boolean)this.cancel.getValue();
      }));
      this.onlyY = this.register(new Setting("OnlyY", false, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK && (Boolean)this.posLook.getValue();
      }));
      this.cancelPacket = this.register(new Setting("Packets", 20, 0, 20, (v) -> {
         return this.mode.getValue() == Phase.Mode.PACKETFLY && this.type.getValue() == Phase.PacketFlyMode.SETBACK && (Boolean)this.posLook.getValue();
      }));
      this.packets = new ConcurrentSet();
      this.teleport = true;
      this.teleportIds = 0;
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Phase getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Phase();
      }

      return INSTANCE;
   }

   public void onDisable() {
      this.packets.clear();
      this.posLookPackets = 0;
      if (mc.field_71439_g != null) {
         if ((Boolean)this.resetConfirm.getValue()) {
            this.teleportIds = 0;
         }

         mc.field_71439_g.field_70145_X = false;
      }

   }

   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if ((Boolean)this.setOnMove.getValue() && this.type.getValue() == Phase.PacketFlyMode.SETBACK && event.getStage() == 0 && !mc.func_71356_B() && this.mode.getValue() == Phase.Mode.PACKETFLY) {
         event.setX(mc.field_71439_g.field_70159_w);
         event.setY(mc.field_71439_g.field_70181_x);
         event.setZ(mc.field_71439_g.field_70179_y);
         if ((Boolean)this.cliperino.getValue()) {
            mc.field_71439_g.field_70145_X = true;
         }
      }

      if (this.type.getValue() != Phase.PacketFlyMode.NONE && event.getStage() == 0 && !mc.func_71356_B() && this.mode.getValue() == Phase.Mode.PACKETFLY) {
         if (!(Boolean)this.boundingBox.getValue() && !(Boolean)this.updates.getValue()) {
            this.doPhase(event);
         }

      }
   }

   @SubscribeEvent
   public void onPush(PushEvent event) {
      if (event.getStage() == 1 && this.type.getValue() != Phase.PacketFlyMode.NONE) {
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public void onMove(UpdateWalkingPlayerEvent event) {
      if (!fullNullCheck() && event.getStage() == 0 && this.type.getValue() == Phase.PacketFlyMode.SETBACK && this.mode.getValue() == Phase.Mode.PACKETFLY) {
         if ((Boolean)this.boundingBox.getValue()) {
            this.doBoundingBox();
         } else if ((Boolean)this.updates.getValue()) {
            this.doPhase((MoveEvent)null);
         }

      }
   }

   private void doPhase(MoveEvent event) {
      if (this.type.getValue() == Phase.PacketFlyMode.SETBACK && !(Boolean)this.boundingBox.getValue()) {
         double[] dirSpeed = this.getMotion(this.teleport ? (double)(Integer)this.yMove.getValue() / 10000.0D : (double)((Integer)this.yMove.getValue() - 1) / 10000.0D);
         double posX = mc.field_71439_g.field_70165_t + dirSpeed[0];
         double posY = mc.field_71439_g.field_70163_u + (mc.field_71474_y.field_74314_A.func_151470_d() ? (this.teleport ? (double)(Integer)this.yMove.getValue() / 10000.0D : (double)((Integer)this.yMove.getValue() - 1) / 10000.0D) : 1.0E-8D) - (mc.field_71474_y.field_74311_E.func_151470_d() ? (this.teleport ? (double)(Integer)this.yMove.getValue() / 10000.0D : (double)((Integer)this.yMove.getValue() - 1) / 10000.0D) : 2.0E-8D);
         double posZ = mc.field_71439_g.field_70161_v + dirSpeed[1];
         CPacketPlayer packetPlayer = new PositionRotation(posX, posY, posZ, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
         this.packets.add(packetPlayer);
         mc.field_71439_g.field_71174_a.func_147297_a(packetPlayer);
         if ((Integer)this.teleportConfirm.getValue() != 3) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(this.teleportIds - 1));
            ++this.teleportIds;
         }

         PositionRotation packet2;
         if ((Boolean)this.extra.getValue()) {
            packet2 = new PositionRotation(mc.field_71439_g.field_70165_t, (double)(Integer)this.offset.getValue() + mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, true);
            this.packets.add(packet2);
            mc.field_71439_g.field_71174_a.func_147297_a(packet2);
         }

         if ((Integer)this.teleportConfirm.getValue() != 1) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(this.teleportIds + 1));
            ++this.teleportIds;
         }

         if ((Boolean)this.ultraPacket.getValue()) {
            packet2 = new PositionRotation(posX, posY, posZ, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false);
            this.packets.add(packet2);
            mc.field_71439_g.field_71174_a.func_147297_a(packet2);
         }

         if ((Integer)this.teleportConfirm.getValue() == 4) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketConfirmTeleport(this.teleportIds));
            ++this.teleportIds;
         }

         if ((Boolean)this.fallPacket.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
         }

         mc.field_71439_g.func_70107_b(posX, posY, posZ);
         this.teleport = !(Boolean)this.teleporter.getValue() || !this.teleport;
         if (event != null) {
            event.setX(0.0D);
            event.setY(0.0D);
            event.setX(0.0D);
         } else {
            mc.field_71439_g.field_70159_w = 0.0D;
            mc.field_71439_g.field_70181_x = 0.0D;
            mc.field_71439_g.field_70179_y = 0.0D;
         }
      }

   }

   private void doBoundingBox() {
      double[] dirSpeed = this.getMotion(this.teleport ? 0.02250000089406967D : 0.02239999920129776D);
      mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t + dirSpeed[0], mc.field_71439_g.field_70163_u + (mc.field_71474_y.field_74314_A.func_151470_d() ? (this.teleport ? 0.0625D : 0.0624D) : 1.0E-8D) - (mc.field_71474_y.field_74311_E.func_151470_d() ? (this.teleport ? 0.0625D : 0.0624D) : 2.0E-8D), mc.field_71439_g.field_70161_v + dirSpeed[1], mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
      mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t, -1337.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, true));
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
      mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t + dirSpeed[0], mc.field_71439_g.field_70163_u + (mc.field_71474_y.field_74314_A.func_151470_d() ? (this.teleport ? 0.0625D : 0.0624D) : 1.0E-8D) - (mc.field_71474_y.field_74311_E.func_151470_d() ? (this.teleport ? 0.0625D : 0.0624D) : 2.0E-8D), mc.field_71439_g.field_70161_v + dirSpeed[1]);
      this.teleport = !this.teleport;
      mc.field_71439_g.field_70159_w = mc.field_71439_g.field_70181_x = mc.field_71439_g.field_70179_y = 0.0D;
      mc.field_71439_g.field_70145_X = this.teleport;
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if ((Boolean)this.posLook.getValue() && event.getPacket() instanceof SPacketPlayerPosLook) {
         SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
         if (mc.field_71439_g.func_70089_S() && mc.field_71441_e.func_175667_e(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v)) && !(mc.field_71462_r instanceof GuiDownloadTerrain)) {
            if (this.teleportIds <= 0) {
               this.teleportIds = packet.func_186965_f();
            }

            if ((Boolean)this.cancel.getValue() && (Boolean)this.cancelType.getValue()) {
               packet.field_148936_d = mc.field_71439_g.field_70177_z;
               packet.field_148937_e = mc.field_71439_g.field_70125_A;
               return;
            }

            if ((Boolean)this.cancel.getValue() && this.posLookPackets >= (Integer)this.cancelPacket.getValue() && (!(Boolean)this.onlyY.getValue() || !mc.field_71474_y.field_74351_w.func_151470_d() && !mc.field_71474_y.field_74366_z.func_151470_d() && !mc.field_71474_y.field_74370_x.func_151470_d() && !mc.field_71474_y.field_74368_y.func_151470_d())) {
               this.posLookPackets = 0;
               event.setCanceled(true);
            }

            ++this.posLookPackets;
         }
      }

   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Send event) {
      if ((Boolean)this.scanPackets.getValue() && event.getPacket() instanceof CPacketPlayer) {
         CPacketPlayer packetPlayer = (CPacketPlayer)event.getPacket();
         if (this.packets.contains(packetPlayer)) {
            this.packets.remove(packetPlayer);
         } else {
            event.setCanceled(true);
         }
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

   public static enum PacketFlyMode {
      NONE,
      SETBACK;
   }

   public static enum Mode {
      PACKETFLY;
   }
}
