package com.manifix.incx;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.os.Vibrator;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.shobhitpuri.custombuttons.*;
import com.theartofdev.edmodo.cropper.*;
import com.theophrast.ui.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;
import com.google.firebase.database.Query;

public class OnboardActivity extends AppCompatActivity {
	
	public final int REQ_CD_LOGIN_GOOGLE = 101;
	
	private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
	
	private ProgressDialog UptimeLoadingDialog;
	private GoogleSignInOptions mGoogleSignInOptions;
	private GoogleSignInAccount mGoogleSignInAccount;
	private AuthCredential mGoogleLoginCredential;
	private FirebaseUser FirebaseUser;
	private double num2 = 0;
	private String RandomUsername = "";
	
	private LinearLayout body;
	private ImageView icon;
	private TextView title;
	private LinearLayout sign_email_button;
	private LinearLayout sign_google_button;
	private LinearLayout terms_and_conditions;
	private TextView create_account_email;
	private ImageView sign_email_button_ic;
	private TextView sign_email_button_title;
	private ImageView sign_email_button_spx;
	private ImageView sign_google_button_ic;
	private TextView sign_google_button_title;
	private ImageView sign_google_button_spx;
	private ImageView terms_and_conditions_checkbox;
	private TextView terms_and_conditions_checkbox_title;
	
