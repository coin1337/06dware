package me.earth.phobos.features.modules.player;

import java.awt.Color;
import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.BlockEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speedmine extends Module {
   public Setting<Boolean> tweaks = this.register(new Setting("Tweaks", true));
   public Setting<Speedmine.Mode> mode;
   public Setting<Boolean> reset;
   public Setting<Float> damage;
   public Setting<Boolean> noBreakAnim;
   public Setting<Boolean> noDelay;
   public Setting<Boolean> noSwing;
   public Setting<Boolean> noTrace;
   public Setting<Boolean> allow;
   public Setting<Boolean> pickaxe;
   public Setting<Boolean> doubleBreak;
   public Setting<Boolean> webSwitch;
   public Setting<Boolean> silentSwitch;
   public Setting<Boolean> render;
   public Setting<Boolean> box;
   public Setting<Boolean> outline;
   private final Setting<Integer> boxAlpha;
   private final Setting<Float> lineWidth;
   private final Setting<Float> range;
   private static Speedmine INSTANCE = new Speedmine();
   public BlockPos currentPos;
   public IBlockState currentBlockState;
   private final Timer timer;
   private boolean isMining;
   private BlockPos lastPos;
   private EnumFacing lastFacing;

   public Speedmine() {
      super("Speedmine", "Speeds up mining.", Module.Category.PLAYER, true, false, false);
      this.mode = this.register(new Setting("Mode", Speedmine.Mode.PACKET, (v) -> {
         return (Boolean)this.tweaks.getValue();
      }));
      this.reset = this.register(new Setting("Reset", true));
      this.damage = this.register(new Setting("Damage", 0.7F, 0.0F, 1.0F, (v) -> {
         return this.mode.getValue() == Speedmine.Mode.DAMAGE && (Boolean)this.tweaks.getValue();
      }));
      this.noBreakAnim = this.register(new Setting("NoBreakAnim", false));
      this.noDelay = this.register(new Setting("NoDelay", false));
      this.noSwing = this.register(new Setting("NoSwing", false));
      this.noTrace = this.register(new Setting("NoTrace", false));
      this.allow = this.register(new Setting("AllowMultiTask", false));
      this.pickaxe = this.register(new Setting("Pickaxe", true, (v) -> {
         return (Boolean)this.noTrace.getValue();
      }));
      this.doubleBreak = this.register(new Setting("DoubleBreak", false));
      this.webSwitch = this.register(new Setting("WebSwitch", false));
      this.silentSwitch = this.register(new Setting("SilentSwitch", false));
      this.render = this.register(new Setting("Render", false));
      this.box = this.register(new Setting("Box", false, (v) -> {
         return (Boolean)this.render.getValue();
      }));
      this.outline = this.register(new Setting("Outline", true, (v) -> {
         return (Boolean)this.render.getValue();
      }));
      this.boxAlpha = this.register(new Setting("BoxAlpha", 85, 0, 255, (v) -> {
         return (Boolean)this.box.getValue() && (Boolean)this.render.getValue();
      }));
      this.lineWidth = this.register(new Setting("LineWidth", 1.0F, 0.1F, 5.0F, (v) -> {
         return (Boolean)this.outline.getValue() && (Boolean)this.render.getValue();
      }));
      this.range = this.register(new Setting("Range", 10.0F, 0.0F, 50.0F));
      this.timer = new Timer();
      this.isMining = false;
      this.lastPos = null;
      this.lastFacing = null;
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Speedmine getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Speedmine();
      }

      return INSTANCE;
   }

   public void onTick() {
      if (this.currentPos != null) {
         if (mc.field_71439_g != null && mc.field_71439_g.func_174818_b(this.currentPos) > MathUtil.square((Float)this.range.getValue())) {
            this.currentPos = null;
            this.currentBlockState = null;
            return;
         }

         if (mc.field_71439_g != null && (Boolean)this.silentSwitch.getValue() && this.timer.passedMs((long)((int)(2000.0F * Phobos.serverManager.getTpsFactor()))) && this.getPickSlot() != -1) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(this.getPickSlot()));
         }

         if (mc.field_71441_e.func_180495_p(this.currentPos).equals(this.currentBlockState) && mc.field_71441_e.func_180495_p(this.currentPos).func_177230_c() != Blocks.field_150350_a) {
            if ((Boolean)this.webSwitch.getValue() && this.currentBlockState.func_177230_c() == Blocks.field_150321_G && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemPickaxe) {
               InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
            }
         } else {
            this.currentPos = null;
            this.currentBlockState = null;
         }
      }

   }

   public void onUpdate() {
      if (!fullNullCheck()) {
         if ((Boolean)this.noDelay.getValue()) {
            mc.field_71442_b.field_78781_i = 0;
         }

         if (this.isMining && this.lastPos != null && this.lastFacing != null && (Boolean)this.noBreakAnim.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
         }

         if ((Boolean)this.reset.getValue() && mc.field_71474_y.field_74313_G.func_151470_d() && !(Boolean)this.allow.getValue()) {
            mc.field_71442_b.field_78778_j = false;
         }

      }
   }

   public void onRender3D(Render3DEvent event) {
      if ((Boolean)this.render.getValue() && this.currentPos != null) {
         Color color = new Color(this.timer.passedMs((long)((int)(2000.0F * Phobos.serverManager.getTpsFactor()))) ? 0 : 255, this.timer.passedMs((long)((int)(2000.0F * Phobos.serverManager.getTpsFactor()))) ? 255 : 0, 0, 255);
         RenderUtil.drawBoxESP(this.currentPos, color, false, color, (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), false);
      }

   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (!fullNullCheck()) {
         if (event.getStage() == 0) {
            if ((Boolean)this.noSwing.getValue() && event.getPacket() instanceof CPacketAnimation) {
               event.setCanceled(true);
            }

            if ((Boolean)this.noBreakAnim.getValue() && event.getPacket() instanceof CPacketPlayerDigging) {
               CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
               if (packet != null && packet.func_179715_a() != null) {
                  try {
                     Iterator var3 = mc.field_71441_e.func_72839_b((Entity)null, new AxisAlignedBB(packet.func_179715_a())).iterator();

                     while(var3.hasNext()) {
                        Entity entity = (Entity)var3.next();
                        if (entity instanceof EntityEnderCrystal) {
                           this.showAnimation();
                           return;
                        }
                     }
                  } catch (Exception var5) {
                  }

                  if (packet.func_180762_c().equals(Action.START_DESTROY_BLOCK)) {
                     this.showAnimation(true, packet.func_179715_a(), packet.func_179714_b());
                  }

                  if (packet.func_180762_c().equals(Action.STOP_DESTROY_BLOCK)) {
                     this.showAnimation();
                  }
               }
            }
         }

      }
   }

   @SubscribeEvent
   public void onBlockEvent(BlockEvent event) {
      if (!fullNullCheck()) {
         if (event.getStage() == 3 && (Boolean)this.reset.getValue() && mc.field_71442_b.field_78770_f > 0.1F) {
            mc.field_71442_b.field_78778_j = true;
         }

         if (event.getStage() == 4 && (Boolean)this.tweaks.getValue()) {
            if (BlockUtil.canBreak(event.pos)) {
               if ((Boolean)this.reset.getValue()) {
                  mc.field_71442_b.field_78778_j = false;
               }

               switch((Speedmine.Mode)this.mode.getValue()) {
               case PACKET:
                  if (this.currentPos == null) {
                     this.currentPos = event.pos;
                     this.currentBlockState = mc.field_71441_e.func_180495_p(this.currentPos);
                     this.timer.reset();
                  }

                  mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, event.pos, event.facing));
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                  event.setCanceled(true);
                  break;
               case DAMAGE:
                  if (mc.field_71442_b.field_78770_f >= (Float)this.damage.getValue()) {
                     mc.field_71442_b.field_78770_f = 1.0F;
                  }
                  break;
               case INSTANT:
                  mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, event.pos, event.facing));
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                  mc.field_71442_b.func_187103_a(event.pos);
                  mc.field_71441_e.func_175698_g(event.pos);
               }
            }

            if ((Boolean)this.doubleBreak.getValue()) {
               BlockPos above = event.pos.func_177982_a(0, 1, 0);
               if (BlockUtil.canBreak(above) && mc.field_71439_g.func_70011_f((double)above.func_177958_n(), (double)above.func_177956_o(), (double)above.func_177952_p()) <= 5.0D) {
                  mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, above, event.facing));
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, above, event.facing));
                  mc.field_71442_b.func_187103_a(above);
                  mc.field_71441_e.func_175698_g(above);
               }
            }
         }

      }
   }

   private int getPickSlot() {
      for(int i = 0; i < 9; ++i) {
         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_151046_w) {
            return i;
         }
      }

      return -1;
   }

   private void showAnimation(boolean isMining, BlockPos lastPos, EnumFacing lastFacing) {
      this.isMining = isMining;
      this.lastPos = lastPos;
      this.lastFacing = lastFacing;
   }

   public void showAnimation() {
      this.showAnimation(false, (BlockPos)null, (EnumFacing)null);
   }

   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   public static enum Mode {
      PACKET,
      DAMAGE,
      INSTANT;
   }
}
