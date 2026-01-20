package com.example.calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etDisplay;

    // Variables lógicas
    private double firstValue = 0;
    private double memoryValue = 0; // Aquí se guarda la memoria
    private String operation = "";
    private boolean isNewOp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDisplay = findViewById(R.id.etDisplay);
        // Desactivar teclado del sistema
        etDisplay.setShowSoftInputOnFocus(false);

        // --- 1. CONFIGURACIÓN NÚMEROS (0-9) ---
        // Asignamos el mismo Listener a todos para ahorrar código
        View.OnClickListener numListener = v -> {
            Button b = (Button) v;
            if (isNewOp) {
                etDisplay.setText("");
                isNewOp = false;
            }
            etDisplay.append(b.getText().toString());
        };

        int[] numIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for (int id : numIds) findViewById(id).setOnClickListener(numListener);


        // --- 2. CONFIGURACIÓN OPERACIONES BÁSICAS (+, -, X, /) ---
        View.OnClickListener opListener = v -> {
            String val = etDisplay.getText().toString();
            if (!val.isEmpty()) {
                firstValue = Double.parseDouble(val);
                Button b = (Button) v;
                operation = b.getText().toString();
                isNewOp = true; // La próxima vez que escriban, se limpia la pantalla
            }
        };

        findViewById(R.id.btnAdd).setOnClickListener(opListener);
        findViewById(R.id.btnSub).setOnClickListener(opListener);
        findViewById(R.id.btnMul).setOnClickListener(opListener);
        findViewById(R.id.btnDiv).setOnClickListener(opListener);


        // --- 3. BOTONES DE ACCIÓN ---

        // PUNTO (.)
        findViewById(R.id.btnDot).setOnClickListener(v -> {
            if (isNewOp) {
                etDisplay.setText("0.");
                isNewOp = false;
            } else if (!etDisplay.getText().toString().contains(".")) {
                etDisplay.append(".");
            }
        });

        // IGUAL (=)
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculate());

        // CLEAR (C) - Borra todo
        findViewById(R.id.btnC).setOnClickListener(v -> {
            etDisplay.setText("");
            firstValue = 0;
            operation = "";
            isNewOp = true;
        });

        // BORRAR (Backspace - ImageButton)
        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            String text = etDisplay.getText().toString();
            if (text.length() > 0) {
                etDisplay.setText(text.substring(0, text.length() - 1));
            }
        });


        // --- 4. FUNCIONES ESPECIALES (Imagen) ---

        // Raíz Cuadrada (Sqrt)
        findViewById(R.id.btnSqrt).setOnClickListener(v -> {
            String val = etDisplay.getText().toString();
            if (!val.isEmpty()) {
                double res = Math.sqrt(Double.parseDouble(val));
                etDisplay.setText(String.valueOf(res));
                isNewOp = true;
            }
        });

        // Elevado al cuadrado (x^2)
        findViewById(R.id.btnSquare).setOnClickListener(v -> {
            String val = etDisplay.getText().toString();
            if (!val.isEmpty()) {
                double d = Double.parseDouble(val);
                double res = Math.pow(d, 2); // Elevado a 2
                etDisplay.setText(String.valueOf(res));
                isNewOp = true;
            }
        });


        // --- 5. MEMORIA (M+, M-, Recuperar) ---

        Button btnMPlus = findViewById(R.id.btnMPlus);

        // M+ (Click corto): Suma a memoria
        btnMPlus.setOnClickListener(v -> {
            String val = etDisplay.getText().toString();
            if (!val.isEmpty()) {
                memoryValue += Double.parseDouble(val);
                Toast.makeText(this, "Memoria: " + memoryValue, Toast.LENGTH_SHORT).show();
                isNewOp = true;
            }
        });

        // RECUPERAR MEMORIA (Click Largo en M+): Muestra el valor guardado
        btnMPlus.setOnLongClickListener(v -> {
            etDisplay.setText(String.valueOf(memoryValue));
            isNewOp = true;
            return true;
        });

        // M- (Resta de memoria)
        findViewById(R.id.btnMMinus).setOnClickListener(v -> {
            String val = etDisplay.getText().toString();
            if (!val.isEmpty()) {
                memoryValue -= Double.parseDouble(val);
                Toast.makeText(this, "Memoria: " + memoryValue, Toast.LENGTH_SHORT).show();
                isNewOp = true;
            }
        });
    }

    // Lógica matemática del botón Igual
    private void calculate() {
        String val = etDisplay.getText().toString();
        if (val.isEmpty()) return;

        double secondValue = Double.parseDouble(val);
        double result = 0;

        switch (operation) {
            case "+": result = firstValue + secondValue; break;
            case "-": result = firstValue - secondValue; break;
            case "X": result = firstValue * secondValue; break;
            case "/":
                if (secondValue != 0) result = firstValue / secondValue;
                else {
                    etDisplay.setText("Error");
                    isNewOp = true;
                    return;
                }
                break;
            default: return;
        }

        // Truco para quitar decimal .0 si es entero (ej: muestra 5 en vez de 5.0)
        if (result == (long) result) {
            etDisplay.setText(String.format("%d", (long) result));
        } else {
            etDisplay.setText(String.valueOf(result));
        }
        isNewOp = true;
    }
}