package TOOLS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TimeMetrics {

    private HashMap<String, Long> timeMetrics = new HashMap<>();
    private HashMap<String, Long> timeMetricsTemp = new HashMap<>();

    public void TimeMetrics() {}

    public void startTimer(String timerName) {
        timeMetricsTemp.put(timerName, System.currentTimeMillis());
    }

    public void stopTimer(String timerName) {
        Long endTime = System.currentTimeMillis();
        Long startTime = timeMetricsTemp.getOrDefault(timerName, endTime);

        timeMetrics.put(timerName, timeMetrics.getOrDefault(timerName, 0L) + (endTime - startTime));
        timeMetricsTemp.remove(timerName);
    }

    public Long getTimer(String timerName) {
        return timeMetrics.getOrDefault(timerName, -1L);
    }

    public String listTimers() {
        String rS = "";
        List<String> keys = new ArrayList<String>(timeMetrics.size());
        keys.addAll(timeMetrics.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            String zeitEinheit = "ms";
            Double zeitValue = Double.valueOf(timeMetrics.get(key));
            if (zeitValue > 1000.0) {
                zeitEinheit = "s";
                zeitValue /= 1000.0;
            }
            rS += "" + key + ": " + zeitValue + zeitEinheit + "\n";
        }
        return rS;
    }
}
