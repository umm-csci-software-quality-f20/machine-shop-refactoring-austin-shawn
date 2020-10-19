package applications;

// top-level nested classes
class Task {
    // data members
    private int machine;
    private int time;

    private int numTasks;
    private int[] specificationsForTasks;
    // constructor
    Task(int theMachine, int theTime) {
        machine = theMachine;
        time = theTime;
    }

    public int getMachine() {
        return machine;
    }

    public int getTime() {
        return time;
    }

    public void setNumTasks(int numTasks) {
        this.numTasks = numTasks;
    }

    public int getNumTasks() {
        return numTasks;
    }

    public void setSpecificationsForTasks(int[] specificationsForTasks) {
        this.specificationsForTasks = specificationsForTasks;
    }

    public int[] getSpecificationsForTasks() {
        return specificationsForTasks;
    }
}
