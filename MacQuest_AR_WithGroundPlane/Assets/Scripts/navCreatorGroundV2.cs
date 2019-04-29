﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Mapbox.Unity.Utilities;
using Mapbox.Utils;
using Vuforia;



/*
 1{"type":"FeatureCollection","features":[{"type":"Feature","properties":{},"geometry":{"type":"LineString","coordinates": 
*/

//{'coordinates':[[-79.92083021864,43.26039838375999]]}"

//getNavInfo(string str);
//Switch to show or hide the route.
//showRoute(string strRoot);
//Set the user current location.
//setUserLocationNav(string str);
//Clean every thing.
//cleanLocation(string strClean);



public class navCreatorGroundV2 : MonoBehaviour
{

    string testUserLocationString = "{'longitude':-79.92064903188, 'latitude':43.25898043040999}";

    string testUserLocationString2 = "{'longitude':-79.92074903188, 'latitude':43.25897043040999}";
    //-79.92065278633999,43.25903028925999

    //-79.921029, 43.258749

    //string test2 = "{'longitude':-79.92065278633999, 'latitude':43.25903028925999}";

    string testString2 = "{ 'coordinates':[[-79.92064222538,43.25884549791999],[-79.92064279005999,43.25885548197],[-79.92064335474998,43.25886546600999],[-79.92064391943,43.25887545006],[-79.92064448412,43.2588854341],[-79.92064454177,43.25888683794],[-79.92064495211,43.25889682951999],[-79.92064536245,43.25890682109999],[-79.92064061525,43.25890683959999],[-79.92063061532998,43.25890687858],[-79.92062061539998,43.25890691755],[-79.92061061548,43.25890695652999],[-79.92060061554999,43.25890699551],[-79.92059061563,43.25890703448],[-79.92058061571,43.25890707345999],[-79.92057061578,43.25890711242999],[-79.92056061586,43.25890715140999],[-79.92055061592998,43.25890719039],[-79.92054061601,43.25890722936],[-79.92053061609,43.25890726834],[-79.92052061616,43.25890730730999],[-79.92051061624,43.25890734629],[-79.92050061631,43.25890738526],[-79.92049061638998,43.25890742423999],[-79.92048061646999,43.25890746321999],[-79.92047061654,43.25890750219],[-79.92046061662,43.25890754116999],[-79.92045061669,43.25890758013999],[-79.92044061677,43.25890761912],[-79.92043061803,43.25890746016],[-79.9204206193,43.25890730121],[-79.92041062056,43.25890714225],[-79.92040062182,43.2589069833],[-79.92039062308999,43.25890682434],[-79.92038062435,43.25890666539],[-79.92037062561,43.25890650643],[-79.92036062687998,43.25890634748],[-79.92035062813999,43.25890618852],[-79.9203406294,43.25890602957],[-79.92033063067,43.25890587061],[-79.92032063193,43.25890571166],[-79.92031063318998,43.25890555269999]]}";


