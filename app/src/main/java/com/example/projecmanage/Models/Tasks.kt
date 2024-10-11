package com.example.projecmanage.Models

import android.os.Parcel
import android.os.Parcelable

data class Tasks(
    val title: String = "",
    val createdBy : String = "",
    var cards : ArrayList<Card> = ArrayList()
    ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Card.CREATOR)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)= with(parcel) {
        parcel.writeString(title)
        parcel.writeString(createdBy)
        parcel.writeTypedList(cards)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tasks> {
        override fun createFromParcel(parcel: Parcel): Tasks {
            return Tasks(parcel)
        }

        override fun newArray(size: Int): Array<Tasks?> {
            return arrayOfNulls(size)
        }
    }
}
