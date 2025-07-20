package com.jfposton.ytdlp.utils;

import java.io.IOException;
import java.io.InputStream;
import com.jfposton.ytdlp.DownloadProgressCallback;

public class StreamGobbler extends Thread {

  private InputStream stream;
  private StringBuilder buffer;
  private DownloadProgressCallback callback;

  public StreamGobbler(StringBuilder buffer, InputStream stream, DownloadProgressCallback callback) {
    this.stream = stream;
    this.buffer = buffer;
    this.callback = callback;
    start();
  }
  public StreamGobbler(StringBuilder buffer, InputStream stream) {
    this(buffer, stream, null);
  }

  @Override
  public void run() {
    try {
      int nextChar;
      while ((nextChar = this.stream.read()) != -1) {
        char c = (char)nextChar;
        buffer.append(c);
        if(callback != null) callback.onErrBufferUpdate(c);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
