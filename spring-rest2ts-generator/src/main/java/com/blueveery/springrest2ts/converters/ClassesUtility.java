package com.blueveery.springrest2ts.converters;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassesUtility {

    public static List<Class<?>> getListOfClassesInPackage(final Class<?> sourceClass, final String packageName) {
        final String casesPath = packageName.replace(".", "/");
        File[] files = new File(Objects.requireNonNull(sourceClass.getClassLoader().getResource(casesPath)).getPath()).listFiles();
        return (files == null) ? Collections.emptyList() :
                Stream.of(files).map(File::getName).
                        filter(name -> name.endsWith(".class"))
                        .map(fileName -> {
                            try {
                                return Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }
}