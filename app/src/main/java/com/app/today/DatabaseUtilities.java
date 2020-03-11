package com.app.today;

import android.util.Log;
import androidx.annotation.NonNull;
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

class DatabaseUtilities {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("alarms");

    List<Alarm> alarms = new ArrayList<>();
    private Alarm alarm;
    private boolean found = false;

    void store(Alarm alarm) {
        myRef.child(alarm.getId()).setValue(alarm);
        //updateLocal();
    }
    String newKey() {
        return myRef.push().getKey();
    }
    void delete(String id) {
        myRef.child(id).removeValue();
    }
    static class FirebaseQuery {

        private DatabaseReference ref;

        FirebaseQuery(DatabaseReference ref) {
            this.ref = ref;
        }

        Task<DataSnapshot> start() {
            Task<DataSnapshot> task;
            final TaskCompletionSource<DataSnapshot> source = new TaskCompletionSource<>();
            final ValueEventListener listener = new EventListener(source);
            ref.addListenerForSingleValueEvent(listener);
            task = source.getTask();
            return task.continueWithTask(new Continuation<DataSnapshot, Task<DataSnapshot>>() {
                @Override
                public Task<DataSnapshot> then(@NonNull Task<DataSnapshot> task) {
                    return task;
                }
            });
        }

        static class EventListener implements ValueEventListener {
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
    static class completeListener extends DatabaseUtilities implements OnCompleteListener<DataSnapshot> {
        @Override
        public void onComplete(@NonNull Task<DataSnapshot> task) {
            try {
                if(task.isSuccessful()) {
                    //final DataSnapshot result = task.getResult();
                    for(DataSnapshot snapshot : task.getResult().getChildren())
                        alarms.add(snapshot.getValue(Alarm.class));
                }
                else
                    throw Objects.requireNonNull(task.getException());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
