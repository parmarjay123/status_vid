package com.example.boozzapp.rateView.rateView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;

/* renamed from: com.zjsoft.rate.view.g */
class PartyC6059g extends AnimatorListenerAdapter {

    /* renamed from: a */
    final /* synthetic */ PartyStarCheckView f18709a;

    PartyC6059g(PartyStarCheckView starCheckView) {
        this.f18709a = starCheckView;
    }

    public void onAnimationEnd(Animator animator) {
        super.onAnimationEnd(animator);
        ValueAnimator unused = this.f18709a.f18689h = null;
    }
}
