package com.kinglloy.multiplatform.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chat(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
)
