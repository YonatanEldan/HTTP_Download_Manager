public class ConfigurationsSettings {

    public static final int SIZE_OF_DATACHUNK = 8192;

    public static final int TIMEOUT_FOR_WORKER = 10000;
    public static final int TIMEOUT_FOR_WRITER = 30000;

    // We won'n open a thread for less then 6MB.
    public static final int THREADS_SIZE_RATIO = 6291456;

    public static final int CHUNKS_QUEUE_CAPACITY = 500;

    public static final int MAX_NUM_OF_THREADS = 20;
}
