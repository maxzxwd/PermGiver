package com.maxzxwd.permgiver;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AvailableWatcher {
  public Map<UUID, Long> firstUses = new HashMap<>(1000);
  public Map<UUID, Integer> uses = new HashMap<>(1000);
}
