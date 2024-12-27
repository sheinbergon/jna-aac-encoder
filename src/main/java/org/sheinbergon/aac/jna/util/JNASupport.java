package org.sheinbergon.aac.jna.util;

import com.sun.jna.Structure;

import java.util.stream.Stream;

public final class JNASupport {

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
