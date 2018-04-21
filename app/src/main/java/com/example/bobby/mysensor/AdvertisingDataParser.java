package com.example.bobby.mysensor;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

class AdvertisingDataParser {
    private static final String TAG = AdvertisingDataParser.class.getSimpleName();


    private ArrayList<AdvDataElement> data;

    static AdvDataElement[] parse(byte[] rawBytes) {
        ArrayList<AdvDataElement> data = new ArrayList<>();

        ByteBuffer bb = ByteBuffer.wrap(rawBytes);

        Log.d(TAG, Arrays.toString(rawBytes));
        while (bb.hasRemaining()) {
            byte b = bb.get();
            int elemLength = b & 0xFF;

            if (elemLength == 0) {
                break;
            }

            byte elemType = bb.get();
            byte elemValueBytes[] = new byte[elemLength - 1]; // Minus one to account for elemType
            bb.get(elemValueBytes);
            data.add(new AdvDataElement(elemType, elemValueBytes));
        }

        AdvDataElement[] ret = new AdvDataElement[data.size()];
        data.toArray(ret);

        return ret;
    }
}
