package com.example.boozzapp.rateView;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.boozzapp.R;
import com.example.boozzapp.rateView.rateView.PartyStarCheckView;
import com.example.boozzapp.utils.Constants;
import com.example.boozzapp.utils.StoreUserData;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.analytics.FirebaseAnalytics;

public class PartyRateDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public PartyStarCheckView rate_star_1;
    public PartyStarCheckView rate_star_2;
    public PartyStarCheckView rate_star_3;
    public PartyStarCheckView rate_star_4;
    public PartyStarCheckView rate_star_5;
    public int f15634n = 0;
    ImageView rate_emoji;
    TextView lib_rate_button, rate_result_title, rate_result_tip;
    AppCompatTextView rate_tip;
    LinearLayout lib_rate_button_bg;

    public PartyRateDialog(Activity a) {
        super(a, R.style.mydialog);
        c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setContentView(R.layout.dialog_rate);

        rate_emoji = (ImageView) findViewById(R.id.rate_emoji);
        lib_rate_button_bg = (LinearLayout) findViewById(R.id.lib_rate_button_bg);
        lib_rate_button = (TextView) findViewById(R.id.lib_rate_button);
        rate_result_title = (TextView) findViewById(R.id.rate_result_title);
        rate_tip = (AppCompatTextView) findViewById(R.id.rate_tip);
        rate_result_tip = (TextView) findViewById(R.id.rate_result_tip);
        rate_emoji.setImageResource(R.drawable.lib_rate_emoji_star_0);
        rate_tip.setVisibility(View.VISIBLE);
        rate_result_title.setVisibility(View.INVISIBLE);
        rate_result_tip.setVisibility(View.INVISIBLE);
        lib_rate_button_bg.setEnabled(false);
        lib_rate_button.setAlpha(0.5f);
        lib_rate_button_bg.setAlpha(0.5f);
        lib_rate_button.setText(c.getResources().getString(R.string.lib_rate_btn_rate).toUpperCase());
        rate_star_1 = (PartyStarCheckView) findViewById(R.id.rate_star_1);
        rate_star_2 = (PartyStarCheckView) findViewById(R.id.rate_star_2);
        rate_star_3 = (PartyStarCheckView) findViewById(R.id.rate_star_3);
        rate_star_4 = (PartyStarCheckView) findViewById(R.id.rate_star_4);
        rate_star_5 = (PartyStarCheckView) findViewById(R.id.rate_star_5);

        rate_star_1.setOnClickListener(this);
        rate_star_2.setOnClickListener(this);
        rate_star_3.setOnClickListener(this);
        rate_star_4.setOnClickListener(this);
        rate_star_5.setOnClickListener(this);

        lib_rate_button_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                if (f15634n >= 5) {
                    launchMarket();
                    Bundle bundle = new Bundle();
                    bundle.putString("party_rating_" + f15634n, "party_rating_" + f15634n);
                    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                    mFirebaseAnalytics.logEvent("party_rating_" + f15634n, bundle);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("party_rating_" + f15634n, "party_rating_" + f15634n);
                    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(c);
                    mFirebaseAnalytics.logEvent("party_rating_" + f15634n, bundle);
                    Toast.makeText(c, "Thanks for your rating!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        boolean z9 = false;
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;

        if (id == R.id.rate_star_1) {
            if (f15634n == 1) {
                f15634n = 0;
                rate_star_1.setCheck(false);
                z4 = false;
            } else {
                z4 = f15634n == 0;
                f15634n = 1;
                rate_star_1.setCheck(true);
                rate_star_2.setCheck(false);
                rate_star_3.setCheck(false);
                rate_star_4.setCheck(false);
                rate_star_5.setCheck(false);
            }
            m18841a();
        } else if (id == R.id.rate_star_2) {
            if (f15634n == 2) {
                f15634n = 1;
                rate_star_2.setCheck(false);
                z3 = false;
            } else {
                z3 = f15634n == 0;
                f15634n = 2;
                rate_star_1.setCheck(true);
                rate_star_2.setCheck(true);
                rate_star_3.setCheck(false);
                rate_star_4.setCheck(false);
                rate_star_5.setCheck(false);
            }
            m18841a();
        } else if (id == R.id.rate_star_3) {
            if (f15634n == 3) {
                f15634n = 2;
                rate_star_3.setCheck(false);
                z2 = false;
            } else {
                z2 = f15634n == 0;
                f15634n = 3;
                rate_star_1.setCheck(true);
                rate_star_2.setCheck(true);
                rate_star_3.setCheck(true);
                rate_star_4.setCheck(false);
                rate_star_5.setCheck(false);
            }
            m18841a();
        } else if (id == R.id.rate_star_4) {
            if (f15634n == 4) {
                f15634n = 3;
                rate_star_4.setCheck(false);
                z = false;
            } else {
                z = f15634n == 0;
                f15634n = 4;
                rate_star_1.setCheck(true);
                rate_star_2.setCheck(true);
                rate_star_3.setCheck(true);
                rate_star_4.setCheck(true);
                rate_star_5.setCheck(false);
            }
            m18841a();
        } else if (id == R.id.rate_star_5) {
            if (f15634n == 5) {
                f15634n = 4;
                rate_star_5.setCheck(false);
            } else {
                if (f15634n == 0) {
                    z9 = true;
                }
                f15634n = 5;
                rate_star_1.setCheck(true);
                rate_star_2.setCheck(true);
                rate_star_3.setCheck(true);
                rate_star_4.setCheck(true);
                rate_star_5.setCheck(true);
            }
            m18841a();
        }
    }

    public void m18841a() {
        int i5 = f15634n;
        if (i5 != 0) {
            lib_rate_button_bg.setEnabled(true);
            lib_rate_button.setAlpha(1f);
            lib_rate_button_bg.setAlpha(1f);
            rate_tip.setVisibility(View.GONE);
            rate_result_title.setVisibility(View.VISIBLE);
            rate_result_tip.setVisibility(View.VISIBLE);
            if (i5 == 1) {
                rate_emoji.setImageResource(R.drawable.lib_rate_emoji_star_1);
                lib_rate_button.setText(R.string.lib_rate_btn_rate);
                rate_result_title.setText(R.string.lib_rate_oh_no);
                rate_result_tip.setText(R.string.lib_rate_leave_feedback);
            } else if (i5 == 2) {
                rate_emoji.setImageResource(R.drawable.lib_rate_emoji_star_2);
                lib_rate_button.setText(R.string.lib_rate_btn_rate);
                rate_result_title.setText(R.string.lib_rate_oh_no);
                rate_result_tip.setText(R.string.lib_rate_leave_feedback);
            } else if (i5 == 3) {
                rate_emoji.setImageResource(R.drawable.lib_rate_emoji_star_3);
                lib_rate_button.setText(R.string.lib_rate_btn_rate);
                rate_result_title.setText(R.string.lib_rate_oh_no);
                rate_result_tip.setText(R.string.lib_rate_leave_feedback);
            } else if (i5 == 4) {
                rate_emoji.setImageResource(R.drawable.lib_rate_emoji_star_4);
                lib_rate_button.setText(R.string.lib_rate_btn_rate);
                rate_result_title.setText(R.string.lib_rate_like_you);
                rate_result_tip.setText(R.string.lib_rate_thanks_feedback);
            } else if (i5 == 5) {
                rate_emoji.setImageResource(R.drawable.lib_rate_emoji_star_5);
                lib_rate_button.setText(R.string.lib_rate_btn_rate);
                rate_result_title.setText(R.string.lib_rate_like_you);
                rate_result_tip.setText(R.string.lib_rate_thanks_feedback);
            }
        } else {
            lib_rate_button_bg.setEnabled(false);
            lib_rate_button.setAlpha(0.5f);
            lib_rate_button_bg.setAlpha(0.5f);
            rate_tip.setVisibility(View.VISIBLE);
            rate_result_title.setVisibility(View.GONE);
            rate_result_tip.setVisibility(View.GONE);
        }
    }

    private void launchMarket() {
        ReviewManager manager = ReviewManagerFactory.create(c);

        com.google.android.play.core.tasks.Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                com.google.android.play.core.tasks.Task<Void> launchReviewFlow = manager.launchReviewFlow(c, reviewInfo);
                launchReviewFlow.addOnCompleteListener((com.google.android.play.core.tasks.OnCompleteListener<Void>) task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(c, "Thanks for your rating!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

