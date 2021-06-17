package me.earth.phobos.features.gui.components.items.buttons;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton extends Button {
   private Setting setting;
   public boolean isListening;

   public BindButton(Setting setting) {
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
         RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4F, this.y + (float)this.height - 0.5F, this.getState() ? (!this.isHovering(mouseX, mouseY) ? Phobos.colorManager.getColorWithAlpha((Integer)((ClickGui)Phobos.moduleManager.getModuleByName("ClickGui")).hoverAlpha.getValue()) : Phobos.colorManager.getColorWithAlpha((Integer)((ClickGui)Phobos.moduleManager.getModuleByName("ClickGui")).alpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
      }

      if (this.isListening) {
         Phobos.textManager.drawStringWithShadow("Listening...", this.x + 2.3F, this.y - 1.7F - (float)PhobosGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
      } else {
         Phobos.textManager.drawStringWithShadow(this.setting.getName() + " " + "§7" + ((Bind)this.setting.getValue()).toString(), this.x + 2.3F, this.y - 1.7F - (float)PhobosGui.getClickGui().getTextOffset(), this.getState() ? -1 : -5592406);
      }

   }

   public void update() {
      this.setHidden(!this.setting.isVisible());
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.isHovering(mouseX, mouseY)) {
         mc.func_147118_V().func_147682_a(PositionedSoundRecord.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
      }

   }

   public void onKeyTyped(char typedChar, int keyCode) {
      if (this.isListening) {
         Bind bind = new Bind(keyCode);
         if (bind.toString().equalsIgnoreCase("Escape")) {
            return;
         }

         if (bind.toString().equalsIgnoreCase("Delete")) {
            bind = new Bind(-1);
         }

         this.setting.setValue(bind);
         super.onMouseClick();
      }

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
}
