package me.earth.phobos.features.command.commands;

import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;

public class FriendCommand extends Command {
   public FriendCommand() {
      super("friend", new String[]{"<add/del/name/clear>", "<name>"});
   }

   public void execute(String[] commands) {
      if (commands.length != 1) {
         String var4;
         byte var5;
         if (commands.length == 2) {
            var4 = commands[0];
            var5 = -1;
            switch(var4.hashCode()) {
            case 108404047:
               if (var4.equals("reset")) {
                  var5 = 0;
               }
            default:
               switch(var5) {
               case 0:
                  Phobos.friendManager.onLoad();
                  sendMessage("Friends got reset.");
                  break;
               default:
                  sendMessage(commands[0] + (Phobos.friendManager.isFriend(commands[0]) ? " is friended." : " isnt friended."));
               }

            }
         } else {
            if (commands.length >= 2) {
               var4 = commands[0];
               var5 = -1;
               switch(var4.hashCode()) {
               case 96417:
                  if (var4.equals("add")) {
                     var5 = 0;
                  }
                  break;
               case 99339:
                  if (var4.equals("del")) {
                     var5 = 1;
                  }
               }

               switch(var5) {
               case 0:
                  Phobos.friendManager.addFriend(commands[1]);
                  sendMessage("§b" + commands[1] + " has been friended");
                  break;
               case 1:
                  Phobos.friendManager.removeFriend(commands[1]);
                  sendMessage("§c" + commands[1] + " has been unfriended");
                  break;
               default:
                  sendMessage("§cBad Command, try: friend <add/del/name> <name>.");
               }
            }

         }
      } else {
         if (Phobos.friendManager.getFriends().isEmpty()) {
            sendMessage("You currently dont have any friends added.");
         } else {
            sendMessage("Friends: ");
            Iterator var2 = Phobos.friendManager.getFriends().entrySet().iterator();

            while(var2.hasNext()) {
               Entry<String, UUID> entry = (Entry)var2.next();
               sendMessage((String)entry.getKey());
            }
         }

      }
   }
}
