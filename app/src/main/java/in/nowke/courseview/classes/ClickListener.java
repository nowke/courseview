package in.nowke.courseview.classes;

import android.view.View;

/**
 * Created by nav on 27/12/15.
 */
public  interface ClickListener {
    public void onClick(View view, int position);

    public void onLongClick(View view, int position);
}

