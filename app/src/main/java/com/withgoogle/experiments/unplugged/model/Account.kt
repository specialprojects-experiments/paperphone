package com.withgoogle.experiments.unplugged.model

data class Account(val accountName: String, val accountType: String) {
    override fun toString(): String {
        return accountName
    }
}