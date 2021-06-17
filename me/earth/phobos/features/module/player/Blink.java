package me.earth.phobos.features.modules.player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blink extends Module {
   public Setting<Boolean> cPacketPlayer = this.register(new Setting("CPacketPlayer", true));
   public Setting<Blink.Mode> autoOff;
   public Setting<Integer> timeLimit;
   public Setting<Integer> packetLimit;
   public Setting<Float> distance;
   private Timer timer;
   private Queue<Packet<?>> packets;
   private EntityOtherPlayerMP entity;
   private int packetsCanceled;
   private BlockPos startPos;
   private static Blink INSTANCE = new Blink();

   public Blink() {
      super("Blink", "Fakelag.", Module.Category.PLAYER, true, false, false);
      this.autoOff = this.register(new Setting("AutoOff", Blink.Mode.MANUAL));
      this.timeLimit = this.register(new Setting("Time", 20, 1, 500, (v) -> {
         return this.autoOff.getValue() == Blink.Mode.TIME;
      }));
      this.packetLimit = this.register(new Setting("Packets", 20, 1, 500, (v) -> {
         return this.autoOff.getValue() == Blink.Mode.PACKETS;
      }));
      this.distance = this.register(new Setting("Distance", 10.0F, 1.0F, 100.0F, (v) -> {
         return this.autoOff.getValue() == Blink.Mode.DISTANCE;
      }));
      this.timer = new Timer();
      this.packets = new ConcurrentLinkedQueue();
      this.packetsCanceled = 0;
      this.startPos = null;
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Blink getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Blink();
      }

      return INSTANCE;
   }

   public void onEnable() {
      if (!fullNullCheck()) {
         this.entity = new EntityOtherPlayerMP(mc.field_71441_e, mc.field_71449_j.func_148256_e());
         this.entity.func_82149_j(mc.field_71439_g);
         this.entity.field_70177_z = mc.field_71439_g.field_70177_z;
         this.entity.field_70759_as = mc.field_71439_g.field_70759_as;
         this.entity.field_71071_by.func_70455_b(mc.field_71439_g.field_71071_by);
         mc.field_71441_e.func_73027_a(6942069, this.entity);
         this.startPos = mc.field_71439_g.func_180425_c();
      } else {
         this.disable();
      }

      this.packetsCanceled = 0;
      this.timer.reset();
   }

   public void onUpdate() {
      if (nullCheck() || this.autoOff.getValue() == Blink.Mode.TIME && this.timer.passedS((double)(Integer)this.timeLimit.getValue()) || this.autoOff.getValue() == Blink.Mode.DISTANCE && this.startPos != null && mc.field_71439_g.func_174818_b(this.startPos) >= MathUtil.square((Float)this.distance.getValue()) || this.autoOff.getValue() == Blink.Mode.PACKETS && this.packetsCanceled >= (Integer)this.packetLimit.getValue()) {
         this.disable();
      }

   }

   public void onLogout() {
      if (this.isOn()) {
         this.disable();
      }

   }

   @SubscribeEvent
   public void onSendPacket(PacketEvent.Send event) {
      if (event.getStage() == 0 && mc.field_71441_e != null && !mc.func_71356_B()) {
         Packet<?> packet = event.getPacket();
         if ((Boolean)this.cPacketPlayer.getValue() && packet instanceof CPacketPlayer) {
            event.setCanceled(true);
            this.packets.add(packet);
            ++this.packetsCanceled;
         }

         if (!(Boolean)this.cPacketPlayer.getValue()) {
            if (packet instanceof CPacketChatMessage || packet instanceof CPacketConfirmTeleport || packet instanceof CPacketKeepAlive || packet instanceof CPacketTabComplete || packet instanceof CPacketClientStatus) {
               return;
            }

            this.packets.add(packet);
            event.setCanceled(true);
            ++this.packetsCanceled;
         }
      }

   }

   public void onDisable() {
      if (!fullNullCheck()) {
         mc.field_71441_e.func_72900_e(this.entity);

         while(!this.packets.isEmpty()) {
            mc.field_71439_g.field_71174_a.func_147297_a((Packet)this.packets.poll());
         }
      }

      this.startPos = null;
   }

   public static enum Mode {
      MANUAL,
      TIME,
      DISTANCE,
      PACKETS;
   }
}
