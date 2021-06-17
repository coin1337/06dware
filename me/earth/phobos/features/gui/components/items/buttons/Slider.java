package me.earth.phobos.features.gui.components.items.buttons;

import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.gui.components.Component;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import org.lwjgl.input.Mouse;

public class Slider extends Button {
   public Setting setting;
   private Number min;
   private Number max;
   private int difference;

   public Slider(Setting setting) {
      super(setting.getName());
      this.setting = setting;
      this.min = (Number)setting.getMin();
      this.max = (Number)setting.getMax();
      this.difference = this.max.intValue() - this.min.intValue();
      this.width = 15;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.dragSetting(mouseX, mouseY);
      RenderUtil.drawRect(this.x, this.y, this.x + (float)this.width + 7.4F, this.y + (float)this.height - 0.5F, !this.isHovering(mouseX, mouseY) ? 290805077 : -2007673515);
      if ((Boolean)ClickGui.getInstance().rainbowRolling.getValue()) {
         int color = ColorUtil.changeAlpha((Integer)HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)), (Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue());
         int color1 = ColorUtil.changeAlpha((Integer)HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)), (Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue());
         RenderUtil.drawGradientRect(this.x, this.y, ((Number)this.setting.getValue()).floatValue() <= this.min.floatValue() ? 0.0F : ((float)this.width + 7.4F) * this.partialMultiplier(), (float)this.height - 0.5F, !this.isHovering(mouseX, mouseY) ? (Integer)HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)) : color, !this.isHovering(mouseX, mouseY) ? (Integer)HUD.getInstance().colorMap.get(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)) : color1);
      } else {
         RenderUtil.drawRect(this.x, this.y, ((Number)this.setting.getValue()).floatValue() <= this.min.floatValue() ? this.x : this.x + ((float)this.width + 7.4F) * this.partialMultiplier(), this.y + (float)this.height - 0.5F, !this.isHovering(mouseX, mouseY) ? Phobos.colorManager.getColorWithAlpha((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()) : Phobos.colorManager.getColorWithAlpha((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()));
      }

      Phobos.textManager.drawStringWithShadow(this.getName() + " " + "§7" + (this.setting.getValue() instanceof Float ? (Number)this.setting.getValue() : ((Number)this.setting.getValue()).doubleValue()), this.x + 2.3F, this.y - 1.7F - (float)PhobosGui.getClickGui().getTextOffset(), -1);
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.isHovering(mouseX, mouseY)) {
         this.setSettingFromX(mouseX);
      }

   }

   public boolean isHovering(int mouseX, int mouseY) {
      Iterator var3 = PhobosGui.getClickGui().getComponents().iterator();

      while(var3.hasNext()) {
         Component component = (Component)var3.next();
         if (component.drag) {
            return false;
         }
      }

      return (float)mouseX >= this.getX() && (float)mouseX <= this.getX() + (float)this.getWidth() + 8.0F && (float)mouseY >= this.getY() && (float)mouseY <= this.getY() + (float)this.height;
   }

   public void update() {
      this.setHidden(!this.setting.isVisible());
   }

   private void dragSetting(int mouseX, int mouseY) {
      if (this.isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
         this.setSettingFromX(mouseX);
      }

   }

   public int getHeight() {
      return 14;
   }

   private void setSettingFromX(int mouseX) {
      float percent = ((float)mouseX - this.x) / ((float)this.width + 7.4F);
      if (this.setting.getValue() instanceof Double) {
         double result = (Double)this.setting.getMin() + (double)((float)this.difference * percent);
         this.setting.setValue((double)Math.round(10.0D * result) / 10.0D);
      } else if (this.setting.getValue() instanceof Float) {
         float result = (Float)this.setting.getMin() + (float)this.difference * percent;
         this.setting.setValue((float)Math.round(10.0F * result) / 10.0F);
      } else if (this.setting.getValue() instanceof Integer) {
         this.setting.setValue((Integer)this.setting.getMin() + (int)((float)this.difference * percent));
      }

   }

   private float middle() {
      return this.max.floatValue() - this.min.floatValue();
   }

   private float part() {
      return ((Number)this.setting.getValue()).floatValue() - this.min.floatValue();
   }

   private float partialMultiplier() {
      return this.part() / this.middle();
   }
}
