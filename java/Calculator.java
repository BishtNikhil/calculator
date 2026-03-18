import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator {
    private JFrame frame;
    private JLabel display;
    private String expression = "";

    public Calculator() {
        frame = new JFrame("Smart Calculator");
        frame.setSize(400, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.decode("#2c3e50"));
        frame.setResizable(false);

        // Display Area
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BorderLayout());
        displayPanel.setBackground(Color.decode("#2c3e50"));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        display = new JLabel("0", SwingConstants.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 36));
        display.setOpaque(true);
        display.setBackground(Color.decode("#ecf0f1"));
        display.setForeground(Color.decode("#2c3e50"));
        display.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        displayPanel.add(display, BorderLayout.CENTER);
        frame.add(displayPanel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(7, 4, 6, 6));
        buttonsPanel.setBackground(Color.decode("#2c3e50"));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        String[] buttons = {
                "C", "DEL", "(", ")",
                "sin", "cos", "tan", "/",
                "sqrt", "7", "8", "9",
                "^", "4", "5", "6",
                "log", "1", "2", "3",
                "!", ".", "0", "=",
                "pi", "+", "-", "*"
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);

            // Set Colors
            if (text.equals("C") || text.equals("DEL")) {
                button.setBackground(Color.decode("#e74c3c"));
            } else if (text.equals("=")) {
                button.setBackground(Color.decode("#27ae60"));
            } else if (text.equals("/") || text.equals("*") || text.equals("-") || text.equals("+")) {
                button.setBackground(Color.decode("#f39c12"));
            } else if ("()^sincostansqrtlog!pi".contains(text)) {
                button.setBackground(Color.decode("#8e44ad"));
            } else {
                button.setBackground(Color.decode("#95a5a6"));
            }
            button.setForeground(Color.WHITE);

            button.addActionListener(new ButtonClickListener());
            buttonsPanel.add(button);
        }

        frame.add(buttonsPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("C")) {
                expression = "";
                display.setText("0");
            } else if (command.equals("DEL")) {
                if (expression.length() > 0) {
                    expression = expression.substring(0, expression.length() - 1);
                    display.setText(expression.isEmpty() ? "0" : expression);
                }
            } else if (command.equals("=")) {
                try {
                    String result = evaluate(expression);
                    // Format to remove .0 if integer
                    if (result.endsWith(".0")) {
                        result = result.substring(0, result.length() - 2);
                    }
                    display.setText(result);
                    expression = result;
                } catch (Exception ex) {
                    display.setText("Error");
                    expression = "";
                }
            } else {
                if ("sincostansqrtlog".contains(command)) {
                    expression += command + "(";
                } else {
                    expression += command;
                }
                display.setText(expression);
            }
        }
    }

    public static String evaluate(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            String parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return String.valueOf(x);
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (func.equals("pi")) return Math.PI;
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x)); // Assuming degrees for calculator
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else if (func.equals("log")) x = Math.log10(x);
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());
                if (eat('!')) {
                    if (x < 0 || x != Math.floor(x)) throw new RuntimeException("Invalid factorial");
                    long result = 1;
                    for (int i = 2; i <= (int) x; i++) {
                        result *= i;
                    }
                    x = result;
                }

                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calculator());
    }
}
