
public class FlightAttendant extends Thread {

    /**
     * An ArrayList for passengers to wait when called by the attendant
     */
    private PassengersList line;

    /**
     * To store a reference to all the passengers waiting to fly
     */
    private PassengersList passengersList;

    /**
     * The length of the line to board the plane
     */
    private int lineLength;


    public FlightAttendant(){
        super("FlightAttendant");
        this.lineLength = 10;
        this.line = new PassengersList(lineLength);
    }


    public void msg(String msg){
        System.out.println("["+Clock.getTime()+"] " + getName() + ": " + msg);
    }


    public void setPassengersList(PassengersList passengersList){
        this.passengersList = passengersList;
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
        //start the timer for 6 secs
        //this is the time the FlightAttendant will wait per zone
        Clock.setTimerStart(6000);

        //BW until all passengers with @zoneNum arrive
        int i = 0;
        Passenger passenger;
        while( Clock.timerIsRunning() ){
            passenger = passengersList.get(i);

            //make sure passenger is at the gate already
            if( passenger.isAtWaitAtGate()  ){

                //make sure passenger is in zone number specified and hasn't already been called
                if( passenger.getZoneNum() == zoneNum && !passenger.isAtBoardingLine() ) {
                    passenger.setIsAtBoardingLine(true);
                    //let the passenger move on to the boarding line
                    passenger.setWaitAtGate(false);
                    //add passenger to the waiting line
                    line.add(passenger);
                }
            }
            i = ((++i)% passengersList.size());
        }

        goToSleep(3000);
        //once all passengers are waiting at the boarding line
        scanBoardingPasses(zoneNum);

    }

    public void scanBoardingPasses(int zoneNum){
        msg("scanning boarding passes for " + line.size() + " passengers in zone " + zoneNum);
        //sort according to arrival time, ensure FCFS
        line.sortByArrivalTime();

        //scan their boarding passes
        for( Passenger pass : line ){
            pass.setBoardingPassScanned(true);
            pass.yield();
            pass.yield();
            msg(pass.getName() + "'s boarding pass with arrival time " + pass.getArrivalTime() + " scanned");
            msg(pass.getName()  + "is in seat " + pass.getSeatNum() + " and departs plane");
        }

        //clear the line for the next zone group
        line.clear();
    }


    /**
     *Check which passengers missed the flight and send them home
     */
    public void rebookFlights(){
        //if this list has passengers, then those passengers missed the flight
        if(passengersList.size() > 0) {

            //stop the threads of all passengers who missed the flight
            for (Passenger pass : passengersList) {
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
        passengersList.sortBySeatNumber();

        for (Passenger passenger : passengersList) {
            if (passenger.isAlive()) {
                try {
                    passenger.setGoHome(true);
                    passenger.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        msg(passengersList.size() + " passengers arrived home");
    }

    /**
     * Call the interrupt() method on all sleeping passengers
     */
    public void interruptAllPassengers(){
        for( Passenger pass : passengersList){
            if( pass.isAlive() ) {
                pass.interrupt();
                goToSleep(100);
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
        msg("All groups have boarded plane in " + totalTime + " ms");
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
        goToSleep(1000);

        //the original list contains all passengers including those who missed the flight
        // remove those that missed the flight
        passengersList.removeStoppedThreads();

        msg("Plane is about to land");

        //wake up passengers for landing
        interruptAllPassengers();

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

