package unisinos.mapapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luiz on 26/05/15.
 */
public class Schedule {

    public static final int MERCADO = 0;
    public static final int NOVO_HAMBURGO = 1;
    public static final int BUS = 2;

    private static Map<Integer, Schedule> schedules;

    public static Schedule get(int identifier) {
        if (schedules == null) {
            schedules = new HashMap<>();
        }

        if (
            schedules.get(identifier) == null &&
            (identifier == MERCADO || identifier == NOVO_HAMBURGO || identifier == BUS)
        ) {
            schedules.put(identifier, new Schedule());
        }
        return schedules.get(identifier);
    }

    private Map<String, ArrayList<MyTime>> schedule;

    public Schedule(){
        schedule = new HashMap<>();
    }

    public void add(MyTime time, String when) {
        ArrayList<MyTime> a = schedule.get(when);
        if (a == null) {
            a = new ArrayList<>();
            schedule.put(when, a);
        }
        a.add(time);
    }

    public MyTime getNext(int distanceTime) {
        MyTime currentTime = MyTime.currentTime();
        return getNext(currentTime, distanceTime);

    }

    public MyTime getNext(MyTime time, int distanceTime) {
        MyTime result = null;
        ArrayList<MyTime> times = getCurrentTimes();

        if (times != null) {
            int size = times.size();
            for (int i=0; i < size; i++) {
                MyTime aux = times.get(i).clone();
                aux.add(distanceTime);
                if (aux.diff(time) >= 0) {
                    result = aux;
                    break;
                }
            }
        }

        return result;
    }

    public MyTime getNext(MyTime time) {
        return getNext(time, 0);
    }

    public ArrayList<MyTime> getCurrentTimes() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        ArrayList<MyTime> times;

        if (dayOfWeek == 0) {
            times = schedule.get("sunday");
        } else if (dayOfWeek == 6) {
            times = schedule.get("saturday");
        } else {
            times = schedule.get("week");
        }
        return times;
    }
}