    //string testString = "{'coordinates':[[-79.92064757845,43.25895699793999],[-79.92064801966,43.2589669882],[-79.92064828098999,43.25897045865],[-79.92064903188,43.25898043040999],[-79.92064978276999,43.25899040217999],[-79.92065053365999,43.25900037394999],[-79.92065128455,43.25901034572],[-79.92065203545,43.25902031749],[-79.92065278633999,43.25903028925999],[-79.92065353722999,43.25904026102],[-79.92065428812,43.25905023279],[-79.92065503901,43.25906020456],[-79.92066242497999,43.25905988881999],[-79.92067241585999,43.25905946170999],[-79.92068240672999,43.25905903460999],[-79.92069239760999,43.25905860750999],[-79.92070238847999,43.25905818040999],[-79.92071237935999,43.25905775329999],[-79.92072237022999,43.25905732619999],[-79.92073236110999,43.25905689909999],[-79.92073546304998,43.25905684151999],[-79.92074546132999,43.25905665590999],[-79.92075545961,43.25905647031],[-79.92076545787999,43.2590562847],[-79.92077545616,43.2590560991],[-79.92077590447,43.25906131666999],[-79.92077676053999,43.25907127995999],[-79.92077761660999,43.25908124325],[-79.92077847267999,43.25909120653999],[-79.92077932875,43.25910116982999],[-79.92078018482,43.25911113312],[-79.92078104089,43.25912109641],[-79.92078189695999,43.2591310597],[-79.92078275302999,43.25914102299],[-79.92078360910999,43.25915098628],[-79.92078446518,43.25916094956999],[-79.92078532124999,43.25917091285999],[-79.92078617732,43.25918087614999],[-79.92078703338999,43.25919083944],[-79.92078788946,43.25920080272999],[-79.92077789041999,43.25920094127],[-79.92076789137999,43.25920107982],[-79.92075789234,43.25920121835999],[-79.9207478933,43.25920135689999],[-79.92073789425999,43.25920149545],[-79.92072789522,43.25920163399],[-79.92071789617999,43.25920177253999],[-79.92070789714,43.25920191107999],[-79.9206978981,43.25920204962],[-79.92068789905999,43.25920218817],[-79.92067790001999,43.25920232671],[-79.92066790098,43.25920246524999],[-79.92065790194,43.2592026038],[-79.92064790289999,43.25920274233999],[-79.92063790386,43.25920288087999],[-79.92062790481999,43.25920301942999],[-79.92061790578,43.25920315797],[-79.92060790674,43.25920329651999],[-79.92059790769999,43.25920343506],[-79.92058790865999,43.25920357359999],[-79.92057790961998,43.25920371215],[-79.92056791057,43.25920385068999],[-79.92055791152999,43.25920398923],[-79.92054791248999,43.25920412778],[-79.92054269999999,43.2592042],[-79.92054331235,43.25921418123],[-79.92054392468999,43.25922416246999],[-79.92054453704,43.2592341437],[-79.92054514938,43.25924412493999],[-79.92054576172999,43.25925410616999],[-79.92054637406999,43.2592640874],[-79.92054698642,43.25927406864],[-79.92054759876999,43.25928404986999],[-79.92054821111,43.25929403111],[-79.92054882345999,43.25930401234],[-79.92054943579999,43.25931399356999],[-79.92055004815,43.25932397481],[-79.92055066048999,43.25933395604],[-79.92055127283999,43.25934393727999],[-79.92055188518,43.25935391850999],[-79.92055249752999,43.25936389974],[-79.92055310988,43.25937388097999],[-79.92055372222,43.25938386220999],[-79.92055433456999,43.25939384345],[-79.92055494690999,43.25940382467999],[-79.92055555925999,43.25941380590999],[-79.9205561716,43.25942378715],[-79.92055678395,43.25943376837999],[-79.92055739629999,43.25944374961999],[-79.92055800863999,43.25945373085],[-79.92055862098998,43.25946371207999],[-79.92055923332999,43.25947369331999],[-79.92055984568,43.25948367455],[-79.92056045802,43.25949365579],[-79.92056107036999,43.25950363702],[-79.92056168272,43.25951361825],[-79.92056229505999,43.25952359949],[-79.92056290740999,43.25953358072],[-79.92056351975,43.25954356196],[-79.92056413209999,43.25955354319],[-79.92056469999999,43.25956279999999],[-79.92055707266,43.25956926712],[-79.92054944532,43.25957573424999],[-79.92054181797999,43.25958220137],[-79.92053419063999,43.25958866849999],[-79.9205265633,43.25959513562],[-79.92051893596,43.25960160275],[-79.92051130862999,43.25960806987],[-79.92050368129,43.25961453698999],[-79.92049605395,43.25962100412],[-79.92049369999999,43.25962299999999],[-79.92048604263999,43.25962943154999],[-79.92047838528,43.2596358631],[-79.92047072792,43.25964229464999],[-79.92046307056,43.25964872621],[-79.9204554132,43.25965515775999],[-79.92044775585,43.25966158931],[-79.92044009849,43.25966802085999],[-79.92043244113,43.25967445240999],[-79.92042478376999,43.25968088396],[-79.92041712640999,43.25968731550999],[-79.92041521180999,43.25968892361999],[-79.92040755444,43.25969535516],[-79.92039989708,43.25970178671],[-79.92039223971,43.25970821824999],[-79.92038458234,43.25971464979],[-79.92037692498,43.25972108132999],[-79.92036926761,43.25972751287999],[-79.92036161025,43.25973394442],[-79.92035395288,43.25974037595999],[-79.92034629551,43.25974680749999],[-79.92033863815,43.25975323905],[-79.92033098077999,43.25975967058999],[-79.92032332341,43.25976610212999],[-79.92031566605,43.25977253367],[-79.92030800867999,43.25977896521999],[-79.92030035131999,43.25978539675999],[-79.92029269395,43.2597918283],[-79.92028503658,43.25979825984999],[-79.92027737921999,43.25980469139],[-79.92026972185,43.25981112293],[-79.92026206448,43.25981755446999],[-79.92025440712,43.25982398602],[-79.92024674975001,43.25983041756],[-79.92023909238,43.25983684909999],[-79.92023143502,43.25984328064],[-79.92022377764999,43.25984971218999],[-79.92021612028999,43.25985614372999],[-79.92020846292,43.25986257527],[-79.92020080554999,43.25986900681],[-79.92019314818999,43.25987543835999],[-79.92018549082,43.2598818699],[-79.92017783345,43.25988830144],[-79.92017017608999,43.25989473298999],[-79.92016251872,43.25990116453],[-79.92015949999999,43.2599037],[-79.92015786977999,43.25991356621999],[-79.92015623956,43.25992343244999],[-79.92015460934,43.25993329866999],[-79.92015297912,43.2599431649],[-79.9201513489,43.25995303112],[-79.92014989999999,43.25996179999999],[-79.92015017705999,43.25997179616],[-79.92015045410999,43.25998179232],[-79.92015073116999,43.25999178847999],[-79.92015100821999,43.26000178464999],[-79.92015128528,43.26001178080999],[-79.92015156233,43.26002177697],[-79.92015183939,43.26003177312999],[-79.92015211643999,43.26004176928999],[-79.9201523935,43.26005176545],[-79.9201524,43.26005199999999],[-79.92015290045999,43.26006198747],[-79.92015340092,43.26007197493999],[-79.92015390138,43.26008196241],[-79.92015440184999,43.26009194987999],[-79.92015490231,43.26010193734999],[-79.92015540277,43.26011192480999],[-79.92015590322999,43.26012191228],[-79.92015640368999,43.26013189974999],[-79.92015690415,43.26014188722],[-79.920157,43.2601438],[-79.92015760268,43.26015378182],[-79.92015820535,43.26016376364999],[-79.92015859999999,43.2601703],[-79.92016348171,43.26017009332999],[-79.92017347275999,43.26016967035999],[-79.92018346381,43.26016924738],[-79.92019345485999,43.26016882441],[-79.92020344591,43.26016840142999],[-79.92021343695999,43.26016797846],[-79.92022342801,43.26016755549],[-79.92023341906,43.26016713250999],[-79.92024341011,43.26016670953999],[-79.92025340115999,43.26016628656],[-79.92026339221999,43.26016586358999],[-79.92027338327,43.26016544060999],[-79.92028337431999,43.26016501764],[-79.92029336536999,43.26016459467],[-79.92030335642001,43.26016417168999],[-79.92031334746999,43.26016374871999],[-79.92032333852,43.26016332574],[-79.92033332956999,43.26016290276999],[-79.92034332062,43.26016247979],[-79.92035331167,43.26016205682],[-79.92036330272,43.26016163383999],[-79.92037329377,43.26016121087],[-79.92038328482,43.26016078789999],[-79.92039327586999,43.26016036491999],[-79.92040326692,43.26015994195],[-79.92041325796999,43.26015951897],[-79.92042324902999,43.260159096],[-79.92043324007999,43.26015867301999],[-79.92044323112999,43.26015825004999],[-79.92045322217999,43.26015782707999],[-79.92046321322999,43.26015740409999],[-79.92047320428,43.26015698113],[-79.92048319532999,43.26015655815],[-79.92049318638,43.26015613517999],[-79.92050317743,43.2601557122],[-79.92051316848,43.26015528922999],[-79.92052315953,43.26015486625999],[-79.92053315057999,43.26015444328],[-79.92054314163,43.26015402030999],[-79.92055313267999,43.26015359733],[-79.92056312373,43.26015317436],[-79.92057311477999,43.26015275137999],[-79.92058310583999,43.26015232840999],[-79.92059309689,43.26015190544],[-79.92060308793999,43.26015148246],[-79.92061307899,43.26015105948999],[-79.92062307003999,43.26015063650999],[-79.92063306109,43.26015021354],[-79.92064305213999,43.26014979055999],[-79.92065304319,43.26014936758999],[-79.92066303423999,43.26014894461],[-79.92067302528999,43.26014852163999],[-79.92068301634001,43.26014809866999],[-79.92069300738999,43.26014767569],[-79.92070299844,43.26014725272],[-79.92071298948999,43.26014682973999],[-79.92072298054,43.26014640677],[-79.92073297159,43.26014598378999],[-79.92074296264999,43.26014556081999],[-79.9207529537,43.26014513785],[-79.92076294474999,43.26014471487],[-79.9207729358,43.26014429189999],[-79.92078292684999,43.26014386892],[-79.9207929179,43.26014344595],[-79.92080290894999,43.26014302296999],[-79.92081289999999,43.2601426],[-79.92081371207,43.26015256696999],[-79.92081452415,43.26016253394],[-79.92081533622,43.26017250091999],[-79.9208161483,43.26018246788999],[-79.92081696037,43.26019243485999],[-79.92081777244999,43.26020240183],[-79.92081858452,43.26021236881],[-79.92081939659999,43.26022233577999],[-79.92082020866999,43.26023230275],[-79.92082102073998,43.26024226972],[-79.92082183281999,43.26025223669],[-79.92082264488999,43.26026220366999],[-79.92082345696999,43.26027217064],[-79.92082370854999,43.26027525842],[-79.9208236817,43.26028525837999],[-79.92082365484999,43.26029525834999],[-79.92082364588,43.2602986],[-79.92082430316,43.26030857838],[-79.92082496042999,43.26031855674999],[-79.92082561770999,43.26032853513],[-79.92082627498,43.2603385135],[-79.92082693226,43.26034849187999],[-79.92082758953999,43.26035847026],[-79.92082824681,43.26036844862999],[-79.92082890408999,43.26037842701],[-79.92082956135999,43.26038840537999],[-79.92083021864,43.26039838375999]]}";

