package com.sergiocruz.capstone.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.sergiocruz.capstone.BuildConfig;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.FragmentEntryLoginBinding;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;

/**
 * A login screen that offers login via email/password.
 */
public class LoginFragment extends Fragment {

    // onActivityResult Request Codes
    public static final int RC_GOOGLE = 3;
    public static final int RC_FACEBOOK = 64206;
    public static final int RC_TWITTER = 140;

    // Facebook permissions
    private static final String EMAIL_PERMISSION = "email";
    private static final String PUBLIC_PROFILE_PERMISSION = "public_profile";

    // UI android dataBinding references
    FragmentEntryLoginBinding binding;

    private TwitterLoginButton twitterLoginButton;

    // Keep track of the login task to ensure we can cancel it if requested.
    private CallbackManager fbCallbackManager;
    private FirebaseAuth mFirebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeTwitter();

        // Inflate view and obtain an instance of the binding class.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_entry_login, container, false);

        // Specify the current fragment as the lifecycle owner.
        binding.setLifecycleOwner(this);

        enterFullScreen();

        binding.passwordEditText.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptEmailPasswordLogin();
                return true;
            }
            return false;
        });

        binding.emailSignInButton.setOnClickListener(view -> attemptEmailPasswordLogin());

        binding.anonymousLogin.setOnClickListener(view -> startAnonymousLogin());

        setupAppLoginOptions();

        return binding.getRoot();
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

    private void createNewEmailLogin(String email, String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sergio >", "createUserWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Sergio >", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void checkEmailPasswordLogin(String email, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sergio >", "signInWithEmail:success");
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sergio >", "signInWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_LONG).show();
                        binding.emailEditText.setError("Could not log in");
                        binding.passwordEditText.setError("Could not log in");
                        binding.emailEditText.requestFocus();
                        updateUI(null);
                    }
                });
    }

    // Must be initialized before binding views... rolling eyes...
    private void initializeTwitter() {
        TwitterConfig twitterConfig = new TwitterConfig.Builder(getContext())
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
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            // TODO Jump to pager fragment
        }

        binding.rootView.findViewById(R.id.google_sign_in_button)
                .setOnClickListener(v -> signInWithGoogle());
    }

    private void setupFacebookLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            Profile profile = Profile.getCurrentProfile();
            String userName = profile.getName();
            Uri userImage = profile.getProfilePictureUri(250, 375); //3:2 aspect ratio
            Log.i("Sergio>", this + " setupFacebookLogin\nuserName= " + userName + "\n" + userImage);
        }

        fbCallbackManager = CallbackManager.Factory.create();

        LoginButton facebookLoginButton = binding.rootView.findViewById(R.id.facebook_login_button);
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
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }

    private void setupTwitterLogin() {
        twitterLoginButton = binding.rootView.findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d("Sergio> ", "twitterLogin:success" + result);
                handleTwitterLoginSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w("Sergio >", "twitterLogin:failure", exception);
                updateUI(null);
            }
        });
    }

    private void startAnonymousLogin() {
        mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sergio >", "signInAnonymously:success");
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sergio >", "signInAnonymously:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    private void handleAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        Log.d("Sergio> ", "handleAuthWithGoogle:" + googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sergio> ", "google signInWithCredential:success");
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        updateUI(user);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sergio> ", "signInWithCredential:failure", task.getException());
                        Snackbar.make(binding.rootView, "Google Authentication Failed.", Snackbar.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("Sergio> ", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sergio> ", "facebook signInWithCredential:success");
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sergio> ", "signInWithCredential:failure", task.getException());
                        Toast.makeText(getContext(), "Facebook Authentication failed.", Toast.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    private void handleTwitterLoginSession(TwitterSession session) {
        Log.d("Sergio >", "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("sergio >", "Twitter signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Sergio >", "Twitter signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Twitter Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in!
            updateUI(currentUser);
        }

    }

    private void signInWithGoogle() {
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
                Log.w("Sergio> ", "Google sign in failed. Status code: " + e.getStatusCode() + e.getLocalizedMessage(), e);
            }
        }

        if (requestCode == RC_TWITTER) {
            // Pass the activity result to the Twitter login button.
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }


    }

    private void updateUI(FirebaseUser currentUser) {
        showProgress(false);
        if (currentUser != null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_fragment, new PagerFragment(), PagerFragment.class.getSimpleName())
                    .commit();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        binding.videoView.suspend();
        binding.videoView.stopPlayback();
        binding.videoView.setVideoURI(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
    }

    private void prepareBackgroundVideo() {
        // Setup Background Video
        Uri uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.beach_fly_over);
        binding.videoView.setVideoURI(uri);
        binding.videoView.start();

        binding.videoView.setOnPreparedListener(mediaPlayer -> {
            //mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

            // get Video dimensions W/H
            int height = mediaPlayer.getVideoHeight();
            int width = mediaPlayer.getVideoWidth();

            int videoViewLargestSize = height > width ? height : width;
            int videoViewSmallestSize = height < width ? height : width;

            float aspectRatio = (float) videoViewLargestSize / videoViewSmallestSize;

            //Adjust videoView size maintaining aspect ratio
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    (int) (videoViewLargestSize * aspectRatio), (int) (videoViewSmallestSize * aspectRatio));
            binding.videoView.setLayoutParams(layoutParams);

            mediaPlayer.setLooping(true);
        });
    }

    private void enterFullScreen() {
        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptEmailPasswordLogin() {

        // Reset errors.
        binding.emailEditText.setError(null);
        binding.passwordEditText.setError(null);

        // Store values at the time of the login attempt.
        String email = binding.emailEditText.getText().toString();
        String password = binding.passwordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            binding.passwordEditText.setError(getString(R.string.error_invalid_password));
            focusView = binding.passwordEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError(getString(R.string.error_field_required));
            focusView = binding.emailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            binding.emailEditText.setError(getString(R.string.error_invalid_email));
            focusView = binding.emailEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            checkEmailPasswordLogin(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            binding.emailLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            binding.emailLoginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    binding.emailLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.loginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.emailLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}

