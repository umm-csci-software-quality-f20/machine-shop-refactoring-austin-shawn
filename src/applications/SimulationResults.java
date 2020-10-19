package applications;

import java.util.Arrays;

public class SimulationResults {
    private int finishTime;
    private int numMachines;
    private int[] numTasksPerMachine;
    private int[] totalWaitTimePerMachine;
    private Job[] jobCompletions;
    private int nextJob = 0;

    public SimulationResults(int numJobs) {
        jobCompletions = new Job[numJobs];
    }

    public void print() {
        for (Job data : jobCompletions) {
            System.out.println("Job " + data.getJobNumber() + " has completed at "
                    + data.getCompletionTime() + " Total wait was " + data.getTotalWaitTime());
        }

        System.out.println("Finish time = " + finishTime);
        for (int p = 1; p <= numMachines; p++) {
            System.out.println("Machine " + p + " completed "
                    + numTasksPerMachine[p] + " tasks");
            System.out.println("The total wait time was "
                    + totalWaitTimePerMachine[p]);
            System.out.println();
        }
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public void setNumMachines(int numMachines) {
        this.numMachines = numMachines;
    }

    public int[] getNumTasksPerMachine() {
        return Arrays.copyOf(numTasksPerMachine, numTasksPerMachine.length);
    }

    public void setNumTasksPerMachine(int[] numTasksPerMachine) {
        this.numTasksPerMachine = numTasksPerMachine;
    }

    public int[] getTotalWaitTimePerMachine() {
        return Arrays.copyOf(totalWaitTimePerMachine, totalWaitTimePerMachine.length);
    }

    public void setTotalWaitTimePerMachine(int[] totalWaitTimePerMachine) {
        this.totalWaitTimePerMachine = totalWaitTimePerMachine;
    }

    public Job[] getJobCompletionData() {
        return jobCompletions;
    }

    public void setJobCompletionData(Job job, int completionTime) {
        Job jobCompletionData = new Job(job.getId(), completionTime, completionTime - job.getLength());
        jobCompletions[nextJob] = jobCompletionData;
        nextJob++;
    }
    /** output wait times at machines
	 * @param machineShopSimulator TODO
	 * */
	void outputStatistics(MachineShopSimulator machineShopSimulator, int timeNow, int numMachines) {
	    setFinishTime(timeNow);
	    setNumMachines(numMachines);
	    setNumTasksPerMachine(machineShopSimulator);
	    setTotalWaitTimePerMachine(machineShopSimulator);
	}

	void setTotalWaitTimePerMachine(MachineShopSimulator machineShopSimulator) {
        int numOfMachines = machineShopSimulator.getNumMachines();
	    int[] totalWaitTimePerMachine = new int[numOfMachines+1];
	    for (int i=1; i<=numOfMachines; ++i) {
            Machine machine = machineShopSimulator.machineAt(i);
	        totalWaitTimePerMachine[i] = machine.getTotalWait();
	    }
	    setTotalWaitTimePerMachine(totalWaitTimePerMachine);
	}

	void setNumTasksPerMachine(MachineShopSimulator machineShopSimulator) {
        int numOfMachines = machineShopSimulator.getNumMachines();
	    int[] numTasksPerMachine = new int[numOfMachines+1];
	    for (int i=1; i<=numOfMachines; ++i) {
            Machine machine = machineShopSimulator.machineAt(i);
	        numTasksPerMachine[i] = machine.getNumTasks();
	    }
	    setNumTasksPerMachine(numTasksPerMachine);
	}
}
