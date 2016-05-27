package com.bridgepoint.fortune;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;

public class Fortune {

  private static final String TOKENIZER = "%";

  private static final String SAFE_TXT = "safe.txt";
  private static final String WRIGHT_TXT = "wright.txt";

  private static final String DEFAULT_TXT = WRIGHT_TXT;

  @SuppressWarnings("unused")
  private static List<String> ALL_FILES = Arrays.asList( //
      SAFE_TXT, //
      WRIGHT_TXT //
      );

  public Fortune() {
    super();
  }

  /**
   * @param args
   */
  public static void main(final String[] args) {
    int count = 1;
    String filename = DEFAULT_TXT;

    if (args.length > 1) {
      filename = args[0];
      try {
        count = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {
        System.out.println("Bad second argument: '" + args[1] + " is not a number - defaulting to one'");
      }
    } else if (args.length == 1) {
      try {
        count = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        filename = args[0];
      }
    }
    Fortune fortune = new Fortune();
    System.out.println(fortune.getFortune(filename, count));
  }

  protected static Map<String, List<String>> fortuneCache = new HashMap<String, List<String>>();

  protected synchronized void init(final String filename, final boolean reset) {
    if (fortuneCache.get(filename) == null || fortuneCache.get(filename).size() == 0 || reset) {
      InputStream inStream = getClass().getResourceAsStream(filename);
      List<String> fortunes = new ArrayList<String>();
      fortuneCache.put(filename, fortunes);
      try {
        convertLinesIntoCookies(IOUtils.readLines(inStream), fortunes);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        org.apache.commons.io.IOUtils.closeQuietly(inStream);
      }
    }
  }

  protected void convertLinesIntoCookies(final List<String> lines, final List<String> fortunes) {
    StringBuffer fortuneBuffer = new StringBuffer();
    for (String line : lines) {
      if (line != null && line.length() > 0) {
        if (line.trim().startsWith(TOKENIZER) && fortuneBuffer.length() > 0) {
          fortunes.add(fortuneBuffer.toString());
          fortuneBuffer = new StringBuffer();
        } else {
          fortuneBuffer.append(line + "\n");
        }
      }
    }
    if (fortuneBuffer.length() > 0) {
      fortunes.add(fortuneBuffer.toString());
    }
  }

  public String getFortune() throws IOException {
    return getFortune(DEFAULT_TXT, 1);
  }

  public String getFortune(final String filename) throws IOException {
    return getFortune(filename, 1);
  }

  public String getFortune(final String filenameIn, final int count) {
    String filename = (filenameIn == null || filenameIn.length() == 0) ? DEFAULT_TXT : filenameIn;
    init(filename, false);
    if (fortuneCache.get(filename) == null || fortuneCache.get(filename).size() == 0) {
      return "No fortunes found";
    }
    Random random = new Random(new Date().getTime());
    OutputStream outStream = new ByteArrayOutputStream();
    try {
      for (int i = 0; i < count; i++) {
        if (i > 0) {
          IOUtils.write("\n", outStream);
        }
        IOUtils.write(fortuneCache.get(filename).get(random.nextInt(fortuneCache.get(filename).size())), outStream);
      }
    } catch (IOException e) {
      return e.getMessage();
    }
    return outStream.toString();
  }
}
