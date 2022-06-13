package com.example.myapplication.ui.currency;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCurrencyBinding;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class CurrencyFragment extends Fragment {
    private FragmentCurrencyBinding binding;

    private ListView listView;
    private TextView tv1, tv2, tvChon1, tvChon2, tvTiGia;
    ArrayList<String> arrayList;
    private boolean flagChon = true, flag;            // = true tức là ô trên được chọn, false là ô dưới
                                                    // flagChon đánh dấu đâu là số nguồn,
                                                    // flag đánh dấu đang muốn thay đổi ô chọn tiền tệ nào
    private int flagTren = 22, flagDuoi = 23;
    private double tiGia;
    private String text = "";
    DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    String[] str = new String[] {"Australia - Dollar", "Brunei - Dollar", "Brazil - Real", "Canada - Dollar",
            "Cambodia - Riel", "China - Yuan", "Cuba - Peso", "Egypt - Pound", "Europe - Euro",
            "India - Rupee", "Indonesia - Rupiah", "Japan - Yen", "Korean - Won", "Laos - Kips",
            "Malaysia - Ringgit", "Mexico - Peso", "Myanmar - Kyat", "Philippines - Peso",
            "South Africa - Rand", "Thailand - Baht", "United Arab Emirates - Dirham",
            "United Kingdom - Pound", "United States - Dollar", "Vietnam - Dong"};

    String[] code = new String[] {"AUD", "BND", "BRL", "CAD", "KHR", "CNY", "CUP", "EGP", "EUR",
            "INR", "IDR", "JPY", "KRW", "LAK", "MYR", "MXN", "MMK", "PHP", "ZAR", "THB", "AED",
            "GBP", "USD", "VND"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCurrencyBinding.inflate(inflater, container, false);
        df.setMaximumFractionDigits(10); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS


        new ReadJSONObject().execute("https://v6.exchangerate-api.com/v6/95589e58e0cef5898e4accd1/pair/USD/VND");
        listView = binding.lvLen;
        tv1 = binding.tvKQ1; tvChon1 = binding.tvChon1;
        tv2 = binding.tvKQ2; tvChon2 = binding.tvChon2;
        tvTiGia = binding.tvTiGia;



        arrayList = new ArrayList<>();
        arrayList.addAll(Arrays.asList(str));
        ArrayAdapter adapter = new ArrayAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setVisibility(View.INVISIBLE);
                if (flag) {
                    tvChon1.setText(str[position] + " ▼");
                    flagTren = position;
                } else {
                    tvChon2.setText(str[position] + " ▼");
                    flagDuoi = position;
                }
                if (flagChon) {
                    new ReadJSONObject().execute("https://v6.exchangerate-api.com/v6/95589e58e0cef5898e4accd1/pair/" + code[flagTren] + "/" + code[flagDuoi]);
                }
                else {
                    new ReadJSONObject().execute("https://v6.exchangerate-api.com/v6/95589e58e0cef5898e4accd1/pair/" + code[flagDuoi] + "/" + code[flagTren]);
                }
                text = "";
            }
        });

        tvChon1.setOnClickListener(v -> {
            listView.setVisibility(View.VISIBLE);
            flag = true;
        });
        tvChon2.setOnClickListener(v -> {
            listView.setVisibility(View.VISIBLE);
            flag = false;
        });
        tv1.setOnClickListener(v -> {
            if (!flagChon) {
                tv2.setBackground(this.getResources().getDrawable(R.color.white));
                tv1.setBackground(this.getResources().getDrawable(R.drawable.textview_layout));
                flagChon = true;
                text = "";
                new ReadJSONObject().execute("https://v6.exchangerate-api.com/v6/95589e58e0cef5898e4accd1/pair/" + code[flagTren] + "/" + code[flagDuoi]);
            }
        });
        tv2.setOnClickListener(v -> {
            if (flagChon) {
                tv1.setBackground(this.getResources().getDrawable(R.color.white));
                tv2.setBackground(this.getResources().getDrawable(R.drawable.textview_layout));
                flagChon = false;
                text = "";
                new ReadJSONObject().execute("https://v6.exchangerate-api.com/v6/95589e58e0cef5898e4accd1/pair/" + code[flagDuoi] + "/" + code[flagTren]);
            }
        });
        binding.btnLenCham.setOnClickListener(v -> {
            new ReadJSONObject().execute("https://v6.exchangerate-api.com/v6/95589e58e0cef5898e4accd1/pair/USD/VND");

        });


        binding.btnLenCE.setOnClickListener(v -> {
            tv1.setText("0");
            tv2.setText("0");
            text = "";
        });
        binding.btnLenXoa.setOnClickListener(v -> {
            text = text.substring(0, text.length() - 1);
            if (text.equals("")) {
                tv1.setText("0");
                tv2.setText("0");
            } else {
                if (flagChon) {
                    tv1.setText(formatText(text));
                    double tien = Double.parseDouble(text) * tiGia;
                    tv2.setText(formatText(String.format("%.2f", tien)));
                } else {
                    tv2.setText(formatText(text));
                    double tien = Double.parseDouble(text) * tiGia;
                    tv1.setText(formatText(String.format("%.2f", tien)));
                }
            }
        });

        binding.btnLen0.setOnClickListener(this::PressNumber);
        binding.btnLen1.setOnClickListener(this::PressNumber);
        binding.btnLen2.setOnClickListener(this::PressNumber);
        binding.btnLen3.setOnClickListener(this::PressNumber);
        binding.btnLen4.setOnClickListener(this::PressNumber);
        binding.btnLen5.setOnClickListener(this::PressNumber);
        binding.btnLen6.setOnClickListener(this::PressNumber);
        binding.btnLen7.setOnClickListener(this::PressNumber);
        binding.btnLen8.setOnClickListener(this::PressNumber);
        binding.btnLen9.setOnClickListener(this::PressNumber);


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void PressNumber(View view) {
        int n = tv1.getText().toString().length() + 1;
        if (n > 17) return;
        else {
            n = tv2.getText().toString().length() + 1;
            if (n > 17) return;
        }

        Button btn = (Button) view;
        if (text.equals("0"))
            text = "";
        text = text + btn.getText().toString();
        if (flagChon) {
            tv1.setText(formatText(text));
            double tien = Double.parseDouble(text) * tiGia;
            tv2.setText(formatText(String.format("%.2f", tien)));
        } else {
            tv2.setText(formatText(text));
            double tien = Double.parseDouble(text) * tiGia;
            tv1.setText(formatText(String.format("%.2f", tien)));
        }
    } // end PressNumber (Nhấn 1 số)


    private String formatText(String s) {
        StringBuilder fs = new StringBuilder();
        if (s.contains("-")) {
            s = s.substring(1);
            fs.append("-");
        }
        int phanNguyen, j, y;
        if (s.contains("."))
            phanNguyen = s.indexOf(".");        // Nếu không phải số nguyên thì lưu vị trí phần nguyên
        else phanNguyen = s.length();
        j = phanNguyen % 3;
        y = phanNguyen/3;

        fs.append(s.substring(0, j));
        for (int i=0; i<y; i++)
            fs.append(" " + s.substring(j+3*i, j+3+3*i));

        fs.append(s.substring(phanNguyen));

        if (fs.charAt(0) == ' ') {
            fs.deleteCharAt(0);
        }
        return fs.toString();
    }


    private class ReadJSONObject extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder content = new StringBuilder();

            try {
                URL url = new URL(strings[0]);
                InputStreamReader inputStreamReader = new InputStreamReader(url.openConnection().getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line);
                }
                bufferedReader.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } // end try catch

            return content.toString();
        } // end doInBackGround

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject object = new JSONObject(s);
                tiGia = object.getDouble("conversion_rate");
                if (flagChon) tvTiGia.setText("1 " + code[flagTren] + " = " + df.format(tiGia) + " " + code[flagDuoi]);
                else tvTiGia.setText("1 " + code[flagDuoi] + " = " + df.format(tiGia) + " " + code[flagTren]);
            } catch (JSONException e) {
                e.printStackTrace();
            } // end try catch
        } // end onPostExecute
    } // end class ReadJSONObject


}
