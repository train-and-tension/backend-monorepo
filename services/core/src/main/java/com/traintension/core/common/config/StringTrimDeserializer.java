package com.traintension.core.common.config;

import org.springframework.boot.jackson.JacksonComponent;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

@JacksonComponent
public class StringTrimDeserializer extends StdDeserializer<String> {

    public StringTrimDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctx) {
        String value = p.getString();
        return value == null ? null : value.strip();
    }
}
