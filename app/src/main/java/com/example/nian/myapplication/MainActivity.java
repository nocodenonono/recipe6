package com.example.nian.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.HashMap;

/**
 * Main screen for our API testing app.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * final parameters.
     */
    final protected double littlePhysicalActivityLevel = 1.53;
    final protected double mediumPhysicalActivityLevel = 1.76;
    final protected double highPhysicalActivityLevel = 2.25;

    /**
     *inputs to be initialized.
     */
    protected String gender;
    protected String activity;
    protected double weight;
    protected double height;
    protected int age;
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
    protected TextView firstMeal;
    protected TextView secondMeal;
    protected TextView thirdMeal;

    /**Images to view*/
    ImageView firstImage;
    ImageView secondImage;
    ImageView thirdImage;

    /**Set up error msg visible to users*/
    CharSequence text = "Please type valid inputs follow the hints";
    int duration = Toast.LENGTH_LONG;

    /** Default logging tag for messages from the main activity. */
    protected static final String TAG = "RECIPE";

    /** Request queue for our network requests. */
    protected static RequestQueue requestQueue;

    /**First meal's information. */
    protected String firstMealName;
    protected String firstMealImage;
    protected int firstMealReadyTime;
    protected String firstMealImageURL;
    protected int firstMealServing;

    /**Second meal's information. */
    protected String secondMealName;
    protected String secondMealImage;
    protected int secondMealReadyTime;
    protected String secondMealImageURL;
    protected int secondMealServing;

    /**Third meal's information. */
    protected String thirdMealName;
    protected String thirdMealImage;
    protected int thirdMealReadyTime;
    protected String thirdMealImageURL;
    protected int thirdMealServing;

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


        //set up button and disable it.
        submitButton = findViewById(R.id.BUTTON);
        submitButton.setEnabled(false);

        // add TextWatcher to monitor user input.
        genderInput.addTextChangedListener(watcher);
        weightInput.addTextChangedListener(watcher);
        heightInput.addTextChangedListener(watcher);
        ageInput.addTextChangedListener(watcher);
        activityInput.addTextChangedListener(watcher);

        //The Json results retrieved from the web API.
        firstMeal = findViewById(R.id.firstMeal);
        secondMeal = findViewById(R.id.secondMeal);
        thirdMeal = findViewById(R.id.thirdMeal);

        //The images retrieved from the web API
        firstImage = findViewById(R.id.firstMealImageInput);
        secondImage = findViewById(R.id.secondMealImageInput);
        thirdImage = findViewById(R.id.thirdMealImageInput);

        //set up button handler.
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
                    e.getStackTrace();
                }

                //Now, we want to display our Json results in TextView.
                try {
                    firstMeal.setText("Name: " + firstMealName + "\n" + "Ready time in minutes: " + firstMealReadyTime + "\n" + "Servings: " + firstMealServing);
                    secondMeal.setText("Name: " + secondMealName + "\n" + "Ready time in minutes: " + secondMealReadyTime + "\n" + "Servings: " + secondMealServing);
                    thirdMeal.setText("Name: " + thirdMealName + "\n" + "Ready time in minutes: " + thirdMealReadyTime + "\n" + "Servings: " + thirdMealServing);
                } catch (NullPointerException e) {
                    Log.d(TAG, "Why is this happening to me");
                }

                //Change to display activity.
                //Intent intent = new Intent(MainActivity.this, displayActivity.class);
                //startActivity(intent);
            }
        });
    }

    /**
     * Make an API call.
     */
    protected void startAPICall() {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/mealplans/generate?targetCalories=" + calorieNeed +
                            "&timeFrame=day",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {

                            Log.d(TAG, "The response is " + response.toString());

                            //First instantiate a JSON parser and get the Json array.
                            JsonParser parser = new JsonParser();
                            JsonObject result = parser.parse(response.toString()).getAsJsonObject();
                            JsonArray recipes = result.getAsJsonArray("meals");

                            //Retrieve first meals' ID, NAME, AND READY TIME.
                            try {
                                JsonObject firstMeal = recipes.get(0).getAsJsonObject();
                                firstMealName = firstMeal.get("title").getAsString();
                                firstMealImage = firstMeal.get("image").getAsString();
                                firstMealReadyTime = firstMeal.get("readyInMinutes").getAsInt();
                                firstMealServing = firstMeal.get("servings").getAsInt();
                                firstMealImageURL = "https://spoonacular.com/recipeImages/" + firstMealImage;
                                Picasso.get().load(firstMealImageURL).into(firstImage);
                            } catch (Exception e) {
                                Log.d("Something goes wrong", "GG");
                            }

                            //Retrieve second meals' ID, NAME, AND READY TIME.
                            try {
                                JsonObject secondMeal = recipes.get(1).getAsJsonObject();
                                secondMealName = secondMeal.get("title").getAsString();
                                secondMealImage = secondMeal.get("image").getAsString();
                                secondMealReadyTime = secondMeal.get("readyInMinutes").getAsInt();
                                secondMealServing = secondMeal.get("servings").getAsInt();
                                secondMealImageURL = "https://spoonacular.com/recipeImages/" + secondMealImage;
                                Picasso.get().load(secondMealImageURL).into(secondImage);
                            } catch (Exception e) {
                                Log.d(TAG, "WTF is happening");
                            }

                            //Retrieve third meals' ID, NAME, AND READY TIME.
                            try {
                                JsonObject thirdMeal = recipes.get(2).getAsJsonObject();
                                thirdMealImage = thirdMeal.get("image").getAsString();
                                thirdMealName = thirdMeal.get("title").getAsString();
                                thirdMealReadyTime = thirdMeal.get("readyInMinutes").getAsInt();
                                thirdMealServing = thirdMeal.get("servings").getAsInt();
                                thirdMealImageURL = "https://spoonacular.com/recipeImages/" + thirdMealImage;
                                Picasso.get().load(thirdMealImageURL).into(thirdImage);
                            } catch (Exception e) {
                                Log.d(TAG, "Seems like you are doing something wrong");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.d("ERROR", "hello");
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
    protected double calorieCalculator() {
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

    /** private method to check if all the fields are complete */
    private boolean isEditTextEmpty(EditText edit) {
        return edit.getText().toString().length() == 0;
    }

    /** update button status */
    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isEditTextEmpty(weightInput) || isEditTextEmpty(heightInput) || isEditTextEmpty(ageInput) || isEditTextEmpty(activityInput)
                    || isEditTextEmpty(genderInput)) {
                submitButton.setEnabled(false);
            } else {
                submitButton.setEnabled(true);
            }
        }
        };
}
