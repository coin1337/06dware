package me.earth.phobos.util;

import java.util.Iterator;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class DamageUtil implements Util {
   public static boolean isArmorLow(EntityPlayer player, int durability) {
      Iterator var2 = player.field_71071_by.field_70460_b.iterator();

      ItemStack piece;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         piece = (ItemStack)var2.next();
         if (piece == null) {
            return true;
         }
      } while(getItemDamage(piece) >= durability);

      return true;
   }

   public static boolean isNaked(EntityPlayer player) {
      Iterator var1 = player.field_71071_by.field_70460_b.iterator();

      ItemStack piece;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         piece = (ItemStack)var1.next();
      } while(piece == null || piece.func_190926_b());

      return false;
   }

   public static int getItemDamage(ItemStack stack) {
      return stack.func_77958_k() - stack.func_77952_i();
   }

   public static float getDamageInPercent(ItemStack stack) {
      return (float)getItemDamage(stack) / (float)stack.func_77958_k() * 100.0F;
   }

   public static int getRoundedDamage(ItemStack stack) {
      return (int)getDamageInPercent(stack);
   }

   public static boolean hasDurability(ItemStack stack) {
      Item item = stack.func_77973_b();
      return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
   }

   public static boolean canBreakWeakness(EntityPlayer player) {
      int strengthAmp = 0;
      PotionEffect effect = mc.field_71439_g.func_70660_b(MobEffects.field_76420_g);
      if (effect != null) {
         strengthAmp = effect.func_76458_c();
      }

      return !mc.field_71439_g.func_70644_a(MobEffects.field_76437_t) || strengthAmp >= 1 || mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword || mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemPickaxe || mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemAxe || mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSpade;
   }

   public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
      float doubleExplosionSize = 12.0F;
      double distancedsize = entity.func_70011_f(posX, posY, posZ) / (double)doubleExplosionSize;
      Vec3d vec3d = new Vec3d(posX, posY, posZ);
      double blockDensity = 0.0D;

      try {
         blockDensity = (double)entity.field_70170_p.func_72842_a(vec3d, entity.func_174813_aQ());
      } catch (Exception var18) {
      }

      double v = (1.0D - distancedsize) * blockDensity;
      float damage = (float)((int)((v * v + v) / 2.0D * 7.0D * (double)doubleExplosionSize + 1.0D));
      double finald = 1.0D;
      if (entity instanceof EntityLivingBase) {
         finald = (double)getBlastReduction((EntityLivingBase)entity, getDamageMultiplied(damage), new Explosion(mc.field_71441_e, (Entity)null, posX, posY, posZ, 6.0F, false, true));
      }

      return (float)finald;
   }

   public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
      float damage;
      if (entity instanceof EntityPlayer) {
         EntityPlayer ep = (EntityPlayer)entity;
         DamageSource ds = DamageSource.func_94539_a(explosion);
         damage = CombatRules.func_189427_a(damageI, (float)ep.func_70658_aO(), (float)ep.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
         int k = 0;

         try {
            k = EnchantmentHelper.func_77508_a(ep.func_184193_aE(), ds);
         } catch (Exception var8) {
         }

         float f = MathHelper.func_76131_a((float)k, 0.0F, 20.0F);
         damage *= 1.0F - f / 25.0F;
         if (entity.func_70644_a(MobEffects.field_76429_m)) {
            damage -= damage / 4.0F;
         }

         damage = Math.max(damage, 0.0F);
         return damage;
      } else {
         damage = CombatRules.func_189427_a(damageI, (float)entity.func_70658_aO(), (float)entity.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
         return damage;
      }
   }

   public static float getDamageMultiplied(float damage) {
      int diff = mc.field_71441_e.func_175659_aa().func_151525_a();
      return damage * (diff == 0 ? 0.0F : (diff == 2 ? 1.0F : (diff == 1 ? 0.5F : 1.5F)));
   }

   public static float calculateDamage(Entity crystal, Entity entity) {
      return calculateDamage(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, entity);
   }

   public static float calculateDamage(BlockPos pos, Entity entity) {
      return calculateDamage((double)pos.func_177958_n() + 0.5D, (double)(pos.func_177956_o() + 1), (double)pos.func_177952_p() + 0.5D, entity);
   }

   public static boolean canTakeDamage(boolean suicide) {
      return !mc.field_71439_g.field_71075_bZ.field_75098_d && !suicide;
   }

   public static int getCooldownByWeapon(EntityPlayer player) {
      Item item = player.func_184614_ca().func_77973_b();
      if (item instanceof ItemSword) {
         return 600;
      } else if (item instanceof ItemPickaxe) {
         return 850;
      } else if (item == Items.field_151036_c) {
         return 1100;
      } else if (item == Items.field_151018_J) {
         return 500;
      } else if (item == Items.field_151019_K) {
         return 350;
      } else if (item != Items.field_151053_p && item != Items.field_151049_t) {
         return !(item instanceof ItemSpade) && item != Items.field_151006_E && item != Items.field_151056_x && item != Items.field_151017_I && item != Items.field_151013_M ? 250 : 1000;
      } else {
         return 1250;
      }
   }
}
