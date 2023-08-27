package com.example.boozzapp.rateView.rateView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;

/* renamed from: com.zjsoft.rate.view.f */
class PartyC6058f extends AnimatorListenerAdapter {

    /* renamed from: a */
    final /* synthetic */ PartyStarCheckView f18708a;

    PartyC6058f(PartyStarCheckView starCheckView) {
        this.f18708a = starCheckView;
    }

    public void onAnimationEnd(Animator animator) {
        super.onAnimationEnd(animator);
        ValueAnimator unused = this.f18708a.f18687f = null;
    }
}