    string testRoute = "{ 'coordinates':[[-79.92068669931,43.25874386324],[-79.92067670632,43.25874423748],[-79.92066671332,43.25874461171999],[-79.92065672033,43.25874498595999],[-79.92064672733,43.2587453602],[-79.92063673434,43.25874573443999],[-79.92063685291998,43.25874802093999],[-79.92063737083998,43.25875800751999],[-79.92063788875998,43.25876799409999],[-79.92063840668,43.25877798068],[-79.92063883725999,43.25878559366],[-79.92063940194998,43.25879557771],[-79.92063996663,43.25880556175],[-79.92064053132,43.25881554579],[-79.92064109599998,43.25882552983999],[-79.92064166069,43.25883551388],[-79.92064222538,43.25884549791999],[-79.92064279005999,43.25885548197],[-79.92064335474998,43.25886546600999],[-79.92064391943,43.25887545006],[-79.92064448412,43.2588854341],[-79.92064454177,43.25888683794],[-79.92064495211,43.25889682951999],[-79.92064536245,43.25890682109999],[-79.92064537241,43.25890704663],[-79.92064581361998,43.25891703688999],[-79.92064625483,43.25892702715],[-79.92064669604,43.25893701741],[-79.92064713724,43.25894700767999],[-79.92064757845,43.25895699793999],[-79.92064801966,43.2589669882],[-79.92064828099,43.25897045865],[-79.92064903188,43.25898043040999],[-79.92064978276998,43.25899040217999],[-79.92065053366,43.25900037394999],[-79.92065128455,43.25901034572],[-79.92065203545,43.25902031749],[-79.92065278633999,43.25903028925999],[-79.92065353723,43.25904026102],[-79.92065428812,43.25905023279],[-79.92065503901,43.25906020456],[-79.9206559095,43.25907016659999],[-79.92065678,43.25908012863999],[-79.92065765049,43.25909009067999],[-79.92065774102,43.25909112674],[-79.92064774954999,43.25909153959],[-79.92063775806999,43.25909195242999],[-79.9206277666,43.25909236528],[-79.92061777512,43.25909277812999],[-79.92060778364998,43.25909319096999],[-79.92059779216999,43.25909360382],[-79.9205878007,43.25909401666999],[-79.92057780923,43.25909442950999],[-79.920575393,43.25909452934999],[-79.920575116,43.25908453319],[-79.92057483899,43.25907453701999],[-79.92057456199,43.25906454086],[-79.92057428499,43.25905454469999],[-79.92057400798,43.25904454853999]]}";

