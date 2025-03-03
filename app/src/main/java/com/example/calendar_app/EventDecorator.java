package com.example.calendar_app;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.List;

/**
 * 给指定日期画圆点的 Decorator
 */
public class EventDecorator implements DayViewDecorator {

    private int color;
    private List<CalendarDay> dates;

    public EventDecorator(int color, List<CalendarDay> dates) {
        this.color = color;
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // 如果在 dates 列表里，就需要画圆点
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // DotSpan: MaterialCalendarView 内置的小圆点
        view.addSpan(new DotSpan(8, color));
    }

    public List<CalendarDay> getDates() {
        return dates;
    }
}
