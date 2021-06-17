package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.Util;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoFall extends Module {
   private Setting<NoFall.Mode> mode;
   private Setting<Integer> distance;
   private Setting<Boolean> glide;
   private Setting<Boolean> silent;
   private Setting<Boolean> bypass;
   private Timer timer;
   private boolean equipped;
   private boolean gotElytra;
   private NoFall.State currentState;
   private static Timer bypassTimer = new Timer();
   private static int ogslot = -1;

   public NoFall() {
      super("NoFall", "Prevents fall damage.", Module.Category.MOVEMENT, true, false, false);
      this.mode = this.register(new Setting("Mode", NoFall.Mode.PACKET));
      this.distance = this.register(new Setting("Distance", 15, 0, 50, (v) -> {
         return this.mode.getValue() == NoFall.Mode.BUCKET;
      }));
      this.glide = this.register(new Setting("Glide", false, (v) -> {
         return this.mode.getValue() == NoFall.Mode.ELYTRA;
      }));
      this.silent = this.register(new Setting("Silent", true, (v) -> {
         return this.mode.getValue() == NoFall.Mode.ELYTRA;
      }));
      this.bypass = this.register(new Setting("Bypass", false, (v) -> {
         return this.mode.getValue() == NoFall.Mode.ELYTRA;
      }));
      this.timer = new Timer();
      this.equipped = false;
      this.gotElytra = false;
      this.currentState = NoFall.State.FALL_CHECK;
   }

   public void onEnable() {
      ogslot = -1;
      this.currentState = NoFall.State.FALL_CHECK;
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (!fullNullCheck()) {
         if (this.mode.getValue() == NoFall.Mode.ELYTRA) {
            if ((Boolean)this.bypass.getValue()) {
               this.currentState = this.currentState.onSend(event);
            } else if (!this.equipped && event.getPacket() instanceof CPacketPlayer && mc.field_71439_g.field_70143_R >= 3.0F) {
               RayTraceResult result = null;
               if (!(Boolean)this.glide.getValue()) {
                  result = mc.field_71441_e.func_147447_a(mc.field_71439_g.func_174791_d(), mc.field_71439_g.func_174791_d().func_72441_c(0.0D, -3.0D, 0.0D), true, true, false);
               }

               if ((Boolean)this.glide.getValue() || result != null && result.field_72313_a == Type.BLOCK) {
                  if (mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b().equals(Items.field_185160_cR)) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
                  } else if ((Boolean)this.silent.getValue()) {
                     int slot = InventoryUtil.getItemHotbar(Items.field_185160_cR);
                     if (slot != -1) {
                        mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, 6, slot, ClickType.SWAP, mc.field_71439_g);
                        mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_FALL_FLYING));
                     }

                     ogslot = slot;
                     this.equipped = true;
                  }
               }
            }
         }

         if (this.mode.getValue() == NoFall.Mode.PACKET && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.field_149474_g = true;
         }

      }
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (!fullNullCheck()) {
         if ((this.equipped || (Boolean)this.bypass.getValue()) && this.mode.getValue() == NoFall.Mode.ELYTRA && (event.getPacket() instanceof SPacketWindowItems || event.getPacket() instanceof SPacketSetSlot)) {
            if ((Boolean)this.bypass.getValue()) {
               this.currentState = this.currentState.onReceive(event);
            } else {
               this.gotElytra = true;
            }
         }

      }
   }

   public void onUpdate() {
      if (!fullNullCheck()) {
         if (this.mode.getValue() == NoFall.Mode.ELYTRA) {
            if ((Boolean)this.bypass.getValue()) {
               this.currentState = this.currentState.onUpdate();
            } else if ((Boolean)this.silent.getValue() && this.equipped && this.gotElytra) {
               mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, 6, ogslot, ClickType.SWAP, mc.field_71439_g);
               mc.field_71442_b.func_78765_e();
               this.equipped = false;
               this.gotElytra = false;
            } else if ((Boolean)this.silent.getValue() && InventoryUtil.getItemHotbar(Items.field_185160_cR) == -1) {
               int slot = InventoryUtil.findStackInventory(Items.field_185160_cR);
               if (slot != -1 && ogslot != -1) {
                  System.out.println(String.format("Moving %d to hotbar %d", slot, ogslot));
                  mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, slot, ogslot, ClickType.SWAP, mc.field_71439_g);
                  mc.field_71442_b.func_78765_e();
               }
            }
         }

      }
   }

   public void onTick() {
      if (!fullNullCheck()) {
         if (this.mode.getValue() == NoFall.Mode.BUCKET && mc.field_71439_g.field_70143_R >= (float)(Integer)this.distance.getValue() && !EntityUtil.isAboveWater(mc.field_71439_g) && this.timer.passedMs(100L)) {
            Vec3d posVec = mc.field_71439_g.func_174791_d();
            RayTraceResult result = mc.field_71441_e.func_147447_a(posVec, posVec.func_72441_c(0.0D, -5.329999923706055D, 0.0D), true, true, false);
            if (result != null && result.field_72313_a == Type.BLOCK) {
               EnumHand hand = EnumHand.MAIN_HAND;
               if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151131_as) {
                  hand = EnumHand.OFF_HAND;
               } else if (mc.field_71439_g.func_184614_ca().func_77973_b() != Items.field_151131_as) {
                  for(int i = 0; i < 9; ++i) {
                     if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_151131_as) {
                        mc.field_71439_g.field_71071_by.field_70461_c = i;
                        mc.field_71439_g.field_70125_A = 90.0F;
                        this.timer.reset();
                        return;
                     }
                  }

                  return;
               }

               mc.field_71439_g.field_70125_A = 90.0F;
               mc.field_71442_b.func_187101_a(mc.field_71439_g, mc.field_71441_e, hand);
               this.timer.reset();
            }
         }

      }
   }

   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   public static enum State {
      FALL_CHECK {
         public NoFall.State onSend(PacketEvent.Send event) {
            RayTraceResult result = Util.mc.field_71441_e.func_147447_a(Util.mc.field_71439_g.func_174791_d(), Util.mc.field_71439_g.func_174791_d().func_72441_c(0.0D, -3.0D, 0.0D), true, true, false);
            if (event.getPacket() instanceof CPacketPlayer && Util.mc.field_71439_g.field_70143_R >= 3.0F && result != null && result.field_72313_a == Type.BLOCK) {
               int slot = InventoryUtil.getItemHotbar(Items.field_185160_cR);
               if (slot != -1) {
                  Util.mc.field_71442_b.func_187098_a(Util.mc.field_71439_g.field_71069_bz.field_75152_c, 6, slot, ClickType.SWAP, Util.mc.field_71439_g);
                  NoFall.ogslot = slot;
                  Util.mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(Util.mc.field_71439_g, Action.START_FALL_FLYING));
                  return WAIT_FOR_ELYTRA_DEQUIP;
               } else {
                  return this;
               }
            } else {
               return this;
            }
         }
      },
      WAIT_FOR_ELYTRA_DEQUIP {
         public NoFall.State onReceive(PacketEvent.Receive event) {
            return (NoFall.State)(!(event.getPacket() instanceof SPacketWindowItems) && !(event.getPacket() instanceof SPacketSetSlot) ? this : REEQUIP_ELYTRA);
         }
      },
      REEQUIP_ELYTRA {
         public NoFall.State onUpdate() {
            Util.mc.field_71442_b.func_187098_a(Util.mc.field_71439_g.field_71069_bz.field_75152_c, 6, NoFall.ogslot, ClickType.SWAP, Util.mc.field_71439_g);
            Util.mc.field_71442_b.func_78765_e();
            int slot = InventoryUtil.findStackInventory(Items.field_185160_cR, true);
            if (slot == -1) {
               Command.sendMessage("§cElytra not found after regain?");
               return WAIT_FOR_NEXT_REQUIP;
            } else {
               Util.mc.field_71442_b.func_187098_a(Util.mc.field_71439_g.field_71069_bz.field_75152_c, slot, NoFall.ogslot, ClickType.SWAP, Util.mc.field_71439_g);
               Util.mc.field_71442_b.func_78765_e();
               NoFall.bypassTimer.reset();
               return RESET_TIME;
            }
         }
      },
      WAIT_FOR_NEXT_REQUIP {
         public NoFall.State onUpdate() {
            return (NoFall.State)(NoFall.bypassTimer.passedMs(250L) ? REEQUIP_ELYTRA : this);
         }
      },
      RESET_TIME {
         public NoFall.State onUpdate() {
            if (!Util.mc.field_71439_g.field_70122_E && !NoFall.bypassTimer.passedMs(250L)) {
               return this;
            } else {
               Util.mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, new ItemStack(Blocks.field_150357_h), (short)1337));
               return FALL_CHECK;
            }
         }
      };

      private State() {
      }

      public NoFall.State onSend(PacketEvent.Send e) {
         return this;
      }

      public NoFall.State onReceive(PacketEvent.Receive e) {
         return this;
      }

      public NoFall.State onUpdate() {
         return this;
      }

      // $FF: synthetic method
      State(Object x2) {
         this();
      }
   }

   public static enum Mode {
      PACKET,
      BUCKET,
      ELYTRA;
   }
}
