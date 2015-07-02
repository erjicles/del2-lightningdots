package com.delsquared.lightningdots.ntuple;

/**
 * Tuple are immutable objects.  Tuples should contain only immutable objects or
 * objects that won't be modified while part of a tuple.
 *
 * http://stackoverflow.com/questions/3642452/java-n-tuple-implementation/3642623#3642623
 */
public interface NTuple {

    public NTupleType getType();
    public int size();
    public <T> T getNthValue(int i);
}