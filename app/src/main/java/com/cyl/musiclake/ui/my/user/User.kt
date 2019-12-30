package com.cyl.musiclake.ui.my.user

import java.io.Serializable

class User : Serializable {
    var id = ""
    var name = ""
    var sex = ""
    var college = ""
    var major = ""
    var classes = ""
    var type = ""

    var avatar = ""
    var email = ""
    var phone = ""
    var nick = ""
    var token: String? = null
    var secret = 0

    constructor() {}

    constructor(id: String, name: String, sex: String, college: String, major: String, classes: String, img: String, email: String, phone: String, nickname: String, secret: Int) {
        this.id = id
        this.name = name
        this.sex = sex
        this.college = college
        this.major = major
        this.classes = classes
        this.avatar = img
        this.email = email
        this.phone = phone
        this.nick = nickname
        this.secret = secret
    }

    override fun toString(): String {
        return "User{" +
                "nickname='" + nick + '\''.toString() +
                ", id='" + id + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", sex='" + sex + '\''.toString() +
                ", college='" + college + '\''.toString() +
                ", major='" + major + '\''.toString() +
                ", class='" + classes + '\''.toString() +
                ", img='" + avatar + '\''.toString() +
                ", email='" + email + '\''.toString() +
                ", phone='" + phone + '\''.toString() +
                ", password='" + token + '\''.toString() +
                ", secret=" + secret +
                ", token=" + token +
                '}'.toString()
    }
}
