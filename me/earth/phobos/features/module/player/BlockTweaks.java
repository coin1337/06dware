package me.earth.phobos.features.modules.player;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockTweaks extends Module {
   public Setting<Boolean> autoTool = this.register(new Setting("AutoTool", false));
   public Setting<Boolean> autoWeapon = this.register(new Setting("AutoWeapon", false));
   public Setting<Boolean> noFriendAttack = this.register(new Setting("NoFriendAttack", false));
   public Setting<Boolean> noBlock = this.register(new Setting("NoHitboxBlock", true));
   public Setting<Boolean> noGhost = this.register(new Setting("NoGlitchBlocks", false));
   public Setting<Boolean> destroy = this.register(new Setting("Destroy", false, (v) -> {
      return (Boolean)this.noGhost.getValue();
   }));
   private static BlockTweaks INSTANCE = new BlockTweaks();
   private int lastHotbarSlot = -1;
   private int currentTargetSlot = -1;
   private boolean switched = false;

   public BlockTweaks() {
      super("BlockTweaks", "Some tweaks for blocks.", Module.Category.PLAYER, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static BlockTweaks getINSTANCE() {
      if (INSTANCE == null) {
         INSTANCE = new BlockTweaks();
      }

      return INSTANCE;
   }

   public void onDisable() {
      if (this.switched) {
         this.equip(this.lastHotbarSlot, false);
      }

      this.lastHotbarSlot = -1;
      this.currentTargetSlot = -1;
   }

   @SubscribeEvent
   public void onBreak(BreakEvent event) {
      if (!fullNullCheck() && (Boolean)this.noGhost.getValue() && (Boolean)this.destroy.getValue()) {
         if (!(mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock)) {
            BlockPos pos = mc.field_71439_g.func_180425_c();
            this.removeGlitchBlocks(pos);
         }

      }
   }

   @SubscribeEvent
   public void onBlockInteract(LeftClickBlock event) {
      if ((Boolean)this.autoTool.getValue() && (Speedmine.getInstance().mode.getValue() != Speedmine.Mode.PACKET || Speedmine.getInstance().isOff() || !(Boolean)Speedmine.getInstance().tweaks.getValue()) && !fullNullCheck() && event.getPos() != null) {
         this.equipBestTool(mc.field_71441_e.func_180495_p(event.getPos()));
      }

   }

   @SubscribeEvent
   public void onAttack(AttackEntityEvent event) {
      if ((Boolean)this.autoWeapon.getValue() && !fullNullCheck() && event.getTarget() != null) {
         this.equipBestWeapon(event.getTarget());
      }

   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (!fullNullCheck()) {
         if ((Boolean)this.noFriendAttack.getValue() && event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
            Entity entity = packet.func_149564_a(mc.field_71441_e);
            if (entity != null && Phobos.friendManager.isFriend(entity.func_70005_c_())) {
               event.setCanceled(true);
            }
         }

      }
   }

   public void onUpdate() {
      if (!fullNullCheck()) {
         if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != this.currentTargetSlot) {
            this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         }

         if (!mc.field_71474_y.field_74312_F.func_151470_d() && this.switched) {
            this.equip(this.lastHotbarSlot, false);
         }
      }

   }

   private void removeGlitchBlocks(BlockPos pos) {
      for(int dx = -4; dx <= 4; ++dx) {
         for(int dy = -4; dy <= 4; ++dy) {
            for(int dz = -4; dz <= 4; ++dz) {
               BlockPos blockPos = new BlockPos(pos.func_177958_n() + dx, pos.func_177956_o() + dy, pos.func_177952_p() + dz);
               if (mc.field_71441_e.func_180495_p(blockPos).func_177230_c().equals(Blocks.field_150350_a)) {
                  mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, blockPos, EnumFacing.DOWN, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND);
               }
            }
         }
      }

   }

   private void equipBestTool(IBlockState blockState) {
      int bestSlot = -1;
      double max = 0.0D;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (!stack.field_190928_g) {
            float speed = stack.func_150997_a(blockState);
            if (speed > 1.0F) {
               int eff;
               speed = (float)((double)speed + ((eff = EnchantmentHelper.func_77506_a(Enchantments.field_185305_q, stack)) > 0 ? Math.pow((double)eff, 2.0D) + 1.0D : 0.0D));
               if ((double)speed > max) {
                  max = (double)speed;
                  bestSlot = i;
               }
            }
         }
      }

      this.equip(bestSlot, true);
   }

   public void equipBestWeapon(Entity entity) {
      int bestSlot = -1;
      double maxDamage = 0.0D;
      EnumCreatureAttribute creatureAttribute = EnumCreatureAttribute.UNDEFINED;
      if (EntityUtil.isLiving(entity)) {
         EntityLivingBase base = (EntityLivingBase)entity;
         creatureAttribute = base.func_70668_bt();
      }

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (!stack.field_190928_g) {
            double damage;
            if (stack.func_77973_b() instanceof ItemTool) {
               damage = (double)((ItemTool)stack.func_77973_b()).field_77865_bY + (double)EnchantmentHelper.func_152377_a(stack, creatureAttribute);
               if (damage > maxDamage) {
                  maxDamage = damage;
                  bestSlot = i;
               }
            } else if (stack.func_77973_b() instanceof ItemSword) {
               damage = (double)((ItemSword)stack.func_77973_b()).func_150931_i() + (double)EnchantmentHelper.func_152377_a(stack, creatureAttribute);
               if (damage > maxDamage) {
                  maxDamage = damage;
                  bestSlot = i;
               }
            }
         }
      }

      this.equip(bestSlot, true);
   }

   private void equip(int slot, boolean equipTool) {
      if (slot != -1) {
         if (slot != mc.field_71439_g.field_71071_by.field_70461_c) {
            this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         }

         this.currentTargetSlot = slot;
         mc.field_71439_g.field_71071_by.field_70461_c = slot;
         mc.field_71442_b.func_78750_j();
         this.switched = equipTool;
      }

   }
}
