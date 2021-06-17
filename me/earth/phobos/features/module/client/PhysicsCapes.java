package me.earth.phobos.features.modules.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.earth.phobos.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class PhysicsCapes extends Module {
   public PhysicsCapes.ModelPhyscisCapes cape = new PhysicsCapes.ModelPhyscisCapes();
   private final ResourceLocation capeTexture = new ResourceLocation("textures/cape.png");

   public PhysicsCapes() {
      super("PhysicsCapes", "Capes with superior physics", Module.Category.CLIENT, true, false, false);
   }

   @SubscribeEvent
   public void onPlayerRender(Post event) {
      GlStateManager.func_179094_E();
      float f11 = (float)System.currentTimeMillis() / 1000.0F;
      Map<ModelRenderer, Float> waveMap = new HashMap();
      float fuck = f11;

      for(Iterator var5 = this.cape.field_78092_r.iterator(); var5.hasNext(); ++fuck) {
         ModelRenderer renderer = (ModelRenderer)var5.next();
         waveMap.put(renderer, (float)Math.sin((double)fuck / 0.5D) * 4.0F);
      }

      double rotate = (double)this.interpolate(event.getEntityPlayer().field_70760_ar, event.getEntityPlayer().field_70761_aq, event.getPartialRenderTick());
      GlStateManager.func_179109_b(0.0F, 0.0F, 0.125F);
      double d0 = event.getEntityPlayer().field_71091_bM + (event.getEntityPlayer().field_71094_bP - event.getEntityPlayer().field_71091_bM) * (double)event.getPartialRenderTick() - (event.getEntityPlayer().field_70169_q + (event.getEntityPlayer().field_70165_t - event.getEntityPlayer().field_70169_q) * (double)event.getPartialRenderTick());
      double d1 = event.getEntityPlayer().field_71096_bN + (event.getEntityPlayer().field_71095_bQ - event.getEntityPlayer().field_71096_bN) * (double)event.getPartialRenderTick() - (event.getEntityPlayer().field_70167_r + (event.getEntityPlayer().field_70163_u - event.getEntityPlayer().field_70167_r) * (double)event.getPartialRenderTick());
      double d2 = event.getEntityPlayer().field_71097_bO + (event.getEntityPlayer().field_71085_bR - event.getEntityPlayer().field_71097_bO) * (double)event.getPartialRenderTick() - (event.getEntityPlayer().field_70166_s + (event.getEntityPlayer().field_70161_v - event.getEntityPlayer().field_70166_s) * (double)event.getPartialRenderTick());
      float f = event.getEntityPlayer().field_70760_ar + (event.getEntityPlayer().field_70761_aq - event.getEntityPlayer().field_70760_ar) * event.getPartialRenderTick();
      double d3 = (double)MathHelper.func_76126_a(f * 0.017453292F);
      double d4 = (double)(-MathHelper.func_76134_b(f * 0.017453292F));
      float f1 = (float)d1 * 10.0F;
      f1 = MathHelper.func_76131_a(f1, -6.0F, 32.0F);
      float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
      float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      float f4 = event.getEntityPlayer().field_71107_bF + (event.getEntityPlayer().field_71109_bG - event.getEntityPlayer().field_71107_bF) * event.getPartialRenderTick();
      f1 += MathHelper.func_76126_a((event.getEntityPlayer().field_70141_P + (event.getEntityPlayer().field_70140_Q - event.getEntityPlayer().field_70141_P) * event.getPartialRenderTick()) * 6.0F) * 32.0F * f4;
      if (event.getEntityPlayer().func_70093_af()) {
         f1 += 25.0F;
      }

      GL11.glRotated(-rotate, 0.0D, 1.0D, 0.0D);
      GlStateManager.func_179114_b(180.0F, 1.0F, 0.0F, 0.0F);
      GL11.glTranslated(0.0D, -((double)event.getEntityPlayer().field_70131_O - (event.getEntityPlayer().func_70093_af() ? 0.25D : 0.0D) - 0.38D), 0.0D);
      GlStateManager.func_179114_b(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179114_b(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
      Iterator var22;
      ModelRenderer renderer;
      if (mc.field_71439_g.field_191988_bg == 0.0F && mc.field_71439_g.field_70702_br == 0.0F) {
         for(var22 = this.cape.field_78092_r.iterator(); var22.hasNext(); renderer.field_78795_f = 0.0F) {
            renderer = (ModelRenderer)var22.next();
         }
      } else {
         for(var22 = this.cape.field_78092_r.iterator(); var22.hasNext(); renderer.field_78795_f = (Float)waveMap.get(renderer)) {
            renderer = (ModelRenderer)var22.next();
         }
      }

      Minecraft.func_71410_x().func_110434_K().func_110577_a(this.capeTexture);
      this.cape.func_78088_a(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      Minecraft.func_71410_x().func_110434_K().func_147645_c(this.capeTexture);
      GlStateManager.func_179121_F();
   }

   public float interpolate(float yaw1, float yaw2, float percent) {
      float rotation = (yaw1 + (yaw2 - yaw1) * percent) % 360.0F;
      if (rotation < 0.0F) {
         rotation += 360.0F;
      }

      return rotation;
   }

   public class ModelPhyscisCapes extends ModelBase {
      public ModelRenderer shape1;
      public ModelRenderer shape2;
      public ModelRenderer shape3;
      public ModelRenderer shape4;
      public ModelRenderer shape5;
      public ModelRenderer shape6;
      public ModelRenderer shape7;
      public ModelRenderer shape8;
      public ModelRenderer shape9;
      public ModelRenderer shape10;
      public ModelRenderer shape11;
      public ModelRenderer shape12;
      public ModelRenderer shape13;
      public ModelRenderer shape14;
      public ModelRenderer shape15;
      public ModelRenderer shape16;

      public ModelPhyscisCapes() {
         this.field_78090_t = 64;
         this.field_78089_u = 32;
         this.shape9 = new ModelRenderer(this, 0, 8);
         this.shape9.func_78793_a(-5.0F, 8.0F, -1.0F);
         this.shape9.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape15 = new ModelRenderer(this, 0, 14);
         this.shape15.func_78793_a(-5.0F, 14.0F, -1.0F);
         this.shape15.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape3 = new ModelRenderer(this, 0, 2);
         this.shape3.func_78793_a(-5.0F, 2.0F, -1.0F);
         this.shape3.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape7 = new ModelRenderer(this, 0, 6);
         this.shape7.func_78793_a(-5.0F, 6.0F, -1.0F);
         this.shape7.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape1 = new ModelRenderer(this, 0, 0);
         this.shape1.func_78793_a(-5.0F, 0.0F, -1.0F);
         this.shape1.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape6 = new ModelRenderer(this, 0, 5);
         this.shape6.func_78793_a(-5.0F, 5.0F, -1.0F);
         this.shape6.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape14 = new ModelRenderer(this, 0, 13);
         this.shape14.func_78793_a(-5.0F, 13.0F, -1.0F);
         this.shape14.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape10 = new ModelRenderer(this, 0, 9);
         this.shape10.func_78793_a(-5.0F, 9.0F, -1.0F);
         this.shape10.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape13 = new ModelRenderer(this, 0, 12);
         this.shape13.func_78793_a(-5.0F, 12.0F, -1.0F);
         this.shape13.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape4 = new ModelRenderer(this, 0, 3);
         this.shape4.func_78793_a(-5.0F, 3.0F, -1.0F);
         this.shape4.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape8 = new ModelRenderer(this, 0, 7);
         this.shape8.func_78793_a(-5.0F, 7.0F, -1.0F);
         this.shape8.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape16 = new ModelRenderer(this, 0, 15);
         this.shape16.func_78793_a(-5.0F, 15.0F, -1.0F);
         this.shape16.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape12 = new ModelRenderer(this, 0, 11);
         this.shape12.func_78793_a(-5.0F, 11.0F, -1.0F);
         this.shape12.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape5 = new ModelRenderer(this, 0, 4);
         this.shape5.func_78793_a(-5.0F, 4.0F, -1.0F);
         this.shape5.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape11 = new ModelRenderer(this, 0, 10);
         this.shape11.func_78793_a(-5.0F, 10.0F, -1.0F);
         this.shape11.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.shape2 = new ModelRenderer(this, 0, 1);
         this.shape2.func_78793_a(-5.0F, 1.0F, -1.0F);
         this.shape2.func_78790_a(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
         this.field_78092_r.add(this.shape1);
         this.field_78092_r.add(this.shape2);
         this.field_78092_r.add(this.shape3);
         this.field_78092_r.add(this.shape4);
         this.field_78092_r.add(this.shape5);
         this.field_78092_r.add(this.shape6);
         this.field_78092_r.add(this.shape7);
         this.field_78092_r.add(this.shape8);
         this.field_78092_r.add(this.shape9);
         this.field_78092_r.add(this.shape10);
         this.field_78092_r.add(this.shape11);
         this.field_78092_r.add(this.shape12);
         this.field_78092_r.add(this.shape13);
         this.field_78092_r.add(this.shape14);
         this.field_78092_r.add(this.shape15);
         this.field_78092_r.add(this.shape16);
      }

      public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
         this.shape9.func_78785_a(f5);
         this.shape15.func_78785_a(f5);
         this.shape3.func_78785_a(f5);
         this.shape7.func_78785_a(f5);
         this.shape1.func_78785_a(f5);
         this.shape6.func_78785_a(f5);
         this.shape14.func_78785_a(f5);
         this.shape10.func_78785_a(f5);
         this.shape13.func_78785_a(f5);
         this.shape4.func_78785_a(f5);
         this.shape8.func_78785_a(f5);
         this.shape16.func_78785_a(f5);
         this.shape12.func_78785_a(f5);
         this.shape5.func_78785_a(f5);
         this.shape11.func_78785_a(f5);
         this.shape2.func_78785_a(f5);
      }

      public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
         modelRenderer.field_78795_f = x;
         modelRenderer.field_78796_g = y;
         modelRenderer.field_78808_h = z;
      }
   }
}
