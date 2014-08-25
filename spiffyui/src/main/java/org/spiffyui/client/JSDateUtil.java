/*******************************************************************************
 * 
 * Copyright 2011-2014 Spiffy UI Team   
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.spiffyui.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * <p> 
 * A collection of utility methods for manipulating dates in the browser. 
 * </p> 
 *  
 * <p> 
 * JSDateUtil provide flexible locale aware support for formatting dates and times in GWT. 
 * This class fills in many of the gaps missing from the java.text package from the JDK. 
 * </p> 
 *  
 * <p> 
 * In addition to standard date formatting this library supports custom formatting following 
 * the <a href="http://code.google.com/p/spiffyui/wiki/DateFormatters">Spiffy UI date formatters</a> 
 * specification. 
 * </p> 
 */
public final class JSDateUtil
{
    
    /**
     * Making sure this class can't be instantiated.
     */
    private JSDateUtil()
    {
    }
    
    /**
     * Convert java.util.Date to Date format in the current locale
     * @param date - the Date object to convert
     * @return - Date format in String
     */    
    public static String getDate(Date date)
    {
        return getDate(date.getTime() + "");
    }
    
    /**
     * <p> 
     * Convert UTC epoch format to short Date format in the current locale 
     * </p> 
     *  
     * @param epochDate - the time in milliseconds since Jan 1, 1970
     * @return - Date format in String
     */    
    public static String getShortDate(String epochDate)
    {
        return getDate(epochDate);
    }
    
    /**
     * Convert UTC epoch format to short Date format in the current locale 
     *  
     * @param epochDate - the time in milliseconds since Jan 1, 1970
     * @return - Date format in String
     */    
    public static native String getDate(String epochDate) /*-{
        return $wnd.spiffyui.getDate(parseInt(epochDate, 10));
    }-*/;

    /**
     * Convert UTC epoch format to a date string matching the specified format.
     * See <a href="http://code.google.com/p/spiffyui/wiki/DateFormatters">Format Specifiers</a>
     * for more details
     *  
     * @param date - the date object to format
     * @param format - the formatter for the date
     * @return - Time format as a String
     */    
    public static String getDate(Date date, String format)
    {
        return getDate(date.getTime() + "", format);
    }

    /**
     * Format the specified date according to the format string.
     * See <a href="http://code.google.com/p/spiffyui/wiki/DateFormatters">Format Specifiers</a>
     *  
     * @param date - the date object to format
     * @param format - the formatter for the date
     * 
     * @return - the formatted string
     */    
    public static String format(Date date, String format)
    {
        return format("" + date.getTime(), format);
    }

    /**
     * Format the specified date according to the format string.
     * See <a href="http://code.google.com/p/spiffyui/wiki/DateFormatters">Format Specifiers</a>
     *  
     * @param epochDate - the long date to format
     * @param format - the formatter for the date
     * 
     * @return - the formatted string
     */    
    public static native String format(String epochDate, String format) /*-{
        return $wnd.spiffyui.formatDate(parseInt(epochDate, 10), format);
    }-*/;

    /**
     * Convert UTC epoch format to a date string matching the specified format.
     * See <a href="http://code.google.com/p/spiffyui/wiki/DateFormatters">Format Specifiers</a>
     * for more details
     *  
     * @param epochDate - the time in milliseconds since Jan , 1, 1970
     * @param format - the formatter for the date
     * @return - Time format as a String
     */    
    public static native String getDate(String epochDate, String format) /*-{
        return $wnd.spiffyui.getDateString(parseInt(epochDate, 10), format);
    }-*/;
    
    /**
     * Convert UTC epoch format to Date format in the current locale 
     *  
     * @param epochDate - the time in milliseconds since Jan 1, 1970
     * @return - Date format in String
     */    
    public static native String getLongDate(String epochDate) /*-{
        return $wnd.spiffyui.getLongDate(parseInt(epochDate, 10));
    }-*/;

    /**
     * Convert UTC epoch format to Date format in the current locale 
     *  
     * @return - Date format in String
     */    
    public static native String getToday() /*-{
        return String($wnd.spiffyui.getToday());
    }-*/;
    
    /**
     * Adds the 1 day to today 
     *  
     * @return - epochDate time in milliseconds String
     */    
    public static native String nextDay() /*-{
        return String($wnd.spiffyui.nextDay());
    }-*/;
    
    
    /**
     * Convert java.util.Date to Short Time format in the current locale
     * @param date - the Date object to convert
     * @return - Time format in String
     */    
    public static String getShortTime(Date date)
    {
        return getShortTime(date.getTime() + "");
    }
    
    /**
     * Convert UTC epoch format to short time format in the current locale 
     *  
     * @param epochDate - the time in milliseconds since Jan 1, 1970
     * @return - Time format in String
     */    
    public static native String getShortTime(String epochDate) /*-{
        return $wnd.spiffyui.getShortTime(parseInt(epochDate, 10));
    }-*/;

