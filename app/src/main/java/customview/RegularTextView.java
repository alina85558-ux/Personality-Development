package customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class RegularTextView extends TextView {

    public RegularTextView(Context context) {
        super(context);
        init();
    }

    public RegularTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RegularTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-Regular.otf");
        setTypeface(tf, 1);

    }
}
