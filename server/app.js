'use strict'

// imports
const express = require('express');
const session = require('express-session');
const mongoose = require('mongoose');
const FCM = require('fcm-push');
const bcrypt = require('bcrypt');
const bodyParser = require('body-parser');
const User = require('./user');
const Zone = require('./zone');

//globals
const MONGO_URL = "mongodb://localhost:27017/";
const DB_NAME =  "FinalProjectDB";
const FCM_SERVER_KEY = "";

//init FCM
//let fcm = new FCM(FCM_SERVER_KEY);

//init DB
mongoose.connect(MONGO_URL + DB_NAME, {
    useNewUrlParser: true,
    useUnifiedTopology: true
});

// init app
let app = express();
app.use(bodyParser.json());
app.use(session({
    secret: "shhhh",
    resave: false,
    saveUninitialized: true
}));


/**
 * Sends a notification to give token about checkin of a user to a zone
 * @param {string} token 
 * @param {string} zone 
 */
const send_notification = (token, zone) => {
    fcm.send({
        to: token,
        data: {},
        notification: {
            title: "A user checked in nearby!",
            body: "Go check " + zone + " to study toghether!"
        }
    }, (error, response) => {
        if(error){
            console.log(error);
        }
    });
    
}

/**
 * Express route for user login.
 * Body parameters: 
 * email (string), 
 * password (string), 
 * token (string),
 * longitude (number),
 * latitude (number)
 */
app.post('/login', (req, res) => {
    // verify email is not empty
    if(!req.body.email || req.body.email == ""){
        return res.status(500).json({emailExists: false, passwordMatches: false});
    }
    // find if input email exists
    User.findOne({email: req.body.email}, (err, user) => {
        if(err) {
            console.log(err);
            return res.status(500).json({emailExists: false, passwordMatches: false, error: err});
        }
        console.log(user);
        if(!user){
            return res.status(200).json({emailExists: false, passwordMatches: false});
        }
        // in case user is found hash input password and compare
        bcrypt.compare(req.body.password, user.password, (err, result) => {
            if(err){
                console.log(err);
                return res.status(500).json({emailExists: true, passwordMatches: false, error: err});
            }
            if(result === false){
                return res.status(200).json({emailExists: true, passwordMatches: false});
            }
            console.log(req.body.token);
            if(req.body.token == undefined || req.body.longitude == undefined || req.body.latitude == undefined){
                return res.status(500).json({emailExists: true, passwordMatches: false, error: true});
            }
            // if password matches and all parameters are filled update token and location
            User.findOneAndUpdate({email: req.body.email}, 
                {token: req.body.token, 
                latitude: req.body.latitude, 
                longitude: req.body.longitude},
                (err, docs) => {
                    if(err){
                        console.log(err);
                        return res.status(500).json({emailExists: true, passwordMatches: true, error:true});
                    }
                    else{
                        console.log(docs);
                    }
                }
            );
            return res.status(200).json({emailExists: true, passwordMatches: true});
        });
    });
});

/**
 * Express route for user registration.
 * Body parameters: 
 * email (string), 
 * password (string), 
 */
app.post('/register', (req, res) => {
    // hash the input password syncronously
    let hashed_pass = bcrypt.hashSync(req.body.password, 10);
    const user = new User({
        email: req.body.email,
        password: hashed_pass
    });
    user.save()
    .then(result => {
        console.log(result);
    })
    .catch(err => {
        res.status(500).json({status: "Fail"});
    });
    res.status(200).json({status: "Success"});
});

/**
 * Express route for getting list of all zones.
 * Responds with a message containing list of all zones, as a json
 * object that contains zone id, zone name, and zone location (lat + lang)
 * No parameters are needed for this request
 */
app.get('/zones', (req, res) => {
    let found_zones = []
    Zone.find((err, zones) => {
        zones.forEach(zone => {
            found_zones.push({_id: zone._id, 
                latitude: zone.latitude, 
                longitude: zone.longitude,
                name: zone.name});
        })
        res.json({"markers": found_zones});
    });
});

/**
 * Express route for posting new zone
 * Body parameters: 
 * _id (string), 
 * latitude (string), 
 * longitude (number),
 * latitude (number),
 * name (string),
 * description (string)
 */
app.post('/zones', (req, res) => {
    console.log(req.body._id);
    const zone = new Zone({
        _id: req.body._id,
        latitude: req.body.latitude,
        longitude: req.body.longitude,
        name: req.body.name,
        description: req.body.description,
        crowdedRating: 0,
        foodRating: 0,
        priceRating: 0,
        numRatings: 0,
        reviews: []
    });
    zone.save();
    res.json({success: true});
    
});

/**
 * Express route for getting specifig details of certain zone
 * Request parameters:
 * id (number)
 * Body parameters: 
 * None
 */
app.get('/zone/:id', (req, res) => {
    Zone.findOne({_id: req.params.id}, (err, zone) => {
        if(err){
            res.status(500).json({status: "fail"});
        }
        res.status(200).json(zone);
    });
})

/**
 * Express route for posting a review for a zone
 * Request parameters:
 * id (number) (id for zone)
 * Body parameters: 
 * foodRating (number), 
 * crowdedRating (number), 
 * priceRating (number),
 * review (string)
 */
app.post('/zone/:id', (req, res) => {
    if(req.body.foodRating < 0 || req.body.foodRating > 5){
        res.status(500).json({status: "Fail"});
    }
    if(req.body.crowdedRating < 0 || req.body.crowdedRating > 5){
        res.status(500).json({status: "Fail"});
    }
    if(req.body.priceRating < 0 || req.body.priceRating > 5){
        res.status(500).json({status: "Fail"});
    }
    Zone.findOne({_id: req.params.id}, (err, zone) => {
        if(err){
            res.status(500).json({status: "Fail"});
        }
        //calculation of running average
        zone.foodRating = ((zone.foodRating * zone.numRatings) + req.body.foodRating)/(zone.numRatings + 1);
        zone.crowdedRating = ((zone.crowdedRating * zone.numRatings) + req.body.crowdedRating)/(zone.numRatings + 1);
        zone.priceRating = ((zone.priceRating * zone.numRatings) + req.body.priceRating)/(zone.numRatings + 1);
        if(req.body.review && req.body.review != ""){
            zone.reviews.push({"content": req.body.review});
        }
        zone.numRatings += 1;
        zone.save();
        res.status(200).json({status: "Success"});
    });
});


/**
 * Express route for user checkin - notifying all users in the area that
 * a user checked in a close zone (except of the user checking in)
 * Body parameters: 
 * id (number) (for zone)
 * email (string)
 */
app.post('/checkin', async (req, res) => {
    if(!req.body.id){
        res.status(500).json({status: "Fail"});
    }
    const email = req.body.email;
    // find zone syncronously and verify it is a valid zone
    const zone = await Zone.findOne({_id: req.body.id});
    console.log(zone);
    if(!zone){
        res.status(500).json({status: "Fail"});
    }
    const latitude = zone.latitude;
    const longitude = zone.longitude;
    // find all users that were last seen in the zone's area
    User.find(
        {email: {$ne: email}, 
        longitude: {$lte: longitude + 1, $gte: longitude - 1},
        latitude: {$lte: latitude + 1, $gte: latitude - 1}},
        (err, users) => {
            if(err){
                console.log(err);
                res.json({success: false});
            }
            users.forEach(user => {
                if(user.token != undefined){
                    console.log(user.token, zone.name);
                    //send_notification(user.token, zone.name);
                }
            });
        });
        res.status(200).json({status: "Success"});
});

app.listen(3000, () => {
    console.log("Listening on port 3000");
});