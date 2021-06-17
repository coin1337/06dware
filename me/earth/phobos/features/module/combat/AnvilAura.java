package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AnvilAura extends Module {
   public Setting<Float> range = this.register(new Setting("Range", 6.0F, 0.0F, 10.0F));
   public Setting<Float> wallsRange = this.register(new Setting("WallsRange", 3.5F, 0.0F, 10.0F));
   public Setting<Integer> placeDelay = this.register(new Setting("PlaceDelay", 0, 0, 500));
   public Setting<Boolean> rotate = this.register(new Setting("Rotate", true));
   public Setting<Boolean> packet = this.register(new Setting("Packet", true));
   public Setting<Boolean> switcher = this.register(new Setting("Switch", true));
   public Setting<Integer> rotations = this.register(new Setting("Spoofs", 1, 1, 20));
   private float yaw = 0.0F;
   private float pitch = 0.0F;
   private boolean rotating = false;
   private int rotationPacketsSpoofed = 0;
   private EntityPlayer finalTarget;
   private BlockPos placeTarget;

   public AnvilAura() {
      super("AnvilAura", "Useless", Module.Category.COMBAT, true, false, false);
   }

   public void onTick() {
      this.doAnvilAura();
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getStage() == 0 && (Boolean)this.rotate.getValue() && this.rotating) {
         if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.field_149476_e = this.yaw;
            packet.field_149473_f = this.pitch;
         }

         ++this.rotationPacketsSpoofed;
         if (this.rotationPacketsSpoofed >= (Integer)this.rotations.getValue()) {
            this.rotating = false;
            this.rotationPacketsSpoofed = 0;
         }
      }

   }

   public void doAnvilAura() {
      this.finalTarget = this.getTarget();
      if (this.finalTarget != null) {
         this.placeTarget = this.getTargetPos(this.finalTarget);
      }

      if (this.placeTarget != null && this.finalTarget != null) {
         this.placeAnvil(this.placeTarget);
      }

   }

   public void placeAnvil(BlockPos pos) {
      if ((Boolean)this.rotate.getValue()) {
         this.rotateToPos(pos);
      }

      if ((Boolean)this.switcher.getValue() && !this.isHoldingAnvil()) {
         this.doSwitch();
      }

      BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, (Boolean)this.packet.getValue(), mc.field_71439_g.func_70093_af());
   }

   public boolean isHoldingAnvil() {
      int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
      return mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.func_184614_ca().func_77973_b()).func_179223_d() instanceof BlockAnvil || mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() instanceof BlockAnvil;
   }

   public void doSwitch() {
      int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
      if (obbySlot == -1) {
         for(int l = 0; l < 9; ++l) {
            ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(l);
            Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
            if (block instanceof BlockObsidian) {
               obbySlot = l;
            }
         }
      }

      if (obbySlot != -1) {
         mc.field_71439_g.field_71071_by.field_70461_c = obbySlot;
      }

   }

   public EntityPlayer getTarget() {
      double shortestDistance = -1.0D;
      EntityPlayer target = null;
      Iterator var4 = mc.field_71441_e.field_73010_i.iterator();

      while(true) {
         EntityPlayer player;
         do {
            do {
               if (!var4.hasNext()) {
                  return target;
               }

               player = (EntityPlayer)var4.next();
            } while(this.getPlaceableBlocksAboveEntity(player).isEmpty());
         } while(shortestDistance != -1.0D && !(mc.field_71439_g.func_70068_e(player) < MathUtil.square(shortestDistance)));

         shortestDistance = (double)mc.field_71439_g.func_70032_d(player);
         target = player;
      }
   }

   public BlockPos getTargetPos(Entity target) {
      double distance = -1.0D;
      BlockPos finalPos = null;
      Iterator var5 = this.getPlaceableBlocksAboveEntity(target).iterator();

      while(true) {
         BlockPos pos;
         do {
            if (!var5.hasNext()) {
               return finalPos;
            }

            pos = (BlockPos)var5.next();
         } while(distance != -1.0D && !(mc.field_71439_g.func_174818_b(pos) < MathUtil.square(distance)));

         finalPos = pos;
         distance = mc.field_71439_g.func_70011_f((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p());
      }
   }

   public List<BlockPos> getPlaceableBlocksAboveEntity(Entity target) {
      new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v));
      List<BlockPos> positions = new ArrayList();

      for(int i = (int)Math.floor(mc.field_71439_g.field_70163_u + 2.0D); i <= 256; ++i) {
         BlockPos pos = new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), (double)i, Math.floor(mc.field_71439_g.field_70161_v));
         if (BlockUtil.isPositionPlaceable(pos, false) == 0 || BlockUtil.isPositionPlaceable(pos, false) == -1 || BlockUtil.isPositionPlaceable(pos, false) == 2) {
            break;
         }

         positions.add(pos);
      }

      return positions;
   }

   private void rotateToPos(BlockPos pos) {
      if ((Boolean)this.rotate.getValue()) {
         float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5F), (double)((float)pos.func_177956_o() - 0.5F), (double)((float)pos.func_177952_p() + 0.5F)));
         this.yaw = angle[0];
         this.pitch = angle[1];
         this.rotating = true;
      }

   }
}
