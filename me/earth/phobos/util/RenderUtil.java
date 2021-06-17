package me.earth.phobos.util;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Objects;
import me.earth.phobos.Phobos;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

public class RenderUtil implements Util {
   public static RenderItem itemRender;
   public static ICamera camera;
   private static final Frustum frustrum;
   private static boolean depth;
   private static boolean texture;
   private static boolean clean;
   private static boolean bind;
   private static boolean override;
   private static final FloatBuffer screenCoords;
   private static final IntBuffer viewport;
   private static final FloatBuffer modelView;
   private static final FloatBuffer projection;

   public static void drawRectangleCorrectly(int x, int y, int w, int h, int color) {
      GL11.glLineWidth(1.0F);
      Gui.func_73734_a(x, y, x + w, y + h, color);
   }

   public static AxisAlignedBB interpolateAxis(AxisAlignedBB bb) {
      return new AxisAlignedBB(bb.field_72340_a - mc.func_175598_ae().field_78730_l, bb.field_72338_b - mc.func_175598_ae().field_78731_m, bb.field_72339_c - mc.func_175598_ae().field_78728_n, bb.field_72336_d - mc.func_175598_ae().field_78730_l, bb.field_72337_e - mc.func_175598_ae().field_78731_m, bb.field_72334_f - mc.func_175598_ae().field_78728_n);
   }

