package com.renz.planner.data.notes

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class Notes(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    var createAt: String,
    var characters: Int,
    var isFavorite: Boolean
) : Parcelable