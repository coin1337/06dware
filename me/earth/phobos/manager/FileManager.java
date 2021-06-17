package me.earth.phobos.manager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.Module;

public class FileManager extends Feature {
   private final Path base = this.getMkDirectory(this.getRoot(), "phobos");
   private final Path config;

   private String[] expandPath(String fullPath) {
      return fullPath.split(":?\\\\\\\\|\\/");
   }

   private Stream<String> expandPaths(String... paths) {
      return Arrays.stream(paths).map(this::expandPath).flatMap(Arrays::stream);
   }

   private Path lookupPath(Path root, String... paths) {
      return Paths.get(root.toString(), paths);
   }

   private Path getRoot() {
      return Paths.get("");
   }

   private void createDirectory(Path dir) {
      try {
         if (!Files.isDirectory(dir, new LinkOption[0])) {
            if (Files.exists(dir, new LinkOption[0])) {
               Files.delete(dir);
            }

            Files.createDirectories(dir);
         }
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   private Path getMkDirectory(Path parent, String... paths) {
      if (paths.length < 1) {
         return parent;
      } else {
         Path dir = this.lookupPath(parent, paths);
         this.createDirectory(dir);
         return dir;
      }
   }

   public FileManager() {
      this.config = this.getMkDirectory(this.base, "config");
      this.getMkDirectory(this.base, "util");
      Iterator var1 = Phobos.moduleManager.getCategories().iterator();

      while(var1.hasNext()) {
         Module.Category category = (Module.Category)var1.next();
         this.getMkDirectory(this.config, category.getName());
      }

   }

   public Path getBasePath() {
      return this.base;
   }

   public Path getBaseResolve(String... paths) {
      String[] names = (String[])this.expandPaths(paths).toArray((x$0) -> {
         return new String[x$0];
      });
      if (names.length < 1) {
         throw new IllegalArgumentException("missing path");
      } else {
         return this.lookupPath(this.getBasePath(), names);
      }
   }

   public Path getMkBaseResolve(String... paths) {
      Path path = this.getBaseResolve(paths);
      this.createDirectory(path.getParent());
      return path;
   }

   public Path getConfig() {
      return this.getBasePath().resolve("config");
   }

   public Path getCache() {
      return this.getBasePath().resolve("cache");
   }

   public Path getMkBaseDirectory(String... names) {
      return this.getMkDirectory(this.getBasePath(), (String)this.expandPaths(names).collect(Collectors.joining(File.separator)));
   }

   public Path getMkConfigDirectory(String... names) {
      return this.getMkDirectory(this.getConfig(), (String)this.expandPaths(names).collect(Collectors.joining(File.separator)));
   }

   public static boolean appendTextFile(String data, String file) {
      try {
         Path path = Paths.get(file);
         Files.write(path, Collections.singletonList(data), StandardCharsets.UTF_8, Files.exists(path, new LinkOption[0]) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
         return true;
      } catch (IOException var3) {
         System.out.println("WARNING: Unable to write file: " + file);
         return false;
      }
   }

   public static List<String> readTextFileAllLines(String file) {
      try {
         Path path = Paths.get(file);
         return Files.readAllLines(path, StandardCharsets.UTF_8);
      } catch (IOException var2) {
         System.out.println("WARNING: Unable to read file, creating new file: " + file);
         appendTextFile("", file);
         return Collections.emptyList();
      }
   }
}
