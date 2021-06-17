package me.earth.phobos.manager;

import java.awt.Font;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.gui.font.CustomFont;
import me.earth.phobos.features.modules.client.FontMod;
import me.earth.phobos.util.Timer;
import net.minecraft.util.math.MathHelper;

public class TextManager extends Feature {
   private CustomFont customFont = new CustomFont(new Font("Verdana", 0, 17), true, false);
   public int scaledWidth;
   public int scaledHeight;
   public int scaleFactor;
   private final Timer idleTimer = new Timer();
   private boolean idling;

   public TextManager() {
      this.updateResolution();
   }

   public void init(boolean startup) {
      FontMod cFont = (FontMod)Phobos.moduleManager.getModuleByClass(FontMod.class);

      try {
         this.setFontRenderer(new Font((String)cFont.fontName.getValue(), (Integer)cFont.fontStyle.getValue(), (Integer)cFont.fontSize.getValue()), (Boolean)cFont.antiAlias.getValue(), (Boolean)cFont.fractionalMetrics.getValue());
      } catch (Exception var4) {
      }

   }

   public void drawStringWithShadow(String text, float x, float y, int color) {
      this.drawString(text, x, y, color, true);
   }

   public float drawString(String text, float x, float y, int color, boolean shadow) {
      if (Phobos.moduleManager.isModuleEnabled(FontMod.class)) {
         return shadow ? this.customFont.drawStringWithShadow(text, (double)x, (double)y, color) : this.customFont.drawString(text, x, y, color);
      } else {
         return (float)mc.field_71466_p.func_175065_a(text, x, y, color, shadow);
      }
   }

   public int getStringWidth(String text) {
      return Phobos.moduleManager.isModuleEnabled(FontMod.class) ? this.customFont.getStringWidth(text) : mc.field_71466_p.func_78256_a(text);
   }

   public int getFontHeight() {
      if (Phobos.moduleManager.isModuleEnabled(FontMod.class)) {
         String text = "A";
         return this.customFont.getStringHeight(text);
      } else {
         return mc.field_71466_p.field_78288_b;
      }
   }

   public void setFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
      this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
   }

   public Font getCurrentFont() {
      return this.customFont.getFont();
   }

   public void updateResolution() {
      this.scaledWidth = mc.field_71443_c;
      this.scaledHeight = mc.field_71440_d;
      this.scaleFactor = 1;
      boolean flag = mc.func_152349_b();
      int i = mc.field_71474_y.field_74335_Z;
      if (i == 0) {
         i = 1000;
      }

      while(this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
         ++this.scaleFactor;
      }

      if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
         --this.scaleFactor;
      }

      double scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
      double scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
      this.scaledWidth = MathHelper.func_76143_f(scaledWidthD);
      this.scaledHeight = MathHelper.func_76143_f(scaledHeightD);
   }

   public String getIdleSign() {
      if (this.idleTimer.passedMs(500L)) {
         this.idling = !this.idling;
         this.idleTimer.reset();
      }

      return this.idling ? "_" : "";
   }
}
