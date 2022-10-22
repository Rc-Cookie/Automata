package com.github.rccookie.automata;

import java.util.Collection;
import java.util.Set;

public interface Automaton<Q,T,R> {

    Q getQ0();

    Set<Q> getQEnds();




    default R exec(Collection<? extends T> input) {
        return start(input).exec();
    }

    @SuppressWarnings("unchecked")
    default R exec(T... input) {
        return start(input).exec();
    }

    @SuppressWarnings("unchecked")
    default Automaton.Instance<Q, R> start(Collection<? extends T> input) {
        return start((T[]) input.toArray());
    }

    @SuppressWarnings("unchecked")
    Automaton.Instance<Q, R> start(T... input);




    interface Instance<Q,R> {

        int getStep();

        Q getState();

        boolean execStep();

        R exec();
    }
}
