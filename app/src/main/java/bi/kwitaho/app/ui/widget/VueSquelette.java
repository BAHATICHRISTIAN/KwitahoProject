package bi.kwitaho.app.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import bi.kwitaho.app.R;

/** Bloc gris animé (effet squelette / shimmer) affiché pendant le chargement. */
public class VueSquelette extends View {
    private final Paint peinture = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectangle = new RectF();
    private final Matrix matrice = new Matrix();
    private LinearGradient degrade;
    private float rayon;
    private float progression = -1f;
    private ValueAnimator animateur;
    private int couleurBase;
    private int couleurReflet;

    public VueSquelette(Context contexte) {
        this(contexte, null);
    }

    public VueSquelette(Context contexte, @Nullable AttributeSet attributs) {
        this(contexte, attributs, 0);
    }

    public VueSquelette(Context contexte, @Nullable AttributeSet attributs, int styleParDefaut) {
        super(contexte, attributs, styleParDefaut);
        float rayonParDefaut = 8f * contexte.getResources().getDisplayMetrics().density;
        if (attributs != null) {
            TypedArray a = contexte.obtainStyledAttributes(attributs, R.styleable.VueSquelette);
            rayon = a.getDimension(R.styleable.VueSquelette_rayonSquelette, rayonParDefaut);
            a.recycle();
        } else {
            rayon = rayonParDefaut;
        }
        couleurBase = ContextCompat.getColor(contexte, R.color.kw_squelette_base);
        couleurReflet = ContextCompat.getColor(contexte, R.color.kw_squelette_reflet);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectangle.set(0, 0, w, h);
        degrade = new LinearGradient(0, 0, Math.max(1, w), 0,
                new int[]{couleurBase, couleurReflet, couleurBase},
                new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (degrade == null) return;
        matrice.setTranslate(progression * getWidth(), 0f);
        degrade.setLocalMatrix(matrice);
        peinture.setShader(degrade);
        canvas.drawRoundRect(rectangle, rayon, rayon, peinture);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isShown()) demarrer();
    }

    @Override
    protected void onDetachedFromWindow() {
        arreter();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View vueModifiee, int visibilite) {
        super.onVisibilityChanged(vueModifiee, visibilite);
        if (isShown()) demarrer();
        else arreter();
    }

    private void demarrer() {
        if (animateur != null) return;
        animateur = ValueAnimator.ofFloat(-1f, 1f);
        animateur.setDuration(1200L);
        animateur.setRepeatCount(ValueAnimator.INFINITE);
        animateur.setInterpolator(new LinearInterpolator());
        animateur.addUpdateListener(a -> {
            progression = (float) a.getAnimatedValue();
            invalidate();
        });
        animateur.start();
    }

    private void arreter() {
        if (animateur != null) {
            animateur.cancel();
            animateur = null;
        }
    }
}
