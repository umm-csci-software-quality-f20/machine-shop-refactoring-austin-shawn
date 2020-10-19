package applications;

import java.util.Arrays;

public class SimulationSpecification {
    int numMachines;
    private int numJobs;
    private int[] changeOverTimes;
    private Task[] taskSpecifications;
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
        taskSpecifications[jobNumber].setSpecificationsForTasks(specificationsForTasks);
    }

    public void setJobSpecification(Task[] taskSpecifications) {
        this.taskSpecifications = taskSpecifications;
    }

    public Task getJobSpecifications(int jobNumber) {
        return taskSpecifications[jobNumber];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<").append(numMachines).append(" machines, ");
        builder.append(numJobs).append(" jobs; ");
        builder.append("change overs: ").append(Arrays.toString(changeOverTimes));
        for (int i=1; i<=numJobs; ++i) {
            builder.append("; job ").append(i).append(" tasks: ");
            builder.append(Arrays.toString(taskSpecifications[i].getSpecificationsForTasks()));
        }

        builder.append(">");
        return builder.toString();
    }

	public void setMachineChangeOverTimes(MachineShopSimulator machineShopSimulator) {
	    for (int i = 1; i<=numMachines; ++i) {
			Machine machine =machineShopSimulator.machineAt(i);
	        machine.setChangeTime(getChangeOverTimes(i));
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
				int index = 2*(j-1) + 1;
				int theMachine = getJobSpecifications(i).getSpecificationsForTasks()[index];
				index = 2*(j-1) +2;
	            int theTaskTime = getJobSpecifications(i).getSpecificationsForTasks()[index];
	            if (j == 1)
	                firstMachine = theMachine; // job's first machine
	            theJob.addTask(theMachine, theTaskTime); // add to
	        } // task queue
	        machine[firstMachine].getJobQ().put(theJob);
	    }
	}

}
