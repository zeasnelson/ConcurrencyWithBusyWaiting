public class Passenger extends Thread implements Comparable<Passenger>{

    /**
     * Store the seat number
     */
    private volatile int seatNum;

    /**
     * Store the zone number
     */
    private volatile int zoneNum;

    /**
     * Flag to BW passenger in stand by area.
     */
    private volatile boolean standBy;

    /**
     * Flag to BW passenger at gate
     */
    private volatile boolean waitAtGate;

    /**
     * Flag to BW passenger until flight attendant scans boarding pass
     */
    private volatile boolean boardingPassScanned;

    /**
     * Flag to send passenger home when flight is missed
     */
    private volatile boolean stop;

    /**
     * Flag to BW passenger until instructed by flight attendant to leave plane
     */
    private volatile boolean goHome;

    /**
     * Flag to know when a passenger is waiting at the boarding line
     */
    private volatile boolean isAtBoardingLine;

    /**
     * To save the arrival time to airport.
     * Later used to save arrival time to boarding gate
     */
    private long arrivalTime;


    public Passenger(String passengerID){
        super(passengerID);
        this.seatNum = -1;
        this.zoneNum = -1;
        this.standBy = true;
        this.waitAtGate = false;
        this.stop = false;
        this.goHome = false;
        this.boardingPassScanned = false;
        this.isAtBoardingLine = false;
    }

    //getters and setters

    public boolean isAtBoardingLine() {
        return isAtBoardingLine;
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

    public void setStandBy(boolean standBy){
        this.standBy = standBy;
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
            msg("sleep interrupted. Ready for landing");
        }
    }


    public void msg(String msg){
        System.out.println("["+Clock.getTime()+"] " + getName() + ": " + msg);
    }

    /**
     * Passenger missed flight. Stop flag set to true
     */
    public void stopThread(boolean stop){
        this.stop = stop;
    }

    @Override
    public int compareTo(Passenger otherPass) {
        return seatNum - otherPass.getSeatNum();
    }

    @Override
    public void run() {

        //get a random number between 0 and 1 sec for arrival time
        int randomTime = (int) (Math.random()*5000);
        goToSleep(randomTime);

        //passenger arrives, save the arrival time
        this.arrivalTime = Clock.getTime();
        msg("arrived to the airport - waiting in stand by area");

        //BW until a spot open in one of the clerk lines. standBy updated by clerk thread
        while (standBy);
        msg("waiting in clerk line");

        //BW for the clerk to assign a ticket and zone number
        while ( seatNum == -1 || zoneNum == -1 );

        //passenger now rushes to the security line
        msg("is going through security line ");
        int defaultPriority = getPriority();
        setPriority(1);
        randomTime = (int) (Math.random()*8000);
        goToSleep(randomTime);
        setPriority(defaultPriority);

        waitAtGate = true;
        msg("arrived to gate. Waiting for flight attendant to call");
        //BW until flight attendant calls. Once flight attendant calls this passenger, will stop BW
        while (waitAtGate);

        //continues only if this passenger did not miss the flight
        if( !stop ) {

            isAtBoardingLine = true;

            msg("Walking to the boarding door. Seat: " + seatNum + " Zone: " + zoneNum);
            //will take up to 1 seconds to walk to boarding line
            randomTime = (int) (Math.random() * 1000);
            goToSleep(randomTime);

            msg("Waiting at boarding line to scan boarding pass");
            //set arrival time to the boarding line
            arrivalTime = Clock.getTime();

            //BW until boarding pass is scanned by flight attendant
            while (!boardingPassScanned) ;
            msg("is scanning boarding pass");
            yield();
            yield();
            msg("has boarded the plane and is sitting - going to sleep");

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