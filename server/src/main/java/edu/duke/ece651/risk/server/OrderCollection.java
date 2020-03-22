package edu.duke.ece651.risk.server;

public class OrderCollection{
    int cnt;
    int target;

    public OrderCollection(int target) {
        this.cnt = 0;
        this.target = target;
    }

    public synchronized void ordersCompleted() {
        cnt++;
        if (cnt == target){
            this.notify();
        }
    }

    public synchronized void waitForCompletion() throws InterruptedException {
        while(!allPlayersCompleted()) {
            this.wait();
        }
    }

    synchronized boolean allPlayersCompleted(){
        return cnt == target;
    }
}
