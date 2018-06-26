package com.sergiocruz.capstone.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
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
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiocruz.capstone.BuildConfig;
import com.sergiocruz.capstone.R;
import com.sergiocruz.capstone.databinding.FragmentLoginBinding;
import com.sergiocruz.capstone.model.User;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    FragmentLoginBinding binding;

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
                //binding.passwordEditText.requestFocus();
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
            binding.emailEditText.setError(errorMessage);
            binding.emailEditText.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            checkEmailExists(email);
        }
    }

    private void validatePassword() {
        if (!isEmailValid) {
            slideToEmail();
            binding.emailEditText.setError("Invalid e-mail");
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
            binding.passwordEditText.setError(getString(R.string.error_invalid_password));
            binding.passwordEditText.requestFocus();
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
                        Log.d("Sergio >", "signInWithEmail:success");
                        message = "Signed In With Email successfully!";
                    } else {
                        Log.w("Sergio >", "signInWithEmail:failure");
                        message = "Signed In With Email Failed";
                        slideToEmail();
                    }
                    updateUI(task.isSuccessful(), message);

                })
                .addOnFailureListener(e -> {

                    // Sign in fails
                    Log.w("Sergio >", "signInWithEmail:failure", e.getCause());
                    binding.emailEditText.setError(e.getLocalizedMessage());
                    binding.passwordEditText.setError("Could not log in");
                    slideToEmail();
                    updateUI(false, e.getLocalizedMessage());

                });
    }

    private void checkEmailExists(String email) {
        showProgress(true);
        mFirebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

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
                    Log.w("Sergio>", this + "onComplete: \n" + "= " + task.getException());
                    binding.emailEditText.setError("Unregistered e-mail");
                    binding.emailEditText.requestFocus();
                    isEmailValid = false;
                    showRegisterDialog(email);
                }
                showProgress(false);
            }
        });
    }

    private void showRegisterDialog(String email) {
        RegisterDialog dialog = RegisterDialog.newInstance(this, this, email);
        dialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), null);
    }

    @Override
    public void onOKClicked() {
        binding.emailSignInButton.setText("Register");
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
        // Additional validation?
        return email.contains("@");
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
                        Log.d("Sergio >", "createUserWithEmail:success");
                        message = "Created new Email/Password login!";
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sergio >", "createUserWithEmail:failure", task.getException());
                        message = "Authentication failed.";
                    }
                    updateUI(task.isSuccessful(), message);
                })
                .addOnFailureListener(e -> {
                    Log.w("Sergio >", "signInWithEmail:failure", e.getCause());
                    binding.emailEditText.setError(e.getLocalizedMessage());
                    binding.passwordEditText.setError("Could not log in");
                    binding.emailEditText.requestFocus();

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
                //
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getContext(), "Error: Facebook Login", Toast.LENGTH_LONG).show();
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
                updateUI(false, "twitter login failure");
            }
        });
    }

    private void startAnonymousLogin(View view) {
        mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    String message;

                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d("Sergio> ", "Anonymous signIn:success");
                        message = "Anonymous sign in";
                    } else {
                        // Sign in fails
                        Log.w("Sergio> ", "Anonymous signInWithCredential:failure", task.getException());
                        message = "Anonymous Authentication failed.";
                    }

                    updateUI(task.isSuccessful(), message);
                });
    }

    private void handleAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        Log.d("Sergio> ", "handleAuthWithGoogle:" + googleSignInAccount.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    String message;
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d("Sergio> ", "Google signInWithCredential:success");
                        message = "Google sign in success!";
                    } else {
                        // Sign in fails
                        Log.w("Sergio> ", "Google signInWithCredential:failure", task.getException());
                        message = "Google Authentication failed.";
                    }
                    updateUI(task.isSuccessful(), message);
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("Sergio> ", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    String message;
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d("Sergio> ", "facebook signInWithCredential:success");
                        message = "facebook sign in success";
                    } else {
                        // Sign in fails
                        Log.w("Sergio> ", "signInWithCredential:failure", task.getException());
                        message = "Facebook Authentication failed.";
                    }
                    updateUI(task.isSuccessful(), message);
                });
    }

    private void handleTwitterLoginSession(TwitterSession session) {
        Log.d("Sergio >", "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            String message;
            if (task.isSuccessful()) {
                // Sign in success
                Log.d("sergio >", "Twitter signInWithCredential:success");
                message = "Twitter sign in success";
            } else {
                // Sign in fails
                Log.w("Sergio >", "Twitter signInWithCredential:failure", task.getException());
                message = "Twitter Authentication failed.";
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
            String message = "Authenticated with " + Objects.requireNonNull(currentUser.getProviders()).get(0);
            updateUI(true, message);
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

    private void updateUI(boolean taskIsSuccessful, String message) {
        showProgress(false);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

        if (!taskIsSuccessful) return;

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        String userID = firebaseUser != null ? firebaseUser.getUid() : null;
        DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
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
                    String email = firebaseUser != null ? firebaseUser.getEmail() : null;
                    if (email == null) {
                        email = firebaseUser.getProviderData().get(1).getEmail();
                    }

                    User user = new User(
                            firebaseUser.getUid(),
                            firebaseUser.getDisplayName(),
                            String.valueOf(firebaseUser.getPhotoUrl()),
                            email,
                            firebaseUser.getPhoneNumber(),
                            firebaseUser.getProviders() != null ? firebaseUser.getProviders().get(0) : null,
                            false);

                    writeNewUserToDB(user);
                }
                Log.i("Sergio>", this + " onDataChange\nsnapshot= " +
                        snapshot.getValue() == null ? "null snapshot" : String.valueOf(snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Database Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }

        });

    }

    private void writeNewUserToDB(User newUser) {
        Toast.makeText(getContext(), "Creating new User", Toast.LENGTH_LONG).show();
        Log.i("Sergio>", this + " writeNewUserToDB\nuser= " + newUser.toString());

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(newUser.getUserID()).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        goToMainContainerFragment();
                    } else {
                        Toast.makeText(getContext(), "Could not create new user. Database error!", Toast.LENGTH_LONG).show();
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

    private void enterFullScreen() {
        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getActivity().getWindow().setNavigationBarColor(R.color.transparent);
    }

    private void exitFullScreen() {
        this.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        this.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.videoView.stopPlayback();
        binding.videoView.suspend();
        binding.videoView.setVideoURI(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void onLoginBackPressed() {
        if (isRegistering || isSigningIn) {
            if (binding.passwordInputLayout.getVisibility() == View.VISIBLE) {
                slideToEmail();
                isSigningIn = false;
                isRegistering = false;
                isEmailValid = false;
            }
        } else if (binding.emailInputLayout.getVisibility() == View.VISIBLE) {
            getActivity().finish();
        }
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
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

    private void slideToEmail() {
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

    /**
     * Shows the progress bar
     */
    private void showProgress(final boolean show) {

        binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);

//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//
//        int animationTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
//
//        binding.loginForms.setVisibility(show ? View.GONE : View.VISIBLE);
//        binding.loginForms.animate().setDuration(animationTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                binding.loginForms.setVisibility(show ? View.GONE : View.VISIBLE);
//            }
//
//        });
//
//        binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
//        binding.loginProgress.animate().setDuration(animationTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//            }
//        });

    }


}

