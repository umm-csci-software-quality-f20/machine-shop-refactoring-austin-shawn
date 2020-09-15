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
    public int numJobs; // number of jobs
    EventList eList; // pointer to event list
    public Machine[] machine; // array of machines
    int largeTime; // all machines finish before this

    /** load first jobs onto each machine
     * @param specification
     * */
    void startShop(SimulationSpecification specification) {
        // Move this to startShop when ready
        numMachines = specification.getNumMachines();
        numJobs = specification.getNumJobs();
        specification.createEventAndMachineQueues(this);

        // Move this to startShop when ready
        specification.setMachineChangeOverTimes(this);

        // Move this to startShop when ready
        specification.setUpJobs(this.machine);

        for (int p = 1; p <= numMachines; p++) {
			// schedule next one.
			Job lastJob;
			if (machine[p].getActiveJob() == null) {// in idle or change-over
			                                            // state
			    lastJob = null;
			    // wait over, ready for new job
			    if (machine[p].getJobQ().isEmpty()) // no waiting job
			        eList.setFinishTime(p, largeTime);
			    else {// take job off the queue and work on it
			        machine[p].setActiveJob((Job) machine[p].getJobQ()
			                .remove());
			        machine[p].setTotalWait(machine[p].getTotalWait() + timeNow
			                - machine[p].getActiveJob().getArrivalTime());
			        machine[p].setNumTasks(machine[p].getNumTasks() + 1);
			        int t = machine[p].getActiveJob().removeNextTask();
			        eList.setFinishTime(p, timeNow + t);
			    }
			} else {// task has just finished on machine[theMachine]
			        // schedule change-over time
			    lastJob = machine[p].getActiveJob();
			    machine[p].setActiveJob(null);
			    eList.setFinishTime(p, timeNow
			            + machine[p].getChangeTime());
			}
		}
    }

  
    /** output wait times at machines
     * @param simulationResults
     * */
    void outputStatistics(SimulationResults simulationResults) {
        simulationResults.setFinishTime(timeNow);
        simulationResults.setNumMachines(numMachines);
        setNumTasksPerMachine(simulationResults);
        setTotalWaitTimePerMachine(simulationResults);
    }

    private void setTotalWaitTimePerMachine(SimulationResults simulationResults) {
        int[] totalWaitTimePerMachine = new int[numMachines+1];
        for (int i=1; i<=numMachines; ++i) {
            totalWaitTimePerMachine[i] = machine[i].getTotalWait();
        }
        simulationResults.setTotalWaitTimePerMachine(totalWaitTimePerMachine);
    }

    private void setNumTasksPerMachine(SimulationResults simulationResults) {
        int[] numTasksPerMachine = new int[numMachines+1];
        for (int i=1; i<=numMachines; ++i) {
            numTasksPerMachine[i] = machine[i].getNumTasks();
        }
        simulationResults.setNumTasksPerMachine(numTasksPerMachine);
    }

    public  SimulationResults runSimulation(SimulationSpecification specification) {
        largeTime = Integer.MAX_VALUE;
        timeNow = 0;
        startShop(specification); // initial machine loading
        SimulationResults simulationResults = new SimulationResults(numJobs);
        simulationResults.simulate(this); // run all jobs through shop
        outputStatistics(simulationResults);
        return simulationResults;
    }

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
}
