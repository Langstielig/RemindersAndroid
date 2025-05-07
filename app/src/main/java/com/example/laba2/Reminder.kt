package com.example.laba2

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Reminder(
    var title: String,
    var description: String,
    var date: Date?,
    var time: Date?,
    val id: Int,
    var notified: Boolean = false
) :Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Date(parcel.readLong()),
        Date(parcel.readLong()),
        parcel.readInt(),
        //boolean
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(description)
        dest.writeLong(date?.time ?: -1L)
        dest.writeLong(time?.time ?: -1L)
        dest.writeInt(id)
        dest.writeByte(if (notified) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR: Parcelable.Creator<Reminder>
    {
        override fun createFromParcel(source: Parcel): Reminder {
            return Reminder(source)
        }

        override fun newArray(size: Int): Array<Reminder?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "id: $id, title: $title, description: $description, date: $date, time: $time, notified: $notified"
    }
}