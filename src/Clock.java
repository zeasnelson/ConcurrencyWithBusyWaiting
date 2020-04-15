import java.util.concurrent.TimeUnit;

public class Clock {

    private static volatile long startTime;
    private static volatile long timerStart;
    private static volatile long timerLength;

    public static void setStartTime(){
       startTime = System.currentTimeMillis();
    }


    public static void setTimerStart(long length){
        timerLength = length;
        timerStart = getTime();
    }

    public static boolean timerIsRunning(){
        return ( getTime() - timerStart ) <= timerLength;

    }

    public static long getTime(){
        return System.currentTimeMillis() - startTime;
    }





}
