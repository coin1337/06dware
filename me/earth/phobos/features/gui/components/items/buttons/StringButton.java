package me.earth.phobos.features.gui.components.items.buttons;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

public class StringButton extends Button {
   private Setting setting;
   public boolean isListening;
   private StringButton.CurrentString currentString = new StringButton.CurrentString("");

   public StringButton(Setting setting) {
      super(setting.getName());
      this.setting = setting;
      this.width = 15;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if ((Boolean)ClickGui.getInstance().rainbowRolling.getValue()) {
         int color = ColorUtil.changeAlpha((Integer)HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)), (Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue());
         int color1 = ColorUtil.changeAlpha((Integer)HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)), (Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue());
         RenderUtil.drawGradientRect(this.x, this.y, (float)this.width + 7.4F, (float)this.height - 0.5F, this.getState() ? (!this.isHovering(mouseX, mouseY) ? (Integer)HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)) : color) : (!this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515), this.getState() ? (!this.isHovering(mouseX, mouseY) ? (Integer)HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)) : color1) : (!this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
      } else {
         RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4F, this.y + (float)this.height - 0.5F, this.getState() ? (!this.isHovering(mouseX, mouseY) ? Phobos.colorManager.getColorWithAlpha((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()) : Phobos.colorManager.getColorWithAlpha((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
      }

      if (this.isListening) {
         Phobos.textManager.drawStringWithShadow(this.currentString.getString() + Phobos.textManager.getIdleSign(), this.x + 2.3F, this.y - 1.7F - (float)PhobosGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
      } else {
         Phobos.textManager.drawStringWithShadow((this.setting.shouldRenderName() ? this.setting.getName() + " " + "§7" : "") + this.setting.getValue(), this.x + 2.3F, this.y - 1.7F - (float)PhobosGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
      }

   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.isHovering(mouseX, mouseY)) {
         mc.func_147118_V().func_147682_a(PositionedSoundRecord.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
      }

   }

   public void onKeyTyped(char typedChar, int keyCode) {
      if (this.isListening) {
         if (keyCode == 1) {
            return;
         }

         if (keyCode == 28) {
            this.enterString();
         } else if (keyCode == 14) {
            this.setString(removeLastChar(this.currentString.getString()));
         } else if (keyCode == 47 && (Keyboard.isKeyDown(157) || Keyboard.isKeyDown(29))) {
            try {
               this.setString(this.currentString.getString() + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
            } catch (Exception var4) {
               var4.printStackTrace();
            }
         } else if (ChatAllowedCharacters.func_71566_a(typedChar)) {
            this.setString(this.currentString.getString() + typedChar);
         }
      }

   }

   public void update() {
      this.setHidden(!this.setting.isVisible());
   }

   private void enterString() {
      if (this.currentString.getString().isEmpty()) {
         this.setting.setValue(this.setting.getDefaultValue());
      } else {
         this.setting.setValue(this.currentString.getString());
      }

      this.setString("");
      super.onMouseClick();
   }

   public int getHeight() {
      return 14;
   }

   public void toggle() {
      this.isListening = !this.isListening;
   }

   public boolean getState() {
      return !this.isListening;
   }

   public void setString(String newString) {
      this.currentString = new StringButton.CurrentString(newString);
   }

   public static String removeLastChar(String str) {
      String output = "";
      if (str != null && str.length() > 0) {
         output = str.substring(0, str.length() - 1);
      }

      return output;
   }

   public static class CurrentString {
      private String string;

      public CurrentString(String string) {
         this.string = string;
      }

      public String getString() {
         return this.string;
      }
   }
}
