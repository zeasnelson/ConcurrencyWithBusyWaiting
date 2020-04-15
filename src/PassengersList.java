import java.util.ArrayList;

public class PassengersList extends ArrayList<Passenger> {

    public PassengersList(PassengersList passengers){
        super(passengers);
    }

    public PassengersList(int size){
        super(size);
    }

    /**
     * Finds the passenger with the earliest arrival time. This insures FCFS
     * Passengers with arrivalTime == -1, are not checked.
     * This passengers have not arrived to the airport yet
     * @return the passenger with the earliest arrival time or null if no one arrived yet
     */
    public Passenger getNextPassenger(){
        if( isEmpty() ){
            return null;
        }

        long earliestArrivalTime = Integer.MAX_VALUE;
        int passengerIndex = -1;

        for( int i = 0; i < size(); i++) {
            Passenger passenger = get(i);
            if (passenger.getArrivalTime() != -1 ){
                if (passenger.getArrivalTime() < earliestArrivalTime) {
                    earliestArrivalTime = passenger.getArrivalTime();
                    passengerIndex = i;
                }
            }
        }

        if( passengerIndex == -1 )
            return null;
        else
            return remove(passengerIndex);

    }

    public void sortBySeatNumber(){
        sort(Passenger::compareSeatNumber);
    }

    public void sortByArrivalTime(){
        sort(Passenger::compareArrivalTime);
    }

    public void removeStoppedThreads(){
        removeIf( passenger -> (!passenger.isAlive()) );
    }

}
