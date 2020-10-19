package applications;

// top-level nested classes
class Task {
    // data members
    private int machine;
    private int time;

    // constructor
    Task(int machineIndex, int timeSpent) {
        machine = machineIndex;
        time = timeSpent;
    }

    public int getMachine() {
        return machine;
    }

    public int getTime() {
        return time;
    }
}
