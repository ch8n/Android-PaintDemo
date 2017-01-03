package com.ch8n.paintdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import petrov.kristiyan.colorpicker.ColorPicker;

public class MainActivity extends AppCompatActivity {

    private Button cleanButton;
    private Button undoButton;
    private Button redoButton;
    private Button saveButton;
    private Button shareButton;
    private Button colorButton;
    private SeekBar seekBar;
    private LinearLayout activity_main;
    private CanvasView mCanvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cleanButton = (Button) findViewById(R.id.cleanButton);
        undoButton = (Button) findViewById(R.id.undoButton);
        redoButton = (Button) findViewById(R.id.redoButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        colorButton = (Button) findViewById(R.id.colorButton);
        shareButton = (Button) findViewById(R.id.shareButton);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        activity_main = (LinearLayout) findViewById(R.id.activity_main);
        mCanvasView = (CanvasView) findViewById(R.id.canvasView);


        cleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvasView.onCleanCanvas();
            }
        });


        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvasView.onClickUndo();
            }
        });

        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvasView.onClickRedo();
            }
        });


        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPicker colorPicker = new ColorPicker(MainActivity.this);
                colorPicker.show();
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position,int color) {
                        // put code
                        mCanvasView.setPaintColor(color);
                        activity_main.setBackgroundColor(color);
                    }

                    @Override
                    public void onCancel(){
                        // put code
                    }
                });
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCanvasView.setDrawingCacheEnabled(true);
                mCanvasView.invalidate();
                String path = Environment.getExternalStorageDirectory().toString();
                OutputStream fOut = null;
                File file = new File(path, "android_drawing_app.png");
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                    fOut = new FileOutputStream(file);
                    if (mCanvasView.getDrawingCache() == null) {
                        Toast.makeText(MainActivity.this, "ERROR: " + "Unable to get drawing cache ", Toast.LENGTH_SHORT).show();
                    } else {
                        mCanvasView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    }
                    fOut.flush();
                    fOut.close();

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "ERROR: " + e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().toString();
                File file = new File(path, "android_drawing_app.png");
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                shareIntent.setType("image/png");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share image"));

            }
        });

        seekBar.setMax(20);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float progressVal=0f;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressVal= (float) progress;
                mCanvasView.setStrokeSize(progressVal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
