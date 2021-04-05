package org.whutosa.blog.common.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.whutosa.blog.common.utils.misc.DateUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author bobo
 * @date 2021/3/31
 */

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.toInstant(DateUtils.DEFAULT_ZONE).toEpochMilli());
    }
}

