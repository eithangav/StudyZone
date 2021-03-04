const { Decimal128 } = require('bson');
const mongoose = require('mongoose');

const ZoneSchema = new mongoose.Schema({
    _id: 
    {
        type: Number,
        required: true,
    },
    latitude: {
        type: Number,   
        required: true
    },
    longitude: {
        type: Number,
        required: true
    },
    name: {
        type: String,
        required: true
    },
    description:{
        type: String,
        required: false
    },
    crowdedRating: {
        type: Number,
        required: false
    },
    foodRating: {
        type: Number,
        required: false
    },
    priceRating: {
        type: Number,
        required: false
    },
    numRatings: {
        type: Number,
        required: false
    },
    reviews: [{content: String}]
});

const Zone = mongoose.model('Zone', ZoneSchema);

module.exports = Zone;