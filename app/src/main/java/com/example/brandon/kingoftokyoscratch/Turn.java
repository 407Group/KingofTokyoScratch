package com.example.brandon.kingoftokyoscratch;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Basic turn data. It's just a blank data string and a turn number counter.
 *
 * @author wolff
 *
 */
public class Turn {

    ArrayList<Player> players;
    boolean isTokyoAttacked;
    String lastAttackerId;
    ArrayList<Card> drawPile;
    ArrayList<Card> discardPile;
    Card displayPile[];

    public Turn() {
        players = new ArrayList<>();
        isTokyoAttacked = false;
        lastAttackerId = "";
        drawPile = new ArrayList<>();
        displayPile = new Card[3];
        discardPile = new ArrayList<>();
    }

    public void addPlayer(String playerName, String playerID) {
        Player tmpPlayer = new Player(playerName, playerID);
        players.add(tmpPlayer);
    }

    public boolean isTokyoAttacked() {
        return isTokyoAttacked;
    }

    public String getLastAttackerId() {
        return lastAttackerId;
    }

    public void setTokyoAttacked(boolean isTokyoAttacked) {
        this.isTokyoAttacked = isTokyoAttacked;
    }

    public void setLastAttackerId(String lastAttackerId) {
         this.lastAttackerId = lastAttackerId;
    }

    public boolean isTokyoEmpty(){
        for (int i = 0; i < players.size(); i++){
            if (players.get(i).getInTokyo()){
                return false;
            }
        }
        return true;
    }

    public void setUpCards(){
        drawPile.add(new Card(drawPile.size()+1, 5, "Apartment Building", "+3 VP", 0, 0, 1, 3, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 4, "Commuter Train", "+2 VP", 0, 0, 1, 2, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 3, "Corner Store", "+1 VP", 0, 0, 1, 1, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 8, "Energize", "+9 Energy", 0, 0, 0, 0, 1, 9));
        drawPile.add(new Card(drawPile.size()+1, 7, "Evacuation Orders", "-5 VP to enemies", 0, 0, 2, -5, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 3, "Fire Blast", "Enemies take 2 damage", 2, -2, 0, 0, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 6, "Gas Refinery", "+2 VP and 3 damage to enemies.", 2, -3, 1, 2, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 3, "Heal", "+2 hearts", 1, 2, 0, 0, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 4, "High Altitude Bombing", "3 damage to all", 3, -3, 0, 0, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 5, "Jet Fighters", "+5 VP and 4 damage", 1, -4, 1, 5, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 3, "National Guard", "+2 VP and 2 damage", 1, -2, 1, -2, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 6, "Nuclear Power Plant", "+2 VP and +3 hearts.", 1, 2, 1, 3, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 6, "Skyscraper", "+4 VP", 0, 0, 1, 4, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 4, "Tanks", "+4 VP and 3 damage.", 1, 3, 1, 4, 0, 0));
        drawPile.add(new Card(drawPile.size()+1, 6, "Amusement Park", "+4 VP", 0, 0, 1, 4, 0, 0));
        shuffleDrawPile();
        placeCard(0);
        placeCard(1);
        placeCard(2);
    }

    //Takes card from draw pile and places on spot num (0, 1, or 2)
    public void placeCard(int num){
        //make sure with in bound
        if (num < 0 || num > 2){
            return;
        }
        //make discard pile into draw pile if draw empty
        if(drawPile.isEmpty()){
            drawPile = discardPile;
            discardPile = new ArrayList<>();
            shuffleDrawPile();
        }
        //draw a card to display in spot num
        displayPile[num] = drawPile.remove(0);
    }

    //randomize drawPile's order
    public void shuffleDrawPile(){
        long seed = System.nanoTime();
        Collections.shuffle(drawPile, new Random(seed));
    }

    //replace a bought or discarded card
    public void replaceCard(int num){
        discardPile.add(displayPile[num]);
        placeCard(num);
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();

        try {
            //retVal.put("data", data);
            //retVal.put("turnCounter", turnCounter);
            retVal.put("isTokyoAttacked", isTokyoAttacked);
            retVal.put("lastAttackerId",lastAttackerId);
            for(int i = 0; i < players.size(); i++){
                JSONObject tempVal = new JSONObject();
                tempVal.put("name",players.get(i).getName());
                tempVal.put("pid",players.get(i).getPid());
                tempVal.put("heart",players.get(i).getHealth());
                tempVal.put("vp", players.get(i).getVictoryPoint());
                tempVal.put("energy", players.get(i).getEnergy());
                tempVal.put("inTokyo", players.get(i).getInTokyo());
                retVal.put("Player"+Integer.toString(i),tempVal);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String st = retVal.toString();

        Log.d("TURNDATA", "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of SkeletonTurn.
    static public Turn unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d("TURNDATA", "Empty array---possible bug.");
            return new Turn();
        }

        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d("TURNDATA", "====UNPERSIST \n" + st);

        Turn retVal = new Turn();

        try {
            JSONObject obj = new JSONObject(st);

            for (int i = 0; i < obj.length()-2; i++) { // TODO change bound
                String playerNum = "Player" + Integer.toString(i);
                if (obj.has(playerNum)) {
                    JSONObject playerObj = obj.getJSONObject(playerNum);

                    if (playerObj.has("name") && playerObj.has("pid")) {
                        //retVal.players.get(i).setName(obj.getString("name"));
                        retVal.addPlayer(playerObj.getString("name"),playerObj.getString("pid"));
                    }
                    if (playerObj.has("heart")) {
                        //Log.d("heart",Integer.toString(obj.getInt("heart")));
                        retVal.players.get(i).setHealth(playerObj.getInt("heart"));
                    }
                    if (playerObj.has("vp")) {
                        retVal.players.get(i).setVictoryPoint(playerObj.getInt("vp"));
                    }
                    if (playerObj.has("energy")) {
                        retVal.players.get(i).setEnergy(playerObj.getInt("energy"));
                    }
                    if (playerObj.has("inTokyo")) {
                        retVal.players.get(i).setInTokyo(playerObj.getBoolean("inTokyo"));
                    }
                }
            }
            if (obj.has("isTokyoAttacked")) {
                retVal.setTokyoAttacked(obj.getBoolean("isTokyoAttacked"));
                Log.d("TURNDATA","isTokyoAttacked = "+retVal.isTokyoAttacked());
            }
            if (obj.has("lastAttackerId")) {
                retVal.setLastAttackerId(obj.getString("lastAttackerId"));
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d("TURNDATA","Is Tokyo Attacked? "+retVal.isTokyoAttacked());

        return retVal;
    }
}