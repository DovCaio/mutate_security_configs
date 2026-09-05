package com.caio.args.flags;

public class FlagsConfig {
    private boolean isVerbose;
    private Integer timeOut;

    public FlagsConfig() {
        this.isVerbose = false;
        this.timeOut = 3;
    }

    public boolean isVerbose() {
        return isVerbose;
    }

    public Integer timeOut() {
        return timeOut;
    }

    public void setVerbose(boolean isVerbose) {
        this.isVerbose = isVerbose;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }
}
