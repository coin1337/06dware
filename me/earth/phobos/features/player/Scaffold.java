package me.earth.phobos.features.modules.player;

import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Scaffold extends Module {
   public Setting<Boolean> rotation = this.register(new Setting("Rotate", false));
   private final Timer timer = new Timer();

   public Scaffold() {
      super("Scaffold", "Places Blocks underneath you.", Module.Category.PLAYER, true, false, false);
   }

   public void onEnable() {
      this.timer.reset();
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayerPost(UpdateWalkingPlayerEvent event) {
      if (!this.isOff() && !fullNullCheck() && event.getStage() != 0) {
         if (!mc.field_71474_y.field_74314_A.func_151470_d()) {
            this.timer.reset();
         }

         BlockPos playerBlock = EntityUtil.getPlayerPosWithEntity();
         if (BlockUtil.isScaffoldPos(playerBlock.func_177982_a(0, -1, 0))) {
            if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -2, 0))) {
               this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.UP);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 0))) {
               this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.EAST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 0))) {
               this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.WEST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, -1))) {
               this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.SOUTH);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
               this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.NORTH);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
               if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
                  this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.NORTH);
               }

               this.place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.EAST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 1))) {
               if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 0))) {
                  this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.WEST);
               }

               this.place(playerBlock.func_177982_a(-1, -1, 1), EnumFacing.SOUTH);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
               if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
                  this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.SOUTH);
               }

               this.place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.WEST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
               if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
                  this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.EAST);
               }

               this.place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.NORTH);
            }
         }

      }
   }

   public void place(BlockPos posI, EnumFacing face) {
      BlockPos pos = posI;
      if (face == EnumFacing.UP) {
         pos = posI.func_177982_a(0, -1, 0);
      } else if (face == EnumFacing.NORTH) {
         pos = posI.func_177982_a(0, 0, 1);
      } else if (face == EnumFacing.SOUTH) {
         pos = posI.func_177982_a(0, 0, -1);
      } else if (face == EnumFacing.EAST) {
         pos = posI.func_177982_a(-1, 0, 0);
      } else if (face == EnumFacing.WEST) {
         pos = posI.func_177982_a(1, 0, 0);
      }

      int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
      int newSlot = -1;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (!InventoryUtil.isNull(stack) && stack.func_77973_b() instanceof ItemBlock && Block.func_149634_a(stack.func_77973_b()).func_176223_P().func_185913_b()) {
            newSlot = i;
            break;
         }
      }

      if (newSlot != -1) {
         boolean crouched = false;
         if (!mc.field_71439_g.func_70093_af()) {
            Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
            if (BlockUtil.blackList.contains(block)) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
               crouched = true;
            }
         }

         if (!(mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock)) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(newSlot));
            mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
            mc.field_71442_b.func_78765_e();
         }

         if (mc.field_71474_y.field_74314_A.func_151470_d()) {
            EntityPlayerSP var10000 = mc.field_71439_g;
            var10000.field_70159_w *= 0.3D;
            var10000 = mc.field_71439_g;
            var10000.field_70179_y *= 0.3D;
            mc.field_71439_g.func_70664_aZ();
            if (this.timer.passedMs(1500L)) {
               mc.field_71439_g.field_70181_x = -0.28D;
               this.timer.reset();
            }
         }

         if ((Boolean)this.rotation.getValue()) {
            float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5F), (double)((float)pos.func_177956_o() - 0.5F), (double)((float)pos.func_177952_p() + 0.5F)));
            mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(angle[0], (float)MathHelper.func_180184_b((int)angle[1], 360), mc.field_71439_g.field_70122_E));
         }

         mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, face, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND);
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(oldSlot));
         mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
         mc.field_71442_b.func_78765_e();
         if (crouched) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         }

      }
   }
}
