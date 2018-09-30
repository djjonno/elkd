package org.elkd.core.server;

import org.junit.Test;

import static org.junit.Assert.*;

public class BirdTest {
  @Test
  public void should_create() {
    final Bird bird = new Bird("rocky");

    assertEquals("rocky", bird.getName());
  }
}