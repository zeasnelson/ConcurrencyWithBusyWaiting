import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PassengerList extends ArrayList<Passenger> {

    public PassengerList(PassengerList passengers){
        super(passengers);
    }

    public PassengerList(int size){
        super(size);
    }

    /**
     * Finds the passenger with the earliest arrival time. This insures FCFS
     * @return the passenger with the earliest arrival time
     */
    public Passenger getNextPassenger(){
        if( isEmpty() ){
            return null;
        }

        long earliestArrivalTime = Integer.MAX_VALUE;
        int passengerIndex = -1;

        for( int i = 0; i < size(); i++) {
            Passenger passenger = get(i);
            if (passenger.getArrivalTime() != -1){
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
