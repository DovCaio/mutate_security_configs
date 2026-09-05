package com.caio.args.flags;

public class FlagsConfig {
    private boolean isVerbose;
    private Integer timeOut;
    private Integer workersQuantity;

    public FlagsConfig() {
        this.isVerbose = false;
        this.timeOut = 3;
        this.workersQuantity = null;
    }

    public boolean isVerbose() {
        return isVerbose;
    }

    public Integer timeOut() {
        return timeOut;
    }

    public boolean workersDefined() {
        return workersQuantity != null;
    }

    public Integer getWorkersQuantity() {
        if (workersQuantity == null) {
            throw new IllegalStateException("A quantidade de workers não foi definida.");
        }
        return workersQuantity;
    }

    public void setVerbose(boolean isVerbose) {
        this.isVerbose = isVerbose;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public void setWorkersQuantity(Integer workersQuantity) {
        this.workersQuantity = workersQuantity;
    }
}
