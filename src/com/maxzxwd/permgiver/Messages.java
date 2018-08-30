package com.maxzxwd.permgiver;

import org.bukkit.ChatColor;

public final class Messages {
  public static final String INVALID_USAGE = ChatColor.RED + "Вам необходимо указать выдаваемую привилегию и ник игрока. " +
      "Например: " + ChatColor.GREEN + "/giveperm vip lolkek";

  public static final String ONLY_PLAYER = ChatColor.RED + "Только игроки могут использовать эту команду!";

  public static final String PLAYER_NOT_FOUND = ChatColor.RED + "Игрок с таким ником не найден!";

  public static final String CANT_GIVE = ChatColor.RED + "Вы не можете выдать привилегию так как "
      + "Ваш лимит на сегодня исчерпан.";

  public static final String DONT_PERMISSION = ChatColor.RED + "Вы не можете выдавать привелегии!";

  public static final String SUCCESS = ChatColor.GREEN + "Вы успешно выдали привилегию!";

  public static final String ALREADY = ChatColor.RED + "Данный игрок уже имеет эту привилегию!";

  public static final String SUCCESS_TARGET = ChatColor.YELLOW + "%s" + ChatColor.GREEN +
      " выдал Вам " + ChatColor.YELLOW + "%s" + ChatColor.GREEN + " на " +
      ChatColor.YELLOW + "%s" + ChatColor.GREEN + " минут!";
  private Messages() {
  }
}
