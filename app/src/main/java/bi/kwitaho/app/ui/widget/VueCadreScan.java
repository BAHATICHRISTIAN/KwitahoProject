package bi.kwitaho.app.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** Cadre guide de la caméra : zone assombrie autour d'un carré avec quatre coins. */
public class VueCadreScan extends View {
    private final Paint dim = new Paint();
    private final Paint coin = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF cadre = new RectF();
    private final float densite;

    public VueCadreScan(Context contexte) {
        this(contexte, null);
    }

    public VueCadreScan(Context contexte, @Nullable AttributeSet attributs) {
        super(contexte, attributs);
        densite = contexte.getResources().getDisplayMetrics().density;
        dim.setColor(0x66000000);
        coin.setColor(0xFFFFFFFF);
        coin.setStyle(Paint.Style.STROKE);
        coin.setStrokeWidth(4f * densite);
        coin.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float cote = Math.min(w, h) * 0.78f;
        float cx = w / 2f;
        float cy = h * 0.40f;
        cadre.set(cx - cote / 2f, cy - cote / 2f, cx + cote / 2f, cy + cote / 2f);
    }

    @Override
    protected void onDraw(@NonNull Canvas c) {
        int w = getWidth();
        int h = getHeight();
        c.drawRect(0, 0, w, cadre.top, dim);
        c.drawRect(0, cadre.bottom, w, h, dim);
        c.drawRect(0, cadre.top, cadre.left, cadre.bottom, dim);
        c.drawRect(cadre.right, cadre.top, w, cadre.bottom, dim);

        float longueur = 32f * densite;
        float l = cadre.left, t = cadre.top, r = cadre.right, b = cadre.bottom;
        c.drawLine(l, t, l + longueur, t, coin);
        c.drawLine(l, t, l, t + longueur, coin);
        c.drawLine(r, t, r - longueur, t, coin);
        c.drawLine(r, t, r, t + longueur, coin);
        c.drawLine(l, b, l + longueur, b, coin);
        c.drawLine(l, b, l, b - longueur, coin);
        c.drawLine(r, b, r - longueur, b, coin);
        c.drawLine(r, b, r, b - longueur, coin);
    }
}
