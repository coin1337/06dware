package me.earth.phobos.features.modules.misc;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Bypass extends Module {
   public Setting<Boolean> illegals = this.register(new Setting("Illegals", false));
   public Setting<Boolean> secretClose = this.register(new Setting("SecretClose", false, (v) -> {
      return (Boolean)this.illegals.getValue();
   }));
   public Setting<Boolean> rotation = this.register(new Setting("Rotation", false, (v) -> {
      return (Boolean)this.secretClose.getValue() && (Boolean)this.illegals.getValue();
   }));
   public Setting<Boolean> elytra = this.register(new Setting("Elytra", false));
   public Setting<Boolean> reopen = this.register(new Setting("Reopen", false, (v) -> {
      return (Boolean)this.elytra.getValue();
   }));
   public Setting<Integer> reopen_interval = this.register(new Setting("ReopenDelay", 1000, 0, 5000, (v) -> {
      return (Boolean)this.elytra.getValue();
   }));
   public Setting<Integer> delay = this.register(new Setting("Delay", 0, 0, 1000, (v) -> {
      return (Boolean)this.elytra.getValue();
   }));
   public Setting<Boolean> allow_ghost = this.register(new Setting("Ghost", true, (v) -> {
      return (Boolean)this.elytra.getValue();
   }));
   public Setting<Boolean> cancel_close = this.register(new Setting("Cancel", true, (v) -> {
      return (Boolean)this.elytra.getValue();
   }));
   public Setting<Boolean> discreet = this.register(new Setting("Secret", true, (v) -> {
      return (Boolean)this.elytra.getValue();
   }));
   public Setting<Boolean> packets = this.register(new Setting("Packets", false));
   public Setting<Boolean> limitSwing = this.register(new Setting("LimitSwing", false, (v) -> {
      return (Boolean)this.packets.getValue();
   }));
   public Setting<Integer> swingPackets = this.register(new Setting("SwingPackets", 1, 0, 100, (v) -> {
      return (Boolean)this.packets.getValue();
   }));
   public Setting<Boolean> noLimit = this.register(new Setting("NoCompression", false, (v) -> {
      return (Boolean)this.packets.getValue();
   }));
   int cooldown = 0;
   private final Timer timer = new Timer();
   private float yaw;
   private float pitch;
   private boolean rotate;
   private BlockPos pos;
   private Timer swingTimer = new Timer();
   private int swingPacket = 0;
   private static Bypass instance;

   public Bypass() {
      super("Bypass", "Bypass for stuff", Module.Category.MISC, true, false, false);
      instance = this;
   }

   public static Bypass getInstance() {
      if (instance == null) {
         instance = new Bypass();
      }

      return instance;
   }

   public void onToggle() {
      this.swingPacket = 0;
   }

   @SubscribeEvent
   public void onGuiOpen(GuiOpenEvent event) {
      if (event.getGui() == null && (Boolean)this.secretClose.getValue() && (Boolean)this.rotation.getValue()) {
         this.pos = new BlockPos(mc.field_71439_g.func_174791_d());
         this.yaw = mc.field_71439_g.field_70177_z;
         this.pitch = mc.field_71439_g.field_70125_A;
         this.rotate = true;
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onPacketSend(PacketEvent.Send event) {
      if ((Boolean)this.illegals.getValue() && (Boolean)this.secretClose.getValue()) {
         if (event.getPacket() instanceof CPacketCloseWindow) {
            event.setCanceled(true);
         } else if (event.getPacket() instanceof CPacketPlayer && (Boolean)this.rotation.getValue() && this.rotate) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.field_149476_e = this.yaw;
            packet.field_149473_f = this.pitch;
         }
      }

      if ((Boolean)this.packets.getValue() && (Boolean)this.limitSwing.getValue() && event.getPacket() instanceof CPacketAnimation) {
         if (this.swingPacket > (Integer)this.swingPackets.getValue()) {
            event.setCanceled(true);
         }

         ++this.swingPacket;
      }

   }

   @SubscribeEvent
   public void onIncomingPacket(PacketEvent.Receive event) {
      if (!fullNullCheck() && (Boolean)this.elytra.getValue()) {
         if (event.getPacket() instanceof SPacketSetSlot) {
            SPacketSetSlot packet = (SPacketSetSlot)event.getPacket();
            if (packet.func_149173_d() == 6) {
               event.setCanceled(true);
            }

            if (!(Boolean)this.allow_ghost.getValue() && packet.func_149174_e().func_77973_b().equals(Items.field_185160_cR)) {
               event.setCanceled(true);
            }
         }

         if ((Boolean)this.cancel_close.getValue() && mc.field_71439_g.func_184613_cA() && event.getPacket() instanceof SPacketEntityMetadata) {
            SPacketEntityMetadata MetadataPacket = (SPacketEntityMetadata)event.getPacket();
            if (MetadataPacket.func_149375_d() == mc.field_71439_g.func_145782_y()) {
               event.setCanceled(true);
            }
         }
      }

      if (event.getPacket() instanceof SPacketCloseWindow) {
         this.rotate = false;
      }

   }

   public void onTick() {
      if ((Boolean)this.secretClose.getValue() && (Boolean)this.rotation.getValue() && this.rotate && this.pos != null && mc.field_71439_g != null && mc.field_71439_g.func_174818_b(this.pos) > 400.0D) {
         this.rotate = false;
      }

      if ((Boolean)this.elytra.getValue()) {
         if (this.cooldown > 0) {
            --this.cooldown;
         } else if (mc.field_71439_g != null && !(mc.field_71462_r instanceof GuiInventory) && (!mc.field_71439_g.field_70122_E || !(Boolean)this.discreet.getValue())) {
            for(int i = 0; i < 36; ++i) {
               ItemStack item = mc.field_71439_g.field_71071_by.func_70301_a(i);
               if (item.func_77973_b().equals(Items.field_185160_cR)) {
                  mc.field_71442_b.func_187098_a(0, i < 9 ? i + 36 : i, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
                  this.cooldown = (Integer)this.delay.getValue();
                  return;
               }
            }
         }
      }

   }

   public void onUpdate() {
      this.swingPacket = 0;
      if ((Boolean)this.elytra.getValue() && this.timer.passedMs((long)(Integer)this.reopen_interval.getValue()) && (Boolean)this.reopen.getValue() && !mc.field_71439_g.func_184613_cA() && mc.field_71439_g.field_70143_R > 0.0F) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
      }

   }
}
