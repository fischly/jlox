public class AstPrinterTest {
    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(42)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Binary(
                                new Expr.Literal(5),
                                new Token(TokenType.PLUS, "+", null, 1),
                                new Expr.Literal(10)
                        )
                )
        );

        System.out.println(new AstPrinter().print(expression));
    }
}
