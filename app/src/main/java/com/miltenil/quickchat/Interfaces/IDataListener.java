package com.miltenil.quickchat.Interfaces;

import android.net.Uri;

public interface IDataListener {
    void onGetUriResult(Uri uri);

    void onProgressUpdate(int progress);
}
