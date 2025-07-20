package com.jfposton.ytdlp;

public abstract class DownloadProgressCallback {
  public void onProgressUpdate(float progress, long etaInSeconds) {}

  public void onOutBufferUpdate(char nextChar) {}

  public void onErrBufferUpdate(char nextChar) {}
}
