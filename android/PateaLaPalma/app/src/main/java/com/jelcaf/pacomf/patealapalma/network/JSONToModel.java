package com.jelcaf.pacomf.patealapalma.network;

import android.location.Location;

import com.jelcaf.pacomf.patealapalma.binding.dao.Comment;
import com.jelcaf.pacomf.patealapalma.binding.dao.Geo;
import com.jelcaf.pacomf.patealapalma.binding.dao.Photo;
import com.jelcaf.pacomf.patealapalma.binding.dao.Sendero;
import com.jelcaf.pacomf.patealapalma.binding.parser.ISO8601DateParse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Paco on 11/03/2015.
 */
public class JSONToModel {

    public static Double ratingFromResponseAddRating (JSONObject response){
        return response.optDouble("rating", 0);
    }

    public static Date dateFromResponse (JSONObject response){
        String dateStr = response.optString("date");
        try {
            return ISO8601DateParse.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static void senderoInfoFromResponse (JSONObject response, String idSendero, String idUser){
        try {

            Sendero sendero = Sendero.getByIdServer(idSendero);
            sendero.setDateUpdated(ISO8601DateParse.parse(response.optString("update")));

            Double rating = response.optDouble("rating", -1);
            if (rating != -1) {
                sendero.setRating(rating);
            }
            Integer userrating = response.optInt("userrating", -1);
            if (userrating != -1){
                sendero.setUserRating(userrating);
            }

            sendero.save();

            JSONArray comments = response.optJSONArray("comments");
            Comment comment;
            List<Comment> commentsL = sendero.comments();
            for (int i=0; i<comments.length(); i++){
                comment = commentFromJSON(sendero, comments.getJSONObject(i));
                if (comment != null) {
                    Boolean exists = false;
                    for (Comment c: commentsL){
                        if ((c.getDescription().equals(comment.getDescription())) && (c.getOwner().equals(comment.getOwner()))){
                            exists = true;
                            break;
                        }
                    }
                    if (!exists)
                        comment.save();
                }
            }

            JSONArray photos = response.optJSONArray("photos");
            Photo photo;
            List<Photo> photosL = sendero.photos();
            for (int i=0; i<photos.length(); i++){
                photo = photoFromJSON(sendero, photos.getJSONObject(i));
                if (photo != null) {
                    Boolean exists = false;
                    for (Photo p: photosL) {
                        if (p.getUrl().equals(photo.getUrl())){
                            exists = true;
                            break;
                        }

                    }
                    if (!exists)
                        photo.save();
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static Comment commentFromJSON (Sendero sendero, JSONObject commentJSON){
        try {
            Location location = geoStrFromJSON(commentJSON.optString("geo"), "comment");

            Comment comment = new Comment(sendero, commentJSON.optString("id_owner"), commentJSON.optString("name_owner"), commentJSON.optString("description"), ISO8601DateParse.parse(commentJSON.optString("date")), commentJSON.optInt("likes"), location);
            return comment;
        } catch (Exception e){
            return null;
        }
    }

    public static Photo photoFromJSON (Sendero sendero, JSONObject photoJSON){
        try {
            Location location = geoStrFromJSON(photoJSON.optString("geo"), "photo");

            Photo photo = new Photo(sendero, photoJSON.optString("url"), photoJSON.optString("id_owner"),  ISO8601DateParse.parse(photoJSON.optString("date")), photoJSON.optInt("likes"), location);
            return photo;
        } catch (Exception e){
            return null;
        }
    }

    // TODO: Anadir a la funcion las LISTAS de coordenadas y esas cosas
    public static Sendero senderoFromJSON (JSONObject senderoJSON){
        Sendero sendero = new Sendero();
        sendero.setName(senderoJSON.optString("name"));
        sendero.setRegularName(senderoJSON.optString("regular_name"));
        sendero.setVersion(senderoJSON.optString("version"));
        sendero.setLength(senderoJSON.optDouble("length"));
        sendero.setType(senderoJSON.optString("type"));
        sendero.setDifficulty(senderoJSON.optString("difficulty"));
        sendero.setServerId(senderoJSON.optString("_id"));
        JSONArray coordinates = senderoJSON.optJSONArray("coordinates");
        List<Geo> coordinatesList = new ArrayList();
        for (int i=0; i<coordinates.length(); i++){
            coordinatesList.add(geoFromJSON(sendero, coordinates.optJSONObject(i), "coordinate"));
        }

        /*
        List<Geo> wpList = new ArrayList();
        String wpStr = senderoJSON.optString("water_points");
        String[] wpCoordinates;
        if (wpStr != "[]"){
            wpStr = wpStr.substring(1, wpStr.length() - 1);
            wpCoordinates = wpStr.split("],");
            Geo geoWP;
            for (String wpCoordinate: wpCoordinates) {
                if (!wpCoordinate.endsWith("]"))
                    wpCoordinate+="]";
                geoWP = geoStrFromJSON(wpCoordinate, "water_points", sendero);
                if (geoWP != null)
                    wpList.add(geoWP);
            }
        }*/

        return sendero;
    }

    public static Location geoStrFromJSON (String geo, String source){
        Location location = new Location(source);
        try {
            geo = geo.replace("[", "");
            geo = geo.replace("]", "");
            String[] loc = geo.split(",");
            Double latitud = Double.valueOf(loc[1]);
            Double longitud = Double.valueOf(loc[0]);
            location.setLatitude(latitud);
            location.setLongitude(longitud);
        } catch (Exception e){}
        return location;
    }

    public static Geo geoStrFromJSON (String geo, String source, Sendero sendero){
        try {
            geo = geo.replace("[", "");
            geo = geo.replace("]", "");
            String[] loc = geo.split(",");
            Double latitud = Double.valueOf(loc[1]);
            Double longitud = Double.valueOf(loc[0]);
            return new Geo(latitud, longitud, source, sendero);
        } catch (Exception e){}
        return null;
    }

    public static Geo geoFromJSON (Sendero sendero, JSONObject geoJSON, String type){
        return new Geo(geoJSON.optDouble("latitude"), geoJSON.optDouble("longitude"), type, sendero);
    }
}
