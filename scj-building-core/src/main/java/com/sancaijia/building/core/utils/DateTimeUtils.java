package com.sancaijia.building.core.utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.time.DateUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.regex.Pattern;

public class DateTimeUtils extends DateUtils {

    public static final String TIME="yyyy/MM/dd HH:mm:ss";
    public static final String TIME2="yyyy-MM-dd HH:mm:ss";
    public static final String TIME3="yyyy-MM-dd HH:mm";
    public static final String TIME4="yyyy年MM月dd日 HH时mm分ss秒";
    public static final String TIME5="yyyyMMddHHmmss";
    public static final String DATE ="yyyy/MM/dd";
    public static final String DATE2 = "yyyy-MM-dd";
    public static final String DATE3 = "yyyy年MM月dd日";
    public static final String DATE4 = "yyyyMMdd";
    public static final String MONTH ="yyyy/MM";
    public static final String MONTH2 = "yyyy-MM";
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("^\\d{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1])) ((0[0-9])|(1[0-2])):(([0-5][0-9])|60):(([0-5][0-9])|60)$");
    private static final Pattern DATE_MINUTE_PATTERN = Pattern.compile("^\\d{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1])) ((0[0-9])|(1[0-2])):(([0-5][0-9])|60)$");
    private static final Pattern DATE_HOUR_PATTERN = Pattern.compile("^\\d{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1])) ((0[0-9])|(1[0-2]))$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$");
    private static final Pattern DATE_MONTH_PATTERN = Pattern.compile("^\\d{4}-((0[1-9])|(1[0-2]))$");
    private static final Pattern DATE_YEAR_PATTERN = Pattern.compile("^\\d{4}$");
    private static Map<Pattern,PatternUtil> patternMapper = null;
    static {
        if(patternMapper == null){
            patternMapper = new HashMap<>();
        }
        patternMapper.put(DATE_TIME_PATTERN,new PatternUtil("yyyy-MM-dd HH:mm:ss",Calendar.SECOND));
        patternMapper.put(DATE_MINUTE_PATTERN,new PatternUtil("yyyy-MM-dd HH:mm",Calendar.MINUTE));
        patternMapper.put(DATE_HOUR_PATTERN,new PatternUtil("yyyy-MM-dd HH",Calendar.HOUR_OF_DAY));
        patternMapper.put(DATE_PATTERN,new PatternUtil("yyyy-MM-dd",Calendar.DAY_OF_MONTH));
        patternMapper.put(DATE_MONTH_PATTERN,new PatternUtil("yyyy-MM",Calendar.MONTH));
        patternMapper.put(DATE_YEAR_PATTERN,new PatternUtil("yyyy",Calendar.YEAR));
    }
    public static String format(Date date, String... patterns){
        SimpleDateFormat sdf = new SimpleDateFormat();
        if(date == null){
            return null;
        }
        if(patterns.length ==0){
            sdf.applyPattern(TIME);
            return sdf.format(date);
        }
        for (String pattern : patterns) {
            try {
                sdf.applyPattern(pattern);
                return sdf.format(date);
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    /**
     * source是否等于或晚于目标日期
     * @param source
     * @param compareTarget
     * @return
     */
    public static boolean afterOrEqual(Date source,Date compareTarget){
        if(source == null || compareTarget == null){
            throw new NullPointerException("日期参数不可以为空");
        }
        return source.compareTo(compareTarget) >= 0;
    }
    /**
     * source是否早于或等于目标日期
     * @param source
     * @param compareTarget
     * @return
     */
    public static boolean beforeOrEqual(Date source,Date compareTarget){
        if(source == null || compareTarget == null){
            throw new NullPointerException("日期参数不可以为空");
        }
        return source.compareTo(compareTarget) <= 0;
    }

    /**
     * 以时间戳,构建一个date对象
     * @param timeStamp
     * @return
     */
    public static Date ofTimeStamp(Long timeStamp){
        if(timeStamp == null){
            return null;
        }
        return new Date(timeStamp);
    }

    public static String format(Long date, String... patterns){
        if(date == null){
            return null;
        }
        return format(new Date(date),patterns);
    }

    public static String format(Long date){
        if(date == null){
            return null;
        }
        return format(new Date(date),DATE);
    }

    /**
     * 字符串转换为日期
     * @param str
     * @param parsePatterns
     * @return
     */
    public static Date parseDate(final String str,final String... parsePatterns){
        try {
            return DateUtils.parseDate(str,parsePatterns);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断两个日期是否相等
     * 若任何一个日期是null,则返回false
     * @param date1
     * @param date2
     * @return
     */
    public static boolean equals(Date date1,Date date2){
        if(date1 == null || date2 == null){
            return false;
        }
        return date1.equals(date2);
    }

    public static Date add(final Date date, final int calendarField, final int amount){
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    public static Date addDays(final Date date, final int amount) {
        if(date == null){
            return null;
        }
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * 获取日期中精确到某一位的日期值
     * <br/>2018/11/12 23:59:59 999 >>年>>   2018/01/01 00:00:00 000
     * <br/>2018/11/12 23:59:59 999 >>月>>   2018/11/01 00:00:00 000
     * <br/>2018/11/12 23:59:59 999 >>日>>   2018/11/12 00:00:00 000
     * <br/>2018/11/12 23:59:59 999 >>时>>   2018/11/12 23:00:00 000
     * <br/>2018/11/12 23:59:59 999 >>分>>   2018/11/12 23:59:00 000
     * <br/>2018/11/12 23:59:59 999 >>秒>>   2018/11/12 23:59:59 000
     * <br/>2018/11/12 23:59:59 999 >>毫秒>> 2018/11/12 23:59:59 999
     * <br/>
     * @param date 日期
     * @param calendarField 要精确的位置，请使用{@link Calendar}
     * <br/>年   {@link Calendar#YEAR}
     * <br/>月   {@link Calendar#MONTH}
     * <br/>日   {@link Calendar#DAY_OF_MONTH}
     * <br/>时   {@link Calendar#HOUR_OF_DAY}
     * <br/>分   {@link Calendar#MINUTE}
     * <br/>秒   {@link Calendar#SECOND}
     * <br/>毫秒 {@link Calendar#MILLISECOND}
     * @return
     */
    public static Date getDate(final Date date, final int calendarField){
        if(date == null){
            return null;
        }
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        switch (calendarField){
            case Calendar.YEAR :
                c.set(Calendar.MONTH, 0);
            case Calendar.MONTH :
                c.set(Calendar.DAY_OF_MONTH, 1);
            case Calendar.DAY_OF_MONTH :
            case Calendar.DAY_OF_YEAR :
                c.set(Calendar.HOUR_OF_DAY, 0);
            case Calendar.HOUR_OF_DAY :
            case Calendar.HOUR :
                c.set(Calendar.MINUTE, 0);
            case Calendar.MINUTE :
                c.set(Calendar.SECOND, 0);
            case Calendar.SECOND :
                c.set(Calendar.MILLISECOND, 0);
                break;
        }
        return c.getTime();
    }

    /**
     * 获取一个Date对象的日期
     * @param date
     * @return
     */
    public static Date getDate(final Date date){
        return getDate(date,Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取日期对应位置的值
     * @param date
     * @param calendarField
     * @param defaultValue 当获取出错时,默认返回的值
     * @return
     */
    public static int get(Date date,int calendarField,int defaultValue){
        if(date == null){
            return defaultValue;
        }else{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(calendarField);
        }

    }

    /**
     * 获取一个日期的时间戳
     * @param date 原始日期
     * @param calendarField 时间戳的级别，默认为毫秒级时间戳，支持变更为秒级时间戳。
     * @return
     */
    public static Long getTimeStamp(final Date date,final int calendarField){
        long currentTimeStamp = date.getTime();
        Long timeStamp = null;
        switch (calendarField){
            case Calendar.SECOND :
                timeStamp = currentTimeStamp/1000;
                break;
            case Calendar.MILLISECOND :
                timeStamp = currentTimeStamp;
                break;
            default:
                timeStamp = currentTimeStamp;
        }
        return timeStamp;
    }

    /**
     * 传入一个日期，获取该时间的最大值,该方法适用于sql查询时，结束日期可设置为允许的最大值 <br/>
     * 2018 -> 2018-12-31 23:59:59 999 <br/>
     * 2018-01 -> 2018-01-31 23:59:59 999 <br/>
     * 2018-02 -> 2018-02-28 23:59:59 999 <br/>
     * 2018-02-21 18:01 -> 2018-02-21 18:01:59 999<br/>
     * Lian weimao CreateTime:2018年7月16日 下午5:16:17
     *
     * @param date
     * @param calendarFields 保留到的精度,该位置的数值不会发生变化,小于此位置的数据将会置为允许的最大值
     * <br/>年   {@link Calendar#YEAR}
     * <br/>月   {@link Calendar#MONTH}
     * <br/>日   {@link Calendar#DAY_OF_MONTH}
     * <br/>时   {@link Calendar#HOUR_OF_DAY}
     * <br/>分   {@link Calendar#MINUTE}
     * <br/>秒   {@link Calendar#SECOND}
     * <br/>毫秒 {@link Calendar#MILLISECOND}
     * @return
     */
    public static Date getActualMaximum(final Date date, final int... calendarFields) {
        if(date == null){
            return null;
        }
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        int calendarField = Calendar.SECOND;
        if(calendarFields != null && calendarFields.length > 0){
            calendarField =calendarFields[0];
        }
        switch (calendarField){
            case Calendar.YEAR :
                c.set(Calendar.MONTH,c.getActualMaximum(Calendar.MONTH));
            case Calendar.MONTH :
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            case Calendar.DAY_OF_MONTH :
            case Calendar.DAY_OF_YEAR :
                c.set(Calendar.HOUR_OF_DAY,c.getActualMaximum(Calendar.HOUR_OF_DAY));
            case Calendar.HOUR_OF_DAY :
            case Calendar.HOUR :
                c.set(Calendar.MINUTE, c.getActualMaximum(Calendar.MINUTE));
            case Calendar.MINUTE :
                c.set(Calendar.SECOND, c.getActualMaximum(Calendar.SECOND));
            case Calendar.SECOND :
                c.set(Calendar.MILLISECOND, c.getActualMaximum(Calendar.MILLISECOND));
                break;
        }
        return c.getTime();
    }
    public static Long getDayDiff(Date dateStart, Date dateEnd) {
        try {
            long l = dateStart.getTime() - dateEnd.getTime();
            long day = l / (24 * 60 * 60 * 1000);
            return Math.abs(day);
        } catch (Exception e) {
            return 0L;
        }
    }


    public static int getDaysDiff(Date dateStart, Date dateEnd) {
        try {
            long l = dateStart.getTime() - getDate(dateEnd).getTime();
            int day = (int) (l / (24 * 60 * 60 * 1000));
            return day;
        } catch (Exception e) {
            return 0;
        }
    }

    public static Long getSecondDiff(long dateStart, long dateEnd) {
        try {
            long l = dateStart- dateEnd;
            long day = l / 1000;
            return Math.abs(day);
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 计算两个日期之间的间隔天数
     * @param dateStart 开始日期
     * @param dateEnd 结束日期
     * @param strict 是否是严格计算
     * <table border>
     *      <tr>
     *          <td>strict</td>
     *          <td>释义</td>
     *      </tr>
     *      <tr>
     *          <td>严格true</td>
     *          <td>日期的毫秒数超过一天的毫秒数,记为1天,不足时不计算</td>
     *      </tr>
     *      <tr>
     *          <td>非严格false</td>
     *          <td>日期数字相差</td>
     *      </tr>
     *
     * </table>
     * <br>严格:  2018-01-01 23:50  -  2018-01-02 00:30  >  0;
     * <br>严格:  2018-01-01 23:50  -  2018-01-01 23:55  >  0;
     * <br>严格:  2018-01-01 23:50  -  2018-01-02 23:55  >  1;
     * <br>非严格:2018-01-01 23:50  -  2018-01-02 00:30  >  1;<br>
     * <br>非严格:2018-01-01 23:50  -  2018-01-01 23:55  >  0;<br>
     * @return
     */
    public static Long getDayDiff(Date dateStart, Date dateEnd,boolean strict) {
        if(dateStart == null || dateEnd == null){
            return null;
        }
        Long dayDiff = 0L;
        if(!strict){
            dateStart = getDate(dateStart,Calendar.DAY_OF_MONTH );
            dateEnd = getDate(dateEnd,Calendar.DAY_OF_MONTH );
        }
        try {
            long l = dateStart.getTime() - dateEnd.getTime();
            long day = l / (24 * 60 * 60 * 1000);
            dayDiff = Math.abs(day);
        } catch (Exception e) {
        }
        return dayDiff;
    }

    /**
     * 获取两个日期的年份之差
     * <br/>2017-01-15  -- 2018-01-01  |  0
     * <br/>2017-01-15  -- 2018-01-13  |  0
     * <br/>2017-01-15  -- 2018-01-14  |  1
     * <br/>2017-01-15  -- 2018-01-15  |  1
     * <br/>2017-01-15  -- 2018-01-16  |  1
     * <br/>
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public static int getYearDiff(Date dateStart, Date dateEnd) {
        Calendar start = Calendar.getInstance();
        start.setTime(dateStart);
        Calendar end = Calendar.getInstance();
        end.setTime(dateEnd);
        if (dateEnd.before(dateStart)) {
            start.setTime(dateEnd);
            end.setTime(dateStart);
        }
        end.set(Calendar.DAY_OF_MONTH,end.get(Calendar.DAY_OF_MONTH)+1);
        int startYear = start.get(Calendar.YEAR);
        int endYear = end.get(Calendar.YEAR);
        int years = endYear - startYear;
        start.set(Calendar.YEAR,start.get(Calendar.YEAR)+years);
        if (start.after(end)) {
            years--;
        }
        return years;
    }
    /**
     * 获取两个日期的月份之差
     * <br/>2017-01-15  -- 2018-01-01  |  11
     * <br/>2017-01-15  -- 2018-01-13  |  11
     * <br/>2017-01-15  -- 2018-01-14  |  12
     * <br/>2017-01-15  -- 2018-01-15  |  12
     * <br/>2017-01-15  -- 2018-01-16  |  12
     * <br/>
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public static int getMonthDiff(Date dateStart, Date dateEnd){
        Calendar start = Calendar.getInstance();
        start.setTime(dateStart);
        Calendar end = Calendar.getInstance();
        end.setTime(dateEnd);
        if (dateEnd.before(dateStart)) {
            start.setTime(dateEnd);
            end.setTime(dateStart);
        }
        end.set(Calendar.DAY_OF_MONTH,end.get(Calendar.DAY_OF_MONTH)+1);
        int months = (end.get(Calendar.YEAR)-start.get(Calendar.YEAR))*12+end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
        start.set(Calendar.MONTH,start.get(Calendar.MONTH)+months);
        if (start.after(end)) {
            months--;
        }
        return months;
    }


    /**
     * 获取起租日期和结束日期中最后一个月余下的天数
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public static int getLastDayOfRent(Date dateStart, Date dateEnd){
        Calendar start = Calendar.getInstance();
        start.setTime(dateStart);
        Calendar end = Calendar.getInstance();
        end.setTime(dateEnd);
        int yearDiff = getYearDiff(dateStart, dateEnd);
        int monthDiff = getMonthDiff(dateStart, dateEnd)%12;
        Date endTemp = null;
        if(yearDiff != 0 || monthDiff != 0){
            endTemp = calcRentEndDate(dateStart,yearDiff ,monthDiff,0 );
        }else{
            endTemp = addDays(dateStart, -1);
        }
//        end.set(Calendar.DAY_OF_MONTH,end.get(Calendar.DAY_OF_MONTH)+1);
//        start.set(Calendar.YEAR, start.get(Calendar.YEAR)+yearDiff);
//        start.set(Calendar.MONTH, start.get(Calendar.MONTH)+monthDiff%12);
        return getDayDiff(endTemp,dateEnd,true).intValue();
    }

    /**
     * 获取一个日期的某个属性值
     * @param date
     * @param field
     * @return
     */
    public static int getField(Date date,int field){
        if(date == null){
            throw new IllegalArgumentException("传入的日期为null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(field);
    }

    /**
     * 设置一个日期的某个属性为指定的属性值
     * @param date
     * @param field
     * @param value
     * @return
     */
    public static Date setField(Date date,int field,int value){
        if(date == null){
            throw new IllegalArgumentException("传入的日期为null");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(field, value);
        return calendar.getTime();
    }

    /**
     * 设置一个日期的天为指定的天
     * @param date
     * @param value
     * @return
     */
    public static Date setDay(Date date,int value){
        return setField(date,Calendar.DAY_OF_MONTH,value);
    }

    /**
     * 获取一个日期的天
     * @param date
     * @return
     */
    public static int getDay(Date date){
        return getField(date,Calendar.DAY_OF_MONTH);
    }



    /**
     * 获取两个时间戳之间的天、小时、分钟
     * @param endDate
     * @param nowDate
     * @return
     */
    public static String getDatePoor(Date endDate, Date nowDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        if (day > 0) {
            return day + "天";
        } else if (hour > 0) {
            return hour + "小时" + min + "分钟";
        } else if (min > 0) {
            return min + "分钟";
        }
        return null;
    }

    private static Date addRentYear(Date start,int amount){
        if(amount <= 0){
            throw new IllegalArgumentException("租期不能为0或小于0");
        }
        Date endDate = addYears(start,amount);
        int startDay = getField(start, Calendar.DAY_OF_MONTH);
        int endDay = getField(endDate, Calendar.DAY_OF_MONTH);
        if(startDay == endDay){
            endDate = addDays(endDate, -1);
        }
        return endDate;
    }
    private static Date addRentMonth(Date start,int amount){
        if(amount <= 0){
            throw new IllegalArgumentException("租期不能为0或小于0");
        }
        Date endDate = addMonths(start,amount);
        int startMonth = getField(start, Calendar.DAY_OF_MONTH);
        int endMonth = getField(endDate, Calendar.DAY_OF_MONTH);
        if(startMonth == endMonth){
            endDate = addDays(endDate, -1);
        }
        return endDate;
    }
    private static Date addRentDay(Date start,int amount){
        if(amount <= 0){
            throw new IllegalArgumentException("租期不能为0或小于0");
        }
        Date endDate = addDays(start,amount-1);
        int startDay = getField(start, Calendar.DAY_OF_MONTH);
        int endDay = getField(endDate, Calendar.DAY_OF_MONTH);
        return endDate;
    }

    /**
     * 根据租赁市场通用原则,计算租客租期
     * @param startDate 租客开始时间
     * @param year 年
     * @param month 月
     * @param day 日
     * @return
     */
    public static Date calcRentEndDate(Date startDate,Integer year,Integer month,Integer day){
        if(startDate == null){
            return null;
        }
        year = Optional.ofNullable(year).orElse(0);
        month = Optional.ofNullable(month).orElse(0);
        day = Optional.ofNullable(day).orElse(0);
        boolean needPlusOneDay = false;
        Date endDate = startDate;
        if(year > 0){
            endDate = addRentYear(startDate,year );
            needPlusOneDay = true;
        }
        if(month > 0){
            if(needPlusOneDay){
                endDate = addDays(endDate, 1);
            }
            endDate = addRentMonth(endDate,month );
            needPlusOneDay = true;
        }
        if(day > 0){
            if(needPlusOneDay){
                endDate = addDays(endDate, 1);
            }
            endDate = addRentDay(endDate,day );
        }
        return endDate;
    }

    /**
     * 获取指定日期所在周的指定星期的日期,注意,星期以周一开始,周日结束
     * @param source 日期
     * @param weekField 星期属性,参考
     * 周日{@link Calendar#SUNDAY}
     * 周一{@link Calendar#MONDAY}
     * 周二{@link Calendar#TUESDAY}
     * 周三{@link Calendar#WEDNESDAY}
     * 周四{@link Calendar#THURSDAY}
     * 周五{@link Calendar#FRIDAY}
     * 周六{@link Calendar#SATURDAY}
     * @return
     */
    public static Date getWeekDate(Date source,Integer weekField){
        Calendar c = Calendar.getInstance();
        c.setTime(source);
        int week = c.get(Calendar.DAY_OF_WEEK);
        if(week == weekField){
            return source;
        }
        if(week == Calendar.SUNDAY){
            week += 7;
        }
        if(weekField == Calendar.SUNDAY){
            weekField += 7;
        }
        int minusCount = week - weekField;
        return addDays(source, 0-minusCount);
    }


    /**
     * 自动转换字符串为date对象
     * <br/>默认与内置的规则进行匹配,以返回一个最符合字符串的Date对象
     * <br/>默认规则可识别如下格式:
     * <br/>yyyy-MM-dd HH:mm:ss
     * <br/>yyyy-MM-dd HH:mm
     * <br/>yyyy-MM-dd HH
     * <br/>yyyy-MM-dd
     * @param dateStr
     * @param otherFieldSetMax 其他位置是否设为允许的最大值
     * @param patterns 如果有定义传入,会优先使用这个来转换
     * @return
     */
    public static Date autoParse(String dateStr,boolean otherFieldSetMax,String... patterns) throws ParseException {
        Date date = null;
        if(patterns.length > 0){
            date = parseDateStrictly(dateStr, patterns);
        }
        if(date != null){
            return date;
        }
        Set<Map.Entry<Pattern, PatternUtil>> entries = patternMapper.entrySet();
        for (Map.Entry<Pattern, PatternUtil> patternUtilEntry : entries) {
            if(patternUtilEntry.getKey().matcher(dateStr).matches()){
                PatternUtil util = patternUtilEntry.getValue();
                date = parseDateStrictly(dateStr,util.getDatePattern());
                if(otherFieldSetMax){
                    date = getActualMaximum(date,util.getLastField() );
                }
                return date;
            }
        }
        return null;
    }


    public static Date today() {
        return getDate(new Date());
    }
    public static Date tomorrow() {
        return addDays(today(),1);
    }

    /**
     * 判断时间参数,是否在一个时间范围内
     * 若传入时间晚于或等于开始时间,并且早于或等于结束时间,则判定为在范围内
     * @param date 时间参数
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    public static boolean inDateScope(Date date, Date startDate, Date endDate) {
        if(date == null || startDate == null || endDate == null){
            return false;
        }
        long timeStamp = date.getTime();
        long startTimeStamp = startDate.getTime();
        long endTimeStamp = endDate.getTime();
        return timeStamp >= Math.min(startTimeStamp, endTimeStamp) && timeStamp <= Math.max(startTimeStamp,endTimeStamp );
    }


    @Getter
    @AllArgsConstructor
    static class PatternUtil{
        private String datePattern;
        private int lastField;

    }

    // 获取某天所在月的最大天 2019-10-31 00:00:00
    public static Date getEndOfMonth(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime result = localDateTime.with(TemporalAdjusters.lastDayOfMonth());
        return Date.from(result.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 格式化两个日期间的每一个时间节点
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param calendarField 时间步进的范围,取值来自{@link Calendar}
     * 通常取值
     * <table>
     *     <tr><td>年</td><td>{@link Calendar#YEAR}</td></tr>
     *     <tr><td>月</td><td>{@link Calendar#MONTH}</td></tr>
     *     <tr><td>日</td><td>{@link Calendar#DAY_OF_MONTH}</td></tr>
     *     <tr><td>时</td><td>{@link Calendar#HOUR_OF_DAY}</td></tr>
     *     <tr><td>分</td><td>{@link Calendar#MINUTE}</td></tr>
     *     <tr><td>秒</td><td>{@link Calendar#SECOND}</td></tr>
     * </table>
     * @param formatPattern 格式化表达式
     * @return 如下表,<b>连续出现相同</b>的内容会去重
     * <table>
     *     <tr>
     *         <th>格式化表达式\日期</th>
     *         <th>2020-01-01 10:00:00 至 2020-05-01 10:00:00 </th>
     *     </tr>
     *     <tr>
     *         <td>yyyy</td>
     *         <td>[2020]</td>
     *     </tr>
     *     <tr>
     *         <td>yyyy-MM</td>
     *         <td>[2020-01,2020-02,2020-03,2020-04,2020-05]</td>
     *     </tr>
     *     <tr>
     *         <td>MM-dd</td>
     *         <td>[01-01,01-02,.....,04-30,05-01]</td>
     *     </tr>
     * </table>
     */
    public static List<String> formatEveryTimeUnit(Date startDate,Date endDate,int calendarField,String formatPattern){
        if(startDate == null || endDate == null || formatPattern == null){
            throw new IllegalArgumentException("开始或结束日期不能为空");
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatPattern);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("格式化规则无法识别");
        }
        if(endDate.before(startDate)){
            Date dateTemp = (Date) endDate.clone();
            endDate = startDate;
            startDate = dateTemp;
        }
        List<String> formatList = new ArrayList<>();
        Date dateTime = startDate;
        String beforeFormatStr = null;
        while(true){
            String formatStr = format(dateTime, formatPattern);
            if(!formatStr.equals(beforeFormatStr)){
                formatList.add(formatStr);
                beforeFormatStr = formatStr;
            }
            dateTime = add(dateTime,calendarField,1);
            if(dateTime.after(endDate)){
                break;
            }
        }
        return formatList;
    }


    /**
     * 获取最早的日期
     * @param date1
     * @param otherDate
     * @return
     */
    public static Date earliest(Date date1,Date... otherDate){
        List<Date> dateList = new ArrayList<>();
        dateList.add(date1);
        dateList.addAll(Arrays.asList(otherDate));
        return dateList.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Date::getTime))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取最晚的日期
     * @param date1
     * @param otherDate
     * @return
     */
    public static Date latest(Date date1,Date... otherDate){
        List<Date> dateList = new ArrayList<>();
        dateList.add(date1);
        dateList.addAll(Arrays.asList(otherDate));
        return dateList.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Date::getTime).reversed())
                .findFirst()
                .orElse(null);
    }


    /**
     * 按照每年执行一次,计算执行次数
     * 2020-02-29 - 2021-02-27 >> 1
     * 2020-02-29 - 2021-02-28 >> 1
     * 2020-02-29 - 2021-03-01 >> 2
     * 2020-02-29 - 2021-03-02 >> 2
     * @param startTime
     * @param endTime
     * @return
     */
    public static int calcProcessCountEveryYear(Date startTime,Date endTime) {
        if(startTime == null || endTime == null){
            return 0;
        }
        Calendar startTimeC = DateTimeUtils.toCalendar(DateTimeUtils.earliest(startTime,endTime));
        Calendar endTimeC = DateTimeUtils.toCalendar(DateTimeUtils.latest(startTime,endTime));
        //获取年份差
        int startYear = startTimeC.get(Calendar.YEAR);
        int endYear = endTimeC.get(Calendar.YEAR);
        if(startYear == endYear){
            return 1;
        }
        int yearDiff = endYear - startYear;
        int splitCount = yearDiff + 1;;
        //开始日期按照年份差,增加年份
        Date rentEndDate = getActualMaximum(calcRentEndDate(startTimeC.getTime(), yearDiff, 0, 0),Calendar.DAY_OF_MONTH);
        if(!rentEndDate.before(endTimeC.getTime()) ){
            splitCount--;
        }
        return splitCount;
    }



    //判断选择的日期是否是本周
    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(currentDayOfWeek == 1) currentWeek -= 1;
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int paramDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(paramDayOfWeek == 1) paramWeek -= 1;
        if(paramWeek==currentWeek){
            return true;
        }
        return false;
    }
    //判断选择的日期是否是今天
    public static boolean isToday(long time)
    {
        return isThisTime(time,"yyyy-MM-dd");
    }
    //判断选择的日期是否是本月
    public static boolean isThisMonth(long time)
    {
        return isThisTime(time,"yyyy-MM");
    }

    //判断选择的日期是否是本年
    public static boolean isThisYear(long time)
    {
        return isThisTime(time,"yyyy");
    }
    private static boolean isThisTime(long time,String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if(param.equals(now)){
            return true;
        }
        return false;
    }

    /**
     * 比较传入的时间是否在范围内
     * @param time 传入的时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param containEqual 是否包含相等值
     * @return
     */
    public static boolean isBetween(Date time, Date startTime, Date endTime,Boolean containEqual) {
        if (startTime == null || endTime == null || time == null) {
            return false;
        }
        if(containEqual && (time == startTime || time == endTime)) return true;
        return time.after(startTime) && time.before(endTime);
    }



   /* @Data
    @AllArgsConstructor
    private static class DateInfo{
        private Date date;
        private DateTimeUtils.DateType type;
    }*/

   /* @Getter
    @AllArgsConstructor
    private static enum DateType implements IBizEnum {
        PLUS(1,"日期增加"),
        MINUS(2,"日期减少");
        private Integer value;
        private String description;
    }
*/

    public final static String UTC = "GMT+8";

    /**
     * 获取指定某一天的开始时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @param timeZone  如 GMT+8:00
     * @return
     */
    public static Long getDailyStartTime(Long timeStamp, String timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取指定某一天的结束时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @param timeZone  如 GMT+8:00
     * @return
     */
    public static Long getDailyEndTime(Long timeStamp, String timeZone) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当月开始时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @param timeZone  如 GMT+8:00
     * @return
     */
    public static Long getMonthStartTime(Long timeStamp, String timeZone) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当月的结束时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @param timeZone  如 GMT+8:00
     * @return
     */
    public static Long getMonthEndTime(Long timeStamp, String timeZone) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));// 获取当前月最后一天
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当年的开始时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @param timeZone  如 GMT+8:00
     * @return
     */
    public static Long getYearStartTime(Long timeStamp, String timeZone) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.DATE, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当年的最后时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @param timeZone  如 GMT+8:00
     * @return
     */
    public static Long getYearEndTime(Long timeStamp, String timeZone) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(timeStamp);
        int year = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTimeInMillis();
    }



}

