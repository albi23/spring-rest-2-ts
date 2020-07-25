package com.blueveery.springrest2ts.filters;

import org.slf4j.Logger;

import java.util.List;

public class AndFilterOperator extends ComplexFilterOperator {

    public AndFilterOperator(List<JavaTypeFilter> javaTypeFilters) {
        super(javaTypeFilters);
    }

    @Override
    public boolean accept(Class javaType) {
        for (JavaTypeFilter typeFilter : getJavaTypeFilters()) {
            if (!typeFilter.accept(javaType)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void explain(Class packageClass, Logger logger, String indentation) {
        logger.info(indentation + "{ AND FILTER");
        getJavaTypeFilters().forEach(f -> f.explain(packageClass, logger, indentation+"\t"));
        logger.info(indentation + "}");
    }

}

