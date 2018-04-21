package com.example.bobby.mysensor;

import android.util.Log;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.HashMap;

public class AdvDataElement {
    class Types {
        static final byte FLAGS = 0x01;
        static final byte MANUFACTURER_DATA = (byte) 0xFF;
    }


    private byte type;
    private byte[] mValue;

    AdvDataElement(byte type, byte[] mValue) {
        this.type = type;
        this.mValue = mValue;
    }

    @Override
    public String toString() {
        if (type == Types.MANUFACTURER_DATA) {
            return "AdvDataElement{type=" + getTypeName() +
                    String.format(", Company Id=0x%04x", getCompanyId()) +
                    "}";
        }

        return "AdvDataElement{" +
                "mType=" + getTypeName() +
                ", mValue=" + Arrays.toString(mValue) +
                '}';
    }

    public String getCompanyIdString() {
        return toHex(mValue[1]) + "" + toHex(mValue[0]);
    }

    public short getCompanyId() {
        int lo = mValue[1] & 0xFF;
        int hi = mValue[0] & 0xFF;
        return (short) (lo << 8 | hi);
    }

    private String toHex(byte b) {
        return String.format("%02x", b);
    }

    private String getTypeName() {
        switch(type) {
            case Types.MANUFACTURER_DATA: return "Manufacturer Data";
            case Types.FLAGS: return "Flags";
            default:
                return "NOT FOUND";
        }
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte[] getValue() {
        return mValue;
    }

    public void setValue(byte[] mValue) {
        this.mValue = mValue;
    }
}