	private Vibrator vbr;
	private Intent intent = new Intent();
	private GoogleSignInClient LOGIN_GOOGLE;
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
	private DatabaseReference main = _firebase.getReference("skyline");
	private ChildEventListener _main_child_listener;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.onboard);
		initialize(_savedInstanceState);
		FirebaseApp.initializeApp(this);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		body = findViewById(R.id.body);
		icon = findViewById(R.id.icon);
		title = findViewById(R.id.title);
		sign_email_button = findViewById(R.id.sign_email_button);
		sign_google_button = findViewById(R.id.sign_google_button);
		terms_and_conditions = findViewById(R.id.terms_and_conditions);
		create_account_email = findViewById(R.id.create_account_email);
		sign_email_button_ic = findViewById(R.id.sign_email_button_ic);
		sign_email_button_title = findViewById(R.id.sign_email_button_title);
		sign_email_button_spx = findViewById(R.id.sign_email_button_spx);
		sign_google_button_ic = findViewById(R.id.sign_google_button_ic);
		sign_google_button_title = findViewById(R.id.sign_google_button_title);
		sign_google_button_spx = findViewById(R.id.sign_google_button_spx);
		terms_and_conditions_checkbox = findViewById(R.id.terms_and_conditions_checkbox);
		terms_and_conditions_checkbox_title = findViewById(R.id.terms_and_conditions_checkbox_title);
		vbr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		auth = FirebaseAuth.getInstance();
		
		sign_email_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (terms_and_conditions_checkbox.isEnabled()) {
					intent.setClass(getApplicationContext(), LoginActivity.class);
					startActivity(intent);
				} else {
					SketchwareUtil.showMessage(getApplicationContext(), getResources().getString(R.string.please_accept_terms_and_conditions));
					vbr.vibrate((long)(48));
				}
			}
		});
		
		sign_google_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (terms_and_conditions_checkbox.isEnabled()) {
					Intent mGoogleLoginIntent = LOGIN_GOOGLE.getSignInIntent();
					startActivityForResult(mGoogleLoginIntent, REQ_CD_LOGIN_GOOGLE);
				} else {
					SketchwareUtil.showMessage(getApplicationContext(), getResources().getString(R.string.please_accept_terms_and_conditions));
					vbr.vibrate((long)(48));
				}
			}
		});
		
		terms_and_conditions.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (terms_and_conditions_checkbox.isEnabled()) {
					terms_and_conditions_checkbox.setEnabled(false);
					terms_and_conditions_checkbox.setImageResource(R.drawable.checkbox_not_checked);
				} else {
					terms_and_conditions_checkbox.setEnabled(true);
					terms_and_conditions_checkbox.setImageResource(R.drawable.checkbox_checked);
				}
				vbr.vibrate((long)(48));
			}
		});
		
		create_account_email.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (terms_and_conditions_checkbox.isEnabled()) {
					intent.setClass(getApplicationContext(), RegisterActivity.class);
					startActivity(intent);
				} else {
					SketchwareUtil.showMessage(getApplicationContext(), getResources().getString(R.string.please_accept_terms_and_conditions));
					vbr.vibrate((long)(48));
				}
			}
		});
		
		_main_child_listener = new ChildEventListener() {
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
		main.addChildEventListener(_main_child_listener);
		
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
		String GoogleAuthClientKey = "client key";
		GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(GoogleAuthClientKey).requestEmail().build();
		LOGIN_GOOGLE = GoogleSignIn.getClient(this, mGoogleSignInOptions);
		auth = FirebaseAuth.getInstance();
		_transparentStatus();
		_viewGraphics(sign_email_button, 0xFFFFFFFF, 0xFFEEEEEE, 300, 3, 0xFFEEEEEE);
		_viewGraphics(sign_google_button, 0xFFFFFFFF, 0xFFEEEEEE, 300, 3, 0xFFEEEEEE);
		_viewGraphics(create_account_email, 0xFF2196F3, 0xFF1976D2, 300, 0, 0x7BFFFFFF);
		terms_and_conditions_checkbox.setEnabled(false);
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			case REQ_CD_LOGIN_GOOGLE:
			if (_resultCode == Activity.RESULT_OK) {
				Task<GoogleSignInAccount> _task = GoogleSignIn.getSignedInAccountFromIntent(_data);
				
				try {
					GoogleSignInAccount mGoogleSignInAccount = _task.getResult(ApiException.class);
					_GoogleLoginContinue(mGoogleSignInAccount);
				} catch (Exception e) {
					SketchwareUtil.showMessage(getApplicationContext(), e.getMessage());
				}
			}
			else {
				_LoadingDialog(false);
			}
			break;
			default:
			break;
		}
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
	
	
	public void _GoogleLoginContinue(final GoogleSignInAccount _account) {
		_LoadingDialog(true);
		AuthCredential mGoogleLoginCredential = GoogleAuthProvider.getCredential(_account.getIdToken(), null);
		auth.signInWithCredential(mGoogleLoginCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if (task.isSuccessful()) {
					FirebaseUser = auth.getCurrentUser();
					if (task.getResult().getAdditionalUserInfo().isNewUser()) {
						_createRandomUsernameRepeat();
					} else {
						_LoadingDialog(false);
						intent.setClass(getApplicationContext(), HomeActivity.class);
						startActivity(intent);
						finish();
					}
				} else {
					SketchwareUtil.showMessage(getApplicationContext(), "Something went wrong");
					_LoadingDialog(false);
				}
			}
		});
	}
	
	
	public void _createRandomUsernameRepeat() {
		num2 = SketchwareUtil.getRandom((int)(1111111111), (int)(2111111111));
		RandomUsername = "user".concat(String.valueOf((long)(num2)));
		DatabaseReference createRandomUsernameRef = FirebaseDatabase.getInstance().getReference().child("skyline/users");
		Query createRandomUsernameQuery = createRandomUsernameRef.orderByChild("username").equalTo(RandomUsername);
		createRandomUsernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if(dataSnapshot.exists()) {
					_createRandomUsernameRepeat();
				} else {
					_LoadingDialog(false);
					intent.setClass(getApplicationContext(), CompleteProfileActivity.class);
					intent.putExtra("findedUsername", RandomUsername);
					intent.putExtra("googleLoginName", FirebaseUser.getDisplayName());
					intent.putExtra("googleLoginEmail", FirebaseUser.getEmail());
					intent.putExtra("googleLoginAvatarUri", FirebaseUser.getPhotoUrl().toString());
					startActivity(intent);
					finish();
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				
			}
		});
	}
	
	
	public void _transparentStatus() {
		if (Build.VERSION.SDK_INT >= 19) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}
		if (Build.VERSION.SDK_INT >= 21) {
			setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
			getWindow().setStatusBarColor(Color.TRANSPARENT);
		}
		
	}
	private void setWindowFlag(final int bits, boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
	{
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
	
}