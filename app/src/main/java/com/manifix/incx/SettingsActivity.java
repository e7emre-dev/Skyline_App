package com.manifix.incx;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.Intent;
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
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.shobhitpuri.custombuttons.*;
 // import com.theartofdev.edmodo.cropper.View;
import com.theophrast.ui.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class SettingsActivity extends AppCompatActivity {
	
	private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
	
	private String UserAvatarUri = "";
	
	private LinearLayout body;
	private LinearLayout top;
	private ScrollView scrollMain;
	private ImageView mBack;
	private TextView mTitle;
	private ImageView spc;
	private LinearLayout scrollBody;
	private LinearLayout profile_stage;
	private TextView application_stage_title;
	private LinearLayout application_stage;
	private TextView account_stage_title;
	private LinearLayout account_stage;
	private CardView profile_image_card;
	private LinearLayout profile_details;
	private TextView profile_username;
	private ImageView profile_image_view;
	private TextView profile_nickname;
	private ImageView profile_gender;
	private CardView profile_flag_card;
	private ImageView profile_flag_image;
	private LinearLayout application_stage_themes;
	private LinearLayout application_stage_language;
	private ImageView application_stage_themes_ic;
	private TextView application_stage_themes_title;
	private ImageView application_stage_language_ic;
	private TextView application_stage_language_title;
	private LinearLayout account_stage_privacy;
	private LinearLayout account_stage_security;
	private LinearLayout account_stage_logout;
	private LinearLayout account_stage_premium;
	private ImageView account_stage_premium_ic;
	private TextView account_stage_premium_title;
	private ImageView account_stage_privacy_ic;
	private TextView account_stage_privacy_title;
	private ImageView account_stage_security_ic;
	private TextView account_stage_security_title;
	private ImageView account_stage_logout_ic;
	private TextView account_stage_logout_title;
	
	private FirebaseAuth auth;
	private OnCompleteListener<AuthResult> _auth_create_user_listener;
	private OnCompleteListener<AuthResult> _auth_sign_in_listener;
	private OnCompleteListener<Void> _auth_reset_password_listener;
	private OnCompleteListener<Void> auth_updateEmailListener;
	private OnCompleteListener<Void> auth_updatePasswordListener;
	private OnCompleteListener<Void> auth_emailVerificationSentListener;
	private OnCompleteListener<Void> auth_deleteUserListener;
	private OnCompleteListener<Void> auth_updateProfileListener;
	private OnCompleteListener<AuthResult> auth_phoneAuthListener;
	private OnCompleteListener<AuthResult> auth_googleSignInListener;
	private Intent intent = new Intent();
	private DatabaseReference mainDb = _firebase.getReference("/");
	private ChildEventListener _mainDb_child_listener;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.settings);
		initialize(_savedInstanceState);
		FirebaseApp.initializeApp(this);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		body = findViewById(R.id.body);
		top = findViewById(R.id.top);
		scrollMain = findViewById(R.id.scrollMain);
		mBack = findViewById(R.id.mBack);
		mTitle = findViewById(R.id.mTitle);
		spc = findViewById(R.id.spc);
		scrollBody = findViewById(R.id.scrollBody);
		profile_stage = findViewById(R.id.profile_stage);
		application_stage_title = findViewById(R.id.application_stage_title);
		application_stage = findViewById(R.id.application_stage);
		account_stage_title = findViewById(R.id.account_stage_title);
		account_stage = findViewById(R.id.account_stage);
		profile_image_card = findViewById(R.id.profile_image_card);
		profile_details = findViewById(R.id.profile_details);
		profile_username = findViewById(R.id.profile_username);
		profile_image_view = findViewById(R.id.profile_image_view);
		profile_nickname = findViewById(R.id.profile_nickname);
		profile_gender = findViewById(R.id.profile_gender);
		profile_flag_card = findViewById(R.id.profile_flag_card);
		profile_flag_image = findViewById(R.id.profile_flag_image);
		application_stage_themes = findViewById(R.id.application_stage_themes);
		application_stage_language = findViewById(R.id.application_stage_language);
		application_stage_themes_ic = findViewById(R.id.application_stage_themes_ic);
		application_stage_themes_title = findViewById(R.id.application_stage_themes_title);
		application_stage_language_ic = findViewById(R.id.application_stage_language_ic);
		application_stage_language_title = findViewById(R.id.application_stage_language_title);
		account_stage_privacy = findViewById(R.id.account_stage_privacy);
		account_stage_security = findViewById(R.id.account_stage_security);
			account_stage_logout = findViewById(R.id.account_stage_logout);
			account_stage_premium = findViewById(R.id.account_stage_premium);
			account_stage_premium_ic = findViewById(R.id.account_stage_premium_ic);
			account_stage_premium_title = findViewById(R.id.account_stage_premium_title);
			account_stage_privacy_ic = findViewById(R.id.account_stage_privacy_ic);
		account_stage_privacy_title = findViewById(R.id.account_stage_privacy_title);
		account_stage_security_ic = findViewById(R.id.account_stage_security_ic);
		account_stage_security_title = findViewById(R.id.account_stage_security_title);
		account_stage_logout_ic = findViewById(R.id.account_stage_logout_ic);
		account_stage_logout_title = findViewById(R.id.account_stage_logout_title);
		auth = FirebaseAuth.getInstance();
		
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				finish();
			}
		});
		
		application_stage_themes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
			}
		});
		
		application_stage_language.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
			}
		});
		
		account_stage_privacy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
			}
		});
		
			account_stage_security.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					// Şifre Sıfırlama E-postası Gönder
					if (auth.getCurrentUser() != null && auth.getCurrentUser().getEmail() != null) {
						auth.sendPasswordResetEmail(auth.getCurrentUser().getEmail())
							.addOnCompleteListener(new OnCompleteListener<Void>() {
								@Override
								public void onComplete(@NonNull Task<Void> task) {
									if (task.isSuccessful()) {
										Toast.makeText(getApplicationContext(), "Şifre sıfırlama bağlantısı e-postanıza gönderildi.", Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(getApplicationContext(), "Hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
									}
								}
							});
					}
				}
			});

			account_stage_privacy.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					// Hesap Gizliliği Toggle (Basit bir örnek)
					final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("skyline/users").child(auth.getUid());
					userRef.child("isPrivate").addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot snapshot) {
							boolean isPrivate = snapshot.exists() && snapshot.getValue(Boolean.class);
							userRef.child("isPrivate").setValue(!isPrivate);
							String msg = !isPrivate ? "Hesabınız artık gizli." : "Hesabınız artık herkese açık.";
							Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
						}
						@Override
						public void onCancelled(@NonNull DatabaseError error) {}
					});
				}
			});
		
			account_stage_premium.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					String shopierUrl = "https://www.shopier.com/e7emree"; // Kullanıcının Shopier linki buraya gelecek
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(shopierUrl));
					startActivity(i);
				}
			});

			account_stage_logout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
				{
					final AlertDialog NewCustomDialog = new AlertDialog.Builder(SettingsActivity.this).create();
					LayoutInflater NewCustomDialogLI = getLayoutInflater();
					View NewCustomDialogCV = (View) NewCustomDialogLI.inflate(R.layout.skyline_dialog_background_view, null);
					NewCustomDialog.setView(NewCustomDialogCV);
					NewCustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					
					final TextView dialog_title = (TextView) NewCustomDialogCV.findViewById(R.id.dialog_title);
					final TextView dialog_message = (TextView) NewCustomDialogCV.findViewById(R.id.dialog_message);
					final TextView dialog_no_button = (TextView) NewCustomDialogCV.findViewById(R.id.dialog_no_button);
					final TextView dialog_yes_button = (TextView) NewCustomDialogCV.findViewById(R.id.dialog_yes_button);
					dialog_yes_button.setTextColor(0xFFF44336);
					_viewGraphics(dialog_yes_button, 0xFFFFFFFF, 0xFFFFCDD2, 28, 0, Color.TRANSPARENT);
					dialog_no_button.setTextColor(0xFF2196F3);
					_viewGraphics(dialog_no_button, 0xFFFFFFFF, 0xFFBBDEFB, 28, 0, Color.TRANSPARENT);
					dialog_title.setText(getResources().getString(R.string.info));
					dialog_message.setText(getResources().getString(R.string.logout_dialog_inf));
					dialog_yes_button.setText(getResources().getString(R.string.yes));
					dialog_no_button.setText(getResources().getString(R.string.no));
					dialog_yes_button.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View _view) {
							UnepixApp.setUserStatusOffline();
							FirebaseAuth.getInstance().signOut();
							intent.setClass(getApplicationContext(), MainActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							UnepixApp.setUserStatusOfflineListenerCancel();
							NewCustomDialog.dismiss();
						}
					});
					dialog_no_button.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View _view) {
							NewCustomDialog.dismiss();
						}
					});
					NewCustomDialog.setCancelable(true);
					NewCustomDialog.show();
				}
			}
		});
		
		_mainDb_child_listener = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				
			}
			
			@Override
			public void onChildChanged(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				
			}
			
			@Override
			public void onChildMoved(DataSnapshot _param1, String _param2) {
				
			}
			
			@Override
			public void onChildRemoved(DataSnapshot _param1) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				
			}
			
			@Override
			public void onCancelled(DatabaseError _param1) {
				final int _errorCode = _param1.getCode();
				final String _errorMessage = _param1.getMessage();
				
			}
		};
		mainDb.addChildEventListener(_mainDb_child_listener);
		
		auth_updateEmailListener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		auth_updatePasswordListener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		auth_emailVerificationSentListener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		auth_deleteUserListener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		auth_phoneAuthListener = new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(Task<AuthResult> task) {
				final boolean _success = task.isSuccessful();
				final String _errorMessage = task.getException() != null ? task.getException().getMessage() : "";
				
			}
		};
		
		auth_updateProfileListener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		auth_googleSignInListener = new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(Task<AuthResult> task) {
				final boolean _success = task.isSuccessful();
				final String _errorMessage = task.getException() != null ? task.getException().getMessage() : "";
				
			}
		};
		
		_auth_create_user_listener = new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(Task<AuthResult> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		_auth_sign_in_listener = new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(Task<AuthResult> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		_auth_reset_password_listener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				
			}
		};
	}
	
	private void initializeLogic() {
		_stateColor(0xFFFFFFFF, 0xFFFAFAFA);
		profile_image_card.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)300, Color.TRANSPARENT));
		application_stage.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)28, 0xFFFFFFFF));
		_viewGraphics(application_stage_themes, 0xFFFFFFFF, 0xFFF5F5F5, 28, 0, Color.TRANSPARENT);
		_viewGraphics(application_stage_language, 0xFFFFFFFF, 0xFFF5F5F5, 28, 0, Color.TRANSPARENT);
		application_stage_themes_ic.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)16, 0xFFF5F5F5));
		application_stage_language_ic.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)16, 0xFFF5F5F5));
		account_stage.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)28, 0xFFFFFFFF));
		_viewGraphics(account_stage_privacy, 0xFFFFFFFF, 0xFFF5F5F5, 28, 0, Color.TRANSPARENT);
		_viewGraphics(account_stage_security, 0xFFFFFFFF, 0xFFF5F5F5, 28, 0, Color.TRANSPARENT);
		_viewGraphics(account_stage_logout, 0xFFFFFFFF, 0xFFF5F5F5, 28, 0, Color.TRANSPARENT);
		account_stage_privacy_ic.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)16, 0xFFF5F5F5));
		account_stage_security_ic.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)16, 0xFFF5F5F5));
		account_stage_logout_ic.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)16, 0xFFF5F5F5));
		_getUserReference();
	}
	
	public void _stateColor(final int _statusColor, final int _navigationColor) {
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		getWindow().setStatusBarColor(_statusColor);
		getWindow().setNavigationBarColor(_navigationColor);
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
	
	
	public void _getUserReference() {
		DatabaseReference getUserReference = FirebaseDatabase.getInstance().getReference("skyline/users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
		getUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if(dataSnapshot.exists()) {
					if (dataSnapshot.child("banned").getValue(String.class).equals("true")) {
						profile_image_view.setImageResource(R.drawable.avatar);
					} else {
						if (dataSnapshot.child("avatar").getValue(String.class).equals("null")) {
							profile_image_view.setImageResource(R.drawable.avatar);
						} else {
							Glide.with(getApplicationContext()).load(Uri.parse(dataSnapshot.child("avatar").getValue(String.class))).into(profile_image_view);
						}
					}
					if (dataSnapshot.child("nickname").getValue(String.class).equals("null")) {
						profile_nickname.setText("@" + dataSnapshot.child("username").getValue(String.class));
					} else {
						profile_nickname.setText(dataSnapshot.child("nickname").getValue(String.class));
					}
					profile_username.setText("@" + dataSnapshot.child("username").getValue(String.class));
					if (dataSnapshot.child("gender").getValue(String.class).equals("hidden")) {
						profile_gender.setVisibility(View.GONE);
					} else {
						if (dataSnapshot.child("gender").getValue(String.class).equals("male")) {
							profile_gender.setImageResource(R.drawable.male_badge);
							profile_gender.setVisibility(View.VISIBLE);
						} else {
							if (dataSnapshot.child("gender").getValue(String.class).equals("female")) {
								profile_gender.setImageResource(R.drawable.female_badge);
								profile_gender.setVisibility(View.VISIBLE);
							}
						}
					}
					if (dataSnapshot.child("user_region").getValue(String.class) != null) {
						Glide.with(getApplicationContext()).load(Uri.parse("https://flagcdn.com/w640/".concat(dataSnapshot.child("user_region").getValue(String.class).concat(".png")))).into(profile_flag_image);
						profile_flag_card.setVisibility(View.VISIBLE);
					} else {
						profile_flag_card.setVisibility(View.GONE);
					}
				} else {
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				
			}
		});
	}
	
}
