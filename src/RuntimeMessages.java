public class RuntimeMessages {

    //manager
    public static final String THE_PROGRAM_SHUT_DOWN = "The program shut down unexpectedly.\n" +
                                                        "Launch it again to resume your download.";
    public static final String DOWNLOAD_SUCCEEDED = "Download succeeded";

    // worker
    public static final String INPUTSTREAM_CLOSE_EXEPTION = "Failed Closing the connection with the server";
    public static final String SERVER_CONNECTION_FAILED = "Connection to the server was failed.\n" +
                                                            "Please restart the program to resume download.";
    public static final String FAILED_TO_FETCH_DATA_FROM_SERVER = "Failed to fetch data from the server";
    public static final String FAILED_TO_INSERT_INTO_THE_QUEUE = "Failed to insert the dataChunk into the queue";
    public static final String FAILED_TO_SAVE_DATA = "Failed to save the file.\n" +
                                                        "Please restart the program to resume download.";

    // writer
    public static final String FAILED_TO_TAKE_DATA_FROM_THE_QUEUE = "Failed take data from the queue";
    public static final String FAILED_WHEN_WRITING_TO_FILE = "Failed to write the file. \n" +
                                                                "Please restart the program to try again";

    //progressKeeper
    public static final String FAILED_INIT_META_DATA_FILES = "Failed to create the meta data files.\n" +
                                                            "If you want to be able to pause and resume your download, please restart the program.";
    public static final String COULD_NOT_READ_MATA_DATA = "could not read the meta data file. Restarting the download...";





}

