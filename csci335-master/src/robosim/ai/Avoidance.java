package robosim.ai;

import robosim.core.Action;
import robosim.core.Controller;
import robosim.core.Simulator;
import robosim.reinforcement.QTable;

public class Avoidance implements Controller {

    private QTable qValues = new QTable(State.values().length, Action.values().length, 0, 0, 100, 0.5);
    @Override
    public void control(Simulator sim) {
        State state = getState(sim);
        int chosenAction = qValues.senseActLearn(state.getIndex(), reward(sim));
        Action.values()[chosenAction].applyTo(sim);
    }

    public State getState(Simulator sim){
        if (sim.findClosestProblem() < 30){
            return State.CLOSE;
        }else{
            return State.FAR;
        }
    }

    public double reward(Simulator sim){
        if (sim.wasHit()){
            return -10.0;
        }else if (Action.values()[qValues.getLastAction()].equals(Action.FORWARD)){
            return 5.0;
        //dissuade forward/backward oscillation
        }else if (Action.values()[qValues.getLastAction()].equals(Action.BACKWARD)) {
            return -1.0;
        //dissuade spinning
        }else if (Action.values()[qValues.getLastAction()].equals(Action.LEFT) || Action.values()[qValues.getLastAction()].equals(Action.RIGHT)) {
            return -1.0;
        }else{
            return 0.0;
        }
    }
}
