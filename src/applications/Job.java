package applications;

import dataStructures.LinkedQueue;

public class Job {
    // data members
    private LinkedQueue taskQ; // this job's tasks
    int length; // sum of scheduled task times
    int arrivalTime; // arrival time at current queue
    int id; // job identifier

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


	public int getMachineNumber(){
        return  ((Task) getTaskQ().getFrontElement()).getMachine();
    }



}
