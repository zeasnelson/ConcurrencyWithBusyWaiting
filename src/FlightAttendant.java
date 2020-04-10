
public class FlightAttendant extends Thread {

    /**
     * An ArrayList for passengers to wait when called by the attendant
     */
    private PassengerList line;

    /**
     * To store a reference to all the passengers waiting to fly
     */
    private PassengerList passengers;

    /**
     * The length of the line to board the plane
     */
    private int lineLength;


    public FlightAttendant(){
        super("FlightAttendant");
        this.lineLength = 10;
        this.line = new PassengerList(lineLength);
    }


    public void msg(String msg){
        System.out.println("["+Clock.getTime()+"] " + getName() + ": " + msg);
    }


    public void setPassengers(PassengerList passengers){
        this.passengers = passengers;
    }

    /**
     * Put this thread to sleep
     * @param milli time in millis for thread to sleep
     */
    public void goToSleep(long milli){
        try {
            sleep(milli);
        } catch (InterruptedException e) {
//            e.printStackTrace();
            System.out.println("Ooops. Looks like the flight attendant just quit");
        }
    }


    /**
     * The flight attendant will call all passengers per zone number.
     * For each zone, the flight attendant will wait 5 minutes.
     * If passengers don't make it, they have to rebook their flights
     * @param zoneNum the zone number being called
     */
    public void callZone(int zoneNum){
        long startTime = Clock.getTime();
        long timer = 0;

        //BW until all passengers with @zoneNum arrive
        //Flight attendant will spend 10 minutes per zone
        int i = 0;
        Passenger passenger;
        while( Clock.millisToSecs(timer) < 5 ){
            passenger = passengers.get(i);
            if( passenger.isAtWaitAtGate()  ){
                if( passenger.getZoneNum() == zoneNum && !passenger.isAtBoardingLine() ) {
                    line.add(passenger);
                    passenger.setWaitAtGate(false);
                }
            }
            i = ((++i)%passengers.size());
            timer = Clock.getTime() - startTime;
        }

        //once all passengers are waiting at the boarding line
        //scan their boarding passes
        for( Passenger pass : line ){
            pass.setBoardingPassScanned(true);
        }

        //clear the line for the next zone group
        line.clear();
    }


    /**
     *Check which passengers missed the flight and send them home
     */
    public void rebookFlights(){
        //if this list has passengers, then those passengers missed the flight
        if(passengers.size() > 0) {

            //stop the threads of all passengers who missed the flight
            for (Passenger pass : passengers) {
                if( !pass.isAtBoardingLine() ) {
                    msg("Passenger " + pass.getName() + " missed the flight");
                    pass.stopThread(true);
                    pass.setWaitAtGate(false);
                }
            }
        }
    }


    /**
     * Use isAlive() and join() to send all passengers home
     */
    public void sendPassengersHome(){
        passengers.sort(Passenger::compareTo);

        for (Passenger passenger : passengers) {
            if (passenger.isAlive()) {
                try {
                    passenger.setGoHome(true);
                    passenger.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Call the interrupt() method on all sleeping passengers
     */
    public void interruptAllPassengers(){
        for( Passenger pass : passengers ){
            if( pass.isAlive() ) {
                pass.interrupt();
            }
        }
    }

    @Override
    public void run(){
        msg("Get ready to board plane");

        //get boarding start time, in 30 minutes plane doors will close
        long boardingStartTime = Clock.getTime();

        msg("Calling all passengers in zone 1");
        callZone(1);
        msg("Calling all passengers in zone 2");
        callZone(2);
        msg("Calling all passengers in zone 3");
        callZone(3);

        long totalTime = Clock.getTime() - boardingStartTime;
        msg("All groups have boarded plane in " + Clock.millisToSecs(totalTime) + " minutes");
        msg("Plane doors are closed");

        //check which passenger missed the flight and have them rebook flights
        rebookFlights();

        msg("Welcome to Purrel Airlines. Plane is departing now");
        msg("Plane is departing now");
        msg("This flight will be two hours long.");

        goToSleep(4000);
        msg("Mid-flight meal. We'll arrive in 1 hour");
        goToSleep(4000);
        msg("We'll arrive in 10 minutes. Fasten seat belts");
        goToSleep(100);

        //the original list contains all passengers including those who missed the flight
        // remove those that missed the flight
        passengers.removeStoppedThreads();

        //wake up passengers for landing
        interruptAllPassengers();

        msg("Plane is about to land");
        goToSleep(1000);
        msg("Plane landed. Seat belt light turned off");
        msg("Leave plane in ascending order according to seat number");

        //disembark plane according to seat number
        sendPassengersHome();

        msg("cleaning airplane..");
        goToSleep(2000);
        msg("Done cleaning - going home");

    }


}

