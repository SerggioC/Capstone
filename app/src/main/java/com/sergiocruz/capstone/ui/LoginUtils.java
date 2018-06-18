package com.sergiocruz.capstone.ui;

public class LoginUtils {

// Convert an anonymous account to a permanent account
// When an anonymous user signs up to your app, you might want to allow
// them to continue their work with their new account—for example,
// you might want to make the items the user added to their shopping cart
// before they signed up available in their new account's shopping cart.
// To do so, complete the following steps:
//
// When the user signs up, complete the sign-in flow for the user's
// authentication provider up to, but not including, calling one of the
// FirebaseAuth.signInWith methods. For example, get the user's Google ID token,
// Facebook access token, or email address and password.
//
// Get an AuthCredential for the new authentication provider:
//
//    Google Sign-In
//    AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);
//    //Facebook Login
//    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//    //Email-password sign-in
//    AuthCredential credential = EmailAuthProvider.getCredential(email, password);
//    //Pass the AuthCredential object to the sign-in user's linkWithCredential method:
//
//            mAuth.getCurrentUser().linkWithCredential(credential)
//        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//        @Override
//        public void onComplete(@NonNull Task<AuthResult> task) {
//            if (task.isSuccessful()) {
//                Log.d("", "linkWithCredential:success");
//                FirebaseUser user = task.getResult().getUser();
//                updateUI(user);
//            } else {
//                Log.w("", "linkWithCredential:failure", task.getException());
//                Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show();
//                updateUI(null);
//            }
//
//            // ...
//        }
//    });

//    AnonymousAuthActivity.java
//    If the call to linkWithCredential succeeds, the user's new account can access the anonymous account's Firebase data.

}
