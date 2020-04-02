package TOOLS;

public class ActionStats {

    private Integer count;
    private Long nanos;

    public ActionStats() {};

    public ActionStats(Long Nanos) {
        count = 1;
        nanos = Nanos;
    }

    public void increaseNanos(Long nanosLong) {
        count++;
        nanos += nanosLong;
    }

    public Long getNanos() {
        return nanos;
    }

    public Integer getCount() {
        return count;
    }

    public Double getAvg() {
        return Double.valueOf(nanos) / Double.valueOf(count);
    }
}
