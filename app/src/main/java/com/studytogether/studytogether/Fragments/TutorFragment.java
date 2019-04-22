package com.studytogether.studytogether.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studytogether.studytogether.Adapters.GroupAdapter;
import com.studytogether.studytogether.Models.Group;
import com.studytogether.studytogether.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TutorFragment extends Fragment {

    // Declare a interaction listener for URI
    private OnFragmentInteractionListener mListener;

    // Declare a searchView for the filter at appBar
    private SearchView searchView = null;
    // Declare a queryTextListener to take a query from a User
    private SearchView.OnQueryTextListener queryTextListener;

    // Declare to set up the groupAdapter
    RecyclerView groupRecyclerView;
    // Declare the groupAdapter, and it holds groupList
    // The groupAdapter needs for this fragment because it's groupList will be showed in TutorFragment
    GroupAdapter groupAdapter;
    // Declare to hold groups
    List<Group> groupList;

    // Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference ;

    // Constructor of TutorFragment
    public TutorFragment() {
        // Required empty public constructor
    }

    // OnCreate for the first call of this fragment of activity

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setHasOptionsMenu(true);
    }

    // Search Filter, working on appBar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Get groupList from the database
        updateList();
        // Get the menu item: search
        MenuItem searchItem = menu.findItem(R.id.action_search);
        // Set up searchManager
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        // Check the item condition
        if (searchItem != null) {
            // If item is null, reset the item
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            // Connect the searchView item with searchManager
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            // Get a query from a User
            queryTextListener = new SearchView.OnQueryTextListener() {
                // onDataChange detect user's query as real time
                @Override
                public boolean onQueryTextChange(final String query) {
                    // Check groupAdapter
                    if (groupAdapter == null) {
                        Toast.makeText(getContext(), "GroupAdapter is null", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    //Filtering groupList
                    //User can search by group's name, group's place, and group's goal in Tutor lists
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            // Reset the groupList
                            groupList = new ArrayList<>();
                            // Take groupList from database and loop through whole groups
                            for (DataSnapshot groupsnap: dataSnapshot.getChildren()) {

                                // Grab each group
                                Group group = groupsnap.getValue(Group.class);
                                // If groupName, groupPlace, or groupGoal holds query
                                if (group.getGroupName().toLowerCase().contains(query.toLowerCase()) || group.getGroupPlace().toLowerCase().contains(query.toLowerCase()) || group.getGroupGoal().toLowerCase().contains(query.toLowerCase())) {
                                    // Add only tutor groups
                                    if (group.getTutor().contains("true")) {
                                        //  Add the group in groupList
                                        groupList.add(group);
                                    }
                                }
                            }
                            // Reverse the groupList to see the recently added groups on top
                            Collections.reverse(groupList);

                            // Set recyclerView through groupAdapter
                            groupAdapter = new GroupAdapter(getActivity(),groupList);
                            groupRecyclerView.setAdapter(groupAdapter);
                        }

                        // When the database doesn't response
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    return true;
                }
                // This function is called when a user press the return button after type some query
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            };
            // Pass the query into searchView
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Option for items on appBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return false;
            default:
                break;
        }
        // Pass the query into searchView
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    // onCreateView creates and returns the view hierarchy associated with the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_tutor, container, false);
        // Set up recyclerView
        groupRecyclerView  = fragmentView.findViewById(R.id.tutorRV);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupRecyclerView.setHasFixedSize(true);

        // Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        // Take a reference of Groups
        databaseReference = firebaseDatabase.getReference("Groups");
        return fragmentView ;
    }


    @Override
    public void onStart() {
        super.onStart();

        // Get List Posts from the database
        updateList();
    }

    // Update groupList
    private void updateList() {
        // Update groupList when a group is added
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // tutoringFilter Flag
                String tutoringFilter = "true";

                // Reinitialize the groupList
                groupList = new ArrayList<>();
                // Loop whole groups
                for (DataSnapshot groupsnap: dataSnapshot.getChildren()) {

                    Group group = groupsnap.getValue(Group.class);
                    // If the group is for tutoring,
                    if(group.getTutor().toLowerCase().contains(tutoringFilter.toLowerCase())){
                        // Add the group
                        groupList.add(group);
                    }
                }
                // Reverse the groupList to see the recently added groups on top
                Collections.reverse(groupList);

                // Set recyclerView using groupAdapter
                groupAdapter = new GroupAdapter(getActivity(),groupList);
                groupRecyclerView.setAdapter(groupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}