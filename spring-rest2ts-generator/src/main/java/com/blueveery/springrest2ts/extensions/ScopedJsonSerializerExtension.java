package com.blueveery.springrest2ts.extensions;

import com.blueveery.springrest2ts.tsmodel.TSMethod;
import com.blueveery.springrest2ts.tsmodel.TSParameter;

import java.util.Collections;
import java.util.List;

public class ScopedJsonSerializerExtension implements ModelSerializerExtension {

    private final String serialisationTemple;
    private final String deserializationTemple;

    public ScopedJsonSerializerExtension() {
        this.serialisationTemple = "JsonScopedSerializer.stringify(%s, new JsonScope(false, []))";
        this.deserializationTemple = "JSON.parse(%s)";
    }

    public ScopedJsonSerializerExtension(String serialisationTemple, String deserializationTemple) {
        this.serialisationTemple = serialisationTemple;
        this.deserializationTemple = deserializationTemple;
    }

    @Override
    public List<TSParameter> getImplementationSpecificParameters(TSMethod method) {
        return Collections.emptyList();
    }

    @Override
    public String generateSerializationCode(String modelVariableName) {
        return String.format(this.serialisationTemple, modelVariableName);
    }

    @Override
    public String generateDeserializationCode(String modelVariableName) {
        return String.format(this.deserializationTemple, modelVariableName);
    }
}
