package com.remon.books;

import androidx.annotation.NonNull;

public interface ItemTouchHelperListener {
    boolean onItemMove(int from_position, int to_position);
    void onItemSwipe(int position);


}
