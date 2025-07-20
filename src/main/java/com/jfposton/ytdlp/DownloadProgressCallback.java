package com.jfposton.ytdlp;

public abstract class DownloadProgressCallback {
  public void onProgressUpdate(float progress, long etaInSeconds) {}
  public void onBufferUpdate(char nextChar) {}
}
