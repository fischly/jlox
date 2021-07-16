import java.util.List;

public class NativeFunctions {
    public static void addNativeFunctions(Environment globals) {
        globals.define("clock", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public int arity() { return 0; }

            @Override
            public String toString() { return "<native fun>"; }
        });
    }
}
