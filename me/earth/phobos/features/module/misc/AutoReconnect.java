package me.earth.phobos.features.modules.misc;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoReconnect extends Module {
   private static ServerData serverData;
   private static AutoReconnect INSTANCE = new AutoReconnect();
   private final Setting<Integer> delay = this.register(new Setting("Delay", 5));

   public AutoReconnect() {
      super("AutoReconnect", "Reconnects you if you disconnect.", Module.Category.MISC, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static AutoReconnect getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new AutoReconnect();
      }

      return INSTANCE;
   }

   @SubscribeEvent
   public void sendPacket(GuiOpenEvent event) {
      if (event.getGui() instanceof GuiDisconnected) {
         this.updateLastConnectedServer();
         if (AutoLog.getInstance().isOff()) {
            GuiDisconnected disconnected = (GuiDisconnected)event.getGui();
            event.setGui(new AutoReconnect.GuiDisconnectedHook(disconnected));
         }
      }

   }

   @SubscribeEvent
   public void onWorldUnload(Unload event) {
      this.updateLastConnectedServer();
   }

   public void updateLastConnectedServer() {
      ServerData data = mc.func_147104_D();
      if (data != null) {
         serverData = data;
      }

   }

   private class GuiDisconnectedHook extends GuiDisconnected {
      private final Timer timer = new Timer();

      public GuiDisconnectedHook(GuiDisconnected disconnected) {
         super(disconnected.field_146307_h, disconnected.field_146306_a, disconnected.field_146304_f);
         this.timer.reset();
      }

      public void func_73876_c() {
         if (this.timer.passedS((double)(Integer)AutoReconnect.this.delay.getValue())) {
            this.field_146297_k.func_147108_a(new GuiConnecting(this.field_146307_h, this.field_146297_k, AutoReconnect.serverData == null ? this.field_146297_k.field_71422_O : AutoReconnect.serverData));
         }

      }

      public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
         super.func_73863_a(mouseX, mouseY, partialTicks);
         String s = "Reconnecting in " + MathUtil.round((double)((long)((Integer)AutoReconnect.this.delay.getValue() * 1000) - this.timer.getPassedTimeMs()) / 1000.0D, 1);
         AutoReconnect.this.renderer.drawString(s, (float)(this.field_146294_l / 2 - AutoReconnect.this.renderer.getStringWidth(s) / 2), (float)(this.field_146295_m - 16), 16777215, true);
      }
   }
}
