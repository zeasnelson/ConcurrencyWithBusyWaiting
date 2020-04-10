public class Airport extends Thread{

    /**
     * Store the number of passengers in the simulation
     */
    protected int numOfPassengers;

    /**
     * A custom ArrayList of type passengers
     */
    private PassengerList passengers;

    /**
     * Instance of Counter thread
     */
    private Counter counter;

    /**
     * Instance of FlightAttendant thread
     */
    private FlightAttendant flightAttendant;



    public Airport(int numOfPassengers){
        super("Airport");
        this.numOfPassengers = numOfPassengers;
        this.flightAttendant = new FlightAttendant();
        this.passengers      = new PassengerList(numOfPassengers);
        this.counter         = new Counter(numOfPassengers);
    }

    public void createPassengers(){
        for( int i = 0; i < numOfPassengers; i++){
            String passID = "Passenger-" + (i + 1);
            Passenger passenger = new Passenger(passID);
            passengers.add(passenger);
        }
    }

    public int getDepartTime(int numOfPassengers){
        if( numOfPassengers <= 10)
            return 6000;
        else if( numOfPassengers <= 20)
            return 12000;
        else
            return 22000;
    }

    @Override
    public void run(){
        //Store the start time for the entire simulation
        Clock.setStartTime();

        //create the passenger threads
        createPassengers();

        //passengers are in stand by and waiting to be called by the check in clerks
        counter.setStandByPassengers(passengers);
        //start the Counter thread
        counter.start();

        //plane depart time is calculated based on the number of passengers
        int departTime = getDepartTime(numOfPassengers);
        System.out.println("["+Clock.getTime()+"] " + getName() + ": Flight to Purell-Wonderland, NY will depart at " + departTime);

        //BW until it is time to start boarding plane
        while ( Clock.getTime() < departTime );

        //give the flight attendant a reference to all passengers
        flightAttendant.setPassengers(passengers);

        //start the FlightAttendant thread
        flightAttendant.start();

        //wait for flight attendant to terminate then terminate airport thread
        while ( flightAttendant.isAlive() );
        System.out.println("["+Clock.getTime()+"] " + getName() + "Airport closed");
    }



    public static void main(String[] args) {
        int numOfPassengers = 10;
        (new Airport(numOfPassengers)).start();
    }

}
