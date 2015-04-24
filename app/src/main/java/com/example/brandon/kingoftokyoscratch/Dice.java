package com.example.brandon.kingoftokyoscratch;

import java.util.Random;

/**
 * Created by Brandon on 4/20/2015.
 *
 * Dice Class
 */
public class Dice {
    private int value;
    private String image;

    public Dice(){
        value = 0;
        image = "";
    }

    public int roll(){
        Random rn = new Random();
        int r = rn.nextInt(6);

        value = r;

        if (r == 0){
            image = "Energy";
        }
        else if (r == 1){
            image = "1";
        }
        else if (r == 2){
            image = "2";
        }
        else if (r == 3){
            image = "3";
        }
        else if (r == 4){
            image = "Claw";
        }
        else if (r == 5){
            image = "Heart";
        }
        else{
            image = "Error";
        }

        return r;
    }

    public int getValue(){
        return value;
    }

    public String getImage(){
        return image;
    }

}
