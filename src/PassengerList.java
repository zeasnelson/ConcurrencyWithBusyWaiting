import java.util.ArrayList;

public class PassengerList extends ArrayList<Passenger> {

    public PassengerList(PassengerList passengers){
        super(passengers);
    }

    public PassengerList(int size){
        super(size);
    }

    /**
     * Finds the passenger with the earliest arrival time
     * @return the passenger with the earliest arrival time
     */
    public Passenger getNextPassenger(){
        if( isEmpty() ){
            return null;
        }

        long earliestArrivalTime = Integer.MAX_VALUE;
        int passengerIndex = -1;

        for( int i = 0; i < size(); i++){
            Passenger passenger = get(i);
            if( passenger.getArrivalTime() < earliestArrivalTime ){
                earliestArrivalTime = passenger.getArrivalTime();
                passengerIndex = i;
            }
        }

        return remove(passengerIndex);
    }


    public void removeStoppedThreads(){
        removeIf( passenger -> (!passenger.isAlive()) );
    }



}
