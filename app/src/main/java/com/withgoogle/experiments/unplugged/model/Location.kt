package com.withgoogle.experiments.unplugged.model

data class Location(val latitude: Double, val longitude: Double, val address: String) {
    override fun toString(): String {
        return "$latitude,$longitude"
    }
}