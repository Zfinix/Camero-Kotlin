package com.camero.model

class DataCSV(phone: String, name: String) {

    var name: String
        internal set
    var phone: String
        internal set

    init {
        this.phone = phone
        this.name = name
    }
}