package com.delsquared.lightningdots.ntuple;

import java.util.Arrays;

/**
 * http://stackoverflow.com/questions/3642452/java-n-tuple-implementation/3642623#3642623
 */
class NTupleImpl implements NTuple {

    private final NTupleType type;
    private final Object[] values;

    NTupleImpl(NTupleType type, Object[] values) {
        this.type = type;
        if (values == null || values.length == 0) {
            this.values = new Object[0];
        } else {
            this.values = new Object[values.length];
            System.arraycopy(values, 0, this.values, 0, values.length);
        }
    }

    @Override
    public NTupleType getType() {
        return type;
    }

    @Override
    public int size() {
        return values.length;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getNthValue(int i) {
        return (T) values[i];
    }

    @Override
    public boolean equals(Object object) {
        if (object == null)   return false;
        if (this == object)   return true;

        if (! (object instanceof NTuple))   return false;

        final NTuple other = (NTuple) object;
        if (other.size() != size())   return false;

        final int size = size();
        for (int i = 0; i < size; i++) {
            final Object thisNthValue = getNthValue(i);
            final Object otherNthValue = other.getNthValue(i);
            if ((thisNthValue == null && otherNthValue != null) ||
                    (thisNthValue != null && ! thisNthValue.equals(otherNthValue))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        for (Object value : values) {
            if (value != null) {
                hash = hash * 37 + value.hashCode();
            }
        }
        return hash;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }
}