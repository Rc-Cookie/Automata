package com.github.rccookie.automata;

import com.github.rccookie.util.Table;

public interface TmDFA<Q,T> {

    Transition<Q,T> getTransition(Q state, T value);

    record Transition<Q,T>(Q nextState, T output, Direction direction) { }


    static <Q,T> TmDFA<Q,T> fromTable(Table<? super Q, ? super T, ? extends Transition<Q,T>> table) {
        return table::get;
    }
}
