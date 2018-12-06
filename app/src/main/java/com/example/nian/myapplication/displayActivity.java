package com.example.nian.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class displayActivity extends MainActivity {

    /** text to display */
    protected TextView firstMeal;
    protected TextView secondMeal;
    protected TextView thirdMeal;

    /**Images to view*/
    ImageView firstImage;
    ImageView secondImage;
    ImageView thirdImage;

    /** get new recipe button */
    Button newButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //The Json results retrieved from the web API.
        firstMeal = findViewById(R.id.firstMeal);
        secondMeal = findViewById(R.id.secondMeal);
        thirdMeal = findViewById(R.id.thirdMeal);

        //The images retrieved from the web API
        firstImage = findViewById(R.id.firstMealImageInput);
        secondImage = findViewById(R.id.secondMealImageInput);
        thirdImage = findViewById(R.id.thirdMealImageInput);

        //set up button.
        newButton = findViewById(R.id.getNewOne);

        //New button.
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startAPICall();
                } catch (Exception e) {
                    e.getStackTrace();
                }
                try {
                    firstMeal.setText("Name: " + firstMealName + "\n" + "Ready time in minutes: " + firstMealReadyTime + "\n" + "Servings: " + firstMealServing);
                    secondMeal.setText("Name: " + secondMealName + "\n" + "Ready time in minutes: " + secondMealReadyTime + "\n" + "Servings: " + secondMealServing);
                    thirdMeal.setText("Name: " + thirdMealName + "\n" + "Ready time in minutes: " + thirdMealReadyTime + "\n" + "Servings: " + thirdMealServing);
                    Picasso.get().load(firstMealImageURL).into(firstImage);
                    Picasso.get().load(secondMealImageURL).into(secondImage);
                    Picasso.get().load(thirdMealImageURL).into(thirdImage);
                } catch (NullPointerException e) {
                    Log.d(TAG, "Why is this happening to me");
                }
            }
        });

        try {
            firstMeal.setText("Name: " + firstMealName + "\n" + "Ready time in minutes: " + firstMealReadyTime + "\n" + "Servings: " + firstMealServing);
            secondMeal.setText("Name: " + secondMealName + "\n" + "Ready time in minutes: " + secondMealReadyTime + "\n" + "Servings: " + secondMealServing);
            thirdMeal.setText("Name: " + thirdMealName + "\n" + "Ready time in minutes: " + thirdMealReadyTime + "\n" + "Servings: " + thirdMealServing);
            Picasso.get().load(firstMealImageURL).into(firstImage);
            Picasso.get().load(secondMealImageURL).into(secondImage);
            Picasso.get().load(thirdMealImageURL).into(thirdImage);
        } catch (NullPointerException e) {
            Log.d(TAG, "Why is this happening to me");
        }
    }
}
