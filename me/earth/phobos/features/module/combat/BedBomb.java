package me.earth.phobos.features.modules.combat;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RotationUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BedBomb extends Module {
   private final Setting<Boolean> place = this.register(new Setting("Place", false));
   private final Setting<Integer> placeDelay = this.register(new Setting("Placedelay", 50, 0, 500, (v) -> {
      return (Boolean)this.place.getValue();
   }));
   private final Setting<Float> placeRange = this.register(new Setting("PlaceRange", 6.0F, 1.0F, 10.0F, (v) -> {
      return (Boolean)this.place.getValue();
   }));
   private final Setting<Boolean> extraPacket = this.register(new Setting("InsanePacket", false, (v) -> {
      return (Boolean)this.place.getValue();
   }));
   private final Setting<Boolean> packet = this.register(new Setting("Packet", false, (v) -> {
      return (Boolean)this.place.getValue();
   }));
   private final Setting<Boolean> explode = this.register(new Setting("Break", true));
   private final Setting<Integer> breakDelay = this.register(new Setting("Breakdelay", 50, 0, 500, (v) -> {
      return (Boolean)this.explode.getValue();
   }));
   private final Setting<Float> breakRange = this.register(new Setting("BreakRange", 6.0F, 1.0F, 10.0F, (v) -> {
      return (Boolean)this.explode.getValue();
   }));
   private final Setting<Float> minDamage = this.register(new Setting("MinDamage", 5.0F, 1.0F, 36.0F, (v) -> {
      return (Boolean)this.explode.getValue();
   }));
   private final Setting<Float> range = this.register(new Setting("Range", 10.0F, 1.0F, 12.0F, (v) -> {
      return (Boolean)this.explode.getValue();
   }));
   private final Setting<Boolean> suicide = this.register(new Setting("Suicide", false, (v) -> {
      return (Boolean)this.explode.getValue();
   }));
   private final Setting<Boolean> removeTiles = this.register(new Setting("RemoveTiles", false));
   private final Setting<Boolean> rotate = this.register(new Setting("Rotate", false));
   private final Setting<BedBomb.Logic> logic;
   private final Timer breakTimer;
   private final Timer placeTimer;
   private EntityPlayer target;
   private boolean sendRotationPacket;
   private final AtomicDouble yaw;
   private final AtomicDouble pitch;
   private final AtomicBoolean shouldRotate;
   private BlockPos maxPos;
   private int lastHotbarSlot;
   private int bedSlot;

   public BedBomb() {
      super("BedBomb", "AutoPlace and Break for beds", Module.Category.COMBAT, true, false, false);
      this.logic = this.register(new Setting("Logic", BedBomb.Logic.BREAKPLACE, (v) -> {
         return (Boolean)this.place.getValue() && (Boolean)this.explode.getValue();
      }));
      this.breakTimer = new Timer();
      this.placeTimer = new Timer();
      this.target = null;
      this.sendRotationPacket = false;
      this.yaw = new AtomicDouble(-1.0D);
      this.pitch = new AtomicDouble(-1.0D);
      this.shouldRotate = new AtomicBoolean(false);
      this.maxPos = null;
      this.lastHotbarSlot = -1;
      this.bedSlot = -1;
   }

   @SubscribeEvent
   public void onPacket(PacketEvent.Send event) {
      if (this.shouldRotate.get() && event.getPacket() instanceof CPacketPlayer) {
         CPacketPlayer packet = (CPacketPlayer)event.getPacket();
         packet.field_149476_e = (float)this.yaw.get();
         packet.field_149473_f = (float)this.pitch.get();
         this.shouldRotate.set(false);
      }

   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0 && !fullNullCheck() && (mc.field_71439_g.field_71093_bK == -1 || mc.field_71439_g.field_71093_bK == 1)) {
         this.doBedBomb();
      }
   }

   private void doBedBomb() {
      switch((BedBomb.Logic)this.logic.getValue()) {
      case BREAKPLACE:
         this.mapBeds();
         this.breakBeds();
         this.placeBeds();
         break;
      case PLACEBREAK:
         this.mapBeds();
         this.placeBeds();
         this.breakBeds();
      }

   }

   private void breakBeds() {
      if ((Boolean)this.explode.getValue() && this.breakTimer.passedMs((long)(Integer)this.breakDelay.getValue()) && this.maxPos != null) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         BlockUtil.rightClickBlockLegit(this.maxPos, (Float)this.range.getValue(), (Boolean)this.rotate.getValue() && !(Boolean)this.place.getValue(), EnumHand.MAIN_HAND, this.yaw, this.pitch, this.shouldRotate, true);
         if (mc.field_71439_g.func_70093_af()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
         }

         this.breakTimer.reset();
      }

   }

   private void mapBeds() {
      this.maxPos = null;
      float maxDamage = 0.5F;
      Iterator var7;
      EntityPlayer player;
      float damage;
      BlockPos pos;
      float selfDamage;
      if ((Boolean)this.removeTiles.getValue()) {
         List<BedBomb.BedData> removedBlocks = new ArrayList();
         Iterator var3 = mc.field_71441_e.field_147482_g.iterator();

         while(var3.hasNext()) {
            TileEntity tile = (TileEntity)var3.next();
            if (tile instanceof TileEntityBed) {
               TileEntityBed bed = (TileEntityBed)tile;
               BedBomb.BedData data = new BedBomb.BedData(tile.func_174877_v(), mc.field_71441_e.func_180495_p(tile.func_174877_v()), bed, bed.func_193050_e());
               removedBlocks.add(data);
            }
         }

         var3 = removedBlocks.iterator();

         BedBomb.BedData data;
         while(var3.hasNext()) {
            data = (BedBomb.BedData)var3.next();
            mc.field_71441_e.func_175698_g(data.getPos());
         }

         var3 = removedBlocks.iterator();

         label147:
         while(true) {
            do {
               do {
                  do {
                     if (!var3.hasNext()) {
                        var3 = removedBlocks.iterator();

                        while(var3.hasNext()) {
                           data = (BedBomb.BedData)var3.next();
                           mc.field_71441_e.func_175656_a(data.getPos(), data.getState());
                        }

                        return;
                     }

                     data = (BedBomb.BedData)var3.next();
                  } while(!data.isHeadPiece());

                  pos = data.getPos();
               } while(!(mc.field_71439_g.func_174818_b(pos) <= MathUtil.square((Float)this.breakRange.getValue())));

               selfDamage = DamageUtil.calculateDamage((BlockPos)pos, mc.field_71439_g);
            } while(!((double)selfDamage + 1.0D < (double)EntityUtil.getHealth(mc.field_71439_g)) && DamageUtil.canTakeDamage((Boolean)this.suicide.getValue()));

            var7 = mc.field_71441_e.field_73010_i.iterator();

            while(true) {
               do {
                  do {
                     do {
                        if (!var7.hasNext()) {
                           continue label147;
                        }

                        player = (EntityPlayer)var7.next();
                     } while(!(player.func_174818_b(pos) < MathUtil.square((Float)this.range.getValue())));
                  } while(!EntityUtil.isValid(player, (double)((Float)this.range.getValue() + (Float)this.breakRange.getValue())));

                  damage = DamageUtil.calculateDamage((BlockPos)pos, player);
               } while(!(damage > selfDamage) && (!(damage > (Float)this.minDamage.getValue()) || DamageUtil.canTakeDamage((Boolean)this.suicide.getValue())) && !(damage > EntityUtil.getHealth(player)));

               if (damage > maxDamage) {
                  maxDamage = damage;
                  this.maxPos = pos;
               }
            }
         }
      } else {
         Iterator var10 = mc.field_71441_e.field_147482_g.iterator();

         label101:
         while(true) {
            do {
               do {
                  TileEntityBed bed;
                  do {
                     TileEntity tile;
                     do {
                        if (!var10.hasNext()) {
                           return;
                        }

                        tile = (TileEntity)var10.next();
                     } while(!(tile instanceof TileEntityBed));

                     bed = (TileEntityBed)tile;
                  } while(!bed.func_193050_e());

                  pos = bed.func_174877_v();
               } while(!(mc.field_71439_g.func_174818_b(pos) <= MathUtil.square((Float)this.breakRange.getValue())));

               selfDamage = DamageUtil.calculateDamage((BlockPos)pos, mc.field_71439_g);
            } while(!((double)selfDamage + 1.0D < (double)EntityUtil.getHealth(mc.field_71439_g)) && DamageUtil.canTakeDamage((Boolean)this.suicide.getValue()));

            var7 = mc.field_71441_e.field_73010_i.iterator();

            while(true) {
               do {
                  do {
                     do {
                        if (!var7.hasNext()) {
                           continue label101;
                        }

                        player = (EntityPlayer)var7.next();
                     } while(!(player.func_174818_b(pos) < MathUtil.square((Float)this.range.getValue())));
                  } while(!EntityUtil.isValid(player, (double)((Float)this.range.getValue() + (Float)this.breakRange.getValue())));

                  damage = DamageUtil.calculateDamage((BlockPos)pos, player);
               } while(!(damage > selfDamage) && (!(damage > (Float)this.minDamage.getValue()) || DamageUtil.canTakeDamage((Boolean)this.suicide.getValue())) && !(damage > EntityUtil.getHealth(player)));

               if (damage > maxDamage) {
                  maxDamage = damage;
                  this.maxPos = pos;
               }
            }
         }
      }
   }

   private void placeBeds() {
      if ((Boolean)this.place.getValue() && this.placeTimer.passedMs((long)(Integer)this.placeDelay.getValue()) && this.maxPos == null) {
         this.bedSlot = this.findBedSlot();
         if (this.bedSlot == -1) {
            if (mc.field_71439_g.func_184592_cb().func_77973_b() != Items.field_151104_aV) {
               return;
            }

            this.bedSlot = -2;
         }

         this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         this.target = EntityUtil.getClosestEnemy((double)(Float)this.placeRange.getValue());
         if (this.target != null) {
            BlockPos targetPos = new BlockPos(this.target.func_174791_d());
            this.placeBed(targetPos, true);
         }
      }

   }

   private void placeBed(BlockPos pos, boolean firstCheck) {
      if (mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150324_C) {
         float damage = DamageUtil.calculateDamage((BlockPos)pos, mc.field_71439_g);
         if ((double)damage > (double)EntityUtil.getHealth(mc.field_71439_g) + 0.5D) {
            if (firstCheck) {
               this.placeBed(pos.func_177984_a(), false);
            }

         } else if (!mc.field_71441_e.func_180495_p(pos).func_185904_a().func_76222_j()) {
            if (firstCheck) {
               this.placeBed(pos.func_177984_a(), false);
            }

         } else {
            List<BlockPos> positions = new ArrayList();
            Map<BlockPos, EnumFacing> facings = new HashMap();
            EnumFacing[] var6 = EnumFacing.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               EnumFacing facing = var6[var8];
               if (facing != EnumFacing.DOWN && facing != EnumFacing.UP) {
                  BlockPos position = pos.func_177972_a(facing);
                  if (mc.field_71439_g.func_174818_b(position) <= MathUtil.square((Float)this.placeRange.getValue()) && mc.field_71441_e.func_180495_p(position).func_185904_a().func_76222_j() && !mc.field_71441_e.func_180495_p(position.func_177977_b()).func_185904_a().func_76222_j()) {
                     positions.add(position);
                     facings.put(position, facing.func_176734_d());
                  }
               }
            }

            if (positions.isEmpty()) {
               if (firstCheck) {
                  this.placeBed(pos.func_177984_a(), false);
               }

            } else {
               positions.sort(Comparator.comparingDouble((pos2) -> {
                  return mc.field_71439_g.func_174818_b(pos2);
               }));
               BlockPos finalPos = (BlockPos)positions.get(0);
               EnumFacing finalFacing = (EnumFacing)facings.get(finalPos);
               float[] rotation = RotationUtil.simpleFacing(finalFacing);
               if (!this.sendRotationPacket && (Boolean)this.extraPacket.getValue()) {
                  RotationUtil.faceYawAndPitch(rotation[0], rotation[1]);
                  this.sendRotationPacket = true;
               }

               this.yaw.set((double)rotation[0]);
               this.pitch.set((double)rotation[1]);
               this.shouldRotate.set(true);
               Vec3d hitVec = (new Vec3d(finalPos.func_177977_b())).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(finalFacing.func_176734_d().func_176730_m())).func_186678_a(0.5D));
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
               InventoryUtil.switchToHotbarSlot(this.bedSlot, false);
               BlockUtil.rightClickBlock(finalPos.func_177977_b(), hitVec, this.bedSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, EnumFacing.UP, (Boolean)this.packet.getValue());
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
               this.placeTimer.reset();
            }
         }
      }
   }

   public String getDisplayInfo() {
      return this.target != null ? this.target.func_70005_c_() : null;
   }

   public void onToggle() {
      this.lastHotbarSlot = -1;
      this.bedSlot = -1;
      this.sendRotationPacket = false;
      this.target = null;
      this.yaw.set(-1.0D);
      this.pitch.set(-1.0D);
      this.shouldRotate.set(false);
   }

   private int findBedSlot() {
      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() == Items.field_151104_aV) {
            return i;
         }
      }

      return -1;
   }

   public static enum Logic {
      BREAKPLACE,
      PLACEBREAK;
   }

   public static class BedData {
      private final BlockPos pos;
      private final IBlockState state;
      private final boolean isHeadPiece;
      private final TileEntityBed entity;

      public BedData(BlockPos pos, IBlockState state, TileEntityBed bed, boolean isHeadPiece) {
         this.pos = pos;
         this.state = state;
         this.entity = bed;
         this.isHeadPiece = isHeadPiece;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public IBlockState getState() {
         return this.state;
      }

      public boolean isHeadPiece() {
         return this.isHeadPiece;
      }

      public TileEntityBed getEntity() {
         return this.entity;
      }
   }
}
