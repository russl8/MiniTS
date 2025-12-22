package com.russl8.mints.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import model.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * MixIn so we don't have to modify model.Value in the compiler module.
     * We expose a stable JSON shape:
     *   { "type": "...", "value": ... }
     * and we ignore typed getters that can throw ClassCastException.
     */
    public abstract static class ValueMixin {
        @JsonIgnore public abstract Integer getValueAsInt();
        @JsonIgnore public abstract Boolean getValueAsBool();
        @JsonIgnore public abstract Character getValueAsCharacter();
        @JsonIgnore public abstract Object getValueAsExpression();

        @JsonProperty("value")
        public abstract Object getValueAsObject();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer valueMixInCustomizer() {
        return builder -> builder.mixIn(Value.class, ValueMixin.class);
    }
}
