package com.example.pgmachine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String PREF_SELECTED_BUILDING = "selected_building";
    private Spinner spinner;
    private RecyclerView recyclerView;
    private WasherAdapter washerAdapter;
    private List<ApiResponse.Data.Item> items = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        washerAdapter = new WasherAdapter(items);
        //recyclerView.setAdapter(washerAdapter);
        FloatingActionButton refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重新加载数据
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FloatingActionButton alipayButton = findViewById(R.id.alipayButton);
        alipayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重新加载数据
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("alipays://platformapi/startapp?saId=10000007"));
                    startActivity(intent);
                } catch (Exception e) {
                    // 如果未安装支付宝或无法打开，给出提示
                    Toast.makeText(MainActivity.this, "无法打开支付宝扫码", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


        loadWashers();


        // Spinner item selected listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBuilding = parent.getItemAtPosition(position).toString();
                selectedBuilding = selectedBuilding.replace("龙河校区", "").replace("幢", "");
                saveSelectedBuilding(selectedBuilding);
                filterWashersByBuilding(selectedBuilding);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadWashers() {
        String url = "https://userapi.qiekj.com/machineModel/near/machines";
        String jsonBody =
                "machineTypeId=c9892cb4-bd78-40f6-83c2-ba73383b090a&" +
                        "page=1&" +
                        "pageSize=120&" +
                        "shopId=202308241533480000011301057363&";
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/x-www-form-urlencoded"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Accept-Charset", "UTF-8")
                .addHeader("Cookie", "acw_tc=2f624a3e17172143835903967e3f7d04023237383973d20b7eac6177bef315; SERVERID=7b421cf89a037c2202535e8b7e367ae3|1717214483|1717214383")
                .addHeader("referer", "https://2018072460764274.hybrid.alipay-eco.com/2018072460764274/0.2.2405291428.18/index.html")
                .addHeader("channel", "alipay")
                .addHeader("sign", "573f455ed9cb4b6b8d7dd3b71cb008730ecac35c04ea7fdb508cb22dda7ff8e3")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("x-release-type", "ONLINE")
                .addHeader("timestamp", "1717214556170")
                .addHeader("alipayMiniMark", "ObuoOremZXn5EKnqXSk6OCvoyp93RHvZbEdLHVw/HXJVNjA2VOZYRifAlrjb+gAjHWOQCWXmkRszxVCyLDDpmrVEL8WA5Mwv8WJzXbPbxNc=")
                .addHeader("Accept-Encoding", "gzip")
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 13; M2012K11AC Build/TKQ1.221114.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/105.0.5195.148 MYWeb/0.11.0.240528142833 UWS/3.22.2.9999 UCBS/3.22.2.9999_220000000000 Mobile Safari/537.36 NebulaSDK/1.8.100112 Nebula AlipayDefined(nt:WIFI,ws:393|0|2.75,ac:sp) AliApp(AP/10.6.0.8000) AlipayClient/10.6.0.8000 Language/zh-Hans isConcaveScreen/true Region/CNAriver/1.0.0 DTN/2.0")
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Host", "userapi.qiekj.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        ApiResponse apiResponse = gson.fromJson(responseData, ApiResponse.class);
                        runOnUiThread(() -> {
                            items.clear();
                            items.addAll(apiResponse.getData().getItems());
                            washerAdapter.notifyDataSetChanged();

                            // Populate Spinner with unique buildings
                            Set<String> buildings = new HashSet<>();
                            for (ApiResponse.Data.Item item : items) {
                                String building = extractBuildingNumber(item.getName());
                                buildings.add(building);
                            }
                            List<String> buildingList = new ArrayList<>(buildings);
                            List<String> dropdownBuildingList = new ArrayList<>();
                            for (String building : buildingList) {
                                dropdownBuildingList.add("龙河校区" + building + "幢");
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, dropdownBuildingList) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    TextView label = (TextView) super.getView(position, convertView, parent);
                                    String selectedBuilding = dropdownBuildingList.get(position).replace("龙河校区", "");
                                    label.setText("当前选择楼栋：" + selectedBuilding);
                                    return label;
                                }

                                @Override
                                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                    TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                                    label.setText(dropdownBuildingList.get(position));
                                    return label;
                                }
                            };
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);

                            SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            String selectedBuilding = preferences.getString(PREF_SELECTED_BUILDING, "403");
                            setSpinnerSelection(selectedBuilding);
                        });
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void filterWashersByBuilding(String building) {
        List<ApiResponse.Data.Item> filteredItems = new ArrayList<>();
        for (ApiResponse.Data.Item item : items) {
            if (extractBuildingNumber(item.getName()).equals(building)) {
                filteredItems.add(item);
            }
        }
        washerAdapter = new WasherAdapter(filteredItems);
        recyclerView.setAdapter(washerAdapter);
    }

    private void saveSelectedBuilding(String building) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_SELECTED_BUILDING, building);
        editor.apply();
    }

    private void setSpinnerSelection(String building) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition("龙河校区" + building + "幢");
            if (position != -1) {
                spinner.setSelection(position);
            }
        }
    }

    private String extractBuildingNumber(String name) {
        // 提取“幢”之前的三位数字
        int index = name.indexOf("幢");
        if (index > 2) {
            return name.substring(index - 3, index);
        }
        return "";
    }
}
