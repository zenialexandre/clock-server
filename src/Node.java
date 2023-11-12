package src;

import java.time.LocalTime;

public class Node {

    private Integer id;
    private LocalTime actualTime;

    public Node(final Integer id, final LocalTime actualTime) {
        setId(id);
        setActualTime(actualTime);
    }

    private void setId(final Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    private void setActualTime(final LocalTime actualTime) {
        this.actualTime = actualTime;
    }

    public LocalTime getActualTime() {
        return actualTime;
    }

}
