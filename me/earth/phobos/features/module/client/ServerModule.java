package me.earth.phobos.features.modules.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.mixin.mixins.accessors.IC00Handshake;
import me.earth.phobos.util.TextUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerModule extends Module {
   public Setting<String> ip = this.register(new Setting("PhobosIP", "0.0.0.0.0"));
   public Setting<String> port = this.register((new Setting("Port", "0")).setRenderName(true));
   public Setting<String> serverIP = this.register(new Setting("ServerIP", "AnarchyHvH.eu"));
   public Setting<Boolean> noFML = this.register(new Setting("RemoveFML", false));
   public Setting<Boolean> getName = this.register(new Setting("GetName", false));
   public Setting<Boolean> average = this.register(new Setting("Average", false));
   public Setting<Boolean> clear = this.register(new Setting("ClearPings", false));
   public Setting<Boolean> oneWay = this.register(new Setting("OneWay", false));
   public Setting<Integer> delay = this.register(new Setting("KeepAlives", 10, 1, 50));
   private static ServerModule instance;
   private final AtomicBoolean connected = new AtomicBoolean(false);
   private final Timer pingTimer = new Timer();
   private long currentPing = 0L;
   private long serverPing = 0L;
   private StringBuffer name = null;
   private long averagePing = 0L;
   private final List<Long> pingList = new ArrayList();

   public ServerModule() {
      super("PingBypass", "Manages Phobos`s internal Server", Module.Category.CLIENT, false, false, true);
      instance = this;
   }

   public String getPlayerName() {
      return this.name == null ? null : this.name.toString();
   }

   public static ServerModule getInstance() {
      if (instance == null) {
         instance = new ServerModule();
      }

      return instance;
   }

   public void onLogout() {
      this.averagePing = 0L;
      this.currentPing = 0L;
      this.serverPing = 0L;
      this.pingList.clear();
      this.connected.set(false);
      this.name = null;
   }

   public void onTick() {
      if (mc.func_147114_u() != null && this.isConnected()) {
         if ((Boolean)this.getName.getValue()) {
            mc.func_147114_u().func_147297_a(new CPacketChatMessage("@Servername"));
            this.getName.setValue(false);
         }

         if (this.pingTimer.passedMs((long)((Integer)this.delay.getValue() * 1000))) {
            mc.func_147114_u().func_147297_a(new CPacketKeepAlive(100L));
            this.pingTimer.reset();
         }

         if ((Boolean)this.clear.getValue()) {
            this.pingList.clear();
         }
      }

   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.getPacket() instanceof SPacketChat) {
         SPacketChat packetChat = (SPacketChat)event.getPacket();
         if (packetChat.func_148915_c().func_150254_d().startsWith("@Client")) {
            this.name = new StringBuffer(TextUtil.stripColor(packetChat.func_148915_c().func_150254_d().replace("@Client", "")));
            event.setCanceled(true);
         }
      } else if (event.getPacket() instanceof SPacketKeepAlive) {
         SPacketKeepAlive alive = (SPacketKeepAlive)event.getPacket();
         if (alive.func_149134_c() != 0L && alive.func_149134_c() < 1000L) {
            this.serverPing = alive.func_149134_c();
            if ((Boolean)this.oneWay.getValue()) {
               this.currentPing = this.pingTimer.getPassedTimeMs() / 2L;
            } else {
               this.currentPing = this.pingTimer.getPassedTimeMs();
            }

            this.pingList.add(this.currentPing);
            this.averagePing = this.getAveragePing();
         }
      }

   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getPacket() instanceof C00Handshake) {
         IC00Handshake packet = (IC00Handshake)event.getPacket();
         String ip = packet.getIp();
         if (ip.equals(this.ip.getValue())) {
            packet.setIp((String)this.serverIP.getValue());
            System.out.println(packet.getIp());
            this.connected.set(true);
         }
      }

   }

   public String getDisplayInfo() {
      return this.averagePing + "ms";
   }

   private long getAveragePing() {
      if ((Boolean)this.average.getValue() && !this.pingList.isEmpty()) {
         int full = 0;

         long i;
         for(Iterator var2 = this.pingList.iterator(); var2.hasNext(); full = (int)((long)full + i)) {
            i = (Long)var2.next();
         }

         return (long)(full / this.pingList.size());
      } else {
         return this.currentPing;
      }
   }

   public boolean isConnected() {
      return this.connected.get();
   }

   public int getPort() {
      try {
         int result = Integer.parseInt((String)this.port.getValue());
         return result;
      } catch (NumberFormatException var3) {
         return -1;
      }
   }

   public long getServerPing() {
      return this.serverPing;
   }
}
