package com.evans.insta.models

class User {

    private var userName: String = ""
    private var uid: String = ""
    private var image: String = ""
    private var fullName: String = ""
    private var email: String = ""
    private var bio: String = ""

    constructor()

    constructor(userName: String, uid: String, image: String, fullName: String, email: String, bio: String){
        this.userName = userName
        this.uid = uid
        this.image = image
        this.fullName = fullName
        this.email = email
        this.bio = bio
    }

    fun getUserName(): String{
        return userName
    }

    fun setUserName(userName: String){
        this.userName = userName
    }

    fun getUid(): String{
        return uid
    }

    fun setUid(uid: String){
        this.uid = uid
    }

    fun getImage(): String{
        return image
    }

    fun setImage(image: String){
        this.image = image
    }

    fun getFullName(): String{
        return fullName
    }

    fun setFullName(fullName: String){
        this.fullName = fullName
    }

    fun  getEmail(): String{
        return email
    }

    fun setEmail(email: String){
        this.email = email
    }

    fun getBio(): String{
        return bio
    }

    fun setBio(bio: String){
        this.bio = bio
    }
}