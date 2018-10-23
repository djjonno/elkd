package org.elkd.core.config;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ConfigTest {
  private static final String KEY_1 = "key1";
  private static final String VALUE_1 = "value1";

  private static final String INTEGER_KEY = "integerKey";
  private static final Integer INTEGER_VALUE = 123;

  private static final String DOUBLE_KEY = "doubleKey";
  private static final Double DOUBLE_VALUE = 123d;

  private static final String BOOLEAN_KEY = "booleanKey";
  private static final Boolean BOOLEAN_VALUE = true;

  private static final Map<String, String> CONFIG = ImmutableMap.of(
      KEY_1, VALUE_1,
      INTEGER_KEY, String.valueOf(INTEGER_VALUE),
      DOUBLE_KEY, String.valueOf(DOUBLE_VALUE),
      BOOLEAN_KEY, String.valueOf(BOOLEAN_VALUE)
  );

  private Config mConfig;

  @Before
  public void setup() throws Exception {
    mConfig = new Config(CONFIG);
  }

  @Test
  public void should_return_defaults() {
    // Given / When
    final Map<String, String> keyDefaults = Config.getKeyDefaults();

    // Then
    assertTrue(keyDefaults.containsKey(Config.KEY_DATA_DIR));
    assertFalse(keyDefaults.get(Config.KEY_DATA_DIR).isEmpty());
  }

  @Test
  public void should_return_value_of_key() {
    // Given / When - constructor

    // Then
    assertEquals(VALUE_1, mConfig.get(KEY_1));
  }

  @Test
  public void should_return_integer() {
    // Given / When
    final Integer integer = mConfig.getInteger(INTEGER_KEY);

    // Then
    assertEquals(INTEGER_VALUE, integer);
  }

  @Test
  public void should_return_double() {
    // Given / When
    final Double dub = mConfig.getDouble(DOUBLE_KEY);

    // Then
    assertEquals(DOUBLE_VALUE, dub);
  }

  @Test
  public void should_return_boolean() {
    // Given / When
    final Boolean bool = mConfig.getBoolean(BOOLEAN_KEY);

    // Then
    assertEquals(BOOLEAN_VALUE, bool);
  }
}
