package com.gdx.game.screen.transition;

public class TimeTransition {

    private float transitionTime;
    private float currentTime;

    private boolean finished = true;
    private boolean started = false;

    public boolean isFinished() {
        return finished;
    }

    /**
     * Returns the percentage of the current time over the total time.
     */
    public float get() {
        if(transitionTime == 0) {
            return 1f;
        }
        return (currentTime / transitionTime);
    }

    public void start(float time) {
        this.transitionTime = time;
        this.currentTime = 0;
        this.finished = false;
        this.started = true;
    }

    public void stop() {
        this.started = false;
        this.finished = false;
    }

    public void update(float time) {
        if(!started) {
            return;
        }
        if(finished) {
            return;
        }
        this.currentTime += time;
        if(currentTime >= transitionTime) {
            currentTime = transitionTime;
            finished = true;
        }

    }
}
