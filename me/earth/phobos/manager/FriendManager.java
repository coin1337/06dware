package me.earth.phobos.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

public class FriendManager extends Feature {
   private final Map<String, UUID> friends = new HashMap();

   public FriendManager() {
      super("Friends");
   }

   public boolean isFriend(String name) {
      return this.friends.get(name) != null;
   }

   public boolean isFriend(EntityPlayer player) {
      return this.isFriend(player.func_70005_c_());
   }

   public void addFriend(String name) {
      FriendManager.Friend friend = this.getFriendByName(name);
      if (friend != null) {
         this.friends.put(friend.getUsername(), friend.getUuid());
      }

   }

   public void removeFriend(String name) {
      this.friends.remove(name);
   }

   public void onLoad() {
      this.friends.clear();
      this.clearSettings();
   }

   public void saveFriends() {
      this.clearSettings();
      Iterator var1 = this.friends.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<String, UUID> entry = (Entry)var1.next();
         this.register(new Setting(((UUID)entry.getValue()).toString(), entry.getKey()));
      }

   }

   public Map<String, UUID> getFriends() {
      return this.friends;
   }

   public FriendManager.Friend getFriendByName(String input) {
      UUID uuid = PlayerUtil.getUUIDFromName(input);
      return uuid != null ? new FriendManager.Friend(input, uuid) : null;
   }

   public void addFriend(FriendManager.Friend friend) {
      this.friends.put(friend.getUsername(), friend.getUuid());
   }

   public static class Friend {
      private final String username;
      private final UUID uuid;

      public Friend(String username, UUID uuid) {
         this.username = username;
         this.uuid = uuid;
      }

      public String getUsername() {
         return this.username;
      }

      public UUID getUuid() {
         return this.uuid;
      }

      public boolean equals(Object other) {
         return other instanceof FriendManager.Friend && ((FriendManager.Friend)other).getUsername().equals(this.username) && ((FriendManager.Friend)other).getUuid().equals(this.uuid);
      }

      public int hashCode() {
         return this.username.hashCode() + this.uuid.hashCode();
      }
   }
}
