package me.earth.phobos.features.modules.combat;

import java.util.Objects;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.Timer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Criticals extends Module {
   private Setting<Criticals.Mode> mode;
   public Setting<Boolean> noDesync;
   private Setting<Integer> packets;
   private Setting<Integer> desyncDelay;
   public Setting<Boolean> cancelFirst;
   public Setting<Integer> delay32k;
   private Timer timer;
   private Timer timer32k;
   private boolean firstCanceled;
   private boolean resetTimer;

   public Criticals() {
      super("Criticals", "Scores criticals for you", Module.Category.COMBAT, true, false, false);
      this.mode = this.register(new Setting("Mode", Criticals.Mode.PACKET));
      this.noDesync = this.register(new Setting("NoDesync", true));
      this.packets = this.register(new Setting("Packets", 2, 1, 5, (v) -> {
         return this.mode.getValue() == Criticals.Mode.PACKET;
      }, "Amount of packets you want to send."));
      this.desyncDelay = this.register(new Setting("DesyncDelay", 10, 0, 500, (v) -> {
         return this.mode.getValue() == Criticals.Mode.PACKET;
      }, "Amount of packets you want to send."));
      this.cancelFirst = this.register(new Setting("CancelFirst32k", true));
      this.delay32k = this.register(new Setting("32kDelay", 25, 0, 500, (v) -> {
         return (Boolean)this.cancelFirst.getValue();
      }));
      this.timer = new Timer();
      this.timer32k = new Timer();
      this.firstCanceled = false;
      this.resetTimer = false;
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (Auto32k.getInstance().isOn() && (Auto32k.getInstance().switching || !(Boolean)Auto32k.getInstance().autoSwitch.getValue() || Auto32k.getInstance().mode.getValue() == Auto32k.Mode.DISPENSER) && this.timer.passedMs(500L) && (Boolean)this.cancelFirst.getValue()) {
         this.firstCanceled = true;
      } else if (Auto32k.getInstance().isOff() || !Auto32k.getInstance().switching && (Boolean)Auto32k.getInstance().autoSwitch.getValue() && Auto32k.getInstance().mode.getValue() != Auto32k.Mode.DISPENSER || !(Boolean)this.cancelFirst.getValue()) {
         this.firstCanceled = false;
      }

      if (event.getPacket() instanceof CPacketUseEntity) {
         CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
         if (packet.func_149565_c() == Action.ATTACK) {
            if (this.firstCanceled) {
               this.timer32k.reset();
               this.resetTimer = true;
               this.timer.setMs((long)((Integer)this.desyncDelay.getValue() + 1));
               this.firstCanceled = false;
               return;
            }

            if (this.resetTimer && !this.timer32k.passedMs((long)(Integer)this.delay32k.getValue())) {
               return;
            }

            if (this.resetTimer && this.timer32k.passedMs((long)(Integer)this.delay32k.getValue())) {
               this.resetTimer = false;
            }

            if (!this.timer.passedMs((long)(Integer)this.desyncDelay.getValue())) {
               return;
            }

            if (mc.field_71439_g.field_70122_E && !mc.field_71474_y.field_74314_A.func_151470_d() && (packet.func_149564_a(mc.field_71441_e) instanceof EntityLivingBase || !(Boolean)this.noDesync.getValue()) && !mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_180799_ab()) {
               if (this.mode.getValue() == Criticals.Mode.PACKET) {
                  switch((Integer)this.packets.getValue()) {
                  case 1:
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.10000000149011612D, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
                     break;
                  case 2:
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.0625101D, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.1E-5D, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
                     break;
                  case 3:
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.0625101D, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.0125D, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
                     break;
                  case 4:
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.05D, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.03D, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
                     break;
                  case 5:
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.1625D, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 4.0E-6D, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0E-6D, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayer());
                     mc.field_71439_g.func_71009_b((Entity)Objects.requireNonNull(packet.func_149564_a(mc.field_71441_e)));
                  }
               } else if (this.mode.getValue() == Criticals.Mode.BYPASS) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.11D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.1100013579D, mc.field_71439_g.field_70161_v, false));
                  mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.1100013579D, mc.field_71439_g.field_70161_v, false));
               } else {
                  mc.field_71439_g.func_70664_aZ();
                  if (this.mode.getValue() == Criticals.Mode.MINIJUMP) {
                     EntityPlayerSP var10000 = mc.field_71439_g;
                     var10000.field_70181_x /= 2.0D;
                  }
               }

               this.timer.reset();
            }
         }
      }

   }

   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   public static enum Mode {
      JUMP,
      MINIJUMP,
      PACKET,
      BYPASS;
   }
}
