package com.jfposton.ytdlp;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.jfposton.ytdlp.mapper.VideoFormat;

public class YtDlpResponseTest {

  @Test
  public void getFormatsCanHandleLargerVideos() throws YtDlpException {
    List<VideoFormat> videoFormats =
        YtDlp.getFormats("https://www.youtube.com/watch?v=jPTO3lcPpik");
    assertTrue(videoFormats.get(0).getUrl().contains("jPTO3lcPpik"));
  }
}
