package com.jfposton.ytdlp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfposton.ytdlp.mapper.VideoFormat;
import com.jfposton.ytdlp.mapper.VideoInfo;
import com.jfposton.ytdlp.mapper.VideoThumbnail;
import com.jfposton.ytdlp.utils.StreamGobbler;
import com.jfposton.ytdlp.utils.StreamProcessExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Provide an interface for yt-dlp executable
 *
 * <p>For more information on yt-dlp, please see <a
 * href="https://github.com/yt-dlp/yt-dlp/blob/master/README.md">yt-dlp Documentation</a>
 */
public class YtDlp {

  private YtDlp() {
    // Private constructor is here to encourage static usage of this class
  }

  /** yt-dlp executable name */
  protected static String executablePath = "yt-dlp";

  /**
   * Append executable name to command
   *
   * @param command Command string
   * @return Command string
   */
  protected static String buildCommand(String command) {
    return String.format("%s %s", executablePath, command);
  }

  /**
   * Execute yt-dlp request
   *
   * @param request request object
   * @return response object
   * @throws YtDlpException
   */
  public static YtDlpResponse execute(YtDlpRequest request) throws YtDlpException {
    return execute(request, null);
  }

  /**
   * Execute yt-dlp request
   *
   * @param request request object
   * @param callback callback
   * @return response object
   * @throws YtDlpException
   */
  public static YtDlpResponse execute(YtDlpRequest request, DownloadProgressCallback callback)
      throws YtDlpException {

    String command = buildCommand(request.buildOptions());
    String directory = request.getDirectory();
    Map<String, String> options = request.getOption();

    YtDlpResponse ytDlpResponse;
    Process process;
    int exitCode;
    StringBuilder outBuffer = new StringBuilder(); // stdout
    StringBuilder errBuffer = new StringBuilder(); // stderr
    long startTime = System.nanoTime();

    String[] split = command.split(" ");

    ProcessBuilder processBuilder = new ProcessBuilder(split);

    // Define directory if one is passed
    if (directory != null) processBuilder.directory(new File(directory));

    try {
      process = processBuilder.start();
    } catch (IOException e) {
      throw new YtDlpException(e);
    }

    InputStream outStream = process.getInputStream();
    InputStream errStream = process.getErrorStream();

    StreamProcessExtractor stdOutProcessor =
        new StreamProcessExtractor(outBuffer, outStream, callback);
    StreamGobbler stdErrProcessor = new StreamGobbler(errBuffer, errStream, callback);

    try {
      stdOutProcessor.join();
      stdErrProcessor.join();
      exitCode = process.waitFor();
    } catch (InterruptedException e) {

      // process exited for some reason
      throw new YtDlpException(e);
    }

    String out = outBuffer.toString();
    String err = errBuffer.toString();

    if (exitCode > 0) {
      throw new YtDlpException(err);
    }

    int elapsedTime = (int) ((System.nanoTime() - startTime) / 1000000);

    ytDlpResponse = new YtDlpResponse(command, options, directory, exitCode, elapsedTime, out, err);

    return ytDlpResponse;
  }

  /**
   * Get yt-dlp executable version
   *
   * @return version string
   * @throws YtDlpException
   */
  public static String getVersion() throws YtDlpException {
    YtDlpRequest request = new YtDlpRequest();
    request.setOption("version");
    return YtDlp.execute(request).getOut();
  }

  /**
   * Retrieve all information available on a video
   *
   * @param url Video url
   * @return Video info
   * @throws YtDlpException
   */
  public static VideoInfo getVideoInfo(String url) throws YtDlpException {

    // Build request
    YtDlpRequest request = new YtDlpRequest(url);
    request.setOption("dump-json");
    request.setOption("no-playlist");
    YtDlpResponse response = YtDlp.execute(request);

    // Parse result
    ObjectMapper objectMapper = new ObjectMapper();
    VideoInfo videoInfo;

    try {
      videoInfo = objectMapper.readValue(response.getOut(), VideoInfo.class);
    } catch (IOException e) {
      throw new YtDlpException("Unable to parse video information: " + e.getMessage());
    }

    return videoInfo;
  }

  /**
   * List formats
   *
   * @param url Video url
   * @return list of formats
   * @throws YtDlpException
   */
  public static List<VideoFormat> getFormats(String url) throws YtDlpException {
    VideoInfo info = getVideoInfo(url);
    return info.getFormats();
  }

  /**
   * List thumbnails
   *
   * @param url Video url
   * @return list of thumbnail
   * @throws YtDlpException
   */
  public static List<VideoThumbnail> getThumbnails(String url) throws YtDlpException {
    VideoInfo info = getVideoInfo(url);
    return info.getThumbnails();
  }

  /**
   * List categories
   *
   * @param url Video url
   * @return list of category
   * @throws YtDlpException
   */
  public static List<String> getCategories(String url) throws YtDlpException {
    VideoInfo info = getVideoInfo(url);
    return info.getCategories();
  }

  /**
   * List tags
   *
   * @param url Video url
   * @return list of tag
   * @throws YtDlpException
   */
  public static List<String> getTags(String url) throws YtDlpException {
    VideoInfo info = getVideoInfo(url);
    return info.getTags();
  }

  /**
   * Get command executable or path to the executable
   *
   * @return path string
   */
  public static String getExecutablePath() {
    return executablePath;
  }

  /**
   * Set path to use for the command
   *
   * @param path String path to the executable
   */
  public static void setExecutablePath(String path) {
    executablePath = path;
  }
}
