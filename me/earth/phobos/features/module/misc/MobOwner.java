package me.earth.phobos.features.modules.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.util.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;

public class MobOwner extends Module {
   private final Map<Entity, String> owners = new HashMap();
   private final Map<Entity, UUID> toLookUp = new ConcurrentHashMap();
   private final List<Entity> lookedUp = new ArrayList();

   public MobOwner() {
      super("MobOwner", "Shows you who owns mobs.", Module.Category.MISC, false, false, false);
   }

   public void onUpdate() {
      if (!fullNullCheck()) {
         Iterator var1;
         if (PlayerUtil.timer.passedS(5.0D)) {
            var1 = this.toLookUp.entrySet().iterator();

            while(var1.hasNext()) {
               Entry<Entity, UUID> entry = (Entry)var1.next();
               Entity entity = (Entity)entry.getKey();
               UUID uuid = (UUID)entry.getValue();
               if (uuid != null) {
                  EntityPlayer owner = mc.field_71441_e.func_152378_a(uuid);
                  if (owner == null) {
                     try {
                        String name = PlayerUtil.getNameFromUUID(uuid);
                        if (name != null) {
                           this.owners.put(entity, name);
                           this.lookedUp.add(entity);
                        }
                     } catch (Exception var7) {
                        this.lookedUp.add(entity);
                        this.toLookUp.remove(entry);
                     }

                     PlayerUtil.timer.reset();
                     break;
                  }

                  this.owners.put(entity, owner.func_70005_c_());
                  this.lookedUp.add(entity);
               } else {
                  this.lookedUp.add(entity);
                  this.toLookUp.remove(entry);
               }
            }
         }

         var1 = mc.field_71441_e.func_72910_y().iterator();

         while(var1.hasNext()) {
            Entity entity = (Entity)var1.next();
            if (!entity.func_174833_aM()) {
               if (entity instanceof EntityTameable) {
                  EntityTameable tameableEntity = (EntityTameable)entity;
                  if (tameableEntity.func_70909_n() && tameableEntity.func_184753_b() != null) {
                     if (this.owners.get(tameableEntity) != null) {
                        tameableEntity.func_174805_g(true);
                        tameableEntity.func_96094_a((String)this.owners.get(tameableEntity));
                     } else if (!this.lookedUp.contains(entity)) {
                        this.toLookUp.put(tameableEntity, tameableEntity.func_184753_b());
                     }
                  }
               } else if (entity instanceof AbstractHorse) {
                  AbstractHorse tameableEntity = (AbstractHorse)entity;
                  if (tameableEntity.func_110248_bS() && tameableEntity.func_184780_dh() != null) {
                     if (this.owners.get(tameableEntity) != null) {
                        tameableEntity.func_174805_g(true);
                        tameableEntity.func_96094_a((String)this.owners.get(tameableEntity));
                     } else if (!this.lookedUp.contains(entity)) {
                        this.toLookUp.put(tameableEntity, tameableEntity.func_184780_dh());
                     }
                  }
               }
            }
         }

      }
   }

   public void onDisable() {
      Iterator var1 = mc.field_71441_e.field_72996_f.iterator();

      while(true) {
         Entity entity;
         do {
            if (!var1.hasNext()) {
               return;
            }

            entity = (Entity)var1.next();
         } while(!(entity instanceof EntityTameable) && !(entity instanceof AbstractHorse));

         try {
            entity.func_174805_g(false);
         } catch (Exception var4) {
         }
      }
   }
}
