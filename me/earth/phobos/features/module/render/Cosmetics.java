package me.earth.phobos.features.modules.render;

import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Cosmetics extends Module {
   public final Cosmetics.TopHatModel hatModel = new Cosmetics.TopHatModel();
   public final Cosmetics.GlassesModel glassesModel = new Cosmetics.GlassesModel();
   private final Cosmetics.HatGlassesModel hatGlassesModel = new Cosmetics.HatGlassesModel();
   public final Cosmetics.SantaHatModel santaHatModel = new Cosmetics.SantaHatModel();
   public final Cosmetics.ModelHatFez fezModel = new Cosmetics.ModelHatFez();
   private final ResourceLocation hatTexture = new ResourceLocation("textures/tophat.png");
   private final ResourceLocation fezTexture = new ResourceLocation("textures/fez.png");
   private final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
   private final ResourceLocation santaHatTexture = new ResourceLocation("textures/santahat.png");
   public static Cosmetics INSTANCE;

   public Cosmetics() {
      super("Cosmetics", "Bitch", Module.Category.RENDER, true, false, false);
      INSTANCE = this;
   }

   @SubscribeEvent
   public void onRenderPlayer(Post event) {
      if (Phobos.cosmeticsManager.hasCosmetics(event.getEntityPlayer())) {
         GlStateManager.func_179094_E();
         RenderManager renderManager = mc.func_175598_ae();
         GlStateManager.func_179137_b(event.getX(), event.getY(), event.getZ());
         double scale = 1.0D;
         double rotate = (double)this.interpolate(event.getEntityPlayer().field_70758_at, event.getEntityPlayer().field_70759_as, event.getPartialRenderTick());
         double rotate1 = (double)this.interpolate(event.getEntityPlayer().field_70127_C, event.getEntityPlayer().field_70125_A, event.getPartialRenderTick());
         GL11.glScaled(-scale, -scale, scale);
         GL11.glTranslated(0.0D, -((double)event.getEntityPlayer().field_70131_O - (event.getEntityPlayer().func_70093_af() ? 0.25D : 0.0D) - 0.38D) / scale, 0.0D);
         GL11.glRotated(180.0D + rotate, 0.0D, 1.0D, 0.0D);
         GL11.glRotated(rotate1, 1.0D, 0.0D, 0.0D);
         GlStateManager.func_179137_b(0.0D, -0.45D, 0.0D);
         Iterator var9 = Phobos.cosmeticsManager.getRenderModels(event.getEntityPlayer()).iterator();

         while(var9.hasNext()) {
            ModelBase model = (ModelBase)var9.next();
            if (model instanceof Cosmetics.TopHatModel) {
               mc.func_110434_K().func_110577_a(this.hatTexture);
               this.hatModel.func_78088_a(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
               mc.func_110434_K().func_147645_c(this.hatTexture);
            } else if (model instanceof Cosmetics.GlassesModel) {
               if (event.getEntityPlayer().func_175148_a(EnumPlayerModelParts.HAT)) {
                  mc.func_110434_K().func_110577_a(this.glassesTexture);
                  this.hatGlassesModel.func_78088_a(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                  mc.func_110434_K().func_147645_c(this.glassesTexture);
               } else {
                  mc.func_110434_K().func_110577_a(this.glassesTexture);
                  this.glassesModel.func_78088_a(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                  mc.func_110434_K().func_147645_c(this.glassesTexture);
               }
            } else if (model instanceof Cosmetics.SantaHatModel) {
               mc.func_110434_K().func_110577_a(this.santaHatTexture);
               this.santaHatModel.func_78088_a(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
               mc.func_110434_K().func_147645_c(this.santaHatTexture);
            }
         }

         GlStateManager.func_179121_F();
      }
   }

   public float interpolate(float yaw1, float yaw2, float percent) {
      float rotation = (yaw1 + (yaw2 - yaw1) * percent) % 360.0F;
      if (rotation < 0.0F) {
         rotation += 360.0F;
      }

      return rotation;
   }

   public class SantaHatModel extends ModelBase {
      public ModelRenderer baseLayer;
      public ModelRenderer baseRedLayer;
      public ModelRenderer midRedLayer;
      public ModelRenderer topRedLayer;
      public ModelRenderer lastRedLayer;
      public ModelRenderer realFinalLastLayer;
      public ModelRenderer whiteLayer;

      public SantaHatModel() {
         this.field_78090_t = 64;
         this.field_78089_u = 32;
         this.topRedLayer = new ModelRenderer(this, 46, 0);
         this.topRedLayer.func_78793_a(0.5F, -8.4F, -1.5F);
         this.topRedLayer.func_78790_a(0.0F, 0.0F, 0.0F, 3, 2, 3, 0.0F);
         this.setRotateAngle(this.topRedLayer, 0.0F, 0.0F, 0.5009095F);
         this.baseLayer = new ModelRenderer(this, 0, 0);
         this.baseLayer.func_78793_a(-4.0F, -1.0F, -4.0F);
         this.baseLayer.func_78790_a(0.0F, 0.0F, 0.0F, 8, 2, 8, 0.0F);
         this.midRedLayer = new ModelRenderer(this, 28, 0);
         this.midRedLayer.func_78793_a(-1.2F, -6.8F, -2.0F);
         this.midRedLayer.func_78790_a(0.0F, 0.0F, 0.0F, 4, 3, 4, 0.0F);
         this.setRotateAngle(this.midRedLayer, 0.0F, 0.0F, 0.22759093F);
         this.realFinalLastLayer = new ModelRenderer(this, 46, 8);
         this.realFinalLastLayer.func_78793_a(4.0F, -10.4F, 0.0F);
         this.realFinalLastLayer.func_78790_a(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
         this.setRotateAngle(this.realFinalLastLayer, 0.0F, 0.0F, 1.0016445F);
         this.lastRedLayer = new ModelRenderer(this, 34, 8);
         this.lastRedLayer.func_78793_a(2.0F, -9.4F, 0.0F);
         this.lastRedLayer.func_78790_a(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
         this.setRotateAngle(this.lastRedLayer, 0.0F, 0.0F, 0.8196066F);
         this.whiteLayer = new ModelRenderer(this, 0, 22);
         this.whiteLayer.func_78793_a(4.1F, -9.7F, -0.5F);
         this.whiteLayer.func_78790_a(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
         this.setRotateAngle(this.whiteLayer, -0.091106184F, 0.0F, 0.18203785F);
         this.baseRedLayer = new ModelRenderer(this, 0, 11);
         this.baseRedLayer.func_78793_a(-3.0F, -4.0F, -3.0F);
         this.baseRedLayer.func_78790_a(0.0F, 0.0F, 0.0F, 6, 3, 6, 0.0F);
         this.setRotateAngle(this.baseRedLayer, 0.0F, 0.0F, 0.045553092F);
      }

      public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
         this.topRedLayer.func_78785_a(f5);
         this.baseLayer.func_78785_a(f5);
         this.midRedLayer.func_78785_a(f5);
         this.realFinalLastLayer.func_78785_a(f5);
         this.lastRedLayer.func_78785_a(f5);
         this.whiteLayer.func_78785_a(f5);
         this.baseRedLayer.func_78785_a(f5);
      }

      public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
         modelRenderer.field_78795_f = x;
         modelRenderer.field_78796_g = y;
         modelRenderer.field_78808_h = z;
      }
   }

   public class HatGlassesModel extends ModelBase {
      public final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
      public ModelRenderer firstLeftFrame;
      public ModelRenderer firstRightFrame;
      public ModelRenderer centerBar;
      public ModelRenderer farLeftBar;
      public ModelRenderer farRightBar;
      public ModelRenderer leftEar;
      public ModelRenderer rightEar;

      public HatGlassesModel() {
         this.field_78090_t = 64;
         this.field_78089_u = 64;
         this.farLeftBar = new ModelRenderer(this, 0, 13);
         this.farLeftBar.func_78793_a(-4.0F, 3.5F, -5.0F);
         this.farLeftBar.func_78790_a(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
         this.rightEar = new ModelRenderer(this, 10, 0);
         this.rightEar.func_78793_a(3.2F, 3.5F, -5.0F);
         this.rightEar.func_78790_a(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
         this.centerBar = new ModelRenderer(this, 0, 9);
         this.centerBar.func_78793_a(-1.0F, 3.5F, -5.0F);
         this.centerBar.func_78790_a(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
         this.firstLeftFrame = new ModelRenderer(this, 0, 0);
         this.firstLeftFrame.func_78793_a(-3.0F, 3.0F, -5.0F);
         this.firstLeftFrame.func_78790_a(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
         this.firstRightFrame = new ModelRenderer(this, 0, 5);
         this.firstRightFrame.func_78793_a(1.0F, 3.0F, -5.0F);
         this.firstRightFrame.func_78790_a(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
         this.leftEar = new ModelRenderer(this, 20, 0);
         this.leftEar.func_78793_a(-4.2F, 3.5F, -5.0F);
         this.leftEar.func_78790_a(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
         this.farRightBar = new ModelRenderer(this, 0, 17);
         this.farRightBar.func_78793_a(3.0F, 3.5F, -5.0F);
         this.farRightBar.func_78790_a(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      }

      public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
         this.farLeftBar.func_78785_a(f5);
         this.rightEar.func_78785_a(f5);
         this.centerBar.func_78785_a(f5);
         this.firstLeftFrame.func_78785_a(f5);
         this.firstRightFrame.func_78785_a(f5);
         this.leftEar.func_78785_a(f5);
         this.farRightBar.func_78785_a(f5);
      }

      public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
         modelRenderer.field_78795_f = x;
         modelRenderer.field_78796_g = y;
         modelRenderer.field_78808_h = z;
      }
   }

   public class GlassesModel extends ModelBase {
      public final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
      public ModelRenderer firstLeftFrame;
      public ModelRenderer firstRightFrame;
      public ModelRenderer centerBar;
      public ModelRenderer farLeftBar;
      public ModelRenderer farRightBar;
      public ModelRenderer leftEar;
      public ModelRenderer rightEar;

      public GlassesModel() {
         this.field_78090_t = 64;
         this.field_78089_u = 64;
         this.farLeftBar = new ModelRenderer(this, 0, 13);
         this.farLeftBar.func_78793_a(-4.0F, 3.5F, -4.0F);
         this.farLeftBar.func_78790_a(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
         this.rightEar = new ModelRenderer(this, 10, 0);
         this.rightEar.func_78793_a(3.2F, 3.5F, -4.0F);
         this.rightEar.func_78790_a(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
         this.centerBar = new ModelRenderer(this, 0, 9);
         this.centerBar.func_78793_a(-1.0F, 3.5F, -4.0F);
         this.centerBar.func_78790_a(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
         this.firstLeftFrame = new ModelRenderer(this, 0, 0);
         this.firstLeftFrame.func_78793_a(-3.0F, 3.0F, -4.0F);
         this.firstLeftFrame.func_78790_a(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
         this.firstRightFrame = new ModelRenderer(this, 0, 5);
         this.firstRightFrame.func_78793_a(1.0F, 3.0F, -4.0F);
         this.firstRightFrame.func_78790_a(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
         this.leftEar = new ModelRenderer(this, 20, 0);
         this.leftEar.func_78793_a(-4.2F, 3.5F, -4.0F);
         this.leftEar.func_78790_a(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
         this.farRightBar = new ModelRenderer(this, 0, 17);
         this.farRightBar.func_78793_a(3.0F, 3.5F, -4.0F);
         this.farRightBar.func_78790_a(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      }

      public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
         this.farLeftBar.func_78785_a(f5);
         this.rightEar.func_78785_a(f5);
         this.centerBar.func_78785_a(f5);
         this.firstLeftFrame.func_78785_a(f5);
         this.firstRightFrame.func_78785_a(f5);
         this.leftEar.func_78785_a(f5);
         this.farRightBar.func_78785_a(f5);
      }

      public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
         modelRenderer.field_78795_f = x;
         modelRenderer.field_78796_g = y;
         modelRenderer.field_78808_h = z;
      }
   }

   public class TopHatModel extends ModelBase {
      public final ResourceLocation hatTexture = new ResourceLocation("textures/tophat.png");
      public ModelRenderer bottom;
      public ModelRenderer top;

      public TopHatModel() {
         this.field_78090_t = 64;
         this.field_78089_u = 32;
         this.top = new ModelRenderer(this, 0, 10);
         this.top.func_78790_a(0.0F, 0.0F, 0.0F, 4, 10, 4, 0.0F);
         this.top.func_78793_a(-2.0F, -11.0F, -2.0F);
         this.bottom = new ModelRenderer(this, 0, 0);
         this.bottom.func_78790_a(0.0F, 0.0F, 0.0F, 8, 1, 8, 0.0F);
         this.bottom.func_78793_a(-4.0F, -1.0F, -4.0F);
      }

      public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
         this.top.func_78785_a(f5);
         this.bottom.func_78785_a(f5);
      }

      public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
         modelRenderer.field_78795_f = x;
         modelRenderer.field_78796_g = y;
         modelRenderer.field_78808_h = z;
      }
   }

   public static class ModelHatFez extends ModelBase {
      private ModelRenderer baseLayer;
      private ModelRenderer topLayer;
      private ModelRenderer stringLayer;
      private ModelRenderer danglingStringLayer;
      private ModelRenderer otherDanglingStringLayer;

      public ModelHatFez() {
         this.field_78090_t = 64;
         this.field_78089_u = 32;
         this.baseLayer = new ModelRenderer(this, 1, 1);
         this.baseLayer.func_78789_a(-3.0F, 0.0F, -3.0F, 6, 4, 6);
         this.baseLayer.func_78793_a(0.0F, -4.0F, 0.0F);
         this.baseLayer.func_78787_b(this.field_78090_t, this.field_78089_u);
         this.baseLayer.field_78809_i = true;
         this.setRotation(this.baseLayer, 0.0F, 0.12217305F, 0.0F);
         this.topLayer = new ModelRenderer(this, 1, 1);
         this.topLayer.func_78789_a(0.0F, 0.0F, 0.0F, 1, 1, 1);
         this.topLayer.func_78793_a(-0.5F, -4.75F, -0.5F);
         this.topLayer.func_78787_b(this.field_78090_t, this.field_78089_u);
         this.topLayer.field_78809_i = true;
         this.setRotation(this.topLayer, 0.0F, 0.0F, 0.0F);
         this.stringLayer = new ModelRenderer(this, 25, 1);
         this.stringLayer.func_78789_a(-0.5F, -0.5F, -0.5F, 3, 1, 1);
         this.stringLayer.func_78793_a(0.5F, -3.75F, 0.0F);
         this.stringLayer.func_78787_b(this.field_78090_t, this.field_78089_u);
         this.stringLayer.field_78809_i = true;
         this.setRotation(this.stringLayer, 0.7853982F, 0.0F, 0.0F);
         this.danglingStringLayer = new ModelRenderer(this, 41, 1);
         this.danglingStringLayer.func_78789_a(-0.5F, -0.5F, -0.5F, 3, 1, 1);
         this.danglingStringLayer.func_78793_a(3.0F, -3.5F, 0.0F);
         this.danglingStringLayer.func_78787_b(this.field_78090_t, this.field_78089_u);
         this.danglingStringLayer.field_78809_i = true;
         this.setRotation(this.danglingStringLayer, 0.2268928F, 0.7853982F, 1.2042772F);
         this.otherDanglingStringLayer = new ModelRenderer(this, 33, 9);
         this.otherDanglingStringLayer.func_78789_a(-0.5F, -0.5F, -0.5F, 3, 1, 1);
         this.otherDanglingStringLayer.func_78793_a(3.0F, -3.5F, 0.0F);
         this.otherDanglingStringLayer.func_78787_b(this.field_78090_t, this.field_78089_u);
         this.otherDanglingStringLayer.field_78809_i = true;
         this.setRotation(this.otherDanglingStringLayer, 0.2268928F, -0.9250245F, 1.2042772F);
      }

      public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
         super.func_78088_a(entity, f, f1, f2, f3, f4, f5);
         this.setRotationAngles(f, f1, f2, f3, f4, f5);
         this.baseLayer.func_78785_a(f5);
         this.topLayer.func_78785_a(f5);
         this.stringLayer.func_78785_a(f5);
         this.danglingStringLayer.func_78785_a(f5);
         this.otherDanglingStringLayer.func_78785_a(f5);
      }

      private void setRotation(ModelRenderer model, float x, float y, float z) {
         model.field_78795_f = x;
         model.field_78796_g = y;
         model.field_78808_h = z;
      }

      public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5) {
         super.func_78087_a(f, f1, f2, f3, f4, f5, (Entity)null);
      }
   }
}
