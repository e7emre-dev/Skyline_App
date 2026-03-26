package com.manifix.incx;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.shobhitpuri.custombuttons.*;
import com.canhub.cropper.CropImageView;
import com.theophrast.ui.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.database.Cursor;
import android.provider.MediaStore;
import com.bumptech.glide.request.RequestOptions;
import java.net.URL;
import java.net.MalformedURLException;

public class CreateImagePostActivity extends AppCompatActivity {
    
    public final int REQ_CD_IMAGE_PICKER = 101;
    
    private ProgressDialog UptimeLoadingDialog;
    private String AddFromUrlStr = null;
    private Intent intent = new Intent();
    private Intent IMAGE_PICKER = new Intent(Intent.ACTION_GET_CONTENT);
    
    private LinearLayout back;
    private TextView title;
    private View topSpc;
    private TextView continueButton;
    private CropImageView cropImageView;
    private RelativeLayout urlImagePreview;
    private ImageView urlImagePreviewImage;
    private RecyclerView imagesView;
    private LinearLayout bottomButtons;
    private LinearLayout selectImageWithGallery;
    private LinearLayout addFromUrl;
    
    private ArrayList<HashMap<String, Object>> imagesListMap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.create_image_post);
        initialize(_savedInstanceState);
        FirebaseApp.initializeApp(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        } else {
            initializeLogic();
        }
    }

    private void initialize(Bundle _savedInstanceState) {
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        topSpc = findViewById(R.id.topSpc);
        continueButton = findViewById(R.id.continueButton);
        cropImageView = findViewById(R.id.cropImageView);
        urlImagePreview = findViewById(R.id.urlImagePreview);
        urlImagePreviewImage = findViewById(R.id.urlImagePreviewImage);
        imagesView = findViewById(R.id.imagesView);
        bottomButtons = findViewById(R.id.bottomButtons);
        selectImageWithGallery = findViewById(R.id.selectImageWithGallery);
        addFromUrl = findViewById(R.id.addFromUrl);
        
        IMAGE_PICKER.setType("image/*");
        IMAGE_PICKER.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                onBackPressed();
            }
        });
        
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                proceedWithPost();
            }
        });
        
        selectImageWithGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                startActivityForResult(IMAGE_PICKER, REQ_CD_IMAGE_PICKER);
            }
        });
        
        addFromUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                _AddFromUrlDialog();
            }
        });
    }

    private void proceedWithPost() {
        if (AddFromUrlStr == null) {
            try {
                saveBitmapAsPng();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            intent.setClass(getApplicationContext(), CreateImagePostNextStepActivity.class);
            intent.putExtra("type", "url");
            intent.putExtra("path", AddFromUrlStr);
            startActivity(intent);
            finish();
        }
    }

    private void saveBitmapAsPng() throws IOException {
        _LoadingDialog(true);
        Calendar cc = Calendar.getInstance();
        File getCacheDir = getExternalCacheDir();
        File getCacheFolder = new File(getCacheDir, "cropped_images");
        getCacheFolder.mkdirs();
        File getImageFile = new File(getCacheFolder, cc.getTimeInMillis() + ".png");
        final String savedFilePath = getImageFile.getAbsolutePath();
        
        final FileOutputStream outStream = new FileOutputStream(getImageFile);
        final Bitmap finalBitmap = cropImageView.getCroppedImage();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _LoadingDialog(false);
                            intent.setClass(getApplicationContext(), CreateImagePostNextStepActivity.class);
                            intent.putExtra("type", "local");
                            intent.putExtra("path", savedFilePath);
                            startActivity(intent);
                            finish();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initializeLogic() {
        _stateColor(0xFFFFFFFF, 0xFFFFFFFF);
        _viewGraphics(continueButton, 0xFF4CAF50, 0xFF388E3C, 300, 0, Color.TRANSPARENT);
        _viewGraphics(bottomButtons, 0xFFFFFFFF, 0xFFEEEEEE, 0, 3, 0xFFEEEEEE);
        
        imagesView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesView.setAdapter(new ImagesAdapter());
        _getImageFiles();
    }

    public void _viewGraphics(View _view, int _color1, int _color2, float _radius, float _stroke, int _strokeColor) {
        GradientDrawable GG = new GradientDrawable();
        GG.setColor(_color1);
        GG.setCornerRadius(_radius);
        GG.setStroke((int) _stroke, _strokeColor);
        _view.setBackground(GG);
    }

    public void _getImageFiles() {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor imagesCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        
        if (imagesCursor != null && imagesCursor.moveToFirst()) {
            do {
                String imagePath = imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                if (imagePath.endsWith(".png") || imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg")) {
                    HashMap<String, Object> mediaItem = new HashMap<>();
                    mediaItem.put("type", "Image");
                    mediaItem.put("path", imagePath);
                    imagesListMap.add(mediaItem);
                }
            } while (imagesCursor.moveToNext());
            imagesCursor.close();
        }
        
        if (imagesView.getAdapter() != null) {
            imagesView.getAdapter().notifyDataSetChanged();
        }
        if (imagesListMap.size() > 0) {
            _loadCropImage(imagesListMap.get(0).get("path").toString(), false);
        }
    }

    public void _stateColor(int _statusColor, int _navigationColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(_statusColor);
            getWindow().setNavigationBarColor(_navigationColor);
        }
    }

    public void _LoadingDialog(boolean _visibility) {
        if (_visibility) {
            if (UptimeLoadingDialog == null) {
                UptimeLoadingDialog = new ProgressDialog(this);
                UptimeLoadingDialog.setCancelable(false);
                UptimeLoadingDialog.setCanceledOnTouchOutside(false);
                UptimeLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                if (UptimeLoadingDialog.getWindow() != null) {
                    UptimeLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            }
            UptimeLoadingDialog.show();
            UptimeLoadingDialog.setContentView(R.layout.loading);
            LinearLayout loading_bar_layout = UptimeLoadingDialog.findViewById(R.id.loading_bar_layout);
            if (loading_bar_layout != null) {
                GradientDrawable gd = new GradientDrawable();
                gd.setCornerRadius(100);
                gd.setColor(0xFFFFFFFF);
                loading_bar_layout.setBackground(gd);
            }
        } else {
            if (UptimeLoadingDialog != null && UptimeLoadingDialog.isShowing()) {
                UptimeLoadingDialog.dismiss();
            }
        }
    }

    public void _loadCropImage(String _path, boolean _isUrl) {
        if (!_isUrl) {
            AddFromUrlStr = null;
            cropImageView.setImageUriAsync(Uri.fromFile(new File(_path)));
            urlImagePreview.setVisibility(View.GONE);
            cropImageView.setVisibility(View.VISIBLE);
        } else {
            AddFromUrlStr = _path;
            Glide.with(getApplicationContext()).load(Uri.parse(_path)).into(urlImagePreviewImage);
            urlImagePreview.setVisibility(View.VISIBLE);
            cropImageView.setVisibility(View.GONE);
        }
    }

    public void _AddFromUrlDialog() {
        final AlertDialog NewCustomDialog = new AlertDialog.Builder(this).create();
        View NewCustomDialogCV = getLayoutInflater().inflate(R.layout.create_image_post_add_from_url, null);
        NewCustomDialog.setView(NewCustomDialogCV);
        if (NewCustomDialog.getWindow() != null) {
            NewCustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        
        androidx.cardview.widget.CardView dialog_card = NewCustomDialogCV.findViewById(R.id.dialog_card);
        final EditText image_url_input = NewCustomDialogCV.findViewById(R.id.image_url_input);
        TextView add_button = NewCustomDialogCV.findViewById(R.id.add_button);
        TextView cancel_button = NewCustomDialogCV.findViewById(R.id.cancel_button);
        
        GradientDrawable cardGd = new GradientDrawable();
        cardGd.setCornerRadius(28);
        cardGd.setColor(0xFFFFFFFF);
        dialog_card.setBackground(cardGd);
        
        GradientDrawable inputGd = new GradientDrawable();
        inputGd.setCornerRadius(28);
        inputGd.setStroke(3, 0xFFEEEEEE);
        inputGd.setColor(Color.TRANSPARENT);
        image_url_input.setBackground(inputGd);
        
        _viewGraphics(add_button, 0xFF4CAF50, 0xFF388E3C, 300, 0, Color.TRANSPARENT);
        _viewGraphics(cancel_button, 0xFFF44336, 0xFFD32F2F, 300, 0, Color.TRANSPARENT);
        
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!image_url_input.getText().toString().isEmpty()) {
                    _loadCropImage(image_url_input.getText().toString(), true);
                    NewCustomDialog.dismiss();
                }
            }
        });
        
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCustomDialog.dismiss();
            }
        });
        
        NewCustomDialog.show();
    }

    private class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.skyline_create_image_post_picker_custom, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            Glide.with(getApplicationContext()).load(Uri.fromFile(new File(imagesListMap.get(position).get("path").toString()))).into(holder.image);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _loadCropImage(imagesListMap.get(position).get("path").toString(), false);
                }
            });
        }

        @Override
        public int getItemCount() {
            return imagesListMap.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            ViewHolder(View v) {
                super(v);
                image = v.findViewById(R.id.image);
            }
        }
    }
}
