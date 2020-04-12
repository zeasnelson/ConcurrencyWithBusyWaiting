import java.util.concurrent.TimeUnit;

public class Clock {

    private static volatile long startTime;
    private static volatile long timerStart;
    private static volatile long timerLength;

    public static void setStartTime(){
       startTime = System.currentTimeMillis();
    }

//    public static Long millisToHour(Long milli){
//        if( milli == null || milli == 0 ){
//            return (long)0;
//        }
//        return TimeUnit.MILLISECONDS.toHours(milli);
//    }
//
//    public static Long millisToMinutes(Long milli){
//        if( milli == null || milli == 0 ){
//            return (long)0;
//        }
//        return TimeUnit.MILLISECONDS.toMinutes(milli);
//    }
//
    public static Long millisToSecs(Long milli){

        if( milli == null || milli == 0 ){
            return (long)0;
        }
        return TimeUnit.MILLISECONDS.toSeconds(milli);
    }


//    public static String getHHMMSS(Long elapsedTime){
//        Long hour = millisToHour(elapsedTime);
//        Long min = millisToMinutes(elapsedTime);
//        Long secs = millisToSecs(elapsedTime);
//        return String.format("%02d:%02d:%02d", hour, min, secs);
//    }

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
