package com.example.calendar_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private Button btnAddTask;
    private ListView lvTasks;

    // 存储：某个日期 (yyyy-MM-dd) -> 这一天的任务列表
    private Map<String, List<String>> tasksMap = new HashMap<>();

    // 当前选中的日期
    private Calendar selectedDate;

    // 用于显示当前选中日期的任务列表
    private ArrayList<String> currentDayTasks = new ArrayList<>();
    private TasksAdapter tasksAdapter;

    // 用来给有任务的日期加小圆点
    private EventDecorator eventDecorator;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.materialCalendarView);
        btnAddTask   = findViewById(R.id.btnAddTask);
        lvTasks      = findViewById(R.id.lvTasks);

        // 1. 初始化 ListView 的适配器
        tasksAdapter = new TasksAdapter(this, currentDayTasks);
        lvTasks.setAdapter(tasksAdapter);

        // 2. 让日历选中 "今天"
        selectedDate = Calendar.getInstance();
        calendarView.setSelectedDate(CalendarDay.today());

        // 3. 创建一个初始的装饰器（颜色 + 空的日期列表）
        eventDecorator = new EventDecorator(0xFFFF0000, new ArrayList<>());
        calendarView.addDecorator(eventDecorator);

        // 4. 监听日历日期被点击/选择
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = calendarFromCalendarDay(date);
            updateTaskListForDay(selectedDate);
        });

        // 5. "Add Task" 按钮点击
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    /**
     * 弹出对话框，输入任务内容
     */
    private void showAddTaskDialog() {
        EditText editText = new EditText(this);
        editText.setHint("请输入任务内容");
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("Add Task")
                .setView(editText)
                .setPositiveButton("确定", (dialog, which) -> {
                    String taskContent = editText.getText().toString().trim();
                    if (!taskContent.isEmpty()) {
                        addTaskToSelectedDate(taskContent);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 将任务添加到 selectedDate 并刷新界面
     */
    private void addTaskToSelectedDate(String taskContent) {
        // 1. 存到 tasksMap
        String dateKey = dateFormat.format(selectedDate.getTime());
        List<String> dayTasks = tasksMap.get(dateKey);
        if (dayTasks == null) {
            dayTasks = new ArrayList<>();
            tasksMap.put(dateKey, dayTasks);
        }
        dayTasks.add(taskContent);

        // 2. 刷新当前列表
        updateTaskListForDay(selectedDate);

        // 3. 给选中的日期加上小圆点
        addDotForDate(selectedDate);
    }

    /**
     * 更新下方 ListView 显示
     */
    private void updateTaskListForDay(Calendar date) {
        currentDayTasks.clear();
        String dateKey = dateFormat.format(date.getTime());
        List<String> tasks = tasksMap.get(dateKey);
        if (tasks != null) {
            currentDayTasks.addAll(tasks);
        }
        tasksAdapter.notifyDataSetChanged();
    }

    /**
     * 在日历上为指定日期添加一个小圆点
     */
    private void addDotForDate(Calendar date) {
        CalendarDay day = calendarDayFromCalendar(date);

        // 获取当前已有的日期
        List<CalendarDay> decoratedDays = eventDecorator.getDates();
        if (!decoratedDays.contains(day)) {
            decoratedDays.add(day);
        }

        // 重新设置一次 decorator
        calendarView.removeDecorator(eventDecorator);
        eventDecorator = new EventDecorator(0xFFFF0000, decoratedDays);
        calendarView.addDecorator(eventDecorator);
    }

    /**
     * CalendarDay -> Calendar
     */
    private Calendar calendarFromCalendarDay(CalendarDay day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(day.getYear(), day.getMonth() - 1, day.getDay()); // Calendar的月份从0开始
        return calendar;
    }

    /**
     * Calendar -> CalendarDay
     */
    private CalendarDay calendarDayFromCalendar(Calendar calendar) {
        return CalendarDay.from(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }
}
