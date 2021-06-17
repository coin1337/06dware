package me.earth.phobos.features.modules.client;

import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Cosmetics extends Module {
   public final Cosmetics.ModelBetterPhysicsCape betterPhysicsCape = new Cosmetics.ModelBetterPhysicsCape();
   public final Cosmetics.ModelCloutGoggles cloutGoggles = new Cosmetics.ModelCloutGoggles();
   public final Cosmetics.ModelPhyscisCapes capesModel = new Cosmetics.ModelPhyscisCapes();
   public final Cosmetics.ModelSquidFlag flag = new Cosmetics.ModelSquidFlag();
   public final Cosmetics.TopHatModel hatModel = new Cosmetics.TopHatModel();
   public final Cosmetics.GlassesModel glassesModel = new Cosmetics.GlassesModel();
   private final Cosmetics.HatGlassesModel hatGlassesModel = new Cosmetics.HatGlassesModel();
   public final Cosmetics.SantaHatModel santaHatModel = new Cosmetics.SantaHatModel();
   public final Cosmetics.ModelHatFez fezModel = new Cosmetics.ModelHatFez();
   private final ResourceLocation hatTexture = new ResourceLocation("textures/tophat.png");
   private final ResourceLocation fezTexture = new ResourceLocation("textures/fez.png");
   private final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
   private final ResourceLocation santaHatTexture = new ResourceLocation("textures/santahat.png");
   private final ResourceLocation capeTexture = new ResourceLocation("textures/cape.png");
   private final ResourceLocation squidTexture = new ResourceLocation("textures/squid.png");
   private final ResourceLocation cloutGoggleTexture = new ResourceLocation("textures/cloutgoggles.png");
   private final ResourceLocation squidLauncherTexture = new ResourceLocation("textures/squidlauncher.png");
   public final Cosmetics.ModelSquidLauncher squidLauncher = new Cosmetics.ModelSquidLauncher();
   public static Cosmetics INSTANCE;

   public Cosmetics() {
      super("Cosmetics", "Bitch", Module.Category.CLIENT, true, false, false);
      INSTANCE = this;
   }

   @SubscribeEvent
   public void onRenderPlayer(Post event) {
      if (Phobos.cosmeticsManager.hasCosmetics(event.getEntityPlayer()) && !EntityUtil.isFakePlayer(event.getEntityPlayer())) {
         for(Iterator var2 = Phobos.cosmeticsManager.getRenderModels(event.getEntityPlayer()).iterator(); var2.hasNext(); GlStateManager.func_179121_F()) {
            ModelBase model = (ModelBase)var2.next();
            GlStateManager.func_179094_E();
            RenderManager renderManager = mc.func_175598_ae();
            GlStateManager.func_179137_b(event.getX(), event.getY(), event.getZ());
            double scale = 1.0D;
            double rotate = (double)this.interpolate(event.getEntityPlayer().field_70758_at, event.getEntityPlayer().field_70759_as, event.getPartialRenderTick());
            double rotate1 = (double)this.interpolate(event.getEntityPlayer().field_70127_C, event.getEntityPlayer().field_70125_A, event.getPartialRenderTick());
            double rotate3 = event.getEntityPlayer().func_70093_af() ? 22.0D : 0.0D;
            float limbSwingAmount = this.interpolate(event.getEntityPlayer().field_184618_aE, event.getEntityPlayer().field_70721_aZ, event.getPartialRenderTick());
            float rotate2 = MathHelper.func_76134_b(event.getEntityPlayer().field_184619_aG * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount / 1.0F;
            GL11.glScaled(-scale, -scale, scale);
            GL11.glTranslated(0.0D, -((double)event.getEntityPlayer().field_70131_O - (event.getEntityPlayer().func_70093_af() ? 0.25D : 0.0D) - 0.38D) / scale, 0.0D);
            GL11.glRotated(180.0D + rotate, 0.0D, 1.0D, 0.0D);
            if (!(model instanceof Cosmetics.ModelSquidLauncher)) {
               GL11.glRotated(rotate1, 1.0D, 0.0D, 0.0D);
            }

            if (model instanceof Cosmetics.ModelSquidLauncher) {
               GL11.glRotated(rotate3, 1.0D, 0.0D, 0.0D);
            }

            GlStateManager.func_179137_b(0.0D, -0.45D, 0.0D);
            if (model instanceof Cosmetics.ModelSquidLauncher) {
               GlStateManager.func_179137_b(0.15D, 1.3D, 0.0D);

               ModelRenderer renderer;
               for(Iterator var15 = this.squidLauncher.field_78092_r.iterator(); var15.hasNext(); renderer.field_78795_f = rotate2) {
                  renderer = (ModelRenderer)var15.next();
               }
            }

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
            } else if (model instanceof Cosmetics.ModelCloutGoggles) {
               mc.func_110434_K().func_110577_a(this.cloutGoggleTexture);
               this.cloutGoggles.func_78088_a(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
               mc.func_110434_K().func_147645_c(this.cloutGoggleTexture);
            } else if (model instanceof Cosmetics.ModelSquidFlag) {
               mc.func_110434_K().func_110577_a(this.squidTexture);
               this.flag.func_78088_a(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
               mc.func_110434_K().func_147645_c(this.squidTexture);
            } else if (model instanceof Cosmetics.ModelSquidLauncher) {
               mc.func_110434_K().func_110577_a(this.squidLauncherTexture);
               this.squidLauncher.func_78088_a(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0325F);
               mc.func_110434_K().func_147645_c(this.squidLauncherTexture);
            }
         }

      }
   }

   public float interpolate(float yaw1, float yaw2, float percent) {
      float rotation = (yaw1 + (yaw2 - yaw1) * percent) % 360.0F;
      if (rotation < 0.0F) {
         rotation += 360.0F;
      }

      return rotation;
   }

   public class ModelBetterPhysicsCape extends ModelBase {
      public ModelRenderer segment1;

      public ModelBetterPhysicsCape() {
         this.field_78090_t = 256;
         this.field_78089_u = 128;

         for(int i = 0; i < 160; ++i) {
            ModelRenderer segment = new ModelRenderer(this, 0, i);
            segment.func_78793_a(0.0F, 0.0F, 0.0F);
            segment.func_78790_a(-5.0F, 0.0F + (float)i, 0.0F, 10, 1, 1, 0.0F);
            this.field_78092_r.add(segment);
         }

      }

      public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
         Iterator var8 = this.field_78092_r.iterator();

         while(var8.hasNext()) {
            ModelRenderer model = (ModelRenderer)var8.next();
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(model.field_82906_o, model.field_82908_p, model.field_82907_q);
            GlStateManager.func_179109_b(model.field_78800_c * f5, model.field_78797_d * f5, model.field_78798_e * f5);
            GlStateManager.func_179139_a(1.0D, 0.1D, 1.0D);
            GlStateManager.func_179109_b(-model.field_82906_o, -model.field_82908_p, -model.field_82907_q);
            GlStateManager.func_179109_b(-model.field_78800_c * f5, -model.field_78797_d * f5, -model.field_78798_e * f5);
            model.func_78785_a(f5);
            GlStateManager.func_179121_F();
         }

      }

      public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
         modelRenderer.field_78795_f = x;
         modelRenderer.field_78796_g = y;
         modelRenderer.field_78808_h = z;
      }
   }

   public class ModelCloutGoggles extends ModelBase {
      public ModelRenderer leftGlass;
      public ModelRenderer topLeftFrame;
      public ModelRenderer bottomLeftFrame;
      public ModelRenderer leftLeftFrame;
      public ModelRenderer rightLeftFrame;
      public ModelRenderer rightGlass;
      public ModelRenderer topRightFrame;
      public ModelRenderer bottomLeftFrame_1;
      public ModelRenderer leftRightFrame;
      public ModelRenderer rightRightFrame;
      public ModelRenderer leftEar;
      public ModelRenderer rightEar;

      public ModelCloutGoggles() {
         this.field_78090_t = 64;
         this.field_78089_u = 32;
         this.rightLeftFrame = new ModelRenderer(this, 18, 0);
         this.rightLeftFrame.func_78793_a(-3.0F, 3.0F, -4.0F);
         this.rightLeftFrame.func_78790_a(0.0F, 2.0F, 0.0F, 2, 1, 1, 0.0F);
         this.bottomLeftFrame_1 = new ModelRenderer(this, 26, 5);
         this.bottomLeftFrame_1.func_78793_a(-3.0F, 3.0F, -4.0F);
         this.bottomLeftFrame_1.func_78790_a(4.0F, 2.0F, 0.0F, 2, 1, 1, 0.0F);
         this.leftLeftFrame = new ModelRenderer(this, 10, 5);
         this.leftLeftFrame.func_78793_a(-3.0F, 3.0F, -4.0F);
         this.leftLeftFrame.func_78790_a(2.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
         this.rightGlass = new ModelRenderer(this, 18, 5);
         this.rightGlass.func_78793_a(-3.0F, 3.0F, -4.0F);
         this.rightGlass.func_78790_a(4.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
         this.rightRightFrame = new ModelRenderer(this, 10, 11);
         this.rightRightFrame.func_78793_a(3.0F, 3.0F, -4.0F);
         this.rightRightFrame.func_78790_a(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
         this.leftEar = new ModelRenderer(this, 18, 11);
         this.leftEar.func_78793_a(-3.0F, 3.0F, -4.0F);
         this.leftEar.func_78790_a(-1.2F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
         this.topRightFrame = new ModelRenderer(this, 26, 0);
         this.topRightFrame.func_78793_a(1.0F, 3.0F, -4.0F);
         this.topRightFrame.func_78790_a(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
         this.topLeftFrame = new ModelRenderer(this, 0, 5);
         this.topLeftFrame.func_78793_a(-3.0F, 3.0F, -4.0F);
         this.topLeftFrame.func_78790_a(-1.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
         this.rightEar = new ModelRenderer(this, 28, 11);
         this.rightEar.func_78793_a(-3.0F, 3.0F, -4.0F);
         this.rightEar.func_78790_a(6.2F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
         this.leftGlass = new ModelRenderer(this, 0, 0);
         this.leftGlass.func_78793_a(-3.0F, 3.0F, -4.0F);
         this.leftGlass.func_78790_a(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
         this.bottomLeftFrame = new ModelRenderer(this, 10, 0);
         this.bottomLeftFrame.func_78793_a(-3.0F, 3.0F, -4.0F);
         this.bottomLeftFrame.func_78790_a(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
         this.leftRightFrame = new ModelRenderer(this, 0, 11);
         this.leftRightFrame.func_78793_a(-3.0F, 3.0F, -4.0F);
         this.leftRightFrame.func_78790_a(3.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
      }

      public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
         this.rightLeftFrame.func_78785_a(f5);
         this.bottomLeftFrame_1.func_78785_a(f5);
         this.leftLeftFrame.func_78785_a(f5);
         this.rightGlass.func_78785_a(f5);
         this.rightRightFrame.func_78785_a(f5);
         this.leftEar.func_78785_a(f5);
         this.topRightFrame.func_78785_a(f5);
         this.topLeftFrame.func_78785_a(f5);
         this.rightEar.func_78785_a(f5);
         this.leftGlass.func_78785_a(f5);
         this.bottomLeftFrame.func_78785_a(f5);
         this.leftRightFrame.func_78785_a(f5);
      }

      public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
         modelRenderer.field_78795_f = x;
         modelRenderer.field_78796_g = y;
         modelRenderer.field_78808_h = z;
      }
   }

   public class ModelCosmetic extends ModelBase {
      public ResourceLocation texture;
   }

   public class ModelSquidLauncher extends ModelBase {
      public ModelRenderer barrel;
      public ModelRenderer squid;
      public ModelRenderer secondBarrel;
      public ModelRenderer barrelSide1;
      public ModelRenderer barrelSide2;
      public ModelRenderer barrelSide3;
      public ModelRenderer barrelSide4;
      public ModelRenderer stock;
      public ModelRenderer stockEnd;
      public ModelRenderer trigger;

      public ModelSquidLauncher() {
         this.field_78090_t = 64;
         this.field_78089_u = 32;
         this.barrelSide4 = new ModelRenderer(this, 0, 0);
         this.barrelSide4.func_78793_a(0.5F, 0.0F, 0.0F);
         this.barrelSide4.func_78790_a(0.0F, -2.0F, 0.2F, 4, 5, 1, 0.0F);
         this.setRotateAngle(this.barrelSide4, 0.091106184F, 0.0F, 0.0F);
         this.stock = new ModelRenderer(this, 0, 24);
         this.stock.func_78793_a(0.0F, 0.0F, 0.0F);
         this.stock.func_78790_a(1.5F, 3.0F, 1.5F, 2, 4, 2, 0.0F);
         this.squid = new ModelRenderer(this, 0, 16);
         this.squid.func_78793_a(0.0F, 0.0F, 0.0F);
         this.squid.func_78790_a(1.2F, -11.5F, 0.8F, 3, 4, 3, 0.0F);
         this.setRotateAngle(this.squid, 0.0F, -0.091106184F, 0.0F);
         this.barrelSide2 = new ModelRenderer(this, 18, 14);
         this.barrelSide2.func_78793_a(0.0F, 0.0F, 0.0F);
         this.barrelSide2.func_78790_a(3.8F, -2.5F, 0.5F, 1, 5, 4, 0.0F);
         this.setRotateAngle(this.barrelSide2, 0.0F, 0.0F, 0.091106184F);
         this.secondBarrel = new ModelRenderer(this, 32, 14);
         this.secondBarrel.func_78793_a(0.0F, 0.0F, 0.0F);
         this.secondBarrel.func_78790_a(0.5F, -2.0F, 0.5F, 4, 5, 4, 0.0F);
         this.stockEnd = new ModelRenderer(this, 18, 26);
         this.stockEnd.func_78793_a(0.0F, 0.0F, 0.0F);
         this.stockEnd.func_78790_a(2.0F, 7.0F, 1.5F, 1, 1, 4, 0.0F);
         this.barrelSide1 = new ModelRenderer(this, 18, 14);
         this.barrelSide1.func_78793_a(0.0F, 0.0F, 0.0F);
         this.barrelSide1.func_78790_a(0.2F, -2.0F, 0.5F, 1, 5, 4, 0.0F);
         this.setRotateAngle(this.barrelSide1, 0.0F, 0.0F, -0.091106184F);
         this.barrelSide3 = new ModelRenderer(this, 0, 0);
         this.barrelSide3.func_78793_a(0.0F, 0.0F, 0.0F);
         this.barrelSide3.func_78790_a(0.5F, -2.5F, 3.8F, 4, 5, 1, 0.0F);
         this.setRotateAngle(this.barrelSide3, -0.091106184F, 0.0F, 0.0F);
         this.trigger = new ModelRenderer(this, 40, 0);
         this.trigger.func_78793_a(0.0F, 0.0F, 0.0F);
         this.trigger.func_78790_a(12.0F, 6.6F, 5.4F, 1, 1, 1, 0.0F);
         this.barrel = new ModelRenderer(this, 18, 0);
         this.barrel.func_78793_a(0.0F, 0.0F, 0.0F);
         this.barrel.func_78790_a(0.0F, -8.0F, 0.0F, 5, 6, 5, 0.0F);
         this.field_78092_r.add(this.barrel);
         this.field_78092_r.add(this.squid);
         this.field_78092_r.add(this.secondBarrel);
         this.field_78092_r.add(this.barrelSide1);
         this.field_78092_r.add(this.barrelSide2);
         this.field_78092_r.add(this.barrelSide3);
         this.field_78092_r.add(this.barrelSide4);
         this.field_78092_r.add(this.stock);
         this.field_78092_r.add(this.stockEnd);
         this.field_78092_r.add(this.trigger);
      }

      public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
         this.stock.func_78785_a(f5);
         this.barrelSide1.func_78785_a(f5);
         this.stockEnd.func_78785_a(f5);
         this.secondBarrel.func_78785_a(f5);
         this.barrelSide3.func_78785_a(f5);
         this.squid.func_78785_a(f5);
         this.barrelSide4.func_78785_a(f5);
         this.barrel.func_78785_a(f5);
         this.barrelSide2.func_78785_a(f5);
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(this.trigger.field_82906_o, this.trigger.field_82908_p, this.trigger.field_82907_q);
         GlStateManager.func_179109_b(this.trigger.field_78800_c * f5, this.trigger.field_78797_d * f5, this.trigger.field_78798_e * f5);
         GlStateManager.func_179139_a(0.2D, 1.0D, 0.8D);
         GlStateManager.func_179109_b(-this.trigger.field_82906_o, -this.trigger.field_82908_p, -this.trigger.field_82907_q);
         GlStateManager.func_179109_b(-this.trigger.field_78800_c * f5, -this.trigger.field_78797_d * f5, -this.trigger.field_78798_e * f5);
         this.trigger.func_78785_a(f5);
         GlStateManager.func_179121_F();
      }

      public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
         modelRenderer.field_78795_f = x;
         modelRenderer.field_78796_g = y;
         modelRenderer.field_78808_h = z;
      }
   }

   public class ModelSquidFlag extends ModelBase {
      public ModelRenderer flag;

      public ModelSquidFlag() {
         this.field_78090_t = 64;
         this.field_78089_u = 32;
         this.flag = new ModelRenderer(this, 0, 0);
         this.flag.func_78793_a(0.0F, 0.0F, 0.0F);
         this.flag.func_78790_a(-5.0F, -16.0F, 0.0F, 10, 16, 1, 0.0F);
      }

      public void func_78088_a(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
         this.flag.func_78785_a(f5);
      }

      public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
         modelRenderer.field_78795_f = x;
         modelRenderer.field_78796_g = y;
         modelRenderer.field_78808_h = z;
      }
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
