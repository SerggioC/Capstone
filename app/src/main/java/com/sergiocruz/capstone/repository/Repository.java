package com.sergiocruz.capstone.repository;

public class Repository {
    public static String userName = "the name";
    private static Repository sInstance;

    private final FirebaseRepository remoteRepository;
    private final LocalRepository localRepository;

    private Repository(FirebaseRepository remoteRepository, LocalRepository localRepository) {
        this.remoteRepository = remoteRepository;
        this.localRepository = localRepository;
    }

    public static Repository getInstance() {
        if (sInstance == null) {
            sInstance = new Repository(FirebaseRepository.getInstance(), LocalRepository);
        }
        return sInstance;
    }

    public String getUser() {
        remoteRepository.getDBUserRef();
    }


}
