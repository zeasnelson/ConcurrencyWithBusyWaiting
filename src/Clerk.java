import java.util.Random;

public class Clerk extends Thread{

    //Store the number of passengers in the simulation
    private int numberOfPassengers;

    //The line length at the counter
    private int counterNum;

    //An ArrayList to store all passengers waiting in line
    private PassengersList lineQueue;

    //The start time for this thread
    private long startTime;

    //To store the number of passengers that this clerk helps
    private int numOfPassengersHelped;

    //To store a reference to the seat numbers already generated, no repeated seat numbers
    private int [] ticketNumbers;

    //To store a reference to all the passenger threads in the simulation
    private volatile PassengersList standByPassengers;

    /**
     * Constructs a Clerk thread
     * @param threadName The name of this thread
     * @param numberOfPassengers The number of passengers in the simulation
     * @param counterNum The line length for this clerk
     * @param ticketNumbers reference to the ticket numbers already generated
     */
    public Clerk(String threadName, int numberOfPassengers, int counterNum, int [] ticketNumbers){
        super(threadName);
        this.numberOfPassengers = 0;
        this.startTime          = -1;
        this.numberOfPassengers = numberOfPassengers;
        this.counterNum         = counterNum;
        this.lineQueue          = new PassengersList(counterNum);
        this.ticketNumbers      = ticketNumbers;
    }

    /**
     * Store a reference to all passengers in the simulation
     * @param passengersList An PassengersList
     */
    public void setStandByPassengers(PassengersList passengersList){
        this.standByPassengers = new PassengersList(passengersList);
    }


    /**
     * Put this thread to sleep
     * @param milli time in millis for thread to sleep
     */
    public void goToSleep(long milli){
        try {
            sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print a msg preceded by the thread name and the time this msg is being printed
     * @param msg The msg to be printed
     */
    public void msg(String msg){
        System.out.println("["+getTime()+"] " + getName() + ": " + msg);
    }


    /**
     * Compute the total elapsed time based on the start time
     * @return total elapsed time
     */
    public long getTime(){
        return System.currentTimeMillis() - this.startTime;
    }


    /**
     * Generates a random unique seat number for a passenger
     * @return the seat number
     */
    public int getSeatNum(){
        Random rand = new Random();
        boolean foundTicketNum = false;
        int seatNum = -1;

        while (!foundTicketNum){
            seatNum = rand.nextInt(30)+1;
            if( ticketNumbers[seatNum] == 0 ){
                foundTicketNum = true;
                ticketNumbers[seatNum]++;
            }
        }
        return seatNum;
    }

    /**
     * Generate a zone number based on the seat number
     * @param seatNum the seat number
     * @return a zone number between 1-3
     */
    public int getZone(int seatNum){
        if( seatNum >= 0 && seatNum <= 10 ){
            return 1;
        }
        else if( seatNum >= 11 && seatNum <= 20 ){
            return 2;
        }

        else if( seatNum >= 21 && seatNum <= 30 ){
            return 3;
        }
        else
            return -1;
    }



    /**
     * Checks if this clerk has a spot available in the line for another passenger
     * @return true if a spot is available, false otherwise
     */
    public boolean hasSpotAvailable(){
        return this.lineQueue.size() < counterNum;
    }


    /**
     * Add a passenger from stand by to one of the clerk lines
     * @param passenger the passenger to be added to the clerk line
     * @return true if passenger was added, false otherwise
     */
    public boolean addPassengerToLine(Passenger passenger){
        if( passenger == null )
            return false;

        if( hasSpotAvailable() ){
            return lineQueue.add(passenger);
        }

        return false;
    }


    /**
     * Calls the passenger with the earliest time, generates a random seat number and zone number
     * for the passenger.
     */
    public void helpPassengers(){
        if( !lineQueue.isEmpty() ){
            //help next passenger in line
            Passenger passenger = lineQueue.getNextPassenger();
            if( passenger == null )
                return;

            //get seat number and zone number for passenger
            int seatNum = getSeatNum();
            int zoneNum = getZone(seatNum);

            //Clerk takes 1 second to generate a ticket and zone number
            goToSleep(1000);

            msg(passenger.getName() + " assigned seatNum: " + seatNum + " zoneNum: " + zoneNum);

            //assign the seat number to the passenger
            passenger.setSeatNum(seatNum);
            passenger.setZoneNum(zoneNum);

            //keep track of the number of passengers this clerk helped
            this.numOfPassengersHelped++;
        }
    }


    @Override
    public void run() {
        this.startTime = System.currentTimeMillis();
        msg("arrived to the counter. Getting ready...");
        //clerk takes 2 seconds to get ready to work, breakfast, etc...
        goToSleep(2000);
        msg("ready to help passengers");

        boolean isInLine    = true;
        Passenger passenger = null;
        //loop until all passengers are given a boarding pass
        while( !standByPassengers.isEmpty() || !lineQueue.isEmpty() ){

            //loop until all passengers in stand-b area lining in the clerk lines
            //if passenger was added to clerk, remove passenger from stand-by
            if( isInLine || passenger == null ){
                //get the next passenger with the earliest arrival time to ensure FCFS
                passenger = standByPassengers.getNextPassenger();
            }

            if( passenger != null && passenger.getStandBy() ){
                //passenger moves to clerk line
                passenger.setStandBy(false);
                //Add passenger to line queue
                isInLine = addPassengerToLine(passenger);
                //clerk takes 1 second to help one customer
                goToSleep(1000);
            }
            else
                isInLine = true;

            //call passengers from the lineQueue
            helpPassengers();
        }

        msg("is done - going home!. Helped " + this.numOfPassengersHelped + " passengers");
    }

}