package hackfest.overlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ChooseOverlayActivity extends ActionBarActivity {

    @InjectView(R.id.overlay) ImageView mOverlayPng;
    @InjectView(R.id.selected_photo) ImageView mSelectedPhoto;

    private final String TAG = ChooseOverlayActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_overlay);
        ButterKnife.inject(this);
        populateGallery();
        saveOverlayedImage();
    }

    private void saveOverlayedImage() {
        // processes two images, merges them and saves the result
        Bitmap selectedPhotoBitmap = imageViewToBitmap(mSelectedPhoto);
        Bitmap overlayBitmap = imageViewToBitmap(mOverlayPng);
        Bitmap bmOverlay = Bitmap.createBitmap(selectedPhotoBitmap.getWidth(),
                selectedPhotoBitmap.getHeight(), selectedPhotoBitmap.getConfig());
        Canvas canvas = new Canvas();
        canvas.setBitmap(bmOverlay);
        canvas.drawBitmap(selectedPhotoBitmap, new Matrix(), null);
        canvas.drawBitmap(overlayBitmap, new Matrix(), null);
        canvas.save();

        // saving the bitmap
        try {
            String fileName = Environment.getExternalStorageDirectory().toString();
            File overlayedImageFile = new File(fileName, "overlayedImage.png");
            overlayedImageFile.createNewFile();
            Log.i(TAG, "file created " + overlayedImageFile.toString());
            FileOutputStream out = new FileOutputStream(overlayedImageFile);
            bmOverlay.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (IOException e) {
            Log.e(TAG, "ERROR");
            e.printStackTrace();
        }
    }

    private Bitmap imageViewToBitmap(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        imageView.layout(0, 0, imageView.getMeasuredWidth(),
                imageView.getMeasuredHeight());

        imageView.buildDrawingCache();
        return imageView.getDrawingCache();
    }

    private void populateGallery() {
        LinearLayout imageGallery = (LinearLayout) findViewById(R.id.imageGallery);
        for (int i = 0; i < 10; i++) {
            imageGallery.addView(getImageView(R.drawable.angie_head_circle));
        }
    }

    private View getImageView(Integer image) {
        ImageView imageView = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200,200);
        lp.setMargins(0, 0, 50, 0);
        imageView.setLayoutParams(lp);
        imageView.setImageResource(image);
        return imageView;
    }

    public ArrayList<Overlay> getAllOverlays() {
        final ArrayList<Overlay> allOverlays = new ArrayList<Overlay>();
        ParseQuery query = new ParseQuery("Overlay");
        Log.d("Angie", "Retrieved  Brands");

        query.findInBackground(new FindCallback() {
            @Override
            public void done(List list, com.parse.ParseException e) {
                if (e == null) {

                    String myObject = list.toString();
                    Log.d("Angie", "Retrieved " + myObject + " Brands");


                } else {
                    Log.d("Angie", "Error: " + e.getMessage());
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                Log.d("Angie", "Retrieved " + o.toString());
                List<ParseObject> list = (List<ParseObject>) o;
                for (int i=0; i<list.size(); i++) {
                    ParseObject object = (ParseObject) list.get(i);
                    Log.v("Angie", "ID" + object.getObjectId());
                    Overlay overlay = new Overlay(object);
                    allOverlays.add(overlay);
                }

            }
        });
        return allOverlays;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_overlay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
