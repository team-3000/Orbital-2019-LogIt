package com.team3000.logit;

import java.io.Serializable;

public interface EntryListener extends Serializable {
    interface OnDestroyListener extends Serializable {
        void onDestroy(int entryPosition);
    }
}
