package tfar.gravity.ducks;

public interface PlayerDuck {
    void reverseGravity();
    boolean getGravity();
    void setGravity(boolean b);
    int getTimer();
    void setTimer(int i);
}
