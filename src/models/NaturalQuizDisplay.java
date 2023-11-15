package models;

import org.dreambot.api.methods.map.Area;

import java.util.List;

public class NaturalQuizDisplay {
    private String name;
    private boolean isDone;
    private Area area;
    private List<String> options;

    public NaturalQuizDisplay(String name, Area area, List<String> options) {
        this.name = name;
        this.area = area;
        this.options = options;
        this.isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    public Area getArea() {
        return area;
    }

    public String getName() {
        return name;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }
}
