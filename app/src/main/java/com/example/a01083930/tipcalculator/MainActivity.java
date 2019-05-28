package com.example.a01083930.tipcalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.text.NumberFormat;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener  {

    EditText billAmount;
    TextView perPersonLabel;
    TextView perPersonNumberLabel;
    TextView percentNumberLabel;
    TextView tipNumberLabel;
    TextView totalNumberLabel;
    Button subtractPercent;
    Button addPercent;
    Button applyButton;
    Spinner splitBillSpinner;
    RadioGroup tipOption;

    Integer checkID;
    Integer splitOption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkID = 0;
        splitOption = 0;

        billAmount = findViewById(R.id.billEditText);
        billAmount.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Double.valueOf(billAmount.getText().toString()) > 0)
                    updateTipAndTotal();
            }
        });

        perPersonLabel = findViewById(R.id.perPersonLabel);
        perPersonNumberLabel = findViewById(R.id.perPersonNumberLabel);
        percentNumberLabel = findViewById(R.id.percentNumberLabel);
        tipNumberLabel = findViewById(R.id.tipLabel);
        totalNumberLabel = findViewById(R.id.totalNumberLabel);

        splitBillSpinner = findViewById(R.id.splitSpinner);

        subtractPercent = findViewById(R.id.subtractButton);
        addPercent = findViewById(R.id.addButton);
        applyButton = findViewById(R.id.applyButton);

        tipOption = findViewById(R.id.radioGroup);
        tipOption.setOnCheckedChangeListener(this);

        //handle button assignment
        subtractPercent.setOnClickListener(percentListener);
        addPercent.setOnClickListener(percentListener);
        applyButton.setOnClickListener(percentListener);

        //handle spinner events
        splitBillSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0) {
                    perPersonLabel.setVisibility(View.VISIBLE);
                    perPersonNumberLabel.setVisibility(View.VISIBLE);
                    splitOption = position+1;
                }
                else {
                    perPersonLabel.setVisibility(View.GONE);
                    perPersonNumberLabel.setVisibility(View.GONE);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedID)
    {
        //https://stackoverflow.com/questions/28345451/how-to-get-id-of-selected-radio-button-in-a-radio-group
        int radioButtonID = group.getCheckedRadioButtonId();
        View radioButton = group.findViewById(radioButtonID);
        checkID = group.indexOfChild(radioButton);
    }

    //handle percent button events
    private OnClickListener percentListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String numberString;
            Integer numberInt;

            switch(v.getId()) {
                case R.id.subtractButton:
                    numberString = parseStringPercent(percentNumberLabel);
                    numberInt = Integer.parseInt(numberString) - 1 > 0 ? Integer.parseInt(numberString)-1 : 0;
                    numberString = String.valueOf(numberInt).concat("%");
                    percentNumberLabel.setText(numberString);
                    updateTipAndTotal();
                    break;
                case R.id.addButton:
                    numberString = parseStringPercent(percentNumberLabel);
                    numberInt = Integer.parseInt(numberString)+1;
                    numberString = String.valueOf(numberInt).concat("%");
                    percentNumberLabel.setText(numberString);
                    updateTipAndTotal();
                    break;
                 case R.id.applyButton:
                     updateTipAndTotal();
            }
        }
    };

    private String parseStringPercent(TextView percent)
    {
        return percent.getText().toString().replace("%","");
    }

    private void updateTipAndTotal()
    {
        String billString = billAmount.getText().toString();
        Double percent = Double.valueOf(parseStringPercent(percentNumberLabel));
        Double percentDouble = (percent)/100 > 0? (percent)/100: 0;
        if (!billString.isEmpty())
        {
            Double billNumber = Double.parseDouble(billString);
            if (billNumber > 0)
            {
                Double tipNumber = (billNumber*percentDouble);
                Double totalNumber = (billNumber +(billNumber*percentDouble));
                String tipString = checkID == 1 ? formatCurrencyWhole(tipNumber):String.format("$%.2f", tipNumber);
                String totalString = checkID == 2 ? formatCurrencyWhole(totalNumber):String.format("$%.2f", totalNumber);
                tipNumberLabel.setText(tipString);
                totalNumberLabel.setText(totalString);
                if (splitOption > 0)
                {
                    perPersonNumberLabel.setText(String.format("$%.2f", totalNumber/splitOption));
                }
            }
        }
    }


    //https://stackoverflow.com/questions/12966725/rounding-currency-to-nearest-dollar-how-did-you-do-it
    public static String formatCurrencyWhole(double amount) {
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        currency.setMaximumFractionDigits(0);
        currency.setMinimumFractionDigits(0);

        return currency.format(amount);
    }

}
