import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class Ast2Dot implements Expr.Visitor<String> {
    public String print(Expr expression) {
        String dot =
                "digraph G {\n" +
                        expression.accept(this) + "\n" +
                        "}\n";

        try (FileWriter fw = new FileWriter("ast.dot")) {
            fw.write(dot);
        } catch (Exception e) {
            System.err.println("couldnt write file");
        }

        return dot;
    }

//    @Override
//    public String visitTernaryExpr(Expr.Ternary expr) {
//        String ternaryNodeName = getNodeName(expr);
//        String condNodeName = getNodeName(expr.cond);
//        String condTrueNodeName = getNodeName(expr.condTrue);
//        String condFalseNodeName = getNodeName(expr.condFalse);
//
//        String dot =
//                ternaryNodeName + " [label=\"?:\", fillcolor=palegreen, style=filled];\n" +
//                ternaryNodeName + " -> " + condNodeName + ";\n" +
//                ternaryNodeName + " -> " + condTrueNodeName + ";\n" +
//                ternaryNodeName + " -> " + condFalseNodeName + ";\n" +
//                "\n" +
//                expr.cond.accept(this) +
//                expr.condTrue.accept(this) +
//                expr.condFalse.accept(this);
//
//        return dot;
//    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        String binaryNodeName = getNodeName(expr);
        String leftNodeName = getNodeName(expr.left);
        String rightNodeName = getNodeName(expr.right);

        String dot =
                binaryNodeName + " [label=\"" + expr.operator.lexeme + "\", fillcolor=gray, style=filled];\n" +
                        binaryNodeName + " -> " + leftNodeName + "\n" +
                        binaryNodeName + " -> " + rightNodeName + "\n\n";

        return dot + expr.left.accept(this) + expr.right.accept(this);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        String groupingNodeName = getNodeName(expr);
        String innerNodeName = getNodeName(expr.expression);

        String dot =
                groupingNodeName + " [label=\"( )\", fillcolor=gray, style=filled];\n" +
                groupingNodeName + " -> " + innerNodeName + ";\n" +
                expr.expression.accept(this);

        return dot;
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        String literalName = "";
        if (expr.value == null) literalName = "nil";
        literalName = expr.value.toString();

        String nodeName = getNodeName(expr);

        return nodeName + " [label=\"" + literalName + "\", fillcolor=lightblue, style=filled];\n";
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        String nodeName = getNodeName(expr);
        String nodeNameRight = getNodeName(expr.right);

        return "\n" +
                nodeName + " [label=\"" + expr.operator.lexeme + "\", fillcolor=gray, style=filled];\n" +
                nodeName + " -> " + nodeNameRight + ";\n\n" +

                expr.right.accept(this);
    }


    private Map<Expr, String> nodeNames = new HashMap<>();
    private int nodeCounter = 0;

    private String getNodeName(Expr expr) {
        if (nodeNames.containsKey(expr)) {
            return nodeNames.get(expr);
        }

        nodeNames.put(expr, Integer.toString(nodeCounter++));
        return nodeNames.get(expr);
    }
}
