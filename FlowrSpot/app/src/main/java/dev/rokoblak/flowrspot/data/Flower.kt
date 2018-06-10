package dev.rokoblak.flowrspot.data


/**
    "id": 7,
    "name": "Alpski volcin",
    "latin_name": "Daphne alpina",
    "sightings": 0,
    "profile_picture": "//flowrspot.s3.amazonaws.com/flowers/profile_pictures/000/000/007/medium/L_00010.jpg?1527514226",
    "favorite": false
 */

data class Flower(
        val id: Long,
        val name: String,
        val latin_name: String,
        val sightings: Int,
        val profile_picture: String,
        var favorite: Boolean
)