package com.example.weatherapp

//data class WeatherResponse(
//    val name: String,
//    val main: Main,
//    val weather: List<Weather>,
//    val wind: Wind,
//    val sys: Sys
//)

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Clouds(
    val all: Int
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double
)

data class Weather(
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double
)

data class Sys(
    val sunrise: Long,
    val sunset: Long
)