package org.sheinbergon.aac.encoder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.val;
import org.sheinbergon.aac.encoder.util.AACEncoderException;
import org.sheinbergon.aac.encoder.util.AACEncodingProfile;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@NotThreadSafe
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleAACEncoder implements AutoCloseable {

  @Value
  @Accessors(fluent = true)
  @SuppressWarnings("VisibilityModifier")
  public static class Specification {
    int channels;
    int sampleRate;
    @Nonnull
    AACEncodingProfile profile;
  }

  private static SimpleAACEncoder create(
      final @Nonnull Specification specification,
      final @Nonnull OutputStream target) {
    val encoder = AACEncoder.builder()
        .afterBurner(true)
        .channels(specification.channels())
        .profile(specification.profile())
        .sampleRate(specification.sampleRate())
        .build();
    return new SimpleAACEncoder(encoder, target, true);
  }

  private final AACEncoder encoder;
  private final OutputStream target;
  private final boolean external;

  private boolean concluded = false;


  @Getter
  private int encoded = 0;

  @Getter
  private int total = 0;

  /**
   * @param buffer
   * @param length
   */
  @SneakyThrows
  public void encode(
      final byte[] buffer,
      final int length) {
    if (concluded) {
      throw new IllegalStateException("Cannot encode using a concluded encoder");
    } else {
      val input = WAVInput.wrap(buffer, length);
      val output = encoder.encode(input);
      write(output);
    }
  }

  /**
   *
   */
  @SneakyThrows
  public void conclude() {
    if (concluded) {
      throw new IllegalStateException("Cannot conclude using a concluded encoder");
    } else {
      concluded = true;
      val output = encoder.conclude();
      write(output);
    }
  }

  @Override
  public void close() {
    if (!concluded) {
      encoder.close();
      concluded = true;
    }
  }

  /**
   *
   */
  @SneakyThrows
  public InputStream drain() {
    if (!external && target instanceof ByteArrayOutputStream) {
      val stream = (ByteArrayOutputStream) target;
      val bytes = stream.toByteArray();
      stream.reset();
      encoded = 0;
      return new ByteArrayInputStream(bytes);
    } else {
      throw new AACEncoderException("Draining of external byte stream target is not allowed");
    }
  }

  @SneakyThrows
  private void write(final @Nonnull AACOutput output) {
    if (output.length() > 0) {
      target.write(output.data());
      total += encoded += output.length();
    }
  }
}