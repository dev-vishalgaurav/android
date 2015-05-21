/**
 * 
 */

package com.calendarsample;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author Vishal Gaurav (vishal.gaurav@hotmail.com)
 */
public class CalendarView extends FrameLayout implements OnClickListener {
    
    private static class CalendarFormatter{
        private static final String MONTH_FORMAT = "MMMM";
        private static final String MONTH_WITH_YEAR_FORMAT = "MMMM, yyyy ";

        private static final String WEEK_FORMAT = "cc" ;
        private SimpleDateFormat calendarFormatter = null; 
        public CalendarFormatter() {
            calendarFormatter = new SimpleDateFormat();
        }
        
        public String getMonth(Calendar calendar){
             calendarFormatter.applyPattern(MONTH_WITH_YEAR_FORMAT);
             return calendarFormatter.format(calendar.getTime());
        }
        public String[] getWeekDays(){
            String weekdays[] = new String[7];
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            calendarFormatter.applyPattern(WEEK_FORMAT);
            for (int weekCount = 0; weekCount < weekdays.length; weekCount++) {
                weekdays[weekCount] = calendarFormatter.format(calendar.getTime());
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            return weekdays;
        }
        
        
    }
    private static class CalendarViewInfo {
        private static final int INFO_KEY = R.id.txtCalDay;
        private int year;
        private int month;
        private int day;
        public CalendarViewInfo() {
            super();
        }
        public void set(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }
    
    private View mRootView = null;
    private ViewGroup mWeeksHeader = null ;
    private View mMonthsHeader = null;
    private ViewGroup mWeeksContainer = null;
    private View mHandleLeft = null;
    private View mHandleRight = null ;
    private View[][] mDaysHolder = null ;
    private View[] mWeeksHolder = null ;
    private TextView mMonthText = null;
    private Context mContext = null;
    private String[] mWeekDaysStrings = null ;
    private Calendar mCalendarRunning ;
    private CalendarFormatter mFormatter ;
    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    /**
     * 
     * @param exception message
     */
    private void throwIllegalException(String message){
            throw new IllegalStateException(message);
    }
    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, 0);
        initCalendar(context,attrs,defStyle);
        mCalendarRunning = Calendar.getInstance();
        setCalendarView();
    }
    private static int getWeekIndexFromCalendar(int dayOfWeeks){
        int index = 0;
        switch (dayOfWeeks) {
            case Calendar.SUNDAY:
                index = 6 ;
                break;
            case Calendar.MONDAY:
                index = 0 ;
                break;
            case Calendar.TUESDAY:
                index = 1 ;
                break;
            case Calendar.WEDNESDAY:
                index = 2 ;
                break;
            case Calendar.THURSDAY:
                index = 3 ;
                break;
            case Calendar.FRIDAY:
                index = 4 ;
                break;
            case Calendar.SATURDAY:
                index = 5 ;
                break;
            default:
                break;
        }
        return index ;
    }
    
    private void setCalendarView(){
        setCalendarView((Calendar)mCalendarRunning.clone());
    }
    /**
     * sets calendar data according to calendar instance. 
     * <br/>current month = month from calendarInstance 
     * @param calendarInstance
     */
    
    private void setCalendarView(Calendar calendar) {
        setMonthView(calendar);
        offsetCalendarToFirst(calendar);
        setDaysView(calendar);
    }
    /**
     * 
     */
    private void setWeeksView() {
        for (int weekColumn = 0; weekColumn < mWeeksHolder.length; weekColumn++) {
            setWeekView(weekColumn);
        }

    }

    private void setWeekView(int weekColumn) {
        TextView weekView = (TextView)mWeeksHolder[weekColumn];
        weekView.setText(mWeekDaysStrings[weekColumn]);
        
    }

    private void setMonthView(Calendar calendar) {
        mMonthText.setText(mFormatter.getMonth(calendar));
        
    }

    private void setDaysView(Calendar calendar) {
        for (int weekCount = 0; weekCount < mDaysHolder.length; weekCount++) {
            for (int weekDaysCount = 0; weekDaysCount < mDaysHolder[0].length; weekDaysCount++) {
                setDayView(weekCount,weekDaysCount,calendar);
                calendar.add(Calendar.DAY_OF_YEAR,1);
            }
        }
        
    }

    private void setDayView(int weekCount, int weekDaysCount, Calendar calendar) {
       TextView dayView = (TextView) mDaysHolder[weekCount][weekDaysCount];
       dayView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
       CalendarViewInfo info = getCalendarInfo(dayView);
       info.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
       dayView.setTag(CalendarViewInfo.INFO_KEY, info);
    }

