package com.sergiocruz.capstone.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.sergiocruz.capstone.model.Comment;
import com.sergiocruz.capstone.model.Status;
import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.TravelStar;
import com.sergiocruz.capstone.repository.Repository;

import java.util.List;

public class TravelDetailsViewModel extends AndroidViewModel {
    private Repository repository;
    private Travel travel;
    private MutableLiveData<List<Comment>> commentListLiveData = new MutableLiveData<>();
    private MutableLiveData<Long> numComments = new MutableLiveData<>();
    private MutableLiveData<TravelStar> travelStarsLiveData = new MutableLiveData<>();
    private MutableLiveData<Status> status = new MutableLiveData<>();

    public TravelDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public void setRepository(Repository repository) {
        if (this.repository == null) {
            this.repository = repository;
        }
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    //Original function
    public MutableLiveData<List<Comment>> getCommentsForTravelID() {
        if (commentListLiveData.getValue() == null) {
            commentListLiveData = repository.getCommentsForTravelID(getTravel().getID());
        }
        return commentListLiveData;
    }

//    public MutableLiveData<List<Comment>> getCommentsForTravelID() {
//        if (commentListLiveData.getValue() == null) {
//
//            String travelID = getTravel().getID();
//            FirebaseDatabase
//                    .getInstance()
//                    .getReference()
//                    .child(TRAVEL_PACK_COMMENTS_REF)
//                    .child(travelID)
//                    .orderByChild("date")
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            List<Comment> commentList = new ArrayList<>();
//                            if (dataSnapshot.hasChildren()) {
//                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                    Comment comment = snapshot.getValue(Comment.class);
//                                    commentList.add(comment);
//                                }
//                            }
//                            commentListLiveData.postValue(commentList);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//        }
//        return commentListLiveData;
//    }
//
//    public void CommentsLiveData(DatabaseReference databaseReference) {
//
////        this.query = databaseReference.orderByChild("date"); // .limitToLast(10);
//    }

    public Travel getTravel() {
        return travel;
    }

    public MutableLiveData<TravelStar> getStars() {
        if (travelStarsLiveData == null) {
            travelStarsLiveData = repository.getStarsForTravelID(getTravel().getID());
        }
        return travelStarsLiveData;
    }

    public MutableLiveData<Long> getNumComments() {
        if (numComments == null) {
            numComments = repository.getNumCommentsForTravelID(getTravel().getID());
        }
        return numComments;
    }

    public LiveData<Status> getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status.setValue(status);
    }

    public Boolean saveToFavorites(Travel selectedTravel) {
        return repository.saveTravelToFavorites(selectedTravel);
    }

    public void removeFromFavorites(String travelID) {
        repository.removeTravelFromFavorites(travelID);
    }

}
