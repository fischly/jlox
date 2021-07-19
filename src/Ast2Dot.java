import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class Ast2Dot implements Expr.Visitor<String>, Stmt.Visitor<String> {
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

    public String print(Stmt stmt) {
        String dot =
                "digraph G {\n" +
                        stmt.accept(this) + "\n" +
                        "}\n";

        try (FileWriter fw = new FileWriter("ast.dot")) {
            fw.write(dot);
        } catch (Exception e) {
            System.err.println("couldnt write file");
        }

        StringSelection stringSelection = new StringSelection(dot);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        return dot;
    }

//    public String printStatement(Stmt stmt) {
//
//
//        if (stmt instanceof Stmt.Print) {
//            Stmt.Print printStmt = (Stmt.Print)stmt;
//
//
//        }
//    }

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
        if (expr.value == null)
            literalName = "nil";
        else
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

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        String nodeName = getNodeName(expr);

        return "\n" +
                nodeName + " [label=\"" + expr.name.lexeme + "\", fillcolor=lightpink, style=filled];\n";
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        String nodeName = getNodeName(expr);
        String valueNodeName = getNodeName(expr.value);

        return "\n" +
                nodeName + " [label=\"" + expr.name.lexeme + " = \"];\n" +
                nodeName + " -> " + valueNodeName + "\n\n" +
                expr.value.accept(this);
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        String nodeName = getNodeName(expr);
        String leftNodeName = getNodeName(expr.left);
        String rightNodeName = getNodeName(expr.right);

        return "\n"+
                nodeName + " [label=\"" + expr.operator.lexeme + "\"];\n" +
                nodeName + " -> " + leftNodeName + ";\n" +
                nodeName + " -> " + rightNodeName + ";\n\n" +
                expr.left.accept(this) +
                expr.right.accept(this);
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        final String nodeName = getNodeName(expr);
        final String calleeNodeName = getNodeName(expr.callee);

        String dot = "\n" +
                nodeName + " [label=\"call\"]\n;" +
                nodeName + " -> " + calleeNodeName + " [label=\"callee\"];\n" +
                expr.callee.accept(this);

        int argCounter = 1;
        for (Expr arg : expr.arguments) {
            final String argNodeName = getNodeName(arg);
            dot += nodeName + " -> " + argNodeName + " [label=\"arg " + (argCounter++) + "\"];\n";
            dot += arg.accept(this);
        }

        return dot;
    }

    /* STATEMENTS */
    @Override
    public String visitBlockStmt(Stmt.Block stmt) {
        final String nodeName = getNodeName(stmt);

        String dot = "\n" + nodeName + " [label=\"block\"];\n";

        for (Stmt bodyStmt : stmt.statements) {
            final String bodyStmtNodeName = getNodeName(bodyStmt);
            dot += nodeName + " -> " + bodyStmtNodeName + ";\n";
            dot += bodyStmt.accept(this);
        }

        return dot;
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt) {
        final String nodeName = getNodeName(stmt);
        final String exprNodeName = getNodeName(stmt.expression);

        return "\n" +
                nodeName + " [label=\"expr\"];\n" +
                nodeName + " -> " + exprNodeName + ";\n" +
                stmt.expression.accept(this);
    }

    @Override
    public String visitFunctionStmt(Stmt.Function stmt) {
        final String nodeName = getNodeName(stmt);
        final String funBodyNodeName = getNodeName(stmt.body);

        String parameterList = "";
        for (Token param : stmt.params) {
            parameterList += param.lexeme + ", ";
        }
        if (parameterList.length() >= 1)
            parameterList = parameterList.substring(0, parameterList.length() - 2);

        String dot =
                "\n" +
                nodeName + " [label=\"fun " + stmt.name.lexeme + "(" + parameterList + ")\"];\n";

        for (Stmt bodyStmt : stmt.body) {
            final String bodyStmtNodeName = getNodeName(bodyStmt);
            dot += nodeName + " -> " + bodyStmtNodeName + ";\n";
            dot += bodyStmt.accept(this);
        }

        return dot;
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {
        final String nodeName = getNodeName(stmt);
        final String condNodeName = getNodeName(stmt.condition);
        final String thenNodeName = getNodeName(stmt.thenBranch);
        final String elseNodeName = getNodeName(stmt.elseBranch);

        return "\n" +
                nodeName + " [label=\"if\", fillcolor=palegreen, style=filled];\n" +
                nodeName + " -> " + condNodeName + " [label=\"cond\"];\n" +
                nodeName + " -> " + thenNodeName + " [label=\"then\"];\n" +
                (stmt.elseBranch != null ? nodeName + " -> " + elseNodeName + " [label=\"else\"];\n" : "") +
                stmt.condition.accept(this) +
                stmt.thenBranch.accept(this) +
                (stmt.elseBranch != null ? stmt.elseBranch.accept(this) : "");
    }

    @Override
    public String visitPrintStmt(Stmt.Print stmt) {
        final String nodeName = getNodeName(stmt);
        final String exprNodeName = getNodeName(stmt.expression);

        return "\n" +
                nodeName + " [label=\"print\"];\n" +
                nodeName + " -> " + exprNodeName + ";\n" +
                stmt.expression.accept(this);
    }

    @Override
    public String visitReturnStmt(Stmt.Return stmt) {
        final String nodeName = getNodeName(stmt);
        final String returnValueNodeName = getNodeName(stmt.value);

        return "\n" +
                nodeName + " [label=\"return\"];\n" +
                nodeName + " -> " + returnValueNodeName + ";\n" +
                stmt.value.accept(this);
    }

    @Override
    public String visitVarStmt(Stmt.Var stmt) {
        final String nodeName = getNodeName(stmt);
        final String  initializerNodeName = getNodeName(stmt.initializer);

        return "\n" +
                nodeName + " [label=\"var " + stmt.name.lexeme + "\"];\n" +
                nodeName + " -> " + initializerNodeName + " [label=\"=\"];\n" +
                stmt.initializer.accept(this);
    }

    @Override
    public String visitWhileStmt(Stmt.While stmt) {
        final String nodeName = getNodeName(stmt);
        final String condNodeName = getNodeName(stmt.condition);
        final String bodyNodeName = getNodeName(stmt.body);

        return "\n" +
                nodeName + " [label=\"while\"];\n" +
                nodeName + " -> " + condNodeName + " [label=\"cond\"];\n" +
                nodeName + " -> " + bodyNodeName + " [label=\"body\"];\n" +
                stmt.condition.accept(this) +
                stmt.body.accept(this);
    }

    /* UTILS */
    private Map<Object, String> nodeNames = new HashMap<>();
    private int nodeCounter = 0;

    private String getNodeName(Object exprOrStmt) {
        if (nodeNames.containsKey(exprOrStmt)) {
            return nodeNames.get(exprOrStmt);
        }

        nodeNames.put(exprOrStmt, Integer.toString(nodeCounter++));
        return nodeNames.get(exprOrStmt);
    }


}
