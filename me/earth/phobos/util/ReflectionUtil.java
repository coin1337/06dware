package me.earth.phobos.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Objects;

public class ReflectionUtil {
   public static <F, T extends F> void copyOf(F from, T to, boolean ignoreFinal) throws NoSuchFieldException, IllegalAccessException {
      Objects.requireNonNull(from);
      Objects.requireNonNull(to);
      Class<?> clazz = from.getClass();
      Field[] var4 = clazz.getDeclaredFields();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Field field = var4[var6];
         makePublic(field);
         if (!isStatic(field) && (!ignoreFinal || !isFinal(field))) {
            makeMutable(field);
            field.set(to, field.get(from));
         }
      }

   }

   public static <F, T extends F> void copyOf(F from, T to) throws NoSuchFieldException, IllegalAccessException {
      copyOf(from, to, false);
   }

   public static boolean isStatic(Member instance) {
      return (instance.getModifiers() & 8) != 0;
   }

   public static boolean isFinal(Member instance) {
      return (instance.getModifiers() & 16) != 0;
   }

   public static void makeAccessible(AccessibleObject instance, boolean accessible) {
      Objects.requireNonNull(instance);
      instance.setAccessible(accessible);
   }

   public static void makePublic(AccessibleObject instance) {
      makeAccessible(instance, true);
   }

   public static void makePrivate(AccessibleObject instance) {
      makeAccessible(instance, false);
   }

   public static void makeMutable(Member instance) throws NoSuchFieldException, IllegalAccessException {
      Objects.requireNonNull(instance);
      Field modifiers = Field.class.getDeclaredField("modifiers");
      makePublic(modifiers);
      modifiers.setInt(instance, instance.getModifiers() & -17);
   }

   public static void makeImmutable(Member instance) throws NoSuchFieldException, IllegalAccessException {
      Objects.requireNonNull(instance);
      Field modifiers = Field.class.getDeclaredField("modifiers");
      makePublic(modifiers);
      modifiers.setInt(instance, instance.getModifiers() & 16);
   }
}
