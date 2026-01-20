package com.example.calculadora;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etDisplay;
    private static final int MAX_DIGITS = 10; // Límite de dígitos

    // Variables lógicas
    private double firstValue = 0;
    private double memoryValue = 0;
    private String operation = "";
    private boolean isNewOp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDisplay = findViewById(R.id.etDisplay);
        etDisplay.setShowSoftInputOnFocus(false);

        // --- 1. CONFIGURACIÓN NÚMEROS (0-9) ---
        View.OnClickListener numListener = v -> {
            Button b = (Button) v;
            if (isNewOp) {
                etDisplay.setText("");
                isNewOp = false;
            }
            // Solo añadir si no supera el límite
            if (getDigitCount(etDisplay.getText().toString()) < MAX_DIGITS) {
                etDisplay.append(b.getText().toString());
            }
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
                isNewOp = true;
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

        // CLEAR (C)
        findViewById(R.id.btnC).setOnClickListener(v -> {
            etDisplay.setText("");
            firstValue = 0;
            operation = "";
            isNewOp = true;
        });

        // BORRAR (Backspace)
        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            String text = etDisplay.getText().toString();
            if (text.length() > 0) {
                etDisplay.setText(text.substring(0, text.length() - 1));
            }
        });


        // --- 4. FUNCIONES ESPECIALES ---

        // Raíz Cuadrada
        findViewById(R.id.btnSqrt).setOnClickListener(v -> {
            String val = etDisplay.getText().toString();
            if (!val.isEmpty()) {
                double res = Math.sqrt(Double.parseDouble(val));
                setResultWithLimit(res);
                isNewOp = true;
            }
        });

        // Elevado al cuadrado
        findViewById(R.id.btnSquare).setOnClickListener(v -> {
            String val = etDisplay.getText().toString();
            if (!val.isEmpty()) {
                double d = Double.parseDouble(val);
                double res = Math.pow(d, 2);
                setResultWithLimit(res);
                isNewOp = true;
            }
        });


        // --- 5. MEMORIA ---

        Button btnMPlus = findViewById(R.id.btnMPlus);

        btnMPlus.setOnClickListener(v -> {
            String val = etDisplay.getText().toString();
            if (!val.isEmpty()) {
                memoryValue += Double.parseDouble(val);
                Toast.makeText(this, "Memoria: " + memoryValue, Toast.LENGTH_SHORT).show();
                isNewOp = true;
            }
        });

        btnMPlus.setOnLongClickListener(v -> {
            setResultWithLimit(memoryValue);
            isNewOp = true;
            return true;
        });

        findViewById(R.id.btnMMinus).setOnClickListener(v -> {
            String val = etDisplay.getText().toString();
            if (!val.isEmpty()) {
                memoryValue -= Double.parseDouble(val);
                Toast.makeText(this, "Memoria: " + memoryValue, Toast.LENGTH_SHORT).show();
                isNewOp = true;
            }
        });
    }

    // Cuenta solo los dígitos (ignora punto y signo negativo)
    private int getDigitCount(String text) {
        return text.replace(".", "").replace("-", "").length();
    }

    // Muestra el resultado respetando el límite de dígitos
    private void setResultWithLimit(double result) {
        String resultStr;

        if (result == (long) result) {
            resultStr = String.format("%d", (long) result);
        } else {
            resultStr = String.valueOf(result);
        }

        // Si supera el límite, truncar decimales
        if (getDigitCount(resultStr) > MAX_DIGITS) {
            int integerDigits = String.valueOf((long) result).replace("-", "").length();
            int decimalPlaces = MAX_DIGITS - integerDigits;

            if (decimalPlaces > 0) {
                resultStr = String.format("%." + decimalPlaces + "f", result);
            } else {
                resultStr = "Error"; // Número demasiado grande
            }
        }

        etDisplay.setText(resultStr);
    }

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

        setResultWithLimit(result);
        isNewOp = true;
    }
}