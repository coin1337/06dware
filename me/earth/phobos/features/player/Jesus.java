package me.earth.phobos.features.modules.player;

import me.earth.phobos.event.events.JesusEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Jesus extends Module {
   public Setting<Jesus.Mode> mode;
   public Setting<Boolean> cancelVehicle;
   public Setting<Jesus.EventMode> eventMode;
   public Setting<Boolean> fall;
   public static AxisAlignedBB offset = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9999D, 1.0D);
   private static Jesus INSTANCE = new Jesus();
   private boolean grounded;

   public Jesus() {
      super("Jesus", "Allows you to walk on water", Module.Category.PLAYER, true, false, false);
      this.mode = this.register(new Setting("Mode", Jesus.Mode.NORMAL));
      this.cancelVehicle = this.register(new Setting("NoVehicle", false));
      this.eventMode = this.register(new Setting("Jump", Jesus.EventMode.PRE, (v) -> {
         return this.mode.getValue() == Jesus.Mode.TRAMPOLINE;
      }));
      this.fall = this.register(new Setting("NoFall", false, (v) -> {
         return this.mode.getValue() == Jesus.Mode.TRAMPOLINE;
      }));
      this.grounded = false;
      INSTANCE = this;
   }

   public static Jesus getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Jesus();
      }

      return INSTANCE;
   }

   @SubscribeEvent
   public void updateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (!fullNullCheck() && !Freecam.getInstance().isOn()) {
         if (event.getStage() == 0 && (this.mode.getValue() == Jesus.Mode.BOUNCE || this.mode.getValue() == Jesus.Mode.VANILLA || this.mode.getValue() == Jesus.Mode.NORMAL) && !mc.field_71439_g.func_70093_af() && !mc.field_71439_g.field_70145_X && !mc.field_71474_y.field_74314_A.func_151470_d() && EntityUtil.isInLiquid()) {
            mc.field_71439_g.field_70181_x = 0.10000000149011612D;
         }

         if (event.getStage() != 0 || this.mode.getValue() != Jesus.Mode.TRAMPOLINE || this.eventMode.getValue() != Jesus.EventMode.ALL && this.eventMode.getValue() != Jesus.EventMode.PRE) {
            if (event.getStage() == 1 && this.mode.getValue() == Jesus.Mode.TRAMPOLINE && (this.eventMode.getValue() == Jesus.EventMode.ALL || this.eventMode.getValue() == Jesus.EventMode.POST)) {
               this.doTrampoline();
            }
         } else {
            this.doTrampoline();
         }

      }
   }

   @SubscribeEvent
   public void sendPacket(PacketEvent.Send event) {
      if (event.getPacket() instanceof CPacketPlayer && Freecam.getInstance().isOff() && (this.mode.getValue() == Jesus.Mode.BOUNCE || this.mode.getValue() == Jesus.Mode.NORMAL) && mc.field_71439_g.func_184187_bx() == null && !mc.field_71474_y.field_74314_A.func_151470_d()) {
         CPacketPlayer packet = (CPacketPlayer)event.getPacket();
         if (!EntityUtil.isInLiquid() && EntityUtil.isOnLiquid(0.05000000074505806D) && EntityUtil.checkCollide() && mc.field_71439_g.field_70173_aa % 3 == 0) {
            packet.field_149477_b -= 0.05000000074505806D;
         }
      }

   }

   @SubscribeEvent
   public void onLiquidCollision(JesusEvent event) {
      if (!fullNullCheck() && !Freecam.getInstance().isOn()) {
         if (event.getStage() == 0 && (this.mode.getValue() == Jesus.Mode.BOUNCE || this.mode.getValue() == Jesus.Mode.VANILLA || this.mode.getValue() == Jesus.Mode.NORMAL) && mc.field_71441_e != null && mc.field_71439_g != null && EntityUtil.checkCollide() && !(mc.field_71439_g.field_70181_x >= 0.10000000149011612D) && (double)event.getPos().func_177956_o() < mc.field_71439_g.field_70163_u - 0.05000000074505806D) {
            if (mc.field_71439_g.func_184187_bx() != null) {
               event.setBoundingBox(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.949999988079071D, 1.0D));
            } else {
               event.setBoundingBox(Block.field_185505_j);
            }

            event.setCanceled(true);
         }

      }
   }

   @SubscribeEvent
   public void onPacketReceived(PacketEvent.Receive event) {
      if ((Boolean)this.cancelVehicle.getValue() && event.getPacket() instanceof SPacketMoveVehicle) {
         event.setCanceled(true);
      }

   }

   public String getDisplayInfo() {
      return this.mode.getValue() == Jesus.Mode.NORMAL ? null : this.mode.currentEnumName();
   }

   private void doTrampoline() {
      if (!mc.field_71439_g.func_70093_af()) {
         if (EntityUtil.isAboveLiquid(mc.field_71439_g) && !mc.field_71439_g.func_70093_af() && !mc.field_71474_y.field_74314_A.field_74513_e) {
            mc.field_71439_g.field_70181_x = 0.1D;
         } else {
            if (mc.field_71439_g.field_70122_E || mc.field_71439_g.func_70617_f_()) {
               this.grounded = false;
            }

            EntityPlayerSP var10000;
            if (mc.field_71439_g.field_70181_x > 0.0D) {
               if (mc.field_71439_g.field_70181_x < 0.03D && this.grounded) {
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x += 0.06713D;
               } else if (mc.field_71439_g.field_70181_x <= 0.05D && this.grounded) {
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x *= 1.20000000999D;
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x += 0.06D;
               } else if (mc.field_71439_g.field_70181_x <= 0.08D && this.grounded) {
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x *= 1.20000003D;
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x += 0.055D;
               } else if (mc.field_71439_g.field_70181_x <= 0.112D && this.grounded) {
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x += 0.0535D;
               } else if (this.grounded) {
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x *= 1.000000000002D;
                  var10000 = mc.field_71439_g;
                  var10000.field_70181_x += 0.0517D;
               }
            }

            if (this.grounded && mc.field_71439_g.field_70181_x < 0.0D && mc.field_71439_g.field_70181_x > -0.3D) {
               var10000 = mc.field_71439_g;
               var10000.field_70181_x += 0.045835D;
            }

            if (!(Boolean)this.fall.getValue()) {
               mc.field_71439_g.field_70143_R = 0.0F;
            }

            if (EntityUtil.checkForLiquid(mc.field_71439_g, true)) {
               if (EntityUtil.checkForLiquid(mc.field_71439_g, true)) {
                  mc.field_71439_g.field_70181_x = 0.5D;
               }

               this.grounded = true;
            }
         }
      }
   }

   public static enum Mode {
      TRAMPOLINE,
      BOUNCE,
      VANILLA,
      NORMAL;
   }

   public static enum EventMode {
      PRE,
      POST,
      ALL;
   }
}
