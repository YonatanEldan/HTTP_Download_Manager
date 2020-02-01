public class ConfigurationsSettings {

    public static final int SIZE_OF_DATACHUNK = 8192;

    //TODO: fix!
    public static final int TIMEOUT_FOR_WORKER = 15000000;
    public static final int TIMEOUT_FOR_WRITER = 30000000;
    // We won'n open a thread for less then 6MB.
    public static final int THREADS_SIZE_RATIO = 6291456;

}
