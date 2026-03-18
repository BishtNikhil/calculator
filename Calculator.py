import tkinter as tk
from tkinter import messagebox
import math

class SmartCalculator:
    def __init__(self, root):
        self.root = root
        self.root.title("Smart Calculator")
        self.root.geometry("400x650")
        self.root.configure(bg="#2c3e50")
        self.root.resizable(False, False)

        self.expression = ""

        # Display Frame
        display_frame = tk.Frame(self.root, bg="#2c3e50")
        display_frame.pack(expand=False, fill="both", padx=10, pady=10)

        self.display_var = tk.StringVar()
        self.display_var.set("0")
        
        display_label = tk.Label(display_frame, textvariable=self.display_var, anchor="e", bg="#ecf0f1", 
                                 fg="#2c3e50", font=("Arial", 36, "bold"), padx=10, pady=20, relief="sunken", bd=0)
        display_label.pack(expand=True, fill="both")

        # Buttons Frame
        buttons_frame = tk.Frame(self.root, bg="#2c3e50")
        buttons_frame.pack(expand=True, fill="both", padx=10, pady=10)

        # Button Layout
        buttons = [
            ('C', 1, 0), ('DEL', 1, 1), ('(', 1, 2), (')', 1, 3),
            ('sin', 2, 0), ('cos', 2, 1), ('tan', 2, 2), ('/', 2, 3),
            ('sqrt', 3, 0), ('7', 3, 1), ('8', 3, 2), ('9', 3, 3),
            ('^', 4, 0), ('4', 4, 1), ('5', 4, 2), ('6', 4, 3),
            ('log', 5, 0), ('1', 5, 1), ('2', 5, 2), ('3', 5, 3),
            ('!', 6, 0), ('.', 6, 1), ('0', 6, 2), ('=', 6, 3),
            ('pi', 7, 0), ('+', 7, 1), ('-', 7, 2), ('*', 7, 3),
        ]

        # Ensure grid spaces out evenly
        for i in range(1, 8):
            buttons_frame.rowconfigure(i, weight=1)
        for i in range(4):
            buttons_frame.columnconfigure(i, weight=1)

        for (text, row, col) in buttons:
            self.create_button(buttons_frame, text, row, col)

    def create_button(self, frame, text, row, col):
        # Default colors
        bg_color = "#34495e"
        fg_color = "white"
        
        if text in ['=', 'C', 'DEL']:
            bg_color = "#e74c3c" if text in ['C', 'DEL'] else "#27ae60"
        elif text in ['/', '*', '-', '+']:
            bg_color = "#f39c12"
        elif text in ['(', ')', '^', 'sin', 'cos', 'tan', 'sqrt', 'log', '!', 'pi']:
            bg_color = "#8e44ad"
        else:
            bg_color = "#95a5a6"  # Numbers

        button = tk.Button(frame, text=text, bg=bg_color, fg=fg_color, font=("Arial", 16, "bold"), 
                           relief="flat", activebackground="#7f8c8d", activeforeground="white",
                           command=lambda t=text: self.on_button_click(t))
        button.grid(row=row, column=col, sticky="nsew", padx=3, pady=3)

    def on_button_click(self, char):
        if char == 'C':
            self.expression = ""
            self.display_var.set("0")
        elif char == 'DEL':
            self.expression = self.expression[:-1]
            self.display_var.set(self.expression if self.expression else "0")
        elif char == '=':
            try:
                # Replace mathematical symbols with Python equivalent functions for evaluation
                eval_expr = self.expression.replace('^', '**')
                eval_expr = eval_expr.replace('sin', 'math.sin')
                eval_expr = eval_expr.replace('cos', 'math.cos')
                eval_expr = eval_expr.replace('tan', 'math.tan')
                eval_expr = eval_expr.replace('sqrt', 'math.sqrt')
                eval_expr = eval_expr.replace('log', 'math.log10')
                eval_expr = eval_expr.replace('pi', 'math.pi')
                
                # Handle factorial formatting manually
                while '!' in eval_expr:
                    for i, c in enumerate(eval_expr):
                        if c == '!':
                            j = i - 1
                            while j >= 0 and (eval_expr[j].isdigit() or eval_expr[j] == '.'):
                                j -= 1
                            num_str = eval_expr[j+1:i]
                            if num_str:
                                # Replace with math.factorial
                                eval_expr = eval_expr[:j+1] + f"math.factorial({int(float(num_str))})" + eval_expr[i+1:]
                            break

                result = str(eval(eval_expr))
                
                # Format to remove .0 if it's an integer
                if result.endswith('.0'):
                    result = result[:-2]
                    
                self.display_var.set(result)
                self.expression = result
            except Exception as e:
                self.display_var.set("Error")
                self.expression = ""
        else:
            if char in ['sin', 'cos', 'tan', 'sqrt', 'log']:
                self.expression += f"{char}("
            else:
                self.expression += str(char)
            self.display_var.set(self.expression)

if __name__ == "__main__":
    root = tk.Tk()
    app = SmartCalculator(root)
    root.mainloop()
