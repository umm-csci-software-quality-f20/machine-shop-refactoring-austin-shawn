package applications;

import java.util.Arrays;

public class SimulationResults {
    private int finishTime;
    private int numMachines;
    private int[] numTasksPerMachine;
    private int[] totalWaitTimePerMachine;
    private JobCompletionData[] jobCompletions;
    private int nextJob = 0;

    public SimulationResults(int numJobs) {
        jobCompletions = new JobCompletionData[numJobs];
    }

    public void print() {
        for (JobCompletionData data : jobCompletions) {
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

    public JobCompletionData[] getJobCompletionData() {
        return jobCompletions;
    }

    public void setJobCompletionData(int jobNumber, int completionTime, int totalWaitTime) {
        JobCompletionData jobCompletionData = new JobCompletionData(jobNumber, completionTime, totalWaitTime);
        jobCompletions[nextJob] = jobCompletionData;
        nextJob++;
    }
    void simulate(MachineShopSimulator machineShopSimulator) {
        while (machineShopSimulator.numJobs > 0) {// at least one job left
            int nextToFinish = machineShopSimulator.eList.nextEventMachine();
            machineShopSimulator.timeNow = machineShopSimulator.eList.nextEventTime(nextToFinish);
            // change job on machine nextToFinish
            Job theJob = machineShopSimulator.changeState(nextToFinish);
            // move theJob to its next machine
            // decrement numJobs if theJob has finished
            if (theJob != null && !theJob.moveToNextMachine(machineShopSimulator, this))
                machineShopSimulator.numJobs--;
        }
    }
}
