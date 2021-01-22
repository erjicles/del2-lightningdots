package com.delsquared.lightningdots.ntuple;

/**
 * Represents a type of tuple.  Used to define a type of tuple and then
 * create tuples of that type.
 *
 * http://stackoverflow.com/questions/3642452/java-n-tuple-implementation/3642623#3642623
 */
public interface NTupleType {

    @SuppressWarnings("unused")
    int size();

    @SuppressWarnings("unused")
    Class<?> getNthType(int i);

    /**
     * Tuple are immutable objects.  Tuples should contain only immutable objects or
     * objects that won't be modified while part of a tuple.
     *
     * @param values Comma-delimited list of values to add to the tuple
     * @return Tuple with the given values
     * @throws IllegalArgumentException if the wrong # of arguments or incompatible tuple values are provided
     */
    NTuple createNTuple(Object... values);

    class DefaultFactory {
        public static NTupleType create(final Class<?>... types) {
            return new NTupleTypeImpl(types);
        }
    }

}