    string testRoute2 = "{ 'coordinates':[[-79.92068669931,43.25874386324],[-79.92067670632,43.25874423748]]}";

    string testLoc = "{ 'longitude':-79.9207049, 'latitude':43.2587665}";

    public routeCoordinates info;

    public List<NavObject> routeList;


    public static bool isShowRoute = false;

    public static Vector2d userCurrentLocation;

    public delegate void moveLocation();

    public event moveLocation onMoveLocation;

    bool firstDraw = true;


    GameObject directionsArrow;

    GameObject destinationMarker;

    GameObject groundPlane;

    GameObject planeFinder;

    double distTravel = 0;

    bool firstRecal = true;

    float cameraYCurrent;

    float cameraYPrevious;

    float trueRouteHeading = 0;

    HeadingInfo trueHeading;

    toolsLib toolsLib;


    bool isRecalbrationSuccessful = true;



    public void getNavInfo(string str)
    {

        info = Mapbox.Json.JsonConvert.DeserializeObject<routeCoordinates>(str);
        //For clean the debug info
        //Debug.Log("NavInfo Called: ");
    }

    public void showRoute(string strBool)
    {

        isShowRoute = bool.Parse(strBool.Trim());
        //For clean the debug info
        //Debug.Log("Route enabled");



    }


