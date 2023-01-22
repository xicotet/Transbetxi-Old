package com.example.transbetxi.ui.main.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    public static final int BUTTON_WIDTH = 200;
    private static Context mContext;
    public Queue<Integer> recoverQueue;
    private RecyclerView recyclerView;
    private List<UnderlayButton> buttons;
    private GestureDetector gestureDetector;
    private int swipedPos = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List<UnderlayButton>> buttonsBuffer;
    private boolean firstSwipeHasOccurred;//To prevent the swipe simulation (createItemSwipeAnimation()) from starting if a user has swiped before
    public View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent e) {
            Log.i("arasi", "onTouchComenç " + e.getX());
            if (swipedPos < 0) {
                firstSwipeHasOccurred = true;
                return false;


            }
            Log.i("arasi", "onTouchDespresIf " + e.getRawY());
            Point point = new Point((int) e.getRawX(), (int) e.getRawY());

            RecyclerView.ViewHolder swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos);
            View swipedItem = swipedViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);

            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {

                if (rect.top < point.y && rect.bottom > point.y) {
                    Log.i("onTouchDespresIf", "arriba");
                    gestureDetector.onTouchEvent(e);
                } else {
                    recoverQueue.add(swipedPos);
                    swipedPos = -1;
                    recoverSwipedItem();
                }
            }
            return false;
        }
    };
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("arasi", "gestureListener " + swipedPos);
            for (UnderlayButton button : buttons) {
                if (button.onClick(e.getX(), e.getY()))
                    break;
            }

            return true;
        }
    };
    private LinkedHashMap<Integer, Float> dsf = new LinkedHashMap<>();
    private LinkedHashMap<Integer, Boolean> swipingLeft = new LinkedHashMap<>();

    public SwipeHelper(Context context, RecyclerView recyclerView) {
        super(0, ItemTouchHelper.LEFT);
        Log.i("arasi", "constructor");
        this.recyclerView = recyclerView;
        this.mContext = context;
        this.buttons = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        firstSwipeHasOccurred = false;

        buttonsBuffer = new HashMap<>();
        recoverQueue = new LinkedList<Integer>() {
            @Override
            public boolean add(Integer o) {
                if (contains(o))
                    return false;
                else
                    return super.add(o);
            }
        };

        attachSwipe();
    }

    public int getSwipedPos() {
        return swipedPos;
    }

    public boolean getFirstSwipeHasOccurred() {
        return firstSwipeHasOccurred;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Log.i("arasi", "onMove " + +swipedPos);
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.i("arasi", "onSwipped " + viewHolder.getAbsoluteAdapterPosition());
        int pos = viewHolder.getAbsoluteAdapterPosition();

        if (swipedPos != pos)
            recoverQueue.add(swipedPos);
        Log.i("valencianistaSwiped", String.valueOf(recoverQueue));
        swipedPos = pos;

        if (buttonsBuffer.containsKey(swipedPos))
            buttons = buttonsBuffer.get(swipedPos);
        else
            buttons.clear();

        buttonsBuffer.clear();
        swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
        recoverSwipedItem();

    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        int pos = viewHolder.getAbsoluteAdapterPosition();

        Log.i("arasi OnChilddraaw", pos + " " + recyclerView.getChildAt(pos).getX() + " " + swipedPos);
        float translationX = dX;
        View itemView = viewHolder.itemView;

        if (pos < 0) {
            swipedPos = pos;

            return;
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                List<UnderlayButton> buffer = new ArrayList<>();

                if (!buttonsBuffer.containsKey(pos)) {
                    instantiateUnderlayButton(viewHolder, buffer);
                    buttonsBuffer.put(pos, buffer);
                } else {
                    buffer = buttonsBuffer.get(pos);
                }

                translationX = dX * buffer.size() * BUTTON_WIDTH / itemView.getWidth();
                drawButtons(c, itemView, buffer, pos, translationX);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private synchronized void recoverSwipedItem() {
        Log.i("arasi recoverSwipeQ: ", "entra al metodo" + String.valueOf(recoverQueue));
        if (recyclerView.getChildAt(0).getX() < 0 && swipedPos != 0) {
            Log.i("arasi recovering", "entra");
            recyclerView.getAdapter().notifyItemChanged(0);
        }
        while (!recoverQueue.isEmpty()) {
            int pos = recoverQueue.poll();
            Log.i("arasi recoverSwipeQ: ", String.valueOf(recoverQueue));
            if (pos > -1) {
                Log.i("arasi recoverSwipe: ", String.valueOf(pos) + " " + swipedPos);
                recyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
    }

    private void drawButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX) {
        Log.i("arasi", "drawButtons " + recyclerView.getChildAdapterPosition(itemView));
        float right = itemView.getRight();
        float dButtonWidth = (-1) * dX / buffer.size();
        for (UnderlayButton button : buffer) {
            int buttonNumber = buffer.indexOf(button);
            float left = right - dButtonWidth;
            button.onDraw(
                    c,
                    new RectF(
                            left,
                            itemView.getTop(),
                            right,
                            itemView.getBottom()
                    ),
                    pos,
                    buttonNumber
            );

            right = left;
        }
    }

    public void attachSwipe() {
        Log.i("arasi", "attachSwipe");
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public abstract void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons);

    public interface UnderlayButtonClickListener {
        void onClick(int pos);
    }

    public static class UnderlayButton {
        private String text;
        private int imageResId;
        private int color;
        private int pos;
        private RectF clickRegion;
        private UnderlayButtonClickListener clickListener;

        public UnderlayButton(String text, int imageResId, int color, UnderlayButtonClickListener clickListener) {
            Log.i("arasi", "UnderlayButton ");
            this.text = text;
            this.imageResId = imageResId;
            this.color = color;
            this.clickListener = clickListener;
        }

        public boolean onClick(float x, float y) {
            Log.i("arasi", "UnderlayButton - onClick");
            if (clickRegion != null && clickRegion.contains(x, y)) {
                clickListener.onClick(pos);
                return true;
            }

            return false;
        }

        public void onDraw(Canvas c, RectF rect, int pos, int buttonNumber) {
            Log.i("arasi", "onDraw ");
            Paint p = new Paint();
            float cornerRadius = 50;
            // Draw background
            p.setColor(color);

            if (buttonNumber == 2) {
                Log.i("izquierda", String.valueOf(rect.right - 2));
                //Boton izquierda del todo
                RectF inner = new RectF(rect.right - 2, rect.top, rect.right, rect.bottom);
                float[] radii = {cornerRadius, cornerRadius, 0, 0, 0, 0, cornerRadius, cornerRadius}; //x, y corner radii for top left, top right, bottom right, bottom left corners
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    c.drawDoubleRoundRect(rect, radii, inner, radii, p);
                } else {
                    c.drawRoundRect(rect, cornerRadius, cornerRadius, p);
                }
            } else if (buttonNumber == 0) {
                //Boton derecha del todo
                RectF inner = new RectF(rect.left, rect.top, rect.left + 2, rect.bottom);
                float[] radii = {0, 0, cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0, 0};//x, y corner radii for top left, top right, bottom right, bottom left corners
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    c.drawDoubleRoundRect(rect, radii, inner, radii, p);
                } else {
                    c.drawRoundRect(rect, cornerRadius, cornerRadius, p);
                }
            } else {
                c.drawRect(rect, p);
            }

            Context context = mContext;
            Resources resources = context.getResources();
            Drawable drawable = resources.getDrawable(imageResId);

            // Set the bounds of the drawable
            int distance = (int) ((rect.right - rect.left - drawable.getIntrinsicWidth()) / 2);
            int verticalDistance = (int) ((((rect.bottom - rect.top) / 2) - (drawable.getIntrinsicHeight())) / 2);
            int left = (int) (rect.left + distance);
            int top = (int) rect.top + verticalDistance + 30; // 30 es un offset para imagen y texto que me invento
            int right = (int) (rect.right - distance);
            int bottom = (int) (top + drawable.getIntrinsicHeight());
            drawable.setBounds(left, top, right, bottom);

            // Draw the drawable on the canvas
            c.save();
            drawable.draw(c);

// Draw Text
            p.setColor(Color.BLACK);
            p.setTextSize(Resources.getSystem().getDisplayMetrics().density * 14);

            Rect r = new Rect();
            float cHeight = rect.height();
            float cWidth = rect.width();
            p.setTextAlign(Paint.Align.LEFT);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "oswald.ttf");
            p.setTypeface(typeface);

            p.getTextBounds(text, 0, text.length(), r);
            float x = cWidth / 2f - r.width() / 2f - r.left;
            float y = cHeight / 2f + r.height() / 2f - r.bottom;


// Draw the text on the canvas
            c.drawText(text, rect.left + x, rect.top + y + 50, p); //50 es un offset vertical
            c.restore();

            clickRegion = rect;
            this.pos = pos;
        }
    }
}