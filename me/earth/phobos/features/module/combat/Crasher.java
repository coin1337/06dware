package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class Crasher extends Module {
   private final Setting<Boolean> oneDot15 = this.register(new Setting("1.15", false));
   private final Setting<Float> placeRange = this.register(new Setting("PlaceRange", 6.0F, 0.0F, 10.0F));
   private final Setting<Integer> crystals = this.register(new Setting("Packets", 25, 0, 100));
   private final Setting<Integer> coolDown = this.register(new Setting("CoolDown", 400, 0, 1000));
   private final Setting<InventoryUtil.Switch> switchMode;
   public Setting<Integer> sort;
   private boolean offhand;
   private boolean mainhand;
   private final Timer timer;
   private int lastHotbarSlot;
   private boolean switchedItem;
   private boolean chinese;
   private final List<Integer> entityIDs;
   private int currentID;

   public Crasher() {
      super("CrystalCrash", "Attempts to crash chinese AutoCrystals", Module.Category.COMBAT, false, false, true);
      this.switchMode = this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
      this.sort = this.register(new Setting("Sort", 0, 0, 2));
      this.offhand = false;
      this.mainhand = false;
      this.timer = new Timer();
      this.lastHotbarSlot = -1;
      this.switchedItem = false;
      this.chinese = false;
      this.entityIDs = new ArrayList();
      this.currentID = -1000;
   }

   public void onEnable() {
      this.chinese = false;
      if (!fullNullCheck() && this.timer.passedMs((long)(Integer)this.coolDown.getValue())) {
         this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         this.placeCrystals();
         this.disable();
      } else {
         this.disable();
      }
   }

   public void onDisable() {
      if (!fullNullCheck()) {
         Iterator var1 = this.entityIDs.iterator();

         while(var1.hasNext()) {
            int i = (Integer)var1.next();
            mc.field_71441_e.func_73028_b(i);
         }
      }

      this.entityIDs.clear();
      this.currentID = -1000;
      this.timer.reset();
   }

   @SubscribeEvent
   public void onTick(ClientTickEvent event) {
      if (!fullNullCheck() && event.phase != Phase.START && (!this.isOff() || !this.timer.passedMs(10L))) {
         this.switchItem(true);
      }
   }

   private void placeCrystals() {
      this.offhand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
      this.mainhand = mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP;
      int crystalcount = 0;
      List<BlockPos> blocks = BlockUtil.possiblePlacePositions((Float)this.placeRange.getValue(), false, (Boolean)this.oneDot15.getValue());
      if ((Integer)this.sort.getValue() == 1) {
         blocks.sort(Comparator.comparingDouble((hole) -> {
            return mc.field_71439_g.func_174818_b(hole);
         }));
      } else if ((Integer)this.sort.getValue() == 2) {
         blocks.sort(Comparator.comparingDouble((hole) -> {
            return -mc.field_71439_g.func_174818_b(hole);
         }));
      }

      Iterator var3 = blocks.iterator();

      while(var3.hasNext()) {
         BlockPos pos = (BlockPos)var3.next();
         if (this.isOff() || crystalcount >= (Integer)this.crystals.getValue()) {
            break;
         }

         if (BlockUtil.canPlaceCrystal(pos, false, (Boolean)this.oneDot15.getValue())) {
            this.placeCrystal(pos);
            ++crystalcount;
         }
      }

   }

   private void placeCrystal(BlockPos pos) {
      if (!this.chinese && !this.mainhand && !this.offhand && !this.switchItem(false)) {
         this.disable();
      } else {
         RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() - 0.5D, (double)pos.func_177952_p() + 0.5D));
         EnumFacing facing = result != null && result.field_178784_b != null ? result.field_178784_b : EnumFacing.UP;
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, facing, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
         EntityEnderCrystal fakeCrystal = new EntityEnderCrystal(mc.field_71441_e, (double)((float)pos.func_177958_n() + 0.5F), (double)(pos.func_177956_o() + 1), (double)((float)pos.func_177952_p() + 0.5F));
         int newID = this.currentID--;
         this.entityIDs.add(newID);
         mc.field_71441_e.func_73027_a(newID, fakeCrystal);
      }
   }

   private boolean switchItem(boolean back) {
      this.chinese = true;
      if (this.offhand) {
         return true;
      } else {
         boolean[] value = InventoryUtil.switchItemToItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), Items.field_185158_cP);
         this.switchedItem = value[0];
         return value[1];
      }
   }
}
