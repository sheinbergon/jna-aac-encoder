package org.sheinbergon.aac.jna.util;

import com.sun.jna.Structure;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JNASupport {

  /**
   * Deduce the JNA {@link Structure} subclass field order from the class itself
   * via reflection.
   *
   * @param structure The structure for which the field order is to be deduded
   * @return a list of strings, ordered according to their appearance in the class
   */
  public static List<String> structureFieldOrder(final Class<? extends Structure> structure) {
    return Stream.of(structure.getDeclaredFields())
        .filter(field -> !Modifier.isStatic(field.getModifiers()))
        .map(Field::getName)
        .collect(Collectors.toList());
  }

  /**
   * Clear the memory of the given {@link Structure} instances.
   *
   * @param structures the structures to have their memory cleared
   * @see Structure#clear()
   */
  public static void clearStructureMemory(final Structure... structures) {
    Stream.of(structures).forEach(Structure::clear);
  }

  private JNASupport() {
  }
}
