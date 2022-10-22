package com.github.rccookie.automata;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.github.rccookie.graph.Graph;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Table;

public class DFA<Q,T> implements Automaton<Q,T,Boolean>, Predicate<T[]> {

    public final Q q0;
    public final Set<Q> qEnds;
    private final BiFunction<? super Q, ? super T, ? extends Q> transitions;

    @SafeVarargs
    public DFA(BiFunction<? super Q, ? super T, ? extends Q> transitions, Q q0, Q... qEnds) {
        this.q0 = q0;
        this.transitions = transitions;
        this.qEnds = Set.of(qEnds.clone());
    }

    @SafeVarargs
    public DFA(Table<? super Q, ? super T, ? extends Q> transitions, Q q0, Q... qEnds) {
        this(transitions::get, q0, qEnds);
    }

    @SafeVarargs
    public DFA(Graph<Q, ? extends Set<T>> transitions, Q q0, Q... qEnds) {
        this((q,t) -> {
            if(q == null) return null;
            for(Q adj : transitions.adj(q))
                if(transitions.edge(q,adj).contains(t))
                    return adj;
            return null;
        }, q0, qEnds);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean test(T... word) {
        Q state = q0;
        for(T t : word)
            state = transitions.apply(state, t);
        return qEnds.contains(state);
    }

    @Override
    public Q getQ0() {
        return q0;
    }

    @Override
    public Set<Q> getQEnds() {
        return qEnds;
    }

    @SafeVarargs
    @Override
    public final Instance start(T... input) {
        return new Instance(input);
    }

    public final class Instance implements Automaton.Instance<Q,Boolean> {

        private final T[] input;
        private int step;
        private Q state = q0;


        public Instance(T[] input) {
            this.input = Arguments.checkNull(input, "input").clone();
        }


        @Override
        public int getStep() {
            return step;
        }

        @Override
        public Q getState() {
            return state;
        }

        @Override
        public boolean execStep() {
            if(step == input.length) return false;
            state = transitions.apply(state, input[step++]);
            return true;
        }

        @Override
        public Boolean exec() {
            //noinspection StatementWithEmptyBody
            while(execStep());
            return qEnds.contains(state);
        }
    }
}
