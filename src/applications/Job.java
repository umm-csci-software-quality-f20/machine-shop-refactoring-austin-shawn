package applications;

import dataStructures.LinkedQueue;

public class Job {
    // data members
    private LinkedQueue taskQ; // this job's tasks
    private int length; // sum of scheduled task times
    private int arrivalTime; // arrival time at current queue
    private int id; // job identifier

    private  int completionTime;
    private  int totalWaitTime;
    private  int jobNumber;
    // constructor
       public Job(int theId) {
        id = theId;
        taskQ = new LinkedQueue();
        // length and arrivalTime have default value 0
    }

    // Second constructor that we will try to combine with first constructor in the future
       public Job(int jobNumber, int completionTime, int totalWaitTime) {
        this.jobNumber = jobNumber;
        this.completionTime = completionTime;
        this.totalWaitTime = totalWaitTime;
    }

   public int getCompletionTime() {
        return completionTime;
    }

    public int getTotalWaitTime() {
        return totalWaitTime;
    }

    public int getJobNumber() {
        return jobNumber;
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
        length = length + theTime;
        return theTime;
    }

    public LinkedQueue getTaskQ() {
        return taskQ;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }


	/**
	 * move theJob to machine for its next task
	 * 
	 * @param machineShop TODO
	 * @param simulationResults TODO
	 * @return false iff no next task
	 */
	boolean moveToNextMachine(MachineShopSimulator machineShop, SimulationResults simulationResults) {
	    if (getTaskQ().isEmpty()) {// no next task
	        simulationResults.setJobCompletionData(id, machineShop.getTimeNow(), machineShop.getTimeNow() - length);
	        return false;
	    } else {// theJob has a next task
	            // get machine for next task
	        int index = ((Task) getTaskQ().getFrontElement()).getMachine();
            Machine machineSpec = machineShop.machineAt(index);
            machineSpec.getJobQ().put(this);
            EventList eventList = machineShop.geteList();
            arrivalTime = machineShop.getTimeNow();

            if (eventList.nextEventTime(index) == machineShop.getLargeTime()) {// machine is idle schedule next job
                
                Job lastJob;
                Job activeJob = machineSpec.getActiveJob();
                if (machineSpec.getActiveJob() == null) {// in idle or change-over machines
                                                            
                    lastJob = null;
                    // wait over, ready for new job
                    if (machineSpec.jobQisEmpty()) // no waiting job
                        eventList.setFinishTime(index, machineShop.getLargeTime());
                    else {// take job off the queue and work on it
                       // activeJob = (Job) machineSpec.getJobQ().remove();
                        machineSpec.setActiveJob((Job) machineSpec.getJobQ().remove());
                        machineSpec.setNumTasks(machineSpec.getNumTasks() + 1);
                        int timeSpent = arrivalTime + machineSpec.getActiveJob().removeNextTask();
                       eventList.setFinishTime(index, timeSpent);
                    }
                } else {// task has just finished on machine
                        // schedule change-over time
                    lastJob = machineSpec.getActiveJob();
                    machineSpec.setActiveJob(null);
                    eventList.setFinishTime(index, arrivalTime
                            + machineSpec.getChangeTime());
                }
            }
	        return true;
	    }
	}



}
