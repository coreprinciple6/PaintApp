package com.example.prettypaints.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

public class PrettyPaintsView extends View {

    public static final float TOUCH_TOLERANCE=10;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintScreen;
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;

    public PrettyPaintsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init(){
        paintScreen=new Paint();
        paintLine= new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(7);
        paintLine.setStrokeCap(Paint.Cap.ROUND);

        pathMap=new HashMap<>();
        previousPointMap= new HashMap<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap=Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas= new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
       canvas.drawBitmap(bitmap,0,0,paintScreen);

       for(Integer key: pathMap.keySet()){
           canvas.drawPath(pathMap.get(key),paintLine);
       }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action= event.getActionMasked();
        int actionIndex=event.getActionIndex();

        if(action==MotionEvent.ACTION_DOWN||action==MotionEvent.ACTION_UP){
            touchStarted(event.getX(actionIndex),event.getY(actionIndex),event.getPointerId(actionIndex));
            Log.d("Test: ", String.valueOf(event.getPointerId(actionIndex)));
        }

        else if(action==MotionEvent.ACTION_UP|| action==MotionEvent.ACTION_POINTER_UP){
            touchEnded(event.getPointerId(actionIndex));
        }

        else{
            touchMoved(event);
        }

        invalidate();//redraws the screen

        return true;
    }

    private void touchMoved(MotionEvent event){

        for(int i=0; i<event.getPointerCount();i++){
            int pointerId=event.getPointerId(i);
            int pointerIndex= event.findPointerIndex(pointerId);

            if(pathMap.containsKey(pointerId)){
                float newX= event.getX(pointerIndex);
                float newY= event.getY(pointerIndex);

               Path path= pathMap.get(pointerId);
               Point point= previousPointMap.get(pointerId);

               //Calculating distance
               float deltaX=Math.abs(newX-point.x);
               float deltaY= Math.abs(newY-point.y);

               //if distance moved is significant enough to be considered movement
               if(deltaX>= TOUCH_TOLERANCE||deltaY>=TOUCH_TOLERANCE){
                   //move the path to new location
                   path.quadTo(point.x,point.y,(newX+point.x)/2,(newY+point.y)/2);

                   //store new coordinates
                   point.x= (int) newX;
                   point.y= (int) newY;
               }

            }
        }

    }

    private void touchEnded(int pointerId) {

    }

    private void touchStarted(float x, float y, int pointerId) {
        Path path; //stores path for given touch
        Point point;//stores point for last point in path

        if(pathMap.containsKey(pointerId)){
            path= pathMap.get(pointerId);
            point=previousPointMap.get(pointerId);
        }

        else{
            path= new Path();
            pathMap.put(pointerId,path);
            point= new Point();
            previousPointMap.put(pointerId,point);
        }

        path.moveTo(x,y);
        point.x=(int) x;
        point.y=(int) y;

    }
}
