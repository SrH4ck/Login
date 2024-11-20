package com.ams.mylogin.model

data class User(
    val id: String?,
    val userId: String?,
    val displayName: String?,
    val avatarUrl: String?,
    val quote: String?,
    val profession: String?,
    // Otros campos necesarios
){
    fun toMap(): MutableMap<String, String?> {
        return mutableMapOf(
            "id" to this.id,
            "userId" to this.userId,
            "display_name" to this.displayName,
            "profession" to this.profession,
            "quote" to this.quote,
            "avatar" to this.avatarUrl
        )
    }
}