    /**
     * Convert UTC epoch format to short time format in the current locale 
     * rounded up to the nearest 30 minutes. 
     *  
     * @param epochDate - the time in milliseconds since Jan 1, 1970
     * @return - Date format in String
     */    
    public static native String getShortTimeRounded(String epochDate) /*-{
        return $wnd.spiffyui.getShortTimeRounded(parseInt(epochDate, 10));
    }-*/;
    
    /**
     * Gets the hours from the specified short time string
     *  
     * @param time - the time 11:30pm
     * @return - the hours
     */    
    public static native int getHours(String time) /*-{ 
        return $wnd.spiffyui.getDateFromShortTime(time).getHours();
    }-*/;
    
    /**
     * Gets the minutes from the specified short time string
     *  
     * @param time - the time 11:30pm
     * @return - the hours
     */    
    public static native int getMinutes(String time) /*-{ 
        return $wnd.spiffyui.getDateFromShortTime(time).getMinutes();
    }-*/;
    
  
    /**
     * Convert Date to UTC Epoch format
     * @param dateString - the String to test
     * @return - Time in epoch format
     */    
    public static native String getEpoch(String dateString) /*-{
        return $wnd.spiffyui.getEpoch(dateString);
    }-*/;

    /**
     * Gets the short date format string in the current locale.  In en-US 
     * this is M/d/yyyy in fr_FR it is dd/MM/yyyy 
     *
     * @return - The date format
     */    
    public static native String getShortDateFormat() /*-{
        return $wnd.Date.CultureInfo.formatPatterns.shortDate;
    }-*/;

    /**
     * Gets the long date format string in the current locale.  In en-US 
     * this is dddd, MMMM dd, yyyy in fr_FR it is dddd d MMMM yyyy 
     *
     * @return - The date format
     */    
    public static native String getLongDateFormat() /*-{
        return $wnd.Date.CultureInfo.formatPatterns.longDate;
    }-*/;


    /**
     * Gets the month day format string in the current locale.  In en-US 
     * this is MMMM dd in fr_FR it is d MMMM
     *
     * @return - The date format
     */    
    public static native String getMonthDayFormat() /*-{
        return $wnd.Date.CultureInfo.formatPatterns.monthDay;
    }-*/;

    
    /**
     * Gets the short time format string in the current locale.  In en-US 
     * this is h:mm tt in fr_FR it is HH:mm 
     *
     * @return - The time format
     */    
    public static native String getShortTimeFormat() /*-{
        return $wnd.Date.CultureInfo.formatPatterns.shortTime;
    }-*/;

    /**
     * <p> 
     * Gets the current locale of the application running in the browser. 
     * </p> 
     *  
     * <p> 
     * This methods returns the locale being used by GWT if the GWTLocaleFilter is 
     * used in the application.  This locale is the best match between the user's 
     * preferred locale and the locales supported in the application.  The user's 
     * preferred locale may be different than the installed locale of the browser. 
     * </p> 
     *  
     * <p> 
     * For example, if the application is localized into English (en), French (fr), 
     * and Japanese (ja) the the user will get the follow locales: 
     * </p> 
     *  
     * <h3>Example 1</h3>
     * <p>
     * The user's list of preferred locales is:
     * </p>
     * 
     * <ol>
     * <li>ko_KR (South Korean)</li>
     * <li>ja_JP (Japanese)</li>
     * <li>en (English) </li>
     * </ol> 
     *  
     * <p> 
     * In this example the locale would be Japanese. 
     * </p> 
     *  
     * <h3>Example 2</h3> 
     * <p> 
     * The user's list of preferred locales is: 
     * </p> 
     *  
     * <ol> 
     * <li>en_GB (English - United Kingdom)</li>
     * <li>fr (French)</li>
     * <li>en_US (English - United States)</li>
     * </ol> 
     *  
     * <p> 
     * In this example the user will get French because that is the closest exact match. 
     * If the application was localized into English - United Kingdom (en_GB) then the 
     * user would get that locale. 
     * </p> 
     *  
     *  
     * <h3>Example 3</h3> 
     * <p> 
     * The user's list of preferred locales is: 
     * </p> 
     * 
     * <ol> 
     * <li>iw_IL (Hebrew Israel)</li>
     * <li>is_IS (Icelandic Iceland) </li>
     * </ol> 
     *  
     * <p> 
     * In this example the user will get English because none of their preferred locales 
     * are supported and English is the default.
     * </p> 
     *  
     * <p> 
     * For more information on these processes see
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html">section 14.4</a> 
     * of the HTTP specification.
     * </p> 
     *
     * @return The client Locale
     */    
    public static native String getLocale() /*-{
        return $wnd.Date.CultureInfo.name;
    }-*/;
    
