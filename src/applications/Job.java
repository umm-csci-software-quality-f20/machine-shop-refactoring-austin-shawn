package applications;

import dataStructures.LinkedQueue;

class Job {
    // data members
    private LinkedQueue taskQ; // this job's tasks
    private int length; // sum of scheduled task times
    private int arrivalTime; // arrival time at current queue
    private int id; // job identifier

    // constructor
    Job(int theId) {
        id = theId;
        taskQ = new LinkedQueue();
        // length and arrivalTime have default value 0
    }

    // other methods
    public void addTask(int theMachine, int theTime) {
        getTaskQ().put(new Task(theMachine, theTime));
    }

    /**
     * remove next task of job and return its time also update length
     */
    public int removeNextTask() {
        int theTime = ((Task) getTaskQ().remove()).getTime();
        length = getLength() + theTime;
        return theTime;
    }

    public LinkedQueue getTaskQ() {
        return taskQ;
    }

    public int getLength() {
        return length;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getId() {
        return id;
    }

	/**
	 * move theJob to machine for its next task
	 * 
	 * @param machineShopSimulator TODO
	 * @param simulationResults TODO
	 * @return false iff no next task
	 */
	boolean moveToNextMachine(MachineShopSimulator machineShopSimulator, SimulationResults simulationResults) {
	    if (getTaskQ().isEmpty()) {// no next task
	        simulationResults.setJobCompletionData(getId(), machineShopSimulator.timeNow, machineShopSimulator.timeNow - getLength());
	        return false;
	    } else {// theJob has a next task
	            // get machine for next task
	        int p = ((Task) getTaskQ().getFrontElement()).getMachine();
	        // put on machine p's wait queue
	        machineShopSimulator.machine[p].getJobQ().put(this);
	        setArrivalTime(machineShopSimulator.timeNow);
	        // if p idle, schedule immediately
	        if (machineShopSimulator.eList.nextEventTime(p) == machineShopSimulator.largeTime) {// machine is idle
	            // schedule next one.
				Job lastJob;
				if (machineShopSimulator.machine[p].getActiveJob() == null) {// in idle or change-over
				                                            // state
				    lastJob = null;
				    // wait over, ready for new job
				    if (machineShopSimulator.machine[p].getJobQ().isEmpty()) // no waiting job
				        machineShopSimulator.eList.setFinishTime(p, machineShopSimulator.largeTime);
				    else {// take job off the queue and work on it
				        machineShopSimulator.machine[p].setActiveJob((Job) machineShopSimulator.machine[p].getJobQ()
				                .remove());
				        machineShopSimulator.machine[p].setTotalWait(machineShopSimulator.machine[p].getTotalWait() + machineShopSimulator.timeNow
				                - machineShopSimulator.machine[p].getActiveJob().getArrivalTime());
				        machineShopSimulator.machine[p].setNumTasks(machineShopSimulator.machine[p].getNumTasks() + 1);
				        int t = machineShopSimulator.machine[p].getActiveJob().removeNextTask();
				        machineShopSimulator.eList.setFinishTime(p, machineShopSimulator.timeNow + t);
				    }
				} else {// task has just finished on machine[theMachine]
				        // schedule change-over time
				    lastJob = machineShopSimulator.machine[p].getActiveJob();
				    machineShopSimulator.machine[p].setActiveJob(null);
				    machineShopSimulator.eList.setFinishTime(p, machineShopSimulator.timeNow
				            + machineShopSimulator.machine[p].getChangeTime());
				}
	        }
	        return true;
	    }
	}

}
