package com.github.rccookie.automata;

import java.util.Arrays;

public class RAM {

    long b = 1;
    private final Command[] program;
    private long[] c;

    public RAM(String program, long... input) {
        this(Command.parseProgram(program), input);
    }

    public RAM(Command[] program, long... input) {
        this.program = program;
        c = new long[input.length+1];
        System.arraycopy(input, 0, c, 1, input.length);
    }

    public long c(long i) {
        return i < c.length ? c[(int) i] : 0;
    }

    public void setC(long i, long x) {
        if(i >= c.length)
            c = Arrays.copyOf(c, (int) (i+1));
        c[(int) i] = Math.max(0,x);
    }

    public long getAcc() {
        return c[0];
    }

    public void setAcc(long x) {
        c[0] = Math.max(0,x);
    }

    public boolean execStep() {
        if(b < 1 || b > program.length) {
            System.err.println("Illegal program counter: " + b);
            b = Math.max(1, Math.min(program.length, b));
        }
        Command c = program[(int) (b-1)];
        c.exec(this);
        return c != Command.END;
    }

    public long[] exec() {
        //noinspection StatementWithEmptyBody
        while(execStep());
        return Arrays.copyOfRange(c, 1, c.length);
    }
}
