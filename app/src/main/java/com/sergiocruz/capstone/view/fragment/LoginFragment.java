package com.sergiocruz.capstone.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.BuildConfig;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.model.User;
import com.sergiocruz.capstone.repository.FirebaseRepository;
import com.sergiocruz.capstone.view.RegisterDialog;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * A login screen that offers login via multiple providers
 * including email/password through Firebase Authentication.
 */
public class LoginFragment extends Fragment implements RegisterDialog.OnOKClickedCallback, RegisterDialog.OnCancelClickedCallback {

    // onActivityResult Request Codes
    public static final int RC_GOOGLE = 3;
    public static final int RC_FACEBOOK = 64206;
    public static final int RC_TWITTER = 140;

    // Facebook permissions
    private static final String EMAIL_PERMISSION = "email";
    private static final String PUBLIC_PROFILE_PERMISSION = "public_profile";

    // UI android dataBinding references
    com.sergiocruz.capstone.databinding.FragmentLoginBinding binding;

    private TwitterLoginButton twitterLoginButton;

    // Keep track of the login task to ensure we can cancel it if requested.
    private CallbackManager fbCallbackManager;
    private FirebaseAuth mFirebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private boolean isEmailValid = false;
    private boolean isPasswordValid = false;
    private boolean isRegistering = false;
    private boolean isSigningIn = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeTwitter();

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        enterFullScreen();

