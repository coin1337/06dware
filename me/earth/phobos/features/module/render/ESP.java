package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.Iterator;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.RenderEntityModelEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class ESP extends Module {
   private final Setting<ESP.Mode> mode;
   private final Setting<Boolean> colorSync;
   private final Setting<Boolean> players;
   private final Setting<Boolean> animals;
   private final Setting<Boolean> mobs;
   private final Setting<Boolean> items;
   private final Setting<Boolean> xporbs;
   private final Setting<Boolean> xpbottles;
   private final Setting<Boolean> pearl;
   private final Setting<Integer> red;
   private final Setting<Integer> green;
   private final Setting<Integer> blue;
   private final Setting<Integer> boxAlpha;
   private final Setting<Integer> alpha;
   private final Setting<Float> lineWidth;
   private final Setting<Boolean> colorFriends;
   private final Setting<Boolean> self;
   private final Setting<Boolean> onTop;
   private final Setting<Boolean> invisibles;
   private static ESP INSTANCE = new ESP();

   public ESP() {
      super("ESP", "Renders a nice ESP.", Module.Category.RENDER, false, false, false);
      this.mode = this.register(new Setting("Mode", ESP.Mode.OUTLINE));
      this.colorSync = this.register(new Setting("Sync", false));
      this.players = this.register(new Setting("Players", true));
      this.animals = this.register(new Setting("Animals", false));
      this.mobs = this.register(new Setting("Mobs", false));
      this.items = this.register(new Setting("Items", false));
      this.xporbs = this.register(new Setting("XpOrbs", false));
      this.xpbottles = this.register(new Setting("XpBottles", false));
      this.pearl = this.register(new Setting("Pearls", false));
      this.red = this.register(new Setting("Red", 255, 0, 255));
      this.green = this.register(new Setting("Green", 255, 0, 255));
      this.blue = this.register(new Setting("Blue", 255, 0, 255));
      this.boxAlpha = this.register(new Setting("BoxAlpha", 120, 0, 255));
      this.alpha = this.register(new Setting("Alpha", 255, 0, 255));
      this.lineWidth = this.register(new Setting("LineWidth", 2.0F, 0.1F, 5.0F));
      this.colorFriends = this.register(new Setting("Friends", true));
      this.self = this.register(new Setting("Self", true));
      this.onTop = this.register(new Setting("onTop", true));
      this.invisibles = this.register(new Setting("Invisibles", false));
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static ESP getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ESP();
      }

      return INSTANCE;
   }

   public void onRender3D(Render3DEvent event) {
      int i;
      Iterator var3;
      Entity entity;
      Vec3d interp;
      AxisAlignedBB bb;
      if ((Boolean)this.items.getValue()) {
         i = 0;
         var3 = mc.field_71441_e.field_72996_f.iterator();

         while(var3.hasNext()) {
            entity = (Entity)var3.next();
            if (entity instanceof EntityItem && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
               interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
               bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
               GlStateManager.func_179094_E();
               GlStateManager.func_179147_l();
               GlStateManager.func_179097_i();
               GlStateManager.func_179120_a(770, 771, 0, 1);
               GlStateManager.func_179090_x();
               GlStateManager.func_179132_a(false);
               GL11.glEnable(2848);
               GL11.glHint(3154, 4354);
               GL11.glLineWidth(1.0F);
               RenderGlobal.func_189696_b(bb, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getRed() / 255.0F : (float)(Integer)this.red.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F : (float)(Integer)this.green.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F : (float)(Integer)this.blue.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getAlpha() : (float)(Integer)this.boxAlpha.getValue() / 255.0F);
               GL11.glDisable(2848);
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179126_j();
               GlStateManager.func_179098_w();
               GlStateManager.func_179084_k();
               GlStateManager.func_179121_F();
               RenderUtil.drawBlockOutline(bb, (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), 1.0F);
               ++i;
               if (i >= 50) {
                  break;
               }
            }
         }
      }

      if ((Boolean)this.xporbs.getValue()) {
         i = 0;
         var3 = mc.field_71441_e.field_72996_f.iterator();

         while(var3.hasNext()) {
            entity = (Entity)var3.next();
            if (entity instanceof EntityXPOrb && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
               interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
               bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
               GlStateManager.func_179094_E();
               GlStateManager.func_179147_l();
               GlStateManager.func_179097_i();
               GlStateManager.func_179120_a(770, 771, 0, 1);
               GlStateManager.func_179090_x();
               GlStateManager.func_179132_a(false);
               GL11.glEnable(2848);
               GL11.glHint(3154, 4354);
               GL11.glLineWidth(1.0F);
               RenderGlobal.func_189696_b(bb, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getRed() / 255.0F : (float)(Integer)this.red.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F : (float)(Integer)this.green.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F : (float)(Integer)this.blue.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0F : (float)(Integer)this.boxAlpha.getValue() / 255.0F);
               GL11.glDisable(2848);
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179126_j();
               GlStateManager.func_179098_w();
               GlStateManager.func_179084_k();
               GlStateManager.func_179121_F();
               RenderUtil.drawBlockOutline(bb, (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), 1.0F);
               ++i;
               if (i >= 50) {
                  break;
               }
            }
         }
      }

      if ((Boolean)this.pearl.getValue()) {
         i = 0;
         var3 = mc.field_71441_e.field_72996_f.iterator();

         while(var3.hasNext()) {
            entity = (Entity)var3.next();
            if (entity instanceof EntityEnderPearl && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
               interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
               bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
               GlStateManager.func_179094_E();
               GlStateManager.func_179147_l();
               GlStateManager.func_179097_i();
               GlStateManager.func_179120_a(770, 771, 0, 1);
               GlStateManager.func_179090_x();
               GlStateManager.func_179132_a(false);
               GL11.glEnable(2848);
               GL11.glHint(3154, 4354);
               GL11.glLineWidth(1.0F);
               RenderGlobal.func_189696_b(bb, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getRed() / 255.0F : (float)(Integer)this.red.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F : (float)(Integer)this.green.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F : (float)(Integer)this.blue.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0F : (float)(Integer)this.boxAlpha.getValue() / 255.0F);
               GL11.glDisable(2848);
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179126_j();
               GlStateManager.func_179098_w();
               GlStateManager.func_179084_k();
               GlStateManager.func_179121_F();
               RenderUtil.drawBlockOutline(bb, (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), 1.0F);
               ++i;
               if (i >= 50) {
                  break;
               }
            }
         }
      }

      if ((Boolean)this.xpbottles.getValue()) {
         i = 0;
         var3 = mc.field_71441_e.field_72996_f.iterator();

         while(var3.hasNext()) {
            entity = (Entity)var3.next();
            if (entity instanceof EntityExpBottle && mc.field_71439_g.func_70068_e(entity) < 2500.0D) {
               interp = EntityUtil.getInterpolatedRenderPos(entity, mc.func_184121_ak());
               bb = new AxisAlignedBB(entity.func_174813_aQ().field_72340_a - 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72338_b - 0.0D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72339_c - 0.05D - entity.field_70161_v + interp.field_72449_c, entity.func_174813_aQ().field_72336_d + 0.05D - entity.field_70165_t + interp.field_72450_a, entity.func_174813_aQ().field_72337_e + 0.1D - entity.field_70163_u + interp.field_72448_b, entity.func_174813_aQ().field_72334_f + 0.05D - entity.field_70161_v + interp.field_72449_c);
               GlStateManager.func_179094_E();
               GlStateManager.func_179147_l();
               GlStateManager.func_179097_i();
               GlStateManager.func_179120_a(770, 771, 0, 1);
               GlStateManager.func_179090_x();
               GlStateManager.func_179132_a(false);
               GL11.glEnable(2848);
               GL11.glHint(3154, 4354);
               GL11.glLineWidth(1.0F);
               RenderGlobal.func_189696_b(bb, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getRed() / 255.0F : (float)(Integer)this.red.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F : (float)(Integer)this.green.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F : (float)(Integer)this.blue.getValue() / 255.0F, (Boolean)this.colorSync.getValue() ? (float)Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0F : (float)(Integer)this.boxAlpha.getValue() / 255.0F);
               GL11.glDisable(2848);
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179126_j();
               GlStateManager.func_179098_w();
               GlStateManager.func_179084_k();
               GlStateManager.func_179121_F();
               RenderUtil.drawBlockOutline(bb, (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), 1.0F);
               ++i;
               if (i >= 50) {
                  break;
               }
            }
         }
      }

   }

   public void onRenderModel(RenderEntityModelEvent event) {
      if (event.getStage() == 0 && event.entity != null && (!event.entity.func_82150_aj() || (Boolean)this.invisibles.getValue()) && ((Boolean)this.self.getValue() || !event.entity.equals(mc.field_71439_g)) && ((Boolean)this.players.getValue() || !(event.entity instanceof EntityPlayer)) && ((Boolean)this.animals.getValue() || !EntityUtil.isPassive(event.entity)) && ((Boolean)this.mobs.getValue() || EntityUtil.isPassive(event.entity) || event.entity instanceof EntityPlayer)) {
         Color color = (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : EntityUtil.getColor(event.entity, (Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue(), (Boolean)this.colorFriends.getValue());
         boolean fancyGraphics = mc.field_71474_y.field_74347_j;
         mc.field_71474_y.field_74347_j = false;
         float gamma = mc.field_71474_y.field_74333_Y;
         mc.field_71474_y.field_74333_Y = 10000.0F;
         if ((Boolean)this.onTop.getValue() && (!Chams.getInstance().isEnabled() || !(Boolean)Chams.getInstance().colored.getValue())) {
            event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
         }

         if (this.mode.getValue() == ESP.Mode.OUTLINE) {
            RenderUtil.renderOne((Float)this.lineWidth.getValue());
            event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
            GlStateManager.func_187441_d((Float)this.lineWidth.getValue());
            RenderUtil.renderTwo();
            event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
            GlStateManager.func_187441_d((Float)this.lineWidth.getValue());
            RenderUtil.renderThree();
            RenderUtil.renderFour(color);
            event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
            GlStateManager.func_187441_d((Float)this.lineWidth.getValue());
            RenderUtil.renderFive();
         } else {
            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            if (this.mode.getValue() == ESP.Mode.WIREFRAME) {
               GL11.glPolygonMode(1032, 6913);
            } else {
               GL11.glPolygonMode(1028, 6913);
            }

            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glDisable(2929);
            GL11.glEnable(2848);
            GL11.glEnable(3042);
            GlStateManager.func_179112_b(770, 771);
            GlStateManager.func_179131_c((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
            GlStateManager.func_187441_d((Float)this.lineWidth.getValue());
            event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
         }

         if (!(Boolean)this.onTop.getValue() && (!Chams.getInstance().isEnabled() || !(Boolean)Chams.getInstance().colored.getValue())) {
            event.modelBase.func_78088_a(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
         }

         try {
            mc.field_71474_y.field_74347_j = fancyGraphics;
            mc.field_71474_y.field_74333_Y = gamma;
         } catch (Exception var6) {
         }

         event.setCanceled(true);
      }
   }

   public static enum Mode {
      WIREFRAME,
      OUTLINE;
   }
}
