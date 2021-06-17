package me.earth.phobos.features.modules.player;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastPlace extends Module {
   private Setting<Boolean> all = this.register(new Setting("All", false));
   private Setting<Boolean> obby = this.register(new Setting("Obsidian", false, (v) -> {
      return !(Boolean)this.all.getValue();
   }));
   private Setting<Boolean> enderChests = this.register(new Setting("EnderChests", false, (v) -> {
      return !(Boolean)this.all.getValue();
   }));
   private Setting<Boolean> crystals = this.register(new Setting("Crystals", false, (v) -> {
      return !(Boolean)this.all.getValue();
   }));
   private Setting<Boolean> exp = this.register(new Setting("Experience", false, (v) -> {
      return !(Boolean)this.all.getValue();
   }));
   private Setting<Boolean> feetExp = this.register(new Setting("ExpFeet", false));
   private Setting<Boolean> fastCrystal = this.register(new Setting("PacketCrystal", false));
   private BlockPos mousePos = null;

   public FastPlace() {
      super("FastPlace", "Fast everything.", Module.Category.PLAYER, true, false, false);
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0 && (Boolean)this.feetExp.getValue()) {
         boolean mainHand = mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_151062_by;
         boolean offHand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151062_by;
         if (mc.field_71474_y.field_74313_G.func_151470_d() && (mc.field_71439_g.func_184600_cs() == EnumHand.MAIN_HAND && mainHand || mc.field_71439_g.func_184600_cs() == EnumHand.OFF_HAND && offHand)) {
            Phobos.rotationManager.lookAtVec3d(mc.field_71439_g.func_174791_d());
         }
      }

   }

   public void onUpdate() {
      if (!fullNullCheck()) {
         if (InventoryUtil.holdingItem(ItemExpBottle.class) && (Boolean)this.exp.getValue()) {
            mc.field_71467_ac = 0;
         }

         if (InventoryUtil.holdingItem(BlockObsidian.class) && (Boolean)this.obby.getValue()) {
            mc.field_71467_ac = 0;
         }

         if (InventoryUtil.holdingItem(BlockEnderChest.class) && (Boolean)this.enderChests.getValue()) {
            mc.field_71467_ac = 0;
         }

         if ((Boolean)this.all.getValue()) {
            mc.field_71467_ac = 0;
         }

         if (InventoryUtil.holdingItem(ItemEndCrystal.class) && ((Boolean)this.crystals.getValue() || (Boolean)this.all.getValue())) {
            mc.field_71467_ac = 0;
         }

         if ((Boolean)this.fastCrystal.getValue() && mc.field_71474_y.field_74313_G.func_151470_d()) {
            boolean offhand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
            if (offhand || mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP) {
               RayTraceResult result = mc.field_71476_x;
               if (result == null) {
                  return;
               }

               switch(result.field_72313_a) {
               case MISS:
                  this.mousePos = null;
                  break;
               case BLOCK:
                  this.mousePos = mc.field_71476_x.func_178782_a();
                  break;
               case ENTITY:
                  if (this.mousePos != null) {
                     Entity entity = result.field_72308_g;
                     if (entity != null && this.mousePos.equals(new BlockPos(entity.field_70165_t, entity.field_70163_u - 1.0D, entity.field_70161_v))) {
                        mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.mousePos, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                     }
                  }
               }
            }
         }

      }
   }
}
