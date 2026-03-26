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
import com.theartofdev.edmodo.cropper.*;
import com.theartofdev.edmodo.cropper.CropImageView;
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
	private void saveBitmapAsPng(Bitmap bitmap) throws IOException {
		_LoadingDialog(true);
		Calendar cc = Calendar.getInstance();
		
		File getCacheDir = getExternalCacheDir();
		String getCacheDirName = "cropped_images";
		File getCacheFolder = new File(getCacheDir, getCacheDirName);
		getCacheFolder.mkdirs();
		File getImageFile = new File(getCacheFolder, cc.getTimeInMillis() + ".png");
		String savedFilePath = getImageFile.getAbsolutePath();
		
		final FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(getImageFile);
			final Bitmap finalBitmap = bitmap;
			
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
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								_LoadingDialog(false);
								Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
							}
						});
						e.printStackTrace();
					}
				}
			}).start();
			
		} catch (IOException e) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					_LoadingDialog(false);
					Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
				}
			});
			throw e;
		}
	}
	
	private String AddFromUrlStr = null;
	
	private ArrayList<HashMap<String, Object>> imagesListMap = new ArrayList<>();
	
	private LinearLayout main;
	private LinearLayout top;
	private CropImageView cropImageView;
	private LinearLayout urlImagePreview;
	private LinearLayout body;
	private ImageView back;
	private TextView title;
	private LinearLayout topSpc;
	private TextView continueButton;
	private ImageView urlImagePreviewImage;
	private RecyclerView imagesView;
	private LinearLayout bottomButtons;
	private TextView selectImageWithGallery;
	private TextView addFromUrl;
	
	private Intent intent = new Intent();
	private Intent IMAGE_PICKER = new Intent(Intent.ACTION_GET_CONTENT);
	
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
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		main = findViewById(R.id.main);
		top = findViewById(R.id.top);
		cropImageView = findViewById(R.id.cropImageView);
		urlImagePreview = findViewById(R.id.urlImagePreview);
		body = findViewById(R.id.body);
		back = findViewById(R.id.back);
		title = findViewById(R.id.title);
		topSpc = findViewById(R.id.topSpc);
		continueButton = findViewById(R.id.continueButton);
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
					final String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
					final com.google.firebase.database.DatabaseReference userPostsRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("skyline/user_posts_count").child(uid);
					
					UnepixApp.checkPremiumStatus(new UnepixApp.PremiumCallback() {
						@Override
						public void onResult(boolean isPremium) {
							if (isPremium) {
								proceedWithPost();
							} else {
								userPostsRef.child("count").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
									@Override
									public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
										long count = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
										if (count >= 3) {
											Toast.makeText(getApplicationContext(), "Günlük paylaşım limitine ulaştınız (3/3). Sınırsız paylaşım için Premium'a geçin!", Toast.LENGTH_LONG).show();
										} else {
											proceedWithPost();
											userPostsRef.child("count").setValue(count + 1);
										}
									}
									@Override
									public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
								});
							}
						}
					});
				}
			});
		}

		private void proceedWithPost() {
			if (AddFromUrlStr == null) {
					try {
						saveBitmapAsPng(cropImageView.getCroppedImage());
					} catch (IOException e) {
						
					}
				} else {
					intent.setClass(getApplicationContext(), CreateImagePostNextStepActivity.class);
					intent.putExtra("type", "url");
					intent.putExtra("path", AddFromUrlStr);
					startActivity(intent);
					finish();
				}
			}
		});
		
		selectImageWithGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (Build.VERSION.SDK_INT >= 23) {
					if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED) {
						requestPermissions(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
					} else {
						Intent sendImgInt = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(sendImgInt, REQ_CD_IMAGE_PICKER);
					}
				} else {
					Intent sendImgInt = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(sendImgInt, REQ_CD_IMAGE_PICKER);
				}
			}
		});
		
		addFromUrl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_AddFromUrlDialog();
			}
		});
	}
	
	private void initializeLogic() {
		Display display = getWindowManager().getDefaultDisplay();
		int screenHeight = display.getHeight();
		int desiredHeight = screenHeight * 1 / 2 - 24;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, desiredHeight);
		
		cropImageView.setLayoutParams(params);
		urlImagePreview.setLayoutParams(params);
		_stateColor(0xFFFFFFFF, 0xFFFFFFFF);
		_viewGraphics(back, 0xFFFFFFFF, 0xFFE0E0E0, 300, 0, Color.TRANSPARENT);
		_viewGraphics(continueButton, 0xFFFFFFFF, 0xFFE0E0E0, 300, 0, Color.TRANSPARENT);
		_viewGraphics(selectImageWithGallery, 0xFF2196F3, 0xFF1565C0, 300, 0, Color.TRANSPARENT);
		_viewGraphics(addFromUrl, 0xFF4CAF50, 0xFF388E3C, 300, 0, Color.TRANSPARENT);
		imagesView.setAdapter(new İmagesViewAdapter(imagesListMap));
		GridLayoutManager imagesViewGridLayout = new GridLayoutManager(this, 4);
		imagesView.setLayoutManager(imagesViewGridLayout);
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED) {
				requestPermissions(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
			} else {
				_getImageFiles();
			}
		} else {
			_getImageFiles();
		}
		
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			case REQ_CD_IMAGE_PICKER:
			if (_resultCode == Activity.RESULT_OK) {
				ArrayList<String> _filePath = new ArrayList<>();
				if (_data != null) {
					if (_data.getClipData() != null) {
						for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
							ClipData.Item _item = _data.getClipData().getItemAt(_index);
							_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
						}
					}
					else {
						_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
					}
				}
				if (_filePath.get((int)(0)).endsWith(".png") || (_filePath.get((int)(0)).endsWith(".jpg") || _filePath.get((int)(0)).endsWith(".jpeg"))) {
					_loadCropImage(_filePath.get((int)(0)), false);
				} else {
					SketchwareUtil.showMessage(getApplicationContext(), "Invalid File Type");
				}
			}
			else {
				
			}
			break;
			default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
	

	public void _ImageColor(final ImageView _image, final int _color) {
		_image.setColorFilter(_color,PorterDuff.Mode.SRC_ATOP);
	}
	
	
	public void _viewGraphics(final View _view, final int _onFocus, final int _onRipple, final double _radius, final double _stroke, final int _strokeColor) {
		android.graphics.drawable.GradientDrawable GG = new android.graphics.drawable.GradientDrawable();
		GG.setColor(_onFocus);
		GG.setCornerRadius((float)_radius);
		GG.setStroke((int) _stroke, _strokeColor);
		android.graphics.drawable.RippleDrawable RE = new android.graphics.drawable.RippleDrawable(new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{ _onRipple}), GG, null);
		_view.setBackground(RE);
	}
	
	
	public void _getImageFiles() {
		String[] projection = {
			MediaStore.Images.Media.DATA
		};
		Cursor imagesCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
		
		if (imagesCursor != null && imagesCursor.moveToFirst()) {
			do {
				String imagePath = imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATA));
				if (imagePath.endsWith(".png") || (imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg"))) {
					HashMap<String, Object> mediaItem = new HashMap<>();
					mediaItem.put("type", "Image");
					mediaItem.put("path", imagePath);
					imagesListMap.add(mediaItem);
				}
			} while (imagesCursor.moveToNext());
			imagesCursor.close();
		}
		
		imagesView.getAdapter().notifyDataSetChanged();
		_loadCropImage(imagesListMap.get((int)0).get("path").toString(), false);
	}
	
	
	public void _stateColor(final int _statusColor, final int _navigationColor) {
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		getWindow().setStatusBarColor(_statusColor);
		getWindow().setNavigationBarColor(_navigationColor);
	}
	
	
	public void _LoadingDialog(final boolean _visibility) {
		if (_visibility) {
			if (UptimeLoadingDialog== null){
				UptimeLoadingDialog = new ProgressDialog(this);
				UptimeLoadingDialog.setCancelable(false);
				UptimeLoadingDialog.setCanceledOnTouchOutside(false);
				
				UptimeLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
				UptimeLoadingDialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
				
			}
			UptimeLoadingDialog.show();
			UptimeLoadingDialog.setContentView(R.layout.loading);
			
			LinearLayout loading_bar_layout = (LinearLayout)UptimeLoadingDialog.findViewById(R.id.loading_bar_layout);
			
			
			loading_bar_layout.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)100, 0xFFFFFFFF));
		} else {
			if (UptimeLoadingDialog != null){
				UptimeLoadingDialog.dismiss();
			}
		}
		
	}
	
	
	public void _loadCropImage(final String _path, final boolean _isUrl) {
		if (!_isUrl) {
			AddFromUrlStr = null;
			java.io.File file = new java.io.File(_path);
			Uri uri = Uri.fromFile(file);
			cropImageView.setImageUriAsync(uri);
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
		{
			final AlertDialog NewCustomDialog = new AlertDialog.Builder(CreateImagePostActivity.this).create();
			LayoutInflater NewCustomDialogLI = getLayoutInflater();
			View NewCustomDialogCV = (View) NewCustomDialogLI.inflate(R.layout.create_image_post_add_from_url, null);
			NewCustomDialog.setView(NewCustomDialogCV);
			NewCustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			
			final androidx.cardview.widget.CardView dialog_card = NewCustomDialogCV.findViewById(R.id.dialog_card);
			final EditText image_url_input = NewCustomDialogCV.findViewById(R.id.image_url_input);
			final TextView add_button = NewCustomDialogCV.findViewById(R.id.add_button);
			final TextView cancel_button = NewCustomDialogCV.findViewById(R.id.cancel_button);
			
			dialog_card.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)28, 0xFFFFFFFF));
			image_url_input.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b, int c, int d) { this.setCornerRadius(a); this.setStroke(b, c); this.setColor(d); return this; } }.getIns((int)28, (int)3, 0xFFEEEEEE, Color.TRANSPARENT));
			_viewGraphics(add_button, 0xFF4CAF50, 0xFF388E3C, 300, 0, Color.TRANSPARENT);
			_viewGraphics(cancel_button, 0xFFF5F5F5, 0xFFE0E0E0, 300, 0, Color.TRANSPARENT);
			image_url_input.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
					final String _charSeq = _param1.toString();
					
					if (!_charSeq.trim().equals("")) {
						if (_checkValidUrl(_charSeq.trim())) {
							
						} else {
							((EditText)image_url_input).setError("Invalid URL");
						}
					} else {
						((EditText)image_url_input).setError("Enter Profile Image URL");
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
					
				}
				
				@Override
				public void afterTextChanged(Editable _param1) {
					
				}
			});
			
			add_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					if (!image_url_input.getText().toString().trim().equals("")) {
						if (_checkValidUrl(image_url_input.getText().toString().trim())) {
							_loadCropImage(image_url_input.getText().toString().trim(), true);
							AddFromUrlStr = image_url_input.getText().toString().trim();
							NewCustomDialog.dismiss();
						}
					}
				}
			});
			cancel_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					NewCustomDialog.dismiss();
				}
			});
			
			NewCustomDialog.setCancelable(true);
			NewCustomDialog.show();
		}
	}
	
	
	public boolean _checkValidUrl(final String _url) {
		try {
			new URL(_url);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
	
	public class İmagesViewAdapter extends RecyclerView.Adapter<İmagesViewAdapter.ViewHolder> {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public İmagesViewAdapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater _inflater = getLayoutInflater();
			View _v = _inflater.inflate(R.layout.skyline_create_image_post_picker_custom, null);
			RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			_v.setLayoutParams(_lp);
			return new ViewHolder(_v);
		}
		
		@Override
		public void onBindViewHolder(ViewHolder _holder, final int _position) {
			View _view = _holder.itemView;
			
			final RelativeLayout relative = _view.findViewById(R.id.relative);
			final androidx.cardview.widget.CardView main = _view.findViewById(R.id.main);
			final LinearLayout relativeTop = _view.findViewById(R.id.relativeTop);
			final com.theophrast.ui.widget.SquareImageView image = _view.findViewById(R.id.image);
			final LinearLayout relativeSpc = _view.findViewById(R.id.relativeSpc);
			final ImageView typeIcon = _view.findViewById(R.id.typeIcon);
			final LinearLayout rvtSpc = _view.findViewById(R.id.rvtSpc);
			final TextView mediaDuration = _view.findViewById(R.id.mediaDuration);
			
			_ImageColor(typeIcon, 0xFFFFFFFF);
			typeIcon.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)24, 0x7B000000));
			mediaDuration.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)24, 0x7B000000));
			Glide.with(getApplicationContext()).load(_data.get((int)_position).get("path").toString()).into(image);
			typeIcon.setImageResource(R.drawable.image_ic);
			mediaDuration.setVisibility(View.GONE);
			relative.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					_loadCropImage(_data.get((int)_position).get("path").toString(), false);
				}
			});
		}
		
		@Override
		public int getItemCount() {
			return _data.size();
		}
		
		public class ViewHolder extends RecyclerView.ViewHolder {
			public ViewHolder(View v) {
				super(v);
			}
		}
	}
}
