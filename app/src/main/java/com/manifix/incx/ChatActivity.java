package com.manifix.incx;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;
import org.json.*;
import com.google.firebase.database.Query;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {
	
	private Timer _timer = new Timer();
	private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
	
	private MediaRecorder AudioMessageRecorder;
	private double ChatMessagesLimit = 0;
	private HashMap<String, Object> ChatSendMap = new HashMap<>();
	private HashMap<String, Object> ChatInboxSend = new HashMap<>();
	private double recordMs = 0;
	private HashMap<String, Object> ChatInboxSend2 = new HashMap<>();
	private String SecondUserAvatar = "";
	private HashMap<String, Object> typingSnd = new HashMap<>();
	private String ReplyMessageID = "";
	private String SecondUserName = "";
	private String FirstUserName = "";
	private int ChatInitialSize = 0;
	
	private ArrayList<HashMap<String, Object>> ChatMessagesList = new ArrayList<>();
	
	private LinearLayout body;
	private LinearLayout top;
	private LinearLayout topSpace;
	private LinearLayout middle;
	private LinearLayout bottomSpace;
	private LinearLayout mMessageReplyLayout;
	private LinearLayout bottom;
	private LinearLayout bottomAudioRecorder;
	private ImageView back;
	private LinearLayout topProfileLayout;
	private LinearLayout topProfileLayoutSpace;
	private ImageView more;
	private CardView topProfileCard;
	private LinearLayout topProfileLayoutRight;
	private ImageView topProfileLayoutProfileImage;
	private LinearLayout topProfileLayoutRightTop;
	private TextView topProfileLayoutStatus;
	private TextView topProfileLayoutUsername;
	private ImageView topProfileLayoutGenderBadge;
	private ImageView topProfileLayoutVerifiedBadge;
	private LinearLayout bannedUserInfo;
	private RecyclerView ChatMessagesListRecycler;
	private TextView noChatText;
	private ImageView bannedUserInfoIc;
	private TextView bannedUserInfoText;
	private LinearLayout mMessageReplyLayoutBody;
	private LinearLayout mMessageReplyLayoutSpace;
	private ImageView mMessageReplyLayoutBodyIc;
	private LinearLayout mMessageReplyLayoutBodyRight;
	private ImageView mMessageReplyLayoutBodyCancel;
	private TextView mMessageReplyLayoutBodyRightUsername;
	private TextView mMessageReplyLayoutBodyRightMessage;
	private ImageView bottomStickerButton;
	private EditText bottomMessageInput;
	private ImageView bottomFileAttach;
	private ImageView bottomMicrophone;
	private ImageView bottomSendButton;
	private ImageView bottomAudioRecorderCancel;
	private TextView bottomAudioRecorderTime;
	private ImageView bottomAudioRecorderSend;
	
	private Intent intent = new Intent();
	private DatabaseReference main = _firebase.getReference("skyline");
	private ChildEventListener _main_child_listener;
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
	private TimerTask loadTimer;
	private Calendar cc = Calendar.getInstance();
	private Vibrator vbr;
	private TimerTask timer;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.chat);
		initialize(_savedInstanceState);
		FirebaseApp.initializeApp(this);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		body = findViewById(R.id.body);
		top = findViewById(R.id.top);
		topSpace = findViewById(R.id.topSpace);
		middle = findViewById(R.id.middle);
		bottomSpace = findViewById(R.id.bottomSpace);
		mMessageReplyLayout = findViewById(R.id.mMessageReplyLayout);
		bottom = findViewById(R.id.bottom);
		bottomAudioRecorder = findViewById(R.id.bottomAudioRecorder);
		back = findViewById(R.id.back);
		topProfileLayout = findViewById(R.id.topProfileLayout);
		topProfileLayoutSpace = findViewById(R.id.topProfileLayoutSpace);
		more = findViewById(R.id.more);
		topProfileCard = findViewById(R.id.topProfileCard);
		topProfileLayoutRight = findViewById(R.id.topProfileLayoutRight);
		topProfileLayoutProfileImage = findViewById(R.id.topProfileLayoutProfileImage);
		topProfileLayoutRightTop = findViewById(R.id.topProfileLayoutRightTop);
		topProfileLayoutStatus = findViewById(R.id.topProfileLayoutStatus);
		topProfileLayoutUsername = findViewById(R.id.topProfileLayoutUsername);
		topProfileLayoutGenderBadge = findViewById(R.id.topProfileLayoutGenderBadge);
		topProfileLayoutVerifiedBadge = findViewById(R.id.topProfileLayoutVerifiedBadge);
		bannedUserInfo = findViewById(R.id.bannedUserInfo);
		ChatMessagesListRecycler = findViewById(R.id.ChatMessagesListRecycler);
		noChatText = findViewById(R.id.noChatText);
		bannedUserInfoIc = findViewById(R.id.bannedUserInfoIc);
		bannedUserInfoText = findViewById(R.id.bannedUserInfoText);
		mMessageReplyLayoutBody = findViewById(R.id.mMessageReplyLayoutBody);
		mMessageReplyLayoutSpace = findViewById(R.id.mMessageReplyLayoutSpace);
		mMessageReplyLayoutBodyIc = findViewById(R.id.mMessageReplyLayoutBodyIc);
		mMessageReplyLayoutBodyRight = findViewById(R.id.mMessageReplyLayoutBodyRight);
		mMessageReplyLayoutBodyCancel = findViewById(R.id.mMessageReplyLayoutBodyCancel);
		mMessageReplyLayoutBodyRightUsername = findViewById(R.id.mMessageReplyLayoutBodyRightUsername);
		mMessageReplyLayoutBodyRightMessage = findViewById(R.id.mMessageReplyLayoutBodyRightMessage);
		bottomStickerButton = findViewById(R.id.bottomStickerButton);
		bottomMessageInput = findViewById(R.id.bottomMessageInput);
		bottomFileAttach = findViewById(R.id.bottomFileAttach);
		bottomMicrophone = findViewById(R.id.bottomMicrophone);
		bottomSendButton = findViewById(R.id.bottomSendButton);
		bottomAudioRecorderCancel = findViewById(R.id.bottomAudioRecorderCancel);
		bottomAudioRecorderTime = findViewById(R.id.bottomAudioRecorderTime);
		bottomAudioRecorderSend = findViewById(R.id.bottomAudioRecorderSend);
		auth = FirebaseAuth.getInstance();
		vbr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				onBackPressed();
			}
		});
		
		topProfileLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				intent.setClass(getApplicationContext(), ProfileActivity.class);
				intent.putExtra("uid", getIntent().getStringExtra("uid"));
				startActivity(intent);
			}
		});
		
		more.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
			}
		});
		
		mMessageReplyLayoutBodyCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				ReplyMessageID = "null";
				mMessageReplyLayout.setVisibility(View.GONE);
				vbr.vibrate((long)(48));
			}
		});
		
		bottomStickerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
			}
		});
		
		bottomMessageInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				final String _charSeq = _param1.toString();
				if (_charSeq.trim().equals("")) {
					bottomFileAttach.setVisibility(View.VISIBLE);
					bottomMicrophone.setVisibility(View.VISIBLE);
					bottomSendButton.setVisibility(View.GONE);
					FirebaseDatabase.getInstance().getReference("skyline/chats").child(getIntent().getStringExtra("uid")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("typing-message").removeValue();
				} else {
					bottomFileAttach.setVisibility(View.GONE);
					bottomMicrophone.setVisibility(View.GONE);
					bottomSendButton.setVisibility(View.VISIBLE);
					typingSnd = new HashMap<>();
					typingSnd.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
					typingSnd.put("typingMessageStatus", "true");
					FirebaseDatabase.getInstance().getReference("skyline/chats").child(getIntent().getStringExtra("uid")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("typing-message").updateChildren(typingSnd);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				
			}
			
			@Override
			public void afterTextChanged(Editable _param1) {
				
			}
		});
		
		bottomFileAttach.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				
			}
		});
		
		bottomMicrophone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (Build.VERSION.SDK_INT >= 23) {
					if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_DENIED) {
						requestPermissions(new String[] {android.Manifest.permission.RECORD_AUDIO}, 1000);
					} else {
						_AudioRecorderStart();
					}
				} else {
					_AudioRecorderStart();
				}
			}
		});
		
		bottomSendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (!bottomMessageInput.getText().toString().trim().equals("")) {
					cc = Calendar.getInstance();
					String uniqueMessageKey = main.push().getKey();
					ChatSendMap = new HashMap<>();
					ChatSendMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
					ChatSendMap.put("TYPE", "MESSAGE");
					ChatSendMap.put("message_text", bottomMessageInput.getText().toString().trim());
					ChatSendMap.put("message_state", "sended");
					if (!ReplyMessageID.equals("null")) {
						ChatSendMap.put("replied_message_id", ReplyMessageID);
					}
					ChatSendMap.put("key", uniqueMessageKey);
					ChatSendMap.put("push_date", String.valueOf((long)(cc.getTimeInMillis())));
					FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getIntent().getStringExtra("uid")).child(uniqueMessageKey).updateChildren(ChatSendMap);
					FirebaseDatabase.getInstance().getReference("skyline/chats").child(getIntent().getStringExtra("uid")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(uniqueMessageKey).updateChildren(ChatSendMap);
					ChatInboxSend = new HashMap<>();
					ChatInboxSend.put("uid", getIntent().getStringExtra("uid"));
					ChatInboxSend.put("TYPE", "MESSAGE");
					ChatInboxSend.put("last_message_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
					ChatInboxSend.put("last_message_text", bottomMessageInput.getText().toString().trim());
					ChatInboxSend.put("last_message_state", "sended");
					ChatInboxSend.put("push_date", String.valueOf((long)(cc.getTimeInMillis())));
					FirebaseDatabase.getInstance().getReference("skyline/inbox").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getIntent().getStringExtra("uid")).updateChildren(ChatInboxSend);
					ChatInboxSend2 = new HashMap<>();
					ChatInboxSend2.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
					ChatInboxSend2.put("TYPE", "MESSAGE");
					ChatInboxSend2.put("last_message_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
					ChatInboxSend2.put("last_message_text", bottomMessageInput.getText().toString().trim());
					ChatInboxSend2.put("last_message_state", "sended");
					ChatInboxSend2.put("push_date", String.valueOf((long)(cc.getTimeInMillis())));
					FirebaseDatabase.getInstance().getReference("skyline/inbox").child(getIntent().getStringExtra("uid")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(ChatInboxSend2);
					bottomMessageInput.setText("");
					FirebaseDatabase.getInstance().getReference("skyline/chats").child(getIntent().getStringExtra("uid")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("typing-message").removeValue();
					ChatSendMap.clear();
					ChatInboxSend.clear();
					ChatInboxSend2.clear();
					ReplyMessageID = "null";
					mMessageReplyLayout.setVisibility(View.GONE);
				}
			}
		});
		
		bottomAudioRecorderCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_AudioRecorderStop();
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
		_stateColor(0xFFFFFFFF, 0xFFFFFFFF);
		_viewGraphics(back, 0xFFFFFFFF, 0xFFEEEEEE, 300, 0, Color.TRANSPARENT);
		_viewGraphics(more, 0xFFFFFFFF, 0xFFEEEEEE, 300, 0, Color.TRANSPARENT);
		_viewGraphics(topProfileLayout, 0xFFFFFFFF, 0xFFEEEEEE, 300, 0, Color.TRANSPARENT);
		_viewGraphics(mMessageReplyLayoutBodyCancel, 0xFFFFFFFF, 0xFFEEEEEE, 300, 0, Color.TRANSPARENT);
		_ImageColor(mMessageReplyLayoutBodyIc, 0xFF2196F3);
		_ImageColor(bottomStickerButton, 0xFF757575);
		_ImageColor(bottomFileAttach, 0xFF757575);
		_ImageColor(bottomMicrophone, 0xFF757575);
		_ImageColor(bottomSendButton, 0xFF2196F3);
		_ScrollingText(topProfileLayoutUsername);
		topProfileCard.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)300, Color.TRANSPARENT));
		bannedUserInfo.setVisibility(View.GONE);
		mMessageReplyLayout.setVisibility(View.GONE);
		bottomFileAttach.setVisibility(View.VISIBLE);
		bottomMicrophone.setVisibility(View.VISIBLE);
		bottomSendButton.setVisibility(View.GONE);
		bottomAudioRecorder.setVisibility(View.GONE);
		SecondUserAvatar = "null";
		ReplyMessageID = "null";
		ChatMessagesLimit = 25;
		bannedUserInfo.setElevation((float)2);
		LinearLayoutManager ChatRecyclerLayoutManager = new LinearLayoutManager(this);
		ChatRecyclerLayoutManager.setReverseLayout(false);
		ChatRecyclerLayoutManager.setStackFromEnd(true);
		ChatMessagesListRecycler.setLayoutManager(ChatRecyclerLayoutManager);
		ChatMessagesListRecycler.setAdapter(new ChatMessagesListRecyclerAdapter(ChatMessagesList));
		_getUserReference();
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		FirebaseDatabase.getInstance().getReference("skyline/chats").child(getIntent().getStringExtra("uid")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("typing-message").removeValue();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		FirebaseDatabase.getInstance().getReference("skyline/chats").child(getIntent().getStringExtra("uid")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("typing-message").removeValue();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		FirebaseDatabase.getInstance().getReference("skyline/chats").child(getIntent().getStringExtra("uid")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("typing-message").removeValue();
	}
	public void _stateColor(final int _statusColor, final int _navigationColor) {
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		getWindow().setStatusBarColor(_statusColor);
		getWindow().setNavigationBarColor(_navigationColor);
	}
	
	
	public void _viewGraphics(final View _view, final int _onFocus, final int _onRipple, final double _radius, final double _stroke, final int _strokeColor) {
		android.graphics.drawable.GradientDrawable GG = new android.graphics.drawable.GradientDrawable();
		GG.setColor(_onFocus);
		GG.setCornerRadius((float)_radius);
		GG.setStroke((int) _stroke, _strokeColor);
		android.graphics.drawable.RippleDrawable RE = new android.graphics.drawable.RippleDrawable(new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{ _onRipple}), GG, null);
		_view.setBackground(RE);
	}
	
	
	public void _ImageColor(final ImageView _image, final int _color) {
		_image.setColorFilter(_color,PorterDuff.Mode.SRC_ATOP);
	}
	
	
	public void _messageOverviewPopup(final View _view, final double _position, final ArrayList<HashMap<String, Object>> _data) {
		View pop1V = getLayoutInflater().inflate(R.layout.skyline_chat_messages_menu_popup_custom, null);
		final PopupWindow pop1 = new PopupWindow(pop1V, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		pop1.setFocusable(true);
		pop1.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
		final LinearLayout main = pop1V.findViewById(R.id.main);
		final LinearLayout edit = pop1V.findViewById(R.id.edit);
		final LinearLayout reply = pop1V.findViewById(R.id.reply);
		final LinearLayout copy = pop1V.findViewById(R.id.copy);
		final LinearLayout delete = pop1V.findViewById(R.id.delete);
		pop1.setAnimationStyle(android.R.style.Animation_Dialog);
		int[] location = new int[2];
		View anchorView = _view;
		anchorView.getLocationOnScreen(location);
		
		int screenHeight = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
		int halfScreenHeight = screenHeight / 2;
		int anchorViewHeight = anchorView.getHeight();
		
		if (location[1] < halfScreenHeight) {
			pop1.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] + anchorViewHeight);
		} else if (location[1] > halfScreenHeight) {
			pop1.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] - pop1.getHeight());
		} else {
			pop1.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] + anchorViewHeight / 2 - pop1.getHeight() / 2);
		}
		if (_data.get((int)_position).get("uid").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
			main.setGravity(Gravity.CENTER | Gravity.RIGHT);
			edit.setVisibility(View.VISIBLE);
		} else {
			main.setGravity(Gravity.CENTER | Gravity.LEFT);
			edit.setVisibility(View.GONE);
		}
		_viewGraphics(edit, 0xFFFFFFFF, 0xFFEEEEEE, 0, 0, 0xFFFFFFFF);
		_viewGraphics(reply, 0xFFFFFFFF, 0xFFEEEEEE, 0, 0, 0xFFFFFFFF);
		_viewGraphics(copy, 0xFFFFFFFF, 0xFFEEEEEE, 0, 0, 0xFFFFFFFF);
		_viewGraphics(delete, 0xFFFFFFFF, 0xFFEEEEEE, 0, 0, 0xFFFFFFFF);
		main.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				pop1.dismiss();
			}
		});
		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				pop1.dismiss();
			}
		});
		reply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				ReplyMessageID = _data.get((int)_position).get("key").toString();
				if (_data.get((int)_position).get("uid").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
					mMessageReplyLayoutBodyRightUsername.setText(FirstUserName);
				} else {
					mMessageReplyLayoutBodyRightUsername.setText(SecondUserName);
				}
				mMessageReplyLayoutBodyRightMessage.setText(_data.get((int)_position).get("message_text").toString());
				mMessageReplyLayout.setVisibility(View.VISIBLE);
				vbr.vibrate((long)(48));
				pop1.dismiss();
			}
		});
		copy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				((ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", _data.get((int)_position).get("message_text").toString()));
				vbr.vibrate((long)(48));
				pop1.dismiss();
			}
		});
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_DeleteMessageDialog(_data, _position);
				pop1.dismiss();
			}
		});
	}
	
	
	public void _setMargin(final View _view, final double _r, final double _l, final double _t, final double _b) {
		float dpRatio = new c(this).getContext().getResources().getDisplayMetrics().density;
		int right = (int)(_r * dpRatio);
		int left = (int)(_l * dpRatio);
		int top = (int)(_t * dpRatio);
		int bottom = (int)(_b * dpRatio);
		
		boolean _default = false;
		
		ViewGroup.LayoutParams p = _view.getLayoutParams();
		if (p instanceof LinearLayout.LayoutParams) {
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)p;
			lp.setMargins(left, top, right, bottom);
			_view.setLayoutParams(lp);
		}
		else if (p instanceof RelativeLayout.LayoutParams) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)p;
			lp.setMargins(left, top, right, bottom);
			_view.setLayoutParams(lp);
		}
		else if (p instanceof TableRow.LayoutParams) {
			TableRow.LayoutParams lp = (TableRow.LayoutParams)p;
			lp.setMargins(left, top, right, bottom);
			_view.setLayoutParams(lp);
		}
		
		
	}
	
	class c {
		Context co;
		public <T extends Activity> c(T a) {
			co = a;
		}
		public <T extends Fragment> c(T a) {
			co = a.getActivity();
		}
		public <T extends DialogFragment> c(T a) {
			co = a.getActivity();
		}
		
		public Context getContext() {
			return co;
		}
		
	}
	
	
	{
		
	}
	
	
	public void _getUserReference() {
		DatabaseReference getUserReference = FirebaseDatabase.getInstance().getReference("skyline/users").child(getIntent().getStringExtra("uid"));
		getUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if(dataSnapshot.exists()) {
					if (dataSnapshot.child("banned").getValue(String.class).equals("true")) {
						bannedUserInfo.setVisibility(View.VISIBLE);
						topProfileLayoutProfileImage.setImageResource(R.drawable.banned_avatar);
						SecondUserAvatar = "null_banned";
						topProfileLayoutStatus.setTextColor(0xFF9E9E9E);
						topProfileLayoutStatus.setText(getResources().getString(R.string.offline));
					} else {
						bannedUserInfo.setVisibility(View.GONE);
						if (dataSnapshot.child("avatar").getValue(String.class).equals("null")) {
							topProfileLayoutProfileImage.setImageResource(R.drawable.avatar);
							SecondUserAvatar = "null";
						} else {
							Glide.with(getApplicationContext()).load(Uri.parse(dataSnapshot.child("avatar").getValue(String.class))).into(topProfileLayoutProfileImage);
							SecondUserAvatar = dataSnapshot.child("avatar").getValue(String.class);
						}
					}
					if (dataSnapshot.child("nickname").getValue(String.class).equals("null")) {
						topProfileLayoutUsername.setText("@" + dataSnapshot.child("username").getValue(String.class));
						SecondUserName = "@" + dataSnapshot.child("username").getValue(String.class);
					} else {
						topProfileLayoutUsername.setText(dataSnapshot.child("nickname").getValue(String.class));
						SecondUserName = dataSnapshot.child("nickname").getValue(String.class);
					}
					if (dataSnapshot.child("status").getValue(String.class).equals("online")) {
						topProfileLayoutStatus.setText(getResources().getString(R.string.online));
						topProfileLayoutStatus.setTextColor(0xFF2196F3);
					} else {
						if (dataSnapshot.child("status").getValue(String.class).equals("offline")) {
							topProfileLayoutStatus.setText(getResources().getString(R.string.offline));
						} else {
							_setUserLastSeen(Double.parseDouble(dataSnapshot.child("status").getValue(String.class)), topProfileLayoutStatus);
						}
						topProfileLayoutStatus.setTextColor(0xFF757575);
					}
					if (dataSnapshot.child("gender").getValue(String.class).equals("hidden")) {
						topProfileLayoutGenderBadge.setVisibility(View.GONE);
					} else {
						if (dataSnapshot.child("gender").getValue(String.class).equals("male")) {
							topProfileLayoutGenderBadge.setImageResource(R.drawable.male_badge);
							topProfileLayoutGenderBadge.setVisibility(View.VISIBLE);
						} else {
							if (dataSnapshot.child("gender").getValue(String.class).equals("female")) {
								topProfileLayoutGenderBadge.setImageResource(R.drawable.female_badge);
								topProfileLayoutGenderBadge.setVisibility(View.VISIBLE);
							}
						}
					}
					if (dataSnapshot.child("account_type").getValue(String.class).equals("admin")) {
						topProfileLayoutVerifiedBadge.setImageResource(R.drawable.admin_badge);
						topProfileLayoutVerifiedBadge.setVisibility(View.VISIBLE);
					} else {
						if (dataSnapshot.child("account_type").getValue(String.class).equals("moderator")) {
							topProfileLayoutVerifiedBadge.setImageResource(R.drawable.moderator_badge);
							topProfileLayoutVerifiedBadge.setVisibility(View.VISIBLE);
						} else {
							if (dataSnapshot.child("account_type").getValue(String.class).equals("support")) {
								topProfileLayoutVerifiedBadge.setImageResource(R.drawable.support_badge);
								topProfileLayoutVerifiedBadge.setVisibility(View.VISIBLE);
							} else {
								if (dataSnapshot.child("account_premium").getValue(String.class).equals("true")) {
									topProfileLayoutVerifiedBadge.setImageResource(R.drawable.premium_badge);
									topProfileLayoutVerifiedBadge.setVisibility(View.VISIBLE);
								} else {
									if (dataSnapshot.child("verify").getValue(String.class).equals("true")) {
										topProfileLayoutVerifiedBadge.setImageResource(R.drawable.verified_badge);
										topProfileLayoutVerifiedBadge.setVisibility(View.VISIBLE);
									} else {
										topProfileLayoutVerifiedBadge.setVisibility(View.GONE);
									}
								}
							}
						}
					}
				} else {
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				
			}
		});
		DatabaseReference getFirstUserName = FirebaseDatabase.getInstance().getReference("skyline/users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
		getFirstUserName.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if(dataSnapshot.exists()) {
					if (dataSnapshot.child("nickname").getValue(String.class).equals("null")) {
						FirstUserName = "@" + dataSnapshot.child("username").getValue(String.class);
					} else {
						FirstUserName = dataSnapshot.child("nickname").getValue(String.class);
					}
				} else {
					
				}
			}
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				
			}
		});
		
		_getChatMessagesRef();
	}
	
	
	public void _getChatMessagesRef() {
		Query getChatsMessages = FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getIntent().getStringExtra("uid")).limitToLast((int)ChatMessagesLimit);
		getChatsMessages.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if(dataSnapshot.exists()) {
					ChatMessagesListRecycler.setVisibility(View.VISIBLE);
					noChatText.setVisibility(View.GONE);
					ChatMessagesList.clear();
					
					try {
						GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
						for (DataSnapshot _data : dataSnapshot.getChildren()) {
							HashMap<String, Object> _map = _data.getValue(_ind);
							ChatMessagesList.add(_map);
						}
					} catch (Exception _e) {
						_e.printStackTrace();
					}
					
					if (ChatMessagesList.size() > ChatInitialSize) {
						ChatMessagesListRecycler.getAdapter().notifyDataSetChanged();
						ChatMessagesListRecycler.scrollToPosition(ChatMessagesList.size() - 1);
					} else {
						ChatMessagesListRecycler.getAdapter().notifyDataSetChanged();
					}
					
					ChatInitialSize = ChatMessagesList.size();
				} else {
					ChatMessagesListRecycler.setVisibility(View.GONE);
					noChatText.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				
			}
		});
	}
	
	
	public void _AudioRecorderStart() {
		cc = Calendar.getInstance();
		recordMs = 0;
		bottom.setVisibility(View.GONE);
		bottomAudioRecorder.setVisibility(View.VISIBLE);
		AudioMessageRecorder = new MediaRecorder();
		
		File getCacheDir = getExternalCacheDir();
		String getCacheDirName = "audio_records";
		File getCacheFolder = new File(getCacheDir, getCacheDirName);
		getCacheFolder.mkdirs();
		File getRecordFile = new File(getCacheFolder, cc.getTimeInMillis() + ".mp3");
		String recordFilePath = getRecordFile.getAbsolutePath();
		
		AudioMessageRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		AudioMessageRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		AudioMessageRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		AudioMessageRecorder.setAudioEncodingBitRate(320000);
		AudioMessageRecorder.setOutputFile(recordFilePath);
		
		try {
			AudioMessageRecorder.prepare();
			AudioMessageRecorder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		vbr.vibrate((long)(48));
		timer = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						recordMs = recordMs + 500;
						bottomAudioRecorderTime.setText(_getDurationString((long)recordMs));
					}
				});
			}
		};
		_timer.scheduleAtFixedRate(timer, (int)(0), (int)(500));
		
	}
	
	
	public void _AudioRecorderStop() {
		bottom.setVisibility(View.VISIBLE);
		bottomAudioRecorder.setVisibility(View.GONE);
		if (AudioMessageRecorder != null) {
			AudioMessageRecorder.stop();
			AudioMessageRecorder.release();
			AudioMessageRecorder = null;
		}
		vbr.vibrate((long)(48));
		timer.cancel();
	}
	
	
	public String _getDurationString(final long _durationInMillis) {
		long seconds = _durationInMillis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		seconds %= 60;
		minutes %= 60;
		
		if (hours > 0) {
			return String.format("%02d:%02d:%02d", hours, minutes, seconds);
		} else {
			return String.format("%02d:%02d", minutes, seconds);
		}
	}
	
	
	public void _getOldChatMessagesRef() {
		ChatMessagesLimit = ChatMessagesLimit + 25;
		{
			ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
			Handler mMainHandler = new Handler(Looper.getMainLooper());
			
			mExecutorService.execute(new Runnable() {
				@Override
				public void run() {
					Query getChatsMessages = FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getIntent().getStringExtra("uid")).limitToLast((int)ChatMessagesLimit);
					getChatsMessages.addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
							mMainHandler.post(new Runnable() {
								@Override
								public void run() {
									if(dataSnapshot.exists()) {
										ChatMessagesListRecycler.setVisibility(View.VISIBLE);
										noChatText.setVisibility(View.GONE);
										ChatMessagesList.clear();
										try {
											GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
											for (DataSnapshot _data : dataSnapshot.getChildren()) {
												HashMap<String, Object> _map = _data.getValue(_ind);
												ChatMessagesList.add(_map);
											}
										} catch (Exception _e) {
											_e.printStackTrace();
										}
										
										ChatMessagesListRecycler.getAdapter().notifyDataSetChanged();
									} else {
										ChatMessagesListRecycler.setVisibility(View.GONE);
										noChatText.setVisibility(View.VISIBLE);
									}
								}
							});
						}
						
						@Override
						public void onCancelled(@NonNull DatabaseError databaseError) {
							
						}
					});
				}
			});
		}
	}
	
	
	public void _DeleteMessageDialog(final ArrayList<HashMap<String, Object>> _data, final double _position) {
		{
			final AlertDialog NewCustomDialog = new AlertDialog.Builder(ChatActivity.this).create();
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
			dialog_message.setText(getResources().getString(R.string.delete_message_dialog_message));
			dialog_yes_button.setText(getResources().getString(R.string.yes));
			dialog_no_button.setText(getResources().getString(R.string.no));
			dialog_yes_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getIntent().getStringExtra("uid")).child(_data.get((int)_position).get("key").toString()).removeValue();
					FirebaseDatabase.getInstance().getReference("skyline/chats").child(getIntent().getStringExtra("uid")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(_data.get((int)_position).get("key").toString()).removeValue();
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
	
	
	public void _ScrollingText(final TextView _view) {
		_view.setSingleLine(true);
		_view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		_view.setSelected(true);
	}
	
	
	public void _setUserLastSeen(final double _currentTime, final TextView _txt) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis((long)_currentTime);
		
		long time_diff = c1.getTimeInMillis() - c2.getTimeInMillis();
		
		long seconds = time_diff / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		long weeks = days / 7;
		long months = days / 30;
		long years = days / 365;
		
		if (seconds < 60) {
			if (seconds < 2) {
				_txt.setText("1 " + getResources().getString(R.string.status_text_seconds));
			} else {
				_txt.setText(String.valueOf(seconds) + " " + getResources().getString(R.string.status_text_seconds));
			}
		} else if (minutes < 60) {
			if (minutes < 2) {
				_txt.setText("1 " + getResources().getString(R.string.status_text_minutes));
			} else {
				_txt.setText(String.valueOf(minutes) + " " + getResources().getString(R.string.status_text_minutes));
			}
		} else if (hours < 24) {
			if (hours < 2) {
				_txt.setText("1 " + getResources().getString(R.string.status_text_hours));
			} else {
				_txt.setText(String.valueOf(hours) + " " + getResources().getString(R.string.status_text_hours));
			}
		} else if (days < 7) {
			if (days < 2) {
				_txt.setText("1 " + getResources().getString(R.string.status_text_days));
			} else {
				_txt.setText(String.valueOf(days) + " " + getResources().getString(R.string.status_text_days));
			}
		} else if (weeks < 4) {
			if (weeks < 2) {
				_txt.setText("1 " + getResources().getString(R.string.status_text_week));
			} else {
				_txt.setText(String.valueOf(weeks) + " " + getResources().getString(R.string.status_text_week));
			}
		} else if (months < 12) {
			if (months < 2) {
				_txt.setText("1 " + getResources().getString(R.string.status_text_month));
			} else {
				_txt.setText(String.valueOf(months) + " " + getResources().getString(R.string.status_text_month));
			}
		} else {
			if (years < 2) {
				_txt.setText("1 " + getResources().getString(R.string.status_text_years));
			} else {
				_txt.setText(String.valueOf(years) + " " + getResources().getString(R.string.status_text_years));
			}
		}
	}
	
	public class ChatMessagesListRecyclerAdapter extends RecyclerView.Adapter<ChatMessagesListRecyclerAdapter.ViewHolder> {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public ChatMessagesListRecyclerAdapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater _inflater = getLayoutInflater();
			View _v = _inflater.inflate(R.layout.skyline_chat_messages_custom_view, null);
			RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			_v.setLayoutParams(_lp);
			return new ViewHolder(_v);
		}
		
		@Override
		public void onBindViewHolder(ViewHolder _holder, final int _position) {
			View _view = _holder.itemView;
			
			final ProgressBar showOldMessagesProgress = _view.findViewById(R.id.showOldMessagesProgress);
			final LinearLayout body = _view.findViewById(R.id.body);
			final androidx.cardview.widget.CardView mProfileCard = _view.findViewById(R.id.mProfileCard);
			final LinearLayout message_layout = _view.findViewById(R.id.message_layout);
			final ImageView mProfileImage = _view.findViewById(R.id.mProfileImage);
			final LinearLayout menuView_d = _view.findViewById(R.id.menuView_d);
			final LinearLayout messageBG = _view.findViewById(R.id.messageBG);
			final LinearLayout my_message_info = _view.findViewById(R.id.my_message_info);
			final LinearLayout mRepliedMessageLayout = _view.findViewById(R.id.mRepliedMessageLayout);
			final androidx.cardview.widget.CardView mMessageImageBody = _view.findViewById(R.id.mMessageImageBody);
			final ImageView typingStatusIcon = _view.findViewById(R.id.typingStatusIcon);
			final TextView message_text = _view.findViewById(R.id.message_text);
			final LinearLayout mRepliedMessageLayoutLeftBar = _view.findViewById(R.id.mRepliedMessageLayoutLeftBar);
			final LinearLayout mRepliedMessageLayoutRightBody = _view.findViewById(R.id.mRepliedMessageLayoutRightBody);
			final TextView mRepliedMessageLayoutUsername = _view.findViewById(R.id.mRepliedMessageLayoutUsername);
			final TextView mRepliedMessageLayoutMessage = _view.findViewById(R.id.mRepliedMessageLayoutMessage);
			final ImageView mMessageImageView = _view.findViewById(R.id.mMessageImageView);
			final TextView date = _view.findViewById(R.id.date);
			final ImageView message_state = _view.findViewById(R.id.message_state);
			
			RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			_view.setLayoutParams(_lp);
			Calendar push = Calendar.getInstance();
			_viewGraphics(body, 0xFFFFFFFF, 0xFFEEEEEE, 0, 0, 0xFFFFFFFF);
			mProfileCard.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)300, Color.TRANSPARENT));
			_ImageColor(typingStatusIcon, 0xFFBDBDBD);
			if (_data.get((int)_position).containsKey("typingMessageStatus")) {
				messageBG.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)24, 0xFFF5F5F5));
				_setMargin(message_layout, 60, 0, 4, 4);
				message_layout.setGravity(Gravity.CENTER | Gravity.LEFT);
				body.setGravity(Gravity.TOP | Gravity.LEFT);
				message_state.setVisibility(View.GONE);
				mProfileCard.setVisibility(View.VISIBLE);
				if (SecondUserAvatar.equals("null_banned")) {
					mProfileImage.setImageResource(R.drawable.banned_avatar);
				} else {
					if (SecondUserAvatar.equals("null")) {
						mProfileImage.setImageResource(R.drawable.avatar);
					} else {
						Glide.with(getApplicationContext()).load(Uri.parse(SecondUserAvatar)).into(mProfileImage);
					}
				}
				showOldMessagesProgress.setVisibility(View.GONE);
				my_message_info.setVisibility(View.GONE);
				message_text.setVisibility(View.GONE);
				typingStatusIcon.setVisibility(View.VISIBLE);
				mMessageImageBody.setVisibility(View.GONE);
				mRepliedMessageLayout.setVisibility(View.GONE);
			} else {
				if (_data.get((int)_position).get("uid").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
					messageBG.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)24, 0xFF2196F3));
					body.setGravity(Gravity.TOP | Gravity.RIGHT);
					message_layout.setGravity(Gravity.CENTER | Gravity.RIGHT);
					_setMargin(message_layout, 0, 60, 0, 0);
					message_text.setTextColor(0xFFFFFFFF);
					message_state.setVisibility(View.VISIBLE);
					mProfileCard.setVisibility(View.GONE);
					mRepliedMessageLayoutLeftBar.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)300, 0xFFFFFFFF));
					mRepliedMessageLayoutUsername.setTextColor(0xFFFFFFFF);
					mRepliedMessageLayoutMessage.setTextColor(0xFFFFFFFF);
				} else {
					messageBG.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)24, 0xFFF5F5F5));
					body.setGravity(Gravity.TOP | Gravity.LEFT);
					message_layout.setGravity(Gravity.CENTER | Gravity.LEFT);
					_setMargin(message_layout, 60, 0, 4, 0);
					message_text.setTextColor(0xFF000000);
					message_state.setVisibility(View.GONE);
					mProfileCard.setVisibility(View.VISIBLE);
					mRepliedMessageLayoutLeftBar.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)300, 0xFF2196F3));
					mRepliedMessageLayoutUsername.setTextColor(0xFF2196F3);
					mRepliedMessageLayoutMessage.setTextColor(0xFF000000);
					if (SecondUserAvatar.equals("null")) {
						mProfileImage.setImageResource(R.drawable.avatar);
					} else {
						Glide.with(getApplicationContext()).load(Uri.parse(SecondUserAvatar)).into(mProfileImage);
					}
				}
				message_text.setText(_data.get((int)_position).get("message_text").toString());
				push.setTimeInMillis((long)(Double.parseDouble(_data.get((int)_position).get("push_date").toString())));
				date.setText(new SimpleDateFormat("HH:mm").format(push.getTime()));
				if (_data.get((int)_position).containsKey("replied_message_id")) {
					{
						ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
						Handler mMainHandler = new Handler(Looper.getMainLooper());
						
						mExecutorService.execute(new Runnable() {
							@Override
							public void run() {
								DatabaseReference getRepliedMessageRef = FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getIntent().getStringExtra("uid")).child(_data.get((int)_position).get("replied_message_id").toString());
								getRepliedMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
									@Override
									public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
										mMainHandler.post(new Runnable() {
											@Override
											public void run() {
												if(dataSnapshot.exists()) {
													if (dataSnapshot.child("uid").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
														mRepliedMessageLayoutUsername.setText(FirstUserName);
													} else {
														mRepliedMessageLayoutUsername.setText(SecondUserName);
													}
													if (dataSnapshot.child("message_text").getValue(String.class) != null) {
														mRepliedMessageLayoutMessage.setText(dataSnapshot.child("message_text").getValue(String.class));
														mRepliedMessageLayout.setVisibility(View.VISIBLE);
													} else {
														mRepliedMessageLayout.setVisibility(View.GONE);
													}
												} else {
													mRepliedMessageLayout.setVisibility(View.GONE);
												}
											}
										});
									}
									
									@Override
									public void onCancelled(@NonNull DatabaseError databaseError) {
										
									}
								});
							}
						});
					}
					
					mRepliedMessageLayout.setVisibility(View.VISIBLE);
				} else {
					mRepliedMessageLayout.setVisibility(View.GONE);
				}
				if (_data.get((int)_position).containsKey("message_image_uri")) {
					Glide.with(getApplicationContext()).load(Uri.parse(_data.get((int)_position).get("message_image_uri").toString())).into(mMessageImageView);
					_setMargin(message_text, 0, 0, 8, 0);
					mMessageImageBody.setVisibility(View.VISIBLE);
				} else {
					_setMargin(message_text, 0, 0, 0, 0);
					mMessageImageBody.setVisibility(View.GONE);
				}
				if (_data.get((int)_position).get("message_state").toString().equals("sended")) {
					message_state.setImageResource(R.drawable.ic_check_black);
					_ImageColor(message_state, 0xFF424242);
				} else {
					if (_data.get((int)_position).get("message_state").toString().equals("seen")) {
						message_state.setImageResource(R.drawable.ic_done_all_black);
						_ImageColor(message_state, 0xFF2196F3);
					}
				}
				if (_data.size() > 24) {
					if (_position == 0) {
						showOldMessagesProgress.setVisibility(View.VISIBLE);
						loadTimer = new TimerTask() {
							@Override
							public void run() {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										_getOldChatMessagesRef();
										showOldMessagesProgress.setVisibility(View.GONE);
									}
								});
							}
						};
						_timer.schedule(loadTimer, (int)(1000));
					} else {
						showOldMessagesProgress.setVisibility(View.GONE);
					}
				} else {
					showOldMessagesProgress.setVisibility(View.GONE);
				}
				body.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View _view) {
						_messageOverviewPopup(menuView_d, _position, _data);
					}
				});
				if (_position == 0) {
					my_message_info.setVisibility(View.GONE);
					if (_data.size() == 1) {
						my_message_info.setVisibility(View.VISIBLE);
					} else {
						if (!_data.get((int)_position).get("uid").toString().equals(_data.get((int)_position + 1).get("uid").toString())) {
							my_message_info.setVisibility(View.VISIBLE);
						}
					}
				} else {
					if (!_data.get((int)_position).get("uid").toString().equals(_data.get((int)_position - 1).get("uid").toString())) {
						my_message_info.setVisibility(View.GONE);
						if (_position == (_data.size() - 1)) {
							my_message_info.setVisibility(View.VISIBLE);
						} else {
							if (!_data.get((int)_position).get("uid").toString().equals(_data.get((int)_position + 1).get("uid").toString())) {
								my_message_info.setVisibility(View.VISIBLE);
							}
						}
					} else {
						if (_position == (_data.size() - 1)) {
							my_message_info.setVisibility(View.VISIBLE);
						} else {
							if (_data.get((int)_position).get("uid").toString().equals(_data.get((int)_position - 1).get("uid").toString()) && _data.get((int)_position).get("uid").toString().equals(_data.get((int)_position + 1).get("uid").toString())) {
								my_message_info.setVisibility(View.GONE);
							} else {
								if (_data.get((int)_position).get("uid").toString().equals(_data.get((int)_position + 1).get("uid").toString())) {
									my_message_info.setVisibility(View.GONE);
								} else {
									my_message_info.setVisibility(View.VISIBLE);
								}
							}
						}
					}
				}
				if (!_data.get((int)_position).get("uid").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
					if (_data.get((int)_position).get("message_state").toString().equals("sended")) {
						FirebaseDatabase.getInstance().getReference("skyline/chats").child(getIntent().getStringExtra("uid")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(_data.get((int)_position).get("key").toString()).child("message_state").setValue("seen");
						FirebaseDatabase.getInstance().getReference("skyline/chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getIntent().getStringExtra("uid")).child(_data.get((int)_position).get("key").toString()).child("message_state").setValue("seen");
						FirebaseDatabase.getInstance().getReference("skyline/inbox").child(getIntent().getStringExtra("uid")).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("last_message_state").setValue("seen");
					}
				}
				typingStatusIcon.setVisibility(View.GONE);
				message_text.setVisibility(View.VISIBLE);
			}
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