package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Ranges extends Module {
   private final Setting<Boolean> hitSpheres = this.register(new Setting("HitSpheres", false));
   private final Setting<Boolean> circle = this.register(new Setting("Circle", true));
   private final Setting<Boolean> ownSphere = this.register(new Setting("OwnSphere", false, (v) -> {
      return (Boolean)this.hitSpheres.getValue();
   }));
   private final Setting<Boolean> raytrace = this.register(new Setting("RayTrace", false, (v) -> {
      return (Boolean)this.circle.getValue();
   }));
   private final Setting<Float> lineWidth = this.register(new Setting("LineWidth", 1.5F, 0.1F, 5.0F));
   private final Setting<Double> radius = this.register(new Setting("Radius", 4.5D, 0.1D, 8.0D));

   public Ranges() {
      super("Ranges", "Draws a circle around the player.", Module.Category.RENDER, false, false, false);
   }

   public void onRender3D(Render3DEvent event) {
      if ((Boolean)this.circle.getValue()) {
         GlStateManager.func_179094_E();
         RenderUtil.GLPre((Float)this.lineWidth.getValue());
         GlStateManager.func_179147_l();
         GlStateManager.func_187441_d(3.0F);
         GlStateManager.func_179090_x();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179097_i();
         GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         RenderManager renderManager = mc.func_175598_ae();
         Color color = Color.RED;
         List<Vec3d> hVectors = new ArrayList();
         double x = mc.field_71439_g.field_70142_S + (mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70142_S) * (double)event.getPartialTicks() - renderManager.field_78725_b;
         double y = mc.field_71439_g.field_70137_T + (mc.field_71439_g.field_70163_u - mc.field_71439_g.field_70137_T) * (double)event.getPartialTicks() - renderManager.field_78726_c;
         double z = mc.field_71439_g.field_70136_U + (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70136_U) * (double)event.getPartialTicks() - renderManager.field_78723_d;
         GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
         GL11.glLineWidth((Float)this.lineWidth.getValue());
         GL11.glBegin(1);

         int j;
         for(j = 0; j <= 360; ++j) {
            Vec3d vec = new Vec3d(x + Math.sin((double)j * 3.141592653589793D / 180.0D) * (Double)this.radius.getValue(), y + 0.1D, z + Math.cos((double)j * 3.141592653589793D / 180.0D) * (Double)this.radius.getValue());
            RayTraceResult result = mc.field_71441_e.func_147447_a(new Vec3d(x, y + 0.1D, z), vec, false, true, false);
            if (result != null && (Boolean)this.raytrace.getValue()) {
               hVectors.add(result.field_72307_f);
            } else {
               hVectors.add(vec);
            }
         }

         for(j = 0; j < hVectors.size() - 1; ++j) {
            GL11.glVertex3d(((Vec3d)hVectors.get(j)).field_72450_a, ((Vec3d)hVectors.get(j)).field_72448_b, ((Vec3d)hVectors.get(j)).field_72449_c);
            GL11.glVertex3d(((Vec3d)hVectors.get(j + 1)).field_72450_a, ((Vec3d)hVectors.get(j + 1)).field_72448_b, ((Vec3d)hVectors.get(j + 1)).field_72449_c);
         }

         GL11.glEnd();
         GlStateManager.func_179117_G();
         GlStateManager.func_179126_j();
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179098_w();
         GlStateManager.func_179084_k();
         RenderUtil.GlPost();
         GlStateManager.func_179121_F();
      }

      if ((Boolean)this.hitSpheres.getValue()) {
         Iterator var14 = mc.field_71441_e.field_73010_i.iterator();

         while(true) {
            EntityPlayer player;
            do {
               do {
                  if (!var14.hasNext()) {
                     return;
                  }

                  player = (EntityPlayer)var14.next();
               } while(player == null);
            } while(player.equals(mc.field_71439_g) && !(Boolean)this.ownSphere.getValue());

            Vec3d interpolated = EntityUtil.interpolateEntity(player, event.getPartialTicks());
            if (Phobos.friendManager.isFriend(player.func_70005_c_())) {
               GL11.glColor4f(0.15F, 0.15F, 1.0F, 1.0F);
            } else if (mc.field_71439_g.func_70032_d(player) >= 64.0F) {
               GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
            } else {
               GL11.glColor4f(1.0F, mc.field_71439_g.func_70032_d(player) / 150.0F, 0.0F, 1.0F);
            }

            RenderUtil.drawSphere(interpolated.field_72450_a, interpolated.field_72448_b, interpolated.field_72449_c, ((Double)this.radius.getValue()).floatValue(), 20, 15);
         }
      }
   }
}
