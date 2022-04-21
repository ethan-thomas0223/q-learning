package robosim.ai;

public enum State {
    CLOSE, FAR, DClOSE, DFAR;

    public int getIndex() {
        for (int i = 0; i < State.values().length; i ++){
            if (State.values()[i].equals(this)){
                return i;
            }
        }
        throw new IllegalStateException("THis should never happen");
    }
}
