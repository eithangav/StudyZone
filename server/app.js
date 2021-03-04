'use strict'

// imports
const express = require('express');
const session = require('express-session');
const mongoose = require('mongoose');
const passport = require('passport');
const localStrategy = require('passport-local').Strategy;
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

// set passport.js
app.use(passport.initialize());
app.use(passport.session());

passport.serializeUser((user, done) => {
    done(null, user.id);
});
passport.deserializeUser((id, done) => {
    User.findById(id, (err, user) => {
        done(err, user);
    })
});

passport.use(new localStrategy((username, password, done) => {
    
}))

const isLoggedIn = (req, res, next) => {
    if(req.isAuthenticated()) return next;
}


app.post('/login', (req, res) => {
    if(!req.body.email){
        res.json({emailExists: false, passwordMatches: false});
    }
    User.findOne({email: req.body.email}, (err, user) => {
        if(err) {
            return res.json({message: err});
        }
        console.log(user);
        if(!user){
            return res.json({emailExists: false, passwordMatches: false});
        }   
        bcrypt.compare(req.body.password, user.password, (err, result) => {
            if(err){
                console.log(err);
                return res.json({message: "123"});
            }
            if(result === false){
                return res.json({emailExists: true, passwordMatches: false});
            }
            if(req.body.token && req.body.longitude && req.body.latitude){
                User.findOneAndUpdate({email: req.body.email}, 
                    {token: req.body.token, 
                    latitude: req.body.latitude, 
                    longitude: req.body.longitude},
                    (err, docs) => {
                        if(err){
                            console.log(err);
                        }
                        else{
                            console.log(docs);
                        }
                    });
            }
            
            return res.json({emailExists: true, passwordMatches: true});
        });
    });
});

app.post('/register', (req, res) => {
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
        console.log(err);
    });
    res.status(200).json({
        status: "Success"
    });
});

app.get('/zones', (req, res) => {
    let found_zones = []
    Zone.find((err, zones) => {
        zones.forEach(zone => {
            found_zones.push({_id: zone._id, 
                latitude: zone.latitude, 
                longitude: zone.longitude,
                name: zone.name});
        })
        res.json(found_zones);
    });
});

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

app.get('/zone/:id', (req, res) => {
    Zone.findOne({_id: req.params.id}, (err, zone) => {
        if(err){
            res.json({status: "fail"});
        }
        res.json(zone);
    });
})

app.post('/zone/:id', (req, res) => {
    Zone.findOne({_id: req.params.id}, (err, zone) => {
        if(err){
            res.json({status: "fail"});
        }
        //calculation of running average
        zone.foodRating = ((zone.foodRating * zone.numRatings) + req.body.foodRating)/(zone.numRatings + 1);
        zone.crowdedRating = ((zone.crowdedRating * zone.numRatings) + req.body.crowdedRating)/(zone.numRatings + 1);
        zone.priceRating = ((zone.priceRating * zone.numRatings) + req.body.priceRating)/(zone.numRatings + 1);
        zone.reviews.push({"content": req.body.review});
        zone.numRatings += 1;
        console.log(zone.foodRating);
        console.log(zone.numRatings);
        zone.save();
        res.json({success: true});
    });
});

app.post('/checkin', (req, res) => {
    
});

app.listen(3000, () => {
    console.log("Listening on port 3000");
});