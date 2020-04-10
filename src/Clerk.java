
import java.util.Random;

public class Clerk extends Thread {

    /**
     * FLag to signal clerk to home after all passengers are helped
     */
    private volatile Boolean goHome;

    /**
     * Custom ArrayList
     */
    private volatile PassengerList lineQueue;

    /**
     * The length of the line for the clerk
     */
    private int lineLength;

    /**
     * To store the number of passengers this clerk will help
     */
    private int numOfPassengersHelped;

    /**
     * Constructs a Clerk with its line capacity and name
     * @param lineLength line capacity for this thread
     * @param name the name of this thread
     */
    public Clerk(int lineLength, String name){
        setName(name);
        this.lineLength = lineLength;
        this.lineQueue = new PassengerList(lineLength);
        this.goHome = false;
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
            if( Counter.ticketNumbers[seatNum] == 0 ){
                foundTicketNum = true;
                Counter.ticketNumbers[seatNum]++;
            }
        }
        return seatNum;
    }

    /**
     * Checks if this clerk has a spot available in the line for another passenger
     * @return true if a spot is available, false otherwise
     */
    public boolean hasSpotAvailable(){
        return this.lineQueue.size() < lineLength;
    }

    /**
     * Adds a passenger to the this clerk's line
     * @param passenger the passenger to be added
     * @return true if there was a spot available, false otherwise
     */
    public Boolean addPassToLine(Passenger passenger){
        if( hasSpotAvailable() ) {
            //add passenger to the end of the queue
            this.lineQueue.add(passenger);
            return true;
        }
        else
            return false;
    }

    /**
     * Check if this clerk still has passengers in the line
     * @return true if there still are passengers, false otherwise
     */
    public Boolean hasPassengers(){
        return lineQueue.size() > 0;
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
     * Set goHome variable to true
     */
    public void setCanGoHome(boolean goHome){
        this.goHome = goHome;
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


    public void msg(String msg){
        System.out.println("["+Clock.getTime()+"] " + getName() + ": " + msg);
    }

    /**
     * Calls the passenger with the earliest time, generates a random seat number and zone number
     * for the passenger.
     */
    public void helpPassengers(){
        if( !lineQueue.isEmpty() ){

            //help next passenger in line
            Passenger passenger = lineQueue.getNextPassenger();

            //passenger moves to clerk line
            passenger.setStandBy(false);

            //get seat number and zone number for passenger
            int seatNum = getSeatNum();
            int zoneNum = getZone(seatNum);

            //Clerk takes 1 second to generate a ticket and zone number
            goToSleep(1000);

            msg(passenger.getName() + " assigned seatNum: " + seatNum + " zoneNum: " + zoneNum);

            //assign the seat number to the passenger
            passenger.setSeatNum(seatNum);
            passenger.setZoneNum(zoneNum);
            //passenger can go to security line

            //keep track of the number of passengers this clerk helped
            this.numOfPassengersHelped++;

        }
    }



    @Override
    public void run(){
        msg("arrived to the counter. Getting ready...");
        //clerks take 2 seconds to get ready to work, breakfast, etc...
        goToSleep(2000);
        msg("ready to help passengers");

        //help passengers until all of them have checked in
        //flag goHome is changed by Counter thread class
        while (!goHome){
            helpPassengers();
        }
        msg("is done - going home!. Helped " + this.numOfPassengersHelped + " passengers");
    }
}
