package com.clistash.app;

import java.util.Arrays;

public class ParseData {
  public ParseData() {}

  public static String[] normalizeQuotes(String[] args) {
    if (args.length < 3) {
      return args;
    }

    if (!args[0].equals("-add") && !args[0].equals("-update")) {
      return args;
    }

    String name = args[1];

    String command = String.join(" ",
      Arrays.copyOfRange(args, 2, args.length));

    if (command.length() >= 2 &&
      command.startsWith("\"") &&
      command.endsWith("\"")) {

      command = command.substring(1, command.length() - 1);
    }

    return new String[]{args[0], name, command};
  }
}
