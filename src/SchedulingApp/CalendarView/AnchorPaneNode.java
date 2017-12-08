package SchedulingApp.CalendarView;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;

public class AnchorPaneNode extends AnchorPane {

    // Date for individual calendar pane
    private LocalDate date;

    // Creates individual date panes for days in calendar
    public AnchorPaneNode (Node... children) {
        super(children);
    }

    // Setter for date
    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Getter for date
    public LocalDate getDate() {
        return date;
    }
}
