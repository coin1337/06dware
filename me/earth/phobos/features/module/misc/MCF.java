package me.earth.phobos.features.modules.misc;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.ServerModule;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class MCF extends Module {
   private final Setting<Boolean> middleClick = this.register(new Setting("MiddleClick", true));
   private final Setting<Boolean> keyboard = this.register(new Setting("Keyboard", false));
   private final Setting<Boolean> server = this.register(new Setting("Server", true));
   private final Setting<Bind> key = this.register(new Setting("KeyBind", new Bind(-1), (v) -> {
      return (Boolean)this.keyboard.getValue();
   }));
   private boolean clicked = false;

   public MCF() {
      super("MCF", "Middleclick Friends.", Module.Category.MISC, true, false, false);
   }

   public void onUpdate() {
      if (Mouse.isButtonDown(2)) {
         if (!this.clicked && (Boolean)this.middleClick.getValue() && mc.field_71462_r == null) {
            this.onClick();
         }

         this.clicked = true;
      } else {
         this.clicked = false;
      }

   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onKeyInput(KeyInputEvent event) {
      if ((Boolean)this.keyboard.getValue() && Keyboard.getEventKeyState() && !(mc.field_71462_r instanceof PhobosGui) && ((Bind)this.key.getValue()).getKey() == Keyboard.getEventKey()) {
         this.onClick();
      }

   }

   private void onClick() {
      RayTraceResult result = mc.field_71476_x;
      if (result != null && result.field_72313_a == Type.ENTITY) {
         Entity entity = result.field_72308_g;
         if (entity instanceof EntityPlayer) {
            if (Phobos.friendManager.isFriend(entity.func_70005_c_())) {
               Phobos.friendManager.removeFriend(entity.func_70005_c_());
               Command.sendMessage("§c" + entity.func_70005_c_() + "§r" + " unfriended.");
               if ((Boolean)this.server.getValue() && ServerModule.getInstance().isConnected()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketChatMessage("@Serverprefix" + (String)ClickGui.getInstance().prefix.getValue()));
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketChatMessage("@Server" + (String)ClickGui.getInstance().prefix.getValue() + "friend del " + entity.func_70005_c_()));
               }
            } else {
               Phobos.friendManager.addFriend(entity.func_70005_c_());
               Command.sendMessage("§b" + entity.func_70005_c_() + "§r" + " friended.");
               if ((Boolean)this.server.getValue() && ServerModule.getInstance().isConnected()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketChatMessage("@Serverprefix" + (String)ClickGui.getInstance().prefix.getValue()));
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketChatMessage("@Server" + (String)ClickGui.getInstance().prefix.getValue() + "friend add " + entity.func_70005_c_()));
               }
            }
         }
      }

      this.clicked = true;
   }
}