    private CalendarViewInfo getCalendarInfo(TextView dayView) {
       CalendarViewInfo info = null ;
        if(dayView!=null){
            info = (dayView.getTag(CalendarViewInfo.INFO_KEY) != null)  ? (CalendarViewInfo)dayView.getTag(CalendarViewInfo.INFO_KEY)  : new CalendarViewInfo();
        }else{
            throwIllegalException("day view should not be null");
        }
        return info;
    }

    /**
     * 
     * @param calendar
     */
    private void offsetCalendarToFirst(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH,1); // first day of this month
        int dayOfWeek = getWeekIndexFromCalendar(calendar.get(Calendar.DAY_OF_WEEK)); 
        int offSet = -dayOfWeek; // to start calendar from first column of first row
        calendar.add(Calendar.DAY_OF_YEAR, offSet);
        
    }

    /**
     * 
     * @param context
     * @param attrs
     * @param defStyle
     */
    private void initCalendar(Context context, AttributeSet attrs, int defStyle) {
            this.mContext = context;
            mFormatter = new CalendarFormatter();
            mWeekDaysStrings = mFormatter.getWeekDays();
            initChildViews();
    }

    /**
     * 
     */
    private void initChildViews() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        mRootView = layoutInflater.inflate(R.layout.calendar_view, this, true);
        if (mRootView != null) {
            mMonthsHeader = mRootView.findViewById(R.id.clMonthHeder);
            mWeeksHeader = (ViewGroup) mRootView.findViewById(R.id.clWeekHeder);
            mWeeksContainer = (ViewGroup) mRootView.findViewById(R.id.clWeeks);
            mHandleLeft = mMonthsHeader.findViewById(R.id.txtCalLeftHandle);
            mHandleRight = mMonthsHeader.findViewById(R.id.txtCalRightHandle);
            mHandleLeft.setOnClickListener(CalendarView.this);
            mHandleRight.setOnClickListener(CalendarView.this);
            mMonthText = (TextView) mMonthsHeader.findViewById(R.id.txtCalMonth);
            initWeeks();
            initDays();
            setWeeksView();
        } else {
            throwIllegalException("Calendar view inflation error.");
        }
    }
    /**
     * 
     */
    private void initWeeks() {
        int weekCount = mWeeksHeader.getChildCount();
        mWeeksHolder = new View[weekCount];
        for (int columnCount = 0; columnCount < weekCount; columnCount++) {
            mWeeksHolder[columnCount] = mWeeksHeader.getChildAt(columnCount);
        }
        
    }

    /**
     * 
     */
    private void initDays() {
       if(mWeeksContainer != null ){
           int rows = mWeeksContainer.getChildCount() ;
           int columns = ((ViewGroup)mWeeksContainer.getChildAt(0)).getChildCount();
           mDaysHolder = new View[rows][columns];
           for (int rowCount = 0; rowCount < rows; rowCount++) {
               ViewGroup mRowParent = (ViewGroup)mWeeksContainer.getChildAt(rowCount);
               for (int columnCount = 0; columnCount < columns; columnCount++) {
                mDaysHolder[rowCount][columnCount] = mRowParent.getChildAt(columnCount);
                mDaysHolder[rowCount][columnCount].setOnClickListener(CalendarView.this);
            }
            
        }
       }else{
           throwIllegalException("weeks containter not initialized. ");
       }
        
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtCalLeftHandle: {
                onClickCalendarLeftHandle(v);
            }
                break;
            case R.id.txtCalRightHandle: {
                onClickCalendarRightHandle(v);
            }
            case R.id.txtCalDay: {
                onClickCalendarDay(v);
            }
                break;
            default:
                break;
        }
        
    }
    /**
     *
     */
   private void onClickCalendarLeftHandle(View leftHandle){
        prevMonth();
    }
   /**
    * 
    */
   private void onClickCalendarRightHandle(View rightHandle){
        nextMonth();
    }
   private void onClickCalendarDay(View calendarDay){
       
   }
   
   private void setMonth(int month){
       mCalendarRunning.set(Calendar.MONTH, month);
       setCalendarView();
   }
   private void nextMonth(){
       setMonth(mCalendarRunning.get(Calendar.MONTH) + 1);
   }
   private void prevMonth(){
       setMonth(mCalendarRunning.get(Calendar.MONTH) - 1);
   }
}
