package org.sheinbergon.aac.jna.util;

import com.sun.jna.Structure;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JNAUtil {
    public final static List<String> structureFieldOrder(Class<? extends Structure> structure) {
        return Stream.of(structure.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(Field::getName)
                .collect(Collectors.toList());
    }
}
