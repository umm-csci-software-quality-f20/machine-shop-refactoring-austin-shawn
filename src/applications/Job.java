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
	        simulationResults.setJobCompletionData(getId(), machineShopSimulator.getTimeNow(), machineShopSimulator.getTimeNow() - getLength());
	        return false;
	    } else {// theJob has a next task
	            // get machine for next task
	        int index = ((Task) getTaskQ().getFrontElement()).getMachine();
	        // put on machine p's wait queue
	        machineShopSimulator.machine[index].getJobQ().put(this);
	        setArrivalTime(machineShopSimulator.getTimeNow());
	        // if p idle, schedule immediately
	        changeState(machineShopSimulator, index);
	        return true;
	    }
	}

	private void changeState(MachineShopSimulator machineShopSimulator, int index) {
		if (machineShopSimulator.geteList().nextEventTime(index) == machineShopSimulator.largeTime) {// machine is idle
		    // schedule next one.
			Job lastJob;
			if (machineShopSimulator.machine[index].getActiveJob() == null) {// in idle or change-over
			                                            // state
			    lastJob = null;
			    // wait over, ready for new job
			    if (machineShopSimulator.machine[index].jobQisEmpty()) // no waiting job
			        machineShopSimulator.geteList().setFinishTime(index, machineShopSimulator.largeTime);
			    else {// take job off the queue and work on it
			        machineShopSimulator.machine[index].setActiveJob((Job) machineShopSimulator.machine[index].getJobQ()
			                .remove());
                    machineShopSimulator.machine[index].setTotalWait(machineShopSimulator.machine[index].getTotalWait()
                     + machineShopSimulator.getTimeNow() - machineShopSimulator.machine[index].getActiveJob().getArrivalTime());
			        machineShopSimulator.machine[index].setNumTasks(machineShopSimulator.machine[index].getNumTasks() + 1);
			        int t = machineShopSimulator.machine[index].getActiveJob().removeNextTask();
			        machineShopSimulator.geteList().setFinishTime(index, machineShopSimulator.getTimeNow() + t);
			    }
			} else {// task has just finished on machine[theMachine]
			        // schedule change-over time
			    lastJob = machineShopSimulator.machine[index].getActiveJob();
			    machineShopSimulator.machine[index].setActiveJob(null);
			    machineShopSimulator.geteList().setFinishTime(index, machineShopSimulator.getTimeNow()
			            + machineShopSimulator.machine[index].getChangeTime());
			}
		}
	}

}
