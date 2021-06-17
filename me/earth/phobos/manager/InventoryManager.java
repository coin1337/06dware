package me.earth.phobos.manager;

import me.earth.phobos.util.Util;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryManager implements Util {
   private int recoverySlot = -1;

   public void update() {
      if (this.recoverySlot != -1) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(this.recoverySlot == 8 ? 7 : this.recoverySlot + 1));
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(this.recoverySlot));
         mc.field_71439_g.field_71071_by.field_70461_c = this.recoverySlot;
         mc.field_71442_b.func_78750_j();
         this.recoverySlot = -1;
      }

   }

   public void recoverSilent(int slot) {
      this.recoverySlot = slot;
   }
}
