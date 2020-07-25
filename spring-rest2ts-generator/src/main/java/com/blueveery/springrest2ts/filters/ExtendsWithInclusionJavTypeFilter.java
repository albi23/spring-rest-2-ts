package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ExtendsWithInclusionJavTypeFilter {

    private final Class<?> baseType;
    protected Set<Class<?>> inclusionsClass = new HashSet<>();

    public boolean accept(Class javaType) {
        return this.baseType.isAssignableFrom(javaType) || inclusionsClass.contains(javaType);
    }

    private int isAcceptable(Class<?> javaType) {
        return (this.baseType.isAssignableFrom(javaType)) ? 0 : inclusionsClass.contains(javaType) ? 1 : -1;
    }

    public ExtendsWithInclusionJavTypeFilter(Class<?> baseType) {
        if (baseType.isAnnotation()) {
            throw new IllegalStateException("Annotation could not be a base Type");
        } else {
            this.baseType = baseType;
        }
    }

    protected void registerInclusionsClassesFilter(List<Class<?>> inclusion) {
        inclusion.stream().filter(Objects::nonNull).forEach(inclusionsClass::add);
    }

    public void explain(Class packageClass, Logger logger, String indentation) {
        switch (isAcceptable(packageClass)) {
            case 0:
                logger.info(indentation + String.format("TRUE => class %s extends type %s", packageClass.getSimpleName(), this.baseType.getSimpleName()));
                break;
            case 1:
                logger.info(indentation + String.format("TRUE => class %s is in inclusion set", packageClass.getSimpleName()));
                break;
            default:
                logger.warn(indentation + String.format("FALSE => class %s doesn't extends base type %s and is not in inclusion set", packageClass.getSimpleName(), this.baseType.getSimpleName()));
        }
    }
}
