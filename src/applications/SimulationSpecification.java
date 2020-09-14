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
}
