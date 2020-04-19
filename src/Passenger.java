import java.util.Random;

public class Passenger extends Thread{

    // Store the seat number
    private volatile int seatNum;

    // Store the zone number
    private volatile int zoneNum;

    // Flag to BW passenger in stand by area
    private volatile boolean standBy;

    // Flag to BW passenger at gate
    private volatile boolean waitAtGate;

    // Flag to BW passenger until flight attendant scans boarding pass
    private volatile boolean boardingPassScanned;

    // Flag to send passenger home when flight is missed
    private volatile boolean stop;

    // Flag to BW passenger until instructed by flight attendant to leave plane
    private volatile boolean goHome;

    // Flag to know when a passenger is waiting at the boarding line
    private volatile boolean isAtBoardingLine;

    // To save the airport arrival time.
    // Later used to save arrival time to boarding gate
    private volatile long arrivalTime;

    // To save the start time for the thread
    private volatile long startTime;


    public Passenger(String passengerID){
        super(passengerID);
        this.startTime   = -1;
        this.arrivalTime = -1;
        this.seatNum     = -1;
        this.zoneNum     = -1;
        this.stop        = false;
        this.goHome      = false;
        this.standBy     = true;
        this.waitAtGate  = false;
        this.boardingPassScanned = false;
        this.isAtBoardingLine    = false;
    }

    //getters and setters
    public void setStandBy(boolean standBy){
        this.standBy = standBy;
    }

    public boolean getStandBy(){
        return this.standBy;
    }

    public boolean isAtBoardingLine() {
        return isAtBoardingLine;
    }

    public void setIsAtBoardingLine(Boolean isAtBoardingLine){
        this.isAtBoardingLine = isAtBoardingLine;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public int getZoneNum() {
        return zoneNum;
    }

    public void setGoHome(boolean goHome){
        this.goHome = goHome;
    }

    public void setZoneNum(int zoneNum) {
        this.zoneNum = zoneNum;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setWaitAtGate(boolean waitAtGate){ this.waitAtGate = waitAtGate; }

    public boolean isAtWaitAtGate(){
        return this.waitAtGate;
    }

    public void setBoardingPassScanned(boolean flag) {
        this.boardingPassScanned = flag;
    }


    /**
     * Put the thread to sleep
     * @param milli time to sleep in millis
     */
    public void goToSleep(int milli){
        try {
            sleep(milli);
        } catch (InterruptedException e) {
            if( boardingPassScanned )
                msg("sleep interrupted. Ready for landing");
            else
                System.out.println("Passenger lost");
        }
    }


    public void msg(String msg){
        System.out.println("["+getTime()+"] " + getName() + ": " + msg);
    }

    /**
     * Passenger missed flight. Stop flag set to true
     */
    public void stopThread(boolean stop){
        this.stop = stop;
    }

    /**
     *  Compare the seat number of this passenger with the seat number of another passenger
     * @param OtherPassenger an object of type Passenger
     * @return < 0 if this passengers seat is less that the other, 0 if equal,
     * > 0 if the seat number of this passenger is grater than the other's passenger seat number
     */
    public int compareSeatNumber(Passenger OtherPassenger) {
        return seatNum - OtherPassenger.getSeatNum();
    }

    /**
     *  Compare the arrival time of this passenger with the arrival time of another passenger
     * @param OtherPassenger an object of type Passenger
     * @return < 0 if this passengers arrival time is less that the other, 0 if equal,
     * > 0 if the arrival time of this passenger is grater than the other's passenger arrival time
     */
    public int compareArrivalTime(Passenger OtherPassenger){
        return (int) (arrivalTime - OtherPassenger.getArrivalTime());
    }


    public long getTime(){
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public void run() {

        this.startTime = System.currentTimeMillis();

        //get a random number between 0 and 1 sec for arrival time
        Random rand = new Random();
        int randomTime = rand.nextInt(3000);
        goToSleep(randomTime);

        //passenger arrives, save the arrival time
        this.arrivalTime = getTime();
        msg("arrived to the airport - waiting in stand by area");

        //BW until a spot open in one of the clerk lines. standBy updated by clerk thread
        while (standBy);
        msg("waiting in clerk line");

        //BW for the clerk to assign a ticket and zone number
        while ( seatNum == -1 || zoneNum == -1 );
        arrivalTime = -1;

        //passenger now rushes to the security line
        msg("is going through security line ");
        int defaultPriority = getPriority();
        setPriority(1);

        //passenger will take between 5 to 10 seconds to go pass security
        randomTime = rand.nextInt(5000)+5000;
        goToSleep(randomTime);
        setPriority(defaultPriority);

        msg("arrived to gate. Waiting for flight attendant to call");
        //BW until flight attendant calls. Once flight attendant calls this passenger, will stop BW
        waitAtGate = true;
        while (waitAtGate);

        //continues only if this passenger did not miss the flight
        if( !stop ) {

            msg("Walking to the boarding door. ZoneNum: " + zoneNum);
            //will take between 1 and 2 seconds to make to the line
            randomTime = rand.nextInt(1000)+1000;
            goToSleep(randomTime);

            msg("Waiting at boarding line to scan boarding pass.");
            //set arrival time to the boarding line
            arrivalTime = getTime();


            //BW until boarding pass is scanned by flight attendant
            while (!boardingPassScanned) ;

            //sleep during flight
            goToSleep(90000);

            while(!goHome);
            msg("Exiting plane - going home. Seat: " + seatNum + " ZoneNum: " + zoneNum);
            //Passenger takes .5 seconds to exit plane
            goToSleep(500);

        }
        else{
            // passenger missed flight
            msg("Re-booking flight. Going home");
        }
    }
}