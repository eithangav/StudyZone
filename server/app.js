// imports
const express = require('express');
const session = require('express-session');
const mongoose = require('mongoose');
const passport = require('passport');
const localStrategy = require('passport-local').Strategy;
const bcrypt = require('bcrypt');
const bodyParser = require('body-parser');

//globals
const MONGO_URL = "mongodb://localhost:27017/";
const DB_NAME =  "FinalProjectDB";

//init DB
mongoose.connect(MONGO_URL + DB_NAME, {
    useNewUrlParser: true,
    useUnifiedTopology: true
});

const UserSchema = new mongoose.Schema({
    username: {
        type: String,
        required: true
    },
    password: {
        type: String,
        required: true,

    }
});

const User = mongoose.model('User', UserSchema);

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

passport.use(new localStrategy((user, password, done) => {
    User.findOne({username: username}, (err, user) => {
        if(err) {return done(err);}
        if(!user){
            return done(null, false, {message: 'Incorrect username.'});
        }
        bcrypt.compare(password, user.password, (err, res) => {
            if(err){
                return done(err);
            }
            if(res === false){
                return done(null, false, {message: 'Incorrect password.'})
            }
            return done(null, user);
        });
    });
}))

const isLoggedIn = (req, res, next) => {
    if(req.isAuthenticated()) return next;
}
app.post('/login', (req, res, next) => {
    passport.authenticate('local', (err, user, info) => {
        if(err){
            return next(err);
        }
        if(!user){
            res.json({auth: false})
        }

    })
});

app.post('/register', (req, res) => {
    const user = new User({
        username: req.body.username,
        password: req.body.password
    });
    user.save()
    .then(result => {
        console.log(result);
    })
    .catch(err => {
        console.log(err);
    });
    res.status(201).json({
        message: "try",
        createdUser: user
    });
});

app.listen(3000, () => {
    console.log("Listening on port 3000");
});