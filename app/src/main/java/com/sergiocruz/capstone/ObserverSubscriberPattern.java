package com.sergiocruz.capstone;

import java.util.ArrayList;

public class ObserverSubscriberPattern {


    public interface Observable {
        void addObserver(Observer observer);

        void removeObserver(Observer observer);

        void notifyObserver();
    }

    interface Observer {
        void update();
    }

    public class YoutubeChannel implements Observable {
        private ArrayList<Observer> observers = new ArrayList<Observer>();

        @Override
        public void addObserver(Observer observer) {
            observers.add(observer);
        }

        @Override
        public void removeObserver(Observer observer) {
            observers.remove(observer);
        }

        void releaseNewVideo(String video) {
            System.out.println("Release new video : " + video);
            notifyObserver();
        }

        @Override
        public void notifyObserver() {
            for (Observer observer : observers) {
                observer.update();
            }
        }
    }

    public class YoutubeSubscriber implements Observer {

        YoutubeSubscriber(Observable observable) {
            observable.addObserver(this);
        }

        @Override
        public void update() {
            System.out.println("New video on channel!");
        }
    }


    public class Main {
        public void main(String[] args) {
            YoutubeChannel youtubeChannel = new YoutubeChannel();

            YoutubeSubscriber subscriberA = new YoutubeSubscriber(youtubeChannel);
            YoutubeSubscriber subscriberB = new YoutubeSubscriber(youtubeChannel);
            YoutubeSubscriber subscriberC = new YoutubeSubscriber(youtubeChannel);

            youtubeChannel.addObserver(subscriberA);
            youtubeChannel.addObserver(subscriberB);
            youtubeChannel.addObserver(subscriberC);

            youtubeChannel.releaseNewVideo("Design Patterns : Factory Method");
            youtubeChannel.releaseNewVideo("Design Patterns : Proxy");
            youtubeChannel.releaseNewVideo("Design Patterns : Visitor");
        }
    }


}
