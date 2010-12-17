package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class StringUtilities{
  private static final String blank = "";
  private static final String linker = "<a href=\"$0\">$0</a>";

  private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
  private static final DateFormat timeFormat = new SimpleDateFormat("(HH:mm:ss)");

  private static final Pattern urlPattern = Pattern.compile("http://[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+");
  private static final Pattern fileNameEscapePattern = Pattern.compile("[\\/*?\"|]");

  public static String getDateString(){
    Date d = new Date();
    return dateFormat.format(d);
  }

  public static String getTimeString(){
    Date d = new Date();
    return timeFormat.format(d);
  }

  public static String escapeFileName(String s){
    return fileNameEscapePattern.matcher(s).replaceAll(blank);
  }

  public static String escapeHTML(String s){
    return s.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
  }

  public static boolean isURL(String s){
    return urlPattern.matcher(s).matches();
  }

  public static String linkURL(String s){
    return urlPattern.matcher(s).replaceAll(linker);
  }
  
  public static String join(String separator, Object ... args){
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < args.length; i++){
      builder.append(args[i].toString());
      if(i < args.length - 1){
        builder.append(separator);
      }
    }
    return builder.toString();
  }
}
