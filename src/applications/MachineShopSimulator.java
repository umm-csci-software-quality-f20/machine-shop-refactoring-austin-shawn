/** machine shop simulation */

package applications;

public class MachineShopSimulator {
    
    public static final String NUMBER_OF_MACHINES_MUST_BE_AT_LEAST_1 = "number of machines must be >= 1";
    public static final String NUMBER_OF_MACHINES_AND_JOBS_MUST_BE_AT_LEAST_1 = "number of machines and jobs must be >= 1";
    public static final String CHANGE_OVER_TIME_MUST_BE_AT_LEAST_0 = "change-over time must be >= 0";
    public static final String EACH_JOB_MUST_HAVE_AT_LEAST_1_TASK = "each job must have >= 1 task";
    public static final String BAD_MACHINE_NUMBER_OR_TASK_TIME = "bad machine number or task time";

    // data members of MachineShopSimulator
    int timeNow; // current time
    private int numMachines; // number of machines
    private int numJobs; // number of jobs
    EventList eList; // pointer to event list
	private Machine[] machine; // array of machines
	public final int largeTime = Integer.MAX_VALUE;

    
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
			        nextMachine.incrementNumTasks();
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
	        if (theJob != null && !moveToNextMachine(theJob, simulationResults))
	            numJobs--;
	    }
    }

    /**
	 * move theJob to machine for its next task
	 * 
	 * @param job TODO
	 * @param simulationResults TODO
	 * @return false iff no next task
	 */
	boolean moveToNextMachine(Job job, SimulationResults simulationResults) {
	    if (job.getTaskQ().isEmpty()) {// no next task
	        simulationResults.setJobCompletionData(job, timeNow);
	        return false;
	    } else {// theJob has a next task
	            // get machine for next task
	        int index = job.getMachineNumber();
	        Machine machineSpec = machineAt(index);
	        machineSpec.getJobQ().put(job);        
	        job.setArrivalTime(timeNow);
	
	        if (eList.nextEventTime(index) == largeTime) {// machine is idle schedule next job
				int finishTime = machineSpec.createFinishTime( timeNow, largeTime);
				eList.setFinishTime(index, finishTime);
	        }
	        return true;
	    }
    }

	public  SimulationResults runSimulation(SimulationSpecification simulationSpecification) {
	    timeNow = 0;
	    startShop(simulationSpecification); // initial machine loading
	    SimulationResults simulationResults = new SimulationResults(numJobs);
	    simulate(simulationResults); // run all jobs through shop
	    simulationResults.outputStatistics(this,timeNow, numMachines);
	    return simulationResults;
	}
	public void createEventAndMachineQueues(int numOfMachines) {
	    // create event and machine queues
	    eList = new EventList(numOfMachines, largeTime);
	    this.machine = new Machine[numOfMachines+1];
		for (int i = 1; i <= numOfMachines; i++)
			this.machine[i] = new Machine();
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

    public void setNumJobs(int numJobs) {
        this.numJobs = numJobs;
    }

    public EventList geteList() {
        return eList;
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

	/** load first jobs onto each machine
	 * @param simSpecs TODO
	 * */
	void startShop(SimulationSpecification simSpecs) {
		// Move this to startShop when ready
		numMachines = simSpecs.getNumMachines();
		numJobs = simSpecs.getNumJobs();
	    createEventAndMachineQueues(numMachines);
	
	    // Move this to startShop when ready
	    simSpecs.setMachineChangeOverTimes(this);
	
	    // Move this to startShop when ready
	    simSpecs.setUpJobs(getMachine());
	// activate all jobs
		for (int index = 1; index <= numMachines; index++) {
			// schedule next one.
			Machine machine = machineAt(index);
			int finishTime = machine.createFinishTime(timeNow, largeTime );
			geteList().setFinishTime(index, finishTime);
		}	}

}
