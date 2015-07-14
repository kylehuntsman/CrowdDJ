package com.github.funnygopher.crowddj;

import com.github.funnygopher.crowddj.vlc.VLCStatus;

public interface StatusObserver {

    void update(VLCStatus status);
}
