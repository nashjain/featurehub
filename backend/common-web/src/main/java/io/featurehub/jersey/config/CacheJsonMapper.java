package io.featurehub.jersey.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 */
public class CacheJsonMapper {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CacheJsonMapper.class);
  public static ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new KotlinModule());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
  }

  static public byte[] writeAsZipBytes(Object o) throws IOException {
    byte[] data = (o instanceof String) ? ((String)o).getBytes(StandardCharsets.UTF_8) : mapper.writeValueAsBytes(o);
    final ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
    GZIPOutputStream gzip = new GZIPOutputStream(baos);
    gzip.write(data, 0, data.length);
    gzip.flush();
    baos.flush();
    gzip.close();
    baos.close();
    return baos.toByteArray();
  }

  static public <T> T readFromZipBytes(byte[] data, Class<T> clazz) throws IOException {
    try (ByteArrayInputStream bais = new ByteArrayInputStream(data); GZIPInputStream is = new GZIPInputStream(bais)) {
      if (clazz.equals(String.class)) {
        return (T)new String(is.readAllBytes());
      }
      return mapper.readValue(is, clazz);
    } catch (Exception ignored) {
      // not a valid gzip stream
    }

    // still a string
    return mapper.readValue(data, clazz);
  }
}