    //Function to get user current location.
    public void setUserLocationNav(string str)
    {
        var prevLocation = userCurrentLocation;
        UserLocationObject userLocationObj = Mapbox.Json.JsonConvert.DeserializeObject<UserLocationObject>(str);
        setUserCurrentLocation(userLocationObj.latitude, userLocationObj.longitude);

        if (firstDraw)
        {
            drawPoints();
            firstDraw = false;
        }
        else{
            distTravel += (userCurrentLocation - prevLocation).sqrMagnitude;
            //For clean the debug info
            //Debug.Log("DistTravel: " + distTravel);
        }


        if (distTravel >= 10){
            

            isRecalbrationSuccessful = reCalibratePoints();

            if(isRecalbrationSuccessful == true){
                distTravel = 0;
            }
            else{
                distTravel = 5;
            }

        }


        onMoveLocation();

    }

    //Function to get user current location.
    public void setUserNavOrigin(string str)
    {
        UserLocationObject userLocationObj = Mapbox.Json.JsonConvert.DeserializeObject<UserLocationObject>(str);
        setUserCurrentLocation(userLocationObj.latitude, userLocationObj.longitude);

    }



    //Set user current planer coordinate.
    public void setUserCurrentLocation(double lat, double lng)
    {
        userCurrentLocation = Conversions.GeoToWorldPosition(lat, lng, new Vector2d(0f, 0f));
    }

