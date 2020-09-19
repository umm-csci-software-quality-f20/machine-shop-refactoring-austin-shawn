package applications;

import java.util.Arrays;

public class SimulationSpecification {
    private int numMachines;
    private int numJobs;
    private int[] changeOverTimes;
    private JobSpecification[] jobSpecifications;

    public void setNumMachines(int numMachines) {
        this.numMachines = numMachines;
    }

    public void setNumJobs(int numJobs) {
        this.numJobs = numJobs;
    }

    public int getNumMachines() {
        return numMachines;
    }

    public int getNumJobs() {
        return numJobs;
    }

    public void setChangeOverTimes(int[] changeOverTimes) {
        this.changeOverTimes = changeOverTimes;
    }

    public int getChangeOverTimes(int machineNumber) {
        return changeOverTimes[machineNumber];
    }

    public void setSpecificationsForTasks(int jobNumber, int[] specificationsForTasks) {
        jobSpecifications[jobNumber].setSpecificationsForTasks(specificationsForTasks);
    }

    public void setJobSpecification(JobSpecification[] jobSpecifications) {
        this.jobSpecifications = jobSpecifications;
    }

    public JobSpecification getJobSpecifications(int jobNumber) {
        return jobSpecifications[jobNumber];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<").append(numMachines).append(" machines, ");
        builder.append(numJobs).append(" jobs; ");
        builder.append("change overs: ").append(Arrays.toString(changeOverTimes));
        for (int i=1; i<=numJobs; ++i) {
            builder.append("; job ").append(i).append(" tasks: ");
            builder.append(Arrays.toString(jobSpecifications[i].getSpecificationsForTasks()));
        }

        builder.append(">");
        return builder.toString();
    }

	public void setMachineChangeOverTimes(MachineShopSimulator machineShopSimulator) {
	    for (int i = 1; i<=getNumMachines(); ++i) {
	        machineShopSimulator.machine[i].setChangeTime(getChangeOverTimes(i));
	    }
	}

	void setUpJobs(Machine [] machine) {
	    // input the jobs
	    Job theJob;
	    for (int i = 1; i <= getNumJobs(); i++) {
	        int tasks = getJobSpecifications(i).getNumTasks();
	        int firstMachine = 0; // machine for first task
	
	        // create the job
	        theJob = new Job(i);
	        for (int j = 1; j <= tasks; j++) {
	            int theMachine = getJobSpecifications(i).getSpecificationsForTasks()[2*(j-1)+1];
	            int theTaskTime = getJobSpecifications(i).getSpecificationsForTasks()[2*(j-1)+2];
	            if (j == 1)
	                firstMachine = theMachine; // job's first machine
	            theJob.addTask(theMachine, theTaskTime); // add to
	        } // task queue
	        machine[firstMachine].getJobQ().put(theJob);
	    }
	}

	void createEventAndMachineQueues(MachineShopSimulator machineShopSimulator) {
	    // create event and machine queues
	    machineShopSimulator.eList = new EventList(getNumMachines(), machineShopSimulator.largeTime);
	    machineShopSimulator.machine = new Machine[getNumMachines() + 1];
	    for (int i = 1; i <= getNumMachines(); i++)
	        machineShopSimulator.machine[i] = new Machine();
	}

	/** load first jobs onto each machine
	 * @param machineShopSimulator TODO
	 * */
	void startShop(MachineShopSimulator machineShopSimulator) {
	    // Move this to startShop when ready
	    machineShopSimulator.numMachines = getNumMachines();
	    machineShopSimulator.numJobs = getNumJobs();
	    createEventAndMachineQueues(machineShopSimulator);
	
	    // Move this to startShop when ready
	    setMachineChangeOverTimes(machineShopSimulator);
	
	    // Move this to startShop when ready
	    setUpJobs(machineShopSimulator.machine);
	
	    activateJobs(machineShopSimulator);
	}

	private void activateJobs(MachineShopSimulator machineShopSimulator) {
		for (int p = 1; p <= machineShopSimulator.numMachines; p++) {
			// schedule next one.
			Job lastJob;
			if (machineShopSimulator.machine[p].getActiveJob() == null) {// in idle or change-over
			                                            // state
			    lastJob = null;
			    // wait over, ready for new job
			    if (machineShopSimulator.machine[p].getJobQ().isEmpty()) // no waiting job
			        machineShopSimulator.eList.setFinishTime(p, machineShopSimulator.largeTime);
			    else {// take job off the queue and work on it
			        setupJob(machineShopSimulator, p);
			    }
			} else {// task has just finished on machine[theMachine]
			        // schedule change-over time
			    lastJob = machineShopSimulator.machine[p].getActiveJob();
			    machineShopSimulator.machine[p].setActiveJob(null);
			    machineShopSimulator.eList.setFinishTime(p, machineShopSimulator.timeNow
			            + machineShopSimulator.machine[p].getChangeTime());
			}
		}
	}

	private void setupJob(MachineShopSimulator machineShopSimulator, int p) {
		machineShopSimulator.machine[p].setActiveJob((Job) machineShopSimulator.machine[p].getJobQ()
		        .remove());
		machineShopSimulator.machine[p].setTotalWait(machineShopSimulator.machine[p].getTotalWait() + machineShopSimulator.timeNow
		        - machineShopSimulator.machine[p].getActiveJob().getArrivalTime());
		machineShopSimulator.machine[p].setNumTasks(machineShopSimulator.machine[p].getNumTasks() + 1);
		int t = machineShopSimulator.machine[p].getActiveJob().removeNextTask();
		machineShopSimulator.eList.setFinishTime(p, machineShopSimulator.timeNow + t);
	}

	public  SimulationResults runSimulation(MachineShopSimulator machineShopSimulator) {
	    machineShopSimulator.largeTime = Integer.MAX_VALUE;
	    machineShopSimulator.timeNow = 0;
	    startShop(machineShopSimulator); // initial machine loading
	    SimulationResults simulationResults = new SimulationResults(machineShopSimulator.numJobs);
	    machineShopSimulator.simulate(simulationResults); // run all jobs through shop
	    simulationResults.outputStatistics(machineShopSimulator);
	    return simulationResults;
	}
}