        binding.emailEditText.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_NEXT) {
                validateEmail();
                return true;
            }
            return false;
        });

        binding.passwordEditText.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_GO || id == EditorInfo.IME_NULL) {
                validatePassword();
                return true;
            }
            return false;
        });

        binding.emailSignInButton.setOnClickListener(this::startEmailPasswordLogin);

        binding.skipLogin.setOnClickListener(this::startAnonymousLogin);

        setupAppLoginOptions();
        setupTitle();

        return binding.getRoot();
    }

    private void setupTitle() {
        // Shadow bellow the Text
        binding.travel.setShadowLayer(30, 1, 1, Color.BLACK);
        binding.companion.setShadowLayer(30, 1, 1, Color.BLACK);

        Animation slideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        Animation slideInLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        binding.travel.startAnimation(slideInRight);
        binding.companion.startAnimation(slideInLeft);

    }

    private void enterFullScreen() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @SuppressWarnings("unused")
    private void enterNoLimitsScreen() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void exitFullScreen() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//        getActivity().getWindow().getDecorView().setFitsSystemWindows(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        prepareBackgroundVideo();
    }

    private void setupAppLoginOptions() {
        // Initialize Firebase Authentication
        mFirebaseAuth = FirebaseAuth.getInstance();
        setupGoogleSignIn();
        setupFacebookLogin();
        setupTwitterLogin();
    }

    private void startEmailPasswordLogin(View view) {
        if (!isEmailValid) {
            validateEmail();
        } else if (isEmailValid && !isPasswordValid) {
            validatePassword();
        } else if (isRegistering) {
            createNewEmailLogin();
        }
    }

    private void validateEmail() {

        // Clear Errors
        binding.emailEditText.setError(null);
        String email = binding.emailEditText.getText().toString();

        boolean cancel = false;
        String errorMessage = "";

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            errorMessage = getString(R.string.error_field_required);
            cancel = true;
        } else if (!isEmailValid(email)) {
            errorMessage = getString(R.string.error_invalid_email);
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            showErrorHint(binding.emailEditText, errorMessage);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            checkEmailExists(email);
        }
    }

    private void showErrorHint(View view, String errorMessage) {
        if (view instanceof AutoCompleteTextView) {
            ((AutoCompleteTextView) view).setError(errorMessage);
        } else if (view instanceof EditText) {
            ((EditText) view).setError(errorMessage);
        }
        view.requestFocus();
        timeOutErrorHint(view);
    }

    private void timeOutErrorHint(View view) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        if (view instanceof AutoCompleteTextView) {
                            ((AutoCompleteTextView) view).setError(null);
                        } else if (view instanceof EditText) {
                            ((EditText) view).setError(null);
                        }
                    }
                });
            }
        }, 5000);
    }

    private void validatePassword() {
        if (!isEmailValid) {
            slideToEmail();
            showErrorHint(binding.emailEditText, getString(R.string.invalid_email));
            return;
        } else if (isEmailValid && isPasswordValid && isRegistering) {
            createNewEmailLogin();
            return;
        }

        // Reset errors.
        binding.passwordEditText.setError(null);

        // Store values at the time of the login attempt.
        String password = binding.passwordEditText.getText().toString();

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            showErrorHint(binding.passwordEditText, getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            isPasswordValid = true;
            checkEmailPasswordLogin();
        }
    }

    private void checkEmailPasswordLogin() {
        showProgress(true);
        String password = binding.passwordEditText.getText().toString();
        String email = binding.emailEditText.getText().toString();
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    String message;
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Timber.d("Sergio > signInWithEmail:success");
                        message = getString(R.string.email_signin_ok);
                    } else {
                        Timber.w("Sergio > signInWithEmail:failure");
                        message = getString(R.string.email_signin_fail);
                        slideToEmail();
                    }
                    updateUI(task.isSuccessful(), message);

                })
                .addOnFailureListener(e -> {

                    // Sign in fails
                    Timber.w("Sergio > signInWithEmail:failure" + e.getCause() + " Localized message = " + e.getLocalizedMessage());
                    showErrorHint(binding.passwordEditText, getString(R.string.could_not_login));
                    showErrorHint(binding.emailEditText, e.getLocalizedMessage());
                    slideToEmail();
                    updateUI(false, e.getLocalizedMessage());

                });
    }

    private void checkEmailExists(String email) {
        showProgress(true);
        mFirebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {

            SignInMethodQueryResult result = task.getResult();
            List<String> methods = result.getSignInMethods();
            if (methods != null && methods.size() > 0) {
                // Email already present -> ask password
                slideToPassword();
                binding.emailSignInButton.setText(getString(R.string.action_sign_in_short));
                isEmailValid = true;
                isSigningIn = true;
            } else {
                // Email not present -> register?
                Timber.w("Sergio> onComplete: \n" + "= " + task.getException());
                showErrorHint(binding.emailEditText, getString(R.string.unregistered_email));
                isEmailValid = false;
                showRegisterDialog(email);
            }
            showProgress(false);
        });
    }

    private void showRegisterDialog(String email) {
        RegisterDialog dialog = RegisterDialog.newInstance(this, this, email);
        dialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), null);
    }

    @Override
    public void onOKClicked() {
        binding.emailSignInButton.setText(R.string.register);
        slideToPassword();
        isEmailValid = true;
        isRegistering = true;
    }

    @Override
    public void onCancelClicked() {
        isEmailValid = false;
        isRegistering = false;
    }

    private boolean isEmailValid(String email) {
        // Simple regex validation matches everything "xxxxxx@xxxx.xxxx"
        return Pattern.matches(".+@.+\\..+", email);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private void createNewEmailLogin() {
        String password = binding.passwordEditText.getText().toString();
        String email = binding.emailEditText.getText().toString();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Objects.requireNonNull(this.getActivity()), task -> {
                    String message;
                    if (task.isSuccessful()) {
                        // Sign in success
                        Timber.d("Sergio > createUserWithEmail:success");
                        message = getString(R.string.created_email_login);
                    } else {
                        // If sign in fails, display a message to the user.
                        Timber.w("Sergio > createUserWithEmail:failure " + task.getException());
                        message = getString(R.string.auth_failed);
                    }
                    updateUI(task.isSuccessful(), message);
                })
                .addOnFailureListener(e -> {
                    Timber.w("Sergio > signInWithEmail:failure" + e.getCause());
                    showErrorHint(binding.passwordEditText, getString(R.string.could_not_login));
                    isPasswordValid = false;
                    showErrorHint(binding.emailEditText, e.getLocalizedMessage());
                    updateUI(false, e.getLocalizedMessage());
                });
    }

    // Must be initialized before binding views...
    private void initializeTwitter() {
        TwitterConfig twitterConfig = new TwitterConfig.Builder(Objects.requireNonNull(getContext()))
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(BuildConfig.TWITTER_API_KEY, BuildConfig.TWITTER_API_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(twitterConfig);
    }

    private void setupGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by googleSignInOptions.
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getContext()), googleSignInOptions);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        //if (account != null && !account.isExpired()) {
        //    // TODO Jump to pager fragment
        //}

        binding.googleSignInButton.setOnClickListener(this::signInWithGoogle);
    }

    private void setupFacebookLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            Profile profile = Profile.getCurrentProfile();
            String userName = profile.getName();
            Uri userImage = profile.getProfilePictureUri(250, 375); //3:2 aspect ratio
            Timber.i("Sergio> setupFacebookLogin\nuserName= " + userName + "\n" + userImage);
        }

        fbCallbackManager = CallbackManager.Factory.create();

        LoginButton facebookLoginButton = binding.facebookLoginButton;
        facebookLoginButton.setReadPermissions(Arrays.asList(EMAIL_PERMISSION, PUBLIC_PROFILE_PERMISSION));
        // If you are using in a fragment, call facebookLoginButton.setFragment(this);
        facebookLoginButton.setFragment(this);

        // Callback registration
        facebookLoginButton.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                //
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getContext(), R.string.err_fb_login, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setupTwitterLogin() {
        twitterLoginButton = binding.twitterLoginButton;
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Timber.d("Sergio> twitterLogin:success" + result);
                handleTwitterLoginSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Timber.w("Sergio > twitterLogin:failure %s", exception);
                updateUI(false, getString(R.string.err_twitter_login));
            }
        });
    }

    private void startAnonymousLogin(View view) {
        mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    String message;

                    if (task.isSuccessful()) {
                        // Sign in success
                        Timber.d("Sergio > Anonymous signIn:success");
                        message = getString(R.string.anonym_sign_in);
                    } else {
                        // Sign in fails
                        Timber.w("Sergio > Anonymous signInWithCredential:failure " + task.getException());
                        message = getString(R.string.anonym_auth_failed);
                    }

                    updateUI(task.isSuccessful(), message);
                });
    }

    private void handleAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        Timber.d("Sergio > handleAuthWithGoogle:" + googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    String message;
                    if (task.isSuccessful()) {
                        // Sign in success
                        Timber.d("Sergio > Google signInWithCredential:success");
                        message = getString(R.string.google_signin_ok);
                    } else {
                        // Sign in fails
                        Timber.w("Sergio > Google signInWithCredential:failure " + task.getException());
                        message = getString(R.string.google_signin_failed);
                    }
                    updateUI(task.isSuccessful(), message);
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Timber.d("Sergio > handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    String message;
                    if (task.isSuccessful()) {
                        // Sign in success
                        Timber.d("Sergio > facebook signInWithCredential:success");
                        message = getString(R.string.fb_sign_in_ok);
                    } else {
                        // Sign in fails
                        Timber.w("Sergio > signInWithCredential:failure " + task.getException());
                        message = getString(R.string.fb_auth_failed);
                    }
                    updateUI(task.isSuccessful(), message);
                });
    }

    private void handleTwitterLoginSession(TwitterSession session) {
        Timber.d("Sergio > handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            String message;
            if (task.isSuccessful()) {
                // Sign in success
                Timber.d("Sergio > Twitter signInWithCredential:success");
                message = getString(R.string.twitter_sign_in_ok);
            } else {
                // Sign in fails
                Timber.w("Sergio > Twitter signInWithCredential:failure " + task.getException());
                message = getString(R.string.twitter_auth_failed);
            }
            updateUI(task.isSuccessful(), message);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in!
            String message = getString(R.string.auth_with)+ " " + Objects.requireNonNull(currentUser.getProviders()).get(0);
            updateUI(true, message);
        }

    }

    private void signInWithGoogle(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Handle Facebook callback
        if (requestCode == RC_FACEBOOK) {
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Timber.w("Sergio > Google sign in failed. Status code: " + e.getStatusCode() + e.getLocalizedMessage());
            }
        }

        if (requestCode == RC_TWITTER) {
            // Pass the activity result to the Twitter login button.
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }


    }

    private void updateUI(boolean taskIsSuccessful, String message) {
        showProgress(false);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        if (!taskIsSuccessful) return;

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        String userID = firebaseUser != null ? firebaseUser.getUid() : null;
        DatabaseReference mFirebaseDatabase = FirebaseRepository.getInstance().getDatabaseReference();
        DatabaseReference reference = mFirebaseDatabase.child("users/" + userID + "/");

        // Check if the user is in the database, and listen only once
        // addValueEventListener always listens and has to be removed
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // User exists in database or user logged in anonymously, show content
                if (snapshot.exists() || (firebaseUser != null && firebaseUser.isAnonymous())) {
                    goToMainContainerFragment();
                } else {
                    // User does not exist in database and it's not anonymous, create new user
                    User user = convertUser(firebaseUser);
                    writeNewUserToDB(user);
                }
                Timber.i("Sergio> onDataChange\nsnapshot= " + String.valueOf(snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), getString(R.string.db_error) + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    private User convertUser(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {

            String email = firebaseUser.getEmail();
            List<? extends UserInfo> providerData = firebaseUser.getProviderData();
            if (email == null && providerData != null && providerData.size() > 1) {
                email = providerData.get(1).getEmail();
            }

            String authProvider = "";
            List<String> providers = firebaseUser.getProviders();
            if (providers != null && providers.size() > 0) authProvider = providers.get(0);

            return new User(
                    firebaseUser.getUid(),
                    firebaseUser.getDisplayName(),
                    String.valueOf(firebaseUser.getPhotoUrl()),
                    email,
                    firebaseUser.getPhoneNumber(),
                    authProvider,
                    firebaseUser.isAnonymous());

        } else {
            return new User(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    true);
        }
    }

    private void writeNewUserToDB(User newUser) {
        Toast.makeText(getContext(), R.string.creating_new_user, Toast.LENGTH_LONG).show();
        Timber.i("Sergio> writeNewUserToDB\nuser= " + newUser.toString());

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(newUser.getUserID()).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        goToMainContainerFragment();
                    } else {
                        Toast.makeText(getContext(), R.string.could_not_create_user_db_error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goToMainContainerFragment() {
        exitFullScreen();

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_fragment_container, new MainContainerFragment(), MainContainerFragment.class.getSimpleName())
                .add(R.id.frame_content_holder, new HomeFragment(), HomeFragment.class.getSimpleName())
                .addToBackStack(HomeFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.videoView.stopPlayback();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.i("Detaching LoginFragment");
    }

    public void onLoginBackPressed() {
        if (isRegistering || isSigningIn) {
            if (binding.passwordInputLayout.getVisibility() == View.VISIBLE) {
                slideToEmail();
                isSigningIn = false;
                isRegistering = false;
            }
        } else if (binding.emailInputLayout.getVisibility() == View.VISIBLE) {
            getActivity().finish();
        }
    }

    private void prepareBackgroundVideo() {
        // Setup Random Background Video

        // List Raw files
        Field[] fields = R.raw.class.getFields();

        // Pick a random one
        int videoResourceID;
        try {
            int random = new Random().nextInt(fields.length);
            videoResourceID = fields[random].getInt(fields[random]);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        Uri uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + videoResourceID);
        binding.videoView.setVideoURI(uri);

        binding.videoView.setOnPreparedListener(mediaPlayer -> {
            //mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

            // get Video dimensions W/H
            int height = mediaPlayer.getVideoHeight();
            int width = mediaPlayer.getVideoWidth();

            int videoViewLargestSize = height > width ? height : width;
            int videoViewSmallestSize = height < width ? height : width;

            float aspectRatio = (float) videoViewLargestSize / videoViewSmallestSize;

            //Adjust videoView size maintaining aspect ratio
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    (int) (videoViewLargestSize * aspectRatio), (int) (videoViewSmallestSize * aspectRatio));
            binding.videoView.setLayoutParams(layoutParams);

            binding.videoView.start();

            mediaPlayer.setLooping(true);
        });
    }

    private void slideToEmail() {
        isEmailValid = false;
        isPasswordValid = false;
        Animation slideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        slideInRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.emailInputLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.emailEditText.requestFocus();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.emailInputLayout.startAnimation(slideInRight);

        Animation slideOutRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);
        slideOutRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.passwordInputLayout.setError(null);
                binding.passwordEditText.setText(null);
                binding.passwordInputLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.passwordInputLayout.startAnimation(slideOutRight);
    }

    private void slideToPassword() {
        Animation slideOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
        slideOutLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.emailInputLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.emailInputLayout.startAnimation(slideOutLeft);

        Animation slideInLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
        slideInLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.passwordInputLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.passwordEditText.requestFocus();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.passwordInputLayout.startAnimation(slideInLeft);
    }

    /** Shows the progress bar */
    private void showProgress(final boolean show) {
        binding.loginProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }


}

