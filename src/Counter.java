
public class Counter extends Thread {

    /**
     * Number of passengers per counter line
     */
    private int counterNum;


    /**
     * Array to store unique seat numbers for customers
     */
    static volatile int [] ticketNumbers;

    /**
     * Store the number of passengers
     */
    private int numberOfPassengers;

    /**
     * Clerk One
     */
    private Clerk clerkOne;

    /**
     * Clerk Two
     */
    private Clerk clerkTwo;

    /**
     * passengers will wait here if both clerk lines are full
     */
    private PassengerList standBy;

    public Counter(int numberOfPassengers){
        super("Counter");
        //static
        ticketNumbers = new int[31];

        //class variables
        this.counterNum    = 3;
        this.numberOfPassengers = numberOfPassengers;
        this.standBy  = new PassengerList(numberOfPassengers);
        this.clerkOne = new Clerk(counterNum, "ClerkOne");
        this.clerkTwo = new Clerk(counterNum, "ClerkTwo");
    }

    /**
     * Add a passenger from stand by to on of the clerk lines
     * @param passenger the passenger to be added to the clerk line
     * @return true if passenger was added, false otherwise
     */
    public Boolean assignPassToClerk(Passenger passenger){
        if( passenger == null )
            return false;

        if( clerkOne.hasSpotAvailable() ){
            return clerkOne.addPassToLine(passenger);
        }
        else if( clerkTwo.hasSpotAvailable() ){
            return clerkTwo.addPassToLine(passenger);
        }

        return false;
    }

    /**
     * Receives an ArrayList of Passengers
     * @param passengers the ArrayList of passengers
     */
    public void setStandByPassengers(PassengerList passengers){
        //make a copy to maintain a list with all the passengers for other threads to use later
        this.standBy = new PassengerList(passengers);
        for( Passenger pass: standBy){
            pass.start();
        }
    }


    /**
     * Finds the passenger waiting at stand by with the earliest arrival time
     * and assign it to the next available clerk
     */
    public void callPassengersFromStandBy(){
        Boolean isInLine;
        Passenger passenger = standBy.getNextPassenger();
        while( passenger != null ){
            //If both clerks are full - it will return false
            isInLine = assignPassToClerk(passenger);
            //if passenger was added to clerk, remove passenger from stand-by
            if( isInLine ){
                passenger = standBy.getNextPassenger();
            }
        }
    }

    public void msg(String msg){
        System.out.println("["+Clock.getTime()+"] " + getName() + ": " + msg);
    }


    @Override
    public void run(){

        //To store the start time the check in counter opened
        long startTime = Clock.getTime();
        msg("Check-in counter open");

        //have the clerks start their shift
        clerkOne.start();
        clerkTwo.start();

        //call passengers to be attended by clerks
        callPassengersFromStandBy();

        //BW until clerks finish their lines
        while (clerkOne.hasPassengers() || clerkTwo.hasPassengers());

        //both clerks are done and can go home - terminate
        clerkOne.setCanGoHome(true);
        clerkTwo.setCanGoHome(true);

        //At this point all passengers have a ticket number and should be in their to the security line
        //wait for clerks to go home and close the check-in station
        while (clerkTwo.isAlive() || clerkOne.isAlive());

        long totalTime = Clock.getTime() - startTime;
        msg("Checked-in " + numberOfPassengers + " passengers in " + totalTime + " ms");
        msg("Is closed. Will open tomorrow!");
    }

}

/*

*/