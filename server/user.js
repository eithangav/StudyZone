const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
    email: {
        type: String,
        required: true
    },
    password: {
        type: String,
        required: true,

    },
    token: {
        type: String,
        required: false
    },
    latitude: {
        type: Number,
        required: false
    },
    longitude: {
        type: Number,
        required: false
    }
});

UserSchema.pre('save', function (next) {
    var self = this;
    User.find({email : self.email}, function (err, docs) {
        if (!docs.length){
            next();
        }else{                
            console.log('user exists: ', self.username);
            next(new Error("User exists!"));
        }
    });
}) ;

const User = mongoose.model('User', UserSchema);

module.exports = User;