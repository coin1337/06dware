package me.earth.phobos.features.modules.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.DeathEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.FileManager;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoGG extends Module {
   private final Setting<Boolean> greentext = this.register(new Setting("Greentext", false));
   private final Setting<Boolean> loadFiles = this.register(new Setting("LoadFiles", false));
   private final Setting<Integer> targetResetTimer = this.register(new Setting("Reset", 30, 0, 90));
   private final Setting<Integer> delay = this.register(new Setting("Delay", 10, 0, 30));
   private final Setting<Boolean> test = this.register(new Setting("Test", false));
   public Map<EntityPlayer, Integer> targets = new ConcurrentHashMap();
   public List<String> messages = new ArrayList();
   public EntityPlayer cauraTarget;
   private static final String path = "phobos/autogg.txt";
   private Timer timer = new Timer();
   private Timer cooldownTimer = new Timer();
   private boolean cooldown;

   public AutoGG() {
      super("AutoGG", "Automatically GGs", Module.Category.MISC, true, false, false);
      File file = new File("phobos/autogg.txt");
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

   }

   public void onEnable() {
      this.loadMessages();
      this.timer.reset();
      this.cooldownTimer.reset();
   }

   public void onTick() {
      if ((Boolean)this.loadFiles.getValue()) {
         this.loadMessages();
         Command.sendMessage("<AutoGG> Loaded messages.");
         this.loadFiles.setValue(false);
      }

      if (AutoCrystal.target != null && this.cauraTarget != AutoCrystal.target) {
         this.cauraTarget = AutoCrystal.target;
      }

      if ((Boolean)this.test.getValue()) {
         this.announceDeath(mc.field_71439_g);
         this.test.setValue(false);
      }

      if (!this.cooldown) {
         this.cooldownTimer.reset();
      }

      if (this.cooldownTimer.passedS((double)(Integer)this.delay.getValue()) && this.cooldown) {
         this.cooldown = false;
         this.cooldownTimer.reset();
      }

      if (AutoCrystal.target != null) {
         this.targets.put(AutoCrystal.target, (int)(this.timer.getPassedTimeMs() / 1000L));
      }

      this.targets.replaceAll((p, v) -> {
         return (int)(this.timer.getPassedTimeMs() / 1000L);
      });
      Iterator var1 = this.targets.keySet().iterator();

      while(var1.hasNext()) {
         EntityPlayer player = (EntityPlayer)var1.next();
         if ((Integer)this.targets.get(player) > (Integer)this.targetResetTimer.getValue()) {
            this.targets.remove(player);
         }
      }

   }

   @SubscribeEvent
   public void onEntityDeath(DeathEvent event) {
      if (this.targets.containsKey(event.player) && !this.cooldown) {
         this.announceDeath(event.player);
         this.cooldown = true;
         this.targets.remove(event.player);
      }

   }

   @SubscribeEvent
   public void onAttackEntity(AttackEntityEvent event) {
      if (event.getTarget() instanceof EntityPlayer && !Phobos.friendManager.isFriend(event.getEntityPlayer())) {
         this.targets.put((EntityPlayer)event.getTarget(), 0);
      }

   }

   @SubscribeEvent
   public void onSendAttackPacket(PacketEvent.Send event) {
      if (event.getPacket() instanceof CPacketUseEntity) {
         CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
         if (packet.func_149565_c() == Action.ATTACK && packet.func_149564_a(mc.field_71441_e) instanceof EntityPlayer && !Phobos.friendManager.isFriend((EntityPlayer)packet.func_149564_a(mc.field_71441_e))) {
            this.targets.put((EntityPlayer)packet.func_149564_a(mc.field_71441_e), 0);
         }
      }

   }

   public void loadMessages() {
      this.messages = FileManager.readTextFileAllLines("phobos/autogg.txt");
   }

   public String getRandomMessage() {
      this.loadMessages();
      Random rand = new Random();
      if (this.messages.size() == 0) {
         return "<player> is a noob hahaha fobus on tope";
      } else {
         return this.messages.size() == 1 ? (String)this.messages.get(0) : (String)this.messages.get(MathUtil.clamp(rand.nextInt(this.messages.size()), 0, this.messages.size() - 1));
      }
   }

   public void announceDeath(EntityPlayer target) {
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketChatMessage(((Boolean)this.greentext.getValue() ? ">" : "") + this.getRandomMessage().replaceAll("<player>", target.getDisplayNameString())));
   }
}
