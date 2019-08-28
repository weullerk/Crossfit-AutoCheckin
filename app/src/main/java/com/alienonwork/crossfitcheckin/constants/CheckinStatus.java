package com.alienonwork.crossfitcheckin.constants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

public final class CheckinStatus {
    public static final int CREATED = 1;
    public static final int SCHEDULED = 2;
    public static final int AWAIT_CONNECTION = 3;
    public static final int FAILED = 4;
    public static final int DONE = 5;

    @IntDef({CREATED, SCHEDULED, AWAIT_CONNECTION, FAILED, DONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ICheckinStatus {}
}
