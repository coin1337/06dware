package me.earth.phobos.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.modules.client.Managers;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionManager extends Feature {
   private final Map<EntityPlayer, PotionManager.PotionList> potions = new ConcurrentHashMap();

   public void onLogout() {
      this.potions.clear();
   }

   public void updatePlayer() {
      PotionManager.PotionList list = new PotionManager.PotionList();
      Iterator var2 = mc.field_71439_g.func_70651_bq().iterator();

      while(var2.hasNext()) {
         PotionEffect effect = (PotionEffect)var2.next();
         list.addEffect(effect);
      }

      this.potions.put(mc.field_71439_g, list);
   }

   public void update() {
      this.updatePlayer();
      if (HUD.getInstance().isOn() && (Boolean)HUD.getInstance().textRadar.getValue() && (Boolean)Managers.getInstance().potions.getValue()) {
         ArrayList<EntityPlayer> removeList = new ArrayList();
         Iterator var2 = this.potions.entrySet().iterator();

         while(var2.hasNext()) {
            Entry<EntityPlayer, PotionManager.PotionList> potionEntry = (Entry)var2.next();
            boolean notFound = true;
            Iterator var5 = mc.field_71441_e.field_73010_i.iterator();

            while(var5.hasNext()) {
               EntityPlayer player = (EntityPlayer)var5.next();
               if (this.potions.get(player) == null) {
                  PotionManager.PotionList list = new PotionManager.PotionList();
                  Iterator var8 = player.func_70651_bq().iterator();

                  while(var8.hasNext()) {
                     PotionEffect effect = (PotionEffect)var8.next();
                     list.addEffect(effect);
                  }

                  this.potions.put(player, list);
                  notFound = false;
               }

               if (((EntityPlayer)potionEntry.getKey()).equals(player)) {
                  notFound = false;
               }
            }

            if (notFound) {
               removeList.add(potionEntry.getKey());
            }
         }

         var2 = removeList.iterator();

         while(var2.hasNext()) {
            EntityPlayer player = (EntityPlayer)var2.next();
            this.potions.remove(player);
         }
      }

   }

   public List<PotionEffect> getOwnPotions() {
      return this.getPlayerPotions(mc.field_71439_g);
   }

   public List<PotionEffect> getPlayerPotions(EntityPlayer player) {
      PotionManager.PotionList list = (PotionManager.PotionList)this.potions.get(player);
      List<PotionEffect> potions = new ArrayList();
      if (list != null) {
         potions = list.getEffects();
      }

      return (List)potions;
   }

   public void onTotemPop(EntityPlayer player) {
      PotionManager.PotionList list = new PotionManager.PotionList();
      this.potions.put(player, list);
   }

   public PotionEffect[] getImportantPotions(EntityPlayer player) {
      PotionEffect[] array = new PotionEffect[3];
      Iterator var3 = this.getPlayerPotions(player).iterator();

      while(var3.hasNext()) {
         PotionEffect effect = (PotionEffect)var3.next();
         Potion potion = effect.func_188419_a();
         String var6 = I18n.func_135052_a(potion.func_76393_a(), new Object[0]).toLowerCase();
         byte var7 = -1;
         switch(var6.hashCode()) {
         case -736186929:
            if (var6.equals("weakness")) {
               var7 = 1;
            }
            break;
         case 109641799:
            if (var6.equals("speed")) {
               var7 = 2;
            }
            break;
         case 1791316033:
            if (var6.equals("strength")) {
               var7 = 0;
            }
         }

         switch(var7) {
         case 0:
            array[0] = effect;
            break;
         case 1:
            array[1] = effect;
            break;
         case 2:
            array[2] = effect;
         }
      }

      return array;
   }

   public String getPotionString(PotionEffect effect) {
      Potion potion = effect.func_188419_a();
      return I18n.func_135052_a(potion.func_76393_a(), new Object[0]) + " " + (!(Boolean)HUD.getInstance().potions1.getValue() && effect.func_76458_c() == 0 ? "" : effect.func_76458_c() + 1 + " ") + "§f" + Potion.func_188410_a(effect, 1.0F);
   }

   public String getColoredPotionString(PotionEffect effect) {
      Potion potion = effect.func_188419_a();
      String var3 = I18n.func_135052_a(potion.func_76393_a(), new Object[0]);
      byte var4 = -1;
      switch(var3.hashCode()) {
      case -1898882264:
         if (var3.equals("Poison")) {
            var4 = 12;
         }
         break;
      case -1703702509:
         if (var3.equals("Wither")) {
            var4 = 4;
         }
         break;
      case -1018368008:
         if (var3.equals("Slowness")) {
            var4 = 5;
         }
         break;
      case -671542801:
         if (var3.equals("Weakness")) {
            var4 = 6;
         }
         break;
      case -437701543:
         if (var3.equals("Resistance")) {
            var4 = 2;
         }
         break;
      case -135958429:
         if (var3.equals("Fire Resistance")) {
            var4 = 9;
         }
         break;
      case -91439823:
         if (var3.equals("Jump Boost")) {
            var4 = 0;
         }
         break;
      case 69497451:
         if (var3.equals("Haste")) {
            var4 = 8;
         }
         break;
      case 80089127:
         if (var3.equals("Speed")) {
            var4 = 1;
         }
         break;
      case 733749999:
         if (var3.equals("Absorption")) {
            var4 = 7;
         }
         break;
      case 919950640:
         if (var3.equals("Night Vision")) {
            var4 = 11;
         }
         break;
      case 1197090731:
         if (var3.equals("Regeneration")) {
            var4 = 10;
         }
         break;
      case 1855960161:
         if (var3.equals("Strength")) {
            var4 = 3;
         }
      }

      switch(var4) {
      case 0:
      case 1:
         return "§b" + this.getPotionString(effect);
      case 2:
      case 3:
         return "§c" + this.getPotionString(effect);
      case 4:
      case 5:
      case 6:
         return "§0" + this.getPotionString(effect);
      case 7:
         return "§9" + this.getPotionString(effect);
      case 8:
      case 9:
         return "§6" + this.getPotionString(effect);
      case 10:
         return "§d" + this.getPotionString(effect);
      case 11:
      case 12:
         return "§a" + this.getPotionString(effect);
      default:
         return "§f" + this.getPotionString(effect);
      }
   }

   public String getTextRadarPotionWithDuration(EntityPlayer player) {
      PotionEffect[] array = this.getImportantPotions(player);
      PotionEffect strength = array[0];
      PotionEffect weakness = array[1];
      PotionEffect speed = array[2];
      return "" + (strength != null ? "§c S" + (strength.func_76458_c() + 1) + " " + Potion.func_188410_a(strength, 1.0F) : "") + (weakness != null ? "§8 W " + Potion.func_188410_a(weakness, 1.0F) : "") + (speed != null ? "§b S" + (speed.func_76458_c() + 1) + " " + Potion.func_188410_a(weakness, 1.0F) : "");
   }

   public String getTextRadarPotion(EntityPlayer player) {
      PotionEffect[] array = this.getImportantPotions(player);
      PotionEffect strength = array[0];
      PotionEffect weakness = array[1];
      PotionEffect speed = array[2];
      return "" + (strength != null ? "§c S" + (strength.func_76458_c() + 1) + " " : "") + (weakness != null ? "§8 W " : "") + (speed != null ? "§b S" + (speed.func_76458_c() + 1) + " " : "");
   }

   public static class PotionList {
      private List<PotionEffect> effects = new ArrayList();

      public void addEffect(PotionEffect effect) {
         if (effect != null) {
            this.effects.add(effect);
         }

      }

      public List<PotionEffect> getEffects() {
         return this.effects;
      }
   }
}
