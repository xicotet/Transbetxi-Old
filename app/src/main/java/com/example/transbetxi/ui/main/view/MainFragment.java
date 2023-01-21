package com.example.transbetxi.ui.main.view;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.Instrumentation;
import android.gesture.Gesture;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.transbetxi.R;
import com.example.transbetxi.data.RallyStage;
import com.example.transbetxi.databinding.FragmentMainBinding;
import com.example.transbetxi.ui.main.rvAdapter.RallyStageAdapter;
import com.example.transbetxi.ui.main.viewmodel.MainViewModel;


import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private RecyclerView rallyStageRecyclerView;
    private RallyStageAdapter adapter;
    private int prueba = 3;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        ((MainActivity) requireActivity()).getSupportActionBar().hide();
        // TODO: Use the ViewModel
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        rallyStageRecyclerView = view.findViewById(R.id.rally_stage_recycler_view);
        rallyStageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<RallyStage> rallyStages = getRallyStages(); // retrieve the list of rally stages

        adapter = new RallyStageAdapter(rallyStages);


        //Cuando se hace click en un elemento del recyclerView
        adapter.setOnItemClickListener(new RallyStageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Open the StageMapFragment when an item is clicked

                StageMapFragment fragment = new StageMapFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });


        SwipeHelper swipeHelper = new SwipeHelper(getContext(), rallyStageRecyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new UnderlayButton(
                        "Fotos",
                        R.drawable.ic_baseline_add_a_photo_24,
                        Color.parseColor("#B8C0AD"),
                        new UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: onDelete
                            }
                        }
                ));

                underlayButtons.add(new UnderlayButton(
                        "Resultados",
                        R.drawable.ic_baseline_timer_24,
                        Color.parseColor("#F7C3A3"),
                        new UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnTransfer
                            }
                        }
                ));
                underlayButtons.add(new UnderlayButton(
                        "Cómo llegar",
                        R.drawable.ic_baseline_directions_24,
                        Color.parseColor("#F5E7DF"),
                        new UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnUnshare
                            }
                        }
                ));
            }
        };

        rallyStageRecyclerView.setAdapter(adapter);
        createStartSwipeAnimation(true);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void createStartSwipeAnimation(boolean swipeLeft) {
        rallyStageRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                float startX =  rallyStageRecyclerView.getChildAt(0).getRight();
                float startY = rallyStageRecyclerView.getChildAt(0).getY();
                float endX = rallyStageRecyclerView.getChildAt(0).getX();
                float endY = startY;

                if (!swipeLeft) {
                    float swap = startX;
                    startX = endX;
                    endX = swap;
                }

                int steps = 20;
                float xStep = ((endX - startX) / steps);
                float yStep = 0;

                long start = SystemClock.uptimeMillis();
                long downTime = SystemClock.uptimeMillis();
                long eventTime = SystemClock.uptimeMillis() + 50;

                MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, startX, startY, 0);
                rallyStageRecyclerView.dispatchTouchEvent(event);

                for (int i = 0; i < steps; i++) {
                    eventTime = SystemClock.uptimeMillis() + (i + 1) * 100;
                    event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, startX + (i * xStep), startY, 0);
                    rallyStageRecyclerView.dispatchTouchEvent(event);

                }
                event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, endX, endY, 0);
                rallyStageRecyclerView.dispatchTouchEvent(event);

                long end = SystemClock.uptimeMillis();
                Log.i("tiempo", String.valueOf((end - start)));
            }});
    }

    private List<RallyStage> getRallyStages() {
        RallyStage tcp = new RallyStage("Circuit Camí de Monserrat",
                "00:00",
                "TCP",
                "1.5km");
        RallyStage tc1 = new RallyStage("Sagrat Cor - Rodaor",
                "10:00",
                "TC1",
                "4.5km");
        RallyStage tc2 = new RallyStage("Camí San Francesc – Vinyes de Piquer",
                "11:00",
                "TC2",
                "3km");
        RallyStage tc3 = new RallyStage("Camí Exagres – Camí Monserrat",
                "12:00",
                "TC3",
                "5km");
        RallyStage tc4 = new RallyStage("Muntanyeta Sant Antoni",
                "16:00",
                "TC4",
                "2.5km");

        RallyStage tc5 = new RallyStage("Camí Valencia – Camí Artana",
                "17:00",
                "TC5",
                "5km");

        List<RallyStage> rallyStages = new ArrayList<RallyStage>();
        rallyStages.add(tcp);
        rallyStages.add(tc1);
        rallyStages.add(tc2);
        rallyStages.add(tc3);
        rallyStages.add(tc4);
        rallyStages.add(tc5);

        return rallyStages;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}