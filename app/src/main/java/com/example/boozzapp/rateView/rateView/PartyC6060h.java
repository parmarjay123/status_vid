package com.example.boozzapp.rateView.rateView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;

/* renamed from: com.zjsoft.rate.view.h */
class PartyC6060h extends AnimatorListenerAdapter {

    /* renamed from: a */
    final /* synthetic */ PartyStarCheckView f18710a;

    PartyC6060h(PartyStarCheckView starCheckView) {
        this.f18710a = starCheckView;
    }

    public void onAnimationEnd(Animator animator) {
        super.onAnimationEnd(animator);
        if (this.f18710a.f18690i != null) {
            this.f18710a.f18690i.onAnimationEnd(animator);
        }
        ValueAnimator unused = this.f18710a.f18688g = null;
    }
}