   public static void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder BufferBuilder = tessellator.func_178180_c();
      BufferBuilder.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      BufferBuilder.func_181662_b((double)(x + 0), (double)(y + height), (double)zLevel).func_187315_a((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).func_181675_d();
      BufferBuilder.func_181662_b((double)(x + width), (double)(y + height), (double)zLevel).func_187315_a((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).func_181675_d();
      BufferBuilder.func_181662_b((double)(x + width), (double)(y + 0), (double)zLevel).func_187315_a((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).func_181675_d();
      BufferBuilder.func_181662_b((double)(x + 0), (double)(y + 0), (double)zLevel).func_187315_a((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).func_181675_d();
      tessellator.func_78381_a();
   }

   public static void drawGradientRect(int x, int y, int w, int h, int startColor, int endColor) {
      float f = (float)(startColor >> 24 & 255) / 255.0F;
      float f1 = (float)(startColor >> 16 & 255) / 255.0F;
      float f2 = (float)(startColor >> 8 & 255) / 255.0F;
      float f3 = (float)(startColor & 255) / 255.0F;
      float f4 = (float)(endColor >> 24 & 255) / 255.0F;
      float f5 = (float)(endColor >> 16 & 255) / 255.0F;
      float f6 = (float)(endColor >> 8 & 255) / 255.0F;
      float f7 = (float)(endColor & 255) / 255.0F;
      GlStateManager.func_179090_x();
      GlStateManager.func_179147_l();
      GlStateManager.func_179118_c();
      GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.func_179103_j(7425);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder vertexbuffer = tessellator.func_178180_c();
      vertexbuffer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      vertexbuffer.func_181662_b((double)x + (double)w, (double)y, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
      vertexbuffer.func_181662_b((double)x, (double)y, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
      vertexbuffer.func_181662_b((double)x, (double)y + (double)h, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
      vertexbuffer.func_181662_b((double)x + (double)w, (double)y + (double)h, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179103_j(7424);
      GlStateManager.func_179084_k();
      GlStateManager.func_179141_d();
      GlStateManager.func_179098_w();
   }

   public static void drawGradientBlockOutline(BlockPos pos, Color startColor, Color endColor, float linewidth) {
      IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
      Vec3d interp = EntityUtil.interpolateEntity(mc.field_71439_g, mc.func_184121_ak());
      drawGradientBlockOutline(iblockstate.func_185918_c(mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c), startColor, endColor, linewidth);
   }

   public static void drawProperGradientBlockOutline(BlockPos pos, Color startColor, Color midColor, Color endColor, float linewidth) {
      IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
      Vec3d interp = EntityUtil.interpolateEntity(mc.field_71439_g, mc.func_184121_ak());
      drawProperGradientBlockOutline(iblockstate.func_185918_c(mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c), startColor, midColor, endColor, linewidth);
   }

   public static void drawProperGradientBlockOutline(AxisAlignedBB bb, Color startColor, Color midColor, Color endColor, float linewidth) {
      float red = (float)endColor.getRed() / 255.0F;
      float green = (float)endColor.getGreen() / 255.0F;
      float blue = (float)endColor.getBlue() / 255.0F;
      float alpha = (float)endColor.getAlpha() / 255.0F;
      float red1 = (float)midColor.getRed() / 255.0F;
      float green1 = (float)midColor.getGreen() / 255.0F;
      float blue1 = (float)midColor.getBlue() / 255.0F;
      float alpha1 = (float)midColor.getAlpha() / 255.0F;
      float red2 = (float)startColor.getRed() / 255.0F;
      float green2 = (float)startColor.getGreen() / 255.0F;
      float blue2 = (float)startColor.getBlue() / 255.0F;
      float alpha2 = (float)startColor.getAlpha() / 255.0F;
      double dif = (bb.field_72337_e - bb.field_72338_b) / 2.0D;
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179120_a(770, 771, 0, 1);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glLineWidth(linewidth);
      GL11.glBegin(1);
      GL11.glColor4d((double)red, (double)green, (double)blue, (double)alpha);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c);
      GL11.glColor4d((double)red1, (double)green1, (double)blue1, (double)alpha1);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b + dif, bb.field_72339_c);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b + dif, bb.field_72339_c);
      GL11.glColor4f(red2, green2, blue2, alpha2);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c);
      GL11.glColor4d((double)red, (double)green, (double)blue, (double)alpha);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f);
      GL11.glColor4d((double)red1, (double)green1, (double)blue1, (double)alpha1);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b + dif, bb.field_72334_f);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b + dif, bb.field_72334_f);
      GL11.glColor4d((double)red2, (double)green2, (double)blue2, (double)alpha2);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f);
      GL11.glColor4d((double)red, (double)green, (double)blue, (double)alpha);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f);
      GL11.glColor4d((double)red1, (double)green1, (double)blue1, (double)alpha1);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b + dif, bb.field_72334_f);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b + dif, bb.field_72334_f);
      GL11.glColor4d((double)red2, (double)green2, (double)blue2, (double)alpha2);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f);
      GL11.glColor4d((double)red, (double)green, (double)blue, (double)alpha);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c);
      GL11.glColor4d((double)red1, (double)green1, (double)blue1, (double)alpha1);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b + dif, bb.field_72339_c);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b + dif, bb.field_72339_c);
      GL11.glColor4d((double)red2, (double)green2, (double)blue2, (double)alpha2);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c);
      GL11.glColor4d((double)red2, (double)green2, (double)blue2, (double)alpha2);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f);
      GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c);
      GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c);
      GL11.glEnd();
      GL11.glDisable(2848);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
      GlStateManager.func_179121_F();
   }

   public static void drawGradientBlockOutline(AxisAlignedBB bb, Color startColor, Color endColor, float linewidth) {
      float red = (float)startColor.getRed() / 255.0F;
      float green = (float)startColor.getGreen() / 255.0F;
      float blue = (float)startColor.getBlue() / 255.0F;
      float alpha = (float)startColor.getAlpha() / 255.0F;
      float red1 = (float)endColor.getRed() / 255.0F;
      float green1 = (float)endColor.getGreen() / 255.0F;
      float blue1 = (float)endColor.getBlue() / 255.0F;
      float alpha1 = (float)endColor.getAlpha() / 255.0F;
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179120_a(770, 771, 0, 1);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glLineWidth(linewidth);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      tessellator.func_78381_a();
      GL11.glDisable(2848);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
      GlStateManager.func_179121_F();
   }

   public static void drawGradientFilledBox(BlockPos pos, Color startColor, Color endColor) {
      IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
      Vec3d interp = EntityUtil.interpolateEntity(mc.field_71439_g, mc.func_184121_ak());
      drawGradientFilledBox(iblockstate.func_185918_c(mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c), startColor, endColor);
   }

   public static void drawGradientFilledBox(AxisAlignedBB bb, Color startColor, Color endColor) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179120_a(770, 771, 0, 1);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      float alpha = (float)endColor.getAlpha() / 255.0F;
      float red = (float)endColor.getRed() / 255.0F;
      float green = (float)endColor.getGreen() / 255.0F;
      float blue = (float)endColor.getBlue() / 255.0F;
      float alpha1 = (float)startColor.getAlpha() / 255.0F;
      float red1 = (float)startColor.getRed() / 255.0F;
      float green1 = (float)startColor.getGreen() / 255.0F;
      float blue1 = (float)startColor.getBlue() / 255.0F;
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
      GlStateManager.func_179121_F();
   }

   public static void drawGradientRect(float x, float y, float w, float h, int startColor, int endColor) {
      float f = (float)(startColor >> 24 & 255) / 255.0F;
      float f1 = (float)(startColor >> 16 & 255) / 255.0F;
      float f2 = (float)(startColor >> 8 & 255) / 255.0F;
      float f3 = (float)(startColor & 255) / 255.0F;
      float f4 = (float)(endColor >> 24 & 255) / 255.0F;
      float f5 = (float)(endColor >> 16 & 255) / 255.0F;
      float f6 = (float)(endColor >> 8 & 255) / 255.0F;
      float f7 = (float)(endColor & 255) / 255.0F;
      GlStateManager.func_179090_x();
      GlStateManager.func_179147_l();
      GlStateManager.func_179118_c();
      GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.func_179103_j(7425);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder vertexbuffer = tessellator.func_178180_c();
      vertexbuffer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      vertexbuffer.func_181662_b((double)x + (double)w, (double)y, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
      vertexbuffer.func_181662_b((double)x, (double)y, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
      vertexbuffer.func_181662_b((double)x, (double)y + (double)h, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
      vertexbuffer.func_181662_b((double)x + (double)w, (double)y + (double)h, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179103_j(7424);
      GlStateManager.func_179084_k();
      GlStateManager.func_179141_d();
      GlStateManager.func_179098_w();
   }

   public static void blockESP(BlockPos b, Color c, double length, double length2) {
      blockEsp(b, c, length, length2);
   }

   public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air) {
      if (box) {
         drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha));
      }

      if (outline) {
         drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air);
      }

   }

   public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air, double height) {
      if (box) {
         drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha), height);
      }

      if (outline) {
         drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air, height);
      }

   }

   public static void glScissor(float x, float y, float x1, float y1, ScaledResolution sr) {
      GL11.glScissor((int)(x * (float)sr.func_78325_e()), (int)((float)mc.field_71440_d - y1 * (float)sr.func_78325_e()), (int)((x1 - x) * (float)sr.func_78325_e()), (int)((y1 - y) * (float)sr.func_78325_e()));
   }

   public static void drawLine(float x, float y, float x1, float y1, float thickness, int hex) {
      float red = (float)(hex >> 16 & 255) / 255.0F;
      float green = (float)(hex >> 8 & 255) / 255.0F;
      float blue = (float)(hex & 255) / 255.0F;
      float alpha = (float)(hex >> 24 & 255) / 255.0F;
      GlStateManager.func_179094_E();
      GlStateManager.func_179090_x();
      GlStateManager.func_179147_l();
      GlStateManager.func_179118_c();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179103_j(7425);
      GL11.glLineWidth(thickness);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b((double)x, (double)y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b((double)x1, (double)y1, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179103_j(7424);
      GL11.glDisable(2848);
      GlStateManager.func_179084_k();
      GlStateManager.func_179141_d();
      GlStateManager.func_179098_w();
      GlStateManager.func_179121_F();
   }

   public static void drawBox(BlockPos pos, Color color) {
      AxisAlignedBB bb = new AxisAlignedBB((double)pos.func_177958_n() - mc.func_175598_ae().field_78730_l, (double)pos.func_177956_o() - mc.func_175598_ae().field_78731_m, (double)pos.func_177952_p() - mc.func_175598_ae().field_78728_n, (double)(pos.func_177958_n() + 1) - mc.func_175598_ae().field_78730_l, (double)(pos.func_177956_o() + 1) - mc.func_175598_ae().field_78731_m, (double)(pos.func_177952_p() + 1) - mc.func_175598_ae().field_78728_n);
      camera.func_78547_a(((Entity)Objects.requireNonNull(mc.func_175606_aa())).field_70165_t, mc.func_175606_aa().field_70163_u, mc.func_175606_aa().field_70161_v);
      if (camera.func_78546_a(new AxisAlignedBB(bb.field_72340_a + mc.func_175598_ae().field_78730_l, bb.field_72338_b + mc.func_175598_ae().field_78731_m, bb.field_72339_c + mc.func_175598_ae().field_78728_n, bb.field_72336_d + mc.func_175598_ae().field_78730_l, bb.field_72337_e + mc.func_175598_ae().field_78731_m, bb.field_72334_f + mc.func_175598_ae().field_78728_n))) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179147_l();
         GlStateManager.func_179097_i();
         GlStateManager.func_179120_a(770, 771, 0, 1);
         GlStateManager.func_179090_x();
         GlStateManager.func_179132_a(false);
         GL11.glEnable(2848);
         GL11.glHint(3154, 4354);
         RenderGlobal.func_189696_b(bb, (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
         GL11.glDisable(2848);
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179126_j();
         GlStateManager.func_179098_w();
         GlStateManager.func_179084_k();
         GlStateManager.func_179121_F();
      }

   }

   public static void drawBetterGradientBox(BlockPos pos, Color startColor, Color endColor) {
      float red = (float)startColor.getRed() / 255.0F;
      float green = (float)startColor.getGreen() / 255.0F;
      float blue = (float)startColor.getBlue() / 255.0F;
      float alpha = (float)startColor.getAlpha() / 255.0F;
      float red1 = (float)endColor.getRed() / 255.0F;
      float green1 = (float)endColor.getGreen() / 255.0F;
      float blue1 = (float)endColor.getBlue() / 255.0F;
      float alpha1 = (float)endColor.getAlpha() / 255.0F;
      AxisAlignedBB bb = new AxisAlignedBB((double)pos.func_177958_n() - mc.func_175598_ae().field_78730_l, (double)pos.func_177956_o() - mc.func_175598_ae().field_78731_m, (double)pos.func_177952_p() - mc.func_175598_ae().field_78728_n, (double)(pos.func_177958_n() + 1) - mc.func_175598_ae().field_78730_l, (double)(pos.func_177956_o() + 1) - mc.func_175598_ae().field_78731_m, (double)(pos.func_177952_p() + 1) - mc.func_175598_ae().field_78728_n);
      double offset = (bb.field_72337_e - bb.field_72338_b) / 2.0D;
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder builder = tessellator.func_178180_c();
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179120_a(770, 771, 0, 1);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      builder.func_181668_a(5, DefaultVertexFormats.field_181706_f);
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
   }

   public static void drawBetterGradientBox(BlockPos pos, Color startColor, Color midColor, Color endColor) {
      float red = (float)startColor.getRed() / 255.0F;
      float green = (float)startColor.getGreen() / 255.0F;
      float blue = (float)startColor.getBlue() / 255.0F;
      float alpha = (float)startColor.getAlpha() / 255.0F;
      float red1 = (float)endColor.getRed() / 255.0F;
      float green1 = (float)endColor.getGreen() / 255.0F;
      float blue1 = (float)endColor.getBlue() / 255.0F;
      float alpha1 = (float)endColor.getAlpha() / 255.0F;
      float red2 = (float)midColor.getRed() / 255.0F;
      float green2 = (float)midColor.getGreen() / 255.0F;
      float blue2 = (float)midColor.getBlue() / 255.0F;
      float alpha2 = (float)midColor.getAlpha() / 255.0F;
      AxisAlignedBB bb = new AxisAlignedBB((double)pos.func_177958_n() - mc.func_175598_ae().field_78730_l, (double)pos.func_177956_o() - mc.func_175598_ae().field_78731_m, (double)pos.func_177952_p() - mc.func_175598_ae().field_78728_n, (double)(pos.func_177958_n() + 1) - mc.func_175598_ae().field_78730_l, (double)(pos.func_177956_o() + 1) - mc.func_175598_ae().field_78731_m, (double)(pos.func_177952_p() + 1) - mc.func_175598_ae().field_78728_n);
      double offset = (bb.field_72337_e - bb.field_72338_b) / 2.0D;
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder builder = tessellator.func_178180_c();
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179120_a(770, 771, 0, 1);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      builder.func_181668_a(5, DefaultVertexFormats.field_181706_f);
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72339_c).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72339_c).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72339_c).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72339_c).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b + offset, bb.field_72334_f).func_181666_a(red2, green2, blue2, alpha2).func_181675_d();
      tessellator.func_78381_a();
      GL11.glDisable(2848);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
      GlStateManager.func_179121_F();
   }

   public static void drawEvenBetterGradientBox(BlockPos pos, Color startColor, Color midColor, Color endColor) {
      float red = (float)startColor.getRed() / 255.0F;
      float green = (float)startColor.getGreen() / 255.0F;
      float blue = (float)startColor.getBlue() / 255.0F;
      float alpha = (float)startColor.getAlpha() / 255.0F;
      float red1 = (float)endColor.getRed() / 255.0F;
      float green1 = (float)endColor.getGreen() / 255.0F;
      float blue1 = (float)endColor.getBlue() / 255.0F;
      float alpha1 = (float)endColor.getAlpha() / 255.0F;
      float red2 = (float)midColor.getRed() / 255.0F;
      float green2 = (float)midColor.getGreen() / 255.0F;
      float blue2 = (float)midColor.getBlue() / 255.0F;
      float alpha2 = (float)midColor.getAlpha() / 255.0F;
      AxisAlignedBB bb = new AxisAlignedBB((double)pos.func_177958_n() - mc.func_175598_ae().field_78730_l, (double)pos.func_177956_o() - mc.func_175598_ae().field_78731_m, (double)pos.func_177952_p() - mc.func_175598_ae().field_78728_n, (double)(pos.func_177958_n() + 1) - mc.func_175598_ae().field_78730_l, (double)(pos.func_177956_o() + 1) - mc.func_175598_ae().field_78731_m, (double)(pos.func_177952_p() + 1) - mc.func_175598_ae().field_78728_n);
      double offset = (bb.field_72337_e - bb.field_72338_b) / 2.0D;
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder builder = tessellator.func_178180_c();
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179120_a(770, 771, 0, 1);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      builder.func_181668_a(5, DefaultVertexFormats.field_181706_f);
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red1, green1, blue1, alpha1).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      builder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      tessellator.func_78381_a();
      GL11.glDisable(2848);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
      GlStateManager.func_179121_F();
   }

   public static void drawBox(BlockPos pos, Color color, double height) {
      AxisAlignedBB bb = new AxisAlignedBB((double)pos.func_177958_n() - mc.func_175598_ae().field_78730_l, (double)pos.func_177956_o() - mc.func_175598_ae().field_78731_m, (double)pos.func_177952_p() - mc.func_175598_ae().field_78728_n, (double)(pos.func_177958_n() + 1) - mc.func_175598_ae().field_78730_l, (double)(pos.func_177956_o() + 1) - mc.func_175598_ae().field_78731_m + height, (double)(pos.func_177952_p() + 1) - mc.func_175598_ae().field_78728_n);
      camera.func_78547_a(((Entity)Objects.requireNonNull(mc.func_175606_aa())).field_70165_t, mc.func_175606_aa().field_70163_u, mc.func_175606_aa().field_70161_v);
      if (camera.func_78546_a(new AxisAlignedBB(bb.field_72340_a + mc.func_175598_ae().field_78730_l, bb.field_72338_b + mc.func_175598_ae().field_78731_m, bb.field_72339_c + mc.func_175598_ae().field_78728_n, bb.field_72336_d + mc.func_175598_ae().field_78730_l, bb.field_72337_e + mc.func_175598_ae().field_78731_m, bb.field_72334_f + mc.func_175598_ae().field_78728_n))) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179147_l();
         GlStateManager.func_179097_i();
         GlStateManager.func_179120_a(770, 771, 0, 1);
         GlStateManager.func_179090_x();
         GlStateManager.func_179132_a(false);
         GL11.glEnable(2848);
         GL11.glHint(3154, 4354);
         RenderGlobal.func_189696_b(bb, (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
         GL11.glDisable(2848);
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179126_j();
         GlStateManager.func_179098_w();
         GlStateManager.func_179084_k();
         GlStateManager.func_179121_F();
      }

   }

   public static void drawBlockOutline(BlockPos pos, Color color, float linewidth, boolean air) {
      IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
      if ((air || iblockstate.func_185904_a() != Material.field_151579_a) && mc.field_71441_e.func_175723_af().func_177746_a(pos)) {
         Vec3d interp = EntityUtil.interpolateEntity(mc.field_71439_g, mc.func_184121_ak());
         drawBlockOutline(iblockstate.func_185918_c(mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c), color, linewidth);
      }

   }

   public static void drawBlockOutline(BlockPos pos, Color color, float linewidth, boolean air, double height) {
      IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
      if ((air || iblockstate.func_185904_a() != Material.field_151579_a) && mc.field_71441_e.func_175723_af().func_177746_a(pos)) {
         AxisAlignedBB blockAxis = new AxisAlignedBB((double)pos.func_177958_n() - mc.func_175598_ae().field_78730_l, (double)pos.func_177956_o() - mc.func_175598_ae().field_78731_m, (double)pos.func_177952_p() - mc.func_175598_ae().field_78728_n, (double)(pos.func_177958_n() + 1) - mc.func_175598_ae().field_78730_l, (double)(pos.func_177956_o() + 1) - mc.func_175598_ae().field_78731_m + height, (double)(pos.func_177952_p() + 1) - mc.func_175598_ae().field_78728_n);
         drawBlockOutline(blockAxis.func_186662_g(0.0020000000949949026D), color, linewidth);
      }

   }

   public static void drawBlockOutline(AxisAlignedBB bb, Color color, float linewidth) {
      float red = (float)color.getRed() / 255.0F;
      float green = (float)color.getGreen() / 255.0F;
      float blue = (float)color.getBlue() / 255.0F;
      float alpha = (float)color.getAlpha() / 255.0F;
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179120_a(770, 771, 0, 1);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glLineWidth(linewidth);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      tessellator.func_78381_a();
      GL11.glDisable(2848);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
      GlStateManager.func_179121_F();
   }

   public static void drawBoxESP(BlockPos pos, Color color, float lineWidth, boolean outline, boolean box, int boxAlpha) {
      AxisAlignedBB bb = new AxisAlignedBB((double)pos.func_177958_n() - mc.func_175598_ae().field_78730_l, (double)pos.func_177956_o() - mc.func_175598_ae().field_78731_m, (double)pos.func_177952_p() - mc.func_175598_ae().field_78728_n, (double)(pos.func_177958_n() + 1) - mc.func_175598_ae().field_78730_l, (double)(pos.func_177956_o() + 1) - mc.func_175598_ae().field_78731_m, (double)(pos.func_177952_p() + 1) - mc.func_175598_ae().field_78728_n);
      camera.func_78547_a(((Entity)Objects.requireNonNull(mc.func_175606_aa())).field_70165_t, mc.func_175606_aa().field_70163_u, mc.func_175606_aa().field_70161_v);
      if (camera.func_78546_a(new AxisAlignedBB(bb.field_72340_a + mc.func_175598_ae().field_78730_l, bb.field_72338_b + mc.func_175598_ae().field_78731_m, bb.field_72339_c + mc.func_175598_ae().field_78728_n, bb.field_72336_d + mc.func_175598_ae().field_78730_l, bb.field_72337_e + mc.func_175598_ae().field_78731_m, bb.field_72334_f + mc.func_175598_ae().field_78728_n))) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179147_l();
         GlStateManager.func_179097_i();
         GlStateManager.func_179120_a(770, 771, 0, 1);
         GlStateManager.func_179090_x();
         GlStateManager.func_179132_a(false);
         GL11.glEnable(2848);
         GL11.glHint(3154, 4354);
         GL11.glLineWidth(lineWidth);
         double dist = mc.field_71439_g.func_70011_f((double)((float)pos.func_177958_n() + 0.5F), (double)((float)pos.func_177956_o() + 0.5F), (double)((float)pos.func_177952_p() + 0.5F)) * 0.75D;
         if (box) {
            RenderGlobal.func_189696_b(bb, (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)boxAlpha / 255.0F);
         }

         if (outline) {
            RenderGlobal.func_189694_a(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
         }

         GL11.glDisable(2848);
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179126_j();
         GlStateManager.func_179098_w();
         GlStateManager.func_179084_k();
         GlStateManager.func_179121_F();
      }

   }

   public static void drawText(BlockPos pos, String text) {
      if (pos != null && text != null) {
         GlStateManager.func_179094_E();
         glBillboardDistanceScaled((float)pos.func_177958_n() + 0.5F, (float)pos.func_177956_o() + 0.5F, (float)pos.func_177952_p() + 0.5F, mc.field_71439_g, 1.0F);
         GlStateManager.func_179097_i();
         GlStateManager.func_179137_b(-((double)Phobos.textManager.getStringWidth(text) / 2.0D), 0.0D, 0.0D);
         Phobos.textManager.drawStringWithShadow(text, 0.0F, 0.0F, -5592406);
         GlStateManager.func_179121_F();
      }
   }

   public static void drawOutlinedBlockESP(BlockPos pos, Color color, float linewidth) {
      IBlockState iblockstate = mc.field_71441_e.func_180495_p(pos);
      Vec3d interp = EntityUtil.interpolateEntity(mc.field_71439_g, mc.func_184121_ak());
      drawBoundingBox(iblockstate.func_185918_c(mc.field_71441_e, pos).func_186662_g(0.0020000000949949026D).func_72317_d(-interp.field_72450_a, -interp.field_72448_b, -interp.field_72449_c), linewidth, ColorUtil.toRGBA(color));
   }

   public static void blockEsp(BlockPos blockPos, Color c, double length, double length2) {
      double x = (double)blockPos.func_177958_n() - mc.field_175616_W.field_78725_b;
      double y = (double)blockPos.func_177956_o() - mc.field_175616_W.field_78726_c;
      double z = (double)blockPos.func_177952_p() - mc.field_175616_W.field_78723_d;
      GL11.glPushMatrix();
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(2.0F);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      GL11.glColor4d((double)((float)c.getRed() / 255.0F), (double)((float)c.getGreen() / 255.0F), (double)((float)c.getBlue() / 255.0F), 0.25D);
      drawColorBox(new AxisAlignedBB(x, y, z, x + length2, y + 1.0D, z + length), 0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glColor4d(0.0D, 0.0D, 0.0D, 0.5D);
      drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + length2, y + 1.0D, z + length));
      GL11.glLineWidth(2.0F);
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawRect(float x, float y, float w, float h, int color) {
      float alpha = (float)(color >> 24 & 255) / 255.0F;
      float red = (float)(color >> 16 & 255) / 255.0F;
      float green = (float)(color >> 8 & 255) / 255.0F;
      float blue = (float)(color & 255) / 255.0F;
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b((double)x, (double)h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b((double)w, (double)h, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b((double)w, (double)y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b((double)x, (double)y, 0.0D).func_181666_a(red, green, blue, alpha).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }

   public static void drawColorBox(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
      Tessellator ts = Tessellator.func_178181_a();
      BufferBuilder vb = ts.func_178180_c();
      vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      ts.func_78381_a();
      vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      ts.func_78381_a();
      vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      ts.func_78381_a();
      vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      ts.func_78381_a();
      vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      ts.func_78381_a();
      vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72340_a, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72337_e, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      vb.func_181662_b(axisalignedbb.field_72336_d, axisalignedbb.field_72338_b, axisalignedbb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      ts.func_78381_a();
   }

   public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder vertexbuffer = tessellator.func_178180_c();
      vertexbuffer.func_181668_a(3, DefaultVertexFormats.field_181705_e);
      vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72338_b, boundingBox.field_72339_c).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72338_b, boundingBox.field_72339_c).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72338_b, boundingBox.field_72334_f).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72338_b, boundingBox.field_72334_f).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72338_b, boundingBox.field_72339_c).func_181675_d();
      tessellator.func_78381_a();
      vertexbuffer.func_181668_a(3, DefaultVertexFormats.field_181705_e);
      vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72337_e, boundingBox.field_72339_c).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72337_e, boundingBox.field_72339_c).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72337_e, boundingBox.field_72334_f).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72337_e, boundingBox.field_72334_f).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72337_e, boundingBox.field_72339_c).func_181675_d();
      tessellator.func_78381_a();
      vertexbuffer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
      vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72338_b, boundingBox.field_72339_c).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72337_e, boundingBox.field_72339_c).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72338_b, boundingBox.field_72339_c).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72337_e, boundingBox.field_72339_c).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72338_b, boundingBox.field_72334_f).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72336_d, boundingBox.field_72337_e, boundingBox.field_72334_f).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72338_b, boundingBox.field_72334_f).func_181675_d();
      vertexbuffer.func_181662_b(boundingBox.field_72340_a, boundingBox.field_72337_e, boundingBox.field_72334_f).func_181675_d();
      tessellator.func_78381_a();
   }

   public static void glrendermethod() {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(2848);
      GL11.glLineWidth(2.0F);
      GL11.glDisable(3553);
      GL11.glEnable(2884);
      GL11.glDisable(2929);
      double viewerPosX = mc.func_175598_ae().field_78730_l;
      double viewerPosY = mc.func_175598_ae().field_78731_m;
      double viewerPosZ = mc.func_175598_ae().field_78728_n;
      GL11.glPushMatrix();
      GL11.glTranslated(-viewerPosX, -viewerPosY, -viewerPosZ);
   }

   public static void glStart(float n, float n2, float n3, float n4) {
      glrendermethod();
      GL11.glColor4f(n, n2, n3, n4);
   }

   public static void glEnd() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
      GL11.glEnable(2929);
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glDisable(2848);
   }

   public static AxisAlignedBB getBoundingBox(BlockPos blockPos) {
      return mc.field_71441_e.func_180495_p(blockPos).func_185900_c(mc.field_71441_e, blockPos).func_186670_a(blockPos);
   }

   public static void drawOutlinedBox(AxisAlignedBB axisAlignedBB) {
      GL11.glBegin(1);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f);
      GL11.glVertex3d(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c);
      GL11.glEnd();
   }

   public static void drawFilledBoxESPN(BlockPos pos, Color color) {
      AxisAlignedBB bb = new AxisAlignedBB((double)pos.func_177958_n() - mc.func_175598_ae().field_78730_l, (double)pos.func_177956_o() - mc.func_175598_ae().field_78731_m, (double)pos.func_177952_p() - mc.func_175598_ae().field_78728_n, (double)(pos.func_177958_n() + 1) - mc.func_175598_ae().field_78730_l, (double)(pos.func_177956_o() + 1) - mc.func_175598_ae().field_78731_m, (double)(pos.func_177952_p() + 1) - mc.func_175598_ae().field_78728_n);
      int rgba = ColorUtil.toRGBA(color);
      drawFilledBox(bb, rgba);
   }

   public static void drawFilledBox(AxisAlignedBB bb, int color) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179120_a(770, 771, 0, 1);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      float alpha = (float)(color >> 24 & 255) / 255.0F;
      float red = (float)(color >> 16 & 255) / 255.0F;
      float green = (float)(color >> 8 & 255) / 255.0F;
      float blue = (float)(color & 255) / 255.0F;
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
      GlStateManager.func_179121_F();
   }

   public static void drawBoundingBox(AxisAlignedBB bb, float width, int color) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179120_a(770, 771, 0, 1);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glLineWidth(width);
      float alpha = (float)(color >> 24 & 255) / 255.0F;
      float red = (float)(color >> 16 & 255) / 255.0F;
      float green = (float)(color >> 8 & 255) / 255.0F;
      float blue = (float)(color & 255) / 255.0F;
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      tessellator.func_78381_a();
      GL11.glDisable(2848);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
      GlStateManager.func_179121_F();
   }

   public static void glBillboard(float x, float y, float z) {
      float scale = 0.02666667F;
      GlStateManager.func_179137_b((double)x - mc.func_175598_ae().field_78725_b, (double)y - mc.func_175598_ae().field_78726_c, (double)z - mc.func_175598_ae().field_78723_d);
      GlStateManager.func_187432_a(0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(-mc.field_71439_g.field_70177_z, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(mc.field_71439_g.field_70125_A, mc.field_71474_y.field_74320_O == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179152_a(-scale, -scale, scale);
   }

   public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
      glBillboard(x, y, z);
      int distance = (int)player.func_70011_f((double)x, (double)y, (double)z);
      float scaleDistance = (float)distance / 2.0F / (2.0F + (2.0F - scale));
      if (scaleDistance < 1.0F) {
         scaleDistance = 1.0F;
      }

      GlStateManager.func_179152_a(scaleDistance, scaleDistance, scaleDistance);
   }

   public static void drawColoredBoundingBox(AxisAlignedBB bb, float width, float red, float green, float blue, float alpha) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179147_l();
      GlStateManager.func_179097_i();
      GlStateManager.func_179120_a(770, 771, 0, 1);
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glLineWidth(width);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, 0.0F).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, 0.0F).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, 0.0F).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, 0.0F).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
      bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, 0.0F).func_181675_d();
      tessellator.func_78381_a();
      GL11.glDisable(2848);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
      GlStateManager.func_179121_F();
   }

   public static void drawSphere(double x, double y, double z, float size, int slices, int stacks) {
      Sphere s = new Sphere();
      GL11.glPushMatrix();
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(1.2F);
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      s.setDrawStyle(100013);
      GL11.glTranslated(x - mc.field_175616_W.field_78725_b, y - mc.field_175616_W.field_78726_c, z - mc.field_175616_W.field_78723_d);
      s.draw(size, slices, stacks);
      GL11.glLineWidth(2.0F);
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
   }

   public static void GLPre(float lineWidth) {
      depth = GL11.glIsEnabled(2896);
      texture = GL11.glIsEnabled(3042);
      clean = GL11.glIsEnabled(3553);
      bind = GL11.glIsEnabled(2929);
      override = GL11.glIsEnabled(2848);
      GLPre(depth, texture, clean, bind, override, lineWidth);
   }

   public static void GlPost() {
      GLPost(depth, texture, clean, bind, override);
   }

   private static void GLPre(boolean depth, boolean texture, boolean clean, boolean bind, boolean override, float lineWidth) {
      if (depth) {
         GL11.glDisable(2896);
      }

      if (!texture) {
         GL11.glEnable(3042);
      }

      GL11.glLineWidth(lineWidth);
      if (clean) {
         GL11.glDisable(3553);
      }

      if (bind) {
         GL11.glDisable(2929);
      }

      if (!override) {
         GL11.glEnable(2848);
      }

      GlStateManager.func_187401_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GL11.glHint(3154, 4354);
      GlStateManager.func_179132_a(false);
   }

   public static float[][] getBipedRotations(ModelBiped biped) {
      float[][] rotations = new float[5][];
      float[] headRotation = new float[]{biped.field_78116_c.field_78795_f, biped.field_78116_c.field_78796_g, biped.field_78116_c.field_78808_h};
      rotations[0] = headRotation;
      float[] rightArmRotation = new float[]{biped.field_178723_h.field_78795_f, biped.field_178723_h.field_78796_g, biped.field_178723_h.field_78808_h};
      rotations[1] = rightArmRotation;
      float[] leftArmRotation = new float[]{biped.field_178724_i.field_78795_f, biped.field_178724_i.field_78796_g, biped.field_178724_i.field_78808_h};
      rotations[2] = leftArmRotation;
      float[] rightLegRotation = new float[]{biped.field_178721_j.field_78795_f, biped.field_178721_j.field_78796_g, biped.field_178721_j.field_78808_h};
      rotations[3] = rightLegRotation;
      float[] leftLegRotation = new float[]{biped.field_178722_k.field_78795_f, biped.field_178722_k.field_78796_g, biped.field_178722_k.field_78808_h};
      rotations[4] = leftLegRotation;
      return rotations;
   }

   private static void GLPost(boolean depth, boolean texture, boolean clean, boolean bind, boolean override) {
      GlStateManager.func_179132_a(true);
      if (!override) {
         GL11.glDisable(2848);
      }

      if (bind) {
         GL11.glEnable(2929);
      }

      if (clean) {
         GL11.glEnable(3553);
      }

      if (!texture) {
         GL11.glDisable(3042);
      }

      if (depth) {
         GL11.glEnable(2896);
      }

   }

   public static void drawArc(float cx, float cy, float r, float start_angle, float end_angle, int num_segments) {
      GL11.glBegin(4);

      for(int i = (int)((float)num_segments / (360.0F / start_angle)) + 1; (float)i <= (float)num_segments / (360.0F / end_angle); ++i) {
         double previousangle = 6.283185307179586D * (double)(i - 1) / (double)num_segments;
         double angle = 6.283185307179586D * (double)i / (double)num_segments;
         GL11.glVertex2d((double)cx, (double)cy);
         GL11.glVertex2d((double)cx + Math.cos(angle) * (double)r, (double)cy + Math.sin(angle) * (double)r);
         GL11.glVertex2d((double)cx + Math.cos(previousangle) * (double)r, (double)cy + Math.sin(previousangle) * (double)r);
      }

      glEnd();
   }

   public static void drawArcOutline(float cx, float cy, float r, float start_angle, float end_angle, int num_segments) {
      GL11.glBegin(2);

      for(int i = (int)((float)num_segments / (360.0F / start_angle)) + 1; (float)i <= (float)num_segments / (360.0F / end_angle); ++i) {
         double angle = 6.283185307179586D * (double)i / (double)num_segments;
         GL11.glVertex2d((double)cx + Math.cos(angle) * (double)r, (double)cy + Math.sin(angle) * (double)r);
      }

      glEnd();
   }

   public static void drawCircleOutline(float x, float y, float radius) {
      drawCircleOutline(x, y, radius, 0, 360, 40);
   }

   public static void drawCircleOutline(float x, float y, float radius, int start, int end, int segments) {
      drawArcOutline(x, y, radius, (float)start, (float)end, segments);
   }

   public static void drawCircle(float x, float y, float radius) {
      drawCircle(x, y, radius, 0, 360, 64);
   }

   public static void drawCircle(float x, float y, float radius, int start, int end, int segments) {
      drawArc(x, y, radius, (float)start, (float)end, segments);
   }

   public static void drawOutlinedRoundedRectangle(int x, int y, int width, int height, float radius, float dR, float dG, float dB, float dA, float outlineWidth) {
      drawRoundedRectangle((float)x, (float)y, (float)width, (float)height, radius);
      GL11.glColor4f(dR, dG, dB, dA);
      drawRoundedRectangle((float)x + outlineWidth, (float)y + outlineWidth, (float)width - outlineWidth * 2.0F, (float)height - outlineWidth * 2.0F, radius);
   }

   public static void drawRectangle(float x, float y, float width, float height) {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glBegin(2);
      GL11.glVertex2d((double)width, 0.0D);
      GL11.glVertex2d(0.0D, 0.0D);
      GL11.glVertex2d(0.0D, (double)height);
      GL11.glVertex2d((double)width, (double)height);
      glEnd();
   }

   public static void drawRectangleXY(float x, float y, float width, float height) {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glBegin(2);
      GL11.glVertex2d((double)(x + width), (double)y);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glVertex2d((double)x, (double)(y + height));
      GL11.glVertex2d((double)(x + width), (double)(y + height));
      glEnd();
   }

   public static void drawFilledRectangle(float x, float y, float width, float height) {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glBegin(7);
      GL11.glVertex2d((double)(x + width), (double)y);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glVertex2d((double)x, (double)(y + height));
      GL11.glVertex2d((double)(x + width), (double)(y + height));
      glEnd();
   }

   public static Vec3d to2D(double x, double y, double z) {
      GL11.glGetFloat(2982, modelView);
      GL11.glGetFloat(2983, projection);
      GL11.glGetInteger(2978, viewport);
      boolean result = GLU.gluProject((float)x, (float)y, (float)z, modelView, projection, viewport, screenCoords);
      return result ? new Vec3d((double)screenCoords.get(0), (double)((float)Display.getHeight() - screenCoords.get(1)), (double)screenCoords.get(2)) : null;
   }

   public static void drawTracerPointer(float x, float y, float size, float widthDiv, float heightDiv, boolean outline, float outlineWidth, int color) {
      boolean blend = GL11.glIsEnabled(3042);
      float alpha = (float)(color >> 24 & 255) / 255.0F;
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(2848);
      GL11.glPushMatrix();
      hexColor(color);
      GL11.glBegin(7);
      GL11.glVertex2d((double)x, (double)y);
      GL11.glVertex2d((double)(x - size / widthDiv), (double)(y + size));
      GL11.glVertex2d((double)x, (double)(y + size / heightDiv));
      GL11.glVertex2d((double)(x + size / widthDiv), (double)(y + size));
      GL11.glVertex2d((double)x, (double)y);
      GL11.glEnd();
      if (outline) {
         GL11.glLineWidth(outlineWidth);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, alpha);
         GL11.glBegin(2);
         GL11.glVertex2d((double)x, (double)y);
         GL11.glVertex2d((double)(x - size / widthDiv), (double)(y + size));
         GL11.glVertex2d((double)x, (double)(y + size / heightDiv));
         GL11.glVertex2d((double)(x + size / widthDiv), (double)(y + size));
         GL11.glVertex2d((double)x, (double)y);
         GL11.glEnd();
      }

      GL11.glPopMatrix();
      GL11.glEnable(3553);
      if (!blend) {
         GL11.glDisable(3042);
      }

      GL11.glDisable(2848);
   }

   public static int getRainbow(int speed, int offset, float s, float b) {
      float hue = (float)((System.currentTimeMillis() + (long)offset) % (long)speed);
      hue /= (float)speed;
      return Color.getHSBColor(hue, s, b).getRGB();
   }

   public static void hexColor(int hexColor) {
      float red = (float)(hexColor >> 16 & 255) / 255.0F;
      float green = (float)(hexColor >> 8 & 255) / 255.0F;
      float blue = (float)(hexColor & 255) / 255.0F;
      float alpha = (float)(hexColor >> 24 & 255) / 255.0F;
      GL11.glColor4f(red, green, blue, alpha);
   }

   public static boolean isInViewFrustrum(Entity entity) {
      return isInViewFrustrum(entity.func_174813_aQ()) || entity.field_70158_ak;
   }

   public static boolean isInViewFrustrum(AxisAlignedBB bb) {
      Entity current = Minecraft.func_71410_x().func_175606_aa();
      frustrum.func_78547_a(current.field_70165_t, current.field_70163_u, current.field_70161_v);
      return frustrum.func_78546_a(bb);
   }

   public static void drawRoundedRectangle(float x, float y, float width, float height, float radius) {
      GL11.glEnable(3042);
      drawArc(x + width - radius, y + height - radius, radius, 0.0F, 90.0F, 16);
      drawArc(x + radius, y + height - radius, radius, 90.0F, 180.0F, 16);
      drawArc(x + radius, y + radius, radius, 180.0F, 270.0F, 16);
      drawArc(x + width - radius, y + radius, radius, 270.0F, 360.0F, 16);
      GL11.glBegin(4);
      GL11.glVertex2d((double)(x + width - radius), (double)y);
      GL11.glVertex2d((double)(x + radius), (double)y);
      GL11.glVertex2d((double)(x + width - radius), (double)(y + radius));
      GL11.glVertex2d((double)(x + width - radius), (double)(y + radius));
      GL11.glVertex2d((double)(x + radius), (double)y);
      GL11.glVertex2d((double)(x + radius), (double)(y + radius));
      GL11.glVertex2d((double)(x + width), (double)(y + radius));
      GL11.glVertex2d((double)x, (double)(y + radius));
      GL11.glVertex2d((double)x, (double)(y + height - radius));
      GL11.glVertex2d((double)(x + width), (double)(y + radius));
      GL11.glVertex2d((double)x, (double)(y + height - radius));
      GL11.glVertex2d((double)(x + width), (double)(y + height - radius));
      GL11.glVertex2d((double)(x + width - radius), (double)(y + height - radius));
      GL11.glVertex2d((double)(x + radius), (double)(y + height - radius));
      GL11.glVertex2d((double)(x + width - radius), (double)(y + height));
      GL11.glVertex2d((double)(x + width - radius), (double)(y + height));
      GL11.glVertex2d((double)(x + radius), (double)(y + height - radius));
      GL11.glVertex2d((double)(x + radius), (double)(y + height));
      glEnd();
   }

   public static void renderOne(float lineWidth) {
      checkSetupFBO();
      GL11.glPushAttrib(1048575);
      GL11.glDisable(3008);
      GL11.glDisable(3553);
      GL11.glDisable(2896);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glLineWidth(lineWidth);
      GL11.glEnable(2848);
      GL11.glEnable(2960);
      GL11.glClear(1024);
      GL11.glClearStencil(15);
      GL11.glStencilFunc(512, 1, 15);
      GL11.glStencilOp(7681, 7681, 7681);
      GL11.glPolygonMode(1032, 6913);
   }

   public static void renderTwo() {
      GL11.glStencilFunc(512, 0, 15);
      GL11.glStencilOp(7681, 7681, 7681);
      GL11.glPolygonMode(1032, 6914);
   }

   public static void renderThree() {
      GL11.glStencilFunc(514, 1, 15);
      GL11.glStencilOp(7680, 7680, 7680);
      GL11.glPolygonMode(1032, 6913);
   }

   public static void renderFour(Color color) {
      setColor(color);
      GL11.glDepthMask(false);
      GL11.glDisable(2929);
      GL11.glEnable(10754);
      GL11.glPolygonOffset(1.0F, -2000000.0F);
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 240.0F);
   }

   public static void renderFive() {
      GL11.glPolygonOffset(1.0F, 2000000.0F);
      GL11.glDisable(10754);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(2960);
      GL11.glDisable(2848);
      GL11.glHint(3154, 4352);
      GL11.glEnable(3042);
      GL11.glEnable(2896);
      GL11.glEnable(3553);
      GL11.glEnable(3008);
      GL11.glPopAttrib();
   }

   public static void setColor(Color color) {
      GL11.glColor4d((double)color.getRed() / 255.0D, (double)color.getGreen() / 255.0D, (double)color.getBlue() / 255.0D, (double)color.getAlpha() / 255.0D);
   }

   public static void checkSetupFBO() {
      Framebuffer fbo = mc.field_147124_at;
      if (fbo != null && fbo.field_147624_h > -1) {
         setupFBO(fbo);
         fbo.field_147624_h = -1;
      }

   }

   private static void setupFBO(Framebuffer fbo) {
      EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.field_147624_h);
      int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
      EXTFramebufferObject.glBindRenderbufferEXT(36161, stencilDepthBufferID);
      EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, mc.field_71443_c, mc.field_71440_d);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencilDepthBufferID);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencilDepthBufferID);
   }

   static {
      itemRender = mc.func_175599_af();
      camera = new Frustum();
      frustrum = new Frustum();
      depth = GL11.glIsEnabled(2896);
      texture = GL11.glIsEnabled(3042);
      clean = GL11.glIsEnabled(3553);
      bind = GL11.glIsEnabled(2929);
      override = GL11.glIsEnabled(2848);
      screenCoords = BufferUtils.createFloatBuffer(3);
      viewport = BufferUtils.createIntBuffer(16);
      modelView = BufferUtils.createFloatBuffer(16);
      projection = BufferUtils.createFloatBuffer(16);
   }

   public static class RenderTesselator extends Tessellator {
      public static RenderUtil.RenderTesselator INSTANCE = new RenderUtil.RenderTesselator();

      public RenderTesselator() {
         super(2097152);
      }

      public static void prepare(int mode) {
         prepareGL();
         begin(mode);
      }

      public static void prepareGL() {
         GL11.glBlendFunc(770, 771);
         GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.func_187441_d(1.5F);
         GlStateManager.func_179090_x();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179147_l();
         GlStateManager.func_179097_i();
         GlStateManager.func_179140_f();
         GlStateManager.func_179129_p();
         GlStateManager.func_179141_d();
         GlStateManager.func_179124_c(1.0F, 1.0F, 1.0F);
      }

      public static void begin(int mode) {
         INSTANCE.func_178180_c().func_181668_a(mode, DefaultVertexFormats.field_181706_f);
      }

      public static void release() {
         render();
         releaseGL();
      }

      public static void render() {
         INSTANCE.func_78381_a();
      }

      public static void releaseGL() {
         GlStateManager.func_179089_o();
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179098_w();
         GlStateManager.func_179147_l();
         GlStateManager.func_179126_j();
      }

      public static void drawBox(BlockPos blockPos, int argb, int sides) {
         int a = argb >>> 24 & 255;
         int r = argb >>> 16 & 255;
         int g = argb >>> 8 & 255;
         int b = argb & 255;
         drawBox(blockPos, r, g, b, a, sides);
      }

      public static void drawBox(float x, float y, float z, int argb, int sides) {
         int a = argb >>> 24 & 255;
         int r = argb >>> 16 & 255;
         int g = argb >>> 8 & 255;
         int b = argb & 255;
         drawBox(INSTANCE.func_178180_c(), x, y, z, 1.0F, 1.0F, 1.0F, r, g, b, a, sides);
      }

      public static void drawBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
         drawBox(INSTANCE.func_178180_c(), (float)blockPos.func_177958_n(), (float)blockPos.func_177956_o(), (float)blockPos.func_177952_p(), 1.0F, 1.0F, 1.0F, r, g, b, a, sides);
      }

      public static BufferBuilder getBufferBuilder() {
         return INSTANCE.func_178180_c();
      }

      public static void drawBox(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
         if ((sides & 1) != 0) {
            buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 2) != 0) {
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 4) != 0) {
            buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 8) != 0) {
            buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 16) != 0) {
            buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 32) != 0) {
            buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
         }

      }

      public static void drawLines(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
         if ((sides & 17) != 0) {
            buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 18) != 0) {
            buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 33) != 0) {
            buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 34) != 0) {
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 5) != 0) {
            buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 6) != 0) {
            buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 9) != 0) {
            buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 10) != 0) {
            buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 20) != 0) {
            buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 36) != 0) {
            buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 24) != 0) {
            buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
         }

         if ((sides & 40) != 0) {
            buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
         }

      }

      public static void drawBoundingBox(AxisAlignedBB bb, float width, float red, float green, float blue, float alpha) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179147_l();
         GlStateManager.func_179097_i();
         GlStateManager.func_179120_a(770, 771, 0, 1);
         GlStateManager.func_179090_x();
         GlStateManager.func_179132_a(false);
         GL11.glEnable(2848);
         GL11.glHint(3154, 4354);
         GL11.glLineWidth(width);
         Tessellator tessellator = Tessellator.func_178181_a();
         BufferBuilder bufferbuilder = tessellator.func_178180_c();
         bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
         bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
         bufferbuilder.func_181662_b(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c).func_181666_a(red, green, blue, alpha).func_181675_d();
         tessellator.func_78381_a();
         GL11.glDisable(2848);
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179126_j();
         GlStateManager.func_179098_w();
         GlStateManager.func_179084_k();
         GlStateManager.func_179121_F();
      }

      public static void drawFullBox(AxisAlignedBB bb, BlockPos blockPos, float width, int argb, int alpha2) {
         int a = argb >>> 24 & 255;
         int r = argb >>> 16 & 255;
         int g = argb >>> 8 & 255;
         int b = argb & 255;
         drawFullBox(bb, blockPos, width, r, g, b, a, alpha2);
      }

      public static void drawFullBox(AxisAlignedBB bb, BlockPos blockPos, float width, int red, int green, int blue, int alpha, int alpha2) {
         prepare(7);
         drawBox(blockPos, red, green, blue, alpha, 63);
         release();
         drawBoundingBox(bb, width, (float)red, (float)green, (float)blue, (float)alpha2);
      }

      public static void drawHalfBox(BlockPos blockPos, int argb, int sides) {
         int a = argb >>> 24 & 255;
         int r = argb >>> 16 & 255;
         int g = argb >>> 8 & 255;
         int b = argb & 255;
         drawHalfBox(blockPos, r, g, b, a, sides);
      }

      public static void drawHalfBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
         drawBox(INSTANCE.func_178180_c(), (float)blockPos.func_177958_n(), (float)blockPos.func_177956_o(), (float)blockPos.func_177952_p(), 1.0F, 0.5F, 1.0F, r, g, b, a, sides);
      }
   }

   public static final class GeometryMasks {
      public static final HashMap<EnumFacing, Integer> FACEMAP = new HashMap();

      static {
         FACEMAP.put(EnumFacing.DOWN, 1);
         FACEMAP.put(EnumFacing.WEST, 16);
         FACEMAP.put(EnumFacing.NORTH, 4);
         FACEMAP.put(EnumFacing.SOUTH, 8);
         FACEMAP.put(EnumFacing.EAST, 32);
         FACEMAP.put(EnumFacing.UP, 2);
      }

      public static final class Line {
         public static final int DOWN_WEST = 17;
         public static final int UP_WEST = 18;
         public static final int DOWN_EAST = 33;
         public static final int UP_EAST = 34;
         public static final int DOWN_NORTH = 5;
         public static final int UP_NORTH = 6;
         public static final int DOWN_SOUTH = 9;
         public static final int UP_SOUTH = 10;
         public static final int NORTH_WEST = 20;
         public static final int NORTH_EAST = 36;
         public static final int SOUTH_WEST = 24;
         public static final int SOUTH_EAST = 40;
         public static final int ALL = 63;
      }

      public static final class Quad {
         public static final int DOWN = 1;
         public static final int UP = 2;
         public static final int NORTH = 4;
         public static final int SOUTH = 8;
         public static final int WEST = 16;
         public static final int EAST = 32;
         public static final int ALL = 63;
      }
   }
}
