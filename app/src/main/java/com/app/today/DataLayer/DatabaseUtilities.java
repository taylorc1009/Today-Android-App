package com.app.today.DataLayer;

import android.util.Log;
import androidx.annotation.NonNull;

import com.app.today.BusinessLayer.Alarm;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseUtilities {
    //Initialize a Firebase database reference to allow us to contact it
    //This is a NoSQL database which stores data in the format of .json files,
    //so we will need to use code here to query it
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference myRef = database.getReference("alarms");

    List<Alarm> alarms = new ArrayList<>();

    //Method used to store an alarm in the child path which matches the passed alarm's ID
    public void store(Alarm alarm) {
        myRef.child(alarm.getId()).setValue(alarm);
    }

    //Used to return a new key of (and also create) a new child in the database
    public String newKey() {
        return myRef.push().getKey();
    }

    //Used to remove the value of the child path specified
    public void delete(String id) {
        myRef.child(id).removeValue();
    }

    //This is where we create our query task to be executed
    //We need to make querying the database for results into a task as the app
    //will not wait for the results to be pulled before continuing
    public static class FirebaseQuery {
        //We first initialise the reference which we want to query
        private DatabaseReference ref;
        public FirebaseQuery(DatabaseReference ref) {
            this.ref = ref;
        }

        //Then we build the task we want to start
        public Task<DataSnapshot> start() {
            Task<DataSnapshot> task;

            //Specifies the result we need from the task, in our case a DataSnapshot from the database
            final TaskCompletionSource<DataSnapshot> source = new TaskCompletionSource<>();

            //Adds a listener to the task so we can later check when the task is completed
            final ValueEventListener listener = new EventListener(source);
            ref.addListenerForSingleValueEvent(listener);

            //Retrieves the task we want to execute
            task = source.getTask();

            //This is the task of querying the database itself, as you can see the Continuation will return a new
            //Task with type DataSnapshot for us to perform another procedure on that, i.e. it returns a new Task
            //for us to handle the result of this Task
            return task.continueWithTask(new Continuation<DataSnapshot, Task<DataSnapshot>>() {
                @Override
                public Task<DataSnapshot> then(@NonNull Task<DataSnapshot> task) {
                    Log.i("! FirebaseQuery Task complete", Objects.requireNonNull(task.getResult()).toString());
                    return task;
                }
            });
        }

        public static class EventListener implements ValueEventListener {
            //Implements the properties of ValueEventListener, which are: first we tell it which task to listen to,
            //then return a result which may either be a DataSnapshot, in out case, or an exception if it's cancelled
            private final TaskCompletionSource<DataSnapshot> taskSource;
            EventListener(TaskCompletionSource<DataSnapshot> taskSource) {
                this.taskSource = taskSource;
            }
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskSource.setResult(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskSource.setException(databaseError.toException());
            }
        }
    }

    //Simply as it looks, this method is invoked upon Task completion, in our activities we
    //override this method to do what we need to do on completion, if not then we can use the
    //existing list in this instance of DatabaseUtilities
    public static class completeListener extends DatabaseUtilities implements OnCompleteListener<DataSnapshot> {
        @Override
        public void onComplete(@NonNull Task<DataSnapshot> task) {
            try {
                if(task.isSuccessful())
                    for(DataSnapshot snapshot : Objects.requireNonNull(task.getResult()).getChildren())
                        alarms.add(snapshot.getValue(Alarm.class));
                else
                    throw Objects.requireNonNull(task.getException());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
