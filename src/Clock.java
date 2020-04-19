public class Clock extends Thread {

    //To store the start time of this thread
    private long startTime;

    //The number of passengers in the simulation
    private int numOfPassengers;

    //Store a reference to the flight attendant thread
    private FlightAttendant flightAttendant;

    /**
     * Construct a Clock thread
     * @param numberOfPassengers The number of passengers in the simulation
     * @param flightAttendant Reference to the flight attendant thread
     */
    public Clock(int numberOfPassengers, FlightAttendant flightAttendant){
        super("Clock");
        this.numOfPassengers = numberOfPassengers;
        this.flightAttendant = flightAttendant;
    }

    /**
     * Calculate the depart time based on the  number of passengers
     * @param numOfPassengers the number of passengers in the simulation
     * @return the plane depart time
     */
    public int getDepartTime(int numOfPassengers){
        if( numOfPassengers <= 10)
            return 6000;
        else if( numOfPassengers <= 20)
            return 12000;
        else
            return 20000;
    }


    public void msg(String msg){
        System.out.println("["+getTime()+"] " + getName() + ": " + msg);
    }

    public long getTime(){
        return System.currentTimeMillis() - this.startTime;
    }

    /**
     * Put this thread to sleep for a specified amount of time
     * @param millis The time in milliseconds for this thread to sleep
     */
    public void gotToSleep(long millis){
        try {
            sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        this.startTime = System.currentTimeMillis();

        //plane depart time is calculated based on the number of passengers
        int boardingTime = getDepartTime(numOfPassengers);
        //the approx. time at which the flight attendant is done embarking
        int departTime = boardingTime+28000;

        msg("Flight to Purell-Wonderland, NY will start boarding at " + boardingTime);
        msg("Flight to Purell-Wonderland, NY will depart at ~" + departTime);

        //sleep until its time to board plane
        gotToSleep(boardingTime);
        //signal the flightAttendant thread to start boarding
        flightAttendant.setStartBoarding(true);

        //sleep until plane departs
        gotToSleep(departTime);

        //plane is in the air, the flight will last 2 hours
        gotToSleep(3000);
        msg("Mid-flight. 1 hour remaining");
        //signal flight attendant to serve mid-flight meal
        flightAttendant.setMidFlightMeal(true);

        gotToSleep(3000);
        flightAttendant.setDisembarkPlane(true);

        while (flightAttendant.isAlive());
        msg("Airport closed ");
    }
}