    /**
     * Convert the specified String to a date object using the short 
     * format of the current locale
     * @param dateString - the String to convert
     * @return - The Date object
     */
    public static Date parseShortDate(String dateString)
    {
        return DateTimeFormat.getFormat(getShortDateFormat()).parseStrict(dateString);
    }
    
    /**
     * Determines if the browser's current locale uses 24 hour time
     * 
     * @return True if the locale uses 24 time and false otherwise
     */
    public static native boolean is24Time()  /*-{
        return $wnd.Date.CultureInfo.formatPatterns.shortTime.indexOf("tt") === -1;
    }-*/;
    
    
    /**
     * Gets the full date time format string in the current locale.  In en-US 
     * this is dddd, MMMM dd, yyyy h:mm:ss tt
     *
     * @return - The time format
     */    
    public static native String getFullDateTimeFormat() /*-{
        return $wnd.Date.CultureInfo.formatPatterns.fullDateTime;
    }-*/;
    
    /**
     * Convert java.util.Date to Full Date Time format in the current locale
     * @param date - the Date object to convert
     * @return - Date Time format in String
     */    
    public static String getFullDateTime(Date date)
    {
        return getFullDateTime(date.getTime() + "");
    }
    
    /**
     * Convert UTC epoch format to Date Time format in the current locale 
     *  
     * @param epochDate - the time in milliseconds since Jan 1, 1970
     * @return - Date Time format in String
     */    
    public static native String getFullDateTime(String epochDate) /*-{
        return $wnd.spiffyui.getDateTime(parseInt(epochDate, 10));
    }-*/;
    
    
    /**
     * Converts a java.util.Date to a concatenation of the Short Date and Short Time format 
     * in the current locale
     * @param date - the Date object to convert
     * @return - Short Date and Short Time format in String
     */
    public static String getShortDateTime(Date date) 
    {
        return getDate(date) + " " + getShortTime(date);
    }

    /**
     * Add to a specified date
     * @param date - a Date to start
     * @param amt - the amount to add
     * @param unit - the unit to add.  Can be WEEK, MONTH, YEAR, HOUR, MINUTE, SECOND or defaults to DAY
     * @return the new date
     */
    public static Date dateAdd(Date date, int amt, String unit)
    {
        String epochDate = dateAdd(String.valueOf(date.getTime()), amt, unit);
        return new Date(Long.parseLong(epochDate));
    }
    /**
     * Add to a specified epoch date
     * @param epochDate - the time in milliseconds since Jan 1, 1970
     * @param amt - the amount to add
     * @param unit - the unit to add.  Can be WEEK, MONTH, YEAR, HOUR, MINUTE, SECOND or defaults to DAY
     * @return the new date as an epoch 
     */    
    public static native String dateAdd(String epochDate, int amt, String unit) /*-{
        return String($wnd.spiffyui.dateAdd(parseInt(epochDate, 10), amt, unit));
    }-*/;

    /**
     * Convert UTC epoch format to Month Day format in the current locale 
     *  
     * @param epochDate - the time in milliseconds since Jan 1, 1970
     * @param abbrev - if true, then abbreviate the month
     * @return - Date format in String
     */    
    public static native String getMonthDay(String epochDate, boolean abbrev) /*-{
        return $wnd.spiffyui.getMonthDay(parseInt(epochDate, 10), abbrev);
    }-*/;
    
    /**
     * Convert UTC epoch format to Month Day format in the current locale 
     * concatenated with the Short Time
     *  
     * @param epochDate - the time in milliseconds since Jan 1, 1970
     * @return - Date format in String
     */    
    public static String getShortMonthDayTime(String epochDate) 
    {
        return getMonthDay(epochDate, true) + " " + getShortTime(epochDate);
    }
    
    /**
     * Get the Ordinal day (numeric day number) of the year, adjusted for leap year. 
     * Returns 1 through 365 (366 in leap years). 
     * @param date - a Date
     * @return the day of year
     */
    public static int getOrdinalNumber(Date date)
    {
        return getOrdinalNumber(String.valueOf(date.getTime()));
    }

    /**
     * Get the Ordinal day (numeric day number) of the year, adjusted for leap year. 
     * Returns 1 through 365 (366 in leap years). 
     * @param epochDate - the time in milliseconds since Jan 1, 1970
     * @return the day of year
     */
    public static native int getOrdinalNumber(String epochDate) /*-{
        return $wnd.spiffyui.getOrdinalNumber(parseInt(epochDate));
    }-*/;
    
    /**
     * Get the UTC offset of the current browser.  This returns 
     * a number in hours like -5 or 8 
     * 
     * @return the UTC offset
     */
    public static native int getUTCOffset() /*-{
        return $wnd.spiffyui.getUTCOffset();
    }-*/;
}
