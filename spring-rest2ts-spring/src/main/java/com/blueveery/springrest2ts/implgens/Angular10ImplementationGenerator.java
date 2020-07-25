package com.blueveery.springrest2ts.implgens;

import com.blueveery.springrest2ts.extensions.ScopedJsonSerializerExtension;
import com.blueveery.springrest2ts.tsmodel.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Angular10ImplementationGenerator extends Angular4ImplementationGenerator {

    private static final String FIELD_NAME_HTTP_SERVICE = "httpService";
    private static final String FIELD_NAME_ERROR_SERVICE = "errorHandlerService";

    private final Set<TSClass> externalImportClass = new HashSet<>();
    private final String[] implementationSpecificFieldNames;
    private final TSClass httpClass;
    private final TSClass errorHandlerClass;

    public Angular10ImplementationGenerator() {
        super(null);
        /* Override implementation set up */
        this.addExternalProjectImport("rxjs", "Subject");

        modelSerializerExtensionsMap.put(JSON_CONTENT_TYPE, new ScopedJsonSerializerExtension());
        implementationSpecificFieldNames = new String[]{FIELD_NAME_HTTP_SERVICE, FIELD_NAME_ERROR_SERVICE};

        TSModule angularHttpModule = new TSModule("@angular/common/http", null, true);
        httpClass = new TSClass("HttpClient", angularHttpModule, this);

        TSModule errorHandlerModule = new TSModule("./error-handler.service", null, true);
        errorHandlerClass = new TSClass("ErrorHandlerService", errorHandlerModule, this);
        this.externalImportClass.add(errorHandlerClass);

    }

    @Override
    public void addComplexTypeUsage(TSClass tsClass) {
        externalImportClass.forEach(tsClass::addScopedTypeUsage);
        super.addComplexTypeUsage(tsClass);
    }

    public Set<TSClass> getExternalImportClass() {
        return externalImportClass;
    }

    public void addExternalProjectImport(String moduleRelativePath, String... importClassName) {
        TSModule newModule = new TSModule(moduleRelativePath, null, true);
        Stream.of(importClassName).forEach(className -> {
            TSClass importClass = new TSClass(className, newModule, this);
            externalImportClass.add(importClass);
        });
    }

    @Override
    protected void writeReturnStatement(BufferedWriter writer, String httpMethod, TSMethod method, boolean isRequestOptionRequired, String tsPath, String requestOptions, boolean isJsonParsingRequired) throws IOException {
        writer.write("   const subject = new Subject<" + method.getType().getName() + ">();\n");
        writer.write("    this." + FIELD_NAME_HTTP_SERVICE + "." + httpMethod + getGenericType(method, isRequestOptionRequired) + "("
                + tsPath
                + requestOptions
                + ")" + getParseResponseFunction(isJsonParsingRequired) + "\n      .subscribe(res => subject.next(res)" + defineErrorHandling() +
                "    return subject.asObservable();");
    }

    private String defineErrorHandling() {
        return ", error => {\n" +
                "       if (!this." + FIELD_NAME_ERROR_SERVICE + ".handleErrors(error)) {\n" +
                "         subject.error(error);\n" +
                "       }\n" +
                "    });\n";
    }

    @Override
    protected String[] getImplementationSpecificFieldNames() {
        return implementationSpecificFieldNames;
    }

    @Override
    public List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        if (method.isConstructor()) {
            List<TSParameter> tsParameters = new ArrayList<>();
            TSParameter httpServiceParameter = new TSParameter(FIELD_NAME_HTTP_SERVICE, httpClass, method, this);
            TSParameter errorHandlerServiceParameter = new TSParameter(FIELD_NAME_ERROR_SERVICE, errorHandlerClass, method, this);
            tsParameters.add(httpServiceParameter);
            tsParameters.add(errorHandlerServiceParameter);
            return tsParameters;
        }
        return Collections.emptyList();
    }

    @Override
    public void addImplementationSpecificFields(TSComplexElement tsComplexType) {
        TSClass tsClass = (TSClass) tsComplexType;
        if (tsClass.getExtendsClass() == null) {
            tsClass.getTsFields().add(new TSField(FIELD_NAME_HTTP_SERVICE, tsComplexType, httpClass));
            tsClass.getTsFields().add(new TSField(FIELD_NAME_ERROR_SERVICE, tsComplexType, errorHandlerClass));
        }
    }
}
