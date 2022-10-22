package com.github.rccookie.automata;

import java.util.Arrays;
import java.util.Set;

import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public class TouringMachine<Q,T> implements Automaton<Q,T,T[]> {

    public final Q q0, qEnd;
    public final T blank;
    public final TmDFA<Q,T> dfa;

    public TouringMachine(Q q0, Q qEnd, TmDFA<Q,T> dfa) {
        this(q0, qEnd, null, dfa);
    }

    public TouringMachine(Q q0, Q qEnd, T blank, TmDFA<Q,T> dfa) {
        this.q0 = q0;
        this.qEnd = qEnd;
        this.blank = blank;
        this.dfa = dfa;
    }


    @Override
    public Q getQ0() {
        return q0;
    }

    @Override
    public Set<Q> getQEnds() {
        return Set.of(qEnd);
    }

    @Override
    @SafeVarargs
    public final Instance start(T... input) {
        return new Instance(input);
    }



    public final class Instance implements Automaton.Instance<Q,T[]> {

        private T[] tape;
        private int pos = 0;
        private int step = 0;

        private Q state = q0;

        private Instance(T[] input) {
            if(Arguments.checkNull(input, "input").length > 0)
                this.tape = input.clone();
            else {
                this.tape = Arrays.copyOf(input, 1);
                this.tape[0] = blank;
            }
        }


        @Override
        public int getStep() {
            return step;
        }

        public T[] getTape() {
            return tape.clone();
        }

        public int getTapePos() {
            return pos;
        }

        @Override
        public Q getState() {
            return state;
        }


        @Override
        public boolean execStep() {

            if(state == qEnd) return false;

            TmDFA.Transition<Q,T> t = dfa.getTransition(state, tape[pos]);
            tape[pos] = t.output();
            state = t.nextState();
            step++;

            if(t.direction() == Direction.LEFT) {
                if(pos == 0) {
                    T[] newTape = Arrays.copyOf(tape, tape.length+1);
                    System.arraycopy(tape, 0, newTape, 1, tape.length);
                    newTape[0] = null;
                    tape = newTape;
                }
                else pos--;
            }
            else if(t.direction() == Direction.RIGHT) {
                if(++pos == tape.length)
                    tape = Arrays.copyOf(tape, tape.length+1);
            }
            return true;
        }

        @Override
        @NotNull
        public T[] exec() {
            //noinspection StatementWithEmptyBody
            while(execStep());
            return Arrays.copyOfRange(tape, pos, tape.length);
        }
    }
}
