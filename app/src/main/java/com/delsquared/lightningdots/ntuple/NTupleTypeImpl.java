package com.delsquared.lightningdots.ntuple;

/**
 * http://stackoverflow.com/questions/3642452/java-n-tuple-implementation/3642623#3642623
 */
class NTupleTypeImpl implements NTupleType {

    final Class<?>[] types;

    NTupleTypeImpl(Class<?>[] types) {
        this.types = (types != null ? types : new Class<?>[0]);
    }

    @SuppressWarnings("unused")
    public int size() {
        return types.length;
    }

    //WRONG
    //public <T> Class<T> getNthType(int i)

    //RIGHT - thanks Emil
    @SuppressWarnings("unused")
    public Class<?> getNthType(int i) {
        return types[i];
    }

    public NTuple createNTuple(Object... values) {
        if ((values == null && types.length == 0) ||
                (values != null && values.length != types.length)) {
            throw new IllegalArgumentException(
                    "Expected "+types.length+" values, not "+
                            (values == null ? "(null)" : values.length) + " values");
        }

        if (values != null) {
            for (int i = 0; i < types.length; i++) {
                final Class<?> nthType = types[i];
                final Object nthValue = values[i];
                if (nthValue != null && ! nthType.isAssignableFrom(nthValue.getClass())) {
                    throw new IllegalArgumentException(
                            "Expected value #"+i+" ('"+
                                    nthValue+"') of new Tuple to be "+
                                    nthType+", not " +
                                    nthValue.getClass());
                }
            }
        }

        return new NTupleImpl(this, values);
    }
}