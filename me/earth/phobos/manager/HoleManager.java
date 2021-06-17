package me.earth.phobos.manager;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.client.Managers;
import me.earth.phobos.features.modules.combat.HoleFiller;
import me.earth.phobos.features.modules.movement.HoleTP;
import me.earth.phobos.features.modules.render.HoleESP;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class HoleManager extends Feature implements Runnable {
   private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true));
   private List<BlockPos> holes = new ArrayList();
   private final List<BlockPos> midSafety = new ArrayList();
   private final Timer syncTimer = new Timer();
   private ScheduledExecutorService executorService;
   private int lastUpdates = 0;
   private Thread thread;
   private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
   private final Timer holeTimer = new Timer();

   public void update() {
      if (Managers.getInstance().holeThread.getValue() == Managers.ThreadMode.WHILE) {
         if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || this.syncTimer.passedMs((long)(Integer)Managers.getInstance().holeSync.getValue())) {
            if (this.thread == null) {
               this.thread = new Thread(this);
            } else if (this.syncTimer.passedMs((long)(Integer)Managers.getInstance().holeSync.getValue()) && !this.shouldInterrupt.get()) {
               this.shouldInterrupt.set(true);
               this.syncTimer.reset();
               return;
            }

            if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
               this.thread = new Thread(this);
            }

            if (this.thread != null && this.thread.getState() == State.NEW) {
               try {
                  this.thread.start();
               } catch (Exception var2) {
                  var2.printStackTrace();
               }

               this.syncTimer.reset();
            }
         }
      } else if (Managers.getInstance().holeThread.getValue() == Managers.ThreadMode.WHILE) {
         if (this.executorService == null || this.executorService.isTerminated() || this.executorService.isShutdown() || this.syncTimer.passedMs(10000L) || this.lastUpdates != (Integer)Managers.getInstance().holeUpdates.getValue()) {
            this.lastUpdates = (Integer)Managers.getInstance().holeUpdates.getValue();
            if (this.executorService != null) {
               this.executorService.shutdown();
            }

            this.executorService = this.getExecutor();
         }
      } else if (this.holeTimer.passedMs((long)(Integer)Managers.getInstance().holeUpdates.getValue()) && !fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn() || HoleTP.getInstance().isOn())) {
         this.holes = this.calcHoles();
         this.holeTimer.reset();
      }

   }

   public void settingChanged() {
      if (this.executorService != null) {
         this.executorService.shutdown();
      }

      if (this.thread != null) {
         this.shouldInterrupt.set(true);
      }

   }

   private ScheduledExecutorService getExecutor() {
      ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
      service.scheduleAtFixedRate(this, 0L, (long)(Integer)Managers.getInstance().holeUpdates.getValue(), TimeUnit.MILLISECONDS);
      return service;
   }

   public void run() {
      if (Managers.getInstance().holeThread.getValue() == Managers.ThreadMode.WHILE) {
         while(!this.shouldInterrupt.get()) {
            if (!fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn() || HoleTP.getInstance().isOn())) {
               this.holes = this.calcHoles();
            }

            try {
               Thread.sleep((long)(Integer)Managers.getInstance().holeUpdates.getValue());
            } catch (InterruptedException var2) {
               this.thread.interrupt();
               var2.printStackTrace();
            }
         }

         this.shouldInterrupt.set(false);
         this.syncTimer.reset();
         Thread.currentThread().interrupt();
      } else {
         if (Managers.getInstance().holeThread.getValue() == Managers.ThreadMode.POOL && !fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn())) {
            this.holes = this.calcHoles();
         }

      }
   }

   public List<BlockPos> getHoles() {
      return this.holes;
   }

   public List<BlockPos> getMidSafety() {
      return this.midSafety;
   }

   public List<BlockPos> getSortedHoles() {
      this.holes.sort(Comparator.comparingDouble((hole) -> {
         return mc.field_71439_g.func_174818_b(hole);
      }));
      return this.getHoles();
   }

   public List<BlockPos> calcHoles() {
      List<BlockPos> safeSpots = new ArrayList();
      this.midSafety.clear();
      List<BlockPos> positions = BlockUtil.getSphere(EntityUtil.getPlayerPos(mc.field_71439_g), (Float)Managers.getInstance().holeRange.getValue(), ((Float)Managers.getInstance().holeRange.getValue()).intValue(), false, true, 0);
      Iterator var3 = positions.iterator();

      while(true) {
         BlockPos pos;
         do {
            do {
               do {
                  if (!var3.hasNext()) {
                     return safeSpots;
                  }

                  pos = (BlockPos)var3.next();
               } while(!mc.field_71441_e.func_180495_p(pos).func_177230_c().equals(Blocks.field_150350_a));
            } while(!mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 1, 0)).func_177230_c().equals(Blocks.field_150350_a));
         } while(!mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 2, 0)).func_177230_c().equals(Blocks.field_150350_a));

         boolean isSafe = true;
         boolean midSafe = true;
         BlockPos[] var7 = surroundOffset;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            BlockPos offset = var7[var9];
            Block block = mc.field_71441_e.func_180495_p(pos.func_177971_a(offset)).func_177230_c();
            if (BlockUtil.isBlockUnSolid(block)) {
               midSafe = false;
            }

            if (block != Blocks.field_150357_h && block != Blocks.field_150343_Z && block != Blocks.field_150477_bB && block != Blocks.field_150467_bQ) {
               isSafe = false;
            }
         }

         if (isSafe) {
            safeSpots.add(pos);
         }

         if (midSafe) {
            this.midSafety.add(pos);
         }
      }
   }

   public boolean isSafe(BlockPos pos) {
      boolean isSafe = true;
      BlockPos[] var3 = surroundOffset;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         BlockPos offset = var3[var5];
         Block block = mc.field_71441_e.func_180495_p(pos.func_177971_a(offset)).func_177230_c();
         if (block != Blocks.field_150357_h) {
            isSafe = false;
            break;
         }
      }

      return isSafe;
   }
}
