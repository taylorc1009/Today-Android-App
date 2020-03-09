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

class DatabaseUtilities { //implements List {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("alarms");

    List<Alarm> alarms = new ArrayList<>();
    private Alarm alarm;
    private boolean found = false;

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
        //updateLocal();
    }
    /*void updateLocal() {
        alarms.clear();
        DatabaseUtilities.FirebaseQuery firebaseQuery = new DatabaseUtilities.FirebaseQuery(myRef);
        final Task<DataSnapshot> load = firebaseQuery.start();
        load.addOnCompleteListener(new DatabaseUtilities.completeListener() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                super.onComplete(task);
                if(task.isSuccessful()) {
                    for (DataSnapshot snapshot : Objects.requireNonNull(load.getResult()).getChildren()) {
                        alarms.add(snapshot.getValue(Alarm.class));
                    }
                }
                else
                    alarms = null;
            }
        });
    }*/
    void store(Alarm alarm) {
        myRef.child(alarm.getId()).setValue(alarm);
        //updateLocal();
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
    boolean has(String id) {
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
                return true;
            }
        }
        return false;
    }
    void delete(String id) {
        myRef.child(id).removeValue();
    }
    /*@Override
    public int size() {
        return alarms.size();
    }
    @Override
    public boolean isEmpty() {
        return alarms.isEmpty();
    }
    @Override
    public boolean contains(@Nullable Object o) {
        for (Alarm al : alarms) {
            Log.i("id", al.getId());
            Alarm a = (Alarm) o;
            assert a != null;
            if (al.getId().equals(a.getId())) {
                return true;
            }
        }
        return false;
        //return alarms.contains(o);
    }
    @NonNull
    @Override
    public Iterator iterator() {
        return alarms.iterator();
    }
    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }
    @Override
    public boolean add(Object o) {
        return false;
    }
    @Override
    public boolean remove(@Nullable Object o) {
        return alarms.remove(o);
    }
    @Override
    public boolean addAll(@NonNull Collection c) {
        return alarms.addAll(c);
    }
    @Override
    public boolean addAll(int index, @NonNull Collection c) {
        return false;
    }
    @Override
    public void clear() {
        alarms.clear();
    }
    @Override
    public Object get(int index) {
        return alarms.get(index);
    }
    @Override
    public Object set(int index, Object element) {
        return alarms.set(index, (Alarm) element);
    }
    @Override
    public void add(int index, Object element) {
        alarms.add(index, (Alarm) element);
    }
    @Override
    public Object remove(int index) {
        return alarms.remove(index);
    }
    @Override
    public int indexOf(@Nullable Object o) {
        return alarms.indexOf(o);
    }
    @Override
    public int lastIndexOf(@Nullable Object o) {
        return alarms.lastIndexOf(o);
    }
    @NonNull
    @Override
    public ListIterator listIterator() {
        return alarms.listIterator();
    }
    @NonNull
    @Override
    public ListIterator listIterator(int index) {
        return alarms.listIterator(index);
    }
    @NonNull
    @Override
    public List subList(int fromIndex, int toIndex) {
        return alarms.subList(fromIndex, toIndex);
    }
    @Override
    public boolean retainAll(@NonNull Collection c) {
        return alarms.retainAll(c);
    }
    @Override
    public boolean removeAll(@NonNull Collection c) {
        return alarms.removeAll(c);
    }
    @Override
    public boolean containsAll(@NonNull Collection c) {
        return alarms.containsAll(c);
    }
    @NonNull
    @Override
    public Object[] toArray(@NonNull Object[] a) {
        return alarms.toArray(a);
    }*/

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
                if (task.isSuccessful()) {
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
