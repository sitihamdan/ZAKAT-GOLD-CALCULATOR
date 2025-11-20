package com.example.zakatcalculator;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText etWeight, etValue;
    RadioGroup radioGroupType;
    RadioButton rbKeep, rbWear;
    Button btnCalculate, btnReset;
    TextView tvTotalValue, tvWeightMinus, tvZakatPayable, tvTotalZakat;
    View resultCard;
    ImageView infoIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Inputs
        etWeight = findViewById(R.id.etWeight);
        etValue = findViewById(R.id.etValue);

        // Radio buttons
        radioGroupType = findViewById(R.id.radioGroupType);
        rbKeep = findViewById(R.id.rbKeep);
        rbWear = findViewById(R.id.rbWear);

        // Buttons
        btnCalculate = findViewById(R.id.btnCalculate);
        btnReset = findViewById(R.id.btnReset);

        // Results
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvWeightMinus = findViewById(R.id.tvWeightMinus);
        tvZakatPayable = findViewById(R.id.tvZakatPayable);
        tvTotalZakat = findViewById(R.id.tvTotalZakat);
        resultCard = findViewById(R.id.resultCard);

        // Info icon
        infoIcon = findViewById(R.id.infoIcon);
        infoIcon.setOnClickListener(v -> {
            String message = "KEEP threshold: 85 g\nWEAR threshold: 200 g";
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Gold Uruf Information")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        });

        // Radio button highlight effect
        radioGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbKeep) {
                rbKeep.setBackgroundResource(R.drawable.radio_selected);
                rbWear.setBackgroundResource(R.drawable.radio_unselected);
            } else if (checkedId == R.id.rbWear) {
                rbWear.setBackgroundResource(R.drawable.radio_selected);
                rbKeep.setBackgroundResource(R.drawable.radio_unselected);
            }
        });

        // Calculate button
        btnCalculate.setOnClickListener(v -> calculateZakat());

        // Reset button
        btnReset.setOnClickListener(v -> {
            etWeight.setText("");
            etValue.setText("");
            radioGroupType.clearCheck();
            resultCard.setVisibility(View.GONE);

            tvTotalValue.setText("Total Gold Value: -");
            tvWeightMinus.setText("Gold Weight - Uruf: -");
            tvZakatPayable.setText("Zakat Payable Amount: -");
            tvTotalZakat.setText("Total Zakat (2.5%): -");

            rbKeep.setBackgroundResource(R.drawable.radio_unselected);
            rbWear.setBackgroundResource(R.drawable.radio_unselected);
        });
    }

    private void calculateZakat() {
        if (etWeight.getText().toString().isEmpty() ||
                etValue.getText().toString().isEmpty() ||
                radioGroupType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please complete all inputs!", Toast.LENGTH_SHORT).show();
            return;
        }

        double weight = Double.parseDouble(etWeight.getText().toString());
        double price = Double.parseDouble(etValue.getText().toString());
        int uruf = rbKeep.isChecked() ? 85 : 200;

        double totalValue = weight * price;
        double weightMinus = Math.max(0, weight - uruf);
        double zakatPayable = weightMinus * price;
        double totalZakat = zakatPayable * 0.025;

        // Format number with commas and 2 decimal places
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("en", "US"));
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        tvTotalValue.setText("Total Gold Value: RM " + formatter.format(totalValue));
        tvWeightMinus.setText("Gold Weight - Uruf: " + formatter.format(weightMinus) + " g");
        tvZakatPayable.setText("Zakat Payable Amount: RM " + formatter.format(zakatPayable));
        tvTotalZakat.setText("Total Zakat (2.5%): RM " + formatter.format(totalZakat));

        // Show result card with fade-in animation
        resultCard.setAlpha(0f);
        resultCard.setVisibility(View.VISIBLE);
        resultCard.animate().alpha(1f).setDuration(400).start();
    }

    // Inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        if (id == R.id.action_share) {
            String appUrl = "https://github.com/sitihamdan/ZAKAT-GOLD-CALCULATOR.git";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Zakat Gold Calculator App");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this Zakat Gold Calculator app: " + appUrl);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

