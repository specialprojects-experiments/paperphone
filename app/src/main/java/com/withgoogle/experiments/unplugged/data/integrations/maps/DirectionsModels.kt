package com.withgoogle.experiments.unplugged.data.integrations.maps

data class Result(val routes: List<Route>)

data class Route(val overview_polyline: Polyline)

data class Polyline(val points: String)