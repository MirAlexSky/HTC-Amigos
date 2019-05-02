package com.alexsinus.android.htcamigos.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EmployeeFetcher {

    private static final String ENDPOINT = "http://www.mocky.io/v2/56fa31e0110000f920a72134";

    public List<Employee> fetchEmployees() throws IOException, JSONException {

        URL url = new URL(ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Connection", "close");
        BufferedInputStream buffInput = new BufferedInputStream(conn.getInputStream());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesLength;

        while ((bytesLength = buffInput.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, bytesLength);
        }

        buffInput.close();

        return parseEmployees(out.toString());
    }

    private List<Employee> parseEmployees(String employeesString) throws JSONException {
        List<Employee> employees = new ArrayList<>();

        JSONObject jsonBody = new JSONObject(employeesString);
        JSONObject rootJson = jsonBody.getJSONObject("company");
        JSONArray employeeJsonArray = rootJson.getJSONArray("employees");

        Employee employee;
        for (int i = 0; i < employeeJsonArray.length(); i++) {
            JSONObject employeeJson = employeeJsonArray.getJSONObject(i);

            employee = new Employee();
            employee.setId(i);
            employee.setName(employeeJson.getString("name"));
            employee.setPhoneNumber(employeeJson.getString("phone_number"));

            JSONArray skillsJson = employeeJson.getJSONArray("skills");
            for (int j = 0; j < skillsJson.length(); j++) {
                employee.addSkill(skillsJson.getString(j));
            }

            employees.add(employee);
        }

        Collections.sort(employees, new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {
                return e1.getName().compareToIgnoreCase(e2.getName());
            }
        });

        return employees;
    }
}
