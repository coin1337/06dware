package me.earth.phobos.features.modules.movement;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.KeyEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlowDown extends Module {
   public Setting<Boolean> guiMove = this.register(new Setting("GuiMove", true));
   public Setting<Boolean> noSlow = this.register(new Setting("NoSlow", true));
   public Setting<Boolean> soulSand = this.register(new Setting("SoulSand", true));
   public Setting<Boolean> strict = this.register(new Setting("Strict", false));
   public Setting<Boolean> webs = this.register(new Setting("Webs", false));
   public final Setting<Double> webHorizontalFactor = this.register(new Setting("WebHSpeed", 2.0D, 0.0D, 100.0D));
   public final Setting<Double> webVerticalFactor = this.register(new Setting("WebVSpeed", 2.0D, 0.0D, 100.0D));
   private static NoSlowDown INSTANCE = new NoSlowDown();
   private static KeyBinding[] keys;

   public NoSlowDown() {
      super("NoSlowDown", "Prevents you from getting slowed down.", Module.Category.MOVEMENT, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static NoSlowDown getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new NoSlowDown();
      }

      return INSTANCE;
   }

   public void onUpdate() {
      if ((Boolean)this.guiMove.getValue()) {
         KeyBinding[] var1;
         int var2;
         int var3;
         KeyBinding bind;
         if (!(mc.field_71462_r instanceof GuiOptions) && !(mc.field_71462_r instanceof GuiVideoSettings) && !(mc.field_71462_r instanceof GuiScreenOptionsSounds) && !(mc.field_71462_r instanceof GuiContainer) && !(mc.field_71462_r instanceof GuiIngameMenu)) {
            if (mc.field_71462_r == null) {
               var1 = keys;
               var2 = var1.length;

               for(var3 = 0; var3 < var2; ++var3) {
                  bind = var1[var3];
                  if (!Keyboard.isKeyDown(bind.func_151463_i())) {
                     KeyBinding.func_74510_a(bind.func_151463_i(), false);
                  }
               }
            }
         } else {
            var1 = keys;
            var2 = var1.length;

            for(var3 = 0; var3 < var2; ++var3) {
               bind = var1[var3];
               KeyBinding.func_74510_a(bind.func_151463_i(), Keyboard.isKeyDown(bind.func_151463_i()));
            }
         }
      }

      if ((Boolean)this.webs.getValue() && ((Flight)Phobos.moduleManager.getModuleByClass(Flight.class)).isDisabled() && ((Phase)Phobos.moduleManager.getModuleByClass(Phase.class)).isDisabled() && mc.field_71439_g.field_70134_J) {
         EntityPlayerSP var10000 = mc.field_71439_g;
         var10000.field_70159_w *= (Double)this.webHorizontalFactor.getValue();
         var10000 = mc.field_71439_g;
         var10000.field_70179_y *= (Double)this.webHorizontalFactor.getValue();
         var10000 = mc.field_71439_g;
         var10000.field_70181_x *= (Double)this.webVerticalFactor.getValue();
      }

   }

   @SubscribeEvent
   public void onInput(InputUpdateEvent event) {
      if ((Boolean)this.noSlow.getValue() && mc.field_71439_g.func_184587_cr() && !mc.field_71439_g.func_184218_aH()) {
         MovementInput var10000 = event.getMovementInput();
         var10000.field_78902_a *= 5.0F;
         var10000 = event.getMovementInput();
         var10000.field_192832_b *= 5.0F;
      }

   }

   @SubscribeEvent
   public void onKeyEvent(KeyEvent event) {
      if ((Boolean)this.guiMove.getValue() && event.getStage() == 0 && !(mc.field_71462_r instanceof GuiChat)) {
         event.info = event.pressed;
      }

   }

   @SubscribeEvent
   public void onPacket(PacketEvent.Send event) {
      if (event.getPacket() instanceof CPacketPlayer && (Boolean)this.strict.getValue() && (Boolean)this.noSlow.getValue() && mc.field_71439_g.func_184587_cr() && !mc.field_71439_g.func_184218_aH()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v)), EnumFacing.DOWN));
      }

   }

   static {
      keys = new KeyBinding[]{mc.field_71474_y.field_74351_w, mc.field_71474_y.field_74368_y, mc.field_71474_y.field_74370_x, mc.field_71474_y.field_74366_z, mc.field_71474_y.field_74314_A, mc.field_71474_y.field_151444_V};
   }
}
