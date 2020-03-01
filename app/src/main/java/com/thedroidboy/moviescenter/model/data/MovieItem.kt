package com.thedroidboy.moviescenter.model.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "movies")
data class MovieItem(
    @PrimaryKey val id: Int,
    @ColumnInfo val title: String?,
    @ColumnInfo val popularity: Float,
    @ColumnInfo val release_date: String?,
    @ColumnInfo val overview: String?,
    @ColumnInfo val vote_count: Int,
    @ColumnInfo val vote_average: Float,
    @ColumnInfo val poster_path: String?,
    @ColumnInfo val backdrop_path: String?
) : Parcelable