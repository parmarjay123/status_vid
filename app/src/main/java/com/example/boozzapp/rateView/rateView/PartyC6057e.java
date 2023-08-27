package com.example.boozzapp.rateView.rateView;

import android.animation.ValueAnimator;

/* renamed from: com.zjsoft.rate.view.e */
class PartyC6057e implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a */
    final /* synthetic */ PartyStarCheckView f18707a;

    PartyC6057e(PartyStarCheckView starCheckView) {
        this.f18707a = starCheckView;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f18707a.invalidate();
    }
}
