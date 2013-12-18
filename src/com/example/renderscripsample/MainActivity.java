package com.example.renderscripsample;

import java.io.IOException;
import java.nio.CharBuffer;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.Menu;
import android.widget.ImageView;

public class MainActivity extends Activity {

	Bitmap photo = null;
	Bitmap dst_photo = null;
	
	ImageView imageView = null;
	
	float blurRadius = 5.0f;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        photo = BitmapFactory.decodeResource(getResources(), R.drawable.fc012);
        imageView = (ImageView)findViewById(R.id.imageView1);
        imageView.setImageBitmap(photo);
        
        Thread thread = new Thread(new ImageBlurRunnable());
        thread.start();
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    class ImageBlurRunnable implements Runnable{

		@Override
		public void run() {
				final RenderScript rs = RenderScript.create( MainActivity.this );
			final Allocation input = Allocation.createFromBitmap( rs, photo, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
			final Allocation output = Allocation.createTyped( rs, input.getType() );
			final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create( rs, Element.U8_4( rs ) );
			script.setRadius( blurRadius/* e.g. 3.f */ );
			script.setInput( input );
			script.forEach( output );
			output.copyTo( dst_photo);
			
			Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					imageView.setImageBitmap(dst_photo);
				}
			});
		}
    	
    }
    
}
