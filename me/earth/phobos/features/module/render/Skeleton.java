package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.RenderEntityModelEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Skeleton extends Module {
   private final Setting<Boolean> colorSync = this.register(new Setting("Sync", false));
   private final Setting<Integer> red = this.register(new Setting("Red", 255, 0, 255));
   private final Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255));
   private final Setting<Integer> blue = this.register(new Setting("Blue", 255, 0, 255));
   private final Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255));
   private final Setting<Float> lineWidth = this.register(new Setting("LineWidth", 1.5F, 0.1F, 5.0F));
   private final Setting<Boolean> colorFriends = this.register(new Setting("Friends", true));
   private final Setting<Boolean> invisibles = this.register(new Setting("Invisibles", false));
   private static Skeleton INSTANCE = new Skeleton();
   private final Map<EntityPlayer, float[][]> rotationList = new HashMap();

   public Skeleton() {
      super("Skeleton", "Draws a nice Skeleton.", Module.Category.RENDER, false, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Skeleton getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Skeleton();
      }

      return INSTANCE;
   }

   public void onRender3D(Render3DEvent event) {
      RenderUtil.GLPre((Float)this.lineWidth.getValue());
      Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

      while(true) {
         EntityPlayer player;
         do {
            do {
               do {
                  do {
                     do {
                        if (!var2.hasNext()) {
                           RenderUtil.GlPost();
                           return;
                        }

                        player = (EntityPlayer)var2.next();
                     } while(player == null);
                  } while(player == mc.func_175606_aa());
               } while(!player.func_70089_S());
            } while(player.func_70608_bn());
         } while(player.func_82150_aj() && !(Boolean)this.invisibles.getValue());

         if (this.rotationList.get(player) != null && mc.field_71439_g.func_70068_e(player) < 2500.0D) {
            this.renderSkeleton(player, (float[][])this.rotationList.get(player), (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : EntityUtil.getColor(player, (Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue(), (Boolean)this.colorFriends.getValue()));
         }
      }
   }

   public void onRenderModel(RenderEntityModelEvent event) {
      if (event.getStage() == 0 && event.entity instanceof EntityPlayer && event.modelBase instanceof ModelBiped) {
         ModelBiped biped = (ModelBiped)event.modelBase;
         float[][] rotations = RenderUtil.getBipedRotations(biped);
         EntityPlayer player = (EntityPlayer)event.entity;
         this.rotationList.put(player, rotations);
      }

   }

   private void renderSkeleton(EntityPlayer player, float[][] rotations, Color color) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179094_E();
      GlStateManager.func_179131_c((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
      Vec3d interp = EntityUtil.getInterpolatedRenderPos(player, mc.func_184121_ak());
      double pX = interp.field_72450_a;
      double pY = interp.field_72448_b;
      double pZ = interp.field_72449_c;
      GlStateManager.func_179137_b(pX, pY, pZ);
      GlStateManager.func_179114_b(-player.field_70761_aq, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179137_b(0.0D, 0.0D, player.func_70093_af() ? -0.235D : 0.0D);
      float sneak = player.func_70093_af() ? 0.6F : 0.75F;
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(-0.125D, (double)sneak, 0.0D);
      if (rotations[3][0] != 0.0F) {
         GlStateManager.func_179114_b(rotations[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
      }

      if (rotations[3][1] != 0.0F) {
         GlStateManager.func_179114_b(rotations[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
      }

      if (rotations[3][2] != 0.0F) {
         GlStateManager.func_179114_b(rotations[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.func_187447_r(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, (double)(-sneak), 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.125D, (double)sneak, 0.0D);
      if (rotations[4][0] != 0.0F) {
         GlStateManager.func_179114_b(rotations[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
      }

      if (rotations[4][1] != 0.0F) {
         GlStateManager.func_179114_b(rotations[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
      }

      if (rotations[4][2] != 0.0F) {
         GlStateManager.func_179114_b(rotations[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.func_187447_r(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, (double)(-sneak), 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179137_b(0.0D, 0.0D, player.func_70093_af() ? 0.25D : 0.0D);
      GlStateManager.func_179094_E();
      double sneakOffset = 0.0D;
      if (player.func_70093_af()) {
         sneakOffset = -0.05D;
      }

      GlStateManager.func_179137_b(0.0D, sneakOffset, player.func_70093_af() ? -0.01725D : 0.0D);
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(-0.375D, (double)sneak + 0.55D, 0.0D);
      if (rotations[1][0] != 0.0F) {
         GlStateManager.func_179114_b(rotations[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
      }

      if (rotations[1][1] != 0.0F) {
         GlStateManager.func_179114_b(rotations[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
      }

      if (rotations[1][2] != 0.0F) {
         GlStateManager.func_179114_b(-rotations[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.func_187447_r(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, -0.5D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.375D, (double)sneak + 0.55D, 0.0D);
      if (rotations[2][0] != 0.0F) {
         GlStateManager.func_179114_b(rotations[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
      }

      if (rotations[2][1] != 0.0F) {
         GlStateManager.func_179114_b(rotations[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
      }

      if (rotations[2][2] != 0.0F) {
         GlStateManager.func_179114_b(-rotations[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.func_187447_r(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, -0.5D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.0D, (double)sneak + 0.55D, 0.0D);
      if (rotations[0][0] != 0.0F) {
         GlStateManager.func_179114_b(rotations[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
      }

      GlStateManager.func_187447_r(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, 0.3D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179121_F();
      GlStateManager.func_179114_b(player.func_70093_af() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
      if (player.func_70093_af()) {
         sneakOffset = -0.16175D;
      }

      GlStateManager.func_179137_b(0.0D, sneakOffset, player.func_70093_af() ? -0.48025D : 0.0D);
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.0D, (double)sneak, 0.0D);
      GlStateManager.func_187447_r(3);
      GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
      GL11.glVertex3d(0.125D, 0.0D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.0D, (double)sneak, 0.0D);
      GlStateManager.func_187447_r(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GL11.glVertex3d(0.0D, 0.55D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.0D, (double)sneak + 0.55D, 0.0D);
      GlStateManager.func_187447_r(3);
      GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
      GL11.glVertex3d(0.375D, 0.0D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179121_F();
   }

   private void renderSkeletonTest(EntityPlayer player, float[][] rotations, Color startColor, Color endColor) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179094_E();
      GlStateManager.func_179131_c((float)startColor.getRed() / 255.0F, (float)startColor.getGreen() / 255.0F, (float)startColor.getBlue() / 255.0F, (float)startColor.getAlpha() / 255.0F);
      Vec3d interp = EntityUtil.getInterpolatedRenderPos(player, mc.func_184121_ak());
      double pX = interp.field_72450_a;
      double pY = interp.field_72448_b;
      double pZ = interp.field_72449_c;
      GlStateManager.func_179137_b(pX, pY, pZ);
      GlStateManager.func_179114_b(-player.field_70761_aq, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179137_b(0.0D, 0.0D, player.func_70093_af() ? -0.235D : 0.0D);
      float sneak = player.func_70093_af() ? 0.6F : 0.75F;
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(-0.125D, (double)sneak, 0.0D);
      if (rotations[3][0] != 0.0F) {
         GlStateManager.func_179114_b(rotations[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
      }

      if (rotations[3][1] != 0.0F) {
         GlStateManager.func_179114_b(rotations[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
      }

      if (rotations[3][2] != 0.0F) {
         GlStateManager.func_179114_b(rotations[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.func_187447_r(3);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GlStateManager.func_179131_c((float)endColor.getRed() / 255.0F, (float)endColor.getGreen() / 255.0F, (float)endColor.getBlue() / 255.0F, (float)endColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.0D, (double)(-sneak), 0.0D);
      GlStateManager.func_179131_c((float)startColor.getRed() / 255.0F, (float)startColor.getGreen() / 255.0F, (float)startColor.getBlue() / 255.0F, (float)startColor.getAlpha() / 255.0F);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.125D, (double)sneak, 0.0D);
      if (rotations[4][0] != 0.0F) {
         GlStateManager.func_179114_b(rotations[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
      }

      if (rotations[4][1] != 0.0F) {
         GlStateManager.func_179114_b(rotations[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
      }

      if (rotations[4][2] != 0.0F) {
         GlStateManager.func_179114_b(rotations[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.func_187447_r(3);
      GlStateManager.func_179131_c((float)startColor.getRed() / 255.0F, (float)startColor.getGreen() / 255.0F, (float)startColor.getBlue() / 255.0F, (float)startColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GlStateManager.func_179131_c((float)endColor.getRed() / 255.0F, (float)endColor.getGreen() / 255.0F, (float)endColor.getBlue() / 255.0F, (float)endColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.0D, (double)(-sneak), 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179137_b(0.0D, 0.0D, player.func_70093_af() ? 0.25D : 0.0D);
      GlStateManager.func_179094_E();
      double sneakOffset = 0.0D;
      if (player.func_70093_af()) {
         sneakOffset = -0.05D;
      }

      GlStateManager.func_179137_b(0.0D, sneakOffset, player.func_70093_af() ? -0.01725D : 0.0D);
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(-0.375D, (double)sneak + 0.55D, 0.0D);
      if (rotations[1][0] != 0.0F) {
         GlStateManager.func_179114_b(rotations[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
      }

      if (rotations[1][1] != 0.0F) {
         GlStateManager.func_179114_b(rotations[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
      }

      if (rotations[1][2] != 0.0F) {
         GlStateManager.func_179114_b(-rotations[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.func_187447_r(3);
      GlStateManager.func_179131_c((float)startColor.getRed() / 255.0F, (float)startColor.getGreen() / 255.0F, (float)startColor.getBlue() / 255.0F, (float)startColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GlStateManager.func_179131_c((float)endColor.getRed() / 255.0F, (float)endColor.getGreen() / 255.0F, (float)endColor.getBlue() / 255.0F, (float)endColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.0D, -0.5D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.375D, (double)sneak + 0.55D, 0.0D);
      if (rotations[2][0] != 0.0F) {
         GlStateManager.func_179114_b(rotations[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
      }

      if (rotations[2][1] != 0.0F) {
         GlStateManager.func_179114_b(rotations[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
      }

      if (rotations[2][2] != 0.0F) {
         GlStateManager.func_179114_b(-rotations[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.func_187447_r(3);
      GlStateManager.func_179131_c((float)startColor.getRed() / 255.0F, (float)startColor.getGreen() / 255.0F, (float)startColor.getBlue() / 255.0F, (float)startColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GlStateManager.func_179131_c((float)endColor.getRed() / 255.0F, (float)endColor.getGreen() / 255.0F, (float)endColor.getBlue() / 255.0F, (float)endColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.0D, -0.5D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.0D, (double)sneak + 0.55D, 0.0D);
      if (rotations[0][0] != 0.0F) {
         GlStateManager.func_179114_b(rotations[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
      }

      GlStateManager.func_187447_r(3);
      GlStateManager.func_179131_c((float)startColor.getRed() / 255.0F, (float)startColor.getGreen() / 255.0F, (float)startColor.getBlue() / 255.0F, (float)startColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GlStateManager.func_179131_c((float)endColor.getRed() / 255.0F, (float)endColor.getGreen() / 255.0F, (float)endColor.getBlue() / 255.0F, (float)endColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.0D, 0.3D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179121_F();
      GlStateManager.func_179114_b(player.func_70093_af() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
      if (player.func_70093_af()) {
         sneakOffset = -0.16175D;
      }

      GlStateManager.func_179137_b(0.0D, sneakOffset, player.func_70093_af() ? -0.48025D : 0.0D);
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.0D, (double)sneak, 0.0D);
      GlStateManager.func_187447_r(3);
      GlStateManager.func_179131_c((float)startColor.getRed() / 255.0F, (float)startColor.getGreen() / 255.0F, (float)startColor.getBlue() / 255.0F, (float)startColor.getAlpha() / 255.0F);
      GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
      GlStateManager.func_179131_c((float)endColor.getRed() / 255.0F, (float)endColor.getGreen() / 255.0F, (float)endColor.getBlue() / 255.0F, (float)endColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.125D, 0.0D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.0D, (double)sneak, 0.0D);
      GlStateManager.func_187447_r(3);
      GlStateManager.func_179131_c((float)startColor.getRed() / 255.0F, (float)startColor.getGreen() / 255.0F, (float)startColor.getBlue() / 255.0F, (float)startColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.0D, 0.0D, 0.0D);
      GlStateManager.func_179131_c((float)endColor.getRed() / 255.0F, (float)endColor.getGreen() / 255.0F, (float)endColor.getBlue() / 255.0F, (float)endColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.0D, 0.55D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      GlStateManager.func_179137_b(0.0D, (double)sneak + 0.55D, 0.0D);
      GlStateManager.func_187447_r(3);
      GlStateManager.func_179131_c((float)startColor.getRed() / 255.0F, (float)startColor.getGreen() / 255.0F, (float)startColor.getBlue() / 255.0F, (float)startColor.getAlpha() / 255.0F);
      GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
      GlStateManager.func_179131_c((float)endColor.getRed() / 255.0F, (float)endColor.getGreen() / 255.0F, (float)endColor.getBlue() / 255.0F, (float)endColor.getAlpha() / 255.0F);
      GL11.glVertex3d(0.375D, 0.0D, 0.0D);
      GlStateManager.func_187437_J();
      GlStateManager.func_179121_F();
      GlStateManager.func_179121_F();
   }
}
