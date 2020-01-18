package modules;

public class Manager {
    String[] servers;
    int MAX_THREADS_NUM;

    public Manager(String[] servers, int maxThreadNum){
        this.servers = servers;
        this.MAX_THREADS_NUM = maxThreadNum;
    }


}
