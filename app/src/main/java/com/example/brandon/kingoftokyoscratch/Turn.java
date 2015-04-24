package com.example.brandon.kingoftokyoscratch;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

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

    public static final String TAG = "EBTurn";

    public String data = "";
    public int turnCounter;
    ArrayList<Player> players;
    Dice[] dice = new Dice[6];
    int curP;   //index of current player
    int tokyoP; //index of player in tokyo
    boolean isTokyoHit; //True means tokyo was attacked this turn

    public Turn() {
        players = new ArrayList<>();
    }

    public void addPlayer(String playerName) {
        Player tmpPlayer = new Player(playerName);
        players.add(tmpPlayer);
    }

    // TODO Add an addplayer function to add to player arraylist

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();

        try {
            //retVal.put("data", data);
            //retVal.put("turnCounter", turnCounter);
            for(int i = 0; i < players.size(); i++){
                JSONObject tempVal = new JSONObject();
                tempVal.put("name",players.get(i).getName());
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

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of SkeletonTurn.
    static public Turn unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new Turn();
        }

        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d(TAG, "====UNPERSIST \n" + st);

        Turn retVal = new Turn();

        try {
            JSONObject obj = new JSONObject(st);

            /*if (obj.has("data")) {
                retVal.data = obj.getString("data");
            }
            if (obj.has("turnCounter")) {
                retVal.turnCounter = obj.getInt("turnCounter");
            }*/
            //Log.d("length",Integer.toString(obj.length()));
            for (int i = 0; i < obj.length(); i++) { // TODO change bound
                String playerNum = "Player" + Integer.toString(i);
                if (obj.has(playerNum)) {
                    JSONObject playerObj = obj.getJSONObject(playerNum);

                    if (playerObj.has("name")) {
                        //retVal.players.get(i).setName(obj.getString("name"));
                        retVal.addPlayer("name");
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

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retVal;
    }

    public void resolveDice(){
        int numHearts = 0;
        int numEnergy = 0;
        int numClaws = 0;
        int numOf1 = 0;
        int numOf2 = 0;
        int numOf3 = 0;
        int vp = 0;

        for (int i = 0; i < 6; i++){
            switch (dice[i].getValue()) {
                case 0: numEnergy++;
                    break;
                case 1:  numOf1++;
                    break;
                case 2:  numOf2++;
                    break;
                case 3:  numOf3++;
                    break;
                case 4:  numClaws++;
                    break;
                case 5:  numHearts++;
                    break;
                default:
                    break;
            }
        }

        if(numOf1 >= 3){
            numOf1 -= 3;
            vp = vp + 1 + numOf1;
        }
        if(numOf2 >= 3){
            numOf2 -= 3;
            vp = vp + 2 + numOf2;
        }
        if(numOf3 >= 3){
            numOf3 -= 3;
            vp = vp + 3 + numOf3;
        }

        //update current player's stats
        //TODO: Remove and replace with update all stats
        players.get(curP).updateVictoryPoint(vp);
        players.get(curP).updateEnergy(numEnergy);
        if(curP != tokyoP) {
            players.get(curP).updateHealth(numHearts);
        }

        //attack another player or take tokyo
        if(numClaws > 0){
            if(tokyoP < 0){
                tokyoP = curP;
            }
            else if(curP != tokyoP){ //current player not in tokyo
                players.get(tokyoP).takeDamage(numClaws);
            }
            else { //current player is in tokyo
                for(int i = 0; i < players.size(); i++){
                    if(tokyoP != i){
                        players.get(i).takeDamage(numClaws);
                    }
                }
            }
        }
    }
}