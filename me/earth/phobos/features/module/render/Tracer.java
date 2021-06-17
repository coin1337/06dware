package me.earth.phobos.features.modules.render;

import java.awt.Color;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Tracer extends Module {
   public Setting<Boolean> players = this.register(new Setting("Players", true));
   public Setting<Boolean> mobs = this.register(new Setting("Mobs", false));
   public Setting<Boolean> animals = this.register(new Setting("Animals", false));
   public Setting<Boolean> invisibles = this.register(new Setting("Invisibles", false));
   public Setting<Boolean> drawFromSky = this.register(new Setting("DrawFromSky", false));
   public Setting<Float> width = this.register(new Setting("Width", 1.0F, 0.1F, 5.0F));
   public Setting<Integer> distance = this.register(new Setting("Radius", 300, 0, 300));
   public Setting<Boolean> crystalCheck = this.register(new Setting("CrystalCheck", false));

   public Tracer() {
      super("Tracers", "Draws lines to other players.", Module.Category.RENDER, false, false, false);
   }

   public void onRender3D(Render3DEvent event) {
      if (!fullNullCheck()) {
         GlStateManager.func_179094_E();
         mc.field_71441_e.field_72996_f.stream().filter(EntityUtil::isLiving).filter((entity) -> {
            return entity instanceof EntityPlayer ? (Boolean)this.players.getValue() && mc.field_71439_g != entity : (EntityUtil.isPassive(entity) ? (Boolean)this.animals.getValue() : (Boolean)this.mobs.getValue());
         }).filter((entity) -> {
            return mc.field_71439_g.func_70068_e(entity) < MathUtil.square((float)(Integer)this.distance.getValue());
         }).filter((entity) -> {
            return (Boolean)this.invisibles.getValue() || !entity.func_82150_aj();
         }).forEach((entity) -> {
            float[] colour = this.getColorByDistance(entity);
            this.drawLineToEntity(entity, colour[0], colour[1], colour[2], colour[3]);
         });
         GlStateManager.func_179121_F();
      }
   }

   public double interpolate(double now, double then) {
      return then + (now - then) * (double)mc.func_184121_ak();
   }

   public double[] interpolate(Entity entity) {
      double posX = this.interpolate(entity.field_70165_t, entity.field_70142_S) - mc.func_175598_ae().field_78725_b;
      double posY = this.interpolate(entity.field_70163_u, entity.field_70137_T) - mc.func_175598_ae().field_78726_c;
      double posZ = this.interpolate(entity.field_70161_v, entity.field_70136_U) - mc.func_175598_ae().field_78723_d;
      return new double[]{posX, posY, posZ};
   }

   public void drawLineToEntity(Entity e, float red, float green, float blue, float opacity) {
      double[] xyz = this.interpolate(e);
      this.drawLine(xyz[0], xyz[1], xyz[2], (double)e.field_70131_O, red, green, blue, opacity);
   }

   public void drawLine(double posx, double posy, double posz, double up, float red, float green, float blue, float opacity) {
      Vec3d eyes = (new Vec3d(0.0D, 0.0D, 1.0D)).func_178789_a(-((float)Math.toRadians((double)mc.field_71439_g.field_70125_A))).func_178785_b(-((float)Math.toRadians((double)mc.field_71439_g.field_70177_z)));
      if (!(Boolean)this.drawFromSky.getValue()) {
         this.drawLineFromPosToPos(eyes.field_72450_a, eyes.field_72448_b + (double)mc.field_71439_g.func_70047_e(), eyes.field_72449_c, posx, posy, posz, up, red, green, blue, opacity);
      } else {
         this.drawLineFromPosToPos(posx, 256.0D, posz, posx, posy, posz, up, red, green, blue, opacity);
      }

   }

   public void drawLineFromPosToPos(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity) {
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth((Float)this.width.getValue());
      GL11.glDisable(3553);
      GL11.glDisable(2929);
      GL11.glDepthMask(false);
      GL11.glColor4f(red, green, blue, opacity);
      GlStateManager.func_179140_f();
      GL11.glLoadIdentity();
      mc.field_71460_t.func_78467_g(mc.func_184121_ak());
      GL11.glBegin(1);
      GL11.glVertex3d(posx, posy, posz);
      GL11.glVertex3d(posx2, posy2, posz2);
      GL11.glVertex3d(posx2, posy2, posz2);
      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glEnable(2929);
      GL11.glDepthMask(true);
      GL11.glDisable(3042);
      GL11.glColor3d(1.0D, 1.0D, 1.0D);
      GlStateManager.func_179145_e();
   }

   public float[] getColorByDistance(Entity entity) {
      if (entity instanceof EntityPlayer && Phobos.friendManager.isFriend(entity.func_70005_c_())) {
         return new float[]{0.0F, 0.5F, 1.0F, 1.0F};
      } else {
         AutoCrystal autoCrystal = (AutoCrystal)Phobos.moduleManager.getModuleByClass(AutoCrystal.class);
         Color col = new Color(Color.HSBtoRGB((float)(Math.max(0.0D, Math.min(mc.field_71439_g.func_70068_e(entity), (Boolean)this.crystalCheck.getValue() ? (double)((Float)autoCrystal.placeRange.getValue() * (Float)autoCrystal.placeRange.getValue()) : 2500.0D) / (double)((Boolean)this.crystalCheck.getValue() ? (Float)autoCrystal.placeRange.getValue() * (Float)autoCrystal.placeRange.getValue() : 2500.0F)) / 3.0D), 1.0F, 0.8F) | -16777216);
         return new float[]{(float)col.getRed() / 255.0F, (float)col.getGreen() / 255.0F, (float)col.getBlue() / 255.0F, 1.0F};
      }
   }
}