    //Get label relative location (Compare with userCurrentLocation).
    public Vector2d calcLabelRelativeLocation(double lat, double lng)
    {
        return Conversions.GeoToWorldPosition(lat, lng, userCurrentLocation);
    }

    public void centreGroundPlane()
    {
        groundPlane.transform.position = new Vector3(0, 0, 0);
    }

    public void drawPoints()
    {
        //Show the route.

        if (info.coordinates != null)
        {
            //Has route info.
            //Display.

            GameObject prevPoint = null;

            int endCount = 0;

            foreach (var routePoint in info.coordinates)
            {

                var point = convertDoubleListToVector2d(routePoint);


                var tempLocation = point - userCurrentLocation;


                var distance = (tempLocation).sqrMagnitude;

                //Debug.Log("Distance: " + distance);
                //For clean the debug info
                //Debug.Log("Drawn");

                GameObject navObject;

                if (endCount == (info.coordinates.Count - 1))
                {
                    navObject = Instantiate(destinationMarker);
                }
                else
                {
                    navObject = Instantiate(directionsArrow);
                    navObject.transform.localScale = new Vector3(0.01f, 0.02f, 0.01f);
                }



                Vector3 navObjectLocation = new Vector3((float)tempLocation.x, 0.0f, (float)tempLocation.y);

                //navObjectLocation = toolsLib.getRotatedLocation(navObjectLocation, new Vector3(0, 0, 0), Vector3.up, headingControl.headingReady ? -headingControl.heading : 0);

                navObjectLocation += new Vector3(0, -0.5f, 0);
                navObject.transform.position = Vector3.Lerp(navObject.transform.position, navObjectLocation, 1.0f);

                navObject.transform.parent = groundPlane.transform;
                //For clean the debug info
                //Debug.Log("Added as child");



                navObject.SetActive(false);

                routeList.Add(new NavObject(navObject, point));

                //For clean the debug info
                //Debug.Log("Add object: " + navObject.transform.position);

                if (prevPoint != null)
                {
                    prevPoint.transform.LookAt(navObject.transform);
                    //prevPoint.transform.Rotate(0, 0, 90);

                }

                prevPoint = navObject;


                endCount++;
            }


            /*
            var headingVector = routeList[1].navLabel.transform.position - routeList[0].navLabel.transform.position;
            headingVector.y = 0;

            trueRouteHeading = Vector3.Angle(headingVector, Vector3.forward);

            groundPlane.transform.Rotate(Vector3.up, )
            */

            // toolsLib.getRotatedLocation(groundPlane.transform.position, new Vector3(0, 0, 0), Vector3.up, headingControl.headingReady ? -headingControl.heading : 0);
            // trueRouteHeading = headingControl.heading;
            trueHeading = toolsLib.updateHeadingInfo();
            groundPlane.transform.Rotate(Vector3.up, -headingControl.headingInfo.heading);
        }

    }

    IEnumerator RotateLerp(GameObject gameObject, float end, float time)
    {
        //TODO: EDIT THESE VALUES
        float i = 0.0f;
        float rate = (1 / time) * 1.0f;

        Vector3 start = gameObject.transform.position;

        while (i < 1.0f)
        {
            i += Time.deltaTime * rate;

            gameObject.transform.RotateAround(gameObject.transform.position, Vector3.up, Mathf.LerpAngle(0, end, time));
            yield return null;
        }

    }


    //Function to update NavLabel Location.
    public void updateNavLabel()
    {
        GameObject arCam = GameObject.FindWithTag("MainCamera");

        foreach (NavObject navObject in routeList)
        {
            var point = navObject.navLabel.transform.position;

            var tempLocation = point - arCam.transform.position;


            var distance = (tempLocation).sqrMagnitude;

            //Debug.Log("Distance: " + distance);

            if (distance < 200)
            {
                navObject.navLabel.SetActive(true);
                //checkDist(navObject.navLabel, distance);
            }
            else
            {
                navObject.navLabel.SetActive(false);
            }
        }
    }

