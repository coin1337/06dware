package me.earth.phobos.features.setting;

import com.google.common.base.Converter;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.lwjgl.input.Keyboard;

public class Bind {
   private int key;

   public Bind(int key) {
      this.key = key;
   }

   public int getKey() {
      return this.key;
   }

   public boolean isEmpty() {
      return this.key < 0;
   }

   public void setKey(int key) {
      this.key = key;
   }

   public String toString() {
      return this.isEmpty() ? "None" : (this.key < 0 ? "None" : this.capitalise(Keyboard.getKeyName(this.key)));
   }

   public boolean isDown() {
      return !this.isEmpty() && Keyboard.isKeyDown(this.getKey());
   }

   private String capitalise(String str) {
      return str.isEmpty() ? "" : Character.toUpperCase(str.charAt(0)) + (str.length() != 1 ? str.substring(1).toLowerCase() : "");
   }

   public static Bind none() {
      return new Bind(-1);
   }

   public static class BindConverter extends Converter<Bind, JsonElement> {
      public JsonElement doForward(Bind bind) {
         return new JsonPrimitive(bind.toString());
      }

      public Bind doBackward(JsonElement jsonElement) {
         String s = jsonElement.getAsString();
         if (s.equalsIgnoreCase("None")) {
            return Bind.none();
         } else {
            int key = -1;

            try {
               key = Keyboard.getKeyIndex(s.toUpperCase());
            } catch (Exception var5) {
            }

            return key == 0 ? Bind.none() : new Bind(key);
         }
      }
   }
}
