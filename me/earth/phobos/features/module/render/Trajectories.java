package me.earth.phobos.features.modules.render;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSnowball;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

public class Trajectories extends Module {
   public Trajectories() {
      super("Trajectories", "Shows the way of projectiles.", Module.Category.RENDER, false, false, false);
   }

   public void onRender3D(Render3DEvent event) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         this.drawTrajectories(mc.field_71439_g, event.getPartialTicks());
      }
   }

   public void enableGL3D(float lineWidth) {
      GL11.glDisable(3008);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      GL11.glEnable(2884);
      mc.field_71460_t.func_175072_h();
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glHint(3155, 4354);
      GL11.glLineWidth(lineWidth);
   }

   public void disableGL3D() {
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDisable(3042);
      GL11.glEnable(3008);
      GL11.glDepthMask(true);
      GL11.glCullFace(1029);
      GL11.glDisable(2848);
      GL11.glHint(3154, 4352);
      GL11.glHint(3155, 4352);
   }

   private void drawTrajectories(EntityPlayer player, float partialTicks) {
      double renderPosX = player.field_70142_S + (player.field_70165_t - player.field_70142_S) * (double)partialTicks;
      double renderPosY = player.field_70137_T + (player.field_70163_u - player.field_70137_T) * (double)partialTicks;
      double renderPosZ = player.field_70136_U + (player.field_70161_v - player.field_70136_U) * (double)partialTicks;
      player.func_184586_b(EnumHand.MAIN_HAND);
      if (mc.field_71474_y.field_74320_O == 0 && (player.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() instanceof ItemBow || player.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() instanceof ItemFishingRod || player.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() instanceof ItemEnderPearl || player.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() instanceof ItemEgg || player.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() instanceof ItemSnowball || player.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() instanceof ItemExpBottle)) {
         GL11.glPushMatrix();
         Item item = player.func_184586_b(EnumHand.MAIN_HAND).func_77973_b();
         double posX = renderPosX - (double)(MathHelper.func_76134_b(player.field_70177_z / 180.0F * 3.1415927F) * 0.16F);
         double posY = renderPosY + (double)player.func_70047_e() - 0.1000000014901161D;
         double posZ = renderPosZ - (double)(MathHelper.func_76126_a(player.field_70177_z / 180.0F * 3.1415927F) * 0.16F);
         double motionX = (double)(-MathHelper.func_76126_a(player.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(player.field_70125_A / 180.0F * 3.1415927F)) * (item instanceof ItemBow ? 1.0D : 0.4D);
         double motionY = (double)(-MathHelper.func_76126_a(player.field_70125_A / 180.0F * 3.1415927F)) * (item instanceof ItemBow ? 1.0D : 0.4D);
         double motionZ = (double)(MathHelper.func_76134_b(player.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(player.field_70125_A / 180.0F * 3.1415927F)) * (item instanceof ItemBow ? 1.0D : 0.4D);
         int var6 = 72000 - player.func_184605_cv();
         float power = (float)var6 / 20.0F;
         power = (power * power + power * 2.0F) / 3.0F;
         if (power > 1.0F) {
            power = 1.0F;
         }

         float distance = MathHelper.func_76133_a(motionX * motionX + motionY * motionY + motionZ * motionZ);
         motionX /= (double)distance;
         motionY /= (double)distance;
         motionZ /= (double)distance;
         float pow = item instanceof ItemBow ? power * 2.0F : (item instanceof ItemFishingRod ? 1.25F : (player.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() == Items.field_151062_by ? 0.9F : 1.0F));
         motionX *= (double)(pow * (item instanceof ItemFishingRod ? 0.75F : (player.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() == Items.field_151062_by ? 0.75F : 1.5F)));
         motionY *= (double)(pow * (item instanceof ItemFishingRod ? 0.75F : (player.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() == Items.field_151062_by ? 0.75F : 1.5F)));
         motionZ *= (double)(pow * (item instanceof ItemFishingRod ? 0.75F : (player.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() == Items.field_151062_by ? 0.75F : 1.5F)));
         this.enableGL3D(2.0F);
         if (power > 0.6F) {
            GlStateManager.func_179131_c(0.0F, 1.0F, 0.0F, 1.0F);
         } else {
            GlStateManager.func_179131_c(0.8F, 0.5F, 0.0F, 1.0F);
         }

         GL11.glEnable(2848);
         float size = (float)(item instanceof ItemBow ? 0.3D : 0.25D);
         boolean hasLanded = false;
         Entity landingOnEntity = null;

         RayTraceResult landingPosition;
         for(landingPosition = null; !hasLanded && posY > 0.0D; motionY -= item instanceof ItemBow ? 0.05D : 0.03D) {
            Vec3d present = new Vec3d(posX, posY, posZ);
            Vec3d future = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            RayTraceResult possibleLandingStrip = mc.field_71441_e.func_147447_a(present, future, false, true, false);
            if (possibleLandingStrip != null && possibleLandingStrip.field_72313_a != Type.MISS) {
               landingPosition = possibleLandingStrip;
               hasLanded = true;
            }

            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - (double)size, posY - (double)size, posZ - (double)size, posX + (double)size, posY + (double)size, posZ + (double)size);
            List entities = this.getEntitiesWithinAABB(arrowBox.func_72317_d(motionX, motionY, motionZ).func_72321_a(1.0D, 1.0D, 1.0D));
            Iterator var35 = entities.iterator();

            while(var35.hasNext()) {
               Object entity = var35.next();
               Entity boundingBox = (Entity)entity;
               if (boundingBox.func_70067_L() && boundingBox != player) {
                  float var7 = 0.3F;
                  AxisAlignedBB var8 = boundingBox.func_174813_aQ().func_72321_a((double)var7, (double)var7, (double)var7);
                  RayTraceResult possibleEntityLanding = var8.func_72327_a(present, future);
                  if (possibleEntityLanding != null) {
                     hasLanded = true;
                     landingOnEntity = boundingBox;
                     landingPosition = possibleEntityLanding;
                  }
               }
            }

            if (landingOnEntity != null) {
               GlStateManager.func_179131_c(1.0F, 0.0F, 0.0F, 1.0F);
            }

            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            float motionAdjustment = 0.99F;
            motionX *= (double)motionAdjustment;
            motionY *= (double)motionAdjustment;
            motionZ *= (double)motionAdjustment;
         }

         if (landingPosition != null && landingPosition.field_72313_a == Type.BLOCK) {
            GlStateManager.func_179137_b(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);
            int side = landingPosition.field_178784_b.func_176745_a();
            if (side == 2) {
               GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
            } else if (side == 3) {
               GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
            } else if (side == 4) {
               GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
            } else if (side == 5) {
               GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
            }

            Cylinder c = new Cylinder();
            GlStateManager.func_179114_b(-90.0F, 1.0F, 0.0F, 0.0F);
            c.setDrawStyle(100011);
            if (landingOnEntity != null) {
               GlStateManager.func_179131_c(0.0F, 0.0F, 0.0F, 1.0F);
               GL11.glLineWidth(2.5F);
               c.draw(0.6F, 0.3F, 0.0F, 4, 1);
               GL11.glLineWidth(0.1F);
               GlStateManager.func_179131_c(1.0F, 0.0F, 0.0F, 1.0F);
            }

            c.draw(0.6F, 0.3F, 0.0F, 4, 1);
         }

         this.disableGL3D();
         GL11.glPopMatrix();
      }
   }

   private List getEntitiesWithinAABB(AxisAlignedBB bb) {
      ArrayList list = new ArrayList();
      int chunkMinX = MathHelper.func_76128_c((bb.field_72340_a - 2.0D) / 16.0D);
      int chunkMaxX = MathHelper.func_76128_c((bb.field_72336_d + 2.0D) / 16.0D);
      int chunkMinZ = MathHelper.func_76128_c((bb.field_72339_c - 2.0D) / 16.0D);
      int chunkMaxZ = MathHelper.func_76128_c((bb.field_72334_f + 2.0D) / 16.0D);

      for(int x = chunkMinX; x <= chunkMaxX; ++x) {
         for(int z = chunkMinZ; z <= chunkMaxZ; ++z) {
            if (mc.field_71441_e.func_72863_F().func_186026_b(x, z) != null) {
               mc.field_71441_e.func_72964_e(x, z).func_177414_a(mc.field_71439_g, bb, list, (Predicate)null);
            }
         }
      }

      return list;
   }
}
