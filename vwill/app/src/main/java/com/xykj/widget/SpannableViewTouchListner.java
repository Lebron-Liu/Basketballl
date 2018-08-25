package com.xykj.widget;

import android.text.Layout;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SpannableViewTouchListner implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        boolean result = false;
        int action = event.getAction();
        TextView widget = (TextView) view;
        Spannable buffer = Spannable.Factory.getInstance().newSpannable(widget.getText());
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] links = buffer.getSpans(off, off, ClickableSpan.class);

            if (links.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    links[0].onClick(widget);
                }
                result = true;
            }
        }
        return result;
    }
}
