/** machine shop simulation */

package applications;

public class MachineShopSimulator {
    
    public static final String NUMBER_OF_MACHINES_MUST_BE_AT_LEAST_1 = "number of machines must be >= 1";
    public static final String NUMBER_OF_MACHINES_AND_JOBS_MUST_BE_AT_LEAST_1 = "number of machines and jobs must be >= 1";
    public static final String CHANGE_OVER_TIME_MUST_BE_AT_LEAST_0 = "change-over time must be >= 0";
    public static final String EACH_JOB_MUST_HAVE_AT_LEAST_1_TASK = "each job must have >= 1 task";
    public static final String BAD_MACHINE_NUMBER_OR_TASK_TIME = "bad machine number or task time";

    // data members of MachineShopSimulator
    private int timeNow; // current time
    private int numMachines; // number of machines
    private int numJobs; // number of jobs
    private EventList eList; // pointer to event list
    private Machine[] machine; // array of machines
    private int largeTime; // all machines finish before this
    
    /** entry point for machine shop simulator */
    public static void main(String[] args) {
        /*
         * It's vital that we (re)set this to 0 because if the simulator is called
         * multiple times (as happens in the acceptance tests), because timeNow
         * is static it ends up carrying over from the last time it was run. I'm
         * not convinced this is the best place for this to happen, though.
         */
        final SpecificationReader specificationReader = new SpecificationReader();
        SimulationSpecification specification = specificationReader.readSpecification();
        MachineShopSimulator simulator = new MachineShopSimulator();
        SimulationResults simulationResults = simulator.runSimulation(specification);
        simulationResults.print();
    }

	void simulate(SimulationResults simulationResults) {
	    while (numJobs > 0) {// at least one job left
	        int nextToFinish = eList.nextEventMachine();
	        timeNow = eList.nextEventTime(nextToFinish);
			// schedule next one.
            Job lastJob;
            Machine nextMachine = machine[nextToFinish];
			if (nextMachine.getActiveJob() == null) {// in idle or change-over
			                                            // state
			    lastJob = null;
			    // wait over, ready for new job
			    if (nextMachine.jobQisEmpty()) // no waiting job
			        eList.setFinishTime(nextToFinish, largeTime);
			    else {// take job off the queue and work on it
			        nextMachine.setActiveJob((Job) nextMachine.getJobQ()
			                .remove());
			        nextMachine.setTotalWait(nextMachine.getTotalWait() + timeNow
			                - nextMachine.getActiveJob().getArrivalTime());
			        nextMachine.setNumTasks(nextMachine.getNumTasks() + 1);
			        int t = nextMachine.getActiveJob().removeNextTask();
			        eList.setFinishTime(nextToFinish, timeNow + t);
			    }
			} else {// task has just finished on machine[theMachine]
			        // schedule change-over time
			    lastJob = nextMachine.getActiveJob();
			    nextMachine.setActiveJob(null);
			    eList.setFinishTime(nextToFinish, timeNow
			            + nextMachine.getChangeTime());
			}
	        // change job on machine nextToFinish
	        Job theJob = lastJob;
	        // move theJob to its next machine
	        // decrement numJobs if theJob has finished
	        if (theJob != null && !theJob.moveToNextMachine(this, simulationResults))
	            numJobs--;
	    }
    }
    public  SimulationResults runSimulation(SimulationSpecification simulationSpecification) {
	    largeTime = Integer.MAX_VALUE;
	    timeNow = 0;
	    simulationSpecification.startShop(this); // initial machine loading
	    SimulationResults simulationResults = new SimulationResults(getNumJobs());
	    simulate(simulationResults); // run all jobs through shop
	    simulationResults.outputStatistics(this);
	    return simulationResults;
	}
    // getters and setters
    public int getTimeNow(){
        return timeNow;
    }
  
    public int getNumMachines() {
        return numMachines;
    }

    public void setNumMachines(int numMachines) {
        this.numMachines = numMachines;
    }

    public int getNumJobs() {
        return numJobs;
    }

    public void setNumJobs(int numJobs) {
        this.numJobs = numJobs;
    }

    public EventList geteList() {
        return eList;
    }

    public void seteList(EventList eList) {
        this.eList = eList;
    }

    public Machine[] getMachine() {
        return machine;
    }

    public Machine machineAt(int i){
        return machine[i];
    }
    public void setMachineAt(int i,Machine newMachine){
        machine[i] = newMachine;
    }
    public void setMachine(Machine[] machine) {
        this.machine = machine;
    }

    public int getLargeTime() {
        return largeTime;
    }

}
