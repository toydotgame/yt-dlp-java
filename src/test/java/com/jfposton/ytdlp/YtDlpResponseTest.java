package com.jfposton.ytdlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.jfposton.ytdlp.mapper.VideoFormat;
import com.jfposton.ytdlp.mapper.VideoInfo;

public class YtDlpResponseTest {

  @Test
  public void getFormatsCanHandleLargerVideos() throws YtDlpException {
<<<<<<< HEAD
    List<VideoFormat> videoFormats =
        YtDlp.getFormats("https://www.youtube.com/watch?v=jPTO3lcPpik");
    assertTrue(videoFormats.get(0).getUrl().contains("jPTO3lcPpik"));
=======
    VideoInfo videoInfo = YtDlp.getVideoInfo("https://www.youtube.com/watch?v=jPTO3lcPpik");
    assertEquals("jPTO3lcPpik", videoInfo.getId());
    assertNotNull(videoInfo.getFormats());
    assertFalse(videoInfo.getFormats().isEmpty());
>>>>>>> 51c864c (tests: improve approach to larger videos test)
  }
}
