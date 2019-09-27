package com.example.prettypaints;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.prettypaints.view.PrettyPaintsView;

import yuku.ambilwarna.AmbilWarnaDialog;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class MainActivity extends AppCompatActivity {

    private PrettyPaintsView prettypaintsView;
    private AlertDialog.Builder currentAlertDialog;
    private ImageView widthImageView;
    private AlertDialog dialogLineWidth;
    int mDefaultColor=BLACK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prettypaintsView=findViewById(R.id.view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.clearid:
                prettypaintsView.clear();
                break;

            case  R.id.saveid:
                prettypaintsView.saveToInternalStorage();
                break;

            case R.id.colorid:
                showColorDialog();
                break;

            case R.id.lineWidthId:
                showLineWidthDialog();
                break;

            case R.id.eraseid:
                onErase();
                break;
        }
        if(item.getItemId()== R.id.clearid){
            prettypaintsView.clear();
        }
        return super.onOptionsItemSelected(item);
    }


    void onErase(){
        prettypaintsView.setDrawingColor(WHITE);
    }

    void showColorDialog(){
        AmbilWarnaDialog colorPicker= new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefaultColor=color;
                prettypaintsView.setDrawingColor(mDefaultColor);
            }
        });
        colorPicker.show();
    }

    void showLineWidthDialog(){
        currentAlertDialog= new AlertDialog.Builder(this);
        View view= getLayoutInflater().inflate(R.layout.width_dialog,null);
        final SeekBar widthSeekBar= view.findViewById(R.id.widthSeekBar);
        widthImageView=view.findViewById(R.id.imageViewid);
        Button setLineWidthButton= view.findViewById(R.id.widthDialogButton);

        setLineWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prettypaintsView.setLineWidth(widthSeekBar.getProgress());
                dialogLineWidth.dismiss();
                currentAlertDialog= null;
            }
        });

        widthSeekBar.setOnSeekBarChangeListener(widthSeekBarChange);

        currentAlertDialog.setView(view);
        dialogLineWidth= currentAlertDialog.create();
        dialogLineWidth.show();
    }

    private SeekBar.OnSeekBarChangeListener widthSeekBarChange=new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap= Bitmap.createBitmap(400,100, Bitmap.Config.ARGB_8888);
        Canvas canvas= new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Paint p= new Paint();
            p.setColor(prettypaintsView.getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);

            bitmap.eraseColor(Color.WHITE);
            canvas.drawLine(30,50,370,50,p);
            widthImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
