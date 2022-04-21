package robosim.ai;

import core.Duple;
import robosim.core.*;
import robosim.reinforcement.QTable;

import java.util.Optional;

public class Vacuum implements Controller {

    private QTable qValues = new QTable(State.values().length, Action.values().length, 0, 0, 200, 0.5);

    @Override
    public void control(Simulator sim) {
        State state = getState(sim);
        if(state.equals(State.DClOSE)){
            Optional<Action> dirt = dirtAction(sim);
            if (dirt.isPresent()) {
                Action da = dirt.get();
                int chosenAction = qValues.senseActLearn(state.getIndex(), reward(sim));
                if (da.equals(Action.values()[chosenAction])) {
                    Action.values()[chosenAction].applyTo(sim);
                }else{
                    //int chosenAction = qValues.senseActLearn(state.getIndex(), reward(sim));
                    Action.values()[da.ordinal()].applyTo(sim);
                }
            }

        }else {
            int chosenAction = qValues.senseActLearn(state.getIndex(), reward(sim));
            Action.values()[chosenAction].applyTo(sim);
        }
    }

    public State getState(Simulator sim){
        if (sim.findClosestProblem() < 30){
            if (sim.findClosestEdge() != sim.findClosestProblem() && sim.allVisibleObjects().get(0).getFirst().isVacuumable()){
            //if (sim.allVisibleObjects().get(0).getFirst().isObstacle() && sim.findClosestEdge() != sim.findClosestProblem()){
                return State.DClOSE;
            }else {
                return State.CLOSE;
            }
        }else{
            return State.FAR;
        }
    }


    public double reward(Simulator sim){
        if (sim.wasHit()){
            return -10.0;
        }else if (Action.values()[qValues.getLastAction()].equals(Action.FORWARD)){
            return 5.0;
        //dissuade spinning and oscillation
        }else if (Action.values()[qValues.getLastAction()].equals(Action.BACKWARD)) {
            return -1.0;
        }else if (Action.values()[qValues.getLastAction()].equals(Action.LEFT) || Action.values()[qValues.getLastAction()].equals(Action.RIGHT)) {
            return -1.0;

            //Dirt chase maybe???
        }else if (Action.values()[qValues.getLastAction()].equals(Action.values()[dirtAction(sim).get().ordinal()])) {
            return sim.getDirt() + 5.0;

            //other variants
            //else if (sim.allVisibleObjects().get(0).getFirst().isVacuumable() && sim.findClosestObstacle().equals(sim.allVisibleObjects().get(0).getSecond())){
            //return 5.0;
            //else if(sim.getDirt() < 20){
            //    return sim.getDirt();
            //else if (sim.getDirt() < numDirt(sim)){
            //return -3.0;
        }else{
            return 0.0;
        }
    }
    public int numDirt(Simulator sim){
        int numD = 0;
        for (Duple<SimObject, Polar> thing: sim.allVisibleObjects()){
            if(thing.getFirst().isVacuumable()){
                numD ++;
            }
        }
        return numD;
    }
    public Optional<Action> dirtAction(Simulator sim) {
        int leftDirt = 0;
        int rightDirt = 0;
        int straightDirt = 0;
        for (Duple<SimObject, Polar> obj: sim.allVisibleObjects()) {
            if (obj.getFirst().isVacuumable()) {
                if (Math.abs(obj.getSecond().getTheta()) < Robot.ANGULAR_VELOCITY) {
                    straightDirt += 1;
                } else if (obj.getSecond().getTheta() < 0) {
                    leftDirt += 1;
                } else {
                    rightDirt += 1;
                }
            }
        }
        if (straightDirt > 0) {
            return Optional.of(Action.FORWARD);
        } else if (leftDirt > 0) {
            return Optional.of(Action.LEFT);
        } else if (rightDirt > 0) {
            return Optional.of(Action.RIGHT);
        } else {
            return Optional.of(Action.BACKWARD);
        }
    }
}
