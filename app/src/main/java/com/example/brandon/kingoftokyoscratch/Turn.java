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

    ArrayList<Player> players;
    boolean isTokyoAttacked;
    String lastAttackerId;

    public Turn() {
        players = new ArrayList<>();
    }

    public void addPlayer(String playerName, String playerID) {
        Player tmpPlayer = new Player(playerName, playerID);
        players.add(tmpPlayer);
        isTokyoAttacked = false;
        lastAttackerId = "";
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

            /*if (obj.has("data")) {
                retVal.data = obj.getString("data");
            }
            if (obj.has("turnCounter")) {
                retVal.turnCounter = obj.getInt("turnCounter");
            }*/

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