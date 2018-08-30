package com.maxzxwd.permgiver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermGiver extends JavaPlugin {
  private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
  private static final String CONFIG_FILE_NAME = "stored.json";

  private AvailableWatcher availableWatcher;

  @Override
  public void onEnable() {
    super.onEnable();

    try {
      readWatcherFromDisk();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDisable() {
    super.onDisable();

    try {
      writeWatcherToDisk();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void readWatcherFromDisk() throws IOException {
    File configFile = new File(getDataFolder(), CONFIG_FILE_NAME);
    if (configFile.exists()) {
      availableWatcher = GSON
          .fromJson(new String(Files.readAllBytes(configFile.toPath()), StandardCharsets.UTF_8),
              AvailableWatcher.class);
    } else {
      availableWatcher = new AvailableWatcher();
      writeWatcherToDisk();
    }
  }

  public void writeWatcherToDisk() throws IOException {
    if (!getDataFolder().mkdirs() && !getDataFolder().exists()) {
      throw new IOException("Failed to create directory " + getDataFolder());
    }
    Files.write(new File(getDataFolder(), CONFIG_FILE_NAME).toPath(),
        GSON.toJson(availableWatcher).getBytes(StandardCharsets.UTF_8),
        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!label.equalsIgnoreCase("givex")) {
      return true;
    }

    if (args.length < 2) {
      sender.sendMessage(Messages.INVALID_USAGE);
      return true;
    }

    if (!(sender instanceof Player)) {
      sender.sendMessage(Messages.ONLY_PLAYER);
      return true;
    }

    PermParam permParam = getGiverPermParam(args[0], sender);

    if (permParam == null) {
      sender.sendMessage(Messages.DONT_PERMISSION);
      return true;
    }

    Player player = (Player) sender;
    Player target = getServer().getPlayer(args[1]);

    if (target == null) {
      sender.sendMessage(Messages.PLAYER_NOT_FOUND);
      return true;
    }

    PermissionUser user = PermissionsEx.getUser(target);
    if (user.inGroup(args[0])) {
      sender.sendMessage(Messages.ALREADY);
      return true;
    }

    permParam = onlyAvailable(permParam, player.getUniqueId());

    if (permParam.count <= 0) {
      sender.sendMessage(Messages.CANT_GIVE);
      return true;
    }

    getServer().dispatchCommand(getServer().getConsoleSender(),
        "pex user " + target.getName() +
            " group add " + args[0] + " * " + permParam.minutes);
    sender.sendMessage(Messages.SUCCESS);
    target.sendMessage(String.format(Messages.SUCCESS_TARGET, player.getDisplayName(), args[0],
        permParam.minutes));
    return true;
  }

  // givex.vip.count.minutes
  public PermParam getGiverPermParam(String group, Permissible target) {
    for (PermissionAttachmentInfo perm : target.getEffectivePermissions()) {
      if (perm.getPermission().startsWith("givex")) {
        String[] parts = perm.getPermission().split("\\.");

        if (parts.length == 4 && parts[1].equals(group)) {
          return new PermParam(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
      }
    }

    return null;
  }

  public PermParam onlyAvailable(PermParam permParam, UUID player) {
    if (!availableWatcher.firstUses.containsKey(player)) {
      availableWatcher.firstUses.put(player, System.currentTimeMillis());
    }
    long firstUse = availableWatcher.firstUses.get(player);

    if (System.currentTimeMillis() - firstUse >= TimeUnit.DAYS.toMillis(1) ||
        !availableWatcher.uses.containsKey(player)) {
      availableWatcher.uses.put(player, 0);
      availableWatcher.firstUses.put(player, System.currentTimeMillis());
    }

    int count = availableWatcher.uses.get(player);
    availableWatcher.uses.put(player, count + 1);

    return new PermParam(permParam.count - count, permParam.minutes);
  }
}
