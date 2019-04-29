package com.mcmaster.wiser.idyll.model.routing;

/**
 * Class to house all routing logic
 * Created by Chen on 2017-06-06.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import jsqlite.Database;
import jsqlite.Stmt;

public class Routing {

    /**
     * NOTES:
     * Looks good! Only thing I would suggest would be to rename some of the functions.
     * Change function names to reflect camelCase naming conventions.  This allows all of our code to look similar.
     * EG. getroutingr2rnearestfloor --> getRoutingR2RNearestFloor
     * You can easily change function names in Android Studio by right clicking an object and using
     * Refactor -> Rename.
     * This will automatically change all references to the function to the updated name.
     */

    private static final String ERROR = "\tERROR: ";
    private Database db;
    // private StringBuilder sb;
    Context myContext; //use camelCase for all object declarations
    public static boolean avoidIndoorPathways = false;
    public static boolean ElevatorOnly = false;

    public Routing(Context context) {
        myContext = context;
        //Log.d("Chen1", "check file");
        File file = new File(myContext.getExternalFilesDir(null).toString() + "/db3.sqlite");

        if (file.exists()) {
            //   Log.d("Chen1", "file exists ");
        } else {
            copyAssets();
            //    Log.d("Chen1", "file not exists");
        }

        try {
            db = new jsqlite.Database();
            db.open(myContext.getExternalFilesDir(null).toString() + "/db3.sqlite", jsqlite.Constants.SQLITE_OPEN_READWRITE
            );

        } catch (Exception e) {
            Log.d("Chen1", "start3");
            e.printStackTrace();
        }

    }

