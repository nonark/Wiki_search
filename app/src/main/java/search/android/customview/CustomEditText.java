package search.android.customview;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by nhnent on 2017. 5. 2..
 */

public class CustomEditText extends AppCompatEditText {

    OnBackPressListener listener;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && listener != null) {
            listener.onBackPressed();
            return true;
        }

        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnBackPressListner(OnBackPressListener listner) {
        this.listener = listner;
    }

    public interface OnBackPressListener {
        void onBackPressed();
    }
}
