package applications;

import dataStructures.LinkedQueue;

class Machine {
    // data members
    private LinkedQueue jobQ; // queue of waiting jobs for this machine
    private int changeTime; // machine change-over time
    private int totalWait; // total delay at this machine
    private int numTasks; // number of tasks processed on this machine
    private Job activeJob; // job currently active on this machine

    // constructor
    Machine() {
        jobQ = new LinkedQueue();
    }

    public LinkedQueue getJobQ() {
        return jobQ;
    }
    
    public boolean jobQisEmpty(){
        return jobQ.isEmpty();
    }

    public int getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(int changeTime) {
        this.changeTime = changeTime;
    }

    public int getTotalWait() {
        return totalWait;
    }

    public void setTotalWait(int totalWait) {
        this.totalWait = totalWait;
    }

    public int getNumTasks() {
        return numTasks;
    }

    public void incrementNumTasks() {// setNumTasks was only ever used to increment the num of tasks by one so incre
        this.numTasks++;
    }

    public Job getActiveJob() {
        return activeJob;
    }

    public void setActiveJob(Job activeJob) {
        this.activeJob = activeJob;
    }
}
