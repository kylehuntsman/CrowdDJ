package com.github.funnygopher.crowddj.managers;

import com.github.funnygopher.crowddj.CrowdDJ;
import com.github.funnygopher.crowddj.ObservableStatus;
import com.github.funnygopher.crowddj.StatusObserver;
import com.github.funnygopher.crowddj.vlc.VLCStatus;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class StatusManager implements ObservableStatus {

    CrowdDJ crowdDJ;
    private VLCStatus status;
    private Thread updateThread;
    public Task updateTask;

    volatile private List<StatusObserver> observers;

    public StatusManager(CrowdDJ crowdDJ) {
        this.crowdDJ = crowdDJ;
        observers = new ArrayList<StatusObserver>();
        //status = crowdDJ.getVLC().getStatus();

        updateTask = new Task() {
            @Override
            protected Object call() throws Exception {
                while(true) {
                    status = crowdDJ.getVLC().getStatus();
                    notifyObservers();
                    Thread.sleep(100);
                }
            }
        };
    }

    public void start() {
        updateThread = new Thread(updateTask);
        updateThread.start();
    }

    public void stop() {
        try {
            updateTask.cancel(true);
            updateThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public VLCStatus getStatus() {
        if(status == null)
            return VLCStatus.NO_CONNECTION;

        return status;
    }

    @Override
    public void registerObserver(StatusObserver observer) {
        if(!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(StatusObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (StatusObserver observer : observers) {
            observer.update(status);
        }
    }
}
