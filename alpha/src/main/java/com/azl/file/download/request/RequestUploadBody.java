package com.azl.file.download.request;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by zhong on 2017/11/16.
 */

public class RequestUploadBody {

    public static RequestBody create(final @Nullable MediaType contentType, final File file, final UploadProgressListener listener) {
        if (file == null) throw new NullPointerException("content == null");

        return new RequestBody() {
            @Override
            public @Nullable
            MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(file);
                    source = source(source, listener, this);
                    sink.writeAll(source);
                } catch (SocketException e) {
                    Log.d("RequestUploadBody","close upload");
                } finally {
                    Util.closeQuietly(source);
                }
            }
        };


    }

    private static Source source(Source source, final UploadProgressListener listener, final RequestBody responseBody) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                listener.progress(responseBody.contentLength(), (totalBytesRead - bytesRead) - 1);
                return bytesRead;
            }
        };
    }

    public interface UploadProgressListener {
        void progress(long total, long current);
    }

}
