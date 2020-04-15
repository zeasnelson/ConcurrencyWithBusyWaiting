public class Main {

    public static void main(String[] args) {
        int numberOfPassengers = 5;
        int counterNum = 3;
        int numOfPlaneSeats = 30;
        int [] ticketNumbers = new int[numOfPlaneSeats+1];

        //create passenger threads
        PassengersList passengersList = new PassengersList(numberOfPassengers);
        for( int i = 0; i < numberOfPassengers; i++){
            String passID = "Passenger-" + (i + 1);
            Passenger passenger = new Passenger(passID);
            passengersList.add(passenger);
        }

        //create FlightAttendant, Clock and Clerk threads
        FlightAttendant flightAttendant = new FlightAttendant();
        Clock clock    = new Clock(numberOfPassengers, flightAttendant);
        Clerk clerkOne = new Clerk("ClerkOne", numberOfPassengers, counterNum, ticketNumbers);
        Clerk clerkTwo = new Clerk("ClerkTwo", numberOfPassengers, counterNum, ticketNumbers);

        //pass a reference of all passengers to each thread
        flightAttendant.setPassengersList(passengersList);
        clerkOne.setStandByPassengers(passengersList);
        clerkTwo.setStandByPassengers(passengersList);

        //start all threads
        clock.start();
        clerkOne.start();
        clerkTwo.start();
        flightAttendant.start();
        passengersList.forEach(passenger -> passenger.start());
    }

}

