package robosim.core;

import core.Duple;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

public class SimMap {
    public static final double SHADOW_CONSTANT = Math.PI / 12;

    private LinkedHashSet<SimObject> objects = new LinkedHashSet<>();

    public SimMap() {}

    public SimMap(String src) {
        String[] objs = src.split(":");
        for (int i = 1; i < objs.length; i++) {
            objects.add(SimObject.from(objs[i]));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SimMap");
        for (SimObject so: objects) {
            sb.append(':');
            sb.append(so.toString());
        }
        return sb.toString();
    }

    public void add(SimObject obj) {
        objects.add(obj);
    }

    public void remove(SimObject obj) {
        objects.remove(obj);
    }

    public void drawOn(Graphics gc, Robot bot) {
        for (SimObject obj: objects) {
            obj.render(gc, obj.getColor());
        }

        for (Duple<SimObject, Polar> visible: visibleObjects(bot)) {
            visible.getFirst().render(gc, Color.YELLOW);
        }
    }

    public boolean isColliding(Robot bot) {
        return objects.stream().anyMatch(bot::isHitting);
    }

    public Optional<SimObject> vaccuumed(Robot bot) {
        return objects.stream().filter(bot::canVacuum).findFirst();
    }

    public ArrayList<Duple<SimObject,Polar>> visibleObjects(Robot bot) {
        ArrayList<Duple<SimObject,Polar>> visible = objects.stream()
                .filter(bot::withinSonar)
                .map(obj -> new Duple<>(obj, bot.offsetTo(obj)))
                .sorted(Comparator.comparingDouble(d -> d.getSecond().getR()))
                .collect(Collectors.toCollection(ArrayList::new));
        HashSet<Integer> blocked = new HashSet<>();
        for (int i = 1; i < visible.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (visible.get(j).getFirst().isObstacle()) {
                    if (Polar.angularDifference(visible.get(j).getSecond().getTheta(), visible.get(i).getSecond().getTheta()) < SHADOW_CONSTANT) {
                        blocked.add(i);
                    }
                }
            }
        }

        ArrayList<Duple<SimObject,Polar>> result = new ArrayList<>();
        for (int i = 0; i < visible.size(); i++) {
            if (!blocked.contains(i)) {
                result.add(visible.get(i));
            }
        }
        return result;
    }
}
