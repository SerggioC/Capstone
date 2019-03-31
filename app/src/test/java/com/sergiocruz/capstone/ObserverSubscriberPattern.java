package com.sergiocruz.capstone;

import org.junit.Test;

import java.util.ArrayList;

public class ObserverSubscriberPattern {


    @Test
    public void main() {
        YoutubeChannel youtubeChannel = new YoutubeChannel();

        YoutubeSubscriber subscriberA = new YoutubeSubscriber(youtubeChannel);
        YoutubeSubscriber subscriberB = new YoutubeSubscriber(youtubeChannel);
        YoutubeSubscriber subscriberC = new YoutubeSubscriber(youtubeChannel);

        youtubeChannel.releaseNewVideo("Design Patterns : Factory Method");
        youtubeChannel.releaseNewVideo("Design Patterns : Proxy");
        youtubeChannel.releaseNewVideo("Design Patterns : Visitor");
    }

    public interface ObservableYoutubeChannel {
        void addObserver(YoutubeSubscriber observer);

        void removeObserver(YoutubeSubscriber observer);

        void notifyObserver(String video);
    }

    interface YoutubeObserver {
        void update(String video);
    }

    public class YoutubeChannel implements ObservableYoutubeChannel {
        private ArrayList<YoutubeSubscriber> observers = new ArrayList<>();

        @Override
        public void addObserver(YoutubeSubscriber observer) {
            observers.add(observer);
        }

        @Override
        public void removeObserver(YoutubeSubscriber observer) {
            observers.remove(observer);
        }

        void releaseNewVideo(String video) {
            System.out.println("Release new video : " + video);
            notifyObserver(video);
        }

        @Override
        public void notifyObserver(String video) {
            for (YoutubeSubscriber observer : observers) {
                observer.update(video);
            }
        }

    }

    public class YoutubeSubscriber implements YoutubeObserver {

        YoutubeSubscriber(YoutubeChannel youtubeChannel) {
            youtubeChannel.addObserver(this);
        }

        @Override
        public void update(String video) {
            System.out.println("New video on channel! " + video);
        }

    }


}
