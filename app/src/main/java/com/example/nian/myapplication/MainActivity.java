package com.example.nian.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

/**
 * Main screen for our API testing app.
 */
public final class MainActivity extends AppCompatActivity {

    /**
     * final parameters.
     */
    final double littlePhysicalActivityLevel = 1.53;
    final double mediumPhysicalActivityLevel = 1.76;
    final double highPhysicalActivityLevel = 2.25;

    /**
     *inputs to be initialized.
     */
    private String gender;
    private String activity;
    private double weight;
    private double height;
    private int age;
    protected double calorieNeed;

    /**
     *  Three inputs get from users.
     */
    EditText genderInput;
    EditText weightInput;
    EditText heightInput;
    EditText ageInput;
    EditText activityInput;

    /**button to submit users'input.*/
    Button submitButton;

    /** text to display */
    TextView jsonResult;

    /**Set up error msg visible to users*/
    CharSequence text = "Please type valid inputs follow the hints";
    int duration = Toast.LENGTH_LONG;

    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "Lab12:Main";

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    /**
     * Run when our activity comes into view.
     *
     * @param savedInstanceState state that was saved by the activity last time it was paused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up a queue for our Volley requests
        requestQueue = Volley.newRequestQueue(this);

        // Load the main layout for our activity
        setContentView(R.layout.activity_main);

        //Initialize all the inputs.
        genderInput = findViewById(R.id.genderInput);
        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        ageInput = findViewById(R.id.ageInput);
        activityInput = findViewById(R.id.activityInput);

        //
        jsonResult = findViewById(R.id.jsonResultOutput);

        //set button onclick action.
        submitButton = findViewById(R.id.BUTTON);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = genderInput.getText().toString();
                activity = activityInput.getText().toString();
                weight = Integer.valueOf(weightInput.getText().toString());
                height = Integer.valueOf(heightInput.getText().toString());
                age = Integer.valueOf(ageInput.getText().toString());

                // We want to make sure the user's inputs are valid.
                try {
                    calorieCalculator();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, text, duration).show(); // tell user that you enter invalid inputs.
                }

                // Now we know we have valid inputs, we want to make sure we don't have 0 for our calorie need.
                try {
                    calorieNeed = calorieCalculator();
                    if (calorieNeed <= 0) {
                        Toast.makeText(MainActivity.this, text, duration).show(); // tell user that you enter invalid inputs.
                    }
                } catch (Exception e) {
                    //e.getStackTrace();
                    Toast.makeText(MainActivity.this, text, duration).show();
                }

                // Now we have a proper calorie needs, we want to call our API to give us recipes.
                try {
                    startAPICall();
                } catch (Exception e) {
                    //e.getStackTrace();
                }
                //Now, we want to display our Json results in TextView.
                jsonResult.setText("lol");
            }
        });
    }

    /**
     * Make an API call.
     */
    void startAPICall() {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/mealplans/generate?targetCalories=" + calorieNeed + "&timeFrame=day",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.d(TAG, response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.w(TAG, error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String>  params = new HashMap<>();
                    params.put("X-RapidAPI-Key", "fd85b09a3bmsh020b824e0f14365p1bf367jsnd0a7817058bb");
                    Log.d(TAG, params.toString());
                    return params;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Calculate the calories one need that day.
     */
    private double calorieCalculator() {
        if (gender.equals("M")) {
            double BMR = 10 * weight + 6.25 * height - 5 * age + 5;
            switch (activity) {
                case "little":
                    return BMR * littlePhysicalActivityLevel;
                case "medium":
                    return BMR * mediumPhysicalActivityLevel;
                case "high":
                    return BMR * highPhysicalActivityLevel;
            }
        } else if (gender.equals("F")) {
            double BMR = 10 * weight + 6.25 * height - 5 * age - 161;
            switch (activity) {
                case "little":
                    return BMR * littlePhysicalActivityLevel;
                case "medium":
                    return BMR * mediumPhysicalActivityLevel;
                case "high":
                    return BMR * highPhysicalActivityLevel;
            }
        }
        return 0;
    }
}
