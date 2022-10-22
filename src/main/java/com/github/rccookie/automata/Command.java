package com.github.rccookie.automata;

@SuppressWarnings("SpellCheckingInspection")
public interface Command {

    Command END = r -> { };

    void exec(RAM ram);


    static Command parse(String cmd) {
        cmd = cmd.toUpperCase();
        if(cmd.equals("END"))
            return END;
        String[] args = cmd.split(" ");
        if(!cmd.startsWith("IF")) {
            long x = Long.parseLong(args[1]);
            try {
                return (Command) Command.class.getMethod(args[0], long.class).invoke(null, x);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        long x,t;
        String op;
        if(args.length == 4) {
            x = Long.parseLong(args[2]);
            t = Long.parseLong(args[3]);
            op = args[1];
        }
        else {
            x = Long.parseLong(args[3]);
            t = Long.parseLong(args[6]);
            op = args[2];
        }

        return switch(op) {
            case "=" -> IFEQUALS(x,t);
            case "<" -> IFLESS(x,t);
            case "<=" -> IFLESSOREQUAL(x,t);
            case ">" -> IFGREATER(x,t);
            case ">=" -> IFGREATEROREQUAL(x,t);
            default -> throw new RuntimeException();
        };
    }

    static Command[] parseProgram(String program) {
        return program.lines().filter(l -> !l.isBlank() && !l.startsWith("#")).map(Command::parse).toArray(Command[]::new);
    }


    static Command CLOAD(long x) {
        return (CalcCommand) r -> r.setAcc(x);
    }

    static Command LOAD(long x) {
        return (CalcCommand) r -> r.setAcc(r.c(x));
    }

    static Command INDLOAD(long x) {
        return (CalcCommand) r -> r.setAcc(r.c(r.c(x)));
    }

    static Command STORE(long x) {
        return (CalcCommand) r -> r.setC(x, r.getAcc());
    }

    static Command INDSTORE(long x) {
        return (CalcCommand) r -> r.setC(r.c(x), r.getAcc());
    }

    static Command CADD(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() + x);
    }

    static Command ADD(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() + r.c(x));
    }

    static Command INDADD(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() + r.c(r.c(x)));
    }

    static Command CSUB(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() - x);
    }

    static Command SUB(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() - r.c(x));
    }

    static Command INDSUB(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() - r.c(r.c(x)));
    }

    static Command CMULT(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() * x);
    }

    static Command MULT(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() * r.c(x));
    }

    static Command INDMULT(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() * r.c(r.c(x)));
    }

    static Command CDIV(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() / x);
    }

    static Command DIV(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() / r.c(x));
    }

    static Command INDDIV(long x) {
        return (CalcCommand) r -> r.setAcc(r.getAcc() / r.c(r.c(x)));
    }

    static Command GOTO(long x) {
        return r -> r.b = x;
    }

    static Command IFEQUALS(long x, long t) {
        return r -> r.b = r.getAcc() == x ? t : r.b+1;
    }

    static Command IFLESS(long x, long t) {
        return r -> r.b = r.getAcc() < x ? t : r.b+1;
    }

    static Command IFLESSOREQUAL(long x, long t) {
        return r -> r.b = r.getAcc() <= x ? t : r.b+1;
    }

    static Command IFGREATER(long x, long t) {
        return r -> r.b = r.getAcc() > x ? t : r.b+1;
    }

    static Command IFGREATEROREQUAL(long x, long t) {
        return r -> r.b = r.getAcc() >= x ? t : r.b+1;
    }
}
interface CalcCommand extends Command {
    @Override
    default void exec(RAM ram) {
        calc(ram);
        ram.b++;
    }

    void calc(RAM ram);
}
