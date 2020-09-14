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
    public Machine[] machine; // array of machines
    int largeTime; // all machines finish before this

    /**
     * change the state of theMachine
     * 
     * @return last job run on this machine
     */
    Job changeState(int theMachine) {// Task on theMachine has finished,
                                            // schedule next one.
        Job lastJob;
        if (machine[theMachine].getActiveJob() == null) {// in idle or change-over
                                                    // state
            lastJob = null;
            // wait over, ready for new job
            if (machine[theMachine].getJobQ().isEmpty()) // no waiting job
                eList.setFinishTime(theMachine, largeTime);
            else {// take job off the queue and work on it
                machine[theMachine].setActiveJob((Job) machine[theMachine].getJobQ()
                        .remove());
                machine[theMachine].setTotalWait(machine[theMachine].getTotalWait() + timeNow
                        - machine[theMachine].getActiveJob().getArrivalTime());
                machine[theMachine].setNumTasks(machine[theMachine].getNumTasks() + 1);
                int t = machine[theMachine].getActiveJob().removeNextTask();
                eList.setFinishTime(theMachine, timeNow + t);
            }
        } else {// task has just finished on machine[theMachine]
                // schedule change-over time
            lastJob = machine[theMachine].getActiveJob();
            machine[theMachine].setActiveJob(null);
            eList.setFinishTime(theMachine, timeNow
                    + machine[theMachine].getChangeTime());
        }

        return lastJob;
    }

    private void createEventAndMachineQueues(SimulationSpecification specification) {
        // create event and machine queues
        eList = new EventList(specification.getNumMachines(), largeTime);
        machine = new Machine[specification.getNumMachines() + 1];
        for (int i = 1; i <= specification.getNumMachines(); i++)
            machine[i] = new Machine();
    }

    /** load first jobs onto each machine
     * @param specification*/
    void startShop(SimulationSpecification specification) {
        // Move this to startShop when ready
        numMachines = specification.getNumMachines();
        numJobs = specification.getNumJobs();
        createEventAndMachineQueues(specification);

        // Move this to startShop when ready
        specification.setMachineChangeOverTimes(this);

        // Move this to startShop when ready
        specification.setUpJobs(this);

        for (int p = 1; p <= numMachines; p++)
            changeState(p);
    }

    /** process all jobs to completion
     * @param simulationResults*/
    void simulate(SimulationResults simulationResults) {
        while (numJobs > 0) {// at least one job left
            int nextToFinish = eList.nextEventMachine();
            timeNow = eList.nextEventTime(nextToFinish);
            // change job on machine nextToFinish
            Job theJob = changeState(nextToFinish);
            // move theJob to its next machine
            // decrement numJobs if theJob has finished
            if (theJob != null && !theJob.moveToNextMachine(this, simulationResults))
                numJobs--;
        }
    }

    /** output wait times at machines
     * @param simulationResults*/
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
        simulate(simulationResults); // run all jobs through shop
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
