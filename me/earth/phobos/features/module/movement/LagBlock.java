package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.RotationUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LagBlock extends Module {
   private Setting<Boolean> packet = this.register(new Setting("Packet", true));
   private Setting<Boolean> invalidPacket = this.register(new Setting("InvalidPacket", false));
   private Setting<Integer> rotations = this.register(new Setting("Rotations", 5, 1, 10));
   private Setting<Integer> timeOut = this.register(new Setting("TimeOut", 194, 0, 1000));
   private BlockPos startPos;
   private final Timer timer = new Timer();
   private int lastHotbarSlot = -1;
   private int blockSlot = -1;
   private static LagBlock INSTANCE;

   public LagBlock() {
      super("BlockLag", "Lags You back", Module.Category.MOVEMENT, true, false, false);
      INSTANCE = this;
   }

   public static LagBlock getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new LagBlock();
      }

      return INSTANCE;
   }

   public void onEnable() {
      this.lastHotbarSlot = -1;
      this.blockSlot = -1;
      if (fullNullCheck()) {
         this.disable();
      } else {
         this.blockSlot = this.findBlockSlot();
         this.startPos = new BlockPos(mc.field_71439_g.func_174791_d());
         if (BlockUtil.isElseHole(this.startPos) && this.blockSlot != -1) {
            mc.field_71439_g.func_70664_aZ();
            this.timer.reset();
         } else {
            this.disable();
         }
      }
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0 && this.timer.passedMs((long)(Integer)this.timeOut.getValue())) {
         this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         InventoryUtil.switchToHotbarSlot(this.blockSlot, false);

         for(int i = 0; i < (Integer)this.rotations.getValue(); ++i) {
            RotationUtil.faceVector(new Vec3d(this.startPos), true);
         }

         BlockUtil.placeBlock(this.startPos, this.blockSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, false, (Boolean)this.packet.getValue(), mc.field_71439_g.func_70093_af());
         InventoryUtil.switchToHotbarSlot(this.lastHotbarSlot, false);
         if ((Boolean)this.invalidPacket.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, 1337.0D, mc.field_71439_g.field_70161_v, true));
         }

         this.disable();
      }
   }

   private int findBlockSlot() {
      int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
      if (obbySlot == -1) {
         if (InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockObsidian.class)) {
            return -2;
         } else {
            int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            return echestSlot == -1 && InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockEnderChest.class) ? -2 : -1;
         }
      } else {
         return obbySlot;
      }
   }
}
