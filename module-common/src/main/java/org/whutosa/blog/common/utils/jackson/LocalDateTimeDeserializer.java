package org.whutosa.blog.common.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.whutosa.blog.common.utils.misc.DateUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author bobo
 * @date 2021/3/31
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Instant.ofEpochMilli(jsonParser.getLongValue()).atZone(DateUtils.DEFAULT_ZONE).toLocalDateTime();
    }
}
