package com.github.funnygopher.crowddj;

public interface ObservableStatus {

    void registerObserver(StatusObserver observer);

    void unregisterObserver(StatusObserver observer);

    void notifyObservers();
}