/*
    public String queryVersions() throws Exception {

        sb.append("Check versions...\n");

        Stmt stmt01 = db.prepare("SELECT spatialite_version();");
        if (stmt01.step()) {
            sb.append("\t").append("SPATIALITE_VERSION: " + stmt01.column_string(0));
            sb.append("\n");
        }

        stmt01 = db.prepare("SELECT proj4_version();");
        if (stmt01.step()) {
            sb.append("\t").append("PROJ4_VERSION: " + stmt01.column_string(0));
            sb.append("\n");
        }

        stmt01 = db.prepare("SELECT * FROM sqlite_master");
        while (stmt01.step()) {
            sb.append("\t").append("GEOS_VERSION: " + stmt01.column_string(0));
            sb.append("\n");
        }

        stmt01 = db.prepare("SELECT * FROM sqlite_master");
        if (stmt01.step()) {
            sb.append("\t").append("GEOS_VERSION: " + stmt01.column_string(0));
            sb.append("\n");
        }

        stmt01.close();

        sb.append("Done...\n");
        return sb.toString();
    }
*/


    private void copyAssets() {
        AssetManager assetManager = myContext.getAssets();
        //   Log.e("copy", "copy");
        String[] files = {"db3.sqlite"};
        //      try {
        //         files = assetManager.list("");
        //      } catch (IOException e) {
        //         Log.e("tag", "Failed to get asset file list.", e);
        //     }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                //   Log.e("copy", "copy");
                in = assetManager.open(filename);

                String path = Environment.getExternalStorageState();
                File outFile = new File(myContext.getExternalFilesDir(null), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    public String[] findRidBid(String lon, String lat, int currentlevel) {
        String romeId = null;
        String buildingId = null;
        //String query = " SELECT rid,min(DISTANCE(geometry, MakePoint("+lon+","+lat+ "))    FROM room2f";
        String query = " SELECT rid,outid , min(DISTANCE(geometry, MakePoint(" + lon + "," + lat + ")))  FROM \"roomsnew\" where floor = "+Integer.toString(currentlevel);
        //    Log.d("Chen5", query);
        try {
            Stmt stmt = db.prepare(query);
            if (stmt.step()) {
                // Log.d("Chen5", "got");
                romeId = stmt.column_string(0);
                buildingId = stmt.column_string(1);
            }
            stmt.close();

        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return new String []{romeId, buildingId};
    }

    public String findRid(String lon, String lat, int currentlevel) {
        String romeId = null;
        //String query = " SELECT rid,min(DISTANCE(geometry, MakePoint("+lon+","+lat+ "))    FROM room2f";
        String query = " SELECT rid, min(DISTANCE(geometry, MakePoint(" + lon + "," + lat + ")))  FROM \"roomsnew\" where floor = "+Integer.toString(currentlevel);
        //     Log.d("Chen5", query);
        try {
            Stmt stmt = db.prepare(query);
            if (stmt.step()) {
                // Log.d("Chen5", "got");
                romeId = stmt.column_string(0);
            }
            stmt.close();

        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return romeId;
    }

    public int findBid(String lon, String lat, int currentlevel) {
        String buildingId = null;
        //String query = " SELECT rid,min(DISTANCE(geometry, MakePoint("+lon+","+lat+ "))    FROM room2f";
        String query = " SELECT outid, min(DISTANCE(geometry, MakePoint(" + lon + "," + lat + ")))  FROM \"roomsnew\" where floor = "+Integer.toString(currentlevel);
        //   Log.d("Chen5", query);
        try {
            Stmt stmt = db.prepare(query);
            if (stmt.step()) {
                // Log.d("Chen5", "got");
                buildingId = stmt.column_string(0);
            }
            stmt.close();

        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return Integer.parseInt(buildingId);
    }

    private String finddestnode(String lon, String lat, int floor) {
        String destnode = null;
        String query = "SELECT Node_To FROM \"roads" + Integer.toString(floor) + "f\"  ORDER BY DISTANCE(Startpoint(geometry), MakePoint(" + lon + "," + lat + ")) limit 1";
        //fLog.d("Chen",query);
        try {
            Stmt stmt = db.prepare(query);
            if (stmt.step()) {
                destnode = stmt.column_string(0);
            }
            stmt.close();

        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return destnode;
    }

    private Location findroomlocation(int floor, int rid) {
        String query = "SELECT location,outid FROM \"roomsnew\" where rid = " + Integer.toString(rid) + ";";
        String location = null;
        Location roomLocation = new Location("");
        try {
            Stmt stmt = db.prepare(query);
            if (stmt.step()) {
                location = stmt.column_string(0);
                roomLocation.setProvider(stmt.column_string(1));

            }
            ///   Log.d("Chen6", location);
            ///   Log.d("Chen6", stmt.column_string(1));
            location = location.substring(6);
            String[] latlon = location.split(" ");

            roomLocation.setLongitude(Double.parseDouble(latlon[0]));
            roomLocation.setLatitude(Double.parseDouble(latlon[1].substring(0, latlon[1].length() - 1)));
            stmt.close();
        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return roomLocation;
    }

    public String[] queryPathbylatlon(String sourcelon, String sourcelat, String destlon, String destlat, int floor) {
        String sourcenode;
        String destnode;
        String cost = null;

        String geojson = "";
        String geo_json_head = "{\"type\":\"FeatureCollection\",\"features\":[";
        String geo_json_tail = "]}";
        String geo_json_small_head = "{\"type\":\"Feature\",\"properties\":{},\"geometry\":";
        String geo_json_small_tail = "}";
        String geometry = "";
        String query;
        sourcenode = findsourcenode(sourcelon, sourcelat, floor);
        destnode = finddestnode(destlon, destlat, floor);
        if (avoidIndoorPathways && floor == 1)
            query = "SELECT NodeFrom,NodeTo,cost,AsGeoJSON(geometry)  FROM \"roads" + Integer.toString(floor) + "f_indoor_net\" where nodefrom = " + sourcenode + "   AND NodeTo = " + destnode + ";";
        else
            query = "SELECT NodeFrom,NodeTo,cost,AsGeoJSON(geometry)  FROM \"roads" + Integer.toString(floor) + "f_net\" where nodefrom = " + sourcenode + "   AND NodeTo = " + destnode + ";";
        try {
            //Log.d("Chen",query);
            Stmt stmt = db.prepare(query);
            if (stmt.step()) {
                String nomeStr = stmt.column_string(0);
                String Nodeto = stmt.column_string(1);
                cost = stmt.column_string(2);
                geometry = stmt.column_string(3);
                //Log.d("Chen5", nomeStr+"  "+Nodeto+"  "+cost+"   "+geometry);
            }
            geojson += geo_json_head;
            geojson += geo_json_small_head;
            geojson += geometry;
            geojson += geo_json_small_tail;
            geojson += geo_json_tail;

            stmt.close();
        } catch (Exception e) {
            Log.d("exception", e.toString());
        }
        // Log.d("Chen8",geojson);
        return new String[]{geojson, cost};
    }



    public String[] getroutingr2latlon_crossfloor(int src_floor, int src_rid, int src_bid, Location sourceLocation, Location destLocation, int dest_floor) {
        String[] destRidBid = findRidBid(Double.toString(destLocation.getLongitude()), Double.toString(destLocation.getLatitude()), dest_floor);
        int destRid=Integer.parseInt(destRidBid[0]);
        int destBid=Integer.parseInt(destRidBid[1]);
        if (src_bid == 0){
            src_bid=findBid(Double.toString(sourceLocation.getLongitude()),Double.toString(sourceLocation.getLatitude()), src_floor);
        }
        if(src_rid == 0){
            return getroutingb2r(src_bid, dest_floor, destRid);
        }
        double cost = 99999;

        //Dest Room is in floor 1, elevators/staircase are not inloved.
        return getroutingr2r_nearestataircase(src_floor, src_bid, src_rid, sourceLocation, dest_floor, destBid, destRid ,destLocation);
    }

    public String[] getroutingr2latlon_ramdon(int src_floor, int src_rid, int srcbid,Location dest_location) {

        String[] Queryresultground = new String[2];
        String geojsonSrcFloor = null;
        String geojsonGround = null;
        String geojsonDestFloor = null;

        if(src_rid == 0){
            Location srcBuildingLocation = findBuildinglocation(srcbid);
            Queryresultground = queryPathbylatlon(
                    Double.toString(srcBuildingLocation.getLongitude()),
                    Double.toString(srcBuildingLocation.getLatitude()),
                    Double.toString(dest_location.getLongitude()),
                    Double.toString(dest_location.getLatitude()),
                    1);
            return new String[]{null, "1" + Queryresultground[0], null};
        }

        Location srcRoomLocation = findroomlocation(src_floor, src_rid);


        if (src_floor == 1) {
            Queryresultground = queryPathbylatlon(
                    Double.toString(srcRoomLocation.getLongitude()),
                    Double.toString(srcRoomLocation.getLatitude()),
                    Double.toString(dest_location.getLongitude()),
                    Double.toString(dest_location.getLatitude()),
                    1);
            geojsonGround = Queryresultground[0];

            return new String[]{geojsonSrcFloor, "1" + geojsonGround, geojsonDestFloor};
        } else {
            Location neareststaircases = findneareststaircase(src_floor, src_rid);

            String[] paths = findPathwithStaircase(srcRoomLocation, neareststaircases, dest_location, src_floor, 1);
            geojsonSrcFloor = paths[0];
            geojsonGround = paths[1];
            //   Log.d("Chen3", "ss");
            return new String[]{Integer.toString(src_floor) + geojsonSrcFloor, "1" + geojsonGround, null};
        }


    }

    public String[] getroutingr2r_nearestataircase(int src_floor, int src_bid, int src_rid, Location srcRoomLocation, int dest_floor,int dest_bid, int dest_rid, Location destRoomLocation) {
        //    Log.d("chen","bid" + src_bid+"  "+dest_bid);
        //     Log.d("chen","floor"+ src_floor+"  "+dest_floor);
        String geojsonSrcFloor = null;
        String geojsonGround = null;
        String geojsonDestFloor = null;

        String[] Queryresultsrc = new String[2];
        String[] Queryresultground = new String[2];
        String[] Queryresultdest = new String[2];

        String[] Queryresultsrc_ground_dest = new String[3];

        Location src_staircase;
        Location dest_staircase;
        Location choosen_staircase;
        //src and dest room are in the first floor. No need to consider staircase.
        if ((src_floor == 1 && dest_floor == 1)|| (src_bid == dest_bid && src_floor== dest_floor)) {
            Queryresultground = queryPathbylatlon(
                    Double.toString(srcRoomLocation.getLongitude()),
                    Double.toString(srcRoomLocation.getLatitude()),
                    Double.toString(destRoomLocation.getLongitude()),
                    Double.toString(destRoomLocation.getLatitude()),
                    src_floor);
            geojsonGround = Queryresultground[0];

            return new String[]{geojsonSrcFloor, Integer.toString(src_floor) + geojsonGround, geojsonDestFloor};

        }


        //The src and des node are not on the first floor, we need to find the nearest staircase/elevator.
//        if (Integer.parseInt(srcRoomLocation.getProvider()) == Integer.parseInt(destRoomLocation.getProvider())) {
//            //    Log.d("Chen4", "start");
//            src_staircase = findneareststaircase(src_floor, src_rid);
//            dest_staircase = findneareststaircase(dest_floor, dest_rid);
//            if (src_staircase.equals(dest_staircase)) {
//                choosen_staircase = src_staircase;
//            } else {
//                choosen_staircase = Findstaircasewithmincost(srcRoomLocation, src_staircase, destRoomLocation, dest_staircase, src_floor, dest_floor);
//            }
//
//            Queryresultsrc_ground_dest = querysrc_ground_dest(srcRoomLocation, choosen_staircase, destRoomLocation, null, src_floor, dest_floor);
//
//            return new String[]{Integer.toString(src_floor) + Queryresultsrc_ground_dest[0], null, Integer.toString(dest_floor) + Queryresultsrc_ground_dest[2]};
//
//        }


        if (src_floor != 1 && dest_floor == 1) {
            Location neareststaircases = findneareststaircase(src_floor, src_rid);

            String[] paths = findPathwithStaircase(srcRoomLocation, neareststaircases, destRoomLocation, src_floor, dest_floor);
            geojsonSrcFloor = paths[0];
            geojsonGround = paths[1];
            //  Log.d("Chen3", "ss");
            return new String[]{Integer.toString(src_floor) + geojsonSrcFloor, "1" + geojsonGround, null};
        } else if (src_floor == 1 && dest_floor != 1) {
            Location neareststaircases = findneareststaircase(dest_floor, dest_rid);
            String[] paths = findPathwithStaircase(srcRoomLocation, neareststaircases,destRoomLocation , src_floor, dest_floor);
            geojsonGround = paths[0];
            geojsonDestFloor = paths[1];

            return new String[]{null, "1" + geojsonGround, Integer.toString(dest_floor) + geojsonDestFloor};
        } //Both src and dest floor are not in the ground level.
        else if (src_floor != 1 && dest_floor != 1) {

            //Iterate all staircases, finds shortest path.
            Location nearestStaircaseSrc = findneareststaircase(src_floor, src_rid);
            //     Log.d("Chen3", " nearestStaircaseSrc " + nearestStaircaseSrc.toString());

            Location nearestStaircaseDest = findneareststaircase(dest_floor, dest_rid);
            Queryresultsrc_ground_dest = querysrc_ground_dest(srcRoomLocation, nearestStaircaseSrc, destRoomLocation, nearestStaircaseDest, src_floor, dest_floor);

            //     Log.d("Chen2", "0 :" + Queryresultsrc_ground_dest[0]);
            //       Log.d("Chen2", "1 :" + Queryresultsrc_ground_dest[1]);
            //      Log.d("Chen2", "2  :" + Queryresultsrc_ground_dest[2]);
            return new String[]{Integer.toString(src_floor) + Queryresultsrc_ground_dest[0], "1" + Queryresultsrc_ground_dest[1], Integer.toString(dest_floor) + Queryresultsrc_ground_dest[2]};
        }

        return new String[]{geojsonSrcFloor, "1" + geojsonGround, geojsonDestFloor};
    }


    private int findGeometricNearestStaircase(ArrayList<Location> staircases, Location RoomLocation) {
        double costSrc = 99999999999.0;
        int nearestStaircaseSrc = 0;

        for (int i = 0; i < staircases.size(); i++) {
            double distance = Math.hypot(RoomLocation.getLongitude() * RoomLocation.getLongitude() -
                            staircases.get(i).getLongitude() * staircases.get(i).getLongitude(),
                    RoomLocation.getLatitude() * RoomLocation.getLatitude() -
                            staircases.get(i).getLatitude() * staircases.get(i).getLatitude());

            if (distance < costSrc && distance != 0) {
                //This staircase has the minimum cost
                costSrc = distance;
                nearestStaircaseSrc = i;
            }
        }
        return nearestStaircaseSrc;
    }


    private String[] querysrc_ground_dest(Location srcRoomLocation, Location source_staircase, Location destRoomLocation, Location dest_staircase, int src_floor, int dest_floor) {
        String[] Queryresultsrc = new String[2];
        String[] Queryresultground = new String[2];

        String[] Queryresultdest = new String[2];
        if (dest_staircase == null) {

            Queryresultsrc = queryPathbylatlon(
                    Double.toString(srcRoomLocation.getLongitude()),
                    Double.toString(srcRoomLocation.getLatitude()),
                    Double.toString(source_staircase.getLongitude()),
                    Double.toString(source_staircase.getLatitude()),
                    src_floor);
            //     Log.d("Chen4", "res:" + Queryresultsrc[0]);
            Queryresultdest = queryPathbylatlon(
                    Double.toString(source_staircase.getLongitude()),
                    Double.toString(source_staircase.getLatitude()),
                    Double.toString(destRoomLocation.getLongitude()),
                    Double.toString(destRoomLocation.getLatitude()),
                    dest_floor);
            return new String[]{Queryresultsrc[0], Queryresultground[0], Queryresultdest[0]};


        }
        ///     Log.d("Chen4", "two staircase");
        Queryresultsrc = queryPathbylatlon(
                Double.toString(srcRoomLocation.getLongitude()),
                Double.toString(srcRoomLocation.getLatitude()),
                Double.toString(source_staircase.getLongitude()),
                Double.toString(source_staircase.getLatitude()),
                src_floor);

        Queryresultground = queryPathbylatlon(
                Double.toString(source_staircase.getLongitude()),
                Double.toString(source_staircase.getLatitude()),
                Double.toString(dest_staircase.getLongitude()),
                Double.toString(dest_staircase.getLatitude()),
                1);

        Queryresultdest = queryPathbylatlon(
                Double.toString(dest_staircase.getLongitude()),
                Double.toString(dest_staircase.getLatitude()),
                Double.toString(destRoomLocation.getLongitude()),
                Double.toString(destRoomLocation.getLatitude()),
                dest_floor);
//        Log.d("Chen2", "!!Queryresultdest:" + Queryresultdest);
        return new String[]{Queryresultsrc[0], Queryresultground[0], Queryresultdest[0]};

    }

    private Location Findstaircasewithmincost(Location srcrLocation, Location src_staircase, Location destRoomLocation, Location dest_staircase, int src_floor, int dest_floor) {
        String[] Queryresult1 = new String[2];
        String[] Queryresult2 = new String[2];

        String[] Queryresult3 = new String[2];
        String[] Queryresult4 = new String[2];

        Queryresult1 = queryPathbylatlon(
                Double.toString(srcrLocation.getLongitude()),
                Double.toString(srcrLocation.getLatitude()),
                Double.toString(src_staircase.getLongitude()),
                Double.toString(src_staircase.getLatitude()),
                src_floor);

        Queryresult2 = queryPathbylatlon(
                Double.toString(src_staircase.getLongitude()),
                Double.toString(src_staircase.getLatitude()),
                Double.toString(destRoomLocation.getLongitude()),
                Double.toString(destRoomLocation.getLatitude()),
                dest_floor);

        Queryresult3 = queryPathbylatlon(
                Double.toString(srcrLocation.getLongitude()),
                Double.toString(srcrLocation.getLatitude()),
                Double.toString(dest_staircase.getLongitude()),
                Double.toString(dest_staircase.getLatitude()),
                src_floor);

        Queryresult4 = queryPathbylatlon(
                Double.toString(dest_staircase.getLongitude()),
                Double.toString(dest_staircase.getLatitude()),
                Double.toString(destRoomLocation.getLongitude()),
                Double.toString(destRoomLocation.getLatitude()),
                dest_floor);
        if ((Double.parseDouble(Queryresult1[1]) + Double.parseDouble(Queryresult2[1])) >
                (Double.parseDouble(Queryresult3[1]) + Double.parseDouble(Queryresult4[1]))) {
            return dest_staircase;
        }
        return src_staircase;
    }

    private Location findneareststaircase(int floor, int rid) {
        String query = "SELECT NearestStaircase,outid FROM \"roomsnew\" where rid = " + Integer.toString(rid) + ";";
        String location = null;
        Location neareatStaircase = new Location("");
        try {
            Stmt stmt = db.prepare(query);
            if (stmt.step()) {
                location = stmt.column_string(0);
                neareatStaircase.setProvider(stmt.column_string(1));

            }
            location = location.substring(6);
            String[] latlon = location.split(" ");

            neareatStaircase.setLongitude(Double.parseDouble(latlon[0]));
            neareatStaircase.setLatitude(Double.parseDouble(latlon[1].substring(0, latlon[1].length() - 1)));
            stmt.close();

        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return neareatStaircase;

    }


    public String[] getroutingb2b(int src_building, int dest_building) {

        Location srcBuildingLocation = findBuildinglocation(src_building);
        Location destBuildingLocation = findBuildinglocation(dest_building);

        String[] Queryresultground = queryPathbylatlon(
                Double.toString(srcBuildingLocation.getLongitude()),
                Double.toString(srcBuildingLocation.getLatitude()),
                Double.toString(destBuildingLocation.getLongitude()),
                Double.toString(destBuildingLocation.getLatitude()),
                1);
        return new String[]{null, "1" + Queryresultground[0], null};
    }


    public String[] getroutingb2r(int src_building, int dest_floor, int dest_rid) {

        Location srcBuildingLocation = findBuildinglocation(src_building);
        Location destroomLocation = findroomlocation(dest_floor, dest_rid);
        double cost = 99999;
        //Dest Room is in floor 1, elevators/staircase are not inloved.
        if (dest_floor == 1) {
            String[] Queryresultground = queryPathbylatlon(
                    Double.toString(srcBuildingLocation.getLongitude()),
                    Double.toString(srcBuildingLocation.getLatitude()),
                    Double.toString(destroomLocation.getLongitude()),
                    Double.toString(destroomLocation.getLatitude()),
                    1);
            return new String[]{null, "1" + Queryresultground[0], null};
        }
        //Dest Room isn't in floor 1, elevators/staircase are inloved.
        else {
            Location dest_staircase = findneareststaircase(dest_floor, dest_rid);

            String[] Queryresultsrc = queryPathbylatlon(
                    Double.toString(srcBuildingLocation.getLongitude()),
                    Double.toString(srcBuildingLocation.getLatitude()),
                    Double.toString(dest_staircase.getLongitude()),
                    Double.toString(dest_staircase.getLatitude()),
                    1);

            String[] Queryresultdest = queryPathbylatlon(
                    Double.toString(dest_staircase.getLongitude()),
                    Double.toString(dest_staircase.getLatitude()),
                    Double.toString(destroomLocation.getLongitude()),
                    Double.toString(destroomLocation.getLatitude()),
                    dest_floor);
            return new String[]{null, "1" + Queryresultsrc, Integer.toString(dest_floor) + Queryresultdest};
        }
    }

    /*
    public String[] getroutingr2latlon(int src_floor, int src_rid, String destLongtitude, String destLatitude) {

        Location srcroomLocation = findroomlocation(src_floor, src_rid);
        double cost = 99999;
        //Dest Room is in floor 1, elevators/staircase are not inloved.
        if (src_floor == 1) {
            String[] Queryresultground = queryPathbylatlon(
                    Double.toString(srcroomLocation.getLongitude()),
                    Double.toString(srcroomLocation.getLatitude()),
                    destLongtitude, destLatitude);
            return new String[]{null, "1" + Queryresultground[0], null};
        }
        //Dest Room isn't in floor 1, elevators/staircase are inloved.
        else {
            Location latlon = new Location("");
            latlon.setLongitude(Double.parseDouble(destLongtitude));
            latlon.setLatitude(Double.parseDouble(destLatitude));

            ArrayList<Location> staircases = findAllStaircase(src_floor, srcroomLocation.getProvider());
            String[] paths = findPathwithStaircase(srcroomLocation, staircases, latlon);

            return new String[]{null, Integer.toString(src_floor) + paths[0], "1" + paths[1]};
        }
    }

    public String[] getroutingr2r(int src_floor, int src_rid, int dest_floor, int dest_rid) {

        Location srcRoomLocation = findroomlocation(src_floor, src_rid);
        Location destRoomLocation = findroomlocation(dest_floor, dest_rid);
        String geojsonSrcFloor = null;
        String geojsonGround = null;
        String geojsonDestFloor = null;

        String[] Queryresultsrc = new String[2];
        String[] Queryresultground = new String[2];
        String[] Queryresultdest = new String[2];
        double cost = 99999999999.0;
        //  Log.d("Chen2", "start");
        //The src and des node are not on the first floor, we need to find the nearest staircase/elevator.
        if (src_floor != 1 && dest_floor == 1) {
            ArrayList<Location> source_staircases = findAllStaircase(src_floor, srcRoomLocation.getProvider());
            Log.d("Chen2", "Number of staircase" + source_staircases.size());
            //Iterate all staircases, finds shortest path.
            for (int i = 0; i < source_staircases.size(); i++) {
                Queryresultsrc = queryPathbylatlon(
                        Double.toString(srcRoomLocation.getLongitude()),
                        Double.toString(srcRoomLocation.getLatitude()),
                        Double.toString(source_staircases.get(i).getLongitude()),
                        Double.toString(source_staircases.get(i).getLatitude()));

                Queryresultground = queryPathbylatlon(
                        Double.toString(source_staircases.get(i).getLongitude()),
                        Double.toString(source_staircases.get(i).getLatitude()),
                        Double.toString(destRoomLocation.getLongitude()),
                        Double.toString(destRoomLocation.getLatitude()));
                //   Log.d("Chen2", "Cost:" + Double.toString(Double.parseDouble(Queryresultsrc[1]) + Double.parseDouble(Queryresultground[1])));
                if (Double.parseDouble(Queryresultsrc[1]) + Double.parseDouble(Queryresultground[1]) < cost)
                    //This staircase has the minimum cost

                    cost = Double.parseDouble(Queryresultsrc[1]) + Double.parseDouble(Queryresultground[1]);
                geojsonSrcFloor = Queryresultsrc[0];
                geojsonGround = Queryresultground[0];
            }
        }
        //Both src and dest floor are not in the ground level.
        if (src_floor != 1 && dest_floor != 1) {
            ArrayList<Location> source_staircases = findAllStaircase(src_floor, srcRoomLocation.getProvider());
            ArrayList<Location> dest_staircases = findAllStaircase(dest_floor, destRoomLocation.getProvider());
            Log.d("Chen2", "Number of staircase source " + source_staircases.size());
            Log.d("Chen2", "Number of staircase dest " + dest_staircases.size());

            //Iterate all staircases, finds shortest path.
            for (int i = 0; i < source_staircases.size(); i++) {
                for (int j = 0; j < dest_staircases.size(); j++) {
                    Queryresultsrc = queryPathbylatlon(
                            Double.toString(srcRoomLocation.getLongitude()),
                            Double.toString(srcRoomLocation.getLatitude()),
                            Double.toString(source_staircases.get(i).getLongitude()),
                            Double.toString(source_staircases.get(i).getLatitude()));

                    Queryresultground = queryPathbylatlon(
                            Double.toString(source_staircases.get(i).getLongitude()),
                            Double.toString(source_staircases.get(i).getLatitude()),
                            Double.toString(dest_staircases.get(j).getLongitude()),
                            Double.toString(dest_staircases.get(j).getLatitude()));

                    Queryresultdest = queryPathbylatlon(
                            Double.toString(dest_staircases.get(j).getLongitude()),
                            Double.toString(dest_staircases.get(j).getLatitude()),
                            Double.toString(destRoomLocation.getLongitude()),
                            Double.toString(destRoomLocation.getLatitude()));

                    Log.d("Chen2", "Cost:" + Double.toString(Double.parseDouble(Queryresultsrc[1]) + Double.parseDouble(Queryresultground[1]) + Double.parseDouble(Queryresultdest[1])));
                    if (Double.parseDouble(Queryresultsrc[1]) + Double.parseDouble(Queryresultground[1]) + Double.parseDouble(Queryresultdest[1]) < cost) {
                        //This staircase has the minimum cost
                        cost = Double.parseDouble(Queryresultsrc[1]) + Double.parseDouble(Queryresultground[1]) + Double.parseDouble(Queryresultdest[1]);
                    }
                    geojsonSrcFloor = Queryresultsrc[0];
                    geojsonGround = Queryresultground[0];
                    geojsonDestFloor = Queryresultdest[0];
                }
            }
        }
        Log.d("Chen2", "Cost:" + Double.toString(cost));

        return new String[]{geojsonSrcFloor, geojsonGround, geojsonDestFloor};
    }


    private String[] findPathwithStaircase(Location srcrLocation, ArrayList<Location> staircases, Location destLocation, int src_floor, int dest_floor) {
        String[] Queryresult1 = new String[2];
        String[] Queryresult2 = new String[2];

        int nearestStaircase = 0;
        double cost = 9999999;

        for (int i = 0; i < staircases.size(); i++) {

            double distance = Math.hypot(srcrLocation.getLongitude() * srcrLocation.getLongitude() -
                              staircases.get(i).getLongitude() * staircases.get(i).getLongitude(),
                              srcrLocation.getLatitude() * srcrLocation.getLatitude() -
                              staircases.get(i).getLatitude() * staircases.get(i).getLatitude());

            if (distance < cost && distance != 0) {
                //This staircase has the minimum cost
                cost = distance;
                nearestStaircase = i;
            }
            Log.d("Chen7","id: "+i+" cost: "+distance);
        }
            Queryresult1 = queryPathbylatlon(
                    Double.toString(srcrLocation.getLongitude()),
                    Double.toString(srcrLocation.getLatitude()),
                    Double.toString(staircases.get(nearestStaircase).getLongitude()),
                    Double.toString(staircases.get(nearestStaircase).getLatitude()),
                    src_floor);

            Queryresult2 = queryPathbylatlon(
                    Double.toString(staircases.get(nearestStaircase).getLongitude()),
                    Double.toString(staircases.get(nearestStaircase).getLatitude()),
                    Double.toString(destLocation.getLongitude()),
                    Double.toString(destLocation.getLatitude()),
                    dest_floor);


        return new String[]{Queryresult1[0], Queryresult2[0]};
    }
 */
    private String[] findPathwithStaircase(Location srcrLocation, Location staircase, Location destLocation, int src_floor, int dest_floor) {
        String[] Queryresult1 = new String[2];
        String[] Queryresult2 = new String[2];
        double cost = 9999999;

        Queryresult1 = queryPathbylatlon(
                Double.toString(srcrLocation.getLongitude()),
                Double.toString(srcrLocation.getLatitude()),
                Double.toString(staircase.getLongitude()),
                Double.toString(staircase.getLatitude()),
                src_floor);
        //   Log.d("Chen3",Queryresult1[0]);
        Queryresult2 = queryPathbylatlon(
                Double.toString(staircase.getLongitude()),
                Double.toString(staircase.getLatitude()),
                Double.toString(destLocation.getLongitude()),
                Double.toString(destLocation.getLatitude()),
                dest_floor);
        //    Log.d("Chen3",Queryresult2[0]);

        return new String[]{Queryresult1[0], Queryresult2[0]};
    }

    private Location findBuildinglocation(int buildingid) {
        String query = "SELECT location FROM buildingLocation where outid = " + Integer.toString(buildingid) + ";";
        String location = null;
        Location buildingLocation = new Location("");
        try {
            Stmt stmt = db.prepare(query);
            if (stmt.step()) {
                location = stmt.column_string(0);
            }
            location = location.substring(6);
            String[] latlon = location.split(" ");

            buildingLocation.setLongitude(Double.parseDouble(latlon[0]));
            buildingLocation.setLatitude(Double.parseDouble(latlon[1].substring(0, latlon[1].length() - 1)));
            stmt.close();
        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return buildingLocation;
    }


    private String findsourcenode(String lon, String lat, int floor) {
        String fromnode = null;
        //  String query = "  SELECT rid FROM Room2f  ORDER BY DISTANCE (geometry, MakePoint(" + lon + "," + lat + ")) limit 1";
        // String query = "SELECT NodeFrom,min(DISTANCE(Startpoint(geometry), MakePoint("+lon+"," +lat+"))) FROM PathWay1f ";
        String query = "SELECT Node_From FROM \"roads" + Integer.toString(floor) + "f\"  ORDER BY DISTANCE(Startpoint(geometry), MakePoint(" + lon + "," + lat + ")) limit 1";
        //Log.d("Chen",query);
        try {
            Stmt stmt = db.prepare(query);
            if (stmt.step()) {
                fromnode = stmt.column_string(0);
            }
            stmt.close();

        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return fromnode;
    }


    public ArrayList<Location> findWashrooms(String lon, String lat, int floor) {
        String location = null;
        String query = "SELECT Location FROM \"roomsnew\"  where utility =\'bathroom\' AND floor = "+Integer.toString(floor) +" ORDER BY DISTANCE(geometry, MakePoint(" + lon + "," + lat + ")) limit 3";
        //Log.d("Chen7", query);
        ArrayList<Location> locations = new ArrayList<Location>();
        try {
            Stmt stmt = db.prepare(query);
            while (stmt.step()) {
                location = stmt.column_string(0);
                location = location.substring(6);
                String[] latlon = location.split(" ");

                Location roomLocation = new Location("");
                roomLocation.setLongitude(Double.parseDouble(latlon[0]));
                roomLocation.setLatitude(Double.parseDouble(latlon[1].substring(0, latlon[1].length() - 1)));
                locations.add(roomLocation);
            }
            ///     Log.d("Chen7", locations.toString());
            stmt.close();

        } catch (Exception e) {
            ///        Log.d("exception", e.toString());
        }

        return locations;
    }

    ArrayList<String> locations_room = new ArrayList<String>();
    ArrayList<String> locations_stair = new ArrayList<String>();


    private ArrayList<Location> findAllStaircase(int floor, String buildId) {
        String query;
        if (ElevatorOnly) {
            query = "SELECT location FROM \"roomsnew\" WHERE outid = " +
                    buildId + " AND utility =\'elevator\' AND floor = "+Integer.toString(floor);
        } else {
            query = "SELECT location FROM \"roomsnew\" WHERE outid = " +
                    buildId + " AND (utility =\'elevator\' OR utility =\'stair\') AND floor = "+Integer.toString(floor);
        }
        ///      Log.d("Chen7", query);
        String location = null;
        ArrayList<Location> locations = new ArrayList<Location>();

        try {
            Stmt stmt = db.prepare(query);
            while (stmt.step()) {
                location = stmt.column_string(0);
                //Log.d("Chen6", location);
                if (location.equals("\\N")) {
                    continue;
                }
                locations_stair.add(location);
                location = location.substring(6);
                String[] latlon = location.split(" ");

                Location roomLocation = new Location("");
                roomLocation.setLongitude(Double.parseDouble(latlon[0]));
                roomLocation.setLatitude(Double.parseDouble(latlon[1].substring(0, latlon[1].length() - 1)));

                locations.add(roomLocation);
            }
            stmt.close();
        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return locations;
    }


    private ArrayList<Location> findAllStaircase2(int floor, String buildId) {
        String query;
        if (ElevatorOnly) {
            query = "SELECT location FROM \"roomsnew\" WHERE outid = " +
                    buildId + " AND utility =\'elevator\' AND floor = "+Integer.toString(floor);
        } else {
            query = "SELECT location FROM \"roomsnew\" WHERE outid = " +
                    buildId + " AND (utility =\'elevator\' OR utility =\'stair\') AND floor = 1";
        }
        ///      Log.d("Chen7", query);
        String location = null;
        ArrayList<Location> locations = new ArrayList<Location>();

        try {
            Stmt stmt = db.prepare(query);
            while (stmt.step()) {
                location = stmt.column_string(0);
                //Log.d("Chen6", location);
                if (location.equals("\\N")) {
                    continue;
                }
                locations_stair.add(location);
                location = location.substring(6);
                String[] latlon = location.split(" ");

                Location roomLocation = new Location("");
                roomLocation.setLongitude(Double.parseDouble(latlon[0]));
                roomLocation.setLatitude(Double.parseDouble(latlon[1].substring(0, latlon[1].length() - 1)));

                locations.add(roomLocation);
            }
            stmt.close();
        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return locations;
    }

    public void creatVoronoiDiagram(int src_floor, int buildingID) {
        ///     Log.d("chen7", "start calculate building " + Integer.toString(buildingID));
        ArrayList<Location> roomsinthebuilding = findAll(src_floor, Integer.toString(buildingID));
        ArrayList<Location> staircasesinthebuilding = findAllStaircase(src_floor, Integer.toString(buildingID));
        if (staircasesinthebuilding.size() == 0) {
      //      locations_room.clear();
        //    locations_stair.clear();
       //     return;
            //   Log.d("Chen3", "using first floor stair " );
            staircasesinthebuilding = findAllStaircase2(src_floor, Integer.toString(buildingID));
        }
        //     Log.d("Chen3", "room size " + roomsinthebuilding.size() + "staicase size  " + staircasesinthebuilding.size());

        for (int i = 0; i < roomsinthebuilding.size(); i++) {
            double mincost = 99999;
            int minid = 0;
            for (int k = 0; k < staircasesinthebuilding.size(); k++) {

                String[] Queryresultground = queryPathbylatlon(
                        Double.toString(roomsinthebuilding.get(i).getLongitude()),
                        Double.toString(roomsinthebuilding.get(i).getLatitude()),
                        Double.toString(staircasesinthebuilding.get(k).getLongitude()),
                        Double.toString(staircasesinthebuilding.get(k).getLatitude())
                        , src_floor);
                double cost = Double.parseDouble(Queryresultground[1]);
                if (cost != 0 && cost < mincost) {
                    mincost = cost;
                    minid = k;
                }
            }

            String query = "update roomsnew set nearestStaircase=\"" + locations_stair.get(minid) + "\"" + " where location=\"" + locations_room.get(i) + "\" AND floor = "+Integer.toString(src_floor);
            //     Log.d("chen", "bid "+ buildingID+ query );

            try {
                Stmt stmt = db.prepare(query);
                if (stmt.step()) {
                }
                stmt.close();
            } catch (Exception e) {
                Log.d("exception", e.toString());
                Log.d("exception", query);
            }
            ///      Log.d("buildgraph: ", Integer.toString(i) + " buildid" + Integer.toString(buildingID));
        }
        locations_room.clear();
        locations_stair.clear();
    }

    public void setNearestElevator(int src_floor, int buildingID) {

            String query = "SELECT outid FROM roomsnew WHERE utility == \"nope\"" ;
            //     Log.d("chen", "bid "+ buildingID+ query );
            String name = "";
            try {
                Stmt stmt = db.prepare(query);
                while (stmt.step()) {
                    name = stmt.column_string(0);
                    Log.d ("Found thing", name);
                }

                stmt.close();
                //Log.d ("Found thing", name);
            } catch (Exception e) {
                Log.d("exception", e.toString());
                Log.d("exception", query);
            }
            ///      Log.d("buildgraph: ", Integer.toString(i) + " buildid" + Integer.toString(buildingID));
    }




    public ArrayList<Location> findAll(int floor, String buildId) {
        String query = "SELECT location,rid FROM \"roomsnew\" WHERE outid = " +
                buildId + " AND location!=\'\\N\' AND floor = "+Integer.toString(floor);
      //   Log.d("Chen", query);
        String location = null;
        ArrayList<Location> locations = new ArrayList<Location>();
        int i = 0;
        try {
            Stmt stmt = db.prepare(query);
            while (stmt.step()) {
                location = stmt.column_string(0);
                locations_room.add(location);
                ///   Log.d("Chen6", "rid " + stmt.column_string(1) + "   " + i);
                i++;
                location = location.substring(6);
                String[] latlon = location.split(" ");

                Location roomLocation = new Location("");
                roomLocation.setLongitude(Double.parseDouble(latlon[0]));
                roomLocation.setLatitude(Double.parseDouble(latlon[1].substring(0, latlon[1].length() - 1)));
                locations.add(roomLocation);
            }
            stmt.close();
        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return locations;
    }

}
