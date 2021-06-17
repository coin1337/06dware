package me.earth.phobos.features.gui.custom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import me.earth.phobos.Phobos;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiCustomMainScreen extends GuiScreen {
   private ResourceLocation resourceLocation = new ResourceLocation("textures/background.png");
   private final String backgroundURL = "https://i.imgur.com/GCJRhiA.png";
   private int y;
   private int x;
   private int singleplayerWidth;
   private int multiplayerWidth;
   private int settingsWidth;
   private int exitWidth;
   private int textHeight;
   private float xOffset;
   private float yOffset;

   public void func_73866_w_() {
      this.field_146292_n.clear();
      this.x = this.field_146294_l / 2;
      this.y = this.field_146295_m / 4 + 48;
      this.field_146292_n.add(new GuiCustomMainScreen.TextButton(0, this.x, this.y + 20, "Singleplayer"));
      this.field_146292_n.add(new GuiCustomMainScreen.TextButton(1, this.x, this.y + 44, "Multiplayer"));
      this.field_146292_n.add(new GuiCustomMainScreen.TextButton(2, this.x, this.y + 66, "Settings"));
      this.field_146292_n.add(new GuiCustomMainScreen.TextButton(2, this.x, this.y + 88, "Exit"));
   }

   protected void func_146284_a(GuiButton button) {
   }

   public void func_73864_a(int mouseX, int mouseY, int mouseButton) {
      if (isHovered(this.x - Phobos.textManager.getStringWidth("Singleplayer") / 2, this.y + 20, Phobos.textManager.getStringWidth("Singleplayer"), Phobos.textManager.getFontHeight(), mouseX, mouseY)) {
         this.field_146297_k.func_147108_a(new GuiWorldSelection(this));
      } else if (isHovered(this.x - Phobos.textManager.getStringWidth("Multiplayer") / 2, this.y + 44, Phobos.textManager.getStringWidth("Multiplayer"), Phobos.textManager.getFontHeight(), mouseX, mouseY)) {
         this.field_146297_k.func_147108_a(new GuiMultiplayer(this));
      } else if (isHovered(this.x - Phobos.textManager.getStringWidth("Settings") / 2, this.y + 66, Phobos.textManager.getStringWidth("Settings"), Phobos.textManager.getFontHeight(), mouseX, mouseY)) {
         this.field_146297_k.func_147108_a(new GuiOptions(this, this.field_146297_k.field_71474_y));
      } else if (isHovered(this.x - Phobos.textManager.getStringWidth("Exit") / 2, this.y + 88, Phobos.textManager.getStringWidth("Exit"), Phobos.textManager.getFontHeight(), mouseX, mouseY)) {
         this.field_146297_k.func_71400_g();
      }

   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      this.xOffset = -1.0F * (((float)mouseX - (float)this.field_146294_l / 2.0F) / ((float)this.field_146294_l / 32.0F));
      this.yOffset = -1.0F * (((float)mouseY - (float)this.field_146295_m / 2.0F) / ((float)this.field_146295_m / 18.0F));
      this.x = this.field_146294_l / 2;
      this.y = this.field_146295_m / 4 + 48;
      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
      this.field_146297_k.func_110434_K().func_110577_a(this.resourceLocation);
      drawCompleteImage(-16.0F + this.xOffset, -9.0F + this.yOffset, (float)(this.field_146294_l + 32), (float)(this.field_146295_m + 18));
      this.field_146297_k.func_110434_K().func_147645_c(this.resourceLocation);
      GlStateManager.func_179147_l();
      GlStateManager.func_179090_x();
      super.func_73863_a(mouseX, mouseY, partialTicks);
   }

   public static void drawCompleteImage(float posX, float posY, float width, float height) {
      GL11.glPushMatrix();
      GL11.glTranslatef(posX, posY, 0.0F);
      GL11.glBegin(7);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(0.0F, height, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(width, height, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(width, 0.0F, 0.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
   }

   public BufferedImage parseBackground(BufferedImage background) {
      int width = 1920;
      int height = 1080;
      int srcWidth = background.getWidth();

      for(int srcHeight = background.getHeight(); width < srcWidth || height < srcHeight; height *= 2) {
         width *= 2;
      }

      BufferedImage imgNew = new BufferedImage(width, height, 2);
      Graphics g = imgNew.getGraphics();
      g.drawImage(background, 0, 0, (ImageObserver)null);
      g.dispose();
      return imgNew;
   }

   public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
      return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height;
   }

   private static class TextButton extends GuiButton {
      public TextButton(int buttonId, int x, int y, String buttonText) {
         super(buttonId, x, y, Phobos.textManager.getStringWidth(buttonText), Phobos.textManager.getFontHeight(), buttonText);
      }

      public void func_191745_a(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
         if (this.field_146125_m) {
            this.field_146124_l = true;
            this.field_146123_n = (float)mouseX >= (float)this.field_146128_h - (float)Phobos.textManager.getStringWidth(this.field_146126_j) / 2.0F && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g;
            Phobos.textManager.drawStringWithShadow(this.field_146126_j, (float)this.field_146128_h - (float)Phobos.textManager.getStringWidth(this.field_146126_j) / 2.0F, (float)this.field_146129_i, Color.WHITE.getRGB());
            if (this.field_146123_n) {
               RenderUtil.drawLine((float)(this.field_146128_h - 1) - (float)Phobos.textManager.getStringWidth(this.field_146126_j) / 2.0F, (float)(this.field_146129_i + 2 + Phobos.textManager.getFontHeight()), (float)this.field_146128_h + (float)Phobos.textManager.getStringWidth(this.field_146126_j) / 2.0F + 1.0F, (float)(this.field_146129_i + 2 + Phobos.textManager.getFontHeight()), 1.0F, Color.WHITE.getRGB());
            }
         }

      }

      public boolean func_146116_c(Minecraft mc, int mouseX, int mouseY) {
         return this.field_146124_l && this.field_146125_m && (float)mouseX >= (float)this.field_146128_h - (float)Phobos.textManager.getStringWidth(this.field_146126_j) / 2.0F && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g;
      }
   }
}
