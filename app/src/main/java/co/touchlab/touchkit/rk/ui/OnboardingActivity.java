package co.touchlab.touchkit.rk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import co.touchlab.touchkit.rk.R;

/**
 * Created by bradleymcdermott on 10/15/15.
 */
public class OnboardingActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_onboarding);


    }

    public void onSignUpClicked(View view)
    {
        startActivity(new Intent(this, EligibilityActivity.class));
    }

    public void onSignInClicked(View view)
    {
        startActivity(new Intent(this, SignInActivity.class));
    }
}
