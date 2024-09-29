package com.example.galeriafotos.data.repository

import com.example.galeriafotos.data.model.PhotoItem

class PhotoRepository {
    private val photos = mutableListOf<PhotoItem>()

    fun addPhoto(photo: PhotoItem) {
        photos.add(photo)
    }

    fun getAllPhotos(): List<PhotoItem> = photos
}