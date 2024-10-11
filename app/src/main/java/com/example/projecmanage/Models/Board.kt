package com.example.projecmanage.Models

import android.os.Parcel
import android.os.Parcelable

data class Board(
    val name: String ="",
    val image: String ="",
    val createdBy: String = "",
    var documentId:String ="",
    val assignedTo: ArrayList<String> = ArrayList(),
    var taskList: java.util.ArrayList<Tasks> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.createTypedArrayList(Tasks.CREATOR)!!
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        parcel.writeString(documentId)
        parcel.writeStringList(assignedTo)
        parcel.writeTypedList(taskList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }
}
