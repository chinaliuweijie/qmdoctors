package com.qingmiao.qmdoctor.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.widget.LineView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BigCaseIconActivity extends AppCompatActivity {

    @BindView(R.id.lineBigView)
    LineView lineBigView;
    @BindView(R.id.tvCase)
    TextView tvCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_big_case_icon);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        String demo = bundle.getString("demo");
        String unit = bundle.getString("unit");
        String title = bundle.getString("title");
        ArrayList<String> data = bundle.getStringArrayList("data");
        ArrayList<String> times = bundle.getStringArrayList("time");


        ArrayList<Double> integerArrayList = new ArrayList<>();
        ArrayList<String> timeArr = new ArrayList<>();
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd");
        for (String s : times) {
            long l = Long.parseLong(s + "000");
            String time = sdr.format(new Date(l));
            timeArr.add(time);

        }

        lineBigView.setTimeText(timeArr);
        tvCase.setText(title);
        String[] demos = demo.split(",");
        /*
          case 50: text = "阴性";
           case 40: text = "阳性";
            case 30: text = "+-";
             case 20:text = "++";
              case 10:text = "+++";
        * */

        if (demos.length >= 2) {

            lineBigView.setMaxScore(30);
            lineBigView.setMinScore(10);

            for (String s : data) {
                switch (s) {
                    case "阴性":
                        integerArrayList.add(30.0);
                        break;
                    case "可疑阳性(±)":
                        integerArrayList.add(20.0);
                        break;
                    case "阳性":
                        integerArrayList.add(10.0);
                        break;
                }
            }
            lineBigView.setScore(integerArrayList);
        } else {

            String[] v = demo.split("-");
            for (String s : data) {
                integerArrayList.add(Double.parseDouble(s));
            }

            double minValue = Double.parseDouble(v[0]);
            double maxValue = Double.parseDouble(v[1]);

            lineBigView.setMaxScore(maxValue);
            lineBigView.setMinScore(minValue);
            lineBigView.setScore(integerArrayList);
        }

    }

}
