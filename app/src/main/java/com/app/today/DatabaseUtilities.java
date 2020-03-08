package com.app.today;

/*import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;*/
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

class DatabaseUtilities { //extends SQLiteOpenHelper {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("alarms");

    List<Alarm> alarms = new ArrayList<>();
    private Alarm alarm;
    private boolean found = false;

    /*public interface OnGetDataListener {
        //this is for callbacks
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure();
    }
    public void readData(final OnGetDataListener listener) {
        listener.onStart();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                listener.onFailure();
            }
        });
    }*/

    DatabaseUtilities() {
        /*myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Alarm alarm = dataSnapshot.getValue(Alarm.class);
                assert alarm != null;
                Log.d("! Firebase request success", myRef.getKey() + ", " + alarm.toString());
                alarms.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    alarms.add(snapshot.getValue(Alarm.class));
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                alarms.clear();
                Log.w("? Firebase database", "failed to read value", error.toException());
            }
        });*/
    }

    void store(Alarm alarm) {
        myRef.child(alarm.getId()).setValue(alarm);
    }
    String newKey() {
        return myRef.push().getKey();
    }
    List<Alarm> get() {
        /*myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                alarms.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Alarm alarm = snapshot.getValue(Alarm.class);
                    alarms.add(alarm);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                alarms.clear();
                Log.e("? database pull cancelled", "alarms retrieved before cancellation = " + alarms.size(), databaseError.toException());
            }
        });*/
        if(!alarms.isEmpty())
            return alarms;
        else
            return null;
    }
    Alarm get(String id) {
        alarm = null;
        /*myRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alarm = dataSnapshot.getValue(Alarm.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                alarms.clear();
                Log.e("? database pull cancelled", "alarms retrieved before cancellation = " + alarms.size(), databaseError.toException());
            }
        });
        Log.i("??? alarm returned?", alarm.getId());*/
        //myRef.child(id).
        //return alarm;
        for (Alarm al : alarms)
            if (al.getId().equals(id))
                return al;
        return null;
    }
    boolean has(final String id) {
        found = false;
        /*myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(id)) {
                    Alarm alarm = dataSnapshot.getValue(Alarm.class);
                    Log.i("? alarm found?", alarm.getId() + " (null = no/false)");
                    found = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                found = false;
            }
        });*/
        /*Query query = myRef.equalTo("id", id);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Alarm alarm = dataSnapshot.getValue(Alarm.class);
                Log.i("test", alarm.getId());
                found = alarm != null;
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });*/
        for (Alarm al : alarms) {
            Log.i("id", al.getId());
            if (al.getId().equals(id)) {
                Log.i("FOUND", "WTFTFTFTFTFTFTFT");
                return true;
            }
        }
        return false;
    }
    void delete(String id) {
        myRef.child(id).removeValue();
    }
    static class FirebaseQuery {

        //private final HashSet<DatabaseReference> refs = new HashSet<>();
        //private final HashMap<DatabaseReference, DataSnapshot> snaps = new HashMap<>();
        //private final HashMap<DatabaseReference, ValueEventListener> listeners = new HashMap<>();

        private DatabaseReference ref;

        FirebaseQuery(DatabaseReference ref) {
            this.ref = ref;
        }

        Task<DataSnapshot> start() {
            Task<DataSnapshot> task;
            final TaskCompletionSource<DataSnapshot> source = new TaskCompletionSource<>();
            final ValueEventListener listener = new EventListener(ref, source);
            ref.addListenerForSingleValueEvent(listener);
            task = source.getTask();
            return task.continueWithTask(new Continuation<DataSnapshot, Task<DataSnapshot>>() {
                @Override
                public Task<DataSnapshot> then(@NonNull Task<DataSnapshot> task) {
                    Log.i("task continuation completed???", String.valueOf(Objects.requireNonNull(task.getResult()).getChildrenCount()));
                    return task;
                }
            });
        }

        /*public void stop() {
            /*for (final Map.Entry<DatabaseReference, ValueEventListener> entry : listeners.entrySet()) {
                entry.getKey().removeEventListener(entry.getValue());
            }
            snap = null;
            //listeners = null;
        }*/

        static class EventListener implements ValueEventListener {
            private final DatabaseReference ref;
            private final TaskCompletionSource<DataSnapshot> taskSource;

            EventListener(DatabaseReference ref, TaskCompletionSource<DataSnapshot> taskSource) {
                this.ref = ref;
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
    static class completeListener implements OnCompleteListener<DataSnapshot> {
        @Override
        public void onComplete(@NonNull Task<DataSnapshot> task) {
            try {
                if (task.isSuccessful()) {
                    final DataSnapshot result = task.getResult();
                    // Look up DataSnapshot objects using the same DatabaseReferences you passed into FirebaseMultiQuery
                }
                else
                    throw Objects.requireNonNull(task.getException());
                    // log the error or whatever you need to do
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