    /*public void checkDist(GameObject navObject, double dist)
    {
        if (navObject != null){
            var distObj = Vector3.SqrMagnitude(navObject.transform.position - new Vector3(0f,0.5f,0f));
            //var distObj = Vuforia.CameraDevice.
            Debug.Log("Dist Obj: " + distObj);
            Debug.Log("DistObj Diff: " + Mathd.Abs(dist - distObj));

            //Debug.Log("Dist obj");
            if (Mathd.Abs(dist - distObj) >= 100)
            {
                Debug.Log("Recalc needed");
                //groundPlane.transform.Rotate(0, 10, 0);
            }
        }

    }*/

    public bool reCalibratePoints()
    {

        bool isRecalibration = false;

        trueHeading = toolsLib.updateHeadingInfo();

        if(trueHeading.isValid == true){
            GameObject route = GameObject.FindWithTag("Route");

            route.transform.position = new Vector3(0f, 0f, 0f);
            route.transform.rotation.eulerAngles.Set(0f, 0f, 0f);

            //For clean the debug info
            //Debug.Log("Route rotation (zero)" + route.transform.rotation);

            trueRouteHeading = headingControl.headingInfo.heading;

            Vector3 center = GameObject.FindWithTag("MainCamera").transform.position;

            //route.transform.RotateAround(center, Vector3.up, -trueHeading.heading);

            StartCoroutine(RotateLerp(route, -trueHeading.heading, 0.3f));

            //For clean the debug info
            //Debug.Log("Route rotation (recal)" + route.transform.rotation);

            isRecalibration = true;
        }else{
            isRecalibration = false;
        }


        return isRecalibration;

    }


    public void cleanNavObjectList()
    {

        foreach (NavObject navObject in routeList)
        {
            Destroy(navObject.navLabel);
        }

        routeList = new List<NavObject>();


    }

    public void foundGroundPlane()
    {
        //For clean the debug info
        //Debug.Log("Ground Plane Found");
        showRoute("true");
        cleanNavObjectList();
        drawPoints();
        onMoveLocation();
        groundPlaneActive();
    }


    private void Start()
    {
        init();
        //For clean the debug info
        //Debug.Log("NavCreator With Ground");

        //test();


    }

    private void OnDisable()
    {
        onMoveLocation -= updateNavLabel;
    }

    private void init()
    {

        directionsArrow = (GameObject)Resources.Load("Prefabs/directionsArrow");
        destinationMarker = (GameObject)Resources.Load("Prefabs/DestinationMarker");

        groundPlane = GameObject.FindWithTag("Route");
        planeFinder = GameObject.FindWithTag("PlaneFinder");

        userCurrentLocation = new Vector2d(0, 0);
        onMoveLocation += updateNavLabel;

        routeList = new List<NavObject>();

        toolsLib = new toolsLib();


    }


    public void cleanLocation(string strClean)
    {
        bool isClean = bool.Parse(strClean.Trim());

        if (isClean == true)
        {
            cleanNavObjectList();

        }

    }

    public void groundPlaneActive()
    {
        planeFinder.GetComponent<PlaneFinderBehaviour>().OnAutomaticHitTest = null;
        //For clean the debug info
        //Debug.Log("Ground Plane Activated");

        groundPlane.transform.parent = GameObject.FindWithTag("GroundPlane").transform;
    }

    private Vector2d convertDoubleListToVector2d(List<double> point)
    {
        return Conversions.GeoToWorldPosition(point[1], point[0], new Vector2d(0, 0));
    }


    void test()
    {
        //headingControl.heading = 345;

        headingControl.headingInfo = new HeadingInfo();
        headingControl.headingInfo.isValid = true;
        headingControl.headingInfo.heading = 104;
        distTravel = 10;

        showRoute("true");
        getNavInfo(testRoute);

        setUserLocationNav(testLoc);
    }







}
