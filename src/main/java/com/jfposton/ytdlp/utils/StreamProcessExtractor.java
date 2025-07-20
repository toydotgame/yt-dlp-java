package com.jfposton.ytdlp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfposton.ytdlp.DownloadProgressCallback;

public class StreamProcessExtractor extends Thread {
  private static final String GROUP_PERCENT = "percent";
  private static final String GROUP_MINUTES = "minutes";
  private static final String GROUP_SECONDS = "seconds";
  private InputStream stream;
  private StringBuilder buffer;
  private final DownloadProgressCallback callback;

  private Pattern percentWithEta =
      Pattern.compile(
          "\\[download\\]\\s+(?<"
              + GROUP_PERCENT
              + ">\\d+\\.\\d)% .* ETA (?<"
              + GROUP_MINUTES
              + ">\\d+):(?<"
              + GROUP_SECONDS
              + ">\\d+).*");
  private Pattern percentOnly =
      Pattern.compile("\\[download\\]\\s+(?<" + GROUP_PERCENT + ">\\d+\\.\\d)% .* ETA Unknown.*");

  public StreamProcessExtractor(
      StringBuilder buffer, InputStream stream, DownloadProgressCallback callback) {
    this.stream = stream;
    this.buffer = buffer;
    this.callback = callback;
    this.start();
  }

  @Override
  public void run() {
    try {
      StringBuilder currentLine = new StringBuilder();
      int nextChar;
      while ((nextChar = stream.read()) != -1) {
        char c = (char) nextChar;
        buffer.append(c);
        if(callback != null) callback.onOutBufferUpdate(c);
        if((nextChar == '\r' || nextChar == '\n') && callback != null) {
          processOutputLine(currentLine.toString());
          currentLine.setLength(0);
          continue;
        }
        currentLine.append(c);
      }
    } catch (IOException ignored) {
      ignored.printStackTrace();
    }
  }

  private void processOutputLine(String line) {
    Matcher matchPercentWithEta = percentWithEta.matcher(line);
    Matcher matchPercentOnly = percentOnly.matcher(line);
    float progress;
    long eta = -1;
    if(matchPercentWithEta.matches()) {
      progress = Float.parseFloat(matchPercentWithEta.group(GROUP_PERCENT));
      eta = convertToSeconds(matchPercentWithEta.group(GROUP_MINUTES), matchPercentWithEta.group(GROUP_SECONDS));
      callback.onProgressUpdate(progress, eta);
    } else if(matchPercentOnly.matches()) {
      progress = Float.parseFloat(matchPercentOnly.group(GROUP_PERCENT));
      callback.onProgressUpdate(progress, eta);
    }
  }

  private int convertToSeconds(String minutes, String seconds) {
    return Integer.parseInt(minutes) * 60 + Integer.parseInt(seconds);
  }
}